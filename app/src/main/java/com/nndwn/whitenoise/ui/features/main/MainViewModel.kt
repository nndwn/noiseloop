package com.nndwn.whitenoise.ui.features.main

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.snapshots.toInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ads.BillingManager
import com.nndwn.whitenoise.data.repository.PreferenceRepository
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.LabelAudio
import com.nndwn.whitenoise.data.extensions.asMediaItem
import com.nndwn.whitenoise.data.repository.AudioRepository
import com.nndwn.whitenoise.service.AudioPlaybackManager
import com.nndwn.whitenoise.service.TimerTime
import com.nndwn.whitenoise.ui.theme.CharcoalDarkGray
import com.nndwn.whitenoise.ui.theme.MediumDarkGray
import com.nndwn.whitenoise.ui.theme.toArgbLong
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val repository: AudioRepository,
    private val playbackManager: AudioPlaybackManager,
    private val preferenceRepository: PreferenceRepository,
    private val billingManager: BillingManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(){
    val isPlaying = playbackManager.isPlaying
    val sessionTrackDuration = playbackManager.sessionTrackDuration

    private val _activeAudioId = MutableStateFlow<String?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeAudio: StateFlow<DataAudio?> = playbackManager.currentMediaItem
        .map { mediaItem -> mediaItem?.mediaId }
        .distinctUntilChanged()
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                repository.getAudioFlowById(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _listAudio = MutableStateFlow<List<DataAudio>?>(null)
    private val _currentFilter = MutableStateFlow<AudioFilter>(AudioFilter.All)

    val musicListState: StateFlow<MainUiState> = combine(
        _listAudio,
        _currentFilter
    ) { list, filter ->
        if (list == null) {
            MainUiState.Loading
        } else {
            val filteredList = when (filter) {
                AudioFilter.All -> list
                AudioFilter.Favorite -> list.filter { it.isFavorite }
                is AudioFilter.SelectedType -> list.filter { it.type == filter.type }
            }
            MainUiState.Success(filteredList)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState.Loading
    )

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    init {
        loadLastPlayedAudio()
        observeBillingEvents()
        observePlaybackErrors()
        observeActiveAudioForColors()
    }

    private fun observeActiveAudioForColors() {
        viewModelScope.launch {
            activeAudio.collectLatest { audio ->
                if (audio != null && !audio.isColor) {
                    updateDominantColor(audio)
                }
            }
        }
    }

    private fun updateDominantColor(audio: DataAudio) {
        viewModelScope.launch(ioDispatcher) {
            val options = BitmapFactory.Options().apply { inSampleSize = 4 }
            val bitmap = BitmapFactory.decodeResource(context.resources, audio.cover, options)

            if (bitmap != null) {
                val palette = Palette.from(bitmap).generate()
                val color1 = palette.vibrantSwatch?.rgb ?: palette.dominantSwatch?.rgb ?: audio.colorPrimary.toInt()
                val color2 = palette.darkVibrantSwatch?.rgb ?: palette.mutedSwatch?.rgb ?: audio.colorSecondary.toInt()

                repository.updateAudioColors(
                    audio.id,
                    color1.toLong() and 0xFFFFFFFFL,
                    color2.toLong() and 0xFFFFFFFFL
                )
            }
        }
    }


    private fun observePlaybackErrors() {
        viewModelScope.launch {
            playbackManager.playbackError.collectLatest { error ->
                if (error) {
                    _uiEvent.emit(UiEvent.ShowErrorNotice(R.string.msg_error_generic))
                }
            }
        }
    }

    private fun observeBillingEvents() {
        viewModelScope.launch {
            billingManager.billingEvent.collectLatest { messageRes ->
                _uiEvent.emit(UiEvent.ShowAdsNotice(messageRes))
            }
        }
    }

    fun buyRemoveAds(activity: Activity) {
        billingManager.launchBillingFlow(activity)
    }

    fun changeFilter(newFilter: AudioFilter) {
        _currentFilter.value = newFilter
    }

    fun setFocusTimer(timerTime: TimerTime) {
        playbackManager.setFocusTimer(timerTime)
    }
    fun toggleFavorite(audio: DataAudio, isFavorite: Boolean) {
        viewModelScope.launch {

            repository.updateFavorite(audio.id, isFavorite)

            val message = if (isFavorite) R.string.msg_favorite_added else R.string.msg_favorite_removed
            _uiEvent.emit(UiEvent.ShowFavoriteNotice(message))
        }
    }

    fun togglePlayPause() {
        playbackManager.togglePlayPause()
    }

    fun playAudio(audio: DataAudio) {
        viewModelScope.launch {
            if (audio.label == LabelAudio.ONLINE) {
                var isRealOnline = false
                context.checkNetworkSmartly { online ->
                    isRealOnline = online
                }

                if (!isRealOnline) {
                    _uiEvent.emit(UiEvent.ShowDownloadRequiredWarning)
                    return@launch
                }
            }

            if (_activeAudioId.value == audio.id) {
                togglePlayPause()
                return@launch
            }

            _activeAudioId.value = audio.id
            preferenceRepository.saveLastAudioId(audio.id)

            playbackManager.play(audio.asMediaItem(context))

            if (audio.label == LabelAudio.ONLINE) {
                repository.downloadAndSaveAudio(context, audio)
            }
        }
    }

    fun recordAdShown() {
        viewModelScope.launch {
            preferenceRepository.recordAdShown()
        }
    }

    private fun loadLastPlayedAudio() {
        viewModelScope.launch {
            val lastAudioId = preferenceRepository.lastAudioId.first()
            if (!lastAudioId.isNullOrEmpty()) {
                _activeAudioId.value = lastAudioId
            }
        }
    }

}

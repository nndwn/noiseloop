package com.nndwn.whitenoise.ui.features.main

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.snapshots.toInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.nndwn.whitenoise.IoDispatcher
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ads.BillingManager
import com.nndwn.whitenoise.data.repository.PreferenceRepository
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.LabelAudio
import com.nndwn.whitenoise.data.extensions.asMediaItem
import com.nndwn.whitenoise.data.local.InitialAudioData
import com.nndwn.whitenoise.data.repository.AudioRepository
import com.nndwn.whitenoise.service.AudioPlaybackManager
import com.nndwn.whitenoise.service.TimerTime
import com.nndwn.whitenoise.ui.UiController
import com.nndwn.whitenoise.ui.UiEffect
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
    private val controller: UiController,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(){
    val isPlaying = playbackManager.isPlaying
    val sessionTrackDuration = playbackManager.sessionTrackDuration
    val activeAudio = playbackManager.activeAudio

    private val _currentFilter = MutableStateFlow<AudioFilter>(AudioFilter.All)

    val musicListState: StateFlow<MainUiState> = combine(
        repository.getAllAudio(),
        _currentFilter
    ) { list, filter ->
        val filteredList = when (filter) {
            AudioFilter.All -> list
            AudioFilter.Favorite -> list.filter { it.isFavorite }
            is AudioFilter.SelectedType -> list.filter { it.type == filter.type }
        }
        MainUiState.Success(filteredList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState.Loading
    )


    init {
        loadLastPlayedAudio()

    }



    fun buyRemoveAds(activity: Activity) {
        billingManager.launchBillingFlow(activity)
    }

    fun changeFilter(newFilter: AudioFilter) {
        _currentFilter.value = newFilter
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

    private fun playbackError(){
        viewModelScope.launch {
            playbackManager.playbackError.collectLatest { error ->
                if (error) {
                    controller.sendEffect(UiEffect.ShowToast(R.string.msg_error_generic))
                }
            }
        }
    }

}

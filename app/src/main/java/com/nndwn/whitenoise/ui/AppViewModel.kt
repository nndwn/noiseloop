package com.nndwn.whitenoise.ui

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.nndwn.whitenoise.IoDispatcher
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ads.AdHelper
import com.nndwn.whitenoise.ads.BillingHelper
import com.nndwn.whitenoise.data.extensions.asMediaItem
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.LabelAudio
import com.nndwn.whitenoise.data.repository.AudioRepository
import com.nndwn.whitenoise.data.repository.PreferenceRepository
import com.nndwn.whitenoise.service.AudioPlaybackManager
import com.nndwn.whitenoise.service.FocusTimerManager
import com.nndwn.whitenoise.service.TimerTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class AppViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val databaseRepository: AudioRepository,
    private val preferenceRepository: PreferenceRepository,
    private val controller: UiController,
    private val focusTimerManager : FocusTimerManager,
    private val playback : AudioPlaybackManager,
    private val billingHelper: BillingHelper,
    private val adHelper: AdHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    val isActiveAudio = playback.activeAudio
    val sessionTrackDuration = playback.sessionTrackDuration
    val isPlaying = playback.isPlaying
    val uiEffect = controller.uiEffect
    private val _isLoadingAd = MutableStateFlow(false)
    val isLoadingAd = _isLoadingAd.asStateFlow()

    val shouldShowAd: StateFlow<Boolean> = preferenceRepository.shouldShowAd
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val removeAdsPrice: StateFlow<String?> = billingHelper.removeAdsPrice

    val isPremium: StateFlow<Boolean> = preferenceRepository.isPremium
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        viewModelScope.launch {
            preferenceRepository.recordAdShownIfFirstTime()
        }

        viewModelScope.launch {
            billingHelper.purchaseSuccessEvent.collectLatest {
                controller.sendEffect(UiEffect.ShowToast(R.string.msg_premium_activated))
            }
        }

        viewModelScope.launch {
            isActiveAudio.collectLatest { audio ->
                if (audio != null && !audio.isColor) {
                    generateAndSaveColors(audio)
                }
            }
        }

        viewModelScope.launch {
            playback.playbackError.collectLatest { error ->
                if (error) {
                    controller.sendEffect(UiEffect.ShowToast(R.string.msg_error_generic))
                }
            }
        }
    }

    fun onPlayClick(audio: DataAudio) {
        val idAudio = isActiveAudio.value?.id
        if ( idAudio == audio.id) {
            if (isPlaying.value) {
                playback.pause()
            } else {
                playback.resume()
            }
            return
        }

        viewModelScope.launch {
            playback.play(audio.asMediaItem(context))
            val isSuccess = withTimeoutOrNull(7.seconds) {
                playback.isPlaying.first { it }
            }

            if (isSuccess == null){
                controller.sendEffect(UiEffect.ShowToast(R.string.msg_download_required))
                return@launch
            }

            preferenceRepository.saveLastAudioId(audio.id)
            if (audio.label == LabelAudio.ONLINE) {
                databaseRepository.downloadAndSaveAudio(context, audio)
            }
        }
    }


    fun onRemoveAdsClicked(activity: Activity) {
        billingHelper.launchBillingFlow(activity)
    }

    fun setFocusTimer (timer : TimerTime) {
        focusTimerManager.setFocusTimer(timer)
    }

    fun recordAdShown() {
        viewModelScope.launch {
            preferenceRepository.recordAdShown()
        }
    }


    fun performAdFlow(activity: Activity, onFinished: () -> Unit) {
        viewModelScope.launch {
            _isLoadingAd.value = true
            val isLoaded = withTimeoutOrNull(7.seconds) {
                adHelper.loadAdAwait(activity.applicationContext)
            } ?: false
            _isLoadingAd.value = false

            if (isLoaded) {
                adHelper.showAd(activity) {
                    recordAdShown()
                    onFinished()
                }
            } else {
                recordAdShown()
                onFinished()
            }
        }
    }

    private fun generateAndSaveColors(audio: DataAudio) {
        viewModelScope.launch(ioDispatcher) {
            val options = BitmapFactory.Options().apply { inSampleSize = 4 }
            val bitmap = try {
                BitmapFactory.decodeResource(context.resources, audio.cover, options)
            } catch (_: Exception) {
                null
            }

            bitmap?.let {
                val palette = Palette.from(it).generate()
                val color1 = palette.vibrantSwatch?.rgb ?: palette.dominantSwatch?.rgb ?: audio.colorPrimary.toInt()
                val color2 = palette.darkVibrantSwatch?.rgb ?: palette.mutedSwatch?.rgb ?: audio.colorSecondary.toInt()

                databaseRepository.updateAudioColors(
                    audio.id,
                    color1.toLong() and 0xFFFFFFFFL,
                    color2.toLong() and 0xFFFFFFFFL
                )
            }
        }
    }
}

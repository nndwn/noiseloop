package com.nndwn.whitenoise.ui

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.nndwn.whitenoise.IoDispatcher
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ads.BillingHelper
import com.nndwn.whitenoise.data.local.entity.DataAudio
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val repository: AudioRepository,
    private val preferenceRepository: PreferenceRepository,
    private val controller: UiController,
    private val focusTimerManager : FocusTimerManager,
    private val playback : AudioPlaybackManager,
    private val billingHelper: BillingHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    val isActiveAudio = playback.activeAudio
    val sessionTrackDuration = playback.sessionTrackDuration
    val isPlaying = playback.isPlaying
    fun setFocusTimer (timer : TimerTime) {
        focusTimerManager.setFocusTimer(timer)
    }
    val uiEffect = controller.uiEffect
    private val _isLoadingAd = MutableStateFlow(false)
    val isLoadingAd = _isLoadingAd.asStateFlow()

    val isAdsEnabled: StateFlow<Boolean> = preferenceRepository.shouldShowAd
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

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

                repository.updateAudioColors(
                    audio.id,
                    color1.toLong() and 0xFFFFFFFFL,
                    color2.toLong() and 0xFFFFFFFFL
                )
            }
        }
    }
}

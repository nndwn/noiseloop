package com.nndwn.whitenoise.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ads.BillingHelper
import com.nndwn.whitenoise.data.local.InitialAudioData
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.repository.AudioRepository
import com.nndwn.whitenoise.data.repository.PreferenceRepository
import com.nndwn.whitenoise.service.AudioPlaybackManager
import com.nndwn.whitenoise.service.FocusTimerManager
import com.nndwn.whitenoise.service.TimerTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: AudioRepository,
    private val preferenceRepository: PreferenceRepository,
    private val controller: UiController,
    private val focusTimerManager : FocusTimerManager,
    private val playback : AudioPlaybackManager,
    private val billingHelper: BillingHelper
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
    }

}
package com.nndwn.whitenoise.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.whitenoise.data.local.InitialAudioData
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.repository.AudioRepository
import com.nndwn.whitenoise.data.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
): ViewModel() {
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
}
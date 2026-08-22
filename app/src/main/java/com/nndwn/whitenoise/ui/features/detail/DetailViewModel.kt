package com.nndwn.whitenoise.ui.features.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.whitenoise.data.extensions.asMediaItem
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.repository.AudioRepository
import com.nndwn.whitenoise.data.repository.PreferenceRepository
import com.nndwn.whitenoise.service.AudioPlaybackManager
import com.nndwn.whitenoise.ui.UiController
import com.nndwn.whitenoise.ui.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val playbackManager: AudioPlaybackManager,
    private val audioRepository: AudioRepository,
    private val preferenceRepository: PreferenceRepository,
    private val uiController: UiController
) : ViewModel() {

    val activeAudio: StateFlow<DataAudio?> = playbackManager.activeAudio
    val isPlaying: StateFlow<Boolean> = playbackManager.isPlaying
    val timePlaying: StateFlow<Long> = playbackManager.sessionTrackDuration

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    audioRepository.updateFavorite(event.audio.id, !event.audio.isFavorite)
                }
            }
            is DetailEvent.TogglePlay -> {
                handlePlayLogic(event.audio)
            }
            DetailEvent.NavigateBack -> {
                uiController.sendEffect(UiEffect.NavigateBack)
            }
        }
    }

    private fun handlePlayLogic(audio: DataAudio) {
        val current = activeAudio.value
        if (current?.id == audio.id) {
            if (isPlaying.value) playbackManager.pause() else playbackManager.resume()
            return
        }

        viewModelScope.launch {
            playbackManager.play(audio.asMediaItem(context))
            preferenceRepository.saveLastAudioId(audio.id)
        }
    }
}

sealed interface DetailEvent {
    data class ToggleFavorite(val audio: DataAudio) : DetailEvent
    data class TogglePlay(val audio: DataAudio) : DetailEvent
    data object NavigateBack : DetailEvent
}

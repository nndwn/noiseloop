package com.nndwn.whitenoise.ui.features.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.repository.AudioRepository
import com.nndwn.whitenoise.service.AudioPlaybackManager
import com.nndwn.whitenoise.ui.UiController
import com.nndwn.whitenoise.ui.UiEffect
import com.nndwn.whitenoise.ui.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val repository: AudioRepository,
    private val playbackManager: AudioPlaybackManager,
    private val controller: UiController,
) : ViewModel(){

    val sessionTrackDuration = playbackManager.sessionTrackDuration

    private val _currentFilter = MutableStateFlow<AudioFilter>(AudioFilter.All)
    val currentFilter = _currentFilter.asStateFlow()

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

    fun onEvent(event : MainUiEvent){
        when(event) {
            is MainUiEvent.NavigateToDetail -> controller.sendEffect(UiEffect.NavigateTo(AppRoute.SoundDetail))
            is MainUiEvent.NavigateBack -> controller.sendEffect(UiEffect.NavigateBack)
            is MainUiEvent.Toast -> controller.sendEffect(UiEffect.ShowToast(event.message))
        }
    }


    fun changeFilter(newFilter: AudioFilter) {
        _currentFilter.value = newFilter
    }

    fun toggleFavorite(audio: DataAudio, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(audio.id, isFavorite)
            val message = if (isFavorite) R.string.msg_favorite_added else R.string.msg_favorite_removed
            onEvent(MainUiEvent.Toast(message))
        }
    }
}

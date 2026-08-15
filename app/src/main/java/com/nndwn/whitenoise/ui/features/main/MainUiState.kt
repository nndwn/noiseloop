package com.nndwn.whitenoise.ui.features.main

import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.TypeAudio
import kotlinx.serialization.Serializable

sealed interface UiEvent {

}

@Serializable
sealed interface AudioFilter {
    @Serializable
    data object All : AudioFilter
    @Serializable
    data object Favorite : AudioFilter
    @Serializable
    data class SelectedType(val type: TypeAudio) : AudioFilter
}

sealed interface  MainUiState {
    data object Loading : MainUiState
    data class Success(val list : List<DataAudio>) : MainUiState
}

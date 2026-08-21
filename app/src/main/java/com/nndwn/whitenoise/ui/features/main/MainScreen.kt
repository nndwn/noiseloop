package com.nndwn.whitenoise.ui.features.main


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nndwn.whitenoise.data.local.InitialAudioData
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.ui.LocalActiveAudio
import com.nndwn.whitenoise.ui.LocalIsPlaying
import com.nndwn.whitenoise.ui.LocalPlayHandle
import com.nndwn.whitenoise.ui.LocalSizeWidth
import com.nndwn.whitenoise.ui.LocalToggleSidebar
import com.nndwn.whitenoise.ui.components.ThreeDotsHorizontal
import com.nndwn.whitenoise.ui.extentions.shimmer
import com.nndwn.whitenoise.ui.features.main.components.ItemSound
import com.nndwn.whitenoise.ui.features.main.components.ListFilterBar
import com.nndwn.whitenoise.ui.features.main.components.LogoText
import com.nndwn.whitenoise.ui.features.main.components.MainSidebar
import com.nndwn.whitenoise.ui.features.main.components.SwipeActionFavorite
import com.nndwn.whitenoise.ui.theme.dimens
import com.nndwn.whitenoise.ui.theme.toComposeColor

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
){
    val listState by viewModel.musicListState.collectAsStateWithLifecycle()
    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val timePlaying by viewModel.sessionTrackDuration.collectAsStateWithLifecycle()
    
    val playHandle = LocalPlayHandle.current

    MainScreenContent(
        listState = listState,
        currentFilter = currentFilter,
        timePlaying = timePlaying,
        onFilterChanged = { viewModel.changeFilter(it) },
        onAudioClick = playHandle,
        onFavorite = { audio ->
            viewModel.toggleFavorite(audio, !audio.isFavorite)
        },
        onEvent = viewModel::onEvent
    )
}



@Composable
fun MainScreenContent(
    listState: MainUiState,
    timePlaying : Long,
    currentFilter: AudioFilter,
    onEvent : (MainUiEvent) -> Unit,
    onFilterChanged: (AudioFilter) -> Unit,
    onAudioClick: (DataAudio) -> Unit,
    onFavorite : (DataAudio) -> Unit,
){
    val windowSizeWidth = LocalSizeWidth.current
    val isPlaying = LocalIsPlaying.current
    val audioActive = LocalActiveAudio.current

    val transition = rememberInfiniteTransition(label = "MainShimmerTransition")
    val shimmerProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MainShimmerProgress"
    )

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        if (windowSizeWidth != WindowWidthSizeClass.Compact){
            MainSidebar(
                timePlaying = timePlaying,
                onClickFavorite = { audioActive?.let { onFavorite(it) } },
                navigate ={onEvent(MainUiEvent.NavigateToDetail)}
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small)
        ) {
            tagsAudio(
                windowSizeWidth = windowSizeWidth,
                shimmerProgress = shimmerProgress,
                stateUiState = listState,
                selectedFilter = currentFilter,
                onFilterSelected = onFilterChanged,
                colorAudioActive = audioActive?.colorSecondary?.toComposeColor()
            )

            when(listState) {
                is MainUiState.Loading -> loadingContent(shimmerProgress)

                is MainUiState.Success -> {
                    items(items = listState.list, key = {it.id}){ audio ->
                        SwipeActionFavorite(
                            isFavorite = audio.isFavorite,
                            onToggleFavorite = { onFavorite(audio) },
                            colorSelected = audioActive?.colorSecondary?.toComposeColor() ?: MaterialTheme.colorScheme.surface
                        ) { _ ->
                            ItemSound(
                                active = audio.id == audioActive?.id && isPlaying,
                                item = audio
                            ) {
                                onAudioClick(audio)
                            }
                        }
                    }
                }
            }
        }
    }
}


private fun LazyListScope.tagsAudio(
    windowSizeWidth : WindowWidthSizeClass,
    shimmerProgress : Float,
    stateUiState: MainUiState,
    selectedFilter: AudioFilter,
    onFilterSelected: (AudioFilter) -> Unit,
    colorAudioActive : Color?,
) {
    if (windowSizeWidth == WindowWidthSizeClass.Compact) {
        item {
            val localEndSidebar = LocalToggleSidebar.current
            LogoText(
                modifier = Modifier.padding(vertical = MaterialTheme.dimens.small),
                content = {
                    ThreeDotsHorizontal(onClick = localEndSidebar)
                }
            )
        }
        item {
            ListFilterBar(
                shimmerProgress = shimmerProgress,
                stateUI = stateUiState,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                colorAudioActive = colorAudioActive ?: MaterialTheme.colorScheme.surface
            )
        }
    } else {
        stickyHeader {
            ListFilterBar(
                shimmerProgress = shimmerProgress,
                stateUI = stateUiState,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                colorAudioActive = colorAudioActive ?: MaterialTheme.colorScheme.surface
            )
        }
    }
}


private fun LazyListScope.loadingContent(
    shimmerProgress : Float
){

    items(items = InitialAudioData.audioList, key = { "shimmer_${it.id}" }){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shimmer(
                    progress = shimmerProgress,
                    shape = MaterialTheme.shapes.small
                )
                .padding(
                    vertical = MaterialTheme.dimens.small,
                    horizontal = MaterialTheme.dimens.medium)

            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)
        ){
            Box(
                modifier = Modifier
                    .size(MaterialTheme.dimens.iconExtraLarge)
            )
        }
    }
}

package com.nndwn.whitenoise.ui.features.main


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.ui.components.MenuOptions
import com.nndwn.whitenoise.ui.features.main.components.AudioFilterBarShimmer
import com.nndwn.whitenoise.ui.features.main.components.LogoText
import com.nndwn.whitenoise.ui.features.main.components.ListAudio
import com.nndwn.whitenoise.ui.features.main.components.ListFilterBar
import com.nndwn.whitenoise.ui.theme.Palette
import com.nndwn.whitenoise.ui.LocalMenuOptionHandler
import com.nndwn.whitenoise.ui.LocalSizeHeight
import com.nndwn.whitenoise.ui.LocalSizeWidth
import com.nndwn.whitenoise.ui.features.main.components.MainSidebar


@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
){

}



@Composable
fun MainScreenContent(
    list: MainUiState,
    isPlaying: Boolean,
    activeAudio: DataAudio?,
    modifier: Modifier = Modifier,
    timePlaying : Long,
    currentFilter: AudioFilter,
    onFilterChanged: (AudioFilter) -> Unit,
    onAudioClick: (DataAudio) -> Unit,
    onSelectedFavorite : () -> Unit,
    onSetFavorite : (DataAudio, Boolean) -> Unit
){
    val windowSizeWidth = LocalSizeWidth.current

    val onMenuSelected = LocalMenuOptionHandler.current

    Row(
        modifier = modifier.fillMaxSize()
    ) {
        if (windowSizeWidth != WindowWidthSizeClass.Compact){
            MainSidebar(
                activeAudio = activeAudio,
                timePlaying = timePlaying,

            )
        }

        Scaffold(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            containerColor = Color.Transparent,
            topBar = {
                if (isLoading && isTablet ){
                    AudioFilterBarShimmer()
                }
                else {
                    ListFilterBar(
                        selectedFilter = currentFilter,
                        colorSelected = dominantColor,
                        onFilterSelected = onFilterChanged,
                        modifier = Modifier
                            .background(Palette.Black2)
                            .padding(top = 13.dp)
                            .statusBarsPadding()
                    )
                }
                AnimatedVisibility(
                    visible = !isTablet,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ){
                   LogoText(
                       withSidebar = true,
                       onMenuClick = onMenuClick)
                }
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
            ) {
                ListAudio(
                    list = list,
                    activeAudioId = activeAudio?.id,
                    isPlaying = isPlaying,
                    isLoading = isLoading,
                    swipeColor = dominantColor,
                    onAudioClick = onAudioClick,
                    onSetFavorite = onSetFavorite,
                    spaceBottom =  spaceBottom - innerPadding.calculateBottomPadding(),
                    isTablet = isTablet,
                    selectedFilter = currentFilter,
                    colorSelected = dominantColor,
                    onFilterSelected = onFilterChanged
                )
            }
        }
    }


}


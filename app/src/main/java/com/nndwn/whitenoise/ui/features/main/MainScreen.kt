package com.nndwn.whitenoise.ui.features.main


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.ui.components.DetailAudioInfo
import com.nndwn.whitenoise.ui.components.FavoriteButton
import com.nndwn.whitenoise.ui.components.MenuOptions
import com.nndwn.whitenoise.ui.components.TimerButton
import com.nndwn.whitenoise.ui.components.TogglePlay
import com.nndwn.whitenoise.ui.features.main.components.AudioFilterBarShimmer
import com.nndwn.whitenoise.ui.features.main.components.Header
import com.nndwn.whitenoise.ui.features.main.components.ListAudio
import com.nndwn.whitenoise.ui.features.main.components.ListFilterBar
import com.nndwn.whitenoise.ui.features.main.components.MiniPlayBottom
import com.nndwn.whitenoise.ui.theme.Dimens
import com.nndwn.whitenoise.ui.theme.Palette
import com.nndwn.whitenoise.ui.utils.LocalIsPremium
import com.nndwn.whitenoise.ui.utils.LocalIsTablet

@Composable
fun MainScreen(
    list: List<DataAudio>,
    isPlaying: Boolean,
    isLoading: Boolean,
    activeAudio: DataAudio?,
    spaceBottom: Dp,
    dominantColor: Color,
    onMenuClick: () -> Unit,
    currentFilter: AudioFilter,
    modifier: Modifier = Modifier,
    timePlaying : Long,
    onFilterChanged: (AudioFilter) -> Unit,
    onAudioClick: (DataAudio) -> Unit,
    menuOptions : (MenuOptions) -> Unit,
    onTogglePlay : () -> Unit,
    onClickTimer : () -> Unit,
    onSelectedFavorite : () -> Unit,
    onSetFavorite : (DataAudio, Boolean) -> Unit,
    onRouteDetail : ()-> Unit
){

    val isTablet = LocalIsTablet.current

    val animatedBackgroundColor by animateColorAsState(
        targetValue = dominantColor.last(),
        animationSpec = tween(durationMillis = 500),
        label = "GlobalColorTransition"
    )

    Row(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = isTablet,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }
            ) + fadeIn(),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }
            ) + fadeOut(),
        ) {
            Scaffold(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .background( dominantColor),
                containerColor = Color.Transparent,
                topBar = {
                    Header(fontSize = 22.sp)
                },
                bottomBar = {
                    AnimatedVisibility(
                        visible = activeAudio != null,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    ){
                        activeAudio?.let { item ->
                            Column (modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.PaddingHorizontal)
                                .navigationBarsPadding(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(Modifier.height(10.dp))
                                Column(
                                    modifier = Modifier
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() },
                                        ){
                                            onRouteDetail()
                                        },
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
                                        .background(color = Palette.Black3),
                                        contentAlignment = Alignment.Center
                                    ){
                                        Image(
                                            painter = painterResource(item.cover),
                                            contentDescription = "cover ${item.name}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    DetailAudioInfo(
                                        title = item.name ,
                                        type = item.type.name,
                                        playing = isPlaying,
                                        timePlaying = timePlaying,
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FavoriteButton(
                                        favorite = item.isFavorite,
                                        onClick = onSelectedFavorite
                                    )
                                    TogglePlay(
                                        sizeIcon = 60.dp,
                                        paddingIcon = 10.dp,
                                        isPlaying = isPlaying,
                                        onTogglePlay = onTogglePlay
                                    )

                                    TimerButton(onClickTimer = onClickTimer)
                                }
                                Spacer(Modifier.height(10.dp))

                            }
                        }
                    }
                }
            ) { innerPadding ->
                MenuOptions(
                    modifier = Modifier
                        .padding(top = innerPadding.calculateTopPadding())
                        .padding(vertical = 10.dp)
                ) { menu ->
                    menuOptions(menu)
                }
            }

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
                   Header(
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


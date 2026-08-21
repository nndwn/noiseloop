package com.nndwn.whitenoise.ui.features.detail


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.ui.components.TogglePlay
import com.nndwn.whitenoise.ui.components.DetailAudioInfo
import com.nndwn.whitenoise.ui.components.FavoriteButton
import com.nndwn.whitenoise.ui.components.ThreeDotsHorizontal
import com.nndwn.whitenoise.ui.components.TimerButton
import com.nndwn.whitenoise.ui.theme.Dimens
import com.nndwn.whitenoise.ui.theme.Palette

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.unit.sp
import com.nndwn.whitenoise.ui.utils.LocalIsTablet

@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    colors: List<Color>,
    timePlaying: Long,
    playing: Boolean,
    onFavoriteClick: () -> Unit,
    itemAudio: DataAudio? = null,
    onTogglePlay: () -> Unit,
    onTimerClick: () -> Unit,
    onClickSidebarRight: () -> Unit
) {
    val isTablet = LocalIsTablet.current

    BoxGradientAnimation(colors)
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Header(
                onClickBack = onBackClick,
                onClickSidebar = onClickSidebarRight
            )
        },
        bottomBar = {

        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = itemAudio != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            itemAudio?.let { item ->
                if (isTablet) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = Dimens.PaddingHorizontal * 2, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
                                .background(color = Palette.Black3),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(item.cover),
                                contentDescription = "cover ${item.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                modifier = Modifier
                                    .widthIn(max = 410.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DetailAudioInfo(
                                    playing = playing,
                                    timePlaying = timePlaying,
                                    title = item.name,
                                    type = item.type.name,
                                    textStyleTitle = 27.sp,
                                    textStyleType = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            MenuButtons(
                                favorite = item.isFavorite,
                                playing = playing,
                                onClickFavorite = onFavoriteClick,
                                onClickTimer = onTimerClick,
                                onClickPlay = onTogglePlay,
                                isTablet = true
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.PaddingHorizontal, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .aspectRatio(1f)
                                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
                                    .background(color = Palette.Black3),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(item.cover),
                                    contentDescription = "cover ${item.name}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.PaddingHorizontal)
                                .navigationBarsPadding(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            DetailAudioInfo(
                                title = item.name,
                                type = item.type.name,
                                playing = playing,
                                timePlaying = timePlaying
                            )
                            MenuButtons(
                                favorite = item.isFavorite,
                                playing = playing,
                                onClickFavorite = onFavoriteClick,
                                onClickTimer = onTimerClick,
                                onClickPlay = onTogglePlay
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuButtons(
    favorite: Boolean,
    playing: Boolean,
    onClickFavorite: () -> Unit,
    onClickTimer: () -> Unit,
    onClickPlay: () -> Unit,
    isTablet: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isTablet) Arrangement.Start else Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isTablet) {

            FavoriteButton(
                favorite = favorite,
                onClick = onClickFavorite,
                size = 67.dp
            )
            Spacer(modifier = Modifier.size(24.dp))
            TogglePlay(
                sizeIcon = 72.dp,
                paddingIcon = 12.dp,
                isPlaying = playing,
                onTogglePlay = onClickPlay
            )
            Spacer(modifier = Modifier.size(24.dp))
            TimerButton(
                onClickTimer = onClickTimer,
                size = 67.dp
            )
        } else {
            FavoriteButton(
                favorite = favorite,
                onClick = onClickFavorite
            )
            TogglePlay(
                sizeIcon = 60.dp,
                paddingIcon = 10.dp,
                isPlaying = playing,
                onTogglePlay = onClickPlay
            )
            TimerButton(onClickTimer = onClickTimer)
        }
    }
}

@Composable
private fun BoxGradientAnimation(colors: List<Color>){
    val infiniteTransition = rememberInfiniteTransition(label = "radial_path")
    val totalDuration = 12000

    val percentX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = totalDuration
                0f at 0 using LinearEasing
                1f at (totalDuration / 4)
                0f at (totalDuration / 2)
                1f at (totalDuration * 3 / 4)
                0f at totalDuration
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "center_x"
    )
    val percentY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = totalDuration
                0f at 0 using LinearEasing
                1f at (totalDuration / 4)
                1f at (totalDuration / 2)
                0f at (totalDuration * 3 / 4)
                0f at totalDuration
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "center_y"
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val brush = Brush.radialGradient(
                    colors = colors,
                    center = Offset(x = size.width * percentX, y = size.height * percentY),
                    radius = size.width * 1.5f
                )
                drawRect(brush = brush)
            }
    )
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    onClickBack : () -> Unit = {},
    onClickSidebar :() -> Unit = {}
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingHorizontal, vertical = 10.dp)
            .background(Color.Transparent)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onClickBack
                )
        ){
            Icon(
                painter = painterResource(R.drawable.ic_down2),
                contentDescription = stringResource(R.string.btn_text_back),
                tint = Color.White,
                modifier = Modifier.size(35.dp)
            )
        }

        ThreeDotsHorizontal {
            onClickSidebar()
        }
    }

}


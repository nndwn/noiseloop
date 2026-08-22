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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nndwn.whitenoise.ui.components.TogglePlay
import com.nndwn.whitenoise.ui.components.DetailAudioInfo
import com.nndwn.whitenoise.ui.components.FavoriteButton
import com.nndwn.whitenoise.ui.components.TimerButton
import com.nndwn.whitenoise.ui.theme.Dimens
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.nndwn.whitenoise.ui.LocalActiveAudio
import com.nndwn.whitenoise.ui.LocalIsPlaying
import com.nndwn.whitenoise.ui.LocalItemTimerHandler
import com.nndwn.whitenoise.ui.LocalSizeWidth
import com.nndwn.whitenoise.ui.features.detail.components.BoxGradientAnimation
import com.nndwn.whitenoise.ui.features.detail.components.Header
import com.nndwn.whitenoise.ui.theme.CharcoalDarkGray
import com.nndwn.whitenoise.ui.theme.MediumDarkGray
import com.nndwn.whitenoise.ui.theme.dimens
import com.nndwn.whitenoise.ui.theme.toComposeColor


@Composable
fun DetailScreen(
    viewModel : DetailViewModel = hiltViewModel()
){

}


@Composable
fun DetailScreenContent(
    timePlaying : Long,
    onClickFavorite: () -> Unit,
    navigateBack : () -> Unit
) {
    val audioActive = LocalActiveAudio.current
    val windowSizeWidth = LocalSizeWidth.current
    val isPlaying  = LocalIsPlaying.current


    val listColor = remember { listOf(
        audioActive?.colorPrimary?.toComposeColor() ?: MediumDarkGray,
        audioActive?.colorSecondary?.toComposeColor() ?: CharcoalDarkGray
    ) }
    BoxGradientAnimation(listColor)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Header(
            onClickBack = navigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
        )
        audioActive?.let { audio ->
            if (windowSizeWidth != WindowWidthSizeClass.Compact){
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = MaterialTheme.dimens.extraLarge,
                            vertical = MaterialTheme.dimens.large),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.extraLarge)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .shadow(
                                elevation = MaterialTheme.dimens.elevationMedium,
                                shape = MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(audio.cover)  ,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.extraLarge),
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
                                playing = isPlaying,
                                timePlaying = timePlaying,
                                title =  audio.name,
                                type = audio.type.name,
                                textStyleTitle = MaterialTheme.typography.headlineMedium,
                                textStyleType = MaterialTheme.typography.bodyLarge
                            )
                        }

                        MenuButtons(
                            favorite = audio.isFavorite,
                            playing = isPlaying,
                            onClickFavorite = onClickFavorite,
                            onClickPlay = onTogglePlay,
                            expandWindow = true
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
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
    Scaffold(

    ) { paddingValues ->
        AnimatedVisibility(
            visible = itemAudio != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            itemAudio?.let { item ->
                if (isTablet) {

                } else {

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
    onClickPlay: () -> Unit,
    expandWindow: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (expandWindow) Arrangement.Start else Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val timerHandle = LocalItemTimerHandler.current
        if (expandWindow) {
            FavoriteButton(
                favorite = favorite,
                onClick = onClickFavorite,
                size = 67.dp
            )

            Spacer(modifier = Modifier.size(MaterialTheme.dimens.large))

            TogglePlay(
                sizeIcon = 72.dp,
                isPlaying = playing,
                onTogglePlay = onClickPlay
            )
            Spacer(modifier = Modifier.size(MaterialTheme.dimens.large))

            TimerButton(
                onClickTimer = timerHandle,
                size = 67.dp
            )
        } else {

            FavoriteButton(
                favorite = favorite,
                onClick = onClickFavorite,
                size = 58.dp
            )

            TogglePlay(
                sizeIcon = 60.dp,
                isPlaying = playing,
                onTogglePlay = onClickPlay
            )
            TimerButton(
                size = 58.dp,
                onClickTimer = timerHandle
            )
        }
    }
}




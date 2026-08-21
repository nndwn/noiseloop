package com.nndwn.whitenoise.ui.features.main.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.ui.LocalActiveAudio
import com.nndwn.whitenoise.ui.LocalIsPlaying
import com.nndwn.whitenoise.ui.components.DetailAudioInfo
import com.nndwn.whitenoise.ui.components.FavoriteButton
import com.nndwn.whitenoise.ui.components.MenuOptions
import com.nndwn.whitenoise.ui.components.TimerButton
import com.nndwn.whitenoise.ui.components.TogglePlay
import com.nndwn.whitenoise.ui.theme.dimens
import com.nndwn.whitenoise.ui.theme.toComposeColor
import com.nndwn.whitenoise.ui.LocalItemTimerHandler
import com.nndwn.whitenoise.ui.LocalMenuOptionHandler
import com.nndwn.whitenoise.ui.LocalPlayHandle
import com.nndwn.whitenoise.ui.LocalSizeHeight
import com.nndwn.whitenoise.ui.LocalSizeWidth

@Composable
fun MainSidebar(
    timePlaying: Long,
    navigate: () -> Unit,
    onClickFavorite: () -> Unit
) {
    val windowSizeHeight = LocalSizeHeight.current
    val playHandle = LocalPlayHandle.current
    val menus = LocalMenuOptionHandler.current
    val activeAudio = LocalActiveAudio.current

    Surface (
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp),
        color = activeAudio?.colorSecondary?.toComposeColor() ?: MaterialTheme.colorScheme.surface
    ){
        Column(modifier = Modifier.fillMaxSize()) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)

            ){
                LogoText(
                    modifier = Modifier
                        .padding(
                            vertical = MaterialTheme.dimens.medium,
                            horizontal = MaterialTheme.dimens.small
                        )
                )

                if (windowSizeHeight != WindowHeightSizeClass.Compact){
                    MenuOptions(onMenuSelected = menus)
                }
            }
            activeAudio?.let { audio ->
                MediaPlayerSidebar(
                    activeAudio = audio,
                    timePlaying = timePlaying,
                    navigate = navigate,
                    onClickFavorite = onClickFavorite,
                    onPlayHandle = playHandle
                )
            }
        }
    }

}

@Composable
private fun MediaPlayerSidebar(
    activeAudio: DataAudio,
    modifier: Modifier = Modifier,
    timePlaying : Long,
    navigate : () -> Unit,
    onClickFavorite : () -> Unit,
    onPlayHandle : (DataAudio) -> Unit
) {

    val onTimerHandle = LocalItemTimerHandler.current
    val isPlaying = LocalIsPlaying.current

    Column (modifier = modifier
        .fillMaxWidth()
        .padding(MaterialTheme.dimens.medium),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = navigate
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(
                    elevation = MaterialTheme.dimens.elevationSmall,
                    shape = MaterialTheme.shapes.small)
                .background(color = MaterialTheme.colorScheme.onSurface),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(activeAudio.cover),
                    contentDescription = "cover ${activeAudio.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            DetailAudioInfo(
                title = activeAudio.name ,
                type = activeAudio.type.name,
                playing = isPlaying,
                timePlaying = timePlaying,
                textStyleTitle = MaterialTheme.typography.headlineSmall,
                textStyleType = MaterialTheme.typography.bodyLarge
            )
        }
//todo : Periksa ukuran nya tidak biasa
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FavoriteButton(
                favorite = activeAudio.isFavorite,
                size = 58.dp,
                onClick = onClickFavorite,
            )
            TogglePlay(
                sizeIcon = 60.dp,
                isPlaying = isPlaying,
                onTogglePlay = {
                    onPlayHandle(activeAudio)
                }
            )

            TimerButton(
                size = 58.dp,
                onClickTimer = onTimerHandle
            )
        }
    }
}
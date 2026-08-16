package com.nndwn.whitenoise.ui.features.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.ui.components.DurationSessionText
import com.nndwn.whitenoise.ui.components.RunningText
import com.nndwn.whitenoise.ui.components.TogglePlay
import com.nndwn.whitenoise.ui.theme.dimens

@Composable
fun MiniPlayBottom(
    data : DataAudio,
    timePlaying : Long,
    isPlaying: Boolean,
    showWarning : Boolean,
    message : String,
    colorBackgroundMessage : Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
    onTogglePlay: () -> Unit,
    navigate : () -> Unit,
){

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.medium)
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Notice(
            text = message,
            containerColor = colorBackgroundMessage
        )

        Row( modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = MaterialTheme.dimens.elevationMedium,
                shape = if (showWarning){
                    MaterialTheme.shapes.small.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp)
                    )
                }else {
                    MaterialTheme.shapes.small
                })
            .background(containerColor)
            .clip(MaterialTheme.shapes.small)
            .padding(MaterialTheme.dimens.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium),
        ) {
           Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember {MutableInteractionSource() },
                        onClick = navigate
                    ),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium),
                verticalAlignment = Alignment.CenterVertically
            ){
               Box(
                   modifier = Modifier
                       .size(MaterialTheme.dimens.iconExtraLarge)
                       .shadow(
                           elevation = MaterialTheme.dimens.elevationSmall,
                           shape = MaterialTheme.shapes.extraSmall)
                       .background(MaterialTheme.colorScheme.onBackground)
               ) {
                   Image(
                       painter = painterResource(data.cover),
                       contentDescription = "mini play cover ${data.name}",
                       modifier = Modifier.fillMaxSize(),
                       contentScale = ContentScale.Crop
                   )
               }

               Column(
                   verticalArrangement = Arrangement.Center
               ) {
                   RunningText(
                       title = data.name,
                       subtitle = data.type.name,
                       fontSize = 17.sp
                   )
                   DurationSessionText(
                       isVisible = timePlaying > 0L,
                       timePlaying = timePlaying,
                   )
               }
            }

            TogglePlay(
                sizeIcon = 48.dp,
                paddingIcon = 8.dp,
                isPlaying = isPlaying,
                onTogglePlay = onTogglePlay
            )
        }
    }

}


@Composable
private fun Notice(
    text : String,
    containerColor : Color = Color.Unspecified,
){
    AnimatedVisibility(
        visible = text.isNotEmpty(),
        enter = slideInVertically (initialOffsetY = {it}) + fadeIn(),
        exit = slideOutVertically (targetOffsetY = {it}) + fadeOut()
    ) {
        Surface (
            color = if (containerColor != Color.Unspecified ) containerColor else MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.small.copy(
                bottomEnd = CornerSize(0.dp),
                bottomStart = CornerSize(0.dp)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.medium),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}

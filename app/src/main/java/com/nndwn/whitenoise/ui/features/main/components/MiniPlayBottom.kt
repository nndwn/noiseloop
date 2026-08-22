package com.nndwn.whitenoise.ui.features.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
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
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.ui.components.DurationSessionText
import com.nndwn.whitenoise.ui.components.RunningText
import com.nndwn.whitenoise.ui.components.TogglePlay
import com.nndwn.whitenoise.ui.theme.dimens


data class MiniPlayState(
    val audio : DataAudio,
    val timePlaying: Long,
    val isPlaying : Boolean,
    val message : String,
    val colorBackgroundMessage: Color,
    val containerColor: Color,
)
@Composable
fun MiniPlayBottom(
    state: MiniPlayState,
    modifier: Modifier = Modifier,
    onTogglePlay: () -> Unit,
    navigate : () -> Unit,
){

    val animatedEffectChangeColor by animateColorAsState(
        targetValue = state.containerColor,
        animationSpec = tween(durationMillis = 500),
        label = "MiniPlayColorTransition"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.medium)
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Notice(
            text = state.message,
            containerColor = state.colorBackgroundMessage
        )

        Row( modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = MaterialTheme.dimens.elevationMedium,
                shape = if (state.message.isNotEmpty()){
                    MaterialTheme.shapes.small.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp)
                    )
                }else {
                    MaterialTheme.shapes.small
                })
            .background(animatedEffectChangeColor)
            .clip(MaterialTheme.shapes.small)
            .padding(MaterialTheme.dimens.medium)
            ,
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
                       painter = painterResource(state.audio.cover),
                       contentDescription = "mini play cover ${state.audio.name}",
                       modifier = Modifier.fillMaxSize(),
                       contentScale = ContentScale.Crop
                   )
               }

               Column(
                   verticalArrangement = Arrangement.Center
               ) {
                   RunningText(
                       title = state.audio.name,
                       subtitle = state.audio.type.name,
                       style = MaterialTheme.typography.titleMedium,
                   )
                   DurationSessionText(
                       timePlaying = state.timePlaying,
                   )
               }
            }

            TogglePlay(
                sizeIcon = MaterialTheme.dimens.iconExtraLarge,
                isPlaying = state.isPlaying,
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

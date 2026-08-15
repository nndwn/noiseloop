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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.whitenoise.data.local.InitialAudioData
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.ui.components.DurationSessionText
import com.nndwn.whitenoise.ui.components.RunningText
import com.nndwn.whitenoise.ui.components.TogglePlay
import com.nndwn.whitenoise.ui.theme.Dimens
import com.nndwn.whitenoise.ui.theme.Palette
import com.nndwn.whitenoise.ui.theme.PlusJakartaFontFamily
import kotlin.time.Duration.Companion.minutes

@Composable
fun MiniPlayBottom(
    data : DataAudio,
    timePlaying : Long,
    isPlaying: Boolean,
    showWarning : Boolean,
    warningText : String,
    colorWarnBg : Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onTogglePlay: () -> Unit,
    toDetailScreen : () -> Unit,
    onWarningDismiss: () -> Unit
){

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingHorizontal, vertical = 14.dp)

        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        WarnNotice(
            active = showWarning,
            text = warningText,
            onDismiss = onWarningDismiss,
            colorBackground = colorWarnBg
        )

        Row( modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = if (showWarning){
                    RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                }else {
                    RoundedCornerShape(8.dp)
                })
            .background(backgroundColor)
            .clip(RoundedCornerShape(8.dp))
            .padding(15.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
           Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember {MutableInteractionSource() }
                    ) {
                        toDetailScreen()
                    },
                verticalAlignment = Alignment.CenterVertically
            ){
               Box(
                   modifier = Modifier
                       .size(50.dp)
                       .shadow(elevation = 4.dp, shape = RoundedCornerShape(5.dp))
                       .background(color = Palette.White)
               ) {
                   Image(
                       painter = painterResource(data.cover),
                       contentDescription = "mini play cover ${data.name}",
                       modifier = Modifier.fillMaxSize(),
                       contentScale = ContentScale.Crop
                   )
               }

               Spacer(Modifier.width(16.dp))
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

            Spacer(Modifier.size(16.dp))
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
private fun WarnNotice(
    colorBackground : Color,
    active : Boolean,
    text: String,
    onDismiss : () -> Unit
){
    AnimatedVisibility(
        visible = active,
        enter = slideInVertically (initialOffsetY = {it}) + fadeIn(),
        exit = slideOutVertically (targetOffsetY = {it}) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorBackground,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .clickable{ onDismiss()}
                .padding(horizontal = Dimens.PaddingHorizontal, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontFamily = PlusJakartaFontFamily,
                fontWeight = FontWeight.Medium,
                color = Palette.White,
                fontSize = 13.sp
            )
        }

    }
}

@Composable
@Preview
private fun Preview(){
    var isPlaying by remember { mutableStateOf(true) }
    MiniPlayBottom(
        data = InitialAudioData.audioList[0],
        timePlaying = 1.minutes.inWholeSeconds,
        isPlaying = true,
        backgroundColor = Palette.Black3,
        showWarning = true,
        onWarningDismiss = {},
        warningText = "",
        toDetailScreen = {},
        colorWarnBg = Palette.White,
        onTogglePlay = {
            isPlaying = !isPlaying
        }
    )
}
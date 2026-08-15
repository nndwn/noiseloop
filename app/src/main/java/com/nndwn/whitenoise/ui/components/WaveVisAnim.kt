package com.nndwn.whitenoise.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nndwn.whitenoise.ui.theme.Palette
import kotlinx.coroutines.isActive



@Composable
fun WaveVisAnim(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    size : Dp = 100.dp,
    alignment : Alignment.Horizontal = Alignment.CenterHorizontally
    ) {
    BoxWithConstraints(
        modifier = modifier
            .size(size),
        contentAlignment = Alignment.Center) {

        val barWidth = maxWidth * 0.09f
        val spaceBetween = maxWidth * 0.05f
        val horizontalPadding = maxWidth * 0.08f

        val scale1 = rememberAudioBarScale(isPlaying, minTarget = 0.3f, maxTarget = 0.7f, durationMillis = 450)
        val scale2 = rememberAudioBarScale(isPlaying, minTarget = 0.2f, maxTarget = 0.5f, durationMillis = 350)
        val scale3 = rememberAudioBarScale(isPlaying, minTarget = 0.4f, maxTarget = 0.7f, durationMillis = 500)
        val scale4 = rememberAudioBarScale(isPlaying, minTarget = 0.1f, maxTarget = 0.4f, durationMillis = 400)

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(spaceBetween, alignment),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            SingleAudioBar(scale1, barWidth)
            SingleAudioBar(scale2, barWidth)
            SingleAudioBar(scale3, barWidth)
            SingleAudioBar(scale4, barWidth)
        }
    }
}


@Composable
private fun rememberAudioBarScale(
    isPlaying: Boolean,
    minTarget: Float,
    maxTarget: Float,
    durationMillis: Int,
): Float {
    val scale = remember { Animatable(0.1f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                scale.animateTo(maxTarget, tween(durationMillis, easing = LinearEasing ))
                scale.animateTo(minTarget, tween(durationMillis, easing = LinearEasing))
            }
        }else {
            scale.animateTo(0.1f, tween (400))
        }
    }
    return scale.value
}

@Composable
private fun SingleAudioBar(scale: Float, width : Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight(fraction = scale)
            .background(
                color =  Palette.White ,
                shape = RoundedCornerShape(percent = 50)
            )
    )
}

@Composable
@Preview
private fun PreviewWavVisAnim(){

    var trigger by remember { mutableStateOf(false) }
    Button(onClick = {
        trigger = !trigger
    }) { }
    WaveVisAnim(trigger)
}


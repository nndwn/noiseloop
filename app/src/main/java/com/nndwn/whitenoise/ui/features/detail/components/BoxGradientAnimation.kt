package com.nndwn.whitenoise.ui.features.detail.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


@Composable
fun BoxGradientAnimation(colors: List<Color>){
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
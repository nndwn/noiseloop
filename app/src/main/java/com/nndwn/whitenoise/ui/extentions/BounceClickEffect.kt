package com.nndwn.whitenoise.ui.extentions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.bounceClickEffect(isTriggered : Boolean) : Modifier = composed {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(isTriggered) {
        if (isTriggered){
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        } else {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 100)
            )
        }
    }
    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}
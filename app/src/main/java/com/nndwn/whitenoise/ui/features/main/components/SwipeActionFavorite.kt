package com.nndwn.whitenoise.ui.features.main.components

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ui.theme.Palette
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


@Composable
fun SwipeActionFavorite(
    isFavorite: Boolean,
    colorSelected: Color,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Boolean) -> Unit
){
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val currentOnToggleFavorite by rememberUpdatedState(onToggleFavorite)

    var dragOffset by remember { mutableFloatStateOf(0f) }
    var hasVibrated by remember { mutableStateOf(false) }
    val actionThresholdPx = with(density) { 90.dp.toPx() }
    val triggerThresholdPx = actionThresholdPx * 0.6f

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {

                        val isTriggered = dragOffset >= triggerThresholdPx

                        hasVibrated = false
                        scope.launch {
                            animate(
                                initialValue = dragOffset,
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = 0.7f,
                                    stiffness = 300f
                                )
                            ) { value, _ -> dragOffset = value }


                            if (isTriggered) currentOnToggleFavorite()
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        // Only allow swiping to the right
                        dragOffset = (dragOffset + dragAmount).coerceIn(0f, actionThresholdPx)
                        
                        if (dragOffset >= triggerThresholdPx) {
                            if (!hasVibrated) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                hasVibrated = true
                            }
                        } else {
                            hasVibrated = false
                        }
                    }
                )
            }
    ) {
        val dragAmount = abs(dragOffset)
        val isNotSettled = dragAmount > 1f

        if (isNotSettled) {
            val progress = (dragAmount / actionThresholdPx).coerceIn(0f, 1f)

            val dynamicBackground = colorSelected.copy(alpha = progress.coerceAtLeast(0.3f))

            val iconScale = if (dragAmount >= triggerThresholdPx) 1.2f else 0.5f + (progress * 0.5f)

            val iconRes = if (isFavorite) R.drawable.ic_heart_line else R.drawable.ic_heart

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(dynamicBackground)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = if (isFavorite) "Remove from Favorite" else "Add to Favorite",
                    tint = Palette.White,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(iconScale)
                )
            }
        }

        Box(
            modifier = Modifier.offset {
                IntOffset(dragOffset.roundToInt(), 0)
            }
        ) {
            content(isNotSettled)
        }
    }
}

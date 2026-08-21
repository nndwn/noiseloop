package com.nndwn.whitenoise.ui.components

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeActionWrapper(
    modifier: Modifier = Modifier,
    onActionTriggered: () -> Unit,
    threshold: Dp = 90.dp,
    swipeBackground: @Composable (dragOffset: Float, isTriggered: Boolean) -> Unit,
    content: @Composable (isSwiping: Boolean) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var dragOffset by remember { mutableFloatStateOf(0f) }
    var hasVibrated by remember { mutableStateOf(false) }

    val actionThresholdPx = with(density) { threshold.toPx() }
    val triggerThresholdPx = actionThresholdPx * 0.6f
    val isSwiping = abs(dragOffset) > 1f

    Box(
        modifier = modifier.pointerInput(Unit) {
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

                        if (isTriggered) onActionTriggered()
                    }
                },
                onHorizontalDrag = { change, dragAmount ->
                    change.consume()
                    // Currently only supports swipe to the right
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
        if (isSwiping) {
            swipeBackground(dragOffset, dragOffset >= triggerThresholdPx)
        }

        Box(
            modifier = Modifier.offset {
                IntOffset(dragOffset.roundToInt(), 0)
            }
        ) {
            content(isSwiping)
        }
    }
}

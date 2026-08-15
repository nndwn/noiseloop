package com.nndwn.whitenoise.ui.extentions

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.disableMultiTouch(): Modifier = this.pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            if (event.changes.size > 1) {
                event.changes.forEach { it.consume() }
            }
        }
    }
}
package com.nndwn.whitenoise.ui.extentions

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


fun Modifier.horizontalFadingEdge(
    isVisible: Boolean,
    edgeWidth : Dp = 20.dp
): Modifier = if (!isVisible) this else this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val edgeWidthPx = edgeWidth.toPx()

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, Color.Black),
                startX = 0f,
                endX = edgeWidthPx
            ),
            size = Size(edgeWidthPx, size.height),
            blendMode = BlendMode.DstIn
        )
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Black, Color.Transparent),
                startX = size.width - edgeWidthPx,
                endX = size.width
            ),
            topLeft = Offset(size.width - edgeWidthPx, 0f),
            size = Size(edgeWidthPx, size.height),
            blendMode = BlendMode.DstIn
        )
    }

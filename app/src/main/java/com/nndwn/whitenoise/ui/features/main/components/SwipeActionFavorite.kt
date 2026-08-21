package com.nndwn.whitenoise.ui.features.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ui.components.SwipeActionWrapper
import com.nndwn.whitenoise.ui.theme.dimens

@Composable
fun SwipeActionFavorite(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    colorSelected : Color,
    content: @Composable (Boolean) -> Unit,
) {


    SwipeActionWrapper(
        modifier = modifier,
        onActionTriggered = onToggleFavorite,
        swipeBackground = { dragOffset, isTriggered ->
            SwipeBackground(
                dragAmount = dragOffset,
                isTriggered = isTriggered,
                isFavorite = isFavorite,
                colorSelected = colorSelected
            )
        },
        content = content
    )
}

@Composable
private fun SwipeBackground(
    dragAmount: Float,
    isTriggered: Boolean,
    isFavorite: Boolean,
    colorSelected : Color
) {
    // Max threshold constant (sync with wrapper default if needed)
    val actionThresholdPx = 250f // Approximation for visual progress
    val progress = (dragAmount / actionThresholdPx).coerceIn(0f, 1f)
    
    val dynamicBackground = colorSelected.copy(
        alpha = progress.coerceAtLeast(0.3f)
    )
    
    val iconScale = if (isTriggered) 1.2f else 0.5f + (progress * 0.5f)
    val iconRes = if (isFavorite) R.drawable.ic_heart_line else R.drawable.ic_heart
    val tintColor = if (dynamicBackground.luminance() > 0.5f) 
        MaterialTheme.colorScheme.onSurface 
    else 
        MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dynamicBackground)
            .padding(horizontal = MaterialTheme.dimens.large),
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = if (isFavorite) "Remove from Favorite" else "Add to Favorite",
            tint = tintColor,
            modifier = Modifier
                .size(MaterialTheme.dimens.iconMedium)
                .scale(iconScale)
        )
    }
}

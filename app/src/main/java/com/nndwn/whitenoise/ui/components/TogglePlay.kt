package com.nndwn.whitenoise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ui.theme.dimens

@Composable
fun TogglePlay(
    sizeIcon: Dp,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {

    IconButton(
        onClick = { onTogglePlay() },
        modifier = Modifier
            .size(sizeIcon)
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .background(
                color = MaterialTheme.colorScheme.primary ,
                shape = CircleShape)
            .padding(MaterialTheme.dimens.small)
    ) {
        val iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val description = if (isPlaying) "sound stop" else "sound playing"

        Icon(
            painter = painterResource(iconRes),
            contentDescription = description,
            tint = Color.Black,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { blendMode = BlendMode.DstOut }
        )
    }
}
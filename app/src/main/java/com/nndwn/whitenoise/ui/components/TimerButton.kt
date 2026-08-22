package com.nndwn.whitenoise.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ui.extentions.bounceClickEffect
import com.nndwn.whitenoise.ui.theme.dimens

@Composable
fun TimerButton(
    modifier: Modifier = Modifier,
    size : Dp ,
    onClickTimer: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    IconButton(
        onClick = onClickTimer,
        interactionSource = interactionSource,
        modifier = modifier
            .size(size)
            .padding(MaterialTheme.dimens.medium)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_timer),
            contentDescription = "sound timer",
            modifier = Modifier
                .fillMaxSize()
                .bounceClickEffect(isPressed)
        )
    }
}
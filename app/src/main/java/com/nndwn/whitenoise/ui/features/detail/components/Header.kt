package com.nndwn.whitenoise.ui.features.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ui.LocalToggleSidebar
import com.nndwn.whitenoise.ui.components.ThreeDotsHorizontal
import com.nndwn.whitenoise.ui.theme.dimens

@Composable
fun Header(
    modifier: Modifier,
    onClickBack : () -> Unit = {},
){
    val handleSidebar = LocalToggleSidebar.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.dimens.medium,
                vertical = MaterialTheme.dimens.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(MaterialTheme.dimens.iconExtraLarge)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onClickBack
                )
        ){
            Icon(
                painter = painterResource(R.drawable.ic_down2),
                contentDescription = stringResource(R.string.btn_text_back),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
            )
        }

        ThreeDotsHorizontal(
            onClick = handleSidebar
        )
    }
}


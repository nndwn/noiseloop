package com.nndwn.whitenoise.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ui.extentions.bounceClickEffect
import com.nndwn.whitenoise.ui.theme.dimens

@Composable
fun FavoriteButton(
    favorite: Boolean,
    modifier : Modifier = Modifier,
    size : Dp,
    onClick: () -> Unit
){
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .padding(MaterialTheme.dimens.medium)
    ) {
        Icon(
            painter = painterResource(if (favorite) R.drawable.ic_heart else R.drawable.ic_heart_line),
            contentDescription = if (favorite) "sound favorite" else "sound not favorite",
            modifier = Modifier
                .fillMaxSize()
                .bounceClickEffect(favorite)
        )
    }
}
package com.nndwn.whitenoise.ui.features.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.LabelAudio
import com.nndwn.whitenoise.ui.components.WaveVisAnim
import com.nndwn.whitenoise.ui.theme.dimens
import com.nndwn.whitenoise.utils.toTitleCase


@Composable
fun ItemSound(
    active : Boolean,
    item : DataAudio,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = MaterialTheme.dimens.small,
                horizontal = MaterialTheme.dimens.medium)
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = MaterialTheme.colorScheme.surfaceVariant),
                onClickLabel = "play sound ${item.name}",
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)
    ){
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small,
            modifier = modifier
                .size(MaterialTheme.dimens.iconExtraLarge)
        ) {
            Image(
                painter = painterResource(item.cover),
                contentDescription = "cover ${item.name}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop

            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)
        ) {
            Text(
                text = item.name.toTitleCase(),
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = item.name.toTitleCase(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = modifier
                )
                if (item.label == LabelAudio.ONLINE){
                    Spacer(Modifier.width(MaterialTheme.dimens.extraSmall))
                    Icon(
                        painter = painterResource(R.drawable.ic_down),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                    )
                }
            }
        }
        WaveVisAnim(active, size = 40.dp)
    }
}






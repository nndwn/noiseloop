package com.nndwn.whitenoise.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.nndwn.whitenoise.utils.formatTime

@Composable
fun DurationSessionText(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    timePlaying: Long
) {
    AnimatedVisibility(
        visible = timePlaying > 0L,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = modifier
        ) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatTime(timePlaying),
                style = style.copy(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                ),
                maxLines = 1
            )
        }

    }
}
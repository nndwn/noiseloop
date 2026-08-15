package com.nndwn.whitenoise.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.whitenoise.ui.theme.Palette
import com.nndwn.whitenoise.ui.theme.PlusJakartaFontFamily
import com.nndwn.whitenoise.utils.formatTime

@Composable
fun DurationSessionText(
    isVisible : Boolean,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 13.sp,
    timePlaying : Long
) {
    AnimatedVisibility(
        visible = isVisible ,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = modifier
        ) {
            Spacer(Modifier.height(4.dp))
            Text(
                fontFamily = PlusJakartaFontFamily,
                text = formatTime(timePlaying),
                fontSize = fontSize,
                color = Palette.White.copy(alpha = 0.6f),
                maxLines = 1
            )
        }

    }
}
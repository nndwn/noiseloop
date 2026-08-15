package com.nndwn.whitenoise.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.nndwn.whitenoise.ui.extentions.horizontalFadingEdge
import com.nndwn.whitenoise.ui.theme.Palette
import com.nndwn.whitenoise.ui.theme.PlusJakartaFontFamily
import com.nndwn.whitenoise.utils.toTitleCase

@Composable
fun RunningText(
    title : String,
    subtitle : String,
    fontSize : TextUnit
) {
    val density = LocalDensity.current
    var textHeight by remember { mutableStateOf(0.dp) }
    var isOverflow by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(fontWeight = FontWeight.Bold, color = Palette.White)
                ){
                    append(title.toTitleCase())
                }
                append(" . ")
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Light,
                    color = Palette.White.copy(alpha = 0.7f)
                )) {
                    append(subtitle.toTitleCase())
                }
            },
            fontFamily = PlusJakartaFontFamily,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Palette.White,
            maxLines = 1,
            onTextLayout = { textLayoutResult ->

                isOverflow = with(density) { textLayoutResult.size.width.toDp() > maxWidth }
                textHeight = with(density) { textLayoutResult.size.height.toDp() }

            },
            modifier = Modifier
                .horizontalFadingEdge(isVisible = isOverflow, edgeWidth = 30.dp)
                .basicMarquee(
                iterations = Int.MAX_VALUE,
                repeatDelayMillis = 1500,
                initialDelayMillis = 1500
            )
        )
    }
}
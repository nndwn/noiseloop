package com.nndwn.whitenoise.ui.extentions

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalResources
import com.nndwn.whitenoise.ui.theme.CharcoalDarkGray
import com.nndwn.whitenoise.ui.theme.DarkNavyGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@Composable
fun rememberDominantColor(
    coverResId: Int?,
    defaultColor: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary),
    colorGenerator: suspend (Resources, Int) ->  List<Color> = { resources, resId ->

        withContext(Dispatchers.IO) {
            val options = BitmapFactory.Options().apply { inSampleSize = 4 }
            val bitmap = BitmapFactory.decodeResource(resources, resId, options)

            suspendCancellableCoroutine { continuation ->
                if (bitmap == null) {
                    continuation.resume(defaultColor)
                    return@suspendCancellableCoroutine
                }
                val palette = androidx.palette.graphics.Palette.from(bitmap).generate()
                val color1 = palette.vibrantSwatch?.rgb
                    ?: defaultColor.first().toArgb()
                val color2 =  palette.darkVibrantSwatch?.rgb
                    ?: defaultColor.last().toArgb()

                continuation.resume(listOf(Color(color1), Color(color2)))
            }
        }
    }
): State<List<Color>> {

    val resources = LocalResources.current
    val dominantColor = remember { mutableStateOf(defaultColor) }

    LaunchedEffect(coverResId) {
        if (coverResId != null) {
            dominantColor.value = colorGenerator(resources, coverResId)
        } else {
            dominantColor.value = defaultColor
        }
    }

    return dominantColor
}
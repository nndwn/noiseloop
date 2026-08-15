package com.nndwn.whitenoise.ui.features.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.LabelAudio
import com.nndwn.whitenoise.data.local.entity.TypeAudio
import com.nndwn.whitenoise.ui.extentions.rememberDominantColor
import com.nndwn.whitenoise.ui.utils.LocalIsTablet

@Composable
private fun DetailInteractivePreviewWrapper(isTablet: Boolean) {
    val sampleAudio = remember {
        DataAudio(
            id = "1",
            name = "Rain on Window",
            type = TypeAudio.NATURE,
            sourcePath = "audio/rain.mp3",
            cover = R.drawable.a5_peaceful_rain,
            label = LabelAudio.OFFLINE,
            isFavorite = false
        )
    }

    var isPlaying by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var audioItem by remember { mutableStateOf(sampleAudio.copy(isFavorite = isFavorite)) }
    val mockColors by rememberDominantColor(sampleAudio.cover)

    CompositionLocalProvider(LocalIsTablet provides isTablet) {
        DetailScreen(
            onBackClick = { /* No-op in preview */ },
            colors = mockColors,
            timePlaying = 125000L, // 2:05
            playing = isPlaying,
            onFavoriteClick = { 
                isFavorite = !isFavorite
                audioItem = audioItem.copy(isFavorite = isFavorite)
            },
            itemAudio = audioItem,
            onTogglePlay = { isPlaying = !isPlaying },
            onTimerClick = { /* No-op in preview */ },
            onClickSidebarRight = { /* No-op in preview */ }
        )
    }
}

//@Preview(name = "Phone Detail", device = "spec:width=411dp,height=891dp", showSystemUi = true)
//@Composable
//private fun PreviewDetailPhone() {
//    DetailInteractivePreviewWrapper(isTablet = false)
//}

@Preview(name = "Tablet Detail", device = "spec:width=1280dp,height=800dp,orientation=landscape", showSystemUi = true)
@Composable
private fun PreviewDetailTablet() {
    DetailInteractivePreviewWrapper(isTablet = true)
}
//
//@Preview(name = "Foldable Detail", device = "spec:width=673dp,height=841dp", showSystemUi = true)
//@Composable
//private fun PreviewDetailFoldable() {
//    DetailInteractivePreviewWrapper(isTablet = true)
//}

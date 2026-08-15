package com.nndwn.whitenoise.data.extensions

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.LabelAudio
import com.nndwn.whitenoise.utils.toTitleCase

fun DataAudio.asMediaItem(context: Context): MediaItem {
    val imageUri = "android.resource://${context.packageName}/$cover".toUri()

    val mediaUriStr = when (label) {
        LabelAudio.ONLINE -> sourcePath
        LabelAudio.OFFLINE -> {
            if (sourcePath.startsWith("audio/")) {
                "asset:///$sourcePath"
            } else {
                if (sourcePath.startsWith("file://")) sourcePath else "file://$sourcePath"
            }
        }
    }


    return MediaItem.Builder()
        .setMediaId(id)
        .setUri(mediaUriStr.toUri())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setIsPlayable(true)
                .setIsBrowsable(true)
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .setTitle(name.toTitleCase())
                .setArtist(type.name.toTitleCase())
                .setArtworkUri(imageUri)
                .build()
        )
        .build()
}
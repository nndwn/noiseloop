package com.nndwn.whitenoise.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailAudioInfo(
    title : String,
    type : String,
    playing : Boolean,
    timePlaying : Long,
    fontSizeTitle : TextUnit = 24.sp,
    fontSizeType : TextUnit = 16.sp
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,

    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ){
            RunningText(
                title = title,
                subtitle = type,
                fontSize = fontSizeTitle
            )
            DurationSessionText(
                isVisible = timePlaying > 0L,
                timePlaying = timePlaying,
                fontSize = fontSizeType
            )
        }
        Spacer(Modifier.size(16.dp))
        WaveVisAnim(
            alignment = Alignment.End,
            size = 49.dp,
            isPlaying = playing
        )

    }

}
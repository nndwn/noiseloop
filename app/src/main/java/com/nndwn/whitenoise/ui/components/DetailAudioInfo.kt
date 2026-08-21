package com.nndwn.whitenoise.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.nndwn.whitenoise.ui.theme.dimens

@Composable
fun DetailAudioInfo(
    title : String,
    type : String,
    playing : Boolean,
    timePlaying : Long,
    textStyleTitle : TextStyle,
    textStyleType : TextStyle
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium),
        verticalAlignment = Alignment.CenterVertically,

    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ){
            RunningText(
                title = title,
                subtitle = type,
                style = textStyleTitle
            )
            DurationSessionText(
                timePlaying = timePlaying,
                style = textStyleType
            )
        }

        WaveVisAnim(
            alignment = Alignment.End,
            size = MaterialTheme.dimens.iconExtraLarge,
            isPlaying = playing
        )
    }
}
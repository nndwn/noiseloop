package com.nndwn.whitenoise.ui.features.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ui.components.ThreeDotsHorizontal
import com.nndwn.whitenoise.ui.theme.Dimens
import com.nndwn.whitenoise.ui.theme.Palette
import com.nndwn.whitenoise.ui.theme.PlusJakartaFontFamily

@Composable
fun Header (
    modifier: Modifier = Modifier,
    withSidebar : Boolean = false,
    fontSize : TextUnit = 20.sp,
    onMenuClick : () -> Unit = {}
) {
    Box( modifier = modifier
        .fillMaxWidth()
        .then(
            if (withSidebar){
                Modifier.shadow(8.dp).background(Palette.Black3)
            } else Modifier
        )
        .statusBarsPadding()
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingHorizontal, vertical = 10.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
        ){
            if (!withSidebar){
                Icon(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    tint = Palette.White,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = stringResource(R.string.app_name),
                fontFamily = PlusJakartaFontFamily,
                fontWeight = FontWeight.Bold,
                color = Palette.White,
                fontSize = fontSize,
                modifier = Modifier.weight(1f)
            )
            if (withSidebar){
                ThreeDotsHorizontal {
                    onMenuClick()
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview(){
    Column {

    }
}
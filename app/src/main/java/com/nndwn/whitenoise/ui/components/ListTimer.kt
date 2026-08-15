package com.nndwn.whitenoise.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.whitenoise.BuildConfig
import com.nndwn.whitenoise.R

import com.nndwn.whitenoise.service.TimerTime
import com.nndwn.whitenoise.ui.extentions.getLabel
import com.nndwn.whitenoise.ui.theme.Palette
import com.nndwn.whitenoise.ui.theme.PlusJakartaFontFamily

@Composable
fun ListTimer(
    show : Boolean,
    onClick : (TimerTime) -> Unit,
    onDismiss : () -> Unit = {}
){
    SlideUpPanel(
        enableDragToDismiss = true,
        visible = show,
        containerColor = Palette.Black2,
        onDismiss = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                fontFamily = PlusJakartaFontFamily,
                color = Palette.White,
                text = stringResource(R.string.btn_text_timer),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = Palette.White.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(5.dp))

            TimerTime.entries.reversed()
                .filter { item ->
                    item != TimerTime.TEST_10_SEC || BuildConfig.DEBUG
                }
                .forEach { item ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                onClick(item)
                                onDismiss()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        fontFamily = PlusJakartaFontFamily,
                        color = Palette.White,
                        text = item.getLabel(LocalContext.current),
                        fontWeight = FontWeight.Normal,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        ListTimer(
            show = true,
            onClick = {}
        )
    }
}
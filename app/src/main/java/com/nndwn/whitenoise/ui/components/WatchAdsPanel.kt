package com.nndwn.whitenoise.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.ui.theme.Dimens
import com.nndwn.whitenoise.ui.theme.Palette
import com.nndwn.whitenoise.ui.theme.PlusJakartaFontFamily


@Composable
fun WatchAdsPanel(
    showPanel : Boolean,
    onDismiss : () -> Unit = {},
    onWatchAds : () -> Unit = {},
    onRemoveAds : () -> Unit = {}
) {
    SlideUpPanel(
        visible = showPanel,
        containerColor = Palette.White,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingHorizontal, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_coffee),
                    contentDescription = stringResource(R.string.buy_coffee),
                    tint = Palette.Black2.copy(alpha = 0.65f),
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    fontFamily = PlusJakartaFontFamily,
                    text = stringResource(R.string.buy_coffee),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Palette.Black2
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.text_dialog_watch_ads),
                fontFamily = PlusJakartaFontFamily,
                fontWeight = FontWeight.Medium,
                color = Palette.Black2.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Palette.Black2,
                    contentColor = Palette.White
                ),
                onClick = {
                    onWatchAds()

                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    fontFamily = PlusJakartaFontFamily,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.btn_text_watch_ad),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    contentColor = Palette.Black2
                ),
                onClick = {
                    onRemoveAds()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    fontFamily = PlusJakartaFontFamily,
                    fontWeight = FontWeight.Normal,
                    text = stringResource(R.string.btn_text_remove_ad),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R.string.btn_maybe_later),
                    fontFamily = PlusJakartaFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Palette.Black2.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())

    }
}



@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun Preview() {
    var show by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                show = !show
            }
        ) {
            Text("Show and Hide")
        }


    }
    WatchAdsPanel(true)
}

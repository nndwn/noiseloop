package com.nndwn.whitenoise.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import com.nndwn.whitenoise.BuildConfig
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.service.TimerTime
import com.nndwn.whitenoise.ui.extentions.getLabel
import com.nndwn.whitenoise.ui.theme.dimens

@Composable
fun ListTimer(
    show : Boolean,
    onClick : (TimerTime) -> Unit,
    onDismiss : () -> Unit = {}
){
    SlideUpPanel(
        state = SlideUpPanelState(
            visible = show,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            enabledDragToDismiss = true
        ),
        onDismiss = onDismiss,
    ) {
        Text(
            text = stringResource(R.string.btn_text_timer),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(MaterialTheme.dimens.small)
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = MaterialTheme.dimens.borderSmall,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(Modifier.height(MaterialTheme.dimens.small))

        TimerTime.entries.reversed()
            .filter { item ->
                item != TimerTime.TEST_10_SEC || BuildConfig.DEBUG
            }
            .forEach { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.dimens.small)
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
                        text = item.getLabel(LocalContext.current),
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
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
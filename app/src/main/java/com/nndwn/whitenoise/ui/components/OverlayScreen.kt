package com.nndwn.whitenoise.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.service.TimerTime
import com.nndwn.whitenoise.ui.features.main.components.DialogNotice
import com.nndwn.whitenoise.ui.utils.LocalSizeWidth

data class OverlayScreenState(
    val overlayTimer : Boolean,
    val showAdsDialog : Boolean,
    val isLoadingAd : Boolean,
    @StringRes val noticeMessage: Int?,
    val adPrice: String?,
    val activeAudio: DataAudio?
)

@Composable
fun OverlayScreen(
    state : OverlayScreenState,
    onDismissTimerOverlay : () -> Unit,
    onClickTimerOverlay : (TimerTime) -> Unit,
    onDismissAds : () -> Unit,
    onWatchAds : () -> Unit,
    onRemoveAds : () -> Unit,
    onDismissNoticeMessage : () -> Unit,
) {
    val windowSizeWidth = LocalSizeWidth.current

    ListTimer(
        show = state.overlayTimer,
        onDismiss = onDismissTimerOverlay,
        onClick = onClickTimerOverlay
    )
    DialogWatchAds(
        price = state.adPrice,
        showPanel = state.showAdsDialog,
        onDismiss = onDismissAds,
        onWatchAds = onWatchAds,
        onRemoveAds = onRemoveAds,
    )
    LoadingScreen(state.isLoadingAd)


    if (state.activeAudio == null || windowSizeWidth != WindowWidthSizeClass.Compact ) {
        DialogNotice(
            visible = state.noticeMessage != null,
            text = state.noticeMessage?.let { stringResource(it) } ?: "",
            onDismiss = onDismissNoticeMessage
        )
    }
}
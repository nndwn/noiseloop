package com.nndwn.whitenoise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.whitenoise.data.local.InitialAudioData
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.ui.components.ListTimer
import com.nndwn.whitenoise.ui.features.main.components.MiniPlayBottom
import com.nndwn.whitenoise.ui.components.MenuOptions
import com.nndwn.whitenoise.ui.extentions.rememberDominantColor
import com.nndwn.whitenoise.ui.components.MainLayout
import com.nndwn.whitenoise.ui.components.Scrim
import com.nndwn.whitenoise.ui.components.WatchAdsPanel
import com.nndwn.whitenoise.ui.components.WaveVisAnim
import com.nndwn.whitenoise.ui.features.main.AudioFilter
import com.nndwn.whitenoise.ui.features.main.components.DialogNotice
import com.nndwn.whitenoise.ui.navigation.SoundDetailRoute
import com.nndwn.whitenoise.ui.navigation.WhiteNoiseNavHost
import com.nndwn.whitenoise.ui.utils.LocalIsTablet
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
private fun InteractivePreviewWrapper(isTablet: Boolean) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    var activeAudio by remember { mutableStateOf<DataAudio?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isSidebarOpen by remember { mutableStateOf(false) }
    var overlayTimer by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf<AudioFilter>(AudioFilter.All) }
    var sessionTrackDuration by remember { mutableLongStateOf(0L) }

    var noticeMessage by remember { mutableStateOf<Int?>(R.string.text_dialog_watch_ads) }
    var showAdsPanel by remember { mutableStateOf(false) }
    var isAdLoading by remember { mutableStateOf(false) }
    var isPremium by remember { mutableStateOf(false) }
    var showAds by remember { mutableStateOf(false) }
    var pendingAudioAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val listFilter = remember(currentFilter) {
        when (val filter = currentFilter) {
            is AudioFilter.All -> InitialAudioData.audioList
            is AudioFilter.Favorite -> InitialAudioData.audioList.filter { it.isFavorite }
            is AudioFilter.SelectedType -> InitialAudioData.audioList.filter { it.type == filter.type }
        }
    }

    val dominantColorResult by rememberDominantColor(activeAudio?.cover)

    val animatedBackgroundColor by animateColorAsState(
        targetValue = dominantColorResult.lastOrNull() ?: Color.Black,
        animationSpec = tween(durationMillis = 500),
        label = "GlobalColorTransition"
    )

    LaunchedEffect(noticeMessage) {
        if (noticeMessage != null) {
            delay(3500.milliseconds)
            noticeMessage = null
        }
    }

    LaunchedEffect(isAdLoading) {
        if (isAdLoading) {
            delay(2000.milliseconds)
            isAdLoading = false
            pendingAudioAction?.invoke()
            pendingAudioAction = null
        }
    }

    val handlePlayRequest = { action : ()-> Unit ->
        if (showAds && !isPremium){
            pendingAudioAction = action
            showAdsPanel = true
        } else {
            action()
        }
    }

    CompositionLocalProvider(LocalIsTablet provides isTablet) {
        MainLayout(
            isSidebarOpen = isSidebarOpen,
            onCloseSidebar = { isSidebarOpen = false },
            sideBarRight = {
                MenuOptions { menu ->
                    when (menu) {
                        MenuOptions.DEBUG -> {

                            isPremium = !isPremium
                            noticeMessage = if (isPremium) R.string.msg_premium_activated else null
                            isSidebarOpen = false
                        }
                        else -> {}
                    }
                }
            },
            overlayContent = {
                ListTimer(
                    show = overlayTimer,
                    onDismiss = { overlayTimer = false },
                    onClick = { /* ViewModel action in real app */ }
                )
                WatchAdsPanel(
                    showPanel = showAdsPanel,
                    onDismiss = {
                        showAdsPanel = false
                        pendingAudioAction = null
                    },
                    onWatchAds = {
                        showAdsPanel = false
                        isAdLoading = true
                    },
                    onRemoveAds = {
                        showAdsPanel = false
                        isPremium = true
                        noticeMessage = R.string.msg_premium_activated
                        pendingAudioAction?.invoke()
                        pendingAudioAction = null
                    }
                )
                if (isAdLoading){
                    Scrim(active = true, onDismiss = {} )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        WaveVisAnim(
                            isPlaying = true,
                            size = 80.dp
                        )
                    }
                }
                if (activeAudio == null || isTablet ) {
                    DialogNotice(
                        visible = noticeMessage != null,
                        text = noticeMessage?.let { stringResource(it) } ?: "",
                        onDismiss = { noticeMessage = null },
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            },
            bottomBarContent = {
                AnimatedVisibility(
                    visible = activeAudio != null && currentDestination?.hasRoute<SoundDetailRoute>() == false && !isTablet,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)

                ) {
                    activeAudio?.let { audio ->
                        MiniPlayBottom(
                            data = audio,
                            timePlaying = sessionTrackDuration,
                            isPlaying = isPlaying,
                            backgroundColor = animatedBackgroundColor,
                            showWarning = noticeMessage != null,
                            warningText = noticeMessage?.let { stringResource(it) } ?: "",
                            onWarningDismiss = { noticeMessage = null },
                            toDetailScreen = {
                                navController.navigate(SoundDetailRoute(audioId = audio.id))
                            },
                            colorWarnBg = dominantColorResult.firstOrNull() ?: Color.Gray,
                            onTogglePlay = {
                                if (!isPlaying) {
                                    handlePlayRequest { isPlaying = true }
                                } else {
                                    isPlaying = false
                                }
                            },
                            modifier = Modifier
                                .pointerInput(Unit) {}
                                .navigationBarsPadding()
                        )
                    }
                }
            }
        ) { innerPadding ->
            WhiteNoiseNavHost(
                navController = navController,
                innerPadding = innerPadding,
                listFilter = listFilter,
                isPlaying = isPlaying,
                isLoading = false,
                activeAudio = activeAudio,
                currentFilter = currentFilter,
                animatedBackgroundColor = animatedBackgroundColor,
                dominantColor = dominantColorResult,
                sessionTrackDuration = sessionTrackDuration,
                onMenuClick = { isSidebarOpen = !isSidebarOpen },
                onFilterChanged = { currentFilter = it },
                onAudioClick = { audioData ->
                    handlePlayRequest {
                        activeAudio = audioData
                        isPlaying = true
                    }
                },
                onSetFavorite = { audioData, set ->
                    activeAudio = activeAudio?.copy(isFavorite = set)
                    noticeMessage = if (set) R.string.msg_favorite_added else R.string.msg_favorite_removed
                },
                onTogglePlay = {
                    if (!isPlaying) {
                        handlePlayRequest { isPlaying = true }
                    } else {
                        isPlaying = false
                    }
                },
                onTimerClick = { overlayTimer = !overlayTimer },
                onSidebarRightClick = { isSidebarOpen = !isSidebarOpen },
                onLeftSideBar = {}
            )
        }
    }
}

@Preview(name = "Phone Preview", device = "spec:width=411dp,height=891dp,navigation=buttons", showSystemUi = true)
@Composable
private fun PreviewPhone(){
    InteractivePreviewWrapper(isTablet = false)
}

@Preview(name = "Tablet Preview", device = "spec:width=1280dp,height=800dp,orientation=landscape", showSystemUi = true)
@Composable
private fun PreviewTablet(){
    InteractivePreviewWrapper(isTablet = true)
}

@Preview(name = "Foldable Preview", device = "spec:width=673dp,height=841dp", showSystemUi = true)
@Composable
private fun PreviewFoldable(){
    InteractivePreviewWrapper(isTablet = true)
}

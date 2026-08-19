package com.nndwn.whitenoise.ui

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.whitenoise.ads.RewardedAdHelper
import com.nndwn.whitenoise.ui.components.ListTimer
import com.nndwn.whitenoise.ui.components.MainLayout
import com.nndwn.whitenoise.ui.components.MainLayoutState
import com.nndwn.whitenoise.ui.components.MenuOptions
import com.nndwn.whitenoise.ui.components.Scrim
import com.nndwn.whitenoise.ui.components.WatchAdsPanel
import com.nndwn.whitenoise.ui.components.WaveVisAnim
import com.nndwn.whitenoise.ui.features.main.components.DialogNotice
import com.nndwn.whitenoise.ui.features.main.components.MiniPlayBottom
import com.nndwn.whitenoise.ui.features.main.components.MiniPlayState
import com.nndwn.whitenoise.ui.navigation.AppRoute
import com.nndwn.whitenoise.ui.navigation.WhiteNoiseNavHost
import com.nndwn.whitenoise.ui.theme.toComposeColor
import com.nndwn.whitenoise.ui.utils.LocalIsPremium
import com.nndwn.whitenoise.ui.utils.LocalItemTimerHandler
import com.nndwn.whitenoise.ui.utils.LocalMenuOptionHandler
import com.nndwn.whitenoise.ui.utils.LocalSizeWidth
import com.nndwn.whitenoise.ui.utils.LocalToggleSidebar
import com.nndwn.whitenoise.ui.utils.gotoMail
import com.nndwn.whitenoise.ui.utils.gotoPlayStore

@Composable
fun WhiteNoiseAppUi (
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = hiltViewModel(),

) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val windowSizeWidth = LocalSizeWidth.current


    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val isLoading by appViewModel.isLoadingAd.collectAsStateWithLifecycle()

    var isSidebarOpen by remember { mutableStateOf(false) }
    var noticeMessage by remember { mutableStateOf<Int?>(null) }
    var showAdsDialog by remember { mutableStateOf(false) }
    var overlayTimer by remember { mutableStateOf(false) }

    val showAds by appViewModel.isAdsEnabled.collectAsStateWithLifecycle()
    val isPremium by appViewModel.isPremium.collectAsStateWithLifecycle()
    val activeAudio by appViewModel.isActiveAudio.collectAsStateWithLifecycle()
    val sessionTrackDuration by appViewModel.sessionTrackDuration.collectAsStateWithLifecycle()
    val isPlaying by appViewModel.isPlaying.collectAsStateWithLifecycle()

    var pendingAudioAction by remember { mutableStateOf<(() -> Unit)?>(null) }


    val handleMenuOption: (MenuOptions) -> Unit = { menu ->
        isSidebarOpen = false
        when (menu) {
            MenuOptions.DEBUG -> navController.navigate(AppRoute.Debug)
            MenuOptions.REMOVE_ADS -> if (!isPremium) showAdsDialog = true
            MenuOptions.RATE_APP -> gotoPlayStore(context)
            MenuOptions.REPORT_ISSUE -> gotoMail(context)
            MenuOptions.TIMER -> overlayTimer = true
        }
    }

    LaunchedEffect(appViewModel.uiEffect, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            appViewModel.uiEffect.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { effect ->
                    when (effect) {
                        is UiEffect.ShowToast -> noticeMessage = effect.message
                        is UiEffect.NavigateTo -> navController.navigate(effect.route)
                        is UiEffect.NavigateBack -> navController.popBackStack()
                    }
                }

        }
    }


    CompositionLocalProvider(
        LocalIsPremium provides (isPremium),
        LocalToggleSidebar provides {isSidebarOpen = !isSidebarOpen},
        LocalMenuOptionHandler provides handleMenuOption,
        LocalItemTimerHandler provides {overlayTimer = !overlayTimer}
    ) {
        MainLayout(
            state = MainLayoutState().copy(
                isOpen = isSidebarOpen
            ),
            onCloseSidebar = { isSidebarOpen = false },
            sideBarEnd = {
                MenuOptions(
                    onMenuSelected = handleMenuOption
                )
            },
            bottomBarContent = {
                AnimatedVisibility(
                    visible = activeAudio != null &&
                            currentDestination?.hasRoute<AppRoute.SoundDetail>() == false &&
                            windowSizeWidth == WindowWidthSizeClass.Compact,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    activeAudio?.let { audio ->
                        MiniPlayBottom(
                            state = MiniPlayState(
                                audio = audio,
                                timePlaying = sessionTrackDuration,
                                isPlaying = isPlaying,
                                message = noticeMessage?.let { stringResource(it) } ?: "",
                                colorBackgroundMessage =  audio.colorPrimary.toComposeColor(),
                                containerColor = audio.colorSecondary.toComposeColor()
                            ),
                            navigate = {
                                navController.navigate(AppRoute.SoundDetail)
                            },
                            onTogglePlay = {
                                    appViewModel.onPlayClick(audio)
                            },
                            modifier = Modifier
                                .pointerInput(Unit) {}
                                .navigationBarsPadding()
                        )
                    }
                }
            },
            overlayContent = {
                ListTimer(
                    show = overlayTimer,
                    onDismiss = { overlayTimer = false },
                    onClick = {  appViewModel.setFocusTimer(it) }
                )
                WatchAdsPanel(
                    showPanel = showAdsDialog,
                    onDismiss = {
                        showAdsDialog = false
                        pendingAudioAction = null
                    },
                    onWatchAds = {
                        showAdsDialog = false
                        isAdLoading = true
                        activity?.let { act ->
                            RewardedAdHelper.showAd(
                                activity = act,
                                onAdClosed = { isRewardEarned ->
                                    isAdLoading = false
                                    if (isRewardEarned) {
                                        viewModel.recordAdShown()
                                    }
                                    pendingAudioAction?.invoke()
                                    pendingAudioAction = null
                                }
                            )
                        } ?: run {
                            isAdLoading = false
                            pendingAudioAction?.invoke()
                            pendingAudioAction = null
                        }
                    },
                    onRemoveAds = {
                        showAdsDialog = false
                        activity?.let { act ->
                            viewModel.buyRemoveAds(act)
                        }
                    }
                )
                if (isAdLoading) {
                    Scrim(active = true, onDismiss = {})
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        WaveVisAnim(
                            isPlaying = true,
                            size = 80.dp
                        )
                    }
                }
                if (activeAudio == null || isTablet) {
                    DialogNotice(
                        visible = noticeMessage != null,
                        text = noticeMessage?.let { stringResource(it) } ?: "",
                        onDismiss = { noticeMessage = null }
                    )
                }
            }
        ) { innerPadding ->
            WhiteNoiseNavHost(
                navController = navController,
                innerPadding = innerPadding,
                listFilter = listFilter,
                isPlaying = isPlaying,
                isLoading = isLoading,
                activeAudio = activeAudio,
                currentFilter = currentFilter,
                animatedBackgroundColor = animatedBackgroundColor,
                dominantColor = dominantColor,
                sessionTrackDuration = sessionTrackDuration,
                onMenuClick = { isSidebarOpen = !isSidebarOpen },
                onFilterChanged = { itemFilter -> viewModel.changeFilter(itemFilter) },
                onAudioClick = { audioData -> handlePlayRequest { viewModel.playAudio(audioData) } },
                onSetFavorite = { audioData, set -> viewModel.toggleFavorite(audioData, set) },
                onTogglePlay = {
                    if (!isPlaying) {
                        handlePlayRequest { viewModel.togglePlayPause() }
                    } else {
                        viewModel.togglePlayPause()
                    }
                },
                onTimerClick = { overlayTimer = !overlayTimer },
                onSidebarRightClick = { isSidebarOpen = !isSidebarOpen },
                onLeftSideBar = handleMenuOption
            )
        }
    }

}

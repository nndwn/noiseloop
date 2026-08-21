package com.nndwn.whitenoise.ui

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.whitenoise.ui.components.MainLayout
import com.nndwn.whitenoise.ui.components.MainLayoutState
import com.nndwn.whitenoise.ui.components.MenuOptions
import com.nndwn.whitenoise.ui.components.OverlayScreen
import com.nndwn.whitenoise.ui.components.OverlayScreenState
import com.nndwn.whitenoise.ui.features.main.components.MiniPlayBottom
import com.nndwn.whitenoise.ui.features.main.components.MiniPlayState
import com.nndwn.whitenoise.ui.navigation.AppRoute
import com.nndwn.whitenoise.ui.navigation.WhiteNoiseNavHost
import com.nndwn.whitenoise.ui.theme.toComposeColor
import com.nndwn.whitenoise.ui.utils.gotoMail
import com.nndwn.whitenoise.ui.utils.gotoPlayStore

@Composable
fun WhiteNoiseAppUi (
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = hiltViewModel(),

) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val windowSizeWidth = LocalSizeWidth.current


    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    var isSidebarOpen by remember { mutableStateOf(false) }
    var noticeMessage by remember { mutableStateOf<Int?>(null) }
    var showAdsDialog by remember { mutableStateOf(false) }
    var overlayTimer by remember { mutableStateOf(false) }

    val shouldShowAd by appViewModel.shouldShowAd.collectAsStateWithLifecycle()
    val isPremium by appViewModel.isPremium.collectAsStateWithLifecycle()
    val activeAudio by appViewModel.isActiveAudio.collectAsStateWithLifecycle()
    val sessionTrackDuration by appViewModel.sessionTrackDuration.collectAsStateWithLifecycle()
    val isPlaying by appViewModel.isPlaying.collectAsStateWithLifecycle()
    val isLoadingAd by appViewModel.isLoadingAd.collectAsStateWithLifecycle()
    val adPrice by appViewModel.removeAdsPrice.collectAsStateWithLifecycle()

    var pendingAudioAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val handlePlayRequest = { action: () -> Unit ->
        val needsAds = !isPremium && shouldShowAd
        if (needsAds) {
            pendingAudioAction = action
            showAdsDialog = true
        } else {
            action()
        }
    }


    val handleMenuOption: (MenuOptions) -> Unit = { menu ->
        isSidebarOpen = false
        when (menu) {
            MenuOptions.DEBUG -> navController.navigate(AppRoute.Debug)
            MenuOptions.REMOVE_ADS -> if (!isPremium) showAdsDialog = !showAdsDialog
            MenuOptions.RATE_APP -> gotoPlayStore(context)
            MenuOptions.REPORT_ISSUE -> gotoMail(context)
            MenuOptions.TIMER -> overlayTimer = !overlayTimer
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
        LocalItemTimerHandler provides {overlayTimer = !overlayTimer},
        LocalIsPlaying provides isPlaying,
        LocalActiveAudio provides activeAudio,
        LocalPlayHandle provides {
            handlePlayRequest{
                appViewModel.onPlayClick(it)
            }
        }
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
                                handlePlayRequest{
                                    appViewModel.onPlayClick(audio)
                                }
                            },
                            modifier = Modifier
                                .pointerInput(Unit) {}
                                .navigationBarsPadding()
                        )
                    }
                }
            },
            overlayContent = {
                OverlayScreen(
                    state = OverlayScreenState(
                        overlayTimer,
                        showAdsDialog,
                        isLoadingAd,
                        noticeMessage,
                        adPrice,
                        activeAudio
                    ),
                    onDismissAds = {
                        showAdsDialog = false
                        pendingAudioAction?.invoke()
                        pendingAudioAction = null
                        appViewModel.recordAdShown()
                    },
                    onWatchAds = {
                        val activity = context as? Activity ?: return@OverlayScreen
                        appViewModel.performAdFlow(activity) {
                            showAdsDialog = false
                            pendingAudioAction?.invoke()
                            pendingAudioAction = null
                        }
                    },
                    onRemoveAds = {
                        val activity = context as? Activity ?: return@OverlayScreen
                        showAdsDialog = false
                        appViewModel.onRemoveAdsClicked(activity)
                    },
                    onClickTimerOverlay = { appViewModel.setFocusTimer(it) },
                    onDismissTimerOverlay = { overlayTimer = false },
                    onDismissNoticeMessage = {noticeMessage = null}
                )

            }
        ) { innerPadding ->
            WhiteNoiseNavHost(
                navController = navController,
                innerPadding = innerPadding,
                activeAudioId = activeAudio?.id
            )
        }
    }

}

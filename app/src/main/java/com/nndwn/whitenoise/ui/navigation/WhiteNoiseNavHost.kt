package com.nndwn.whitenoise.ui.navigation

import android.content.Intent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nndwn.whitenoise.BuildConfig
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.ui.components.MenuOptions
import com.nndwn.whitenoise.ui.features.debug.DebugAdsSimulationPanel
import com.nndwn.whitenoise.ui.features.detail.DetailScreen
import com.nndwn.whitenoise.ui.features.main.AudioFilter
import com.nndwn.whitenoise.ui.features.main.MainScreen

@Composable
fun WhiteNoiseNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    listFilter: List<DataAudio>,
    isPlaying: Boolean,
    isLoading: Boolean,
    activeAudio: DataAudio?,
    currentFilter: AudioFilter,
    animatedBackgroundColor: Color,
    dominantColor: List<Color>,
    sessionTrackDuration: Long,
    onMenuClick: () -> Unit,
    onLeftSideBar : (MenuOptions) -> Unit,
    onFilterChanged: (AudioFilter) -> Unit,
    onAudioClick: (DataAudio) -> Unit,
    onSetFavorite: (DataAudio, Boolean) -> Unit,
    onTogglePlay: () -> Unit,
    onTimerClick: () -> Unit,
    onSidebarRightClick: () -> Unit
) {


    var shouldOpenDetail by remember {
        mutableStateOf(intent?.getBooleanExtra("OPEN_NOW_PLAYING", false) ?: false)
    }

    DisposableEffect(this) {
        val listener = androidx.core.util.Consumer<Intent> { newIntent ->
            if (newIntent.getBooleanExtra("OPEN_NOW_PLAYING", false)) {
                shouldOpenDetail = true
            }
        }
        addOnNewIntentListener(listener)
        onDispose { removeOnNewIntentListener(listener) }
    }

    LaunchedEffect(shouldOpenDetail, activeAudio) {
        if (shouldOpenDetail && activeAudio != null) {
            navController.navigate(SoundDetailRoute(audioId = activeAudio!!.id)) {
                launchSingleTop = true
            }
            shouldOpenDetail = false
            intent?.removeExtra("OPEN_NOW_PLAYING")
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.SoundList,
    ) {

        composable<SoundListRoute> {
            MainScreen(
                list = listFilter,
                isPlaying = isPlaying,
                isLoading = isLoading,
                activeAudio = activeAudio,
                spaceBottom = innerPadding.calculateBottomPadding(),
                onMenuClick = onMenuClick,
                dominantColor = animatedBackgroundColor,
                currentFilter = currentFilter,
                onFilterChanged = onFilterChanged,
                onAudioClick = onAudioClick,
                timePlaying = sessionTrackDuration,
                onSetFavorite = onSetFavorite,
                menuOptions = onLeftSideBar,
                onTogglePlay = onTogglePlay,
                onClickTimer = onTimerClick,
                onSelectedFavorite = {
                    activeAudio?.let { item ->
                        onSetFavorite(item, !item.isFavorite)
                    }
                },
                onRouteDetail = {
                    activeAudio?.let { item ->
                        navController.navigate(SoundDetailRoute(item.id))
                    }
                }
            )
        }

        composable<SoundDetailRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 350))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(durationMillis = 400, easing = FastOutLinearInEasing)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 350))
            }
        ) {
            DetailScreen(
                itemAudio = activeAudio,
                colors = dominantColor,
                timePlaying = sessionTrackDuration,
                onFavoriteClick = {
                    activeAudio?.let { item ->
                        onSetFavorite(item, !item.isFavorite)
                    }
                },
                onBackClick = { navController.popBackStack() },
                playing = isPlaying,
                onTogglePlay = onTogglePlay,
                onTimerClick = onTimerClick,
                onClickSidebarRight = onSidebarRightClick
            )
        }
        if (BuildConfig.DEBUG) {
            composable <DebugRoute>{
                DebugAdsSimulationPanel()
            }
        }

    }
}

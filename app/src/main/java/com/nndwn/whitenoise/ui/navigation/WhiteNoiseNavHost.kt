package com.nndwn.whitenoise.ui.navigation

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nndwn.whitenoise.BuildConfig
import com.nndwn.whitenoise.ui.features.debug.DebugAdsSimulationPanel
import com.nndwn.whitenoise.ui.features.detail.DetailScreen
import com.nndwn.whitenoise.ui.features.main.MainScreen

@Composable
fun WhiteNoiseNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    activeAudioId : String?
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    var shouldOpenDetail by remember {
        mutableStateOf(activity?.intent?.getBooleanExtra("OPEN_NOW_PLAYING", false) ?: false)
    }

    DisposableEffect(activity) {
        val listener = androidx.core.util.Consumer<Intent> { newIntent ->
            if (newIntent.getBooleanExtra("OPEN_NOW_PLAYING", false)) {
                shouldOpenDetail = true
            }
        }
        activity?.addOnNewIntentListener(listener)
        onDispose { activity?.removeOnNewIntentListener(listener) }
    }

    LaunchedEffect(shouldOpenDetail, activeAudioId) {
        if (shouldOpenDetail && activeAudioId != null) {
            navController.navigate(AppRoute.SoundDetail) {
                launchSingleTop = true
            }
            shouldOpenDetail = false
            activity?.intent?.removeExtra("OPEN_NOW_PLAYING")
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.SoundList,
        modifier = Modifier.padding(innerPadding)
    ) {

        composable<AppRoute.SoundList> {
            MainScreen()
        }

        composable<AppRoute.SoundDetail>(
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
            DetailScreen()
        }
        if (BuildConfig.DEBUG) {
            composable <AppRoute.Debug>{
                DebugAdsSimulationPanel()
            }
        }

    }
}

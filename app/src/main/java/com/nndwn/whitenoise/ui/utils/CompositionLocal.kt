package com.nndwn.whitenoise.ui.utils

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.nndwn.whitenoise.service.TimerTime
import com.nndwn.whitenoise.ui.components.MenuOptions


val LocalIsPremium = compositionLocalOf { false }

val LocalSizeHeight = compositionLocalOf {
    WindowHeightSizeClass.Compact
}

val LocalSizeWidth = compositionLocalOf {
    WindowWidthSizeClass.Compact
}

val LocalToggleSidebar = staticCompositionLocalOf<()-> Unit>{
    error("No ToggleSidebar provided")
}

val LocalMenuOptionHandler = staticCompositionLocalOf<(MenuOptions) -> Unit> {
    error("No MenuOptionHandler provided")
}

val LocalItemTimerHandler = staticCompositionLocalOf<(TimerTime) -> Unit> {
    error("No Timer provided")
}
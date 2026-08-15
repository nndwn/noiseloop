package com.nndwn.whitenoise.ui.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object SoundList : AppRoute

    @Serializable
    data object Debug : AppRoute

    @Serializable
    data object SoundDetail : AppRoute
}

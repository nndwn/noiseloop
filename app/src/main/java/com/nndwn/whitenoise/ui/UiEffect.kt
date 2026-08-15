package com.nndwn.whitenoise.ui

import androidx.annotation.StringRes
import com.nndwn.whitenoise.ui.navigation.AppRoute

sealed interface UiEffect{
    data class ShowToast(@param:StringRes val message : Int) : UiEffect
    data class NavigateTo(val route : AppRoute) : UiEffect
    data class ValidationPlay(val validation : ()-> Unit ) : UiEffect
    object NavigateBack : UiEffect
}
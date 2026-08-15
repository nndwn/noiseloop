package com.nndwn.whitenoise.ui

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiController @Inject constructor() {
    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 64)
    val uiEffect = _uiEffect.asSharedFlow()

    fun sendEffect(effect: UiEffect) {
        _uiEffect.tryEmit(effect)
    }
}
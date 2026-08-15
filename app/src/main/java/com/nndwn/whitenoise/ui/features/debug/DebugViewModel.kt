package com.nndwn.whitenoise.ui.features.debug

import androidx.lifecycle.ViewModel
import com.nndwn.whitenoise.data.local.datastore.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel(){

}
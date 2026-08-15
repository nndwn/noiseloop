package com.nndwn.whitenoise.data.repository

import com.nndwn.whitenoise.data.local.datastore.UserPreferencesManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) {
    val isPremium: Flow<Boolean> = preferencesManager.isPremium
    val lastAudioId: Flow<String?> = preferencesManager.lastAudioId
    val shouldShowAd: Flow<Boolean> = preferencesManager.shouldShowAd

    suspend fun setPremiumStatus(isPremium: Boolean) {
        preferencesManager.setPremiumStatus(isPremium)
    }

    suspend fun saveLastAudioId(id: String) {
        preferencesManager.saveLastAudioId(id)
    }

    suspend fun recordAdShown() {
        preferencesManager.recordAdShown()
    }
    suspend fun recordAdShownIfFirstTime() {
        preferencesManager.recordAdShownIfFirstTime()
    }
}

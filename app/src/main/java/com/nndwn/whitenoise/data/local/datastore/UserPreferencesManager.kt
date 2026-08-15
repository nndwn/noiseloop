package com.nndwn.whitenoise.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nndwn.whitenoise.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
){
    companion object {
        private val LAST_AUDIO_ID_KEY = stringPreferencesKey("last_audio_id")
        private val LAST_AD_SHOWN_TIMESTAMP_KEY = longPreferencesKey("last_ad_shown_timestamp")
        private val IS_PREMIUM_KEY = booleanPreferencesKey("is_premium")
        const val AD_COOLDOWN_MS = 1_800_000L
    }
    val isPremium: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
        preferences[IS_PREMIUM_KEY] ?: false
    }

    val shouldShowAd : Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            val isPremium = preferences[IS_PREMIUM_KEY] ?: false
            if (isPremium) return@map false

            val lastAdTimestamp = preferences[LAST_AD_SHOWN_TIMESTAMP_KEY] ?: return@map false

            val currentTime = System.currentTimeMillis()
            (currentTime - lastAdTimestamp) >= AD_COOLDOWN_MS
        }

    suspend fun setPremiumStatus(isPremium: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PREMIUM_KEY] = isPremium
        }
    }

    suspend fun recordAdShown() {
        dataStore.edit { preferences ->
            preferences[LAST_AD_SHOWN_TIMESTAMP_KEY] = System.currentTimeMillis()
        }
    }

    suspend fun recordAdShownIfFirstTime() {
        dataStore.edit { preferences ->
            if (preferences[LAST_AD_SHOWN_TIMESTAMP_KEY] == null) {
                preferences[LAST_AD_SHOWN_TIMESTAMP_KEY] = System.currentTimeMillis()
            }
        }
    }

    val lastAudioId: Flow<String?> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
        preferences[LAST_AUDIO_ID_KEY]
    }

    suspend fun saveLastAudioId(id: String) {
        dataStore.edit { preferences ->
            preferences[LAST_AUDIO_ID_KEY] = id
        }
    }

    suspend fun debugPremium() {
        if (BuildConfig.DEBUG){
            dataStore.edit { preferences ->
                preferences[IS_PREMIUM_KEY] = true
            }
        }
    }

    suspend fun debugForceShowAd() {
        if (BuildConfig.DEBUG){
            dataStore.edit { preferences ->
                preferences[LAST_AD_SHOWN_TIMESTAMP_KEY] = 0L
            }
        }
    }

    suspend fun reset() {
        if (BuildConfig.DEBUG){
            dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }
}
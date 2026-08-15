package com.nndwn.whitenoise

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.nndwn.whitenoise.ads.AdHelper
import com.nndwn.whitenoise.ads.BillingHelper
import com.nndwn.whitenoise.ads.BillingManager
import com.nndwn.whitenoise.ads.RewardedAdHelper
import com.nndwn.whitenoise.data.local.AppDatabase
import com.nndwn.whitenoise.data.local.dao.AudioDao
import com.nndwn.whitenoise.data.local.datastore.UserPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAdHelper(): AdHelper {
        return RewardedAdHelper()
    }

    @Provides
    @Singleton
    fun provideBillingHelper(billingManager: BillingManager): BillingHelper {
        return billingManager
    }


    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context ) : DataStore<Preferences>{
        return PreferenceDataStoreFactory.create (
            produceFile = {context.preferencesDataStoreFile("app_settings")}
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferencesManager(dataStore : DataStore<Preferences>) : UserPreferencesManager {
        return UserPreferencesManager(dataStore)
    }
    @Provides
    @Singleton
    fun provideDatabase( @ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "music_player_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAudioDao(database: AppDatabase): AudioDao {
        return database.audioDao()
    }
}
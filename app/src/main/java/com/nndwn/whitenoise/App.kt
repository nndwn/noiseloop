package com.nndwn.whitenoise

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.nndwn.whitenoise.ads.BillingHelper
import com.nndwn.whitenoise.data.repository.PreferenceRepository
import com.nndwn.whitenoise.worker.SyncDatabaseWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class WhiteNoiseApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var billingHelper: BillingHelper

    @Inject
    lateinit var prefRepository: PreferenceRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    override fun onCreate() {
        super.onCreate()
        scheduleDatabaseSync()
        billingHelper.startConnection(
            setPurchased = { isPremium ->
                applicationScope.launch {
                    prefRepository.setPremiumStatus(isPremium)
                }
            },
            billingDisconnected = {
                // Handle disconnection
            }
        )
    }




    private fun scheduleDatabaseSync() {
        try {
            val packageInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            
            val versionCode = packageInfo.longVersionCode
            val uniqueWorkName = "SyncDatabaseWork_v$versionCode"
            
            val syncRequest = OneTimeWorkRequestBuilder<SyncDatabaseWorker>()
                .build()
                
            WorkManager.getInstance(this).enqueueUniqueWork(
                uniqueWorkName,
                ExistingWorkPolicy.KEEP,
                syncRequest
            )
        } catch (e: Exception) {
            android.util.Log.e("WhiteNoiseApp", "Failed to schedule sync", e)
        }
    }
}


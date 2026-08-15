package com.nndwn.whitenoise.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nndwn.whitenoise.data.repository.AudioRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncDatabaseWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val audioRepository: AudioRepository
): CoroutineWorker(appContext, workerParams){


    override suspend fun doWork(): Result {
        return try {
            audioRepository.syncDatabaseWithRepo()
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("SyncWorker", "Error syncing database", e)
            Result.retry()
        }
    }
}
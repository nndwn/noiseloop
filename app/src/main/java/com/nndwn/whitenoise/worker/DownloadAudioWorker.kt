package com.nndwn.whitenoise.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nndwn.whitenoise.data.repository.AudioRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DownloadAudioWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: AudioRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val audioId = inputData.getString(KEY_AUDIO_ID) ?: return Result.failure()
        
        return try {
            val audio = repository.getAudioData(audioId)
            if (audio != null) {
                repository.downloadAndSaveAudio(context, audio)
                Result.success()
            } else {
                Result.failure()
            }
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_AUDIO_ID = "key_audio_id"
    }
}

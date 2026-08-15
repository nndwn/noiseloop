package com.nndwn.whitenoise.data.repository

import android.content.Context
import com.nndwn.whitenoise.data.local.InitialAudioData
import com.nndwn.whitenoise.data.local.dao.AudioDao
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.LabelAudio
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val audioDao: AudioDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
){
    fun getAllAudio(): Flow<List<DataAudio>> = audioDao.getAllAudio()

    fun getAudioFlowById(id: String): Flow<DataAudio?> = audioDao.getAudioFlowById(id)

    suspend  fun getAudioData(id: String) : DataAudio? = audioDao.getAudioById(id)

    suspend fun syncDatabaseWithRepo() {
        withContext(ioDispatcher) {
            val existingSongsMap = audioDao.getCurrentDatabaseSongs().associateBy { it.id }

            val finalizedList = InitialAudioData.audioList.map { repoAudio ->
                val userAudio = existingSongsMap[repoAudio.id]
                syncAudioItem(repoAudio, userAudio)
            }
            audioDao.syncDatabaseWithRepo(finalizedList)
        }
    }

    private fun syncAudioItem(repoAudio: DataAudio, userAudio: DataAudio?): DataAudio {
        if (userAudio == null) return repoAudio

        val (finalSourcePath, finalLabel) = resolveSourcePath(repoAudio, userAudio)
        val hasCoverChanged = repoAudio.cover != userAudio.cover

        return repoAudio.copy(
            isFavorite = userAudio.isFavorite,
            sourcePath = finalSourcePath,
            label = finalLabel,
            isColor = hasCoverChanged,
            colorPrimary = userAudio.colorPrimary,
            colorSecondary = userAudio.colorSecondary
        )
    }

    private fun resolveSourcePath(repoAudio: DataAudio, userAudio: DataAudio): Pair<String, LabelAudio> {
        val wasDownloaded = repoAudio.label == LabelAudio.ONLINE && userAudio.label == LabelAudio.OFFLINE
        
        return if (wasDownloaded && File(userAudio.sourcePath).exists()) {
            userAudio.sourcePath to LabelAudio.OFFLINE
        } else {
            repoAudio.sourcePath to repoAudio.label
        }
    }

    suspend fun updateFavorite(id: String, isFavorite: Boolean) {
        audioDao.updateFavoriteStatus(id, isFavorite)
    }

    suspend fun updateAudioColors(id: String, primary: Long, secondary: Long) {
        audioDao.updateAudioColors(id, primary, secondary)
    }
    suspend fun updateAudioSourceAndLabel(id: String, newPath: String, newLabel: LabelAudio) {
        audioDao.updateAudioSourceAndLabel(id, newPath, newLabel)
    }

    suspend fun downloadAndSaveAudio(context: Context, audio: DataAudio) {
        withContext(ioDispatcher) {
            val fileName = audio.name.replace(" ", "_").lowercase() + ".mp3"
            val file = File(context.filesDir, fileName)

            if (!file.exists()) {
                try {
                    URL(audio.sourcePath).openStream().use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    updateAudioSourceAndLabel(audio.id, file.absolutePath, LabelAudio.OFFLINE)
                } catch (e: Exception) {
                    if (file.exists()) {
                        val deleted = file.delete()
                        if (!deleted) {
                            android.util.Log.w("AudioRepository", "Could not delete partial file: ${file.absolutePath}")
                        }
                    }
                    android.util.Log.e("AudioRepository", "Download failed: ${e.message}")
                }
            } else {
                updateAudioSourceAndLabel(audio.id, file.absolutePath, LabelAudio.OFFLINE)
            }
        }
    }
}
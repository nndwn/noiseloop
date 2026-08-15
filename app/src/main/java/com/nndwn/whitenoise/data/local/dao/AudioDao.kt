package com.nndwn.whitenoise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.LabelAudio
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {
    @Query("SELECT * FROM audio_table")
    fun getAllAudio() : Flow<List<DataAudio>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(audioList: List<DataAudio>)

    @Query("DELETE FROM audio_table WHERE id NOT IN (:currentIds)")
    suspend fun deleteOrphanedAudio(currentIds: List<String>)

    @Query("SELECT * FROM audio_table")
    suspend fun getCurrentDatabaseSongs(): List<DataAudio>

    @Transaction
    suspend fun syncDatabaseWithRepo(finalizedList: List<DataAudio>){
        val currentIds = finalizedList.map { it.id }
        deleteOrphanedAudio(currentIds)
        insertAll(finalizedList)
    }

    @Query("UPDATE audio_table SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFav: Boolean)

    @Query("UPDATE audio_table SET sourcePath = :newPath, label = :newLabel WHERE id = :id")
    suspend fun updateAudioSourceAndLabel(id: String, newPath: String, newLabel: LabelAudio)

    @Query("UPDATE audio_table SET isColor= 1, colorPrimary = :primary, colorSecondary = :secondary WHERE id = :id")
    suspend fun updateAudioColors(id: String, primary: Long, secondary: Long)

    @Query("SELECT * FROM audio_table WHERE id = :id LIMIT 1")
    suspend fun getAudioById(id: String): DataAudio?

    @Query("SELECT * FROM audio_table WHERE id = :id LIMIT 1")
    fun getAudioFlowById(id: String): Flow<DataAudio?>

}
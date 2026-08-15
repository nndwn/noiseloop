package com.nndwn.whitenoise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nndwn.whitenoise.data.local.dao.AudioDao
import com.nndwn.whitenoise.data.local.entity.AudioLabelConverter
import com.nndwn.whitenoise.data.local.entity.AudioTypeConverter
import com.nndwn.whitenoise.data.local.entity.DataAudio

@Database(entities =  [DataAudio::class], version = 1, exportSchema = false)
@TypeConverters(AudioTypeConverter::class, AudioLabelConverter::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun audioDao() : AudioDao
}
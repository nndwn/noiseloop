package com.nndwn.whitenoise.data.local.entity

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.nndwn.whitenoise.ui.theme.CharcoalDarkGray
import com.nndwn.whitenoise.ui.theme.MediumDarkGray
import com.nndwn.whitenoise.ui.theme.toArgbLong


@Entity(tableName = "audio_table")
data class DataAudio(
    @PrimaryKey
    val id: String,
    val name : String,
    val type : TypeAudio,
    val sourcePath : String,
    @field:DrawableRes val cover : Int,
    val label : LabelAudio,
    val isFavorite: Boolean = false,
    val isColor : Boolean = false,
    val colorPrimary: Long = MediumDarkGray.toArgbLong(),
    val colorSecondary: Long = CharcoalDarkGray.toArgbLong()
)



class AudioLabelConverter {
    @TypeConverter
    fun fromLabelAudio (label : LabelAudio) : String {
        return  label.name
    }

    @TypeConverter
    fun toLabelAudio(value : String) : LabelAudio {
        return runCatching { LabelAudio.valueOf(value) }.getOrDefault(LabelAudio.OFFLINE)
    }
}
class AudioTypeConverter {
    @TypeConverter
    fun fromTypeAudio(type: TypeAudio) : String {
        return type.name
    }
    @TypeConverter
    fun toTypeAudio(value : String) : TypeAudio {
        return runCatching { TypeAudio.valueOf(value) }.getOrDefault(TypeAudio.NATURE)
    }
}

enum class TypeAudio {
    NATURE, OBJECT, ANIMAL, PlACE
}

enum class LabelAudio {
    ONLINE, OFFLINE
}






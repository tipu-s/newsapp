package com.androiddevs.news.db

import androidx.room.TypeConverter
import com.androiddevs.news.model.Source

class Converters {
    @TypeConverter
    fun fromSource(source: Source): String {
        return source.Name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}
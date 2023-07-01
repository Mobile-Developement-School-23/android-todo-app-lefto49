package com.todoapplication.data.room

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun toDate(time: Long?): Date? {
        if (time == null) {
            return null
        }
        return Date(time)
    }
}
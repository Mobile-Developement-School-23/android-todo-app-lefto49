package com.todoapplication.data.room

import androidx.room.TypeConverter
import java.util.*

/**
 * Is used for converting Date to long and vice versa for storing it in the database.
 */
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
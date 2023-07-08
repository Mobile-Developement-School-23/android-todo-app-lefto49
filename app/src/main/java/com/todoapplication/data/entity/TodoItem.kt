package com.todoapplication.data.entity

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.todoapplication.data.room.DateConverter
import java.util.Date

/**
 * Class for storing the information about a TodoItem
 */
@Entity(tableName = "todoitems")
data class TodoItem(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: String,

    @ColumnInfo(name = "task")
    @SerializedName("text")
    var task: String,

    @ColumnInfo(name = "importance")
    @SerializedName("importance")
    var importance: Importance,

    @ColumnInfo(name = "deadline")
    @SerializedName("deadline")
    @TypeConverters(DateConverter::class)
    var deadline: Date?,

    @ColumnInfo(name = "is_done")
    @SerializedName("done")
    var isDone: Boolean,

    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    @TypeConverters(DateConverter::class)
    var createdAt: Date,

    @ColumnInfo(name = "edited_at")
    @SerializedName("changed_at")
    @TypeConverters(DateConverter::class)
    var editedAt: Date
)

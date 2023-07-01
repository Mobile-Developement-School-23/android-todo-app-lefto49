package com.todoapplication.data.network.interaction

import com.google.gson.annotations.SerializedName
import com.todoapplication.data.entity.Importance
import java.util.*

data class RemoteTodoItem(
    @SerializedName("id")
    var id: String,

    @SerializedName("text")
    var task: String,

    @SerializedName("importance")
    var importance: Importance,

    @SerializedName("deadline")
    var deadline: Long? = null,

    @SerializedName("done")
    var isDone: Boolean,

    @SerializedName("color")
    var color: String?,

    @SerializedName("created_at")
    var createdAt: Long,

    @SerializedName("changed_at")
    var editedAt: Long,

    @SerializedName("last_updated_by")
    var lastUpdatedBy: String
)
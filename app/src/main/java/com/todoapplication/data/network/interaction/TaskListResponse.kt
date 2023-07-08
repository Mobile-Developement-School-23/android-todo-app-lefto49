package com.todoapplication.data.network.interaction

import com.google.gson.annotations.SerializedName

/**
 * Is used for deserializing the response with the list of tasks from the remote data source.
 */
data class TaskListResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("list")
    val tasks: List<RemoteTodoItem>,

    @SerializedName("revision")
    val revision: Int
)
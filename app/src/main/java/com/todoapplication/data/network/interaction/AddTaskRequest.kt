package com.todoapplication.data.network.interaction

import com.google.gson.annotations.SerializedName

/**
 * Is used for serializing the request to add a task to remote data source.
 */
data class AddTaskRequest(
    @SerializedName("element")
    val task: RemoteTodoItem
)
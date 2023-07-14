package com.todoapplication.data.network.interaction

import com.google.gson.annotations.SerializedName

/**
 * Is used for serializing the request to update a task stored remotely.
 */
data class UpdateListRequest(
    @SerializedName("list")
    val tasks: List<RemoteTodoItem>
)
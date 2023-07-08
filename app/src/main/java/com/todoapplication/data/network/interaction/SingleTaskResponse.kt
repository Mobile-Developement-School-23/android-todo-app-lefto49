package com.todoapplication.data.network.interaction

import com.google.gson.annotations.SerializedName

/**
 * Is used for deserializing the response with a single task from the remote data source.
 */
data class SingleTaskResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("element")
    val task: RemoteTodoItem,

    @SerializedName("revision")
    val revision: Int
)
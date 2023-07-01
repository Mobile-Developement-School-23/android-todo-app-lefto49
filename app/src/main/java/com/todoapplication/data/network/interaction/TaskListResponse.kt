package com.todoapplication.data.network.interaction

import com.google.gson.annotations.SerializedName

data class TaskListResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("list")
    val tasks: List<RemoteTodoItem>,

    @SerializedName("revision")
    val revision: Int
)
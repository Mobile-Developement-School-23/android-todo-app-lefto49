package com.todoapplication.data.network.interaction

import com.google.gson.annotations.SerializedName

data class SingleTaskResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("element")
    val task: RemoteTodoItem,

    @SerializedName("revision")
    val revision: Int
)
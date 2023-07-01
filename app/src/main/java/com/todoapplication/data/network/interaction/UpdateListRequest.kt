package com.todoapplication.data.network.interaction

import com.google.gson.annotations.SerializedName

data class UpdateListRequest(
    @SerializedName("list")
    val tasks: List<RemoteTodoItem>
)
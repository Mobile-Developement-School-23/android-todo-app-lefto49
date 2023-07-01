package com.todoapplication.data.network.interaction

import com.google.gson.annotations.SerializedName

data class AddTaskRequest(
    @SerializedName("element")
    val task: RemoteTodoItem
)
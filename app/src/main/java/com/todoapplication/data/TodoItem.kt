package com.todoapplication.data

import java.util.Date

data class TodoItem(
    var id: String,
    var task: String,
    var importance: Importance,
    var deadline: Date?,
    var isDone: Boolean,
    var createdAt: Date,
    var editedAt: Date
)

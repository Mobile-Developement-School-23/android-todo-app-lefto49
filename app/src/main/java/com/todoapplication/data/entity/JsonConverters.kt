package com.todoapplication.data.entity

import android.util.Log
import com.todoapplication.data.network.interaction.RemoteTodoItem
import java.util.*

class JsonConverters {
    companion object {
        fun fromRemote(remote: RemoteTodoItem): TodoItem {
            val task = TodoItem(
                remote.id,
                remote.task,
                remote.importance,
                null,
                remote.isDone,
                Date(remote.createdAt),
                Date(remote.editedAt)
            )

            if (remote.deadline != null) {
                task.deadline = Date(remote.deadline!!)
                Log.println(Log.ERROR, "REMOTE", "${remote.task} ${remote.deadline.toString()}")
            }

            return task
        }

        fun toRemote(local: TodoItem): RemoteTodoItem = RemoteTodoItem(
            local.id,
            local.task,
            local.importance,
            local.deadline?.time,
            local.isDone,
            null,
            local.createdAt.time,
            local.editedAt.time,
            "MAIN"
        )
    }
}

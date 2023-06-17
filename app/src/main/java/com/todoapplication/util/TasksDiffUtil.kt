package com.todoapplication.util

import androidx.recyclerview.widget.DiffUtil
import com.todoapplication.data.TodoItem

class TasksDiffUtil(private val oldList: List<TodoItem>, private val newList: List<TodoItem>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id.equals(newList[newItemPosition].id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        val firstHalf =
            oldItem.id.equals(newItem.id) and oldItem.isDone.equals(newItem.isDone) and oldItem.task.equals(
                newItem.task
            )
        val secondHalf =
            (oldItem.deadline == newItem.deadline) and (oldItem.createdAt == newItem.createdAt
                    ) and (oldItem.editedAt == newItem.editedAt) and (oldItem.importance == newItem.importance)
        return firstHalf and secondHalf
    }
}
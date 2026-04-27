package com.tailytask.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "PERSONAL",
    val priority: String = "MEDIUM",
    val dueDate: Long? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val points: Int = 20,
    val sortOrder: Int = 0
)

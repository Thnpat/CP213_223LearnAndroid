package com.tailytask.app.data.repository

import com.tailytask.app.data.local.TaskDao
import com.tailytask.app.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getPendingTasks(): Flow<List<TaskEntity>> = taskDao.getPendingTasks()

    fun getCompletedTasks(): Flow<List<TaskEntity>> = taskDao.getCompletedTasks()

    fun getTasksByCategory(category: String): Flow<List<TaskEntity>> =
        taskDao.getTasksByCategory(category)

    fun getTasksForDate(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksForDate(startOfDay, endOfDay)

    fun getTasksForDateRange(startMs: Long, endMs: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksForDateRange(startMs, endMs)

    fun getTotalTaskCount(): Flow<Int> = taskDao.getTotalTaskCount()

    fun getCompletedTaskCount(): Flow<Int> = taskDao.getCompletedTaskCount()

    fun getPendingTaskCount(): Flow<Int> = taskDao.getPendingTaskCount()

    suspend fun getTaskById(taskId: Long): TaskEntity? = taskDao.getTaskById(taskId)

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun completeTask(task: TaskEntity): Int {
        val updatedTask = task.copy(isCompleted = true)
        taskDao.updateTask(updatedTask)
        return task.points
    }

    suspend fun uncompleteTask(task: TaskEntity) {
        val updatedTask = task.copy(isCompleted = false)
        taskDao.updateTask(updatedTask)
    }
}

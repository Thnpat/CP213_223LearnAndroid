package com.tailytask.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tailytask.app.ai.GeminiService
import com.tailytask.app.data.local.AppDatabase
import com.tailytask.app.data.local.TaskEntity
import com.tailytask.app.data.repository.TaskRepository
import com.tailytask.app.data.repository.UserPrefsRepository
import com.tailytask.app.model.Priority
import com.tailytask.app.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = TaskRepository(database.taskDao())
    private val userPrefs = UserPrefsRepository(application)
    private val geminiService = GeminiService()

    // ===== Task Lists =====
    val allTasks = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingTasks = repository.getPendingTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val completedTasks = repository.getCompletedTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ===== Counts =====
    val totalCount = repository.getTotalTaskCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val completedCount = repository.getCompletedTaskCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val pendingCount = repository.getPendingTaskCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // ===== Selected Category Filter =====
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // ===== Selected Date for Calendar =====
    private val _selectedDateTasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val selectedDateTasks: StateFlow<List<TaskEntity>> = _selectedDateTasks.asStateFlow()

    // ===== Fast Record Result Message =====
    private val _fastRecordMessage = MutableStateFlow<String?>(null)
    val fastRecordMessage: StateFlow<String?> = _fastRecordMessage.asStateFlow()

    // ===== Points =====
    private val _points = MutableStateFlow(userPrefs.getTotalPoints())
    val points: StateFlow<Int> = _points.asStateFlow()

    // ===== CRUD Operations =====
    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            val id = repository.insertTask(task)
            task.dueDate?.let { dueDate ->
                NotificationHelper.scheduleTaskReminder(
                    getApplication(),
                    id,
                    task.title,
                    dueDate,
                    30
                )
            }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
            NotificationHelper.cancelTaskReminder(getApplication(), task.id)
            if (!task.isCompleted) {
                task.dueDate?.let { dueDate ->
                    NotificationHelper.scheduleTaskReminder(
                        getApplication(),
                        task.id,
                        task.title,
                        dueDate,
                        30
                    )
                }
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            NotificationHelper.cancelTaskReminder(getApplication(), task.id)
        }
    }

    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            if (!task.isCompleted) {
                val pointsEarned = repository.completeTask(task)
                userPrefs.addPoints(pointsEarned)
                _points.value = userPrefs.getTotalPoints()
            }
        }
    }

    fun uncompleteTask(task: TaskEntity) {
        viewModelScope.launch {
            if (task.isCompleted) {
                userPrefs.addPoints(-task.points)
                _points.value = userPrefs.getTotalPoints()
                repository.uncompleteTask(task)
            }
        }
    }

    fun toggleTask(task: TaskEntity) {
        if (task.isCompleted) {
            uncompleteTask(task)
        } else {
            completeTask(task)
        }
    }

    // ===== Category Filter =====
    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    // ===== Calendar - Load Tasks for Date =====
    fun loadTasksForDate(startOfDay: Long, endOfDay: Long) {
        viewModelScope.launch {
            repository.getTasksForDate(startOfDay, endOfDay).collect { tasks ->
                _selectedDateTasks.value = tasks
            }
        }
    }

    // ===== AI Fast Record =====
    fun fastRecord(input: String) {
        viewModelScope.launch {
            try {
                val parsed = geminiService.parseNaturalLanguage(input)
                val priority = try {
                    Priority.valueOf(parsed.priority)
                } catch (e: Exception) {
                    Priority.MEDIUM
                }

                val task = TaskEntity(
                    title = parsed.title,
                    description = parsed.description,
                    category = parsed.category,
                    priority = parsed.priority,
                    dueDate = parsed.dueDate,
                    startTime = parsed.startTime,
                    endTime = parsed.endTime,
                    points = priority.points
                )
                val id = repository.insertTask(task)
                task.dueDate?.let { dueDate ->
                    NotificationHelper.scheduleTaskReminder(
                        getApplication(),
                        id,
                        task.title,
                        dueDate,
                        30
                    )
                }
                _fastRecordMessage.value = "✅ เพิ่มงาน \"${parsed.title}\" สำเร็จ!"
            } catch (e: Exception) {
                _fastRecordMessage.value = "❌ ไม่สามารถวิเคราะห์ข้อความได้: ${e.message}"
            }
        }
    }

    fun clearFastRecordMessage() {
        _fastRecordMessage.value = null
    }
}

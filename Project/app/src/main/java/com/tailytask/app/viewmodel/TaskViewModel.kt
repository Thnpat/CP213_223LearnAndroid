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
import java.util.Calendar

data class WeeklyAnalytics(
    val dayLabels: List<String> = emptyList(),
    val completedCounts: List<Int> = emptyList(),
    val totalCounts: List<Int> = emptyList()
)

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

    val overdueTasks = repository.getOverdueTasks()
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

    // ===== Analytics =====
    private val _weeklyAnalytics = MutableStateFlow(WeeklyAnalytics())
    val weeklyAnalytics: StateFlow<WeeklyAnalytics> = _weeklyAnalytics.asStateFlow()

    init {
        loadWeeklyAnalytics()
    }

    // ===== CRUD Operations =====
    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            val id = repository.insertTask(task)
            task.dueDate?.let { dueDate ->
                NotificationHelper.scheduleTaskReminder(
                    getApplication(), id, task.title, dueDate, 30
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
                        getApplication(), task.id, task.title, dueDate, 30
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
                loadWeeklyAnalytics()
            }
        }
    }

    fun uncompleteTask(task: TaskEntity) {
        viewModelScope.launch {
            if (task.isCompleted) {
                userPrefs.addPoints(-task.points)
                _points.value = userPrefs.getTotalPoints()
                repository.uncompleteTask(task)
                loadWeeklyAnalytics()
            }
        }
    }

    fun toggleTask(task: TaskEntity) {
        if (task.isCompleted) uncompleteTask(task) else completeTask(task)
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

    // ===== Update task date (for drag & drop) =====
    fun updateTaskDate(task: TaskEntity, newDueDate: Long) {
        viewModelScope.launch {
            repository.updateTask(task.copy(dueDate = newDueDate))
        }
    }

    // ===== AI Fast Record =====
    fun fastRecord(input: String, overridePriority: String? = null) {
        viewModelScope.launch {
            try {
                val parsed = geminiService.parseNaturalLanguage(input)
                val finalPriority = overridePriority ?: parsed.priority
                val priority = try {
                    Priority.valueOf(finalPriority)
                } catch (e: Exception) {
                    Priority.MEDIUM
                }

                val task = TaskEntity(
                    title = parsed.title,
                    description = parsed.description,
                    category = parsed.category,
                    priority = finalPriority,
                    dueDate = parsed.dueDate,
                    startTime = parsed.startTime,
                    endTime = parsed.endTime,
                    points = priority.points
                )
                val id = repository.insertTask(task)
                task.dueDate?.let { dueDate ->
                    NotificationHelper.scheduleTaskReminder(
                        getApplication(), id, task.title, dueDate, 30
                    )
                }
                _fastRecordMessage.value = "✅ \"${parsed.title}\" added to ${parsed.category}"
            } catch (e: Exception) {
                _fastRecordMessage.value = "ไม่สามารถวิเคราะห์ข้อความได้: ${e.message}"
            }
        }
    }

    fun clearFastRecordMessage() {
        _fastRecordMessage.value = null
    }

    // ===== Analytics =====
    fun loadWeeklyAnalytics() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val dayLabels = mutableListOf<String>()
            val completed = mutableListOf<Int>()
            val totals = mutableListOf<Int>()
            val dayNames = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

            // Go back 6 days from today
            cal.add(Calendar.DAY_OF_YEAR, -6)
            for (i in 0..6) {
                val startOfDay = cal.clone() as Calendar
                startOfDay.set(Calendar.HOUR_OF_DAY, 0)
                startOfDay.set(Calendar.MINUTE, 0)
                startOfDay.set(Calendar.SECOND, 0)
                startOfDay.set(Calendar.MILLISECOND, 0)

                val endOfDay = cal.clone() as Calendar
                endOfDay.set(Calendar.HOUR_OF_DAY, 23)
                endOfDay.set(Calendar.MINUTE, 59)
                endOfDay.set(Calendar.SECOND, 59)
                endOfDay.set(Calendar.MILLISECOND, 999)

                dayLabels.add(dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1])
                completed.add(repository.getCompletedCountInRange(startOfDay.timeInMillis, endOfDay.timeInMillis))
                totals.add(repository.getTotalCountInRange(startOfDay.timeInMillis, endOfDay.timeInMillis))

                cal.add(Calendar.DAY_OF_YEAR, 1)
            }

            _weeklyAnalytics.value = WeeklyAnalytics(dayLabels, completed, totals)
        }
    }

    fun refreshPoints() {
        _points.value = userPrefs.getTotalPoints()
    }
}

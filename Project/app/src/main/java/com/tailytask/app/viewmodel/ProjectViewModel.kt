package com.tailytask.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tailytask.app.data.local.AppDatabase
import com.tailytask.app.data.local.ProjectEntity
import com.tailytask.app.data.local.SubtaskEntity
import com.tailytask.app.data.repository.ProjectRepository
import com.tailytask.app.data.repository.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ProjectRepository(database.projectDao())
    private val userPrefs = UserPrefsRepository(application)

    val allProjects = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeProjects = repository.getActiveProjects()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalProjectCount = repository.getTotalProjectCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val activeProjectCount = repository.getActiveProjectCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // ===== Selected project detail =====
    private val _selectedProject = MutableStateFlow<ProjectEntity?>(null)
    val selectedProject: StateFlow<ProjectEntity?> = _selectedProject.asStateFlow()

    private val _subtasks = MutableStateFlow<List<SubtaskEntity>>(emptyList())
    val subtasks: StateFlow<List<SubtaskEntity>> = _subtasks.asStateFlow()

    private val _subtaskCount = MutableStateFlow(0)
    val subtaskCount: StateFlow<Int> = _subtaskCount.asStateFlow()

    private val _completedSubtaskCount = MutableStateFlow(0)
    val completedSubtaskCount: StateFlow<Int> = _completedSubtaskCount.asStateFlow()

    // ===== Flow accessors for project list (used by ProjectCard) =====
    fun getSubtaskCountFlow(projectId: Long): Flow<Int> =
        repository.getSubtaskCount(projectId)

    fun getCompletedSubtaskCountFlow(projectId: Long): Flow<Int> =
        repository.getCompletedSubtaskCount(projectId)

    // ===== Project CRUD =====
    fun addProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.insertProject(project)
        }
    }

    fun updateProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.updateProject(project)
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }

    fun completeProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.updateProject(project.copy(isCompleted = true))
        }
    }

    // ===== Load project detail =====
    fun loadProject(projectId: Long) {
        viewModelScope.launch {
            val project = repository.getProjectById(projectId)
            _selectedProject.value = project
        }
        viewModelScope.launch {
            repository.getSubtasksForProject(projectId).collect { list ->
                _subtasks.value = list
            }
        }
        viewModelScope.launch {
            repository.getSubtaskCount(projectId).collect { count ->
                _subtaskCount.value = count
            }
        }
        viewModelScope.launch {
            repository.getCompletedSubtaskCount(projectId).collect { count ->
                _completedSubtaskCount.value = count
            }
        }
    }

    // ===== Subtask CRUD =====
    fun addSubtask(subtask: SubtaskEntity) {
        viewModelScope.launch {
            repository.insertSubtask(subtask)
        }
    }

    fun toggleSubtask(subtask: SubtaskEntity) {
        viewModelScope.launch {
            if (subtask.isCompleted) {
                userPrefs.addPoints(-subtask.points)
                repository.uncompleteSubtask(subtask)
            } else {
                val pts = repository.completeSubtask(subtask)
                userPrefs.addPoints(pts)
            }
        }
    }

    fun deleteSubtask(subtask: SubtaskEntity) {
        viewModelScope.launch {
            repository.deleteSubtask(subtask)
        }
    }

    // ===== Calendar: get projects in range =====
    fun getProjectsInRange(startMs: Long, endMs: Long) =
        repository.getProjectsInRange(startMs, endMs)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}

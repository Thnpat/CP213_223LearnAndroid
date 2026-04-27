package com.tailytask.app.data.repository

import com.tailytask.app.data.local.ProjectDao
import com.tailytask.app.data.local.ProjectEntity
import com.tailytask.app.data.local.SubtaskEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {

    fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getActiveProjects(): Flow<List<ProjectEntity>> = projectDao.getActiveProjects()

    fun getCompletedProjects(): Flow<List<ProjectEntity>> = projectDao.getCompletedProjects()

    fun getProjectsInRange(startMs: Long, endMs: Long): Flow<List<ProjectEntity>> =
        projectDao.getProjectsInRange(startMs, endMs)

    fun getTotalProjectCount(): Flow<Int> = projectDao.getTotalProjectCount()

    fun getActiveProjectCount(): Flow<Int> = projectDao.getActiveProjectCount()

    suspend fun getProjectById(id: Long): ProjectEntity? = projectDao.getProjectById(id)

    suspend fun insertProject(project: ProjectEntity): Long = projectDao.insertProject(project)

    suspend fun updateProject(project: ProjectEntity) = projectDao.updateProject(project)

    suspend fun deleteProject(project: ProjectEntity) = projectDao.deleteProject(project)

    // ===== Subtasks =====
    fun getSubtasksForProject(projectId: Long): Flow<List<SubtaskEntity>> =
        projectDao.getSubtasksForProject(projectId)

    fun getSubtaskCount(projectId: Long): Flow<Int> =
        projectDao.getSubtaskCount(projectId)

    fun getCompletedSubtaskCount(projectId: Long): Flow<Int> =
        projectDao.getCompletedSubtaskCount(projectId)

    suspend fun insertSubtask(subtask: SubtaskEntity): Long = projectDao.insertSubtask(subtask)

    suspend fun updateSubtask(subtask: SubtaskEntity) = projectDao.updateSubtask(subtask)

    suspend fun deleteSubtask(subtask: SubtaskEntity) = projectDao.deleteSubtask(subtask)

    suspend fun completeSubtask(subtask: SubtaskEntity): Int {
        val updated = subtask.copy(isCompleted = true)
        projectDao.updateSubtask(updated)
        return subtask.points
    }

    suspend fun uncompleteSubtask(subtask: SubtaskEntity) {
        val updated = subtask.copy(isCompleted = false)
        projectDao.updateSubtask(updated)
    }

    // Export
    suspend fun getAllProjectsSync(): List<ProjectEntity> = projectDao.getAllProjectsSync()
    suspend fun getAllSubtasksSync(): List<SubtaskEntity> = projectDao.getAllSubtasksSync()
}

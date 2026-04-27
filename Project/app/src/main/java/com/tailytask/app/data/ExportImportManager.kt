package com.tailytask.app.data

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tailytask.app.data.local.AppDatabase
import com.tailytask.app.data.local.ProjectEntity
import com.tailytask.app.data.local.SubtaskEntity
import com.tailytask.app.data.local.TaskEntity

data class ExportData(
    val version: Int = 1,
    val exportDate: Long = System.currentTimeMillis(),
    val tasks: List<TaskEntity>,
    val projects: List<ProjectEntity>,
    val subtasks: List<SubtaskEntity>
)

class ExportImportManager(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val database = AppDatabase.getDatabase(context)

    suspend fun exportToJson(): String {
        val tasks = database.taskDao().getAllTasksSync()
        val projects = database.projectDao().getAllProjectsSync()
        val subtasks = database.projectDao().getAllSubtasksSync()

        val data = ExportData(
            tasks = tasks,
            projects = projects,
            subtasks = subtasks
        )
        return gson.toJson(data)
    }

    suspend fun exportToUri(uri: Uri): Boolean {
        return try {
            val json = exportToJson()
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(json.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun importFromUri(uri: Uri): Boolean {
        return try {
            val json = context.contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader().readText()
            } ?: return false

            importFromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun importFromJson(json: String): Boolean {
        return try {
            val data = gson.fromJson(json, ExportData::class.java)

            // Insert tasks (reset IDs to avoid conflicts)
            data.tasks.forEach { task ->
                database.taskDao().insertTask(task.copy(id = 0))
            }

            // Insert projects and subtasks
            data.projects.forEach { project ->
                val oldId = project.id
                val newId = database.projectDao().insertProject(project.copy(id = 0))
                // Update subtask references
                data.subtasks.filter { it.projectId == oldId }.forEach { subtask ->
                    database.projectDao().insertSubtask(subtask.copy(id = 0, projectId = newId))
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

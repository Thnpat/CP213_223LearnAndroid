package com.tailytask.app.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

// ===== Task DAO =====
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY sortOrder ASC, dueDate ASC, createdAt DESC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY dueDate DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY isCompleted ASC, dueDate ASC")
    fun getTasksByCategory(category: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startOfDay AND :endOfDay ORDER BY isCompleted ASC, priority DESC")
    fun getTasksForDate(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startMs AND :endMs ORDER BY dueDate ASC")
    fun getTasksForDateRange(startMs: Long, endMs: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getPendingTaskCount(): Flow<Int>

    // Analytics queries
    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1 AND createdAt BETWEEN :startMs AND :endMs")
    suspend fun getCompletedCountInRange(startMs: Long, endMs: Long): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE createdAt BETWEEN :startMs AND :endMs")
    suspend fun getTotalCountInRange(startMs: Long, endMs: Long): Int

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND dueDate < :nowMs AND dueDate IS NOT NULL")
    fun getOverdueTasks(nowMs: Long): Flow<List<TaskEntity>>

    // Export: get all tasks synchronously
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksSync(): List<TaskEntity>
}

// ===== Project DAO =====
@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY isCompleted ASC, deadline ASC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isCompleted = 0 ORDER BY deadline ASC")
    fun getActiveProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isCompleted = 1 ORDER BY deadline DESC")
    fun getCompletedProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Query("SELECT * FROM projects WHERE (startDate <= :endMs AND deadline >= :startMs)")
    fun getProjectsInRange(startMs: Long, endMs: Long): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("SELECT COUNT(*) FROM projects")
    fun getTotalProjectCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM projects WHERE isCompleted = 0")
    fun getActiveProjectCount(): Flow<Int>

    // ===== Subtask queries =====
    @Query("SELECT * FROM subtasks WHERE projectId = :projectId ORDER BY isCompleted ASC, deadline ASC")
    fun getSubtasksForProject(projectId: Long): Flow<List<SubtaskEntity>>

    @Query("SELECT COUNT(*) FROM subtasks WHERE projectId = :projectId")
    fun getSubtaskCount(projectId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM subtasks WHERE projectId = :projectId AND isCompleted = 1")
    fun getCompletedSubtaskCount(projectId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubtaskEntity): Long

    @Update
    suspend fun updateSubtask(subtask: SubtaskEntity)

    @Delete
    suspend fun deleteSubtask(subtask: SubtaskEntity)

    // Export
    @Query("SELECT * FROM projects")
    suspend fun getAllProjectsSync(): List<ProjectEntity>

    @Query("SELECT * FROM subtasks")
    suspend fun getAllSubtasksSync(): List<SubtaskEntity>
}

// ===== Database =====
@Database(
    entities = [TaskEntity::class, ProjectEntity::class, SubtaskEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tailytask_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

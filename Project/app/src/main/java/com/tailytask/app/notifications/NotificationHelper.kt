package com.tailytask.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

object NotificationHelper {

    private const val CHANNEL_ID = "tailytask_reminders"
    private const val CHANNEL_NAME = "Task Reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "แจ้งเตือนเมื่อใกล้ถึง Deadline ของงาน"
                enableVibration(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun scheduleTaskReminder(
        context: Context,
        taskId: Long,
        taskTitle: String,
        dueDateMillis: Long,
        reminderMinutesBefore: Long = 30
    ) {
        val now = System.currentTimeMillis()
        val reminderTime = dueDateMillis - (reminderMinutesBefore * 60 * 1000)
        val delay = reminderTime - now

        if (delay <= 0) return // Already past reminder time

        val data = Data.Builder()
            .putLong("task_id", taskId)
            .putString("task_title", taskTitle)
            .putLong("due_date", dueDateMillis)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("reminder_task_$taskId")
            .build()

        WorkManager.getInstance(context)
            .enqueue(workRequest)
    }

    fun cancelTaskReminder(context: Context, taskId: Long) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("reminder_task_$taskId")
    }

    fun showNotification(context: Context, taskId: Long, title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⏰ $title")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(taskId.toInt(), notification)
    }
}

class TaskReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong("task_id", 0)
        val taskTitle = inputData.getString("task_title") ?: "งาน"
        val dueDate = inputData.getLong("due_date", 0)

        NotificationHelper.showNotification(
            context = applicationContext,
            taskId = taskId,
            title = taskTitle,
            message = "⏰ งานนี้ใกล้ถึง Deadline แล้ว! อย่าลืมทำนะ 💪"
        )

        return Result.success()
    }
}

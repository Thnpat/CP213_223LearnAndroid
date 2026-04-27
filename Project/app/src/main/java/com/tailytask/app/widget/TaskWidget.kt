package com.tailytask.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.tailytask.app.data.local.AppDatabase
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import com.tailytask.app.MainActivity
import androidx.glance.LocalContext
import android.content.Intent
import androidx.glance.appwidget.cornerRadius

class TaskWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getDatabase(context)
        val today = Calendar.getInstance()
        val startOfDay = (today.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val endOfDay = (today.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val todayTasks = try {
            db.taskDao().getTasksForDate(startOfDay, endOfDay).first()
        } catch (e: Exception) { emptyList() }

        val pendingCount = todayTasks.count { !it.isCompleted }
        val completedCount = todayTasks.count { it.isCompleted }
        val dateText = SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(today.time)

        provideContent {
            GlanceTheme {
                WidgetContent(
                    dateText = dateText,
                    pendingCount = pendingCount,
                    completedCount = completedCount,
                    taskTitles = todayTasks.filter { !it.isCompleted }.take(4).map { it.title }
                )
            }
        }
    }
}

@Composable
fun WidgetContent(
    dateText: String,
    pendingCount: Int,
    completedCount: Int,
    taskTitles: List<String>
) {
    val context = LocalContext.current
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(androidx.compose.ui.graphics.Color.White))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Header Row
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "TailyTask",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF5C5CFF))
                    )
                )
                Text(
                    text = dateText,
                    style = TextStyle(color = ColorProvider(androidx.compose.ui.graphics.Color.Gray))
                )
            }

            // Fast Record Button
            Row(
                modifier = GlanceModifier
                    .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFFEEEEFF)))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .clickable(actionStartActivity(Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨ AI Record",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF5C5CFF)),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(16.dp))

        // Stats Row
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFFF5F5F5)))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📌 $pendingCount Pending",
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(androidx.compose.ui.graphics.Color.DarkGray)
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = "✅ $completedCount Done",
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF4CAF50))
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Tasks List
        if (taskTitles.isEmpty()) {
            Text(
                text = "🎉 All caught up for today!",
                style = TextStyle(color = ColorProvider(androidx.compose.ui.graphics.Color.Gray))
            )
        } else {
            taskTitles.forEach { title ->
                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "•",
                        style = TextStyle(color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF5C5CFF)))
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = title,
                        style = TextStyle(color = ColorProvider(androidx.compose.ui.graphics.Color.Black)),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

class TaskWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TaskWidget()
}

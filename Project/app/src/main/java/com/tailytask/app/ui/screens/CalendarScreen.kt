package com.tailytask.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tailytask.app.model.Priority
import com.tailytask.app.ui.components.TaskCard
import com.tailytask.app.ui.theme.PriorityHigh
import com.tailytask.app.ui.theme.PriorityLow
import com.tailytask.app.ui.theme.PriorityMedium
import com.tailytask.app.viewmodel.ProjectViewModel
import com.tailytask.app.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(
    taskViewModel: TaskViewModel,
    projectViewModel: ProjectViewModel
) {
    val allTasks by taskViewModel.allTasks.collectAsState()
    val allProjects by projectViewModel.allProjects.collectAsState()
    val selectedDateTasks by taskViewModel.selectedDateTasks.collectAsState()

    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDay by remember { mutableStateOf<Int?>(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("th"))
    val today = Calendar.getInstance()

    // Day bounds mapping
    val monthStartCal = (currentMonth.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val monthEndCal = (currentMonth.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
    }

    // Load tasks for selected date
    LaunchedEffect(selectedDay, currentMonth.get(Calendar.MONTH)) {
        selectedDay?.let { day ->
            val cal = currentMonth.clone() as Calendar
            cal.set(Calendar.DAY_OF_MONTH, day)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val startOfDay = cal.timeInMillis
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            val endOfDay = cal.timeInMillis
            taskViewModel.loadTasksForDate(startOfDay, endOfDay)
        }
    }

    // Calendar data
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = (currentMonth.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday

    // Create a map of dates to priority dots
    val taskDotMap = remember(allTasks, currentMonth.get(Calendar.MONTH), currentMonth.get(Calendar.YEAR)) {
        val map = mutableMapOf<Int, MutableList<String>>()

        allTasks.forEach { task ->
            task.dueDate?.let { dueDate ->
                if (dueDate in monthStartCal.timeInMillis..monthEndCal.timeInMillis) {
                    val taskCal = Calendar.getInstance().apply { timeInMillis = dueDate }
                    val day = taskCal.get(Calendar.DAY_OF_MONTH)
                    map.getOrPut(day) { mutableListOf() }.add(task.priority)
                }
            }
        }
        map
    }

    // Create map for project indicators (dates where a project has deadline)
    val projectDotMap = remember(allProjects, currentMonth.get(Calendar.MONTH), currentMonth.get(Calendar.YEAR)) {
        val map = mutableMapOf<Int, MutableList<String>>() // hex colors
        
        allProjects.forEach { project ->
            if (project.deadline in monthStartCal.timeInMillis..monthEndCal.timeInMillis) {
                 val pCal = Calendar.getInstance().apply { timeInMillis = project.deadline }
                 val day = pCal.get(Calendar.DAY_OF_MONTH)
                 map.getOrPut(day) { mutableListOf() }.add(project.colorHex)
            }
        }
        map
    }

    val dayLabels = listOf("อา", "จ", "อ", "พ", "พฤ", "ศ", "ส")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "📅 ปฏิทิน",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Month navigation
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Month header + arrows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            currentMonth = (currentMonth.clone() as Calendar).apply {
                                add(Calendar.MONTH, -1)
                            }
                            selectedDay = null
                        }) {
                            Icon(Icons.Filled.ChevronLeft, "Previous month",
                                tint = MaterialTheme.colorScheme.primary)
                        }

                        Text(
                            text = monthFormat.format(currentMonth.time),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(onClick = {
                            currentMonth = (currentMonth.clone() as Calendar).apply {
                                add(Calendar.MONTH, 1)
                            }
                            selectedDay = null
                        }) {
                            Icon(Icons.Filled.ChevronRight, "Next month",
                                tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Day of week headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        dayLabels.forEach { label ->
                            Text(
                                text = label,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar grid
                    val totalCells = firstDayOfMonth + daysInMonth
                    val rows = (totalCells + 6) / 7

                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0..6) {
                                val cellIndex = row * 7 + col
                                val day = cellIndex - firstDayOfMonth + 1

                                if (day in 1..daysInMonth) {
                                    val isToday = day == today.get(Calendar.DAY_OF_MONTH) &&
                                            currentMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                            currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                                    val isSelected = day == selectedDay
                                    val taskDots = taskDotMap[day]
                                    val projectDots = projectDotMap[day]

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    isSelected -> MaterialTheme.colorScheme.primary
                                                    isToday -> MaterialTheme.colorScheme.primary.copy(
                                                        alpha = 0.15f
                                                    )
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable { selectedDay = day },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            if (projectDots != null) {
                                                Icon(
                                                    Icons.Filled.FolderOpen, null,
                                                    modifier = Modifier.size(10.dp),
                                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                            } else {
                                                Spacer(modifier = Modifier.height(10.dp))
                                            }
                                            
                                            Text(
                                                text = "$day",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isToday || isSelected)
                                                    FontWeight.Bold else FontWeight.Normal,
                                                color = when {
                                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                                    isToday -> MaterialTheme.colorScheme.primary
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }
                                            )

                                            // Priority dots
                                            if (taskDots != null) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    taskDots.take(3).forEach { priority ->
                                                        val dotColor = when (priority) {
                                                            Priority.HIGH.name -> PriorityHigh
                                                            Priority.MEDIUM.name -> PriorityMedium
                                                            else -> PriorityLow
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .size(5.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                    if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(
                                                                        alpha = 0.8f
                                                                    )
                                                                    else dotColor
                                                                )
                                                        )
                                                    }
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.height(5.dp))
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected day tasks
        if (selectedDay != null) {
            item {
                Text(
                    text = "📋 งานวันที่ $selectedDay",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (selectedDateTasks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "😊", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ไม่มีงานในวันนี้",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                items(selectedDateTasks, key = { "cal_${it.id}" }) { task ->
                    TaskCard(
                        task = task,
                        onToggleComplete = { taskViewModel.toggleTask(task) },
                        onDelete = { taskViewModel.deleteTask(task) }
                    )
                }
            }
        }

        // Legend
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem(color = PriorityHigh, label = "สำคัญสูง")
                    LegendItem(color = PriorityMedium, label = "ปานกลาง")
                    LegendItem(color = PriorityLow, label = "ต่ำ")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

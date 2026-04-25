package com.tailytask.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tailytask.app.model.Priority
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

    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val today = Calendar.getInstance()

    // Bounds for the current month
    val monthStartCal = (currentMonth.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    val dayWidth = 60.dp
    val headerHeight = 40.dp
    val rowHeight = 60.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp)
    ) {
        // Title
        Text(
            text = "Calendar",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Month Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            }) {
                Icon(Icons.Filled.ChevronLeft, "Previous month", tint = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = monthFormat.format(currentMonth.time),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
            }) {
                Icon(Icons.Filled.ChevronRight, "Next month", tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Gantt Chart Area
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalScroll(scrollState)
        ) {
            val totalWidth = dayWidth * daysInMonth
            
            // Background Grid
            Row(modifier = Modifier.width(totalWidth)) {
                for (day in 1..daysInMonth) {
                    val isToday = day == today.get(Calendar.DAY_OF_MONTH) &&
                            currentMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                            currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    
                    Box(
                        modifier = Modifier
                            .width(dayWidth)
                            .fillMaxHeight()
                            .border(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            .background(if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent)
                    )
                }
            }

            Column(modifier = Modifier.width(totalWidth)) {
                // Header Row (Days)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(headerHeight)
                ) {
                    for (day in 1..daysInMonth) {
                        Box(
                            modifier = Modifier.width(dayWidth).fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "วัน $day",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))

                // Items (Projects & Tasks)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Filter items relevant to this month
                    val monthStartMs = monthStartCal.timeInMillis
                    val monthEndMs = monthStartMs + (daysInMonth * 24L * 60 * 60 * 1000)

                    val monthProjects = allProjects.filter { 
                        it.startDate <= monthEndMs && it.deadline >= monthStartMs 
                    }
                    val monthTasks = allTasks.filter { 
                        it.dueDate != null && it.dueDate!! in monthStartMs..monthEndMs 
                    }

                    // Render Projects
                    items(monthProjects.size) { index ->
                        val project = monthProjects[index]
                        val color = try { Color(android.graphics.Color.parseColor(project.colorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }
                        
                        // Calculate start and end indices
                        val startDayRaw = ((project.startDate - monthStartMs) / (1000 * 60 * 60 * 24)).toInt()
                        val endDayRaw = ((project.deadline - monthStartMs) / (1000 * 60 * 60 * 24)).toInt()
                        
                        val startDay = startDayRaw.coerceAtLeast(0)
                        val endDay = endDayRaw.coerceAtMost(daysInMonth - 1)
                        val span = (endDay - startDay + 1).coerceAtLeast(1)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(rowHeight)
                                .padding(vertical = 8.dp)
                        ) {
                            // Project Line
                            Box(
                                modifier = Modifier
                                    .padding(start = dayWidth * startDay, top = 20.dp)
                                    .width(dayWidth * span)
                                    .height(4.dp)
                                    .background(color, RoundedCornerShape(2.dp))
                            )
                            // Project Label
                            Text(
                                text = project.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = color,
                                modifier = Modifier.padding(start = dayWidth * startDay + 4.dp)
                            )
                        }
                    }

                    // Render Tasks
                    items(monthTasks.size) { index ->
                        val task = monthTasks[index]
                        val taskDay = ((task.dueDate!! - monthStartMs) / (1000 * 60 * 60 * 24)).toInt().coerceIn(0, daysInMonth - 1)
                        val color = when (task.priority) {
                            Priority.HIGH.name -> PriorityHigh
                            Priority.MEDIUM.name -> PriorityMedium
                            else -> PriorityLow
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(rowHeight)
                                .padding(vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = dayWidth * taskDay + 4.dp, end = 4.dp)
                                    .width(dayWidth * 2 - 8.dp) // Make task boxes span a bit for visibility
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(2.dp, color, RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(8.dp)
                                    .clickable { taskViewModel.toggleTask(task) },
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                                )
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

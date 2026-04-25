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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.asPaddingValues
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
    
    val dayWidth = 75.dp
    val headerHeight = 50.dp
    val rowHeight = 64.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding() + 16.dp)
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
            IconButton(
                onClick = { currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) } },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Filled.ChevronLeft, "Previous month", tint = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = monthFormat.format(currentMonth.time),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = { currentMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) } },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Filled.ChevronRight, "Next month", tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Gantt Chart Area
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalScroll(scrollState)
        ) {
            val totalWidth = dayWidth * daysInMonth
            
            // Background Grid (Vertical Lines & Today Highlight)
            Row(modifier = Modifier.width(totalWidth)) {
                for (day in 1..daysInMonth) {
                    val isToday = day == today.get(Calendar.DAY_OF_MONTH) &&
                            currentMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                            currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    
                    Box(
                        modifier = Modifier
                            .width(dayWidth)
                            .fillMaxHeight()
                            .background(if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent)
                    ) {
                        // Right border line for each day
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        )
                    }
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
                        val isToday = day == today.get(Calendar.DAY_OF_MONTH) &&
                                currentMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                                currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                                
                        Box(
                            modifier = Modifier.width(dayWidth).fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$day",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
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
                                .padding(vertical = 6.dp)
                        ) {
                            // Project Bar (Pill Shape)
                            Box(
                                modifier = Modifier
                                    .padding(start = dayWidth * startDay + 8.dp)
                                    .width(dayWidth * span - 16.dp)
                                    .fillMaxHeight()
                                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                    .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = project.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = color,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
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
                                .padding(vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = dayWidth * taskDay + 8.dp)
                                    .width(dayWidth * 1.5f - 16.dp) // Task box spans 1.5 days for visibility
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.5.dp, if (task.isCompleted) color.copy(alpha = 0.3f) else color, RoundedCornerShape(12.dp))
                                    .clickable { taskViewModel.toggleTask(task) }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                                )
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(120.dp)) }
                }
            }
        }
    }
}

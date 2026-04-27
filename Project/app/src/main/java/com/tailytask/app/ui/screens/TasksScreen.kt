package com.tailytask.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.tailytask.app.data.local.TaskEntity
import com.tailytask.app.model.Category
import com.tailytask.app.model.Priority
import com.tailytask.app.ui.components.AddTaskSheet
import com.tailytask.app.ui.components.TaskCard
import com.tailytask.app.ui.theme.PointsGold
import com.tailytask.app.ui.theme.PriorityHigh
import com.tailytask.app.ui.theme.PriorityLow
import com.tailytask.app.ui.theme.PriorityMedium
import com.tailytask.app.viewmodel.ProjectViewModel
import com.tailytask.app.viewmodel.TaskViewModel
import com.tailytask.app.viewmodel.ThemeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    taskViewModel: TaskViewModel,
    projectViewModel: ProjectViewModel,
    themeViewModel: ThemeViewModel,
    onBack: () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    val allTasks by taskViewModel.allTasks.collectAsState()
    val allProjects by projectViewModel.allProjects.collectAsState()
    val fastRecordMsg by taskViewModel.fastRecordMessage.collectAsState()

    var isGanttView by remember { mutableStateOf(true) }
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedTask by remember { mutableStateOf<com.tailytask.app.data.local.TaskEntity?>(null) }
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val listState = rememberLazyListState()

    LaunchedEffect(fastRecordMsg) {
        fastRecordMsg?.let {
            snackbarHostState.showSnackbar(it)
            taskViewModel.clearFastRecordMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
    ) {
        // Header: CALENDAR + back button + date
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CALENDAR",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = java.text.SimpleDateFormat("EEEE, MMMM d", java.util.Locale.getDefault())
                        .format(java.util.Calendar.getInstance().time),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                )
            }
        }

        // View toggles + add
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Tasks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            // List view toggle
            IconButton(
                onClick = { isGanttView = false },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (!isGanttView) MaterialTheme.colorScheme.primaryContainer
                        else Color.Transparent
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.FormatListBulleted, "List view",
                    tint = if (!isGanttView) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            // Gantt view toggle
            IconButton(
                onClick = { isGanttView = true },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isGanttView) MaterialTheme.colorScheme.primaryContainer
                        else Color.Transparent
                    )
            ) {
                Icon(
                    Icons.Filled.CalendarViewWeek, "Gantt view",
                    tint = if (isGanttView) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = { showAddSheet = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Filled.Add, "Add task",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isGanttView) {
            // ===== GANTT CALENDAR VIEW =====
            GanttCalendarView(
                tasks = allTasks,
                projects = allProjects,
                taskViewModel = taskViewModel,
                onTaskClick = { selectedTask = it },
                modifier = Modifier.weight(1f)
            )
        } else {
            // ===== LIST VIEW (grouped by category) =====
            TaskListView(
                tasks = allTasks,
                taskViewModel = taskViewModel,
                onTaskClick = { selectedTask = it },
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (showAddSheet) {
        AddTaskSheet(
            sheetState = sheetState,
            onDismiss = { showAddSheet = false },
            onSave = { task ->
                taskViewModel.addTask(task)
                showAddSheet = false
            }
        )
    }

    selectedTask?.let { task ->
        com.tailytask.app.ui.components.TaskDetailSheet(
            task = task,
            sheetState = detailSheetState,
            onDismiss = { selectedTask = null }
        )
    }
}

@Composable
fun GanttCalendarView(
    tasks: List<TaskEntity>,
    projects: List<com.tailytask.app.data.local.ProjectEntity>,
    taskViewModel: TaskViewModel,
    onTaskClick: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = Calendar.getInstance()
    val dayWidth = 100.dp
    val dayWidthPx = with(LocalDensity.current) { dayWidth.toPx() }
    val rowHeight = 52.dp
    val headerHeight = 56.dp

    // Show 7 days centered on today (start from 2 days ago)
    val startCal = (today.clone() as Calendar).apply {
        add(Calendar.DAY_OF_YEAR, -1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val totalDays = 14
    val scrollState = rememberScrollState()

    // Auto-scroll to today on first composition
    LaunchedEffect(Unit) {
        scrollState.scrollTo((dayWidthPx * 1).toInt())
    }

    val monthStartMs = startCal.timeInMillis
    val monthEndMs = monthStartMs + (totalDays * 24L * 60 * 60 * 1000)

    val visibleProjects = projects.filter {
        it.startDate <= monthEndMs && it.deadline >= monthStartMs
    }
    val visibleTasks = tasks.filter {
        it.dueDate != null && it.dueDate in monthStartMs..monthEndMs
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
        ) {
            val totalWidth = dayWidth * totalDays

            Column(modifier = Modifier.width(totalWidth)) {
                // Day header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(headerHeight)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    for (day in 0 until totalDays) {
                        val dayCal = (startCal.clone() as Calendar).apply {
                            add(Calendar.DAY_OF_YEAR, day)
                        }
                        val isToday = dayCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                                dayCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                        val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(dayCal.time).uppercase()
                        val dayNum = dayCal.get(Calendar.DAY_OF_MONTH)

                        Box(
                            modifier = Modifier
                                .width(dayWidth)
                                .fillMaxHeight()
                                .then(
                                    if (day < totalDays - 1) Modifier.border(
                                        width = 0.5.dp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                    ) else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isToday) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isToday) MaterialTheme.colorScheme.primary
                                            else Color.Transparent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$dayNum",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isToday) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                // Content area with vertical grid lines
                Box(modifier = Modifier.fillMaxSize()) {
                    // Grid lines
                    Row(modifier = Modifier.fillMaxSize()) {
                        for (day in 0 until totalDays) {
                            val dayCal = (startCal.clone() as Calendar).apply {
                                add(Calendar.DAY_OF_YEAR, day)
                            }
                            val isToday = dayCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                                    dayCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                            Box(
                                modifier = Modifier
                                    .width(dayWidth)
                                    .fillMaxHeight()
                                    .background(
                                        if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                                        else Color.Transparent
                                    )
                                    .then(
                                        if (day < totalDays - 1) Modifier.border(
                                            width = 0.5.dp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                                        ) else Modifier
                                    )
                            )
                        }
                    }

                    // Project bars + Task items
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        // Project bars
                        items(visibleProjects, key = { "proj_${it.id}" }) { project ->
                            val color = try {
                                Color(android.graphics.Color.parseColor(project.colorHex))
                            } catch (e: Exception) { MaterialTheme.colorScheme.primary }

                            val startDayFloat = (project.startDate - monthStartMs).toFloat() / (24f * 60 * 60 * 1000)
                            val endDayFloat = (project.deadline - monthStartMs).toFloat() / (24f * 60 * 60 * 1000)
                            val startDay = startDayFloat.coerceAtLeast(0f)
                            val endDay = endDayFloat.coerceAtMost(totalDays.toFloat())
                            val span = (endDay - startDay).coerceAtLeast(0.5f)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(rowHeight)
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = dayWidth * startDay + 4.dp)
                                        .width(dayWidth * span - 8.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(color.copy(alpha = 0.2f))
                                        .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp),
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

                        // Task items (draggable)
                        items(visibleTasks, key = { "task_${it.id}" }) { task ->
                            val taskDayFloat = ((task.dueDate!! - monthStartMs).toFloat() / (24f * 60 * 60 * 1000))
                                .coerceIn(0f, (totalDays - 1).toFloat())
                            val priorityColor = when (task.priority) {
                                Priority.HIGH.name -> PriorityHigh
                                Priority.MEDIUM.name -> PriorityMedium
                                else -> PriorityLow
                            }

                            var offsetX by remember { mutableFloatStateOf(0f) }
                            var isDragging by remember { mutableStateOf(false) }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(rowHeight)
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .offset { IntOffset((dayWidthPx * taskDayFloat + offsetX).roundToInt(), 0) }
                                        .padding(horizontal = 4.dp)
                                        .width(dayWidth - 8.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isDragging) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                        )
                                        .clickable { onTaskClick(task) }
                                        .pointerInput(task.id) {
                                            detectDragGesturesAfterLongPress(
                                                onDragStart = { isDragging = true },
                                                onDragEnd = {
                                                    isDragging = false
                                                    val daysMoved = (offsetX / dayWidthPx).roundToInt()
                                                    if (daysMoved != 0) {
                                                        val newDate = task.dueDate!! + (daysMoved * 24L * 60 * 60 * 1000)
                                                        taskViewModel.updateTaskDate(task, newDate)
                                                    }
                                                    offsetX = 0f
                                                },
                                                onDragCancel = {
                                                    isDragging = false
                                                    offsetX = 0f
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    offsetX += dragAmount.x
                                                }
                                            )
                                        }
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(priorityColor)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = if (task.isCompleted)
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                            else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                                        )
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskListView(
    tasks: List<TaskEntity>,
    taskViewModel: TaskViewModel,
    onTaskClick: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedTasks = tasks.groupBy { it.category }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedTasks.forEach { (categoryName, categoryTasks) ->
            val category = try { Category.valueOf(categoryName) } catch (e: Exception) { Category.OTHER }

            item(key = "header_$categoryName") {
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(categoryTasks, key = { "list_${it.id}" }) { task ->
                com.tailytask.app.ui.components.TaskCard(
                    task = task,
                    onToggleComplete = { taskViewModel.toggleTask(task) },
                    onDelete = { taskViewModel.deleteTask(task) },
                    onClick = { onTaskClick(task) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (tasks.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No tasks yet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "Tap + to add a task or use Fast Record",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

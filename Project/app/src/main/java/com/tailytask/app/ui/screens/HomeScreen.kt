package com.tailytask.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tailytask.app.model.Category
import com.tailytask.app.ui.components.AddTaskSheet
import com.tailytask.app.ui.components.TaskCard
import com.tailytask.app.viewmodel.ProjectViewModel
import com.tailytask.app.viewmodel.TaskViewModel
import com.tailytask.app.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    taskViewModel: TaskViewModel,
    projectViewModel: ProjectViewModel,
    themeViewModel: ThemeViewModel,
    onNavigateToCalendar: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val allTasks by taskViewModel.allTasks.collectAsState()
    val completedCount by taskViewModel.completedCount.collectAsState()
    val totalCount by taskViewModel.totalCount.collectAsState()
    val overdueTasks by taskViewModel.overdueTasks.collectAsState()
    val totalPoints by themeViewModel.totalPoints.collectAsState()
    val fastRecordMsg by taskViewModel.fastRecordMessage.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedTask by remember { mutableStateOf<com.tailytask.app.data.local.TaskEntity?>(null) }
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val pendingTasks = allTasks.filter { !it.isCompleted }
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    LaunchedEffect(fastRecordMsg) {
        fastRecordMsg?.let {
            snackbarHostState.showSnackbar(it)
            taskViewModel.clearFastRecordMessage()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // ===== HEADER: DASHBOARD + POINTS =====
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DASHBOARD",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                // POINTS badge (top-right, matching wireframe)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("POINTS", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("$totalPoints", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // ===== STATUS CARD (with frame like wireframe) =====
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Progress circle (like wireframe 7/10)
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(90.dp),
                            strokeWidth = 7.dp,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$completedCount/$totalCount",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // ALL Task / DELAY stats (right side)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("ALL Task", style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("$totalCount", style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("DELAY", style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("${overdueTasks.size}", style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (overdueTasks.isNotEmpty()) Color(0xFFE57373)
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
                        }
                    }
                }
            }
        }

        // ===== CALENDAR BUTTON =====
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clickable { onNavigateToCalendar() },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Calendar", fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        // ===== TASK SECTION HEADER =====
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TASK", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showAddSheet = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Add, "Add task", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // ===== TASKS BY CATEGORY (like wireframe: Category 1, Category 2...) =====
        val groupedTasks = pendingTasks.groupBy { it.category }
        groupedTasks.forEach { (categoryName, categoryTasks) ->
            val category = try { Category.valueOf(categoryName) } catch (e: Exception) { Category.OTHER }

            item(key = "header_$categoryName") {
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp)
                )
            }

            items(categoryTasks, key = { "task_${it.id}" }) { task ->
                TaskCard(
                    task = task,
                    onToggleComplete = { taskViewModel.toggleTask(task) },
                    onDelete = { taskViewModel.deleteTask(task) },
                    onClick = { selectedTask = task },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 3.dp)
                )
            }
        }

        // Completed tasks
        val doneTasks = allTasks.filter { it.isCompleted }
        if (doneTasks.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Completed (${doneTasks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            items(doneTasks.take(3), key = { "done_${it.id}" }) { task ->
                TaskCard(
                    task = task,
                    onToggleComplete = { taskViewModel.toggleTask(task) },
                    onDelete = { taskViewModel.deleteTask(task) },
                    onClick = { selectedTask = task },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 3.dp)
                )
            }
        }

        // Empty state
        if (allTasks.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.TaskAlt, null, modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No tasks yet", fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f))
                    Text("Tap + or use AI Fast Record to add", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f))
                }
            }
        }
    }

    if (showAddSheet) {
        AddTaskSheet(
            sheetState = sheetState,
            onDismiss = { showAddSheet = false },
            onSave = { task -> taskViewModel.addTask(task); showAddSheet = false }
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

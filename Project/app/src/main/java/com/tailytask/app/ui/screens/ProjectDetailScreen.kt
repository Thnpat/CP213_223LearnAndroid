package com.tailytask.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tailytask.app.data.local.SubtaskEntity
import com.tailytask.app.viewmodel.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectDetailScreen(
    projectId: Long,
    projectViewModel: ProjectViewModel,
    onBack: () -> Unit
) {
    val project by projectViewModel.selectedProject.collectAsState()
    val subtasks by projectViewModel.subtasks.collectAsState()
    val subtaskCount by projectViewModel.subtaskCount.collectAsState()
    val completedSubtaskCount by projectViewModel.completedSubtaskCount.collectAsState()
    var showAddSubtask by remember { mutableStateOf(false) }
    var newSubtaskTitle by remember { mutableStateOf("") }

    LaunchedEffect(projectId) { projectViewModel.loadProject(projectId) }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val projectColor = try {
        Color(android.graphics.Color.parseColor(project?.colorHex ?: "#F48FB1"))
    } catch (e: Exception) { MaterialTheme.colorScheme.primary }
    val progress = if (subtaskCount > 0) completedSubtaskCount.toFloat() / subtaskCount else 0f

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            Spacer(modifier = Modifier.weight(1f))
            if (project?.isCompleted == false) {
                TextButton(onClick = { project?.let { projectViewModel.completeProject(it) } }) {
                    Text("Mark Done", color = Color(0xFF66BB6A), fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            project?.let { proj ->
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                                        .background(projectColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) { Icon(Icons.Filled.Folder, null, tint = projectColor, modifier = Modifier.size(26.dp)) }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(proj.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                    Text("${dateFormat.format(Date(proj.startDate))} → ${dateFormat.format(Date(proj.deadline))}",
                                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                            }
                            if (proj.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(proj.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LinearProgressIndicator(progress = { progress }, modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                                    color = projectColor, trackColor = projectColor.copy(alpha = 0.15f), strokeCap = androidx.compose.ui.graphics.StrokeCap.Round)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("$completedSubtaskCount/$subtaskCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = projectColor)
                            }
                        }
                    }
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sub-Tasks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { showAddSubtask = !showAddSubtask }) {
                            Icon(if (showAddSubtask) Icons.Filled.Close else Icons.Filled.Add, "Add", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                if (showAddSubtask) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(value = newSubtaskTitle, onValueChange = { newSubtaskTitle = it },
                                placeholder = { Text("New sub-task...") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), singleLine = true)
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                if (newSubtaskTitle.isNotBlank()) {
                                    projectViewModel.addSubtask(SubtaskEntity(projectId = projectId, title = newSubtaskTitle))
                                    newSubtaskTitle = ""
                                }
                            }, shape = RoundedCornerShape(14.dp), contentPadding = PaddingValues(12.dp)) { Icon(Icons.Filled.Add, null, modifier = Modifier.size(20.dp)) }
                        }
                    }
                }
                items(subtasks, key = { it.id }) { subtask ->
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(
                        containerColor = if (subtask.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ), elevation = CardDefaults.cardElevation(1.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { projectViewModel.toggleSubtask(subtask) }, modifier = Modifier.size(32.dp)) {
                                Icon(if (subtask.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked, "Toggle",
                                    tint = if (subtask.isCompleted) projectColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(subtask.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium,
                                    textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else null,
                                    color = if (subtask.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface)
                                Text("+${subtask.points} pts", style = MaterialTheme.typography.labelSmall, color = Color(0xFFB8860B))
                            }
                            IconButton(onClick = { projectViewModel.deleteSubtask(subtask) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.Close, "Delete", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.tailytask.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tailytask.app.ui.components.AddProjectSheet
import com.tailytask.app.ui.components.ProjectCard
import com.tailytask.app.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    projectViewModel: ProjectViewModel,
    onProjectClick: (Long) -> Unit
) {
    val allProjects by projectViewModel.allProjects.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val activeProjects = allProjects.filter { !it.isCompleted }
    val completedProjects = allProjects.filter { it.isCompleted }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Projects",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp, bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Active projects
            if (activeProjects.isNotEmpty()) {
                item {
                    Text(
                        text = "In Progress (${activeProjects.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                items(activeProjects, key = { "active_${it.id}" }) { project ->
                    val sc by remember(project.id) {
                        projectViewModel.getSubtaskCountFlow(project.id)
                    }.collectAsState(initial = 0)
                    val cc by remember(project.id) {
                        projectViewModel.getCompletedSubtaskCountFlow(project.id)
                    }.collectAsState(initial = 0)

                    ProjectCard(
                        project = project,
                        subtaskCount = sc,
                        completedSubtaskCount = cc,
                        onClick = { onProjectClick(project.id) },
                        onDelete = { projectViewModel.deleteProject(project) },
                        onToggleComplete = { projectViewModel.completeProject(project) }
                    )
                }
            }

            // Completed projects
            if (completedProjects.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Completed (${completedProjects.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
                items(completedProjects, key = { "done_${it.id}" }) { project ->
                    val sc by remember(project.id) {
                        projectViewModel.getSubtaskCountFlow(project.id)
                    }.collectAsState(initial = 0)
                    val cc by remember(project.id) {
                        projectViewModel.getCompletedSubtaskCountFlow(project.id)
                    }.collectAsState(initial = 0)

                    ProjectCard(
                        project = project,
                        subtaskCount = sc,
                        completedSubtaskCount = cc,
                        onClick = { onProjectClick(project.id) },
                        onDelete = { projectViewModel.deleteProject(project) },
                        onToggleComplete = { projectViewModel.uncompleteProject(project) }
                    )
                }
            }

            // + ADD PROJECT button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showAddSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = ButtonDefaults.outlinedButtonBorder(true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                ) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "ADD PROJECT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Empty state
            if (allProjects.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Folder, null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No projects yet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddProjectSheet(
            sheetState = sheetState,
            onDismiss = { showAddSheet = false },
            onSave = { project ->
                projectViewModel.addProject(project)
                showAddSheet = false
            }
        )
    }
}

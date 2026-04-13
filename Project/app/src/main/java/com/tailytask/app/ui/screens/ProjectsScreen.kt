package com.tailytask.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Project")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "📁 โปรเจค",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Active projects
            if (activeProjects.isNotEmpty()) {
                item {
                    Text(
                        text = "🔥 กำลังดำเนินการ (${activeProjects.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                items(activeProjects, key = { "active_${it.id}" }) { project ->
                    val subtaskCountFlow = remember(project.id) {
                        projectViewModel.getSubtaskCountFlow(project.id)
                    }
                    val completedCountFlow = remember(project.id) {
                        projectViewModel.getCompletedSubtaskCountFlow(project.id)
                    }
                    val sc by subtaskCountFlow.collectAsState(initial = 0)
                    val cc by completedCountFlow.collectAsState(initial = 0)

                    ProjectCard(
                        project = project,
                        subtaskCount = sc,
                        completedSubtaskCount = cc,
                        onClick = { onProjectClick(project.id) },
                        onDelete = { projectViewModel.deleteProject(project) }
                    )
                }
            }

            // Completed projects
            if (completedProjects.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ เสร็จแล้ว (${completedProjects.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
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
                        onDelete = { projectViewModel.deleteProject(project) }
                    )
                }
            }

            // Empty state
            if (allProjects.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📂", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ยังไม่มีโปรเจค",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "กดปุ่ม + เพื่อสร้างโปรเจคใหม่",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
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

package com.tailytask.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tailytask.app.data.local.ProjectEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectCard(
    project: ProjectEntity,
    subtaskCount: Int,
    completedSubtaskCount: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val progress = if (subtaskCount > 0) completedSubtaskCount.toFloat() / subtaskCount else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress, animationSpec = tween(600), label = "progress"
    )
    val projectColor = try {
        Color(android.graphics.Color.parseColor(project.colorHex))
    } catch (e: Exception) { Color(0xFFF48FB1) }
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val isCompleted = project.isCompleted

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isCompleted) Modifier.background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                    ) else Modifier
                )
                .padding(16.dp)
        ) {
            // Top row: icon + title + actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color folder icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(projectColor.copy(alpha = if (isCompleted) 0.06f else 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.FolderOpen,
                        null,
                        tint = projectColor.copy(alpha = if (isCompleted) 0.4f else 1f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title + date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = if (isCompleted) 0.45f else 1f
                        ),
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                    )
                    Text(
                        text = "${dateFormat.format(Date(project.startDate))} → ${dateFormat.format(Date(project.deadline))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                }

                // Toggle complete/uncomplete button
                if (onToggleComplete != null) {
                    IconButton(onClick = onToggleComplete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (isCompleted) Icons.Filled.Replay else Icons.Filled.CheckCircle,
                            if (isCompleted) "Restore" else "Complete",
                            tint = if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                   else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Delete button
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Filled.Delete, "Delete",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Description
            if (project.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar + count
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = projectColor.copy(alpha = if (isCompleted) 0.3f else 1f),
                    trackColor = projectColor.copy(alpha = 0.08f),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "$completedSubtaskCount/$subtaskCount",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = projectColor.copy(alpha = if (isCompleted) 0.4f else 1f)
                )
            }
        }
    }
}

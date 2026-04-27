package com.tailytask.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tailytask.app.data.local.TaskEntity
import com.tailytask.app.model.Category
import com.tailytask.app.model.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailSheet(
    task: TaskEntity,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        val priority = try { Priority.valueOf(task.priority) } catch (e: Exception) { Priority.MEDIUM }
        val category = try { Category.valueOf(task.category) } catch (e: Exception) { Category.OTHER }
        val priorityColor = Color(priority.color)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Edit, "Edit", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, "Close", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                    }
                }
            }

            // Description
            Text(
                text = if (task.description.isNotBlank()) task.description else "No description provided.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // Info Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Due Date
                DetailCard(
                    title = "DUE DATE",
                    value = task.dueDate?.let { dateFormat.format(Date(it)) } ?: "Not set",
                    modifier = Modifier.weight(1f)
                )
                // Priority
                DetailCard(
                    title = "PRIORITY",
                    value = priority.name.lowercase().replaceFirstChar { it.uppercase() },
                    iconColor = priorityColor,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Points
                DetailCard(
                    title = "POINTS",
                    value = task.points.toString(),
                    icon = Icons.Filled.EmojiEvents,
                    iconColor = Color(0xFF5C5CFF),
                    modifier = Modifier.weight(1f)
                )
                // Start Time
                DetailCard(
                    title = "START TIME",
                    value = task.startTime?.takeIf { it.isNotBlank() } ?: "--:--",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // End Time
                DetailCard(
                    title = "END TIME",
                    value = task.endTime?.takeIf { it.isNotBlank() } ?: "--:--",
                    modifier = Modifier.weight(1f)
                )
                // Category
                DetailCard(
                    title = "CATEGORY",
                    value = category.label,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DetailCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconColor: Color? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null && iconColor != null) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
            } else if (iconColor != null) {
                // Dot icon for priority
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(iconColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

package com.tailytask.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tailytask.app.ui.theme.PointsGold
import com.tailytask.app.viewmodel.TaskViewModel

@Composable
fun AnalyticsScreen(
    taskViewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val totalCount by taskViewModel.totalCount.collectAsState()
    val completedCount by taskViewModel.completedCount.collectAsState()
    val pendingCount by taskViewModel.pendingCount.collectAsState()
    val overdueTasks by taskViewModel.overdueTasks.collectAsState()
    val weeklyAnalytics by taskViewModel.weeklyAnalytics.collectAsState()
    val points by taskViewModel.points.collectAsState()

    LaunchedEffect(Unit) { taskViewModel.loadWeeklyAnalytics() }

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
            Text("Analytics", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary cards
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AnalyticCard("Total", "$totalCount", Icons.Filled.TaskAlt, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    AnalyticCard("Done", "$completedCount", Icons.Filled.EmojiEvents, Color(0xFF66BB6A), Modifier.weight(1f))
                    AnalyticCard("Pending", "$pendingCount", Icons.Filled.Timer, Color(0xFFFFB74D), Modifier.weight(1f))
                }
            }

            item {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Completion Rate", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        val rate = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
                        LinearProgressIndicator(progress = { rate }, modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                            color = Color(0xFF66BB6A), trackColor = Color(0xFF66BB6A).copy(alpha = 0.15f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${(rate * 100).toInt()}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF66BB6A))
                    }
                }
            }

            // Weekly chart
            item {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("This Week", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        FullBarChart(labels = weeklyAnalytics.dayLabels, completed = weeklyAnalytics.completedCounts, total = weeklyAnalytics.totalCounts)
                    }
                }
            }

            // Overdue
            if (overdueTasks.isNotEmpty()) {
                item {
                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE57373).copy(alpha = 0.1f))) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Overdue Tasks (${overdueTasks.size})", style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, color = Color(0xFFE57373))
                            Spacer(modifier = Modifier.height(8.dp))
                            overdueTasks.take(5).forEach { task ->
                                Text("• ${task.title}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun AnalyticCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun FullBarChart(labels: List<String>, completed: List<Int>, total: List<Int>) {
    if (labels.isEmpty()) return
    val maxVal = total.maxOrNull()?.coerceAtLeast(1) ?: 1
    Row(modifier = Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
        labels.indices.forEach { i ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("${completed[i]}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Box(modifier = Modifier.width(20.dp)) {
                    val totalH = (total[i].toFloat() / maxVal * 80).coerceAtLeast(3f)
                    val compH = (completed[i].toFloat() / maxVal * 80).coerceAtLeast(0f)
                    Box(modifier = Modifier.width(20.dp).height(totalH.dp).clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)).align(Alignment.BottomCenter))
                    Box(modifier = Modifier.width(20.dp).height(compH.dp).clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary).align(Alignment.BottomCenter))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(labels[i], style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}

package com.tailytask.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tailytask.app.model.ThemeStore
import com.tailytask.app.viewmodel.TaskViewModel
import com.tailytask.app.viewmodel.ThemeViewModel

@Composable
fun ProfileScreen(
    taskViewModel: TaskViewModel,
    themeViewModel: ThemeViewModel,
    onNavigateToAnalytics: () -> Unit
) {
    val userName by themeViewModel.userName.collectAsState()
    val totalPoints by themeViewModel.totalPoints.collectAsState()
    val completedCount by taskViewModel.completedCount.collectAsState()
    val currentThemeId by themeViewModel.currentThemeId.collectAsState()
    val ownedThemes by themeViewModel.ownedThemes.collectAsState()
    val weeklyAnalytics by taskViewModel.weeklyAnalytics.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar and Name
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(90.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, null, modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(userName, style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(onClick = { showEditNameDialog = true }) {
                    Text("Edit Name", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Stats cards row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatMiniCard(Icons.Filled.EmojiEvents, "$totalPoints", "Total Points",
                    MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                StatMiniCard(Icons.Filled.TaskAlt, "$completedCount", "Tasks Done",
                    Color(0xFF66BB6A), Modifier.weight(1f))
            }
        }

        // Statistics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Statistics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = onNavigateToAnalytics) {
                            Text("View All", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    MiniBarChart(
                        labels = weeklyAnalytics.dayLabels,
                        values = weeklyAnalytics.completedCounts,
                        maxValue = weeklyAnalytics.totalCounts.maxOrNull()?.coerceAtLeast(1) ?: 1
                    )
                }
            }
        }

        // Quick Theme Switch
        item {
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Quick Theme Switch", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeStore.themes.filter { ownedThemes.contains(it.id) }.take(6).forEach { theme ->
                            val isActive = theme.id == currentThemeId
                            Box(
                                modifier = Modifier.size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(theme.primary)
                                    .clickable { themeViewModel.setTheme(theme.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isActive) {
                                    Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit name dialog
    if (showEditNameDialog) {
        var newName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Name", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newName, onValueChange = { newName = it },
                    label = { Text("Your Name") }, singleLine = true, shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newName.isNotBlank()) { themeViewModel.setUserName(newName); showEditNameDialog = false }
                }, shape = RoundedCornerShape(12.dp)) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showEditNameDialog = false }) { Text("Cancel") } },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun StatMiniCard(icon: ImageVector, value: String, label: String, iconTint: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun MiniBarChart(labels: List<String>, values: List<Int>, maxValue: Int) {
    if (labels.isEmpty()) return
    Row(modifier = Modifier.fillMaxWidth().height(80.dp), horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom) {
        labels.zip(values).forEach { (label, value) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                val barHeight = if (maxValue > 0) (value.toFloat() / maxValue * 50).coerceAtLeast(3f) else 3f
                Box(modifier = Modifier.width(16.dp).height(barHeight.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(MaterialTheme.colorScheme.primary))
                Spacer(modifier = Modifier.height(4.dp))
                Text(label, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}

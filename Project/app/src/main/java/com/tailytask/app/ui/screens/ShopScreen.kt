package com.tailytask.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tailytask.app.model.AppTheme
import com.tailytask.app.model.ThemeStore
import com.tailytask.app.ui.theme.PointsGold
import com.tailytask.app.viewmodel.ThemeViewModel

@Composable
fun ShopScreen(
    themeViewModel: ThemeViewModel,
    snackbarHostState: SnackbarHostState
) {
    val totalPoints by themeViewModel.totalPoints.collectAsState()
    val ownedThemes by themeViewModel.ownedThemes.collectAsState()
    val currentThemeId by themeViewModel.currentThemeId.collectAsState()
    val purchaseMessage by themeViewModel.purchaseMessage.collectAsState()

    var showPurchaseDialog by remember { mutableStateOf<AppTheme?>(null) }

    LaunchedEffect(purchaseMessage) {
        purchaseMessage?.let {
            snackbarHostState.showSnackbar(it)
            themeViewModel.clearPurchaseMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
    ) {
        // Header
        Text(
            text = "Theme Shop",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // Points display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "YOUR POINTS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.EmojiEvents, null,
                            tint = PointsGold,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$totalPoints",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB8860B)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Theme grid (2 columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ThemeStore.themes) { theme ->
                val isOwned = ownedThemes.contains(theme.id)
                val isActive = currentThemeId == theme.id

                ThemeGridCard(
                    theme = theme,
                    isOwned = isOwned,
                    isActive = isActive,
                    canAfford = totalPoints >= theme.price,
                    onPurchase = { showPurchaseDialog = theme },
                    onApply = { themeViewModel.setTheme(theme.id) }
                )
            }
        }
    }

    // Purchase dialog
    showPurchaseDialog?.let { theme ->
        AlertDialog(
            onDismissRequest = { showPurchaseDialog = null },
            title = {
                Text("Purchase Theme?", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Buy ${theme.name}?")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${theme.price} pts",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB8860B)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        themeViewModel.purchaseTheme(theme.id)
                        showPurchaseDialog = null
                    },
                    enabled = totalPoints >= theme.price,
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Buy!") }
            },
            dismissButton = {
                TextButton(onClick = { showPurchaseDialog = null }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ThemeGridCard(
    theme: AppTheme,
    isOwned: Boolean,
    isActive: Boolean,
    canAfford: Boolean,
    onPurchase: () -> Unit,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .then(
                if (isActive) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                else Modifier
            )
            .clickable {
                when {
                    isActive -> { /* already active */ }
                    isOwned -> onApply()
                    else -> onPurchase()
                }
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Color preview
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(theme.primary, theme.secondary, theme.accent).forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = theme.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Price / Status
            when {
                isActive -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Active", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                isOwned -> {
                    Text("Owned", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                theme.price == 0 -> {
                    Text("Free", style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold, color = Color(0xFF66BB6A))
                }
                else -> {
                    Text(
                        "${theme.price} pts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (canAfford) Color(0xFFB8860B) else Color.Gray
                    )
                }
            }
        }
    }
}

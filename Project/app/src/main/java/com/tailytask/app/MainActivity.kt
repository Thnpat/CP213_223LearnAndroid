package com.tailytask.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tailytask.app.notifications.NotificationHelper
import com.tailytask.app.ui.components.FastRecordBar
import com.tailytask.app.ui.navigation.Screen
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.tailytask.app.ui.screens.CalendarScreen
import com.tailytask.app.ui.screens.DashboardScreen
import com.tailytask.app.ui.screens.ProfileScreen
import com.tailytask.app.ui.screens.ProjectDetailScreen
import com.tailytask.app.ui.screens.ProjectsScreen
import com.tailytask.app.ui.screens.ShopScreen
import com.tailytask.app.ui.screens.TasksScreen
import com.tailytask.app.ui.theme.TailyTaskTheme
import com.tailytask.app.viewmodel.ProjectViewModel
import com.tailytask.app.viewmodel.TaskViewModel
import com.tailytask.app.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or not */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        // Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        enableEdgeToEdge()
        setContent {
            TailyTaskApp()
        }
    }
}

@Composable
fun TailyTaskApp() {
    val themeViewModel: ThemeViewModel = viewModel()
    val taskViewModel: TaskViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()
    val currentThemeId by themeViewModel.currentThemeId.collectAsState()

    LaunchedEffect(Unit) {
        themeViewModel.refreshPoints()
    }

    TailyTaskTheme(themeId = currentThemeId) {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Hide bottom bar on detail screens
        val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route } ||
                currentRoute == Screen.Profile.route

        var showFastRecord by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    TailyBottomBar(navController = navController)
                }
            },
            floatingActionButton = {
                if (showBottomBar) {
                    FloatingActionButton(
                        onClick = { showFastRecord = true },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "Fast Record")
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        taskViewModel = taskViewModel,
                        themeViewModel = themeViewModel,
                        projectViewModel = projectViewModel,
                        snackbarHostState = snackbarHostState,
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )
                }
                composable(Screen.Tasks.route) {
                    TasksScreen(taskViewModel = taskViewModel)
                }
                composable(Screen.Projects.route) {
                    ProjectsScreen(
                        projectViewModel = projectViewModel,
                        onProjectClick = { projectId ->
                            navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                        }
                    )
                }
                composable(Screen.Calendar.route) {
                    CalendarScreen(
                        taskViewModel = taskViewModel,
                        projectViewModel = projectViewModel
                    )
                }
                composable(Screen.Shop.route) {
                    themeViewModel.refreshPoints()
                    ShopScreen(
                        themeViewModel = themeViewModel,
                        snackbarHostState = snackbarHostState
                    )
                }
                composable(Screen.Profile.route) {
                    themeViewModel.refreshPoints()
                    ProfileScreen(
                        taskViewModel = taskViewModel,
                        themeViewModel = themeViewModel
                    )
                }
                composable(
                    route = Screen.ProjectDetail.route,
                    arguments = listOf(navArgument("projectId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0
                    ProjectDetailScreen(
                        projectId = projectId,
                        projectViewModel = projectViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            if (showFastRecord) {
                AlertDialog(
                    onDismissRequest = { showFastRecord = false },
                    title = { Text("Fast Record ✨", fontWeight = FontWeight.Bold) },
                    text = { 
                        FastRecordBar(onSubmit = { 
                            taskViewModel.fastRecord(it)
                            showFastRecord = false
                        }) 
                    },
                    confirmButton = {
                        TextButton(onClick = { showFastRecord = false }) {
                            Text("ปิด")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TailyBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .shadow(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Screen.bottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) screen.filledIcon else screen.outlinedIcon,
                        contentDescription = screen.label
                    )
                },
                label = {
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    }
}

package com.tailytask.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.tailytask.app.ui.navigation.Screen
import com.tailytask.app.ui.screens.*
import com.tailytask.app.ui.theme.TailyTaskTheme
import com.tailytask.app.viewmodel.ProjectViewModel
import com.tailytask.app.viewmodel.TaskViewModel
import com.tailytask.app.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        NotificationHelper.createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        setContent { TailyTaskApp() }
    }
}

@Composable
fun TailyTaskApp() {
    val themeViewModel: ThemeViewModel = viewModel()
    val taskViewModel: TaskViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()
    val currentThemeId by themeViewModel.currentThemeId.collectAsState()
    LaunchedEffect(Unit) { themeViewModel.refreshPoints() }

    TailyTaskTheme(themeId = currentThemeId) {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }
        var showFastRecord by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        taskViewModel = taskViewModel,
                        projectViewModel = projectViewModel,
                        themeViewModel = themeViewModel,
                        onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                        snackbarHostState = snackbarHostState
                    )
                }
                composable(Screen.Calendar.route) {
                    TasksScreen(
                        taskViewModel = taskViewModel,
                        projectViewModel = projectViewModel,
                        themeViewModel = themeViewModel,
                        onBack = { navController.popBackStack() },
                        snackbarHostState = snackbarHostState
                    )
                }
                composable(Screen.Shop.route) {
                    themeViewModel.refreshPoints()
                    ShopScreen(themeViewModel = themeViewModel, snackbarHostState = snackbarHostState)
                }
                composable(Screen.Profile.route) {
                    themeViewModel.refreshPoints()
                    ProfileScreen(
                        taskViewModel = taskViewModel,
                        themeViewModel = themeViewModel,
                        onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) }
                    )
                }
                composable(Screen.Projects.route) {
                    ProjectsScreen(
                        projectViewModel = projectViewModel,
                        onProjectClick = { projectId ->
                            navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                        }
                    )
                }
                composable(
                    route = Screen.ProjectDetail.route,
                    arguments = listOf(navArgument("projectId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0
                    ProjectDetailScreen(projectId = projectId, projectViewModel = projectViewModel,
                        onBack = { navController.popBackStack() })
                }
                composable(Screen.Analytics.route) {
                    AnalyticsScreen(taskViewModel = taskViewModel,
                        onBack = { navController.popBackStack() })
                }
            }

            SnackbarHost(snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp))

            if (showBottomBar) {
                TailyBottomBar(
                    navController = navController,
                    onFabClick = { showFastRecord = true },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            if (showFastRecord) {
                FastRecordDialog(
                    onDismiss = { showFastRecord = false },
                    onSubmit = { text, priority ->
                        taskViewModel.fastRecord(text, priority)
                        showFastRecord = false
                    }
                )
            }
        }
    }
}

@Composable
fun TailyBottomBar(
    navController: NavHostController,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val items = Screen.bottomNavItems
    val leftItems = items.take(2)
    val rightItems = items.drop(2)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 6.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                leftItems.forEach { screen ->
                    NavTabItem(screen, currentRoute == screen.route, Modifier.weight(1f)) {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                rightItems.forEach { screen ->
                    NavTabItem(screen, currentRoute == screen.route, Modifier.weight(1f)) {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .size(50.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(4.dp)
        ) {
            Icon(Icons.Filled.Add, "Fast Record", modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun NavTabItem(screen: Screen, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val color = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (isSelected) screen.filledIcon else screen.outlinedIcon,
            contentDescription = screen.label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            screen.label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}


package com.tailytask.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String = "",
    val filledIcon: ImageVector = Icons.Filled.Home,
    val outlinedIcon: ImageVector = Icons.Outlined.Home
) {
    // Bottom nav: Home | Projects | (+AI FAB) | Shop | Profile
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Projects : Screen("projects", "Projects", Icons.Filled.Folder, Icons.Outlined.Folder)
    object Shop : Screen("shop", "Shop", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)

    // Sub-screens (not in bottom nav)
    object Calendar : Screen("calendar")
    object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: Long) = "project_detail/$projectId"
    }
    object Analytics : Screen("analytics")

    companion object {
        // Left: Home, Projects | (FAB) | Right: Shop, Profile
        val bottomNavItems = listOf(Home, Projects, Shop, Profile)
    }
}

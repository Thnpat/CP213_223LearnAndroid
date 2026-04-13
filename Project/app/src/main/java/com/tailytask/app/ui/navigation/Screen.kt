package com.tailytask.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FolderCopy
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
) {
    data object Dashboard : Screen(
        route = "dashboard",
        label = "หน้าหลัก",
        filledIcon = Icons.Filled.Dashboard,
        outlinedIcon = Icons.Outlined.Dashboard
    )

    data object Tasks : Screen(
        route = "tasks",
        label = "งาน",
        filledIcon = Icons.Filled.TaskAlt,
        outlinedIcon = Icons.Outlined.TaskAlt
    )

    data object Projects : Screen(
        route = "projects",
        label = "โปรเจค",
        filledIcon = Icons.Filled.FolderCopy,
        outlinedIcon = Icons.Outlined.FolderCopy
    )

    data object Calendar : Screen(
        route = "calendar",
        label = "ปฏิทิน",
        filledIcon = Icons.Filled.CalendarMonth,
        outlinedIcon = Icons.Outlined.CalendarMonth
    )

    data object Shop : Screen(
        route = "shop",
        label = "ร้านค้า",
        filledIcon = Icons.Filled.ShoppingBag,
        outlinedIcon = Icons.Outlined.ShoppingBag
    )

    data object Profile : Screen(
        route = "profile",
        label = "โปรไฟล์",
        filledIcon = Icons.Filled.Person,
        outlinedIcon = Icons.Outlined.Person
    )

    data object ProjectDetail : Screen(
        route = "project_detail/{projectId}",
        label = "รายละเอียดโปรเจค",
        filledIcon = Icons.Filled.FolderCopy,
        outlinedIcon = Icons.Outlined.FolderCopy
    ) {
        fun createRoute(projectId: Long) = "project_detail/$projectId"
    }

    companion object {
        val bottomNavItems = listOf(Dashboard, Tasks, Projects, Calendar, Shop)
    }
}

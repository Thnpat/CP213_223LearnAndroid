package com.tailytask.app.model

import androidx.compose.ui.graphics.Color

// ===== Priority =====
enum class Priority(val label: String, val points: Int, val color: Long) {
    LOW("Low", 10, 0xFF81C784),
    MEDIUM("Medium", 20, 0xFFFFB74D),
    HIGH("High", 50, 0xFFE57373)
}

// ===== Category =====
enum class Category(val label: String, val icon: String) {
    WORK("Work", "work"),
    PERSONAL("Personal", "person"),
    SHOPPING("Shopping", "shopping_cart"),
    STUDY("Study", "school"),
    HEALTH("Health", "favorite"),
    OTHER("Other", "more_horiz")
}

// ===== App Theme =====
data class AppTheme(
    val id: String,
    val name: String,
    val price: Int,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onPrimary: Color = Color.White,
    val onBackground: Color,
    val onSurface: Color,
    val accent: Color
)

object ThemeStore {
    val themes = listOf(
        AppTheme(
            id = "sakura",
            name = "🌸 Sakura Pink",
            price = 0,
            primary = Color(0xFFF48FB1),
            secondary = Color(0xFFF8BBD0),
            tertiary = Color(0xFFCE93D8),
            background = Color(0xFFFFF0F5),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFFCE4EC),
            onBackground = Color(0xFF3E2723),
            onSurface = Color(0xFF4E342E),
            accent = Color(0xFFEC407A)
        ),
        AppTheme(
            id = "ocean",
            name = "🌊 Ocean Breeze",
            price = 100,
            primary = Color(0xFF81D4FA),
            secondary = Color(0xFFB3E5FC),
            tertiary = Color(0xFF80DEEA),
            background = Color(0xFFF0F8FF),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE1F5FE),
            onBackground = Color(0xFF1A237E),
            onSurface = Color(0xFF283593),
            accent = Color(0xFF29B6F6)
        ),
        AppTheme(
            id = "lavender",
            name = "💜 Lavender Dream",
            price = 150,
            primary = Color(0xFFCE93D8),
            secondary = Color(0xFFE1BEE7),
            tertiary = Color(0xFFB39DDB),
            background = Color(0xFFF5F0FF),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF3E5F5),
            onBackground = Color(0xFF311B92),
            onSurface = Color(0xFF4A148C),
            accent = Color(0xFFAB47BC)
        ),
        AppTheme(
            id = "mint",
            name = "🌿 Mint Garden",
            price = 200,
            primary = Color(0xFF80CBC4),
            secondary = Color(0xFFB2DFDB),
            tertiary = Color(0xFFA5D6A7),
            background = Color(0xFFF0FFF0),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE0F2F1),
            onBackground = Color(0xFF1B5E20),
            onSurface = Color(0xFF2E7D32),
            accent = Color(0xFF26A69A)
        ),
        AppTheme(
            id = "sunset",
            name = "🌅 Sunset Glow",
            price = 250,
            primary = Color(0xFFFFAB91),
            secondary = Color(0xFFFFCCBC),
            tertiary = Color(0xFFFFCC80),
            background = Color(0xFFFFF8F0),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFFBE9E7),
            onBackground = Color(0xFFBF360C),
            onSurface = Color(0xFFD84315),
            accent = Color(0xFFFF7043)
        ),
        AppTheme(
            id = "lemon",
            name = "🍋 Lemon Drop",
            price = 300,
            primary = Color(0xFFFFF176),
            secondary = Color(0xFFFFF9C4),
            tertiary = Color(0xFFDCE775),
            background = Color(0xFFFFFFF0),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFFFFDE7),
            onPrimary = Color(0xFF5D4037),
            onBackground = Color(0xFF5D4037),
            onSurface = Color(0xFF6D4C41),
            accent = Color(0xFFFDD835)
        )
    )

    fun getThemeById(id: String): AppTheme {
        return themes.find { it.id == id } ?: themes.first()
    }
}

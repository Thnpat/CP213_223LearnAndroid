package com.tailytask.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.tailytask.app.model.ThemeStore

class UserPrefsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("tailytask_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_TOTAL_POINTS = "total_points"
        private const val KEY_CURRENT_THEME = "current_theme"
        private const val KEY_OWNED_THEMES = "owned_themes"
    }

    // ===== User Name =====
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "TailyTask User") ?: "TailyTask User"

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    // ===== Points =====
    fun getTotalPoints(): Int = prefs.getInt(KEY_TOTAL_POINTS, 0)

    fun addPoints(points: Int) {
        val current = getTotalPoints()
        prefs.edit().putInt(KEY_TOTAL_POINTS, current + points).apply()
    }

    fun spendPoints(points: Int): Boolean {
        val current = getTotalPoints()
        if (current >= points) {
            prefs.edit().putInt(KEY_TOTAL_POINTS, current - points).apply()
            return true
        }
        return false
    }

    // ===== Theme =====
    fun getCurrentThemeId(): String = prefs.getString(KEY_CURRENT_THEME, "sakura") ?: "sakura"

    fun setCurrentTheme(themeId: String) {
        prefs.edit().putString(KEY_CURRENT_THEME, themeId).apply()
    }

    // ===== Owned Themes =====
    fun getOwnedThemes(): Set<String> {
        val defaultOwned = setOf("sakura")
        return prefs.getStringSet(KEY_OWNED_THEMES, defaultOwned) ?: defaultOwned
    }

    fun purchaseTheme(themeId: String): Boolean {
        val theme = ThemeStore.getThemeById(themeId)
        if (spendPoints(theme.price)) {
            val owned = getOwnedThemes().toMutableSet()
            owned.add(themeId)
            prefs.edit().putStringSet(KEY_OWNED_THEMES, owned).apply()
            return true
        }
        return false
    }

    fun isThemeOwned(themeId: String): Boolean = getOwnedThemes().contains(themeId)
}

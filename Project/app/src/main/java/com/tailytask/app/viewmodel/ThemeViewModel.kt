package com.tailytask.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tailytask.app.data.repository.UserPrefsRepository
import com.tailytask.app.model.ThemeStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPrefsRepository(application)

    private val _currentThemeId = MutableStateFlow(userPrefs.getCurrentThemeId())
    val currentThemeId: StateFlow<String> = _currentThemeId.asStateFlow()

    private val _ownedThemes = MutableStateFlow(userPrefs.getOwnedThemes())
    val ownedThemes: StateFlow<Set<String>> = _ownedThemes.asStateFlow()

    private val _userName = MutableStateFlow(userPrefs.getUserName())
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _totalPoints = MutableStateFlow(userPrefs.getTotalPoints())
    val totalPoints: StateFlow<Int> = _totalPoints.asStateFlow()

    private val _purchaseMessage = MutableStateFlow<String?>(null)
    val purchaseMessage: StateFlow<String?> = _purchaseMessage.asStateFlow()

    fun setTheme(themeId: String) {
        if (userPrefs.isThemeOwned(themeId)) {
            userPrefs.setCurrentTheme(themeId)
            _currentThemeId.value = themeId
        }
    }

    fun purchaseTheme(themeId: String) {
        val theme = ThemeStore.getThemeById(themeId)
        if (userPrefs.isThemeOwned(themeId)) {
            _purchaseMessage.value = "คุณมีธีมนี้แล้ว!"
            return
        }
        if (userPrefs.purchaseTheme(themeId)) {
            _ownedThemes.value = userPrefs.getOwnedThemes()
            _totalPoints.value = userPrefs.getTotalPoints()
            _purchaseMessage.value = "🎉 ซื้อธีม ${theme.name} สำเร็จ!"
        } else {
            _purchaseMessage.value = "❌ แต้มไม่เพียงพอ! ต้องการ ${theme.price} แต้ม"
        }
    }

    fun setUserName(name: String) {
        userPrefs.setUserName(name)
        _userName.value = name
    }

    fun refreshPoints() {
        _totalPoints.value = userPrefs.getTotalPoints()
    }

    fun clearPurchaseMessage() {
        _purchaseMessage.value = null
    }
}

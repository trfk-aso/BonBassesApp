package com.bonbasses.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonbasses.data.preferences.QuizPreferences
import com.bonbasses.data.repository.WritingHistoryRepository
import com.bonbasses.platform.FileExporter
import com.bonbasses.platform.iap.IAPManager
import com.bonbasses.platform.utils.DateFormatter
import com.bonbasses.platform.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

enum class ThemeMode {
    DARK, LIGHT, SYSTEM
}

data class SettingsUiState(
    val selectedTheme: ThemeMode = ThemeMode.DARK,
    val timerLength: Int = 7,
    val typingSoundsEnabled: Boolean = false,
    val gentleHapticsEnabled: Boolean = true,
    val showUpgradeDialog: Boolean = false,
    val showResetDialog: Boolean = false,
    val isTimer10MinPurchased: Boolean = false
)

class SettingsViewModel(
    private val quizPreferences: QuizPreferences? = null,
    private val historyRepository: WritingHistoryRepository? = null,
    private val fileExporter: FileExporter? = null,
    private val iapManager: IAPManager = IAPManager()
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()

        viewModelScope.launch {
            iapManager.purchaseState.collect { purchases ->
                val isPurchased = purchases["com.bonbasses.timer10"] == true
                _uiState.update { it.copy(isTimer10MinPurchased = isPurchased) }
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val gentleHaptics = quizPreferences?.getGentleHapticsEnabled() ?: true
            val typingSounds = quizPreferences?.getTypingSoundsEnabled() ?: false
            val savedTheme = quizPreferences?.getThemeMode() ?: "DARK"

            val themeMode = when (savedTheme) {
                "LIGHT" -> ThemeMode.LIGHT
                "SYSTEM" -> ThemeMode.SYSTEM
                else -> ThemeMode.DARK
            }


            val savedTimerLength = quizPreferences?.getTimerLength() ?: 7

            _uiState.update {
                it.copy(
                    gentleHapticsEnabled = gentleHaptics,
                    typingSoundsEnabled = typingSounds,
                    selectedTheme = themeMode,
                    timerLength = savedTimerLength
                )
            }
        }
    }

    fun selectTheme(theme: ThemeMode) {
        _uiState.update { it.copy(selectedTheme = theme) }

        viewModelScope.launch {
            quizPreferences?.setThemeMode(theme.name)
        }
    }

    fun toggleGentleHaptics() {
        val newValue = !_uiState.value.gentleHapticsEnabled
        _uiState.update { it.copy(gentleHapticsEnabled = newValue) }

        viewModelScope.launch {
            quizPreferences?.setGentleHapticsEnabled(newValue)
        }
    }

    fun toggleTypingSounds() {
        val newValue = !_uiState.value.typingSoundsEnabled
        _uiState.update { it.copy(typingSoundsEnabled = newValue) }

        viewModelScope.launch {
            quizPreferences?.setTypingSoundsEnabled(newValue)
        }
    }

    fun showUpgradeDialog() {
        viewModelScope.launch {

            val isPurchased = iapManager.purchaseState.value["com.bonbasses.timer10"] == true

            if (!isPurchased) {
                _uiState.update { it.copy(showUpgradeDialog = true) }
            } else {

                selectTimerLength(10)
            }
        }
    }

    fun selectTimerLength(minutes: Int) {

        if (_uiState.value.timerLength == minutes) {
            return
        }


        if (minutes == 10 && !_uiState.value.isTimer10MinPurchased) {
            _uiState.update { it.copy(showUpgradeDialog = true) }
            return
        }


        _uiState.update { it.copy(timerLength = minutes) }
        viewModelScope.launch {
            quizPreferences?.setTimerLength(minutes)
        }
    }

    fun dismissUpgradeDialog() {
        _uiState.update { it.copy(showUpgradeDialog = false) }
    }

    fun onTimerPurchased() {

        _uiState.update { it.copy(showUpgradeDialog = false, timerLength = 10, isTimer10MinPurchased = true) }
        viewModelScope.launch {
            quizPreferences?.setTimerLength(10)
        }
    }

    fun showResetDialog() {
        _uiState.update { it.copy(showResetDialog = true) }
    }

    fun dismissResetDialog() {
        _uiState.update { it.copy(showResetDialog = false) }
    }

    fun resetAll() {
        viewModelScope.launch {
            historyRepository?.deleteAllHistory()

            quizPreferences?.setThemeMode("DARK")
            quizPreferences?.setGentleHapticsEnabled(true)
            quizPreferences?.setTypingSoundsEnabled(false)
            quizPreferences?.setTimerLength(7)
            quizPreferences?.resetQuizProgress()
            quizPreferences?.clearSearchHistory()

            _uiState.update {
                SettingsUiState(
                    selectedTheme = ThemeMode.DARK,
                    timerLength = 7,
                    typingSoundsEnabled = false,
                    gentleHapticsEnabled = true,
                    showResetDialog = false,
                    isTimer10MinPurchased = _uiState.value.isTimer10MinPurchased
                )
            }
        }
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                val items = historyRepository?.getAllHistory()?.first() ?: emptyList()


                val csvContent = buildString {
                    appendLine("Date,Genre,Story,Words,Characters,Score,Favorite")
                    items.forEach { item ->
                        val date = DateFormatter.formatFullDateTime(item.createdAt)
                        val story = item.storyText.replace("\"", "\"\"")
                        appendLine("\"$date\",\"${item.genre}\",\"$story\",${item.wordCount},${item.charCount},${item.score ?: ""},${item.isFavorite}")
                    }
                }


                val filename = "bonbasses_export_${TimeUtils.currentTimeMillis()}.csv"
                fileExporter?.exportCsv(filename, csvContent)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

//class FakeIAPManager {
//    private val _purchaseState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
//    val purchaseState: StateFlow<Map<String, Boolean>> = _purchaseState
//
//    suspend fun getProducts(): List<String> = listOf("com.bonbasses.timer10")
//
//    suspend fun purchase(productId: String): PurchaseResult {
//        delay(1500)
//        _purchaseState.value = mapOf(productId to true)
//        return PurchaseResult.Success
//    }
//
//    suspend fun restorePurchases(): PurchaseResult {
//        delay(500)
//        return PurchaseResult.Success
//    }
//
//    fun isPurchased(productId: String): Boolean {
//        return _purchaseState.value[productId] == true
//    }
//}
//
//sealed class PurchaseResult {
//    object Success : PurchaseResult()
//    data class Error(val message: String) : PurchaseResult()
//    object Cancelled : PurchaseResult()
//}
//
//class SettingsViewModel(
//    private val quizPreferences: QuizPreferences? = null,
//    private val historyRepository: WritingHistoryRepository? = null,
//    private val fileExporter: FileExporter? = null,
//    private val iapManager: FakeIAPManager = FakeIAPManager()
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(SettingsUiState())
//    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
//
//    init {
//        loadSettings()
//
//        viewModelScope.launch {
//            iapManager.purchaseState.collect { purchases ->
//                val isPurchased = purchases["com.bonbasses.timer10"] == true
//                _uiState.update { it.copy(isTimer10MinPurchased = isPurchased) }
//            }
//        }
//    }
//
//    private fun loadSettings() {
//        viewModelScope.launch {
//            val gentleHaptics = quizPreferences?.getGentleHapticsEnabled() ?: true
//            val typingSounds = quizPreferences?.getTypingSoundsEnabled() ?: false
//            val savedTheme = quizPreferences?.getThemeMode() ?: "DARK"
//
//            val themeMode = when (savedTheme) {
//                "LIGHT" -> ThemeMode.LIGHT
//                "SYSTEM" -> ThemeMode.SYSTEM
//                else -> ThemeMode.DARK
//            }
//
//            val savedTimerLength = quizPreferences?.getTimerLength() ?: 7
//
//            _uiState.update {
//                it.copy(
//                    gentleHapticsEnabled = gentleHaptics,
//                    typingSoundsEnabled = typingSounds,
//                    selectedTheme = themeMode,
//                    timerLength = savedTimerLength
//                )
//            }
//        }
//    }
//
//    fun selectTheme(theme: ThemeMode) {
//        _uiState.update { it.copy(selectedTheme = theme) }
//        viewModelScope.launch { quizPreferences?.setThemeMode(theme.name) }
//    }
//
//    fun toggleGentleHaptics() {
//        val newValue = !_uiState.value.gentleHapticsEnabled
//        _uiState.update { it.copy(gentleHapticsEnabled = newValue) }
//        viewModelScope.launch { quizPreferences?.setGentleHapticsEnabled(newValue) }
//    }
//
//    fun toggleTypingSounds() {
//        val newValue = !_uiState.value.typingSoundsEnabled
//        _uiState.update { it.copy(typingSoundsEnabled = newValue) }
//        viewModelScope.launch { quizPreferences?.setTypingSoundsEnabled(newValue) }
//    }
//
//    fun showUpgradeDialog() {
//        viewModelScope.launch {
//            val isPurchased = iapManager.purchaseState.value["com.bonbasses.timer10"] == true
//            if (!isPurchased) {
//                _uiState.update { it.copy(showUpgradeDialog = true) }
//            } else {
//                selectTimerLength(10)
//            }
//        }
//    }
//
//    fun selectTimerLength(minutes: Int) {
//        if (_uiState.value.timerLength == minutes) return
//
//        if (minutes == 10 && !_uiState.value.isTimer10MinPurchased) {
//            _uiState.update { it.copy(showUpgradeDialog = true) }
//            return
//        }
//
//        _uiState.update { it.copy(timerLength = minutes) }
//        viewModelScope.launch { quizPreferences?.setTimerLength(minutes) }
//    }
//
//    fun dismissUpgradeDialog() {
//        _uiState.update { it.copy(showUpgradeDialog = false) }
//    }
//
//    fun onTimerPurchased() {
//        _uiState.update {
//            it.copy(showUpgradeDialog = false, timerLength = 10, isTimer10MinPurchased = true)
//        }
//        viewModelScope.launch { quizPreferences?.setTimerLength(10) }
//    }
//
//    fun showResetDialog() {
//        _uiState.update { it.copy(showResetDialog = true) }
//    }
//
//    fun dismissResetDialog() {
//        _uiState.update { it.copy(showResetDialog = false) }
//    }
//
//    fun resetAll() {
//        viewModelScope.launch {
//            historyRepository?.deleteAllHistory()
//
//            quizPreferences?.setThemeMode("DARK")
//            quizPreferences?.setGentleHapticsEnabled(true)
//            quizPreferences?.setTypingSoundsEnabled(false)
//            quizPreferences?.setTimerLength(7)
//            quizPreferences?.resetQuizProgress()
//            quizPreferences?.clearSearchHistory()
//
//            _uiState.update {
//                SettingsUiState(
//                    selectedTheme = ThemeMode.DARK,
//                    timerLength = 7,
//                    typingSoundsEnabled = false,
//                    gentleHapticsEnabled = true,
//                    showResetDialog = false,
//                    isTimer10MinPurchased = _uiState.value.isTimer10MinPurchased
//                )
//            }
//        }
//    }
//
//    fun exportData() {
//        viewModelScope.launch {
//            try {
//                val items = historyRepository?.getAllHistory()?.first() ?: emptyList()
//
//                val csvContent = buildString {
//                    appendLine("Date,Genre,Story,Words,Characters,Score,Favorite")
//                    items.forEach { item ->
//                        val date = DateFormatter.formatFullDateTime(item.createdAt)
//                        val story = item.storyText.replace("\"", "\"\"")
//                        appendLine("\"$date\",\"${item.genre}\",\"$story\",${item.wordCount},${item.charCount},${item.score ?: ""},${item.isFavorite}")
//                    }
//                }
//
//                val filename = "bonbasses_export_${TimeUtils.currentTimeMillis()}.csv"
//                fileExporter?.exportCsv(filename, csvContent)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}
//

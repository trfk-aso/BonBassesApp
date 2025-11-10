package com.bonbasses.ui.screens.writing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonbasses.data.preferences.QuizPreferences
import com.bonbasses.platform.TypingSoundPlayer
import com.bonbasses.platform.iap.IAPManager
import com.bonbasses.platform.iap.IAPProducts
import com.bonbasses.platform.utils.DateFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TimerHintsData(
    val hints: List<String>
)

data class WritingUiState(
    val words: List<String> = emptyList(),
    val storyText: String = "",
    val charCount: Int = 0,
    val wordCount: Int = 0,
    val timeLeftSeconds: Int = 420,
    val selectedCanvas: CanvasMode = CanvasMode.CLASSIC,
    val isTimerRunning: Boolean = false,
    val isChecking: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val showTimeUpToast: Boolean = false,
    val showExitDialog: Boolean = false,
    val showPaywallDialog: Boolean = false,
    val lockedCanvasProductId: String? = null,
    val timerHint: String? = null,
    val shouldTriggerHaptic: Boolean = false
)

enum class CanvasMode {
    CLASSIC,
    TYPEWRITER,
    FOCUS
}

class WritingViewModel(
    private val promptWords: List<String> = emptyList(),
    initialText: String = "",
    initialTimeLeft: Int = 420,
    private val timerDurationMinutes: Int = 7,
    private val quizPreferences: QuizPreferences? = null,
    private val iapManager: IAPManager = IAPManager()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(
        WritingUiState(
            words = promptWords,
            storyText = initialText,
            charCount = initialText.length,
            wordCount = if (initialText.isBlank()) 0 else initialText.trim().split("\\s+".toRegex()).size,
            timeLeftSeconds = if (initialTimeLeft == 420) timerDurationMinutes * 60 else initialTimeLeft
        )
    )
    val uiState: StateFlow<WritingUiState> = _uiState.asStateFlow()
    
    private var timerJob: Job? = null
    private var autosaveJob: Job? = null
    private val json = Json { ignoreUnknownKeys = true }
    

    private var userPace: String = "Steady"
    private var userPressure: String = "Yes"
    private var gentleHapticsEnabled: Boolean = true
    private var typingSoundsEnabled: Boolean = false
    private var timerHints: List<String> = emptyList()
    private var shownHintIndexes = mutableSetOf<Int>()
    
    init {
        loadQuizPreferences()
        loadSavedCanvas()
        startTimer()
        startAutosave()
    }
    
    private fun loadQuizPreferences() {
        viewModelScope.launch {
            quizPreferences?.let { prefs ->
                userPace = prefs.getAnswer(1) ?: "Steady"
                userPressure = prefs.getAnswer(2) ?: "Yes"
                gentleHapticsEnabled = prefs.getGentleHapticsEnabled()
                typingSoundsEnabled = prefs.getTypingSoundsEnabled()
            }
        }
    }
    
    private fun loadSavedCanvas() {
        viewModelScope.launch {
            quizPreferences?.let { prefs ->
                val savedCanvas = prefs.getSelectedCanvas()
                val canvasMode = when (savedCanvas) {
                    "TYPEWRITER" -> CanvasMode.TYPEWRITER
                    "FOCUS" -> CanvasMode.FOCUS
                    else -> CanvasMode.CLASSIC
                }
                _uiState.update { it.copy(selectedCanvas = canvasMode) }
            }
        }
    }
    
    private fun getTimingForPace(): Pair<Int, Int> {
        return when (userPace) {
            "Fast" -> Pair(420, 210)
            "Unhurried" -> Pair(300, 30)
            else -> Pair(420, 60)
        }
    }
    
    private fun shouldShowHintAtTime(currentTime: Int): Boolean {
        val (firstHint, secondHint) = getTimingForPace()
        return currentTime == firstHint || currentTime == secondHint
    }
    
    private fun getRandomHint(): String {
        if (timerHints.isEmpty()) return ""
        
        if (shownHintIndexes.size >= timerHints.size) {
            shownHintIndexes.clear()
        }
        
        val availableIndexes = timerHints.indices.filter { it !in shownHintIndexes }
        if (availableIndexes.isEmpty()) return timerHints.random()
        
        val selectedIndex = availableIndexes.random()
        shownHintIndexes.add(selectedIndex)
        return timerHints[selectedIndex]
    }
    
    private fun shouldTriggerHaptic(currentTime: Int): Boolean {

        if (!gentleHapticsEnabled) {
            return false
        }
        

        return currentTime == 60 || currentTime == 0
    }
    
    fun onTextChanged(text: String) {
        val previousLength = _uiState.value.storyText.length
        val charCount = text.length
        val wordCount = if (text.isBlank()) 0 else text.trim().split("\\s+".toRegex()).size
        
        if (charCount > previousLength) {
            if (typingSoundsEnabled) {
                TypingSoundPlayer.playTypingSound()
            }
        }
        
        _uiState.update {
            it.copy(
                storyText = text,
                charCount = charCount,
                wordCount = wordCount,
                hasUnsavedChanges = true
            )
        }
    }
    
    fun selectCanvas(mode: CanvasMode) {
        viewModelScope.launch {

            val canvasProductId = when (mode) {
                CanvasMode.TYPEWRITER, CanvasMode.FOCUS -> IAPProducts.CANVAS_PACK
                CanvasMode.CLASSIC -> null
            }
            

            if (canvasProductId != null && !iapManager.isPurchased(canvasProductId)) {
                _uiState.update { it.copy(showPaywallDialog = true, lockedCanvasProductId = canvasProductId) }
            } else {

                _uiState.update { it.copy(selectedCanvas = mode) }
                saveCanvas(mode)
            }
        }
    }
    
    private fun saveCanvas(mode: CanvasMode) {
        viewModelScope.launch {
            quizPreferences?.setSelectedCanvas(mode.name)
        }
    }
    
    fun dismissPaywallDialog() {
        _uiState.update { it.copy(showPaywallDialog = false, lockedCanvasProductId = null) }
    }
    
    fun onCheckClicked() {
        _uiState.update { it.copy(isChecking = true) }
        stopTimer()
    }
    
    fun onBackClicked(onExit: () -> Unit) {
        if (_uiState.value.storyText.isNotBlank()) {
            _uiState.update { it.copy(showExitDialog = true) }
        } else {
            resetState()
            onExit()
        }
    }
    
    fun dismissExitDialog() {
        _uiState.update { it.copy(showExitDialog = false) }
    }
    
    fun confirmExit(): Boolean {
        resetState()
        return true
    }
    
    fun resetState() {
        timerJob?.cancel()
        autosaveJob?.cancel()
        _uiState.update {
            WritingUiState(
                words = emptyList(),
                timeLeftSeconds = timerDurationMinutes * 60
            )
        }
    }
    
    fun dismissTimeUpToast() {
        _uiState.update { it.copy(showTimeUpToast = false) }
    }
    
    fun dismissTimerHint() {
        _uiState.update { it.copy(timerHint = null) }
    }
    
    fun acknowledgeHaptic() {
        _uiState.update { it.copy(shouldTriggerHaptic = false) }
    }
    
    fun setTimerHints(jsonString: String) {
        if (jsonString.isNotBlank()) {
            try {
                val data = json.decodeFromString<TimerHintsData>(jsonString)
                timerHints = data.hints
            } catch (e: Exception) {
                timerHints = emptyList()
            }
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = true) }
        
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeftSeconds > 0) {
                delay(1000)
                val newTime = _uiState.value.timeLeftSeconds - 1
                
                val shouldShowHint = shouldShowHintAtTime(newTime)
                val hint = if (shouldShowHint) {
                    val randomHint = getRandomHint()
                    if (randomHint.isNotBlank()) randomHint else null
                } else null
                
                val triggerHaptic = shouldTriggerHaptic(newTime)
                
                _uiState.update { 
                    it.copy(
                        timeLeftSeconds = newTime,
                        timerHint = hint,
                        shouldTriggerHaptic = triggerHaptic
                    )
                }
                
                if (newTime == 0) {
                    _uiState.update { 
                        it.copy(
                            isTimerRunning = false,
                            showTimeUpToast = true
                        )
                    }
                }
            }
        }
    }
    
    private fun stopTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = false) }
    }
    
    private fun startAutosave() {
        autosaveJob?.cancel()
        
        autosaveJob = viewModelScope.launch {
            while (true) {
                delay(10000)
                if (_uiState.value.hasUnsavedChanges) {
                    _uiState.update { it.copy(hasUnsavedChanges = false) }
                }
            }
        }
    }
    
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        autosaveJob?.cancel()
    }
}

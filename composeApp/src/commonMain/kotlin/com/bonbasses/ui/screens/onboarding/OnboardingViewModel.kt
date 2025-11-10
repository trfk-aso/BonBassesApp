package com.bonbasses.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonbasses.data.models.QuizOption
import com.bonbasses.data.models.QuizQuestion
import com.bonbasses.data.preferences.QuizPreferences
import com.bonbasses.platform.iap.IAPManager
import com.bonbasses.platform.iap.IAPProducts
import com.bonbasses.platform.iap.PurchaseResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val quizPreferences: QuizPreferences? = null,
    private val iapManager: IAPManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    init {
        loadQuestions()
    }
    
    private fun loadQuestions() {
        val questions = listOf(
            QuizQuestion(
                id = 1,
                question = "What's your natural pace?",
                options = listOf(
                    QuizOption("Fast"),
                    QuizOption("Steady"),
                    QuizOption("Unhurried")
                )
            ),
            QuizQuestion(
                id = 2,
                question = "Do you like a little deadline pressure?",
                options = listOf(
                    QuizOption("Yes"),
                    QuizOption("Not really"),
                    QuizOption("Surprise me")
                )
            ),
            QuizQuestion(
                id = 3,
                question = "Which page feels better?",
                options = listOf(
                    QuizOption("Classic", isLocked = false),
                    QuizOption("Typewriter", isLocked = true, backgroundImage = "bg_typewriter"),
                    QuizOption("Focus", isLocked = true, backgroundImage = "bg_focus")
                )
            ),
            QuizQuestion(
                id = 4,
                question = "What feedback tone do you prefer?",
                options = listOf(
                    QuizOption("Mostly praise"),
                    QuizOption("Balanced"),
                    QuizOption("More hints")
                )
            ),
            QuizQuestion(
                id = 5,
                question = "Pick 2 favorite genres",
                options = listOf(
                    QuizOption("Adventure"),
                    QuizOption("Sci-Fi"),
                    QuizOption("Slice of Life"),
                    QuizOption("Mystery"),
                    QuizOption("Romance"),
                    QuizOption("Fantasy")
                ),
                isMultiSelect = true,
                minSelections = 2,
                maxSelections = 2
            ),
            QuizQuestion(
                id = 6,
                question = "How do you prefer to start writing?",
                options = listOf(
                    QuizOption("With a prompt"),
                    QuizOption("From scratch"),
                    QuizOption("Continue yesterday's story")
                )
            ),
            QuizQuestion(
                id = 7,
                question = "Do you prefer writing sessions to be?",
                options = listOf(
                    QuizOption("Short & frequent"),
                    QuizOption("Long & deep"),
                    QuizOption("Flexible")
                )
            ),
            QuizQuestion(
                id = 8,
                question = "What motivates you most to write?",
                options = listOf(
                    QuizOption("Personal expression"),
                    QuizOption("Building habits"),
                    QuizOption("Sharing with others")
                )
            )
        )
        
        _uiState.update { it.copy(questions = questions) }
    }
    
    fun selectAnswer(answer: String, isLocked: Boolean = false) {
        if (isLocked) {
            _uiState.update { it.copy(showPremiumAlert = true, pendingLockedOption = answer) }
            return
        }
        
        val currentQuestion = _uiState.value.currentQuestion ?: return
        
        if (currentQuestion.isMultiSelect) {
            val currentSelections = _uiState.value.multiSelectAnswers[currentQuestion.id] ?: emptyList()
            val newSelections = if (currentSelections.contains(answer)) {
                currentSelections - answer
            } else {
                if (currentSelections.size < currentQuestion.maxSelections) {
                    currentSelections + answer
                } else {
                    currentSelections
                }
            }
            
            _uiState.update { state ->
                state.copy(
                    multiSelectAnswers = state.multiSelectAnswers + (currentQuestion.id to newSelections)
                )
            }
        } else {
            _uiState.update { state ->
                state.copy(
                    selectedAnswers = state.selectedAnswers + (currentQuestion.id to answer)
                )
            }
        }
    }
    
    fun dismissPremiumAlert() {
        _uiState.update { it.copy(showPremiumAlert = false, pendingLockedOption = null) }
    }
    
    fun dismissPersonalizationAlert() {
        _uiState.update { it.copy(showPersonalizationAlert = false, isCompleted = true) }
    }
    
    fun unlockPremiumLater() {
        dismissPremiumAlert()
    }

    fun purchasePremium() {
        viewModelScope.launch {
            dismissPremiumAlert()

            _uiState.update { it.copy(isPurchasing = true, showErrorMessage = null) }

            val result = iapManager.purchase(IAPProducts.CANVAS_PACK)

            _uiState.update { it.copy(isPurchasing = false) }

            when (result) {
                is PurchaseResult.Success -> {
                    _uiState.update {
                        it.copy(
                            showPremiumAlert = false,
                            showErrorMessage = null
                        )
                    }
                }

                is PurchaseResult.Error -> {
                    _uiState.update {
                        it.copy(
                            showPremiumAlert = false,
                            showErrorMessage = "Purchase failed: ${result.message}"
                        )
                    }
                }

                is PurchaseResult.Cancelled -> {
                    _uiState.update {
                        it.copy(
                            showPremiumAlert = false,
                            showErrorMessage = "Purchase was cancelled"
                        )
                    }
                }
            }
        }
    }

    fun nextQuestion() {
        _uiState.update { state ->
            if (state.isLastQuestion) {
                state.copy(showPersonalizationAlert = true)
            } else {
                state.copy(currentQuestionIndex = state.currentQuestionIndex + 1)
            }
        }
    }
    
    fun skipQuiz() {
        viewModelScope.launch {
            val defaultAnswers = mapOf(
                1 to "Steady",
                2 to "Yes",
                3 to "Classic",
                4 to "Balanced",
                6 to "With a prompt",
                7 to "Flexible",
                8 to "Personal expression"
            )
            
            val defaultMultiSelect = mapOf(
                5 to listOf("Adventure", "Mystery")
            )
            
            _uiState.update { 
                it.copy(
                    selectedAnswers = defaultAnswers,
                    multiSelectAnswers = defaultMultiSelect,
                    isCompleted = true
                )
            }
            
            saveAnswers()
        }
    }
    
    suspend fun saveAnswers() {
        quizPreferences?.let { prefs ->
            _uiState.value.selectedAnswers.forEach { (questionId, answer) ->
                prefs.saveAnswer(questionId, answer)
            }
            
            _uiState.value.multiSelectAnswers.forEach { (questionId, answers) ->
                prefs.saveAnswer(questionId, answers.joinToString(","))
            }
            
            prefs.setQuizCompleted(true)
        }
    }
}

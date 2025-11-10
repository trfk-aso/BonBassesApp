package com.bonbasses.ui.screens.onboarding

import com.bonbasses.data.models.QuizQuestion

data class OnboardingUiState(
    val currentQuestionIndex: Int = 0,
    val questions: List<QuizQuestion> = emptyList(),
    val selectedAnswers: Map<Int, String> = emptyMap(),
    val multiSelectAnswers: Map<Int, List<String>> = emptyMap(),
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val showPremiumAlert: Boolean = false,
    val showPersonalizationAlert: Boolean = false,
    val pendingLockedOption: String? = null,
    val showErrorMessage: String? = null,

    val isPurchasing: Boolean = false
) {
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val progress: String
        get() = "${currentQuestionIndex + 1}/${questions.size}"

    val canProceed: Boolean
        get() {
            val question = currentQuestion ?: return false
            return if (question.isMultiSelect) {
                val selections = multiSelectAnswers[question.id]?.size ?: 0
                selections >= question.minSelections
            } else {
                selectedAnswers.containsKey(question.id)
            }
        }

    val isLastQuestion: Boolean
        get() = currentQuestionIndex == questions.size - 1
}


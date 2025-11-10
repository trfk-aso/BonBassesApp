package com.bonbasses.ui.screens.result

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.random.Random

@Serializable
data class RecommendationsData(
    val recommendations: List<String>
)

@Serializable
data class FeedbackData(
    val praise: List<String>,
    val hints: List<String>
)

data class ResultUiState(
    val isLoading: Boolean = true,
    val isFailed: Boolean = false,
    val score: Int = 0,
    val recommendations: List<String> = emptyList(),
    val feedback: List<String> = emptyList(),
    val isFavorite: Boolean = false
)

class ResultViewModel(
    private val storyText: String,
    private val charCount: Int,
    private val quizPreferences: com.bonbasses.data.preferences.QuizPreferences? = null
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()
    
    private val json = Json { ignoreUnknownKeys = true }
    
    fun loadResult(recommendationsJson: String, feedbackJson: String, userTone: String = "Balanced") {
        val isFailed = charCount < 300
        
        if (isFailed) {
            val recommendationsData = json.decodeFromString<RecommendationsData>(recommendationsJson)
            val selectedRecommendations = recommendationsData.recommendations
                .shuffled()
                .take(5)
            
            _uiState.update {
                it.copy(
                    isFailed = true,
                    recommendations = selectedRecommendations
                )
            }
        } else {
            val feedbackData = json.decodeFromString<FeedbackData>(feedbackJson)
            
            val score = when (Random.nextInt(100)) {
                in 0..14 -> 3
                in 15..69 -> 4
                else -> 5
            }
            

            val (praisePercent, hintsPercent) = when (userTone) {
                "Mostly praise" -> Pair(0.8, 0.2)
                "More hints" -> Pair(0.3, 0.7)
                else -> Pair(0.5, 0.5)
            }
            
            val totalItems = Random.nextInt(6, 8)
            val praiseCount = (totalItems * praisePercent).toInt()
            val hintsCount = totalItems - praiseCount
            
            val selectedPraise = feedbackData.praise.shuffled().take(praiseCount)
            val selectedHints = feedbackData.hints.shuffled().take(hintsCount)
            
            val combinedFeedback = (selectedPraise + selectedHints).shuffled()
            
            _uiState.update {
                it.copy(
                    isFailed = false,
                    score = score,
                    feedback = combinedFeedback
                )
            }
        }
    }
    
    fun toggleFavorite() {
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
    }
    
    fun completeLoading() {
        _uiState.update { it.copy(isLoading = false) }
    }
}

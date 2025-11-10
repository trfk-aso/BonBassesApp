package com.bonbasses.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonbasses.data.models.WritingHistoryItem
import com.bonbasses.data.repository.WritingHistoryRepository
import com.bonbasses.platform.utils.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class StatsPeriod {
    WEEK, MONTH, ALL
}

data class StatsUiState(
    val allItems: List<WritingHistoryItem> = emptyList(),
    val selectedPeriod: StatsPeriod = StatsPeriod.WEEK,
    val total: Int = 0,
    val last7: Int = 0,
    val last30: Int = 0,
    val avgLength: String = "Medium",
    val avgScore: Double = 0.0,
    val streak: Int = 0,
    val hasData: Boolean = false
)

class StatsViewModel(
    private val repository: WritingHistoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()
    
    init {
        loadStats()
    }
    
    private fun loadStats() {
        viewModelScope.launch {
            repository.getAllHistory().collect { items ->
                val now = TimeUtils.currentTimeMillis()
                val last7Items = items.filter { 
                    (now - it.createdAt) <= 7L * 24 * 60 * 60 * 1000 
                }
                val last30Items = items.filter { 
                    (now - it.createdAt) <= 30L * 24 * 60 * 60 * 1000 
                }
                
                val avgLength = when (_uiState.value.selectedPeriod) {
                    StatsPeriod.WEEK -> calculateAvgLength(last7Items)
                    StatsPeriod.MONTH -> calculateAvgLength(last30Items)
                    StatsPeriod.ALL -> calculateAvgLength(items)
                }
                
                val avgScore = when (_uiState.value.selectedPeriod) {
                    StatsPeriod.WEEK -> calculateAvgScore(last7Items)
                    StatsPeriod.MONTH -> calculateAvgScore(last30Items)
                    StatsPeriod.ALL -> calculateAvgScore(items)
                }
                
                val streak = calculateStreak(items)
                
                _uiState.update { 
                    it.copy(
                        allItems = items,
                        total = items.size,
                        last7 = last7Items.size,
                        last30 = last30Items.size,
                        avgLength = avgLength,
                        avgScore = avgScore,
                        streak = streak,
                        hasData = items.isNotEmpty()
                    )
                }
            }
        }
    }
    
    fun selectPeriod(period: StatsPeriod) {
        _uiState.update { currentState ->
            val now = TimeUtils.currentTimeMillis()
            val last7Items = currentState.allItems.filter { 
                (now - it.createdAt) <= 7L * 24 * 60 * 60 * 1000 
            }
            val last30Items = currentState.allItems.filter { 
                (now - it.createdAt) <= 30L * 24 * 60 * 60 * 1000 
            }
            
            val avgLength = when (period) {
                StatsPeriod.WEEK -> calculateAvgLength(last7Items)
                StatsPeriod.MONTH -> calculateAvgLength(last30Items)
                StatsPeriod.ALL -> calculateAvgLength(currentState.allItems)
            }
            
            val avgScore = when (period) {
                StatsPeriod.WEEK -> calculateAvgScore(last7Items)
                StatsPeriod.MONTH -> calculateAvgScore(last30Items)
                StatsPeriod.ALL -> calculateAvgScore(currentState.allItems)
            }
            
            currentState.copy(
                selectedPeriod = period,
                avgLength = avgLength,
                avgScore = avgScore
            )
        }
    }
    
    private fun calculateAvgLength(items: List<WritingHistoryItem>): String {
        if (items.isEmpty()) return "Medium"
        
        val avgChars = items.map { it.charCount }.average()
        return when {
            avgChars < 300 -> "Short"
            avgChars < 600 -> "Medium"
            else -> "Long"
        }
    }
    
    private fun calculateAvgScore(items: List<WritingHistoryItem>): Double {
        val scoredItems = items.filter { it.score != null }
        if (scoredItems.isEmpty()) return 0.0
        
        return scoredItems.mapNotNull { it.score }.average()
    }
    
    private fun calculateStreak(items: List<WritingHistoryItem>): Int {
        if (items.isEmpty()) return 0
        
        val sortedItems = items.sortedByDescending { it.createdAt }
        
        val today = TimeUtils.getStartOfDay(TimeUtils.currentTimeMillis())
        val yesterday = today - 24 * 60 * 60 * 1000
        

        val itemsByDay = sortedItems.groupBy { item ->
            TimeUtils.getStartOfDay(item.createdAt)
        }
        

        if (!itemsByDay.containsKey(today) && !itemsByDay.containsKey(yesterday)) {
            return 0
        }
        

        var currentDay = if (itemsByDay.containsKey(today)) today else yesterday
        var streak = 0
        

        while (itemsByDay.containsKey(currentDay)) {
            streak++
            currentDay -= 24 * 60 * 60 * 1000
        }
        
        return streak
    }
}

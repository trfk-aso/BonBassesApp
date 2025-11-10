package com.bonbasses.ui.screens.history

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

data class HistoryUiState(
    val historyItems: List<WritingHistoryItem> = emptyList(),
    val filteredItems: List<WritingHistoryItem> = emptyList(),
    val selectedItems: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val filterGenre: String? = null,
    val currentFilter: FilterState = FilterState()
)

class HistoryViewModel(
    private val repository: WritingHistoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    init {
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val flow = if (_uiState.value.filterGenre != null) {
                repository.getHistoryByGenre(_uiState.value.filterGenre!!)
            } else {
                repository.getAllHistory()
            }
            
            flow.collect { items ->
                _uiState.update { 
                    it.copy(
                        historyItems = items,
                        filteredItems = applyFilters(items, it.currentFilter),
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(id)
        }
    }
    
    fun updateTitle(id: Long, newTitle: String) {
        viewModelScope.launch {
            repository.updateTitle(id, newTitle)
        }
    }
    
    fun toggleItemSelection(id: Long) {
        _uiState.update { state ->
            val newSelection = if (id in state.selectedItems) {
                state.selectedItems - id
            } else {
                state.selectedItems + id
            }
            state.copy(selectedItems = newSelection)
        }
    }
    
    fun deleteSelectedItems() {
        viewModelScope.launch {
            _uiState.value.selectedItems.forEach { id ->
                repository.deleteHistory(id)
            }
            _uiState.update { it.copy(selectedItems = emptySet()) }
        }
    }
    
    fun deleteAllHistory() {
        viewModelScope.launch {
            repository.deleteAllHistory()
            _uiState.update { it.copy(selectedItems = emptySet()) }
        }
    }
    
    fun filterByGenre(genre: String?) {
        _uiState.update { it.copy(filterGenre = genre) }
        loadHistory()
    }
    
    fun applyFilter(filterState: FilterState) {
        _uiState.update { 
            it.copy(
                currentFilter = filterState,
                filteredItems = applyFilters(it.historyItems, filterState)
            )
        }
    }
    
    fun resetFilter() {
        _uiState.update { 
            it.copy(
                currentFilter = FilterState(),
                filteredItems = it.historyItems
            )
        }
    }
    
    private fun applyFilters(items: List<WritingHistoryItem>, filter: FilterState): List<WritingHistoryItem> {
        return items.filter { item ->
            val lengthMatch = when (filter.selectedLength) {
                "Short" -> item.charCount < 300
                "Medium" -> item.charCount in 300..599
                "Long" -> item.charCount >= 600
                null -> true
                else -> true
            }
            
            val categoryMatch = if (filter.selectedCategories.isEmpty()) {
                true
            } else {
                item.genre in filter.selectedCategories
            }
            
            val scoreMatch = if (filter.selectedScores.isEmpty()) {
                true
            } else {
                item.score in filter.selectedScores
            }
            
            val dateMatch = if (filter.dateFrom.isNotEmpty() || filter.dateTo.isNotEmpty()) {
                try {
                    val itemDate = item.createdAt
                    
                    val fromMatch = if (filter.dateFrom.isNotEmpty()) {
                        val fromMillis = parseFilterDateToMillis(filter.dateFrom)
                        itemDate >= fromMillis
                    } else true
                    
                    val toMatch = if (filter.dateTo.isNotEmpty()) {
                        val toMillis = parseFilterDateToMillis(filter.dateTo)
                        itemDate <= (toMillis + 86400000)
                    } else true
                    
                    fromMatch && toMatch
                } catch (e: Exception) {
                    true
                }
            } else {
                true
            }
            
            lengthMatch && categoryMatch && scoreMatch && dateMatch
        }
    }
    
    private fun parseFilterDateToMillis(dateStr: String): Long {

        return TimeUtils.parseMMDDYY(dateStr) ?: 0
    }
    
    fun exportAsCsv(): String {
        val items = _uiState.value.selectedItems.takeIf { it.isNotEmpty() }
            ?.mapNotNull { id -> _uiState.value.historyItems.find { it.id == id } }
            ?: _uiState.value.historyItems
        
        val header = "ID,Date,Genre,Words,Story Text,Character Count,Word Count,Score,Is Favorite\n"
        val rows = items.joinToString("\n") { item ->
            "${item.id},${item.createdAt},${item.genre},\"${item.words.joinToString(", ")}\",\"${item.storyText.replace("\"", "\"\"")}\",${item.charCount},${item.wordCount},${item.score ?: ""},${item.isFavorite}"
        }
        
        return header + rows
    }
    
    fun exportAsTxt(): String {
        val items = _uiState.value.selectedItems.takeIf { it.isNotEmpty() }
            ?.mapNotNull { id -> _uiState.value.historyItems.find { it.id == id } }
            ?: _uiState.value.historyItems
        
        return items.joinToString("\n\n${"=".repeat(50)}\n\n") { item ->
            """
            |Date: ${item.createdAt}
            |Genre: ${item.genre}
            |Words: ${item.words.joinToString(", ")}
            |Score: ${item.score?.let { "$it/5 stars" } ?: "Draft"}
            |
            |${item.storyText}
            """.trimMargin()
        }
    }
}

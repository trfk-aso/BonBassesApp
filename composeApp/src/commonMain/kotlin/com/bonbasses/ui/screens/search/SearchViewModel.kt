package com.bonbasses.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonbasses.data.models.WritingHistoryItem
import com.bonbasses.data.preferences.QuizPreferences
import com.bonbasses.data.repository.WritingHistoryRepository
import com.bonbasses.ui.screens.history.FilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val allItems: List<WritingHistoryItem> = emptyList(),
    val searchResults: List<WritingHistoryItem> = emptyList(),
    val filteredResults: List<WritingHistoryItem> = emptyList(),
    val searchQuery: String = "",
    val recentQueries: List<String> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false,
    val currentFilter: FilterState = FilterState()
)

class SearchViewModel(
    private val repository: WritingHistoryRepository,
    private val preferences: QuizPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    init {
        loadAllHistory()
        loadRecentQueries()
    }
    
    private fun loadAllHistory() {
        viewModelScope.launch {
            repository.getAllHistory().collect { items ->
                _uiState.update { it.copy(allItems = items) }
            }
        }
    }
    
    private fun loadRecentQueries() {
        val queries = preferences.getSearchHistory()
        _uiState.update { it.copy(recentQueries = queries) }
    }
    
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
    
    fun performSearch() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty()) return
        
        _uiState.update { it.copy(isSearching = true) }
        
        viewModelScope.launch {
            val results = _uiState.value.allItems.filter { item ->
                item.storyText.contains(query, ignoreCase = true) ||
                item.words.any { word -> word.contains(query, ignoreCase = true) } ||
                item.genre.contains(query, ignoreCase = true)
            }
            
            _uiState.update { 
                it.copy(
                    searchResults = results,
                    filteredResults = applyFilters(results, it.currentFilter),
                    isSearching = false,
                    hasSearched = true
                )
            }
            
            addToRecentQueries(query)
        }
    }
    
    fun onRecentQueryClick(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        performSearch()
    }
    
    private fun addToRecentQueries(query: String) {
        val current = _uiState.value.recentQueries.toMutableList()
        current.remove(query)
        current.add(0, query)
        if (current.size > 10) {
            current.removeAt(current.size - 1)
        }
        _uiState.update { it.copy(recentQueries = current) }
        preferences.saveSearchHistory(current)
    }
    
    fun applyFilter(filterState: FilterState) {
        _uiState.update { 
            it.copy(
                currentFilter = filterState,
                filteredResults = applyFilters(it.searchResults, filterState)
            )
        }
    }
    
    fun resetFilter() {
        _uiState.update { 
            it.copy(
                currentFilter = FilterState(),
                filteredResults = it.searchResults
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
            
            lengthMatch && categoryMatch && scoreMatch
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
    
    fun clearSearch() {
        _uiState.update { 
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                filteredResults = emptyList(),
                hasSearched = false
            )
        }
    }
}

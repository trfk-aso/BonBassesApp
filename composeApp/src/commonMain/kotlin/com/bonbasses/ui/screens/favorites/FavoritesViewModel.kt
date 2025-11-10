package com.bonbasses.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonbasses.data.models.WritingHistoryItem
import com.bonbasses.data.repository.WritingHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favoriteItems: List<WritingHistoryItem> = emptyList(),
    val selectedItems: Set<Long> = emptySet(),
    val isLoading: Boolean = false
)

class FavoritesViewModel(
    private val repository: WritingHistoryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getFavoriteHistory().collect { favorites ->
                _uiState.update { 
                    it.copy(
                        favoriteItems = favorites,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun toggleItemSelection(itemId: Long) {
        _uiState.update { state ->
            val newSelection = if (state.selectedItems.contains(itemId)) {
                state.selectedItems - itemId
            } else {
                state.selectedItems + itemId
            }
            state.copy(selectedItems = newSelection)
        }
    }
    
    fun clearSelection() {
        _uiState.update { it.copy(selectedItems = emptySet()) }
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
}


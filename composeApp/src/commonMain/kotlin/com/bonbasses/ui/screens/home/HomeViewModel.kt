package com.bonbasses.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonbasses.data.preferences.QuizPreferences
import com.bonbasses.data.repository.WordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedCategory: Category = Category.ALL,
    val generatedWords: List<String> = emptyList(),
    val generatedGenre: String = "",
    val isTimerButtonEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val showHowToStart: Boolean = false
)

enum class Category(val displayName: String) {
    ALL("All"),
    ADVENTURE("Adventure"),
    SCI_FI("Sci-Fi"),
    SLICE_OF_LIFE("Slice of Life"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    FANTASY("Fantasy")
}

data class FeaturedPrompt(
    val id: Int,
    val category: Category,
    val words: List<String>,
    val backgroundImage: String
)

class HomeViewModel(
    private val quizPreferences: QuizPreferences? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val wordsRepository = WordsRepository()
    private var coreWords: List<String> = emptyList()
    private var categoryWords: Map<String, List<String>> = emptyMap()
    
    val featuredPrompts = listOf(
        FeaturedPrompt(
            id = 1,
            category = Category.SLICE_OF_LIFE,
            words = listOf("evening", "teapot", "November", "window", "notebook", "silence", "thought", "light", "cat", "plaid"),
            backgroundImage = "bg_slice_of_life"
        ),
        FeaturedPrompt(
            id = 2,
            category = Category.SCI_FI,
            words = listOf("atmosp", "orbit", "station", "cybervi", "artifici", "module", "portal", "observa", "signal", "protocol"),
            backgroundImage = "bg_sci_fi"
        ),
        FeaturedPrompt(
            id = 3,
            category = Category.ADVENTURE,
            words = listOf("challen", "peak", "ravine", "backpa", "compass", "camp", "fire", "map", "trace", "cave"),
            backgroundImage = "bg_adventure"
        ),
        FeaturedPrompt(
            id = 4,
            category = Category.MYSTERY,
            words = listOf("mirror", "trace", "creak", "figure", "squeak", "note", "clock", "baseme", "lantern", "shadow"),
            backgroundImage = "bg_mystery"
        ),
        FeaturedPrompt(
            id = 5,
            category = Category.ROMANCE,
            words = listOf("light", "hope", "hug", "silence", "evening", "touch", "letter", "meeting", "look", "heart"),
            backgroundImage = "bg_romance"
        ),
        FeaturedPrompt(
            id = 6,
            category = Category.FANTASY,
            words = listOf("castle", "elf", "portal", "artifact", "dragon", "spell", "moon", "forest", "legend", "sword"),
            backgroundImage = "bg_fantacy"
        )
    )
    
    fun loadWords(coreWordsJson: String, categoryWordsJson: String) {
        viewModelScope.launch {
            coreWords = wordsRepository.parseWordsCore(coreWordsJson)
            categoryWords = wordsRepository.parseWordsByCategory(categoryWordsJson)
        }
    }
    
    fun selectCategory(category: Category) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
    
    fun generateWords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val categoryName = when (_uiState.value.selectedCategory) {
                Category.ALL -> "All"
                Category.ADVENTURE -> "Adventure"
                Category.SCI_FI -> "Sci-Fi"
                Category.SLICE_OF_LIFE -> "Slice of Life"
                Category.MYSTERY -> "Mystery"
                Category.ROMANCE -> "Romance"
                Category.FANTASY -> "Fantasy"
            }
            

            val favoriteGenresAnswer = quizPreferences?.getAnswer(5)
            val favoriteGenres = favoriteGenresAnswer?.split(",")?.map { it.trim() }
            
            val (words, genre) = if (coreWords.isNotEmpty() && categoryWords.isNotEmpty()) {
                wordsRepository.generateWords(
                    category = categoryName,
                    coreWords = coreWords,
                    categoryWords = categoryWords,
                    favoriteGenres = favoriteGenres
                )
            } else {
                val prompt = featuredPrompts.random()
                prompt.words to prompt.category.displayName
            }
            
            _uiState.update {
                it.copy(
                    generatedWords = words,
                    generatedGenre = genre,
                    isTimerButtonEnabled = true,
                    isLoading = false
                )
            }
        }
    }
    
    fun quickStart(onWordsGenerated: (List<String>, String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val categoryName = when (_uiState.value.selectedCategory) {
                Category.ALL -> "All"
                Category.ADVENTURE -> "Adventure"
                Category.SCI_FI -> "Sci-Fi"
                Category.SLICE_OF_LIFE -> "Slice of Life"
                Category.MYSTERY -> "Mystery"
                Category.ROMANCE -> "Romance"
                Category.FANTASY -> "Fantasy"
            }
            

            val favoriteGenresAnswer = quizPreferences?.getAnswer(5)
            val favoriteGenres = favoriteGenresAnswer?.split(",")?.map { it.trim() }
            
            val (words, genre) = if (coreWords.isNotEmpty() && categoryWords.isNotEmpty()) {
                wordsRepository.generateWords(
                    category = categoryName,
                    coreWords = coreWords,
                    categoryWords = categoryWords,
                    favoriteGenres = favoriteGenres
                )
            } else {
                val prompt = featuredPrompts.random()
                prompt.words to prompt.category.displayName
            }
            
            _uiState.update {
                it.copy(
                    generatedWords = words,
                    generatedGenre = genre,
                    isTimerButtonEnabled = true,
                    isLoading = false
                )
            }
            
            onWordsGenerated(words, genre)
        }
    }
    
    fun resetAfterWriting() {
        _uiState.update {
            it.copy(
                generatedWords = emptyList(),
                isTimerButtonEnabled = false
            )
        }
    }
    
    fun checkFirstHomeVisit() {
        viewModelScope.launch {
            val isShown = quizPreferences?.isHowToStartShown() ?: true
            _uiState.update {
                it.copy(showHowToStart = !isShown)
            }
        }
    }
    
    fun dismissHowToStart() {
        viewModelScope.launch {
            quizPreferences?.setHowToStartShown(true)
            _uiState.update {
                it.copy(showHowToStart = false)
            }
        }
    }
}

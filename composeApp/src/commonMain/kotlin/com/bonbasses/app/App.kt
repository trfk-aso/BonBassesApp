package com.bonbasses.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import bonbassesapp.composeapp.generated.resources.Res
import com.bonbasses.data.database.createDatabaseDriverFactory
import com.bonbasses.data.preferences.createQuizPreferences
import com.bonbasses.data.repository.WritingHistoryRepository
import com.bonbasses.platform.createFileExporter
import com.bonbasses.platform.iap.IAPManager
import com.bonbasses.platform.ui.PlatformBackHandler
import com.bonbasses.platform.utils.DateFormatter
import com.bonbasses.platform.utils.TimeUtils
import com.bonbasses.ui.screens.home.HomeScreen
import com.bonbasses.ui.screens.home.HomeViewModel
import com.bonbasses.ui.screens.favorites.FavoritesScreen
import com.bonbasses.ui.screens.favorites.FavoritesViewModel
import com.bonbasses.ui.screens.history.HistoryScreen
import com.bonbasses.ui.screens.history.HistoryViewModel
import com.bonbasses.ui.screens.history.HistoryDetailScreen
import com.bonbasses.ui.screens.onboarding.OnboardingScreen
import com.bonbasses.ui.screens.onboarding.OnboardingViewModel
import com.bonbasses.ui.screens.result.ResultFailedScreen
import com.bonbasses.ui.screens.result.ResultLoaderScreen
import com.bonbasses.ui.screens.result.ResultSuccessScreen
import com.bonbasses.ui.screens.result.ResultViewModel
import com.bonbasses.ui.screens.search.SearchScreen
import com.bonbasses.ui.screens.search.SearchViewModel
import com.bonbasses.ui.screens.splash.SplashScreen
import com.bonbasses.ui.screens.stats.StatsScreen
import com.bonbasses.ui.screens.stats.StatsViewModel
import com.bonbasses.ui.screens.writing.WritingScreen
import com.bonbasses.ui.screens.settings.SettingsScreen
import com.bonbasses.ui.screens.settings.SettingsViewModel
import com.bonbasses.ui.screens.settings.ThemeMode
import com.bonbasses.ui.screens.about.AboutScreen
import com.bonbasses.ui.theme.DarkThemeColors
import com.bonbasses.ui.theme.LightThemeColors
import com.bonbasses.ui.theme.LocalThemeColors
import com.bonbasses.ui.theme.ProvideScreenSizeConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val quizPreferences = remember { createQuizPreferences() }
    var currentTheme by remember { mutableStateOf(ThemeMode.DARK) }
    val isSystemInDarkTheme = isSystemInDarkTheme()
    

    LaunchedEffect(Unit) {
        val savedTheme = quizPreferences.getThemeMode()
        currentTheme = when (savedTheme) {
            "LIGHT" -> ThemeMode.LIGHT
            "SYSTEM" -> ThemeMode.SYSTEM
            else -> ThemeMode.DARK
        }
    }
    

    LaunchedEffect(isSystemInDarkTheme, currentTheme) {

    }
    
    val themeColors = when (currentTheme) {
        ThemeMode.LIGHT -> LightThemeColors
        ThemeMode.DARK -> DarkThemeColors
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme) DarkThemeColors else LightThemeColors
    }
    
    CompositionLocalProvider(LocalThemeColors provides themeColors) {
        MaterialTheme {
            ProvideScreenSizeConfig {
                AppContent(
                    quizPreferences = quizPreferences,
                    onThemeChanged = { theme -> currentTheme = theme }
                )
            }
        }
    }
}

@Composable
private fun AppContent(
    quizPreferences: com.bonbasses.data.preferences.QuizPreferences,
    onThemeChanged: (ThemeMode) -> Unit
) {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }
        var isFirstLaunch by remember { mutableStateOf<Boolean?>(null) }
        var writingWords by remember { mutableStateOf<List<String>>(emptyList()) }
        var writingGenre by remember { mutableStateOf("") }
        var storyText by remember { mutableStateOf("") }
        var charCount by remember { mutableStateOf(0) }
        var timeLeftSeconds by remember { mutableStateOf(420) }
        var timerDurationMinutes by remember { mutableStateOf(7) }
        var selectedHistoryItem by remember { mutableStateOf<com.bonbasses.data.models.WritingHistoryItem?>(null) }
        var previousScreen by remember { mutableStateOf<AppScreen?>(null) }
        var homeViewModel: HomeViewModel? by remember { mutableStateOf(null) }
        val historyRepository = remember { WritingHistoryRepository(createDatabaseDriverFactory()) }
        val fileExporter = remember { createFileExporter() }
        val scope = rememberCoroutineScope()
        
        LaunchedEffect(Unit) {
            isFirstLaunch = !quizPreferences.isQuizCompleted()
            timerDurationMinutes = quizPreferences.getTimerLength()
        }
        
        if (isFirstLaunch == null) {
            return@MaterialTheme
        }
        

        PlatformBackHandler(enabled = currentScreen != AppScreen.Home && currentScreen != AppScreen.Splash && currentScreen != AppScreen.Writing) {
            when (currentScreen) {
                AppScreen.Settings -> {

                    currentScreen = previousScreen ?: AppScreen.Home
                }
                AppScreen.Result -> {

                    currentScreen = AppScreen.Home
                }
                AppScreen.About -> {

                    currentScreen = AppScreen.Settings
                }
                AppScreen.History, AppScreen.Favorite, AppScreen.Search, AppScreen.Stats -> {

                    currentScreen = AppScreen.Home
                }
                AppScreen.HistoryDetail -> {

                    currentScreen = AppScreen.History
                }
                else -> {

                    currentScreen = AppScreen.Home
                }
            }
        }
        
        when (currentScreen) {
            AppScreen.Splash -> {
                SplashScreen(
                    isFirstLaunch = isFirstLaunch!!,
                    onNavigateToOnboarding = { currentScreen = AppScreen.Onboarding },
                    onNavigateToHome = { currentScreen = AppScreen.Home }
                )
            }
            AppScreen.Onboarding -> {
                val iapManager = remember { IAPManager() }

                val viewModel = viewModel { OnboardingViewModel(quizPreferences, iapManager) }

                OnboardingScreen(
                    onComplete = {
                        isFirstLaunch = false
                        currentScreen = AppScreen.Home
                    },
                    viewModel = viewModel,
                    iapManager = iapManager
                )
            }
            AppScreen.Home -> {
                HomeScreen(
                    onNavigateToWriting = { words, genre ->
                        scope.launch {

                            timerDurationMinutes = quizPreferences.getTimerLength()
                            timeLeftSeconds = timerDurationMinutes * 60
                        }
                        writingWords = words
                        writingGenre = genre
                        currentScreen = AppScreen.Writing
                    },
                    onNavigateToTab = { tab ->
                        when (tab) {
                            "history" -> currentScreen = AppScreen.History
                            "favorite" -> currentScreen = AppScreen.Favorite
                            "search" -> currentScreen = AppScreen.Search
                            "stats" -> currentScreen = AppScreen.Stats
                            else -> {}
                        }
                    },
                    onNavigateToSettings = {
                        previousScreen = AppScreen.Home
                        currentScreen = AppScreen.Settings
                    },
                    viewModel = viewModel { HomeViewModel(quizPreferences).also { homeViewModel = it } }
                )
            }
            AppScreen.Writing -> {
                val sharedIapManager = remember { IAPManager() }

                WritingScreen(
                    words = writingWords,
                    initialText = storyText,
                    initialTimeLeft = timeLeftSeconds,
                    timerDurationMinutes = timerDurationMinutes,
                    quizPreferences = quizPreferences,
                    iapManager = sharedIapManager,
                    onBackClick = {
                        writingWords = emptyList()
                        storyText = ""
                        charCount = 0
                        timeLeftSeconds = timerDurationMinutes * 60
                        homeViewModel?.resetAfterWriting()
                        currentScreen = AppScreen.Home
                    },
                    onSettingsClick = {
                        previousScreen = AppScreen.Writing
                        currentScreen = AppScreen.Settings
                    },
                    onCheckClick = { text, count, timeLeft ->
                        storyText = text
                        charCount = count
                        timeLeftSeconds = timeLeft
                        currentScreen = AppScreen.Result
                    },
                    onStateChanged = { text, timeLeft ->
                        storyText = text
                        timeLeftSeconds = timeLeft
                    }
                )
            }
            AppScreen.Result -> {
                val resultViewModel = remember(storyText, charCount) { 
                    ResultViewModel(storyText, charCount, quizPreferences) 
                }
                val uiState by resultViewModel.uiState.collectAsState()
                
                LaunchedEffect(storyText, charCount) {
                    val recommendationsJson = Res.readBytes("files/recommendations.json").decodeToString()
                    val feedbackJson = Res.readBytes("files/feedback.json").decodeToString()
                    

                    val userTone = quizPreferences.getAnswer(4) ?: "Balanced"
                    
                    resultViewModel.loadResult(recommendationsJson, feedbackJson, userTone)
                }
                
                when {
                    uiState.isLoading -> {
                        ResultLoaderScreen(
                            onLoadingComplete = { resultViewModel.completeLoading() }
                        )
                    }
                    uiState.isFailed -> {
                        ResultFailedScreen(
                            charCount = charCount,
                            recommendations = uiState.recommendations,
                            onTryAgain = {
                                storyText = ""
                                charCount = 0
                                timeLeftSeconds = 420
                                currentScreen = AppScreen.Writing
                            },
                            onContinueWriting = {
                                currentScreen = AppScreen.Writing
                            },
                            onSaveDraft = {
                                scope.launch {
                                    historyRepository.insertHistory(
                                        storyText = storyText,
                                        words = writingWords,
                                        genre = writingGenre,
                                        charCount = charCount,
                                        wordCount = storyText.trim().split("\\s+".toRegex()).size,
                                        score = null,
                                        isFavorite = false
                                    )
                                    writingWords = emptyList()
                                    writingGenre = ""
                                    storyText = ""
                                    charCount = 0
                                    timeLeftSeconds = 420
                                    homeViewModel?.resetAfterWriting()
                                    currentScreen = AppScreen.Home
                                }
                            },
                            onBack = {
                                currentScreen = AppScreen.Home
                            },
                            onNavigateToTab = { tab ->
                                when (tab) {
                                    "home" -> {
                                        writingWords = emptyList()
                                        storyText = ""
                                        charCount = 0
                                        timeLeftSeconds = 420
                                        homeViewModel?.resetAfterWriting()
                                        currentScreen = AppScreen.Home
                                    }
                                    "history" -> currentScreen = AppScreen.History
                                    "favorite" -> currentScreen = AppScreen.Favorite
                                    else -> {}
                                }
                            }
                        )
                    }
                    else -> {
                        ResultSuccessScreen(
                            score = uiState.score,
                            feedback = uiState.feedback,
                            isFavorite = uiState.isFavorite,
                            onToggleFavorite = { resultViewModel.toggleFavorite() },
                            onSaveToHistory = {
                                scope.launch {
                                    historyRepository.insertHistory(
                                        storyText = storyText,
                                        words = writingWords,
                                        genre = writingGenre,
                                        charCount = charCount,
                                        wordCount = storyText.trim().split("\\s+".toRegex()).size,
                                        score = uiState.score,
                                        isFavorite = uiState.isFavorite
                                    )
                                    writingWords = emptyList()
                                    writingGenre = ""
                                    storyText = ""
                                    charCount = 0
                                    timeLeftSeconds = 420
                                    homeViewModel?.resetAfterWriting()
                                    currentScreen = AppScreen.Home
                                }
                            },
                            onShareAsTxt = {

                                val txtContent = buildString {
                                    appendLine("=".repeat(50))
                                    appendLine("BonBasses Story")
                                    appendLine("=".repeat(50))
                                    appendLine()
                                    appendLine("Date: ${DateFormatter.formatFullDateTime(TimeUtils.currentTimeMillis())}")
                                    appendLine("Genre: $writingGenre")
                                    appendLine("Score: ${uiState.score}/5")
                                    appendLine("Words: ${storyText.trim().split("\\s+".toRegex()).size}")
                                    appendLine("Characters: $charCount")
                                    appendLine()
                                    appendLine("Prompt Words:")
                                    writingWords.forEachIndexed { index, word ->
                                        append("${index + 1}. $word")
                                        if (index < writingWords.size - 1) appendLine()
                                    }
                                    appendLine()
                                    appendLine()
                                    appendLine("-".repeat(50))
                                    appendLine("Story Text:")
                                    appendLine("-".repeat(50))
                                    appendLine()
                                    appendLine(storyText)
                                    appendLine()
                                    appendLine("=".repeat(50))
                                }
                                
                                val filename = "story_${TimeUtils.currentTimeMillis()}.txt"
                                fileExporter.exportTxt(filename, txtContent)
                            },
                            onBack = {
                                writingWords = emptyList()
                                storyText = ""
                                charCount = 0
                                timeLeftSeconds = 420
                                homeViewModel?.resetAfterWriting()
                                currentScreen = AppScreen.Home
                            },
                            onNavigateToTab = { tab ->
                                when (tab) {
                                    "home" -> {
                                        writingWords = emptyList()
                                        storyText = ""
                                        charCount = 0
                                        timeLeftSeconds = 420
                                        homeViewModel?.resetAfterWriting()
                                        currentScreen = AppScreen.Home
                                    }
                                    "history" -> currentScreen = AppScreen.History
                                    "favorite" -> currentScreen = AppScreen.Favorite
                                    else -> {}
                                }
                            }
                        )
                    }
                }
            }
            AppScreen.History -> {
                val historyViewModel = viewModel { HistoryViewModel(historyRepository) }
                HistoryScreen(
                    viewModel = historyViewModel,
                    onBackClick = {
                        currentScreen = AppScreen.Home
                    },
                    onSettingsClick = {
                        previousScreen = AppScreen.History
                        currentScreen = AppScreen.Settings
                    },
                    onNavigateToTab = { tab ->
                        when (tab) {
                            "home" -> currentScreen = AppScreen.Home
                            "favorite" -> currentScreen = AppScreen.Favorite
                            "search" -> {
                                previousScreen = AppScreen.History
                                currentScreen = AppScreen.Search
                            }
                            "stats" -> {
                                previousScreen = AppScreen.History
                                currentScreen = AppScreen.Stats
                            }
                            else -> {}
                        }
                    },
                    onNavigateToDetail = { item ->
                        selectedHistoryItem = item
                        previousScreen = AppScreen.History
                        currentScreen = AppScreen.HistoryDetail
                    },
                    onExportTxt = {
                        scope.launch {
                            val items = historyRepository.getAllHistory().first()
                            val txtContent = buildString {
                                appendLine("=".repeat(60))
                                appendLine("BonBasses - Story History Export")
                                appendLine("Exported: ${DateFormatter.formatFullDateTime(TimeUtils.currentTimeMillis())}")
                                appendLine("Total Stories: ${items.size}")
                                appendLine("=".repeat(60))
                                appendLine()
                                
                                items.forEachIndexed { index, item ->
                                    appendLine()
                                    appendLine("-".repeat(60))
                                    appendLine("Story #${index + 1}")
                                    appendLine("-".repeat(60))
                                    appendLine("Date: ${DateFormatter.formatFullDateTime(item.createdAt)}")
                                    appendLine("Genre: ${item.genre}")
                                    item.score?.let { appendLine("Score: $it/5") }
                                    appendLine("Words: ${item.wordCount}")
                                    appendLine("Characters: ${item.charCount}")
                                    appendLine("Favorite: ${if (item.isFavorite) "Yes" else "No"}")
                                    appendLine()
                                    appendLine("Prompt Words:")
                                    item.words.forEachIndexed { i, word ->
                                        append("${i + 1}. $word")
                                        if (i < item.words.size - 1) appendLine()
                                    }
                                    appendLine()
                                    appendLine()
                                    appendLine("Story:")
                                    appendLine(item.storyText)
                                    appendLine()
                                }
                                
                                appendLine()
                                appendLine("=".repeat(60))
                                appendLine("End of Export")
                                appendLine("=".repeat(60))
                            }
                            
                            val filename = "bonbasses_history_${TimeUtils.currentTimeMillis()}.txt"
                            fileExporter.exportTxt(filename, txtContent)
                        }
                    },
                    onExportCsv = {
                        scope.launch {
                            val items = historyRepository.getAllHistory().first()
                            val csvContent = buildString {
                                appendLine("Date,Genre,Story,Words,Characters,Score,Favorite")
                                items.forEach { item ->
                                    val date = DateFormatter.formatFullDateTime(item.createdAt)
                                    val story = item.storyText.replace("\"", "\"\"")
                                    appendLine("\"$date\",\"${item.genre}\",\"$story\",${item.wordCount},${item.charCount},${item.score ?: ""},${item.isFavorite}")
                                }
                            }
                            
                            val filename = "bonbasses_history_${TimeUtils.currentTimeMillis()}.csv"
                            fileExporter.exportCsv(filename, csvContent)
                        }
                    }
                )
            }
            AppScreen.HistoryDetail -> {
                selectedHistoryItem?.let { item ->
                    HistoryDetailScreen(
                        item = item,
                        onBackClick = {
                            currentScreen = previousScreen ?: AppScreen.History
                            previousScreen = null
                        },
                        onToggleFavorite = {
                            scope.launch {
                                historyRepository.toggleFavorite(item.id)
                                val updated = historyRepository.getHistoryById(item.id)
                                if (updated != null) {
                                    selectedHistoryItem = updated
                                }
                            }
                        },
                        onEditCopy = {
                            writingWords = item.words
                            writingGenre = item.genre
                            storyText = item.storyText
                            charCount = item.charCount
                            timeLeftSeconds = 420
                            currentScreen = AppScreen.Writing
                        },
                        onDelete = {
                            scope.launch {
                                historyRepository.deleteHistory(item.id)
                                currentScreen = AppScreen.History
                            }
                        },
                        onUpdateTitle = { newTitle ->
                            scope.launch {
                                historyRepository.updateTitle(item.id, newTitle)
                                val updated = historyRepository.getHistoryById(item.id)
                                if (updated != null) {
                                    selectedHistoryItem = updated
                                }
                            }
                        },
                        onNavigateToTab = { tab ->
                            when (tab) {
                                "home" -> currentScreen = AppScreen.Home
                                "history" -> currentScreen = AppScreen.History
                                "favorite" -> currentScreen = AppScreen.Favorite
                                "search" -> {
                                    previousScreen = AppScreen.HistoryDetail
                                    currentScreen = AppScreen.Search
                                }
                                "stats" -> {
                                    previousScreen = AppScreen.HistoryDetail
                                    currentScreen = AppScreen.Stats
                                }
                                else -> {}
                            }
                        }
                    )
                }
            }
            AppScreen.Favorite -> {
                FavoritesScreen(
                    viewModel = viewModel { FavoritesViewModel(historyRepository) },
                    onBackClick = {
                        currentScreen = AppScreen.Home
                    },
                    onSettingsClick = {
                        previousScreen = AppScreen.Favorite
                        currentScreen = AppScreen.Settings
                    },
                    onNavigateToTab = { tab ->
                        when (tab) {
                            "home" -> currentScreen = AppScreen.Home
                            "history" -> currentScreen = AppScreen.History
                            "search" -> {
                                previousScreen = AppScreen.Favorite
                                currentScreen = AppScreen.Search
                            }
                            "stats" -> currentScreen = AppScreen.Stats
                            else -> {}
                        }
                    },
                    onNavigateToDetail = { item ->
                        selectedHistoryItem = item
                        previousScreen = AppScreen.Favorite
                        currentScreen = AppScreen.HistoryDetail
                    }
                )
            }
            AppScreen.Search -> {
                SearchScreen(
                    viewModel = viewModel { SearchViewModel(historyRepository, quizPreferences) },
                    onBackClick = {
                        currentScreen = previousScreen ?: AppScreen.Home
                        previousScreen = null
                    },
                    onItemClick = { id ->
                        scope.launch {
                            historyRepository.getAllHistory().collect { items ->
                                selectedHistoryItem = items.find { it.id == id }
                                if (selectedHistoryItem != null) {
                                    previousScreen = AppScreen.Search
                                    currentScreen = AppScreen.HistoryDetail
                                }
                            }
                        }
                    },
                    onSettingsClick = {
                        previousScreen = AppScreen.Search
                        currentScreen = AppScreen.Settings
                    },
                    onNavigateToTab = { tab ->
                        when (tab) {
                            "home" -> currentScreen = AppScreen.Home
                            "history" -> currentScreen = AppScreen.History
                            "favorite" -> currentScreen = AppScreen.Favorite
                            "stats" -> currentScreen = AppScreen.Stats
                            else -> {}
                        }
                    }
                )
            }
            AppScreen.Stats -> {
                StatsScreen(
                    viewModel = viewModel { StatsViewModel(historyRepository) },
                    onBackClick = {
                        currentScreen = AppScreen.Home
                    },
                    onSettingsClick = {
                        previousScreen = AppScreen.Stats
                        currentScreen = AppScreen.Settings
                    },
                    onNavigateToTab = { tab ->
                        when (tab) {
                            "home" -> currentScreen = AppScreen.Home
                            "history" -> currentScreen = AppScreen.History
                            "favorite" -> currentScreen = AppScreen.Favorite
                            "search" -> currentScreen = AppScreen.Search
                            else -> {}
                        }
                    }
                )
            }
            AppScreen.Settings -> {
                val sharedIapManager = remember { IAPManager() }

                SettingsScreen(
                    viewModel = viewModel {
                        SettingsViewModel(
                            quizPreferences = quizPreferences,
                            historyRepository = historyRepository,
                            fileExporter = fileExporter,
                            iapManager = sharedIapManager
                        )
                    },
                    iapManager = sharedIapManager,
                    onBackClick = {
                        scope.launch {
                            timerDurationMinutes = quizPreferences.getTimerLength()
                        }
                        currentScreen = previousScreen ?: AppScreen.Home
                        previousScreen = null
                    },
                    onNavigateToTab = { tab ->
                        when (tab) {
                            "home" -> currentScreen = AppScreen.Home
                            "history" -> currentScreen = AppScreen.History
                            "favorite" -> currentScreen = AppScreen.Favorite
                            "search" -> currentScreen = AppScreen.Search
                            "stats" -> currentScreen = AppScreen.Stats
                        }
                    },
                    onNavigateToAbout = {
                        previousScreen = AppScreen.Settings
                        currentScreen = AppScreen.About
                    },
                    onThemeChanged = onThemeChanged
                )
            }
            AppScreen.About -> {
                AboutScreen(
                    onBackClick = {
                        currentScreen = previousScreen ?: AppScreen.Settings
                        previousScreen = null
                    },
                    onNavigateToTab = { tab ->
                        when (tab) {
                            "home" -> currentScreen = AppScreen.Home
                            "history" -> currentScreen = AppScreen.History
                            "favorite" -> currentScreen = AppScreen.Favorite
                            "search" -> currentScreen = AppScreen.Search
                            "stats" -> currentScreen = AppScreen.Stats
                            else -> {}
                        }
                    }
                )
            }
        }
    }
}

sealed interface AppScreen {
    data object Splash : AppScreen
    data object Onboarding : AppScreen
    data object Home : AppScreen
    data object Writing : AppScreen
    data object Result : AppScreen
    data object History : AppScreen
    data object HistoryDetail : AppScreen
    data object Favorite : AppScreen
    data object Search : AppScreen
    data object Stats : AppScreen
    data object Settings : AppScreen
    data object About : AppScreen
}
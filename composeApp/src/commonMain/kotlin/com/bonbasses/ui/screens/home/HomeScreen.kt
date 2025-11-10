package com.bonbasses.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.bg_adventure
import bonbassesapp.composeapp.generated.resources.bg_fantacy
import bonbassesapp.composeapp.generated.resources.bg_mystery
import bonbassesapp.composeapp.generated.resources.bg_quiz_dialog
import bonbassesapp.composeapp.generated.resources.bg_romance
import bonbassesapp.composeapp.generated.resources.bg_sci_fi
import bonbassesapp.composeapp.generated.resources.bg_slice_of_life
import bonbassesapp.composeapp.generated.resources.btn_start
import bonbassesapp.composeapp.generated.resources.logo_bonbasses
import com.bonbasses.ui.components.AppBottomNavigation
import com.bonbasses.ui.theme.AppColors
import com.bonbasses.ui.theme.AppRadius
import com.bonbasses.ui.theme.AppSizes
import com.bonbasses.ui.theme.AppSpacing
import com.bonbasses.ui.theme.AppTypography
import com.bonbasses.ui.theme.AppBorders
import com.bonbasses.ui.theme.HomeButtonBackground
import com.bonbasses.ui.theme.HomeButtonText
import com.bonbasses.ui.theme.HomeCategoryBackground
import com.bonbasses.ui.theme.HomeCategoryStroke
import com.bonbasses.ui.theme.HomeCategoryText
import com.bonbasses.ui.theme.HomeOfflineText
import com.bonbasses.ui.theme.HomeWordChipBackground
import com.bonbasses.ui.theme.HomeWordChipStroke
import com.bonbasses.ui.theme.HomeWordChipText
import com.bonbasses.ui.theme.QuizTextColor
import com.bonbasses.ui.theme.RobotoSlabFontFamily
import com.bonbasses.ui.theme.LocalThemeColors
import com.bonbasses.ui.theme.getBackgroundBrush
import com.bonbasses.ui.theme.ResponsiveAppTypography
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppRadius
import com.bonbasses.ui.theme.ResponsiveAppBorders
import com.bonbasses.ui.theme.LocalScreenSizeConfig
import org.jetbrains.compose.resources.painterResource

enum class NavigationTab {
    HOME, HISTORY, SEARCH, FAVORITE, STATS
}

@Composable
fun HomeScreen(
    onNavigateToWriting: (List<String>, String) -> Unit = { _, _ -> },
    onNavigateToTab: (String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = viewModel { HomeViewModel() }
) {
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    val uiState by viewModel.uiState.collectAsState()
    
    val fadeInAlpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        val coreWordsJson = Res.readBytes("files/words_core.json").decodeToString()
        val categoryWordsJson = Res.readBytes("files/words_by_category.json").decodeToString()
        viewModel.loadWords(coreWordsJson, categoryWordsJson)
        viewModel.checkFirstHomeVisit()
        
        fadeInAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
            .alpha(fadeInAlpha.value)
    ) {
        TopBar(
            onQuickStart = { 
                viewModel.quickStart { words, genre ->
                    onNavigateToWriting(words, genre)
                }
            },
            onSettingsClick = onNavigateToSettings
        )
        
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                uiState.showHowToStart -> {
                    HowToStartContent(
                        onStart = {
                            viewModel.dismissHowToStart()
                        }
                    )
                }
                selectedTab == NavigationTab.HOME -> {
                    MainHomeContent(
                        uiState = uiState,
                        viewModel = viewModel,
                        onNavigateToWriting = onNavigateToWriting
                    )
                }
                else -> {}
            }
        }
        
        AppBottomNavigation(
            selectedTab = if (selectedTab == NavigationTab.HOME) "home" else selectedTab.name.lowercase(),
            onTabSelected = { tab ->
                when (tab) {
                    "home" -> selectedTab = NavigationTab.HOME
                    "history" -> onNavigateToTab("history")
                    "search" -> onNavigateToTab("search")
                    "favorite" -> onNavigateToTab("favorite")
                    "stats" -> onNavigateToTab("stats")
                }
            }
        )
    }
}

@Composable
fun HowToStartContent(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ResponsiveAppSpacing.Small, vertical = ResponsiveAppSpacing.Small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "How to start",
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.TitleMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = ResponsiveAppSpacing.Small)
        )
        
        InstructionCard(
            title = "Click \"Generate 10 words\"",
            description = "We will generate 10 random words — this is your basis for a microstory."
        )
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraSmall))
        
        InstructionCard(
            title = "Start the timer for 7 minutes",
            description = "After generating the words, the \"Start Timer\" button is activated. This is your creative countdown."
        )
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraSmall))
        
        InstructionCard(
            title = "Start writing",
            description = "Use the words as inspiration. When you are finished — click \"Check\" and get warm feedback."
        )
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Large))
        
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(64.dp)
                .clickable { onStart() }
        ) {
            Image(
                painter = painterResource(Res.drawable.btn_start),
                contentDescription = "Start",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun TopBar(
    onQuickStart: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)
            .padding(top = ResponsiveAppSpacing.ScreenTop, bottom = ResponsiveAppSpacing.Small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = getSettingsIcon(),
            contentDescription = "Settings",
            modifier = Modifier
                .size(28.dp)
                .clickable { onSettingsClick() },
            colorFilter = ColorFilter.tint(AppColors.TitleText)
        )
        
        Image(
            painter = painterResource(Res.drawable.logo_bonbasses),
            contentDescription = "BonBasses Logo",
            modifier = Modifier.height(36.dp)
        )
        
        Image(
            painter = getAddIcon(),
            contentDescription = "Quick Start",
            modifier = Modifier
                .size(ResponsiveAppSizes.IconMedium)
                .clickable { onQuickStart() },
            colorFilter = ColorFilter.tint(AppColors.TitleText)
        )
    }
}

@Composable
fun MainHomeContent(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    onNavigateToWriting: (List<String>, String) -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal, vertical = ResponsiveAppSpacing.Small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(ResponsiveAppSizes.ButtonHeight + 8.dp)
                .border(
                    width = ResponsiveAppBorders.Thick,
                    color = HomeButtonText,
                    shape = RoundedCornerShape(ResponsiveAppRadius.Large)
                )
                .background(
                    color = HomeButtonBackground,
                    shape = RoundedCornerShape(ResponsiveAppRadius.Large)
                )
                .clickable { viewModel.generateWords() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Generate 10 words",
                color = HomeButtonText,
                fontSize = ResponsiveAppTypography.TitleSmall,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal
            )
        }
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
        
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(ResponsiveAppSizes.ButtonHeight + 8.dp)
                .border(
                    width = ResponsiveAppBorders.Thick,
                    color = if (uiState.isTimerButtonEnabled) HomeButtonText else themeColors.disabledButtonBorder,
                    shape = RoundedCornerShape(ResponsiveAppRadius.Large)
                )
                .background(
                    color = if (uiState.isTimerButtonEnabled) HomeButtonBackground else themeColors.disabledButtonBackground,
                    shape = RoundedCornerShape(ResponsiveAppRadius.Large)
                )
                .clickable(enabled = uiState.isTimerButtonEnabled) { 
                    onNavigateToWriting(uiState.generatedWords, uiState.generatedGenre)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Start 7-min Timer",
                color = if (uiState.isTimerButtonEnabled) HomeButtonText else themeColors.disabledButtonText,
                fontSize = ResponsiveAppTypography.TitleSmall,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal
            )
        }
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraSmall + 4.dp))
        
        Text(
            text = "No accounts. 100% offline.",
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.BodySmall,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal
        )
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Medium - 4.dp))
        
        CategoryFilter(
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = { viewModel.selectCategory(it) }
        )
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Medium - 4.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.ExtraSmall + 4.dp),
            verticalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.ExtraSmall + 4.dp)
        ) {
            items(viewModel.featuredPrompts) { prompt ->
                FeaturedPromptCard(prompt = prompt, onClick = {
                    onNavigateToWriting(prompt.words, prompt.category.displayName)
                })
            }
        }
    }
}

@Composable
fun WordChipsGrid(words: List<String>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 80.dp),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(words) { word ->
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = HomeWordChipStroke,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(
                        color = HomeWordChipBackground,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = word,
                    color = HomeWordChipText,
                    fontSize = 13.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CategoryFilter(
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.ExtraSmall)
    ) {
        items(Category.entries) { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .border(
                        width = ResponsiveAppBorders.Medium,
                        color = if (isSelected) HomeButtonText else HomeCategoryStroke,
                        shape = RoundedCornerShape(ResponsiveAppRadius.Small + 2.dp)
                    )
                    .background(
                        color = if (isSelected) Color(0xFF613923) else HomeCategoryBackground,
                        shape = RoundedCornerShape(ResponsiveAppRadius.Small + 2.dp)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = ResponsiveAppSpacing.Medium - 4.dp, vertical = ResponsiveAppSpacing.ExtraSmall + 2.dp)
            ) {
                Text(
                    text = category.displayName,
                    color = if (isSelected) HomeButtonText else HomeCategoryText,
                    fontSize = ResponsiveAppTypography.BodyMedium,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun FeaturedPromptCard(
    prompt: FeaturedPrompt,
    onClick: () -> Unit
) {
    val backgroundRes = when (prompt.backgroundImage) {
        "bg_slice_of_life" -> Res.drawable.bg_slice_of_life
        "bg_sci_fi" -> Res.drawable.bg_sci_fi
        "bg_adventure" -> Res.drawable.bg_adventure
        "bg_mystery" -> Res.drawable.bg_mystery
        "bg_romance" -> Res.drawable.bg_romance
        "bg_fantacy" -> Res.drawable.bg_fantacy
        else -> Res.drawable.bg_slice_of_life
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium + 4.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(ResponsiveAppSpacing.ExtraSmall + 2.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(prompt.words.take(10)) { word ->
                Box(
                    modifier = Modifier
                        .border(
                            width = ResponsiveAppBorders.Thin,
                            color = HomeWordChipStroke,
                            shape = RoundedCornerShape(ResponsiveAppRadius.Small + 2.dp)
                        )
                        .background(
                            color = HomeWordChipBackground,
                            shape = RoundedCornerShape(ResponsiveAppRadius.Small + 2.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = word,
                        color = HomeWordChipText,
                        fontSize = 10.sp,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun InstructionCard(
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.bg_quiz_dialog),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ResponsiveAppSpacing.Small + 4.dp, vertical = ResponsiveAppSpacing.ExtraSmall + 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = QuizTextColor,
                fontSize = 15.sp,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = description,
                color = QuizTextColor,
                fontSize = ResponsiveAppTypography.BodySmall,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}


package com.bonbasses.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.img_serach
import com.bonbasses.data.models.WritingHistoryItem
import com.bonbasses.ui.components.AppBottomNavigation
import com.bonbasses.ui.screens.history.FilterDialog
import com.bonbasses.ui.screens.history.FilterState
import com.bonbasses.ui.screens.history.HistoryItemCard
import com.bonbasses.ui.screens.home.getBackIcon
import com.bonbasses.ui.screens.home.getFilterIcon
import com.bonbasses.ui.screens.home.getSearchIcon
import com.bonbasses.ui.screens.home.getSettingsIcon
import com.bonbasses.ui.theme.*
import com.bonbasses.ui.theme.getBackgroundBrush
import com.bonbasses.ui.theme.ResponsiveAppTypography
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppRadius
import com.bonbasses.ui.theme.ResponsiveAppBorders
import com.bonbasses.ui.theme.LocalScreenSizeConfig
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToTab: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    
    val handleBackClick = {
        if (uiState.hasSearched) {
            viewModel.clearSearch()
        } else {
            onBackClick()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchTopBar(
                title = if (uiState.hasSearched) "Result" else "Search",
                onBackClick = handleBackClick,
                onSettingsClick = onSettingsClick
            )
            
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onSearch = { viewModel.performSearch() },
                onFilterClick = { showFilterDialog = true }
            )
            
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.hasSearched) {
                    if (uiState.filteredResults.isEmpty()) {
                        SearchEmptyState()
                    } else {
                        SearchResults(
                            items = uiState.filteredResults,
                            onItemClick = onItemClick,
                            onToggleFavorite = { viewModel.toggleFavorite(it) }
                        )
                    }
                } else {
                    if (uiState.recentQueries.isNotEmpty()) {
                        RecentQueries(
                            queries = uiState.recentQueries,
                            onQueryClick = { viewModel.onRecentQueryClick(it) }
                        )
                    }
                }
            }
            
            AppBottomNavigation(
                selectedTab = "search",
                onTabSelected = onNavigateToTab
            )
        }
        
        if (showFilterDialog) {
            FilterDialog(
                filterState = uiState.currentFilter,
                onDismiss = { showFilterDialog = false },
                onApply = { filterState ->
                    viewModel.applyFilter(filterState)
                    showFilterDialog = false
                },
                onReset = {
                    viewModel.resetFilter()
                    showFilterDialog = false
                },
                showDateFields = false
            )
        }
    }
}

@Composable
fun SearchTopBar(
    title: String,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)
            .padding(top = ResponsiveAppSpacing.ScreenTop),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ResponsiveAppSizes.TouchTarget)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = getBackIcon(),
                contentDescription = "Back",
                modifier = Modifier.size(ResponsiveAppSizes.IconMedium),
                colorFilter = ColorFilter.tint(AppColors.TitleText)
            )
        }
        
        Text(
            text = title,
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.TitleMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.SemiBold
        )
        
        Box(
            modifier = Modifier
                .size(ResponsiveAppSizes.TouchTarget)
                .clickable(onClick = onSettingsClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = getSettingsIcon(),
                contentDescription = "Settings",
                modifier = Modifier.size(ResponsiveAppSizes.IconMedium),
                colorFilter = ColorFilter.tint(AppColors.TitleText)
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onFilterClick: () -> Unit
) {
    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            kotlinx.coroutines.delay(500)
            onSearch()
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(3f)
                .height(48.dp)
                .border(
                    width = 1.5.dp,
                    color = AppColors.TitleText,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    color = Color(0xFFD8BCAC),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = getSearchIcon(),
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(Color(0xFF56453C))
                )
                
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF56453C),
                        fontSize = 14.sp,
                        fontFamily = RobotoSlabFontFamily()
                    ),
                    cursorBrush = SolidColor(Color(0xFF56453C)),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                text = "search by stories/prompts",
                                color = Color(0xFF56453C).copy(alpha = 0.6f),
                                fontSize = 14.sp,
                                fontFamily = RobotoSlabFontFamily()
                            )
                        }
                        innerTextField()
                    },
                    singleLine = true
                )
            }
        }
        
        Box(
            modifier = Modifier
                .size(ResponsiveAppSizes.TouchTarget)
                .clickable(onClick = onFilterClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = getFilterIcon(),
                contentDescription = "Filter",
                modifier = Modifier.size(ResponsiveAppSizes.IconMedium),
                colorFilter = ColorFilter.tint(AppColors.TitleText)
            )
        }
    }
}

@Composable
fun RecentQueries(
    queries: List<String>,
    onQueryClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recent queries",
            color = Color(0xFFD8BCAC),
            fontSize = 16.sp,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(queries) { query ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(
                            color = Color(0xFF312117),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onQueryClick(query) }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = query,
                        color = Color(0xFFD8BCAC),
                        fontSize = 14.sp,
                        fontFamily = RobotoSlabFontFamily(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SearchEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No matching entries",
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.BodyMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraLarge))
        
        Image(
            painter = painterResource(Res.drawable.img_serach),
            contentDescription = "No results",
            modifier = Modifier.size(200.dp)
        )
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraLarge))
        
        Text(
            text = "Try a different search\nor remove filters.",
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.BodyMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun SearchResults(
    items: List<WritingHistoryItem>,
    onItemClick: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        items(items) { item ->
            HistoryItemCard(
                item = item,
                isSelected = false,
                isEditMode = false,
                editedTitle = item.title,
                onEditedTitleChange = { },
                onItemClick = { onItemClick(item.id) },
                onToggleSelection = { },
                onToggleFavorite = { onToggleFavorite(item.id) }
            )
        }
    }
}

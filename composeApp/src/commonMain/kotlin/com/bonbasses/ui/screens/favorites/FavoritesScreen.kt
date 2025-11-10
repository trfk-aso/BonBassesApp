package com.bonbasses.ui.screens.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.btn_favorite_selected
import bonbassesapp.composeapp.generated.resources.btn_favorite_unselected
import bonbassesapp.composeapp.generated.resources.img_favorite
import com.bonbasses.data.models.WritingHistoryItem
import com.bonbasses.platform.utils.TimeUtils
import com.bonbasses.ui.components.AppBottomNavigation
import com.bonbasses.ui.screens.history.DeleteConfirmationDialog
import com.bonbasses.ui.screens.home.getBackIcon
import com.bonbasses.ui.screens.home.getSettingsIcon
import com.bonbasses.ui.screens.result.getStarIcon
import com.bonbasses.ui.theme.AppColors
import com.bonbasses.ui.theme.AppRadius
import com.bonbasses.ui.theme.AppSizes
import com.bonbasses.ui.theme.AppSpacing
import com.bonbasses.ui.theme.AppTypography
import com.bonbasses.ui.theme.RobotoSlabFontFamily
import com.bonbasses.ui.theme.getBackgroundBrush
import com.bonbasses.ui.theme.ResponsiveAppTypography
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppRadius
import com.bonbasses.ui.theme.ResponsiveAppBorders
import com.bonbasses.ui.theme.LocalScreenSizeConfig
import org.jetbrains.compose.resources.painterResource

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = viewModel { error("FavoritesViewModel must be provided") },
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onNavigateToTab: (String) -> Unit = {},
    onNavigateToDetail: (WritingHistoryItem) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
    ) {
        FavoritesTopBar(
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick
        )
        
        Box(modifier = Modifier.weight(1f)) {
            if (uiState.favoriteItems.isEmpty()) {
                FavoritesEmptyState()
            } else {
                FavoritesListContent(
                    items = uiState.favoriteItems,
                    selectedItems = uiState.selectedItems,
                    onItemClick = { item ->
                        onNavigateToDetail(item)
                    },
                    onToggleSelection = { viewModel.toggleItemSelection(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                )
            }
        }
        
        AppBottomNavigation(
            selectedTab = "favorite",
            onTabSelected = onNavigateToTab
        )
    }
}

@Composable
fun FavoritesTopBar(
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
            text = "Favorites",
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
                painter = com.bonbasses.ui.screens.home.getSettingsIcon(),
                contentDescription = "Settings",
                modifier = Modifier.size(ResponsiveAppSizes.IconMedium),
                colorFilter = ColorFilter.tint(AppColors.TitleText)
            )
        }
    }
}

@Composable
fun FavoritesEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No favorites yet",
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.BodyMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraLarge))
        
        Image(
            painter = painterResource(Res.drawable.img_favorite),
            contentDescription = "No favorites",
            modifier = Modifier.size(200.dp)
        )
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraLarge))
        
        Text(
            text = "Mark stories as favorites to find\nthem here.",
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.BodyMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FavoritesListContent(
    items: List<WritingHistoryItem>,
    selectedItems: Set<Long>,
    onItemClick: (WritingHistoryItem) -> Unit,
    onToggleSelection: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        items(items, key = { it.id }) { item ->
            FavoriteItemCard(
                item = item,
                isSelected = item.id in selectedItems,
                onItemClick = { onItemClick(item) },
                onToggleSelection = { onToggleSelection(item.id) },
                onToggleFavorite = { onToggleFavorite(item.id) }
            )
        }
    }
}

@Composable
fun FavoriteItemCard(
    item: WritingHistoryItem,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    onToggleSelection: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val dateText = remember(item.createdAt) {
        val timestamp = item.createdAt

        val dateStr = TimeUtils.formatDateDDMMYY(timestamp)
        dateStr
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .border(
                width = 1.5.dp,
                color = Color(0xFFAD8E7D),
                shape = RoundedCornerShape(15.dp)
            )
            .background(
                color = if (isSelected) Color(0xFF4A3228) else Color(0xFF312117),
                shape = RoundedCornerShape(15.dp)
            )
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onToggleSelection
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.title,
                    color = Color(0xFFD8BCAC),
                    fontSize = 16.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = dateText,
                    color = Color(0xFFAD8E7D),
                    fontSize = 12.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF8E7A6D),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.genre,
                    color = Color(0xFF1F140F),
                    fontSize = 11.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            if (item.score != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.score.toString(),
                        color = Color(0xFFD8BCAC),
                        fontSize = 14.sp,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Medium
                    )
                    
                    Image(
                        painter = getStarIcon(),
                        contentDescription = "Star",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(onClick = onToggleFavorite),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.btn_favorite_selected),
                    contentDescription = "Favorite",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

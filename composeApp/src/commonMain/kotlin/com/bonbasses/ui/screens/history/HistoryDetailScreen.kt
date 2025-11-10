package com.bonbasses.ui.screens.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.btn_favorite_selected
import bonbassesapp.composeapp.generated.resources.btn_favorite_unselected
import com.bonbasses.data.models.WritingHistoryItem
import com.bonbasses.platform.utils.TimeUtils
import com.bonbasses.ui.components.AppBottomNavigation
import com.bonbasses.ui.screens.home.getBackIcon
import com.bonbasses.ui.screens.home.getSettingsIcon
import com.bonbasses.ui.screens.result.getStarIcon
import com.bonbasses.ui.theme.AppBorders
import com.bonbasses.ui.theme.AppColors
import com.bonbasses.ui.theme.AppRadius
import com.bonbasses.ui.theme.AppSizes
import com.bonbasses.ui.theme.AppSpacing
import com.bonbasses.ui.theme.AppTypography
import com.bonbasses.ui.theme.ResponsiveAppTypography
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppRadius
import com.bonbasses.ui.theme.ResponsiveAppBorders
import com.bonbasses.ui.theme.LocalScreenSizeConfig
import com.bonbasses.ui.theme.LocalThemeColors
import com.bonbasses.ui.theme.RobotoSlabFontFamily
import com.bonbasses.ui.theme.getBackgroundBrush
import org.jetbrains.compose.resources.painterResource

@Composable
fun HistoryDetailScreen(
    item: WritingHistoryItem,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEditCopy: () -> Unit,
    onDelete: () -> Unit,
    onUpdateTitle: (String) -> Unit,
    onNavigateToTab: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val dateText = remember(item.createdAt) {
        TimeUtils.formatDateDDMMYY(item.createdAt)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
    ) {
        val screenConfig = LocalScreenSizeConfig.current
        val textBoxHeight = if (screenConfig.isSmallScreen) 180.dp else 240.dp
        
        DetailTopBar(
            title = item.title,
            onBackClick = onBackClick,
            onSettingsClick = {  }
        )
        
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)
                .padding(bottom = ResponsiveAppSpacing.Small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraSmall))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(textBoxHeight)
                    .border(
                        width = ResponsiveAppBorders.Medium,
                        color = Color(0xFFAD8E7D),
                        shape = RoundedCornerShape(ResponsiveAppRadius.Large)
                    )
                    .background(
                        color = Color(0xFF312117),
                        shape = RoundedCornerShape(ResponsiveAppRadius.Large)
                    )
                    .padding(ResponsiveAppSpacing.Small)
            ) {
                val scrollState = rememberScrollState()
                Text(
                    text = item.storyText,
                    color = Color(0xFFD8BCAC),
                    fontSize = ResponsiveAppTypography.BodyMedium,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    lineHeight = ResponsiveAppTypography.TitleLarge,
                    modifier = Modifier.verticalScroll(scrollState)
                )
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraSmall))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ResponsiveAppSizes.ButtonHeightSmall)
                        .border(
                            width = ResponsiveAppBorders.Medium,
                            color = Color(0xFFAD8E7D),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Small)
                        )
                        .background(
                            color = Color(0xFF312117),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Small)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.genre,
                        color = Color(0xFFAD8E7D),
                        fontSize = ResponsiveAppTypography.CaptionSmall,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Normal
                    )
                }
                
                Spacer(modifier = Modifier.width(ResponsiveAppSpacing.ExtraSmall))
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ResponsiveAppSizes.ButtonHeightSmall)
                        .border(
                            width = ResponsiveAppBorders.Medium,
                            color = Color(0xFFAD8E7D),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Small)
                        )
                        .background(
                            color = Color(0xFF312117),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Small)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dateText,
                        color = Color(0xFFAD8E7D),
                        fontSize = ResponsiveAppTypography.CaptionSmall,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Normal
                    )
                }
                
                Spacer(modifier = Modifier.width(ResponsiveAppSpacing.ExtraSmall))
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ResponsiveAppSizes.ButtonHeightSmall)
                        .border(
                            width = ResponsiveAppBorders.Medium,
                            color = Color(0xFFAD8E7D),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Small)
                        )
                        .background(
                            color = Color(0xFF312117),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Small)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (item.charCount < 300) "Short" else if (item.charCount < 600) "Medium" else "Long",
                        color = Color(0xFFAD8E7D),
                        fontSize = ResponsiveAppTypography.CaptionSmall,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Normal
                    )
                }
                
                if (item.score != null) {
                    Spacer(modifier = Modifier.width(ResponsiveAppSpacing.ExtraSmall))
                    
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(ResponsiveAppSizes.ButtonHeightSmall)
                            .border(
                                width = ResponsiveAppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(ResponsiveAppRadius.Small)
                            )
                            .background(
                                color = Color(0xFF312117),
                                shape = RoundedCornerShape(ResponsiveAppRadius.Small)
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.score.toString(),
                            color = Color(0xFFAD8E7D),
                            fontSize = ResponsiveAppTypography.CaptionSmall,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.width(ResponsiveAppSpacing.ExtraSmall))
                        Image(
                            painter = getStarIcon(),
                            contentDescription = "Star",
                            modifier = Modifier.size(ResponsiveAppSizes.IconSmall)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraSmall))
            
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.ExtraSmall),
                maxItemsInEachRow = 3
            ) {
                item.words.forEach { word ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = ResponsiveAppSpacing.ExtraSmall)
                            .height(ResponsiveAppSizes.ButtonHeightSmall)
                            .widthIn(min = 80.dp)
                            .border(
                                width = ResponsiveAppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(ResponsiveAppRadius.Small)
                            )
                            .background(
                                color = Color(0xFF312117),
                                shape = RoundedCornerShape(ResponsiveAppRadius.Small)
                            )
                            .padding(
                                horizontal = ResponsiveAppSpacing.ExtraSmall, 
                                vertical = ResponsiveAppSpacing.ExtraSmall
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = word,
                            color = Color(0xFFAD8E7D),
                            fontSize = ResponsiveAppTypography.CaptionSmall,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.ExtraSmall)
            ) {
                Box(
                    modifier = Modifier
                        .size(ResponsiveAppSizes.ButtonHeight)
                        .border(
                            width = ResponsiveAppBorders.Medium,
                            color = Color(0xFFAD8E7D),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                        )
                        .background(
                            color = Color(0xFF613923),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                        )
                        .clickable(onClick = onToggleFavorite),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            if (item.isFavorite) Res.drawable.btn_favorite_selected
                            else Res.drawable.btn_favorite_unselected
                        ),
                        contentDescription = "Favorite",
                        modifier = Modifier.size(ResponsiveAppSizes.IconMedium)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ResponsiveAppSizes.ButtonHeight)
                        .border(
                            width = ResponsiveAppBorders.Medium,
                            color = Color(0xFFAD8E7D),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                        )
                        .background(
                            color = Color(0xFF613923),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                        )
                        .clickable(onClick = onEditCopy),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Edit Copy",
                        color = Color(0xFFAD8E7D),
                        fontSize = ResponsiveAppTypography.BodyMedium,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ResponsiveAppSizes.ButtonHeight)
                        .border(
                            width = ResponsiveAppBorders.Medium,
                            color = Color(0xFFAD8E7D),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                        )
                        .background(
                            color = Color(0xFF360F01),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                        )
                        .clickable(onClick = { showDeleteDialog = true }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Delete",
                        color = Color(0xFFAD8E7D),
                        fontSize = ResponsiveAppTypography.BodyMedium,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Medium))
        }
        
        AppBottomNavigation(
            selectedTab = "history",
            onTabSelected = onNavigateToTab
        )
    }
    
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}

@Composable
fun DetailTopBar(
    title: String,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.ScreenHorizontal)
            .padding(top = AppSpacing.ScreenTop),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(AppSizes.TouchTarget)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = getBackIcon(),
                contentDescription = "Back",
                modifier = Modifier.size(AppSizes.IconMedium),
                colorFilter = ColorFilter.tint(AppColors.TitleText)
            )
        }
        
        Text(
            text = title,
            color = AppColors.TitleText,
            fontSize = 18.sp,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        
        Box(
            modifier = Modifier
                .size(AppSizes.TouchTarget)
                .clickable(onClick = onSettingsClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = getSettingsIcon(),
                contentDescription = "Settings",
                modifier = Modifier.size(AppSizes.IconMedium),
                colorFilter = ColorFilter.tint(AppColors.TitleText)
            )
        }
    }
}



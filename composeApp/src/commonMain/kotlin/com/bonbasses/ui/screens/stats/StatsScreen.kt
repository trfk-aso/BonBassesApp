package com.bonbasses.ui.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.img_stats
import com.bonbasses.ui.components.AppBottomNavigation
import com.bonbasses.ui.screens.home.getBackIcon
import com.bonbasses.ui.screens.home.getSettingsIcon
import com.bonbasses.platform.utils.DateFormatter
import com.bonbasses.ui.screens.result.getStarIcon
import com.bonbasses.ui.theme.*
import com.bonbasses.ui.theme.getBackgroundBrush
import com.bonbasses.ui.theme.ResponsiveAppTypography
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppRadius
import com.bonbasses.ui.theme.ResponsiveAppBorders
import com.bonbasses.ui.theme.LocalScreenSizeConfig
import org.jetbrains.compose.resources.painterResource

@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToTab: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val themeColors = LocalThemeColors.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatsTopBar(
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick
            )
            
            if (uiState.hasData) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PeriodChip(
                        text = "Week",
                        isSelected = uiState.selectedPeriod == StatsPeriod.WEEK,
                        onClick = { viewModel.selectPeriod(StatsPeriod.WEEK) },
                        modifier = Modifier.weight(1f)
                    )
                    PeriodChip(
                        text = "Month",
                        isSelected = uiState.selectedPeriod == StatsPeriod.MONTH,
                        onClick = { viewModel.selectPeriod(StatsPeriod.MONTH) },
                        modifier = Modifier.weight(1f)
                    )
                    PeriodChip(
                        text = "All",
                        isSelected = uiState.selectedPeriod == StatsPeriod.ALL,
                        onClick = { viewModel.selectPeriod(StatsPeriod.ALL) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)
                        .padding(top = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total",
                            value = uiState.total.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Last 7",
                            value = uiState.last7.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Last 30",
                            value = uiState.last30.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "AVG length",
                            value = uiState.avgLength,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "AVG score",
                            value = if (uiState.avgScore > 0) {
                                DateFormatter.formatDecimal(uiState.avgScore, decimals = 0)
                            } else {
                                "0"
                            },
                            showStar = uiState.avgScore > 0,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Streak",
                            value = uiState.streak.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No entries yet",
                        color = themeColors.emptyStateText,
                        fontSize = ResponsiveAppTypography.BodyMedium,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraLarge))
                    
                    Image(
                        painter = painterResource(Res.drawable.img_stats),
                        contentDescription = "No entries",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            AppBottomNavigation(
                selectedTab = "stats",
                onTabSelected = onNavigateToTab
            )
        }
    }
}

@Composable
fun StatsTopBar(
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
            text = "Stats",
            color = AppColors.TitleText,
            fontSize = 18.sp,
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
fun PeriodChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .border(
                width = 1.5.dp,
                color = Color(0xFFAD8E7D),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = if (isSelected) Color(0xFF7E512C) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFFD8BCAC) else AppColors.TitleText,
            fontSize = 16.sp,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    showStar: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = 1.5.dp,
                color = Color(0xFF8E7A6D),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = Color(0xFF312117),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = Color(0xFFD8BCAC),
                fontSize = 11.sp,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    color = Color(0xFFD8BCAC),
                    fontSize = 22.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Bold
                )
                
                if (showStar) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        painter = getStarIcon(),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(Color(0xFFD8BCAC))
                    )
                }
            }
        }
    }
}

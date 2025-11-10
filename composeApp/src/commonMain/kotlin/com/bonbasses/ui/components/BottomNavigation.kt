package com.bonbasses.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import com.bonbasses.ui.screens.home.*
import com.bonbasses.ui.theme.AppRadius
import com.bonbasses.ui.theme.AppSizes
import com.bonbasses.ui.theme.AppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.LocalThemeColors

@Composable
fun AppBottomNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeColors = LocalThemeColors.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ResponsiveAppSpacing.Medium)
            .padding(bottom = ResponsiveAppSpacing.ScreenBottom)
            .height(ResponsiveAppSizes.BottomNavHeight)
            .background(
                color = themeColors.bottomNavBackground,
                shape = RoundedCornerShape(AppRadius.Full)
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = getHomeIcon(),
            isSelected = selectedTab == "home",
            onClick = { onTabSelected("home") }
        )
        BottomNavItem(
            icon = getHistoryIcon(),
            isSelected = selectedTab == "history",
            onClick = { onTabSelected("history") }
        )
        BottomNavItem(
            icon = getSearchIcon(),
            isSelected = selectedTab == "search",
            onClick = { onTabSelected("search") }
        )
        BottomNavItem(
            icon = getFavoriteIcon(),
            isSelected = selectedTab == "favorite",
            onClick = { onTabSelected("favorite") }
        )
        BottomNavItem(
            icon = getStatsIcon(),
            isSelected = selectedTab == "stats",
            onClick = { onTabSelected("stats") }
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: Painter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    Box(
        modifier = Modifier
            .size(ResponsiveAppSizes.TouchTarget)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(ResponsiveAppSizes.BottomNavIconSize),
            colorFilter = ColorFilter.tint(
                if (isSelected) themeColors.bottomNavActive else themeColors.bottomNavInactive
            )
        )
    }
}

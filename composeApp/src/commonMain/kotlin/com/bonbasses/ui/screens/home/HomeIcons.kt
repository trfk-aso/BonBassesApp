package com.bonbasses.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun getHomeIcon(): Painter

@Composable
expect fun getHistoryIcon(): Painter

@Composable
expect fun getSearchIcon(): Painter

@Composable
expect fun getFavoriteIcon(): Painter

@Composable
expect fun getStatsIcon(): Painter

@Composable
expect fun getSettingsIcon(): Painter

@Composable
expect fun getAddIcon(): Painter

@Composable
expect fun getBackIcon(): Painter

@Composable
expect fun getFilterIcon(): Painter

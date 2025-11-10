package com.bonbasses.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.ic_add
import bonbassesapp.composeapp.generated.resources.ic_back
import bonbassesapp.composeapp.generated.resources.ic_favorite
import bonbassesapp.composeapp.generated.resources.ic_filters
import bonbassesapp.composeapp.generated.resources.ic_history
import bonbassesapp.composeapp.generated.resources.ic_home
import bonbassesapp.composeapp.generated.resources.ic_search
import bonbassesapp.composeapp.generated.resources.ic_settings
import bonbassesapp.composeapp.generated.resources.ic_stats
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun getHomeIcon(): Painter {
    return painterResource(Res.drawable.ic_home)
}

@Composable
actual fun getHistoryIcon(): Painter {
    return painterResource(Res.drawable.ic_history)
}

@Composable
actual fun getSearchIcon(): Painter {
    return painterResource(Res.drawable.ic_search)
}

@Composable
actual fun getFavoriteIcon(): Painter {
    return painterResource(Res.drawable.ic_favorite)
}

@Composable
actual fun getStatsIcon(): Painter {
    return painterResource(Res.drawable.ic_stats)
}

@Composable
actual fun getSettingsIcon(): Painter {
    return painterResource(Res.drawable.ic_settings)
}

@Composable
actual fun getAddIcon(): Painter {
    return painterResource(Res.drawable.ic_add)
}

@Composable
actual fun getBackIcon(): Painter {
    return painterResource(Res.drawable.ic_back)
}

@Composable
actual fun getFilterIcon(): Painter {
    return painterResource(Res.drawable.ic_filters)
}

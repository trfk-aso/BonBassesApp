package com.bonbasses.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.ic_filters
import com.bonbasses.app.R
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource as composePainterResource

@Composable
actual fun getHomeIcon(): Painter {
    return rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_home))
}

@Composable
actual fun getHistoryIcon(): Painter {
    return rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_history))
}

@Composable
actual fun getSearchIcon(): Painter {
    return rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_search))
}

@Composable
actual fun getFavoriteIcon(): Painter {
    return rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_favorite))
}

@Composable
actual fun getStatsIcon(): Painter {
    return rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_stats))
}

@Composable
actual fun getSettingsIcon(): Painter {
    return rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_settings))
}

@Composable
actual fun getAddIcon(): Painter {
    return rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_add))
}

@Composable
actual fun getBackIcon(): Painter {
    return rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_back))
}

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun getFilterIcon(): Painter {
    return composePainterResource(Res.drawable.ic_filters)
}

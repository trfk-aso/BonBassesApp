package com.bonbasses.ui.screens.result

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import com.bonbasses.app.R

@Composable
actual fun getStarIcon(): Painter {
    return rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_star))
}

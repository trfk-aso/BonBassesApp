package com.bonbasses.ui.screens.result

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.ic_star
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun getStarIcon(): Painter {
    return painterResource(Res.drawable.ic_star)
}

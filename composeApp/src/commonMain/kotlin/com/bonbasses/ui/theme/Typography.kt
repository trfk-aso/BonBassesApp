package com.bonbasses.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.roboto_slab_regular

@Composable
fun RobotoSlabFontFamily() = FontFamily(
    Font(Res.font.roboto_slab_regular, weight = FontWeight.Normal)
)

@Composable
fun AppTypography() = Typography(
    bodyLarge = TextStyle(
        fontFamily = RobotoSlabFontFamily(),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = RobotoSlabFontFamily(),
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    )
)
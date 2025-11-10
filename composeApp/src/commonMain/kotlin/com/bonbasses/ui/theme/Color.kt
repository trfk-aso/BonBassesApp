package com.bonbasses.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class ThemeColors(

    val backgroundGradientStart: Color,
    val backgroundGradientEnd: Color,
    

    val iconOnBackground: Color,
    val titleOnBackground: Color,
    val emptyStateText: Color,
    

    val bottomNavBackground: Color,
    val bottomNavActive: Color,
    val bottomNavInactive: Color,
    

    val loaderGradientStart: Color,
    val loaderGradientEnd: Color,
    val loaderBlurCircle: Color,
    

    val dialogBackground: Color,
    val dialogText: Color,
    val dialogStroke: Color,
    val dialogChipSelectedBackground: Color,
    val dialogChipSelectedText: Color,
    

    val disabledButtonBackground: Color,
    val disabledButtonBorder: Color,
    val disabledButtonText: Color
)

val DarkThemeColors = ThemeColors(
    backgroundGradientStart = Color(0xFF0C0400),
    backgroundGradientEnd = Color(0xFF150E0A),
    iconOnBackground = Color(0xFFAD8E7D),
    titleOnBackground = Color(0xFFAD8E7D),
    emptyStateText = Color(0xFFAD8E7D),
    bottomNavBackground = Color(0xFF312117),
    bottomNavActive = Color(0xFFAD8E7D),
    bottomNavInactive = Color(0xFF56453C),
    loaderGradientStart = Color(0xFF0C0400),
    loaderGradientEnd = Color(0xFF150E0A),
    loaderBlurCircle = Color(0xFFAD8E7D),
    dialogBackground = Color(0xFF1B0F08),
    dialogText = Color(0xFFAD8E7D),
    dialogStroke = Color(0xFFAD8E7D),
    dialogChipSelectedBackground = Color(0xFF7E512C),
    dialogChipSelectedText = Color(0xFFD8BCAC),
    disabledButtonBackground = Color(0x50613923),
    disabledButtonBorder = Color(0x50AD8E7D),
    disabledButtonText = Color(0x50AD8E7D)
)

val LightThemeColors = ThemeColors(
    backgroundGradientStart = Color(0xFFD2C3BB),
    backgroundGradientEnd = Color(0xFFC8B0A3),
    iconOnBackground = Color(0xFF312117),
    titleOnBackground = Color(0xFF312117),
    emptyStateText = Color(0xFF312117),
    bottomNavBackground = Color(0xFFB79F92),
    bottomNavActive = Color(0xFF56453C),
    bottomNavInactive = Color(0xFF92786A),
    loaderGradientStart = Color(0xFFD0C0B7),
    loaderGradientEnd = Color(0xFFB3988A),
    loaderBlurCircle = Color(0xFF805D49),
    dialogBackground = Color(0xFFB79F92),
    dialogText = Color(0xFF1B0F08),
    dialogStroke = Color(0xFF1B0F08),
    dialogChipSelectedBackground = Color(0xFF1B0F08),
    dialogChipSelectedText = Color(0xFFD8BCAC),
    disabledButtonBackground = Color(0xFFE5D9D1),
    disabledButtonBorder = Color(0xFFB8A89C),
    disabledButtonText = Color(0xFF8C7A6E)
)

val LocalThemeColors = staticCompositionLocalOf { DarkThemeColors }

@Composable
fun getBackgroundBrush(): Brush {
    val colors = LocalThemeColors.current
    return Brush.verticalGradient(
        colors = listOf(
            colors.backgroundGradientStart,
            colors.backgroundGradientEnd
        )
    )
}

val SplashBackground = Color(0xFF0E0E10)
val SloganBrown = Color(0xFFAD8E7D)

val QuizCardUnselectedFill = Color(0xFF381900)
val QuizCardUnselectedStroke = Color(0xFF1B0F08)
val QuizCardSelected = Color(0xFF7E512C)
val QuizTextColor = Color(0xFFD8BCAC)
val QuizPremiumTextColor = Color(0xFF1B0F08)
val QuizAlertTextColor = Color(0xFFAD8E7D)

val BottomNavBackground = Color(0xFF312117)
val BottomNavActive = Color(0xFFAD8E7D)
val BottomNavInactive = Color(0xFF56453C)

val HomeButtonBackground = Color(0xFF613923)
val HomeButtonText = Color(0xFFAD8E7D)
val HomeOfflineText = Color(0xFFAD8E7D)
val HomeCategoryBackground = Color(0xFF312117)
val HomeCategoryText = Color(0xFFD8BCAC)
val HomeCategoryStroke = Color(0xFF1B0F08)
val HomeWordChipBackground = Color(0xCCAD8E7D)
val HomeWordChipText = Color(0xFF1B0F08)
val HomeWordChipStroke = Color(0xFF1B0F08)

val WritingTitleText = Color(0xFFAD8E7D)
val WritingChipBackground = Color(0xFF312117)
val WritingChipStroke = Color(0xFFAD8E7D)
val WritingChipText = Color(0xFFAD8E7D)
val WritingEditorBackground = Color(0xFF312117)
val WritingEditorStroke = Color(0xFFAD8E7D)
val WritingEditorPlaceholder = Color(0xFFD8BCAC)
val WritingEditorCounter = Color(0xFFAD8E7D)
val WritingCanvasButtonBg = Color(0xFF312117)
val WritingCanvasButtonStroke = Color(0xFFAD8E7D)
val WritingCheckButtonBg = Color(0xFF613923)
val WritingCheckButtonStroke = Color(0xFFAD8E7D)
val WritingCheckButtonText = Color(0xFFAD8E7D)


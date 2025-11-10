package com.bonbasses.ui.theme

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object AppColors {
    val TitleText: Color
        @Composable
        get() = LocalThemeColors.current.titleOnBackground
    
    val BodyText = Color(0xFFD8BCAC)
    val SecondaryText = Color(0xFFA89080)
}

data class ScreenSizeConfig(
    val screenWidthDp: Float,
    val screenHeightDp: Float,
    val isSmallScreen: Boolean,
    val scaleFactor: Float
)

val LocalScreenSizeConfig = compositionLocalOf {
    ScreenSizeConfig(
        screenWidthDp = 400f,
        screenHeightDp = 800f,
        isSmallScreen = false,
        scaleFactor = 1f
    )
}

@Composable
fun ProvideScreenSizeConfig(content: @Composable () -> Unit) {
    BoxWithConstraints {
        val density = LocalDensity.current
        val screenWidthDp = with(density) { maxWidth.value }
        val screenHeightDp = with(density) { maxHeight.value }
        val isSmallScreen = screenWidthDp < 380f || screenHeightDp < 700f

        val scaleFactor = if (isSmallScreen) 0.85f else 1f
        
        val config = ScreenSizeConfig(
            screenWidthDp = screenWidthDp,
            screenHeightDp = screenHeightDp,
            isSmallScreen = isSmallScreen,
            scaleFactor = scaleFactor
        )
        
        CompositionLocalProvider(LocalScreenSizeConfig provides config) {
            content()
        }
    }
}

object AppTypography {
    val TitleLarge: TextUnit = 20.sp
    val TitleMedium: TextUnit = 18.sp
    val TitleSmall: TextUnit = 16.sp
    
    val BodyLarge: TextUnit = 16.sp
    val BodyMedium: TextUnit = 14.sp
    val BodySmall: TextUnit = 13.sp
    
    val CaptionSmall: TextUnit = 12.sp
}

object ResponsiveAppTypography {
    val TitleLarge: TextUnit
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (20 * config.scaleFactor).sp
        }
    
    val TitleMedium: TextUnit
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (18 * config.scaleFactor).sp
        }
    
    val TitleSmall: TextUnit
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (16 * config.scaleFactor).sp
        }
    
    val BodyLarge: TextUnit
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (16 * config.scaleFactor).sp
        }
    
    val BodyMedium: TextUnit
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (14 * config.scaleFactor).sp
        }
    
    val BodySmall: TextUnit
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (13 * config.scaleFactor).sp
        }
    
    val HeadlineSmall: TextUnit
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (20 * config.scaleFactor).sp
        }
    
    val HeadlineMedium: TextUnit
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (24 * config.scaleFactor).sp
        }
    
    val DisplayLarge: TextUnit
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (48 * config.scaleFactor).sp
        }
    
    val CaptionSmall: TextUnit
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (12 * config.scaleFactor).sp
        }
}

object AppSpacing {
    val ScreenHorizontal: Dp = 20.dp
    val ScreenTop: Dp = 60.dp
    val ScreenBottom: Dp = 34.dp
    
    val ExtraLarge: Dp = 40.dp
    val Large: Dp = 32.dp
    val Medium: Dp = 24.dp
    val Small: Dp = 16.dp
    val ExtraSmall: Dp = 8.dp
    
    val CardPadding: Dp = 20.dp
    val ButtonPaddingVertical: Dp = 12.dp
    val ButtonPaddingHorizontal: Dp = 24.dp
}

object ResponsiveAppSpacing {
    val ScreenHorizontal: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (20 * config.scaleFactor).dp
        }
    
    val ScreenTop: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return if (config.isSmallScreen) 40.dp else 60.dp
        }
    
    val ScreenBottom: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return if (config.isSmallScreen) 20.dp else 34.dp
        }
    
    val ExtraLarge: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (40 * config.scaleFactor).dp
        }
    
    val Large: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (32 * config.scaleFactor).dp
        }
    
    val Medium: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (24 * config.scaleFactor).dp
        }
    
    val Small: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (16 * config.scaleFactor).dp
        }
    
    val ExtraSmall: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (8 * config.scaleFactor).dp
        }
    
    val CardPadding: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (20 * config.scaleFactor).dp
        }
    
    val ButtonPaddingVertical: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (12 * config.scaleFactor).dp
        }
    
    val ButtonPaddingHorizontal: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (24 * config.scaleFactor).dp
        }
}

object AppSizes {
    val IconSmall: Dp = 20.dp
    val IconMedium: Dp = 24.dp
    val IconLarge: Dp = 32.dp
    
    val ButtonHeight: Dp = 48.dp
    val ButtonHeightSmall: Dp = 40.dp
    
    val TouchTarget: Dp = 44.dp
    
    val BottomNavHeight: Dp = 68.dp
    val BottomNavIconSize: Dp = 24.dp
}

object ResponsiveAppSizes {
    val IconSmall: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (20 * config.scaleFactor).dp
        }
    
    val IconMedium: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (24 * config.scaleFactor).dp
        }
    
    val IconLarge: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (32 * config.scaleFactor).dp
        }
    
    val ButtonHeight: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (48 * config.scaleFactor).dp
        }
    
    val ButtonHeightSmall: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (40 * config.scaleFactor).dp
        }
    
    val TouchTarget: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (44 * config.scaleFactor).dp
        }
    
    val BottomNavHeight: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return if (config.isSmallScreen) 60.dp else 68.dp
        }
    
    val BottomNavIconSize: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (24 * config.scaleFactor).dp
        }
    
    val Small: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (16 * config.scaleFactor).dp
        }
    
    val Medium: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (24 * config.scaleFactor).dp
        }
    
    val Large: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (32 * config.scaleFactor).dp
        }
    
    val ExtraSmall: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (8 * config.scaleFactor).dp
        }
}

object AppRadius {
    val Small: Dp = 8.dp
    val Medium: Dp = 12.dp
    val Large: Dp = 15.dp
    val ExtraLarge: Dp = 20.dp
    val Full: Dp = 100.dp
}

object ResponsiveAppRadius {
    val Small: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (8 * config.scaleFactor).dp
        }
    
    val Medium: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (12 * config.scaleFactor).dp
        }
    
    val Large: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (15 * config.scaleFactor).dp
        }
    
    val ExtraLarge: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (20 * config.scaleFactor).dp
        }
    
    val Full: Dp = 100.dp
}

object AppBorders {
    val Thin: Dp = 1.dp
    val Medium: Dp = 1.5.dp
    val Thick: Dp = 2.dp
}

object ResponsiveAppBorders {
    val Thin: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (1 * config.scaleFactor).dp
        }
    
    val Medium: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (1.5f * config.scaleFactor).dp
        }
    
    val Thick: Dp
        @Composable
        get() {
            val config = LocalScreenSizeConfig.current
            return (2 * config.scaleFactor).dp
        }
}


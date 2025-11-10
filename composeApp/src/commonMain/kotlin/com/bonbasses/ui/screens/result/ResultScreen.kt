package com.bonbasses.ui.screens.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.btn_favorite_selected
import bonbassesapp.composeapp.generated.resources.btn_favorite_unselected
import com.bonbasses.ui.components.AppBottomNavigation
import com.bonbasses.ui.theme.AppBorders
import com.bonbasses.ui.theme.AppRadius
import com.bonbasses.ui.theme.AppSizes
import com.bonbasses.ui.theme.AppTypography
import com.bonbasses.ui.theme.AppColors
import com.bonbasses.ui.theme.ResponsiveAppTypography
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppRadius
import com.bonbasses.ui.theme.ResponsiveAppBorders
import com.bonbasses.ui.theme.RobotoSlabFontFamily
import com.bonbasses.ui.theme.LocalThemeColors
import com.bonbasses.ui.theme.getBackgroundBrush
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

enum class LoaderState {
    READING,
    ALMOST_THERE,
    DONE
}

@Composable
fun ResultLoaderScreen(
    onLoadingComplete: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    var loaderState by remember { mutableStateOf(LoaderState.READING) }
    val infiniteTransition = rememberInfiniteTransition()
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    LaunchedEffect(Unit) {
        delay(800)
        loaderState = LoaderState.ALMOST_THERE
        delay(800)
        loaderState = LoaderState.DONE
        onLoadingComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        themeColors.loaderGradientStart,
                        themeColors.loaderGradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                themeColors.loaderBlurCircle.copy(alpha = 0.8f),
                                themeColors.loaderBlurCircle.copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            radius = 600f
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        Text(
            text = when (loaderState) {
                LoaderState.READING -> "Reading your story…"
                LoaderState.ALMOST_THERE -> "Almost there…"
                LoaderState.DONE -> "Done!"
            },
            color = Color(0xFFD8BCAC).copy(alpha = textAlpha),
            fontSize = ResponsiveAppTypography.TitleMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun ResultFailedScreen(
    charCount: Int,
    recommendations: List<String>,
    onTryAgain: () -> Unit,
    onContinueWriting: () -> Unit,
    onSaveDraft: () -> Unit,
    onBack: () -> Unit,
    onNavigateToTab: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ScreenTop))
                
                Text(
                    text = "Too short (< 300 chars)",
                    color = AppColors.TitleText,
                    fontSize = ResponsiveAppTypography.TitleLarge,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraSmall))
                
                Text(
                    text = "Keep going!",
                    color = AppColors.TitleText,
                    fontSize = ResponsiveAppTypography.BodyMedium,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Large))
                
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.Small)
                ) {
                    items(recommendations) { recommendation ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(ResponsiveAppRadius.Large))
                                .border(
                                    width = ResponsiveAppBorders.Medium,
                                    color = Color(0xFFAD8E7D),
                                    shape = RoundedCornerShape(ResponsiveAppRadius.Large)
                                )
                                .background(Color(0xFF312117))
                                .padding(ResponsiveAppSpacing.Small),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = recommendation,
                                color = Color(0xFFAD8E7D),
                                fontSize = ResponsiveAppTypography.BodyMedium,
                                fontFamily = RobotoSlabFontFamily(),
                                fontWeight = FontWeight.Normal,
                                lineHeight = ResponsiveAppTypography.TitleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Medium))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.Small)
                ) {
                    ResultButton(
                        text = "Try Again",
                        onClick = onTryAgain,
                        modifier = Modifier.weight(1f)
                    )
                    
                    ResultButton(
                        text = "Continue Writing",
                        onClick = onContinueWriting,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
                
                ResultButton(
                    text = "Save Draft",
                    onClick = onSaveDraft,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ScreenBottom))
            }
            
            AppBottomNavigation(
                selectedTab = "result",
                onTabSelected = onNavigateToTab
            )
        }
    }
}

@Composable
fun ResultSuccessScreen(
    score: Int,
    feedback: List<String>,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSaveToHistory: () -> Unit,
    onShareAsTxt: () -> Unit,
    onBack: () -> Unit,
    onNavigateToTab: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ScreenTop))
                
                Text(
                    text = "Successfully",
                    color = AppColors.TitleText,
                    fontSize = ResponsiveAppTypography.TitleLarge,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Medium))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.ExtraSmall)
                ) {
                    repeat(5) { index ->
                        Image(
                            painter = getStarIcon(),
                            contentDescription = "Star",
                            modifier = Modifier.size(ResponsiveAppSizes.IconLarge),
                            alpha = if (index < score) 1f else 0.3f,
                            colorFilter = ColorFilter.tint(AppColors.TitleText)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Large))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(ResponsiveAppRadius.Large))
                        .border(
                            width = ResponsiveAppBorders.Medium,
                            color = Color(0xFFAD8E7D),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Large)
                        )
                        .background(Color(0xFF312117))
                        .padding(ResponsiveAppSpacing.CardPadding),
                    contentAlignment = Alignment.Center
                ) {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.Small)
                    ) {
                        item {
                            Text(
                                text = "Feedback",
                                color = Color(0xFFAD8E7D),
                                fontSize = ResponsiveAppTypography.TitleSmall,
                                fontFamily = RobotoSlabFontFamily(),
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        item {
                            Text(
                                text = feedback.joinToString(" "),
                                color = Color(0xFFAD8E7D),
                                fontSize = ResponsiveAppTypography.BodyMedium,
                                fontFamily = RobotoSlabFontFamily(),
                                fontWeight = FontWeight.Normal,
                                lineHeight = ResponsiveAppTypography.TitleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Medium))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.Small)
                ) {
                    ResultButton(
                        text = "Share as TXT",
                        onClick = onShareAsTxt,
                        modifier = Modifier.weight(1f)
                    )
                    
                    ResultButton(
                        text = "Save to History",
                        onClick = onSaveToHistory,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(ResponsiveAppSizes.ButtonHeight)
                            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium))
                            .border(
                                width = ResponsiveAppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                            )
                            .background(Color(0xFF613923))
                            .clickable(onClick = onToggleFavorite),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                if (isFavorite) Res.drawable.btn_favorite_selected 
                                else Res.drawable.btn_favorite_unselected
                            ),
                            contentDescription = "Favorite",
                            modifier = Modifier.size(ResponsiveAppSizes.IconMedium)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ScreenBottom))
            }
            
            AppBottomNavigation(
                selectedTab = "result",
                onTabSelected = onNavigateToTab
            )
        }
    }
}

@Composable
fun ResultButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(ResponsiveAppSizes.ButtonHeight)
            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium))
            .border(
                width = ResponsiveAppBorders.Medium,
                color = Color(0xFFAD8E7D),
                shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
            )
            .background(Color(0xFF613923))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFAD8E7D),
            fontSize = ResponsiveAppTypography.BodyMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

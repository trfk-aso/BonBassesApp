package com.bonbasses.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.logo_bonbasses
import com.bonbasses.ui.theme.RobotoSlabFontFamily
import com.bonbasses.ui.theme.SloganBrown
import com.bonbasses.ui.theme.SplashBackground
import com.bonbasses.ui.theme.ResponsiveAppTypography
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppRadius
import com.bonbasses.ui.theme.ResponsiveAppBorders
import com.bonbasses.ui.theme.LocalScreenSizeConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(
    isFirstLaunch: Boolean,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = viewModel { SplashViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.95f) }
    val sloganAlpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 600)
            )
        }
        launch {
            sloganAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }
        
        delay(600)
        
        viewModel.checkFirstLaunch(isFirstLaunch)
    }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is SplashUiState.NavigateToOnboarding -> onNavigateToOnboarding()
            is SplashUiState.NavigateToHome -> onNavigateToHome()
            is SplashUiState.Loading -> { }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        SplashBackground,
                        SplashBackground.copy(alpha = 0.95f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.logo_bonbasses),
                contentDescription = "Bon Basses Logo",
                modifier = Modifier
                    .size(240.dp)
                    .alpha(logoAlpha.value)
                    .scale(logoScale.value)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "10 words. 7 minutes. One story.",
                color = SloganBrown,
                fontSize = 16.sp,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.alpha(sloganAlpha.value)
            )
        }
    }
}
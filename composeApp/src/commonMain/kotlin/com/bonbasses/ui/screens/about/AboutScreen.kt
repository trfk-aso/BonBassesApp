package com.bonbasses.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.logo_bonbasses
import com.bonbasses.ui.components.AppBottomNavigation
import com.bonbasses.ui.screens.home.getBackIcon
import com.bonbasses.ui.theme.*
import com.bonbasses.ui.theme.getBackgroundBrush
import com.bonbasses.ui.theme.ResponsiveAppTypography
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppRadius
import com.bonbasses.ui.theme.ResponsiveAppBorders
import com.bonbasses.ui.theme.LocalScreenSizeConfig
import org.jetbrains.compose.resources.painterResource

@Composable
fun AboutScreen(
    onBackClick: () -> Unit = {},
    onNavigateToTab: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
    ) {

        AboutTopBar(onBackClick = onBackClick)
        

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            

            Image(
                painter = painterResource(Res.drawable.logo_bonbasses),
                contentDescription = "BonBasses Logo",
                modifier = Modifier
                    .size(200.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            

            Text(
                text = "Write a micro-story in 7 minutes. We give you 10 prompt words, you write. Hit Check to get instant feedback: a simple 3-5 score and a handful of warm, encouraging phrases. Everything works offline. Your drafts and results stay on your device.",
                color = AppColors.TitleText,
                fontSize = 14.sp,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "No accounts. No cloud.\nYour data stays on your device.",
                color = AppColors.TitleText,
                fontSize = 14.sp,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            

            AboutButton(
                text = "Privacy Policy",
                onClick = {  }
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            

            AboutButton(
                text = "Terms of Use",
                onClick = {  }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            

            Text(
                text = "App Version 1.1",
                color = AppColors.TitleText,
                fontSize = ResponsiveAppTypography.BodySmall,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        

        AppBottomNavigation(
            selectedTab = "about",
            onTabSelected = onNavigateToTab
        )
    }
}

@Composable
fun AboutTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)
            .padding(top = ResponsiveAppSpacing.ScreenTop, bottom = ResponsiveAppSpacing.Small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ResponsiveAppSizes.TouchTarget)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = getBackIcon(),
                contentDescription = "Back",
                modifier = Modifier.size(11.dp, 20.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(AppColors.TitleText)
            )
        }
        
        Text(
            text = "About",
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.TitleMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.size(ResponsiveAppSizes.TouchTarget))
    }
}

@Composable
fun AboutButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(48.dp)
            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium))
            .background(Color(0xFF613923))
            .border(
                width = ResponsiveAppBorders.Medium,
                color = Color(0xFFAD8E7D),
                shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFAD8E7D),
            fontSize = ResponsiveAppTypography.BodyMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal
        )
    }
}

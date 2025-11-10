package com.bonbasses.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.viewmodel.compose.viewModel
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.ic_lock
import bonbassesapp.composeapp.generated.resources.switch_off
import bonbassesapp.composeapp.generated.resources.switch_on
import com.bonbasses.platform.iap.IAPManager
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    iapManager: IAPManager,
    onBackClick: () -> Unit = {},
    onNavigateToTab: (String) -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onThemeChanged: (ThemeMode) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val purchaseState by iapManager.purchaseState.collectAsState()
    

    LaunchedEffect(uiState.selectedTheme) {
        onThemeChanged(uiState.selectedTheme)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
    ) {

        SettingsTopBar(onBackClick = onBackClick)
        

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Medium))
            

            Text(
                text = "Themes",
                color = AppColors.TitleText,
                fontSize = ResponsiveAppTypography.TitleSmall,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ThemeChip(
                    text = "Dark",
                    isSelected = uiState.selectedTheme == ThemeMode.DARK,
                    onClick = { viewModel.selectTheme(ThemeMode.DARK) },
                    modifier = Modifier.weight(1f)
                )
                ThemeChip(
                    text = "Light",
                    isSelected = uiState.selectedTheme == ThemeMode.LIGHT,
                    onClick = { viewModel.selectTheme(ThemeMode.LIGHT) },
                    modifier = Modifier.weight(1f)
                )
                ThemeChip(
                    text = "System",
                    isSelected = uiState.selectedTheme == ThemeMode.SYSTEM,
                    onClick = { viewModel.selectTheme(ThemeMode.SYSTEM) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Large))
            

            Text(
                text = "Preferences",
                color = AppColors.TitleText,
                fontSize = ResponsiveAppTypography.TitleSmall,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
            

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Timer length",
                    color = AppColors.TitleText,
                    fontSize = ResponsiveAppTypography.BodyMedium,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimerLengthChip(
                        text = "7 min",
                        isSelected = uiState.timerLength == 7,
                        onClick = { viewModel.selectTimerLength(7) },
                        modifier = Modifier.width(80.dp)
                    )

                    val isTimerPurchased = purchaseState["com.bonbasses.timer10"] == true
                    TimerLengthChip(
                        text = "10 min",
                        isSelected = uiState.timerLength == 10,
                        isUpgrade = !isTimerPurchased,
                        onClick = { viewModel.selectTimerLength(10) },
                        modifier = Modifier.width(90.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
            

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Typing sounds",
                    color = AppColors.TitleText,
                    fontSize = ResponsiveAppTypography.BodyMedium,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal
                )
                
                CustomSwitch(
                    checked = uiState.typingSoundsEnabled,
                    enabled = true,
                    onCheckedChange = { viewModel.toggleTypingSounds() }
                )
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
            

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gentle haptics",
                    color = AppColors.TitleText,
                    fontSize = ResponsiveAppTypography.BodyMedium,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal
                )
                
                CustomSwitch(
                    checked = uiState.gentleHapticsEnabled,
                    enabled = true,
                    onCheckedChange = { viewModel.toggleGentleHaptics() }
                )
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Medium))
            

            Text(
                text = "Data",
                color = AppColors.TitleText,
                fontSize = ResponsiveAppTypography.TitleSmall,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraSmall + 4.dp))
            
            SettingsButton(
                text = "Export TXT/CSV",
                onClick = { viewModel.exportData() },
                backgroundColor = Color(0xFF613923),
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraSmall))
            
            SettingsButton(
                text = "About",
                onClick = onNavigateToAbout,
                backgroundColor = Color(0xFF613923),
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraSmall))
            
            SettingsButton(
                text = "Reset All",
                onClick = { viewModel.showResetDialog() },
                backgroundColor = Color(0xFF360F01),
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
        }
        

        AppBottomNavigation(
            selectedTab = "settings",
            onTabSelected = onNavigateToTab
        )
    }
    

    if (uiState.showUpgradeDialog) {
        UpgradeTimerDialog(
            iapManager = iapManager,
            onDismiss = { viewModel.dismissUpgradeDialog() },
            onPurchaseSuccess = { 
                viewModel.onTimerPurchased()
            }
        )
    }
    
    if (uiState.showResetDialog) {
        ResetAllDialog(
            onDismiss = { viewModel.dismissResetDialog() },
            onConfirm = { viewModel.resetAll() }
        )
    }
}

@Composable
fun SettingsTopBar(onBackClick: () -> Unit) {
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
            text = "Settings",
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.TitleMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.size(ResponsiveAppSizes.TouchTarget))
    }
}

@Composable
fun ThemeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val backgroundColor = when (text) {
        "Dark" -> Color(0xFF312117)
        "Light" -> Brush.linearGradient(
            colors = listOf(Color(0xFFD2C3BB), Color(0xFFC8B0A3))
        )
        "System" -> Color(0xFFAD8E7D)
        else -> Color.Transparent
    }
    
    val textColor = when (text) {
        "Dark" -> Color(0xFFAD8E7D)
        "Light" -> Color(0xFF312117)
        "System" -> Color(0xFF312117)
        else -> Color(0xFFAD8E7D)
    }
    
    val borderColor = when (text) {
        "Light" -> Color(0xFF312117)
        else -> Color(0xFFAD8E7D)
    }
    
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium))
            .then(
                if (backgroundColor is Color) {
                    Modifier.background(backgroundColor)
                } else {
                    Modifier.background(backgroundColor as Brush)
                }
            )
            .border(
                width = if (isSelected) ResponsiveAppBorders.Thick else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = ResponsiveAppTypography.BodyMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun TimerLengthChip(
    text: String,
    isSelected: Boolean,
    isUpgrade: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium))
            .background(
                if (isSelected) Color(0xFF613923) else Color(0xFF312117)
            )
            .border(
                width = ResponsiveAppBorders.Medium,
                color = Color(0xFFAD8E7D),
                shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isUpgrade) {
                Image(
                    painter = painterResource(Res.drawable.ic_lock),
                    contentDescription = "Locked",
                    modifier = Modifier.size(14.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFAD8E7D))
                )
            }
            Text(
                text = text,
                color = Color(0xFFAD8E7D),
                fontSize = ResponsiveAppTypography.BodyMedium,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 24.dp)
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
    ) {
        Image(
            painter = painterResource(
                if (checked && enabled) Res.drawable.switch_on 
                else Res.drawable.switch_off
            ),
            contentDescription = if (checked) "On" else "Off",
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(AppColors.TitleText)
        )
    }
}

@Composable
fun SettingsButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(ResponsiveAppSizes.ButtonHeight)
            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium))
            .background(backgroundColor)
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
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun UpgradeTimerDialog(
    iapManager: IAPManager,
    onDismiss: () -> Unit,
    onPurchaseSuccess: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(ResponsiveAppRadius.ExtraLarge))
                .background(themeColors.dialogBackground)
                .border(
                    width = ResponsiveAppBorders.Thick,
                    color = themeColors.dialogStroke,
                    shape = RoundedCornerShape(ResponsiveAppRadius.ExtraLarge)
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Upgrade to 10 min",
                    color = themeColors.dialogText,
                    fontSize = 22.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Get extra time to write. Save all the ideas, plot twists, and emotions. Continue your story without rushing!",
                    color = themeColors.dialogText,
                    fontSize = 16.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = Color(0xFFE57373),
                        fontSize = 14.sp,
                        fontFamily = RobotoSlabFontFamily(),
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.Small)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ResponsiveAppSizes.ButtonHeight)
                            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium))
                            .background(Color(0xFF360F01))
                            .border(
                                width = ResponsiveAppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                            )
                            .clickable(enabled = !isLoading, onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFFAD8E7D),
                            fontSize = ResponsiveAppTypography.BodyMedium,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ResponsiveAppSizes.ButtonHeight)
                            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium))
                            .background(Color(0xFF613923))
                            .border(
                                width = ResponsiveAppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                            )
                            .clickable(enabled = !isLoading) {
                                scope.launch {
                                    isLoading = true
                                    errorMessage = null
                                    when (val result = iapManager.purchase("com.bonbasses.timer10")) {
                                        is com.bonbasses.platform.iap.PurchaseResult.Success -> {
                                            isLoading = false
                                            onPurchaseSuccess()
                                        }
                                        is com.bonbasses.platform.iap.PurchaseResult.Error -> {
                                            isLoading = false
                                            errorMessage = result.message
                                        }
                                        is com.bonbasses.platform.iap.PurchaseResult.Cancelled -> {
                                            isLoading = false
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFFAD8E7D),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Buy $1.99",
                                color = Color(0xFFAD8E7D),
                                fontSize = ResponsiveAppTypography.BodyMedium,
                                fontFamily = RobotoSlabFontFamily(),
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResetAllDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(ResponsiveAppRadius.ExtraLarge))
                .background(themeColors.dialogBackground)
                .border(
                    width = ResponsiveAppBorders.Thick,
                    color = themeColors.dialogStroke,
                    shape = RoundedCornerShape(ResponsiveAppRadius.ExtraLarge)
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Reset All Data?",
                    color = themeColors.dialogText,
                    fontSize = 20.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "This will delete all your stories, preferences and statistics. This action cannot be undone.",
                    color = themeColors.dialogText,
                    fontSize = 14.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.Small)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ResponsiveAppSizes.ButtonHeight)
                            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium))
                            .background(Color(0xFF360F01))
                            .border(
                                width = ResponsiveAppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                            )
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFFAD8E7D),
                            fontSize = ResponsiveAppTypography.BodyMedium,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ResponsiveAppSizes.ButtonHeight)
                            .clip(RoundedCornerShape(ResponsiveAppRadius.Medium))
                            .background(Color(0xFF613923))
                            .border(
                                width = ResponsiveAppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                            )
                            .clickable(onClick = onConfirm),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Reset",
                            color = Color(0xFFAD8E7D),
                            fontSize = ResponsiveAppTypography.BodyMedium,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

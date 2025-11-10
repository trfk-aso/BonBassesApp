package com.bonbasses.ui.screens.writing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import com.bonbasses.ui.theme.AppBorders
import com.bonbasses.ui.theme.AppColors
import com.bonbasses.ui.theme.AppRadius
import com.bonbasses.ui.theme.AppSizes
import com.bonbasses.ui.theme.AppSpacing
import com.bonbasses.ui.theme.AppTypography
import com.bonbasses.ui.theme.ResponsiveAppTypography
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppRadius
import com.bonbasses.ui.theme.ResponsiveAppBorders
import com.bonbasses.ui.theme.LocalScreenSizeConfig
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.bg_focus
import bonbassesapp.composeapp.generated.resources.bg_typewriter
import bonbassesapp.composeapp.generated.resources.ic_lock
import com.bonbasses.data.preferences.QuizPreferences
import com.bonbasses.platform.iap.IAPProducts
import com.bonbasses.platform.HapticFeedback
import com.bonbasses.platform.iap.IAPManager
import com.bonbasses.platform.ui.PlatformBackHandler
import com.bonbasses.ui.components.AppBottomNavigation
import com.bonbasses.ui.screens.home.getBackIcon
import com.bonbasses.ui.screens.home.getSettingsIcon
import com.bonbasses.ui.theme.RobotoSlabFontFamily
import com.bonbasses.ui.theme.getBackgroundBrush
import com.bonbasses.ui.theme.LocalThemeColors
import com.bonbasses.ui.theme.WritingCanvasButtonBg
import com.bonbasses.ui.theme.WritingCanvasButtonStroke
import com.bonbasses.ui.theme.WritingCheckButtonBg
import com.bonbasses.ui.theme.WritingCheckButtonStroke
import com.bonbasses.ui.theme.WritingCheckButtonText
import com.bonbasses.ui.theme.WritingChipBackground
import com.bonbasses.ui.theme.WritingChipStroke
import com.bonbasses.ui.theme.WritingChipText
import com.bonbasses.ui.theme.WritingEditorBackground
import com.bonbasses.ui.theme.WritingEditorPlaceholder
import com.bonbasses.ui.theme.WritingEditorStroke
import com.bonbasses.ui.theme.WritingTitleText
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun WritingScreen(
    words: List<String> = emptyList(),
    initialText: String = "",
    initialTimeLeft: Int = 420,
    timerDurationMinutes: Int = 7,
    iapManager: IAPManager,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onCheckClick: (String, Int, Int) -> Unit = { _, _, _ -> },
    onNavigateToTab: (String) -> Unit = {},
    onStateChanged: (String, Int) -> Unit = { _, _ -> },
    quizPreferences: QuizPreferences? = null
) {
    var timerHintsJson by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        timerHintsJson = try {
            Res.readBytes("files/timer_hints.json").decodeToString()
        } catch (e: Exception) {
            ""
        }
    }
    

    val viewModel = remember(words) { 
        WritingViewModel(
            promptWords = words,
            initialText = initialText,
            initialTimeLeft = initialTimeLeft,
            timerDurationMinutes = timerDurationMinutes,
            quizPreferences = quizPreferences,
            iapManager = iapManager
        ) 
    }
    
    LaunchedEffect(timerHintsJson) {
        if (timerHintsJson.isNotBlank()) {
            viewModel.setTimerHints(timerHintsJson)
        }
    }
    
    val uiState by viewModel.uiState.collectAsState()
    

    LaunchedEffect(uiState.storyText, uiState.timeLeftSeconds) {
        onStateChanged(uiState.storyText, uiState.timeLeftSeconds)
    }
    

    PlatformBackHandler {
        viewModel.onBackClicked(onBackClick)
    }
    
    LaunchedEffect(uiState.shouldTriggerHaptic) {
        if (uiState.shouldTriggerHaptic) {
            HapticFeedback.trigger()
            viewModel.acknowledgeHaptic()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
    ) {
        val screenConfig = LocalScreenSizeConfig.current
        val editorHeight = if (screenConfig.isSmallScreen) 200.dp else 280.dp
        
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ScreenTop))
            
            WritingTopBar(
                timeLeft = viewModel.formatTime(uiState.timeLeftSeconds),
                onBackClick = { viewModel.onBackClicked(onBackClick) },
                onSettingsClick = onSettingsClick
            )
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
            
            Box(modifier = Modifier.padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)) {
                WordChips(words = uiState.words)
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
            
            Box(modifier = Modifier.padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)) {
                StoryEditor(
                    text = uiState.storyText,
                    onTextChange = viewModel::onTextChanged,
                    charCount = uiState.charCount,
                    wordCount = uiState.wordCount,
                    canvasMode = uiState.selectedCanvas,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(editorHeight)
                )
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
            
            Box(modifier = Modifier.padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)) {
                CanvasSelector(
                    selectedCanvas = uiState.selectedCanvas,
                    onCanvasSelected = viewModel::selectCanvas,
                    iapManager = iapManager
                )
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.Small))
            
            Box(modifier = Modifier.padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)) {
                CheckButton(
                    onClick = {
                        viewModel.onCheckClicked()
                        onCheckClick(uiState.storyText, uiState.charCount, uiState.timeLeftSeconds)
                    },
                    enabled = uiState.storyText.isNotBlank()
                )
            }
            
            Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ScreenBottom))
        }
        
        AppBottomNavigation(
            selectedTab = "writing",
            onTabSelected = onNavigateToTab
        )
    }
    
    if (uiState.timerHint != null) {
        TimerHintToast(
            hint = uiState.timerHint!!,
            onDismiss = { viewModel.dismissTimerHint() }
        )
    }
    
    if (uiState.showTimeUpToast) {
        TimerHintToast(
            hint = "Time's up — you can still Check",
            onDismiss = { viewModel.dismissTimeUpToast() }
        )
    }
    
    if (uiState.showExitDialog) {
        ExitConfirmationDialog(
            onDismiss = { viewModel.dismissExitDialog() },
            onConfirm = {
                viewModel.dismissExitDialog()
                if (viewModel.confirmExit()) {
                    onBackClick()
                }
            }
        )
    }
    
    if (uiState.showPaywallDialog) {
        CanvasUnlockDialog(
            iapManager = iapManager,
            productId = uiState.lockedCanvasProductId ?: IAPProducts.CANVAS_PACK,
            onDismiss = { viewModel.dismissPaywallDialog() }
        )
    }
}

@Composable
fun WritingTopBar(
    timeLeft: String,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
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
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Writing — $timeLeft",
            color = AppColors.TitleText,
            fontSize = 18.sp,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Box(
            modifier = Modifier
                .size(44.dp)
                .clickable(onClick = onSettingsClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = getSettingsIcon(),
                contentDescription = "Settings",
                modifier = Modifier.size(24.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(AppColors.TitleText)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordChips(words: List<String>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.ExtraSmall, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.ExtraSmall)
    ) {
        words.forEach { word ->
            Box(
                modifier = Modifier
                    .border(
                        width = ResponsiveAppBorders.Thin,
                        color = WritingChipStroke,
                        shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                    )
                    .background(
                        color = WritingChipBackground,
                        shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                    )
                    .padding(
                        horizontal = ResponsiveAppSpacing.Small, 
                        vertical = ResponsiveAppSpacing.ExtraSmall
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = word,
                    color = WritingChipText,
                    fontSize = ResponsiveAppTypography.BodySmall,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun StoryEditor(
    text: String,
    onTextChange: (String) -> Unit,
    charCount: Int,
    wordCount: Int,
    canvasMode: CanvasMode = CanvasMode.CLASSIC,
    modifier: Modifier = Modifier
) {
    val backgroundImage = when (canvasMode) {
        CanvasMode.TYPEWRITER -> Res.drawable.bg_typewriter
        CanvasMode.FOCUS -> Res.drawable.bg_focus
        else -> null
    }
    
    val textColor = when (canvasMode) {
        CanvasMode.TYPEWRITER, CanvasMode.FOCUS -> Color(0xFF312117)
        else -> WritingEditorPlaceholder
    }
    
    val widthFraction = when (canvasMode) {
        CanvasMode.CLASSIC -> 0.9f
        CanvasMode.FOCUS -> 0.66f
        else -> 0.9f
    }
    
    val scrollState = rememberScrollState()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ResponsiveAppRadius.Large))
            .border(
                width = ResponsiveAppBorders.Medium,
                color = WritingEditorStroke,
                shape = RoundedCornerShape(ResponsiveAppRadius.Large)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (backgroundImage != null) {
            Image(
                painter = painterResource(backgroundImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WritingEditorBackground)
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ResponsiveAppSpacing.Small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(widthFraction)
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { newText ->
                        if (newText.length <= 1000) {
                            onTextChange(newText)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = ResponsiveAppTypography.BodyMedium,
                        fontFamily = if (canvasMode == CanvasMode.TYPEWRITER) {
                            androidx.compose.ui.text.font.FontFamily.Monospace
                        } else {
                            RobotoSlabFontFamily()
                        },
                        fontWeight = FontWeight.Normal,
                        lineHeight = ResponsiveAppTypography.TitleLarge
                    ),
                    cursorBrush = SolidColor(textColor),
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
                            Text(
                                text = "Text of the story",
                                color = textColor.copy(alpha = 0.5f),
                                fontSize = ResponsiveAppTypography.BodyMedium,
                                fontFamily = RobotoSlabFontFamily(),
                                fontWeight = FontWeight.Normal
                            )
                        }
                        innerTextField()
                    }
                )
            }
            
            Text(
                text = "$charCount/1000",
                color = textColor.copy(alpha = 0.6f),
                fontSize = ResponsiveAppTypography.CaptionSmall,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth(widthFraction)
                    .align(Alignment.End),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
    }
}

@Composable
fun CanvasSelector(
    selectedCanvas: CanvasMode,
    onCanvasSelected: (CanvasMode) -> Unit,
    iapManager: IAPManager
) {
    val purchaseState = iapManager.purchaseState.collectAsState()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        CanvasButton(
            text = "Classic",
            canvasType = CanvasMode.CLASSIC,
            isSelected = selectedCanvas == CanvasMode.CLASSIC,
            isLocked = false,
            onClick = { onCanvasSelected(CanvasMode.CLASSIC) },
            modifier = Modifier.weight(1f)
        )
        

        val isCanvasPackPurchased = purchaseState.value[IAPProducts.CANVAS_PACK] == true
        CanvasButton(
            text = "Typewriter",
            canvasType = CanvasMode.TYPEWRITER,
            isSelected = selectedCanvas == CanvasMode.TYPEWRITER,
            isLocked = !isCanvasPackPurchased,
            onClick = { onCanvasSelected(CanvasMode.TYPEWRITER) },
            modifier = Modifier.weight(1f)
        )
        
        CanvasButton(
            text = "Focus",
            canvasType = CanvasMode.FOCUS,
            isSelected = selectedCanvas == CanvasMode.FOCUS,
            isLocked = !isCanvasPackPurchased,
            onClick = { onCanvasSelected(CanvasMode.FOCUS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CanvasButton(
    text: String,
    canvasType: CanvasMode,
    isSelected: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundImage = when (canvasType) {
        CanvasMode.TYPEWRITER -> Res.drawable.bg_typewriter
        CanvasMode.FOCUS -> Res.drawable.bg_focus
        else -> null
    }
    
    Box(
        modifier = modifier
            .height(AppSizes.ButtonHeightSmall)
            .clip(RoundedCornerShape(AppRadius.Medium))
            .border(
                width = AppBorders.Medium,
                color = if (isLocked) {
                    Color(0xFF312117)
                } else {
                    WritingCanvasButtonStroke
                },
                shape = RoundedCornerShape(AppRadius.Medium)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (backgroundImage != null && isLocked) {
            Image(
                painter = painterResource(backgroundImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (!isLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WritingCanvasButtonBg)
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isLocked) {
                Image(
                    painter = painterResource(Res.drawable.ic_lock),
                    contentDescription = "Locked",
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = text,
                color = if (isLocked) Color(0xFF312117) else WritingChipText,
                fontSize = 13.sp,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun CheckButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .height(AppSizes.ButtonHeight)
            .border(
                width = AppBorders.Thick,
                color = if (enabled) WritingCheckButtonStroke else Color(0xFF3D2A1F),
                shape = RoundedCornerShape(AppRadius.Medium)
            )
            .background(
                color = if (enabled) WritingCheckButtonBg else Color(0xFF2A1A11),
                shape = RoundedCornerShape(AppRadius.Medium)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Check",
            color = if (enabled) WritingCheckButtonText else Color(0xFF5A4A3D),
            fontSize = AppTypography.TitleSmall,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ExitConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(themeColors.dialogBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your text won't be saved.\nAre you sure you want to exit?",
                    color = themeColors.dialogText,
                    fontSize = 16.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(AppSizes.ButtonHeight)
                            .clip(RoundedCornerShape(AppRadius.Medium))
                            .border(
                                width = AppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(AppRadius.Medium)
                            )
                            .background(Color(0xFF360F01))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Continue",
                            color = Color(0xFFAD8E7D),
                            fontSize = AppTypography.BodyMedium,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(AppSizes.ButtonHeight)
                            .clip(RoundedCornerShape(AppRadius.Medium))
                            .border(
                                width = AppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(AppRadius.Medium)
                            )
                            .background(Color(0xFF613923))
                            .clickable(onClick = onConfirm),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Exit",
                            color = Color(0xFFAD8E7D),
                            fontSize = AppTypography.BodyMedium,
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

@Composable
fun CanvasUnlockDialog(
    iapManager: IAPManager,
    productId: String,
    onDismiss: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    

    val (title, description, price) = when (productId) {
        IAPProducts.CANVAS_PACK -> Triple(
            "Unlock Canvas Pack",
            "Get both Typewriter and Focus canvases. Classic typewriter aesthetic and minimalist focus mode for deep concentration.",
            "$1.99"
        )
        IAPProducts.TIMER_10_MIN -> Triple(
            "Always 10-Minute Timer",
            "Extend your writing sessions from 7 to 10 minutes for all new stories.",
            "$1.99"
        )
        else -> Triple("Unlock Premium Canvas", "Get access to premium writing experience.", "$1.99")
    }
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(AppRadius.ExtraLarge))
                .background(themeColors.dialogBackground)
                .border(
                    width = AppBorders.Thick,
                    color = themeColors.dialogStroke,
                    shape = RoundedCornerShape(AppRadius.ExtraLarge)
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = themeColors.dialogText,
                    fontSize = 22.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = description,
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
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(AppSizes.ButtonHeight)
                            .clip(RoundedCornerShape(AppRadius.Medium))
                            .background(Color(0xFF360F01))
                            .border(
                                width = AppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(AppRadius.Medium)
                            )
                            .clickable(enabled = !isLoading, onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFFAD8E7D),
                            fontSize = AppTypography.BodyMedium,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(AppSizes.ButtonHeight)
                            .clip(RoundedCornerShape(AppRadius.Medium))
                            .background(Color(0xFF613923))
                            .border(
                                width = AppBorders.Medium,
                                color = Color(0xFFAD8E7D),
                                shape = RoundedCornerShape(AppRadius.Medium)
                            )
                            .clickable(enabled = !isLoading) {
                                scope.launch {
                                    isLoading = true
                                    errorMessage = null
                                    when (val result = iapManager.purchase(productId)) {
                                        is com.bonbasses.platform.iap.PurchaseResult.Success -> {
                                            isLoading = false
                                            onDismiss()
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
                                text = "Buy $price",
                                color = Color(0xFFAD8E7D),
                                fontSize = AppTypography.BodyMedium,
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
fun TimerHintToast(
    hint: String,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(hint) {
        if (hint.isNotBlank()) {
            isVisible = true
            kotlinx.coroutines.delay(3500)
            isVisible = false
            kotlinx.coroutines.delay(500)
            onDismiss()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 400)
            ) + slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = tween(durationMillis = 400)
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = 400)
            ) + slideOutVertically(
                targetOffsetY = { -it / 2 },
                animationSpec = tween(durationMillis = 400)
            )
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1E22).copy(alpha = 0.98f))
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFFAD8E7D).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 18.dp)
                    .clickable(onClick = { 
                        isVisible = false
                        kotlinx.coroutines.MainScope().launch {
                            kotlinx.coroutines.delay(400)
                            onDismiss()
                        }
                    })
            ) {
                Text(
                    text = hint,
                    color = Color(0xFFE8D5C4),
                    fontSize = 15.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

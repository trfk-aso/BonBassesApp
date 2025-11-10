package com.bonbasses.ui.screens.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.bg_focus
import bonbassesapp.composeapp.generated.resources.bg_quiz_dialog
import bonbassesapp.composeapp.generated.resources.bg_typewriter
import bonbassesapp.composeapp.generated.resources.btn_buy
import bonbassesapp.composeapp.generated.resources.btn_later
import bonbassesapp.composeapp.generated.resources.btn_next
import bonbassesapp.composeapp.generated.resources.btn_ok
import bonbassesapp.composeapp.generated.resources.btn_start
import bonbassesapp.composeapp.generated.resources.ic_lock
import com.bonbasses.data.models.QuizOption
import com.bonbasses.platform.iap.IAPManager
import com.bonbasses.ui.theme.QuizAlertTextColor
import com.bonbasses.ui.theme.QuizCardSelected
import com.bonbasses.ui.theme.QuizCardUnselectedFill
import com.bonbasses.ui.theme.QuizCardUnselectedStroke
import com.bonbasses.ui.theme.QuizPremiumTextColor
import com.bonbasses.ui.theme.QuizTextColor
import com.bonbasses.ui.theme.RobotoSlabFontFamily
import com.bonbasses.ui.theme.SloganBrown
import com.bonbasses.ui.theme.getBackgroundBrush
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel,
    iapManager: IAPManager
) {
    val uiState by viewModel.uiState.collectAsState()
    var hasCalledOnComplete by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted && !hasCalledOnComplete) {
            hasCalledOnComplete = true
            viewModel.saveAnswers()
            onComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                TextButton(onClick = { viewModel.skipQuiz() }) {
                    Text(
                        text = "Skip",
                        color = SloganBrown,
                        fontSize = 16.sp,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Let's tailor your writing flow",
                    color = SloganBrown,
                    fontSize = 20.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                uiState.currentQuestion?.let { question ->
                    val dialogAlpha = remember { Animatable(0f) }
                    val contentAlpha = remember { Animatable(0f) }
                    val contentOffsetY = remember { Animatable(20f) }

                    LaunchedEffect(question.id) {
                        if (question.id == 1) {
                            dialogAlpha.snapTo(0f)
                            contentAlpha.snapTo(1f)
                            contentOffsetY.snapTo(0f)
                            dialogAlpha.animateTo(1f, tween(400))
                        } else {
                            dialogAlpha.snapTo(1f)
                            contentAlpha.snapTo(0f)
                            contentOffsetY.snapTo(20f)
                            launch { contentAlpha.animateTo(1f, tween(400)) }
                            launch { contentOffsetY.animateTo(0f, tween(400)) }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
                            .alpha(dialogAlpha.value)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.bg_quiz_dialog),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )

                        key(question.id) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 40.dp, vertical = 40.dp)
                                    .alpha(contentAlpha.value)
                                    .offset(y = contentOffsetY.value.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = question.question,
                                    color = QuizTextColor,
                                    fontSize = 18.sp,
                                    fontFamily = RobotoSlabFontFamily(),
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(64.dp))

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (question.isMultiSelect) {
                                        val rows = question.options.chunked(2)
                                        rows.forEach { rowOptions ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                rowOptions.forEach { option ->
                                                    val isSelected =
                                                        uiState.multiSelectAnswers[question.id]?.contains(option.text) == true
                                                    Box(modifier = Modifier.weight(1f)) {
                                                        OptionCard(
                                                            option = option,
                                                            isSelected = isSelected,
                                                            onClick = {
                                                                viewModel.selectAnswer(option.text, option.isLocked)
                                                            }
                                                        )
                                                    }
                                                }
                                                if (rowOptions.size == 1) Spacer(modifier = Modifier.weight(1f))
                                            }
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }
                                    } else {
                                        question.options.forEach { option ->
                                            val isSelected = uiState.selectedAnswers[question.id] == option.text
                                            OptionCard(
                                                option = option,
                                                isSelected = isSelected,
                                                onClick = {
                                                    viewModel.selectAnswer(option.text, option.isLocked)
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }
                                    }
                                }

                                uiState.showErrorMessage?.let { error ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = error,
                                        color = Color(0xFFE57373),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .size(width = 200.dp, height = 60.dp)
                        .clickable(enabled = uiState.canProceed) {
                            viewModel.nextQuestion()
                        }
                ) {
                    Image(
                        painter = painterResource(
                            if (uiState.isLastQuestion) Res.drawable.btn_start else Res.drawable.btn_next
                        ),
                        contentDescription = if (uiState.isLastQuestion) "Start" else "Next",
                        modifier = Modifier.fillMaxSize(),
                        alpha = if (uiState.canProceed) 1f else 0.5f
                    )
                }
            }

            Text(
                text = uiState.progress,
                color = SloganBrown,
                fontSize = 16.sp,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        }

        if (uiState.showPremiumAlert) {
            PremiumAlertDialog(
                onDismiss = { viewModel.dismissPremiumAlert() },
                onLater = { viewModel.unlockPremiumLater() },
                onBuy = { viewModel.purchasePremium() }
            )
        }

        if (uiState.showPersonalizationAlert) {
            PersonalizationAlertDialog(
                onOk = { viewModel.dismissPersonalizationAlert() }
            )
        }

        if (uiState.isPurchasing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SloganBrown)
            }
        }
    }
}

@Composable
fun PremiumAlertDialog(
    onDismiss: () -> Unit,
    onLater: () -> Unit,
    onBuy: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(QuizCardUnselectedStroke)
                .border(
                    width = 2.dp,
                    color = QuizCardUnselectedStroke,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Unlock Premium Textures",
                    color = QuizAlertTextColor,
                    fontSize = 22.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Activate premium to turn every page into a space for deep thoughts and inspiration.",
                    color = QuizAlertTextColor,
                    fontSize = 16.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable(onClick = onLater)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.btn_later),
                            contentDescription = "Later",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable(onClick = onBuy)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.btn_buy),
                            contentDescription = "Buy",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalizationAlertDialog(
    onOk: () -> Unit
) {
    Dialog(onDismissRequest = onOk) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(QuizCardUnselectedStroke)
                .border(
                    width = 2.dp,
                    color = QuizCardUnselectedStroke,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Personalization",
                    color = QuizAlertTextColor,
                    fontSize = 22.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "We'll use these answers to preset your timer nudges, editor page, and prompt preferences. You can change them anytime in Settings.",
                    color = QuizAlertTextColor,
                    fontSize = 16.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(56.dp)
                        .clickable(onClick = onOk)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.btn_ok),
                        contentDescription = "OK",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun OptionCard(
    option: QuizOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        if (option.backgroundImage != null) {
            val backgroundRes = when (option.backgroundImage) {
                "bg_typewriter" -> Res.drawable.bg_typewriter
                "bg_focus" -> Res.drawable.bg_focus
                else -> null
            }
            
            backgroundRes?.let {
                Image(
                    painter = painterResource(it),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isSelected) QuizCardSelected else QuizCardUnselectedFill
                    )
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = QuizCardUnselectedStroke
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (option.isLocked) {
                    Image(
                        painter = painterResource(Res.drawable.ic_lock),
                        contentDescription = "Locked",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Text(
                    text = option.text,
                    color = if (option.backgroundImage != null) QuizPremiumTextColor else QuizTextColor,
                    fontSize = 16.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

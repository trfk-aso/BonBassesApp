package com.bonbasses.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SplashViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun checkFirstLaunch(isFirstLaunch: Boolean) {
        viewModelScope.launch {
            val delayTime = if (isFirstLaunch) {
                Random.nextLong(1000, 1200)
            } else {
                800L
            }
            
            delay(delayTime)
            
            _uiState.value = if (isFirstLaunch) {
                SplashUiState.NavigateToOnboarding
            } else {
                SplashUiState.NavigateToHome
            }
        }
    }
}
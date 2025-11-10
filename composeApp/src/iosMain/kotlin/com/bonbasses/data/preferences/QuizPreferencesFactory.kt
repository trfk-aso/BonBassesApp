package com.bonbasses.data.preferences

actual fun createQuizPreferences(): QuizPreferences {
    return QuizPreferencesIosImpl()
}

package com.bonbasses.data.preferences

import android.content.Context

private lateinit var appContext: Context

fun initQuizPreferences(context: Context) {
    appContext = context.applicationContext
}

actual fun createQuizPreferences(): QuizPreferences {
    return QuizPreferencesImpl(appContext)
}
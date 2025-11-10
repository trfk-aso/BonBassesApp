package com.bonbasses.data.preferences

interface QuizPreferences {
    suspend fun saveAnswer(questionId: Int, answer: String)
    suspend fun getAnswer(questionId: Int): String?
    suspend fun getAllAnswers(): Map<Int, String>
    suspend fun clearAnswers()
    suspend fun isQuizCompleted(): Boolean
    suspend fun setQuizCompleted(completed: Boolean)
    suspend fun isHowToStartShown(): Boolean
    suspend fun setHowToStartShown(shown: Boolean)
    

    suspend fun getGentleHapticsEnabled(): Boolean
    suspend fun setGentleHapticsEnabled(enabled: Boolean)
    suspend fun getTypingSoundsEnabled(): Boolean
    suspend fun setTypingSoundsEnabled(enabled: Boolean)
    suspend fun getThemeMode(): String
    suspend fun setThemeMode(mode: String)
    

    suspend fun getSelectedCanvas(): String
    suspend fun setSelectedCanvas(canvas: String)
    

    suspend fun getTimerLength(): Int
    suspend fun setTimerLength(minutes: Int)
    
    fun saveSearchHistory(queries: List<String>)
    fun getSearchHistory(): List<String>
    fun clearSearchHistory()
    
    suspend fun resetQuizProgress()
}

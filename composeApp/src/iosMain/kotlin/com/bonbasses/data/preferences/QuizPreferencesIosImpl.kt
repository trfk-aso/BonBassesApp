package com.bonbasses.data.preferences

import platform.Foundation.NSUserDefaults

class QuizPreferencesIosImpl : QuizPreferences {
    
    private val userDefaults = NSUserDefaults.standardUserDefaults
    
    companion object {
        private const val QUIZ_COMPLETED_KEY = "quiz_completed"
        private const val HOW_TO_START_SHOWN_KEY = "how_to_start_shown"
        private const val GENTLE_HAPTICS_ENABLED_KEY = "gentle_haptics_enabled"
        private const val TYPING_SOUNDS_ENABLED_KEY = "typing_sounds_enabled"
        private const val THEME_MODE_KEY = "theme_mode"
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val SELECTED_CANVAS_KEY = "selected_canvas"
        private const val TIMER_LENGTH_KEY = "timer_length"
        private fun answerKey(questionId: Int) = "answer_$questionId"
    }
    
    override suspend fun saveAnswer(questionId: Int, answer: String) {
        userDefaults.setObject(answer, forKey = answerKey(questionId))
        userDefaults.synchronize()
    }
    
    override suspend fun getAnswer(questionId: Int): String? {
        return userDefaults.stringForKey(answerKey(questionId))
    }
    
    override suspend fun getAllAnswers(): Map<Int, String> {
        val answers = mutableMapOf<Int, String>()
        val dictionary = userDefaults.dictionaryRepresentation()
        
        dictionary.forEach { (key, value) ->
            if (key is String && key.startsWith("answer_") && value is String) {
                val questionId = key.removePrefix("answer_").toIntOrNull()
                if (questionId != null) {
                    answers[questionId] = value
                }
            }
        }
        
        return answers
    }
    
    override suspend fun clearAnswers() {
        val dictionary = userDefaults.dictionaryRepresentation()
        dictionary.keys.forEach { key ->
            if (key is String && key.startsWith("answer_")) {
                userDefaults.removeObjectForKey(key)
            }
        }
        userDefaults.synchronize()
    }
    
    override suspend fun isQuizCompleted(): Boolean {
        return userDefaults.boolForKey(QUIZ_COMPLETED_KEY)
    }
    
    override suspend fun setQuizCompleted(completed: Boolean) {
        userDefaults.setBool(completed, forKey = QUIZ_COMPLETED_KEY)
        userDefaults.synchronize()
    }
    
    override suspend fun isHowToStartShown(): Boolean {
        return userDefaults.boolForKey(HOW_TO_START_SHOWN_KEY)
    }
    
    override suspend fun setHowToStartShown(shown: Boolean) {
        userDefaults.setBool(shown, forKey = HOW_TO_START_SHOWN_KEY)
        userDefaults.synchronize()
    }
    
    override suspend fun getGentleHapticsEnabled(): Boolean {

        if (userDefaults.objectForKey(GENTLE_HAPTICS_ENABLED_KEY) == null) {
            return true
        }
        return userDefaults.boolForKey(GENTLE_HAPTICS_ENABLED_KEY)
    }
    
    override suspend fun setGentleHapticsEnabled(enabled: Boolean) {
        userDefaults.setBool(enabled, forKey = GENTLE_HAPTICS_ENABLED_KEY)
        userDefaults.synchronize()
    }
    
    override suspend fun getTypingSoundsEnabled(): Boolean {
        return userDefaults.boolForKey(TYPING_SOUNDS_ENABLED_KEY)
    }
    
    override suspend fun setTypingSoundsEnabled(enabled: Boolean) {
        userDefaults.setBool(enabled, forKey = TYPING_SOUNDS_ENABLED_KEY)
        userDefaults.synchronize()
    }
    
    override suspend fun getThemeMode(): String {
        return userDefaults.stringForKey(THEME_MODE_KEY) ?: "DARK"
    }
    
    override suspend fun setThemeMode(mode: String) {
        userDefaults.setObject(mode, forKey = THEME_MODE_KEY)
        userDefaults.synchronize()
    }
    
    override suspend fun getSelectedCanvas(): String {
        return userDefaults.stringForKey(SELECTED_CANVAS_KEY) ?: "default"
    }
    
    override suspend fun setSelectedCanvas(canvas: String) {
        userDefaults.setObject(canvas, forKey = SELECTED_CANVAS_KEY)
        userDefaults.synchronize()
    }
    
    override suspend fun getTimerLength(): Int {
        val value = userDefaults.integerForKey(TIMER_LENGTH_KEY)
        return if (value == 0L) 7 else value.toInt()
    }
    
    override suspend fun setTimerLength(minutes: Int) {
        userDefaults.setInteger(minutes.toLong(), forKey = TIMER_LENGTH_KEY)
        userDefaults.synchronize()
    }
    
    override fun saveSearchHistory(queries: List<String>) {
        userDefaults.setObject(queries.joinToString("|"), forKey = SEARCH_HISTORY_KEY)
        userDefaults.synchronize()
    }
    
    override fun getSearchHistory(): List<String> {
        return userDefaults.stringForKey(SEARCH_HISTORY_KEY)
            ?.split("|")
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }
    
    override fun clearSearchHistory() {
        userDefaults.removeObjectForKey(SEARCH_HISTORY_KEY)
        userDefaults.synchronize()
    }
    
    override suspend fun resetQuizProgress() {
        val dictionary = userDefaults.dictionaryRepresentation()
        dictionary.keys.forEach { key ->
            if (key is String) {
                userDefaults.removeObjectForKey(key)
            }
        }
        userDefaults.synchronize()
    }
}

package com.bonbasses.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quiz_preferences")

class QuizPreferencesImpl(private val context: Context) : QuizPreferences {
    
    companion object {
        private val QUIZ_COMPLETED = booleanPreferencesKey("quiz_completed")
        private val HOW_TO_START_SHOWN = booleanPreferencesKey("how_to_start_shown")
        private val GENTLE_HAPTICS_ENABLED = booleanPreferencesKey("gentle_haptics_enabled")
        private val TYPING_SOUNDS_ENABLED = booleanPreferencesKey("typing_sounds_enabled")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val SELECTED_CANVAS = stringPreferencesKey("selected_canvas")
        private val TIMER_LENGTH = intPreferencesKey("timer_length")
        private val SEARCH_HISTORY = stringPreferencesKey("search_history")
        private fun answerKey(questionId: Int) = stringPreferencesKey("answer_$questionId")
    }
    
    override suspend fun saveAnswer(questionId: Int, answer: String) {
        context.dataStore.edit { preferences ->
            preferences[answerKey(questionId)] = answer
        }
    }
    
    override suspend fun getAnswer(questionId: Int): String? {
        return context.dataStore.data.map { preferences ->
            preferences[answerKey(questionId)]
        }.first()
    }
    
    override suspend fun getAllAnswers(): Map<Int, String> {
        return context.dataStore.data.map { preferences ->
            val answers = mutableMapOf<Int, String>()
            preferences.asMap().forEach { (key, value) ->
                if (key.name.startsWith("answer_") && value is String) {
                    val questionId = key.name.removePrefix("answer_").toIntOrNull()
                    if (questionId != null) {
                        answers[questionId] = value
                    }
                }
            }
            answers
        }.first()
    }
    
    override suspend fun clearAnswers() {
        context.dataStore.edit { preferences ->
            val keysToRemove = preferences.asMap().keys.filter { it.name.startsWith("answer_") }
            keysToRemove.forEach { key ->
                preferences.remove(key)
            }
        }
    }
    
    override suspend fun isQuizCompleted(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[QUIZ_COMPLETED] ?: false
        }.first()
    }
    
    override suspend fun setQuizCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[QUIZ_COMPLETED] = completed
        }
    }
    
    override suspend fun isHowToStartShown(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[HOW_TO_START_SHOWN] ?: false
        }.first()
    }
    
    override suspend fun setHowToStartShown(shown: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HOW_TO_START_SHOWN] = shown
        }
    }
    
    override suspend fun getGentleHapticsEnabled(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[GENTLE_HAPTICS_ENABLED] ?: true
        }.first()
    }
    
    override suspend fun setGentleHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[GENTLE_HAPTICS_ENABLED] = enabled
        }
    }
    
    override suspend fun getTypingSoundsEnabled(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[TYPING_SOUNDS_ENABLED] ?: false
        }.first()
    }
    
    override suspend fun setTypingSoundsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TYPING_SOUNDS_ENABLED] = enabled
        }
    }
    
    override suspend fun getThemeMode(): String {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_MODE] ?: "DARK"
        }.first()
    }
    
    override suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }
    
    override suspend fun getSelectedCanvas(): String {
        return context.dataStore.data.map { preferences ->
            preferences[SELECTED_CANVAS] ?: "CLASSIC"
        }.first()
    }
    
    override suspend fun setSelectedCanvas(canvas: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_CANVAS] = canvas
        }
    }
    
    override suspend fun getTimerLength(): Int {
        return context.dataStore.data.map { preferences ->
            preferences[TIMER_LENGTH] ?: 7
        }.first()
    }
    
    override suspend fun setTimerLength(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[TIMER_LENGTH] = minutes
        }
    }
    
    override fun saveSearchHistory(queries: List<String>) {
        kotlinx.coroutines.runBlocking {
            context.dataStore.edit { preferences ->
                preferences[SEARCH_HISTORY] = queries.joinToString("|")
            }
        }
    }
    
    override fun getSearchHistory(): List<String> {
        return kotlinx.coroutines.runBlocking {
            context.dataStore.data.map { preferences ->
                preferences[SEARCH_HISTORY]?.split("|")?.filter { it.isNotBlank() } ?: emptyList()
            }.first()
        }
    }
    
    override fun clearSearchHistory() {
        kotlinx.coroutines.runBlocking {
            context.dataStore.edit { preferences ->
                preferences.remove(SEARCH_HISTORY)
            }
        }
    }
    
    override suspend fun resetQuizProgress() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

package com.bonbasses.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.bonbasses.data.database.DatabaseDriverFactory
import com.bonbasses.data.models.WritingHistoryItem
import com.bonbasses.database.BonBassesDatabase
import com.bonbasses.platform.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WritingHistoryRepository(databaseDriverFactory: DatabaseDriverFactory) {
    
    private val database = BonBassesDatabase.invoke(
        driver = databaseDriverFactory.createDriver()
    )
    private val queries = database.writingHistoryQueries
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun insertHistory(
        storyText: String,
        words: List<String>,
        genre: String,
        charCount: Int,
        wordCount: Int,
        score: Int?,
        isFavorite: Boolean = false,
        startTime: Long? = null,
        endTime: Long? = null,
        duration: Int? = null,
        outcome: String = "draft",
        title: String = "Untitled"
    ): Long = withContext(Dispatchers.Default) {
        val wordsJson = json.encodeToString(words)
        val createdAt = TimeUtils.currentTimeMillis()
        val sessionStartTime = startTime ?: createdAt
        
        queries.insertHistory(
            title = title,
            storyText = storyText,
            words = wordsJson,
            genre = genre,
            charCount = charCount.toLong(),
            wordCount = wordCount.toLong(),
            score = score?.toLong(),
            isFavorite = if (isFavorite) 1 else 0,
            createdAt = createdAt,
            startTime = sessionStartTime,
            endTime = endTime,
            duration = duration?.toLong(),
            outcome = outcome
        )
        
        queries.transactionWithResult {
            database.writingHistoryQueries.getAllHistory()
                .executeAsList()
                .maxByOrNull { it.createdAt }?.id ?: 0L
        }
    }

    fun getAllHistory(): Flow<List<WritingHistoryItem>> {
        return queries.getAllHistory()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                list.map { it.toHistoryItem() }
            }
    }

    fun getFavoriteHistory(): Flow<List<WritingHistoryItem>> {
        return queries.getFavoriteHistory()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                list.map { it.toHistoryItem() }
            }
    }

    fun getHistoryByGenre(genre: String): Flow<List<WritingHistoryItem>> {
        return queries.getHistoryByGenre(genre)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                list.map { it.toHistoryItem() }
            }
    }

    suspend fun getHistoryById(id: Long): WritingHistoryItem? = withContext(Dispatchers.Default) {
        queries.getHistoryById(id).executeAsOneOrNull()?.toHistoryItem()
    }

    suspend fun toggleFavorite(id: Long) = withContext(Dispatchers.Default) {
        queries.toggleFavorite(id)
    }

    suspend fun updateTitle(id: Long, title: String) = withContext(Dispatchers.Default) {
        queries.updateTitle(title = title, id = id)
    }

    suspend fun deleteHistory(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteHistory(id)
    }

    suspend fun deleteAllHistory() = withContext(Dispatchers.Default) {
        queries.deleteAllHistory()
    }

    private fun com.bonbasses.database.WritingHistory.toHistoryItem(): WritingHistoryItem {
        return WritingHistoryItem(
            id = id,
            title = title,
            storyText = storyText,
            words = try {
                json.decodeFromString<List<String>>(words)
            } catch (e: Exception) {
                emptyList()
            },
            genre = genre,
            charCount = charCount.toInt(),
            wordCount = wordCount.toInt(),
            score = score?.toInt(),
            isFavorite = isFavorite != 0L,
            createdAt = createdAt,
            startTime = startTime,
            endTime = endTime,
            duration = duration?.toInt(),
            outcome = outcome
        )
    }
}

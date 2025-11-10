package com.bonbasses.data.models

import kotlinx.serialization.Serializable

@Serializable
data class WritingHistoryItem(
    val id: Long,
    val title: String = "Untitled",
    val storyText: String,
    val words: List<String>,
    val genre: String,
    val charCount: Int,
    val wordCount: Int,
    val score: Int?,
    val isFavorite: Boolean,
    val createdAt: Long,

    val startTime: Long? = null,
    val endTime: Long? = null,
    val duration: Int? = null,
    val outcome: String = "draft"
)

package com.bonbasses.data.repository

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json

@Serializable
data class WordsCore(
    val words: List<String>
)

@Serializable
data class WordsByCategory(
    @SerialName("Adventure")
    val Adventure: List<String>,
    @SerialName("Sci-Fi")
    val SciFi: List<String>,
    @SerialName("Slice of Life")
    val SliceOfLife: List<String>,
    @SerialName("Mystery")
    val Mystery: List<String>,
    @SerialName("Romance")
    val Romance: List<String>,
    @SerialName("Fantasy")
    val Fantasy: List<String>
)

class WordsRepository {
    private val json = Json { ignoreUnknownKeys = true }
    
    private val availableGenres = listOf(
        "Adventure", "Sci-Fi", "Slice of Life", "Mystery", "Romance", "Fantasy"
    )
    
    fun parseWordsCore(jsonString: String): List<String> {
        return try {
            val wordsCore = json.decodeFromString<WordsCore>(jsonString)
            wordsCore.words
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun parseWordsByCategory(jsonString: String): Map<String, List<String>> {
        return try {
            val categoryWords = json.decodeFromString<WordsByCategory>(jsonString)
            mapOf(
                "Adventure" to categoryWords.Adventure,
                "Sci-Fi" to categoryWords.SciFi,
                "Slice of Life" to categoryWords.SliceOfLife,
                "Mystery" to categoryWords.Mystery,
                "Romance" to categoryWords.Romance,
                "Fantasy" to categoryWords.Fantasy
            )
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    fun generateWords(
        category: String,
        coreWords: List<String>,
        categoryWords: Map<String, List<String>>,
        lastUsedGenre: String? = null,
        favoriteGenres: List<String>? = null
    ): Pair<List<String>, String> {
        val words = mutableListOf<String>()
        val actualGenre: String
        
        if (category == "All") {

            if (!favoriteGenres.isNullOrEmpty()) {

                val favoriteWords = mutableListOf<String>()
                favoriteGenres.take(2).forEach { genre ->
                    val genreWords = categoryWords[genre] ?: emptyList()
                    favoriteWords.addAll(genreWords.shuffled().take(3))
                }
                

                val otherGenres = availableGenres.filter { it !in favoriteGenres }
                val randomOtherGenre = otherGenres.randomOrNull() ?: availableGenres.random()
                val otherWords = categoryWords[randomOtherGenre] ?: emptyList()
                
                words.addAll(favoriteWords.shuffled().take(6))
                words.addAll(otherWords.shuffled().take(4))
                

                actualGenre = favoriteGenres.firstOrNull() ?: randomOtherGenre
            } else {

                actualGenre = lastUsedGenre ?: availableGenres.random()
                val catWords = categoryWords[actualGenre] ?: emptyList()
                val categoryCount = (6..8).random()
                val coreCount = 10 - categoryCount
                
                words.addAll(catWords.shuffled().take(categoryCount))
                words.addAll(coreWords.shuffled().take(coreCount))
            }
        } else {
            actualGenre = category
            val catWords = categoryWords[category] ?: emptyList()
            val categoryCount = (6..8).random()
            val coreCount = 10 - categoryCount
            
            words.addAll(catWords.shuffled().take(categoryCount))
            words.addAll(coreWords.shuffled().take(coreCount))
        }
        
        return words.shuffled().take(10) to actualGenre
    }
}

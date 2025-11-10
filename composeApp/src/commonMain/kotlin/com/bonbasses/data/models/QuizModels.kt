package com.bonbasses.data.models

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<QuizOption>,
    val isMultiSelect: Boolean = false,
    val minSelections: Int = 1,
    val maxSelections: Int = 1
)

data class QuizOption(
    val text: String,
    val isLocked: Boolean = false,
    val backgroundImage: String? = null
)

enum class WritingPace {
    FAST,
    STEADY,
    UNHURRIED;
    
    companion object {
        fun fromString(value: String): WritingPace {
            return when (value.lowercase()) {
                "fast" -> FAST
                "steady" -> STEADY
                "unhurried" -> UNHURRIED
                else -> STEADY
            }
        }
    }
}

enum class DeadlinePressure {
    YES,
    NOT_REALLY,
    SURPRISE_ME;
    
    companion object {
        fun fromString(value: String): DeadlinePressure {
            return when (value.lowercase()) {
                "yes" -> YES
                "not really" -> NOT_REALLY
                "surprise me" -> SURPRISE_ME
                else -> NOT_REALLY
            }
        }
    }
}

enum class CanvasStyle {
    CLASSIC,
    TYPEWRITER,
    FOCUS;
    
    companion object {
        fun fromString(value: String): CanvasStyle {
            return when (value.lowercase()) {
                "classic" -> CLASSIC
                "typewriter" -> TYPEWRITER
                "focus" -> FOCUS
                else -> CLASSIC
            }
        }
    }
}

enum class FeedbackTone {
    MOSTLY_PRAISE,
    BALANCED,
    MORE_HINTS;
    
    companion object {
        fun fromString(value: String): FeedbackTone {
            return when (value.lowercase()) {
                "mostly praise" -> MOSTLY_PRAISE
                "balanced" -> BALANCED
                "more hints" -> MORE_HINTS
                else -> BALANCED
            }
        }
    }
}

enum class Genre {
    ADVENTURE,
    SCI_FI,
    SLICE_OF_LIFE,
    MYSTERY,
    ROMANCE,
    FANTASY;
    
    companion object {
        fun fromString(value: String): Genre {
            return when (value.lowercase()) {
                "adventure" -> ADVENTURE
                "sci-fi" -> SCI_FI
                "slice of life" -> SLICE_OF_LIFE
                "mystery" -> MYSTERY
                "romance" -> ROMANCE
                "fantasy" -> FANTASY
                else -> ADVENTURE
            }
        }
    }
}

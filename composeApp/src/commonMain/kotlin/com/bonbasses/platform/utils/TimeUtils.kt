package com.bonbasses.platform.utils

expect object TimeUtils {
    fun currentTimeMillis(): Long
    fun getStartOfDay(timestamp: Long): Long
    fun groupByDay(timestamps: List<Long>): Map<Long, List<Long>>
    fun formatDateDDMMYY(timestamp: Long): String
    fun getCurrentYear(): Int
    fun getCurrentMonth(): Int
    fun getCurrentDay(): Int
    fun parseMMDDYY(dateStr: String): Long?
    fun formatCurrentDateMMDDYY(): String
}

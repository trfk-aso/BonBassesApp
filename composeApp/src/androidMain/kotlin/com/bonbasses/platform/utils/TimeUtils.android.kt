package com.bonbasses.platform.utils

import java.util.Calendar

actual object TimeUtils {
    actual fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
    
    actual fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    actual fun groupByDay(timestamps: List<Long>): Map<Long, List<Long>> {
        return timestamps.groupBy { timestamp ->
            getStartOfDay(timestamp)
        }
    }
    
    actual fun formatDateDDMMYY(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val year = (calendar.get(Calendar.YEAR) % 100).toString().padStart(2, '0')
        return "$day/$month/$year"
    }
    
    actual fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }
    
    actual fun getCurrentMonth(): Int {
        return Calendar.getInstance().get(Calendar.MONTH)
    }
    
    actual fun getCurrentDay(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }
    
    actual fun parseMMDDYY(dateStr: String): Long? {
        return try {
            val parts = dateStr.split("/")
            if (parts.size == 3) {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.MONTH, parts[0].toInt() - 1)
                calendar.set(Calendar.DAY_OF_MONTH, parts[1].toInt())
                calendar.set(Calendar.YEAR, 2000 + parts[2].toInt())
                calendar.timeInMillis
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    actual fun formatCurrentDateMMDDYY(): String {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val year = (calendar.get(Calendar.YEAR) % 100).toString().padStart(2, '0')
        return "$month/$day/$year"
    }
}

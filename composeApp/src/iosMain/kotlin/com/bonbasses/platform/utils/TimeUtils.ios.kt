package com.bonbasses.platform.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.darwin.*

@OptIn(ExperimentalForeignApi::class)
actual object TimeUtils {
    actual fun currentTimeMillis(): Long {
        return (NSDate().timeIntervalSince1970 * 1000).toLong()
    }
    
    actual fun getStartOfDay(timestamp: Long): Long {
        val timeInterval: NSTimeInterval = timestamp.toDouble() / 1000.0
        val date = NSDate(timeInterval)
        
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
            date
        )
        
        val startOfDay = calendar.dateFromComponents(components)
        return ((startOfDay?.timeIntervalSince1970 ?: 0.0) * 1000).toLong()
    }
    
    actual fun groupByDay(timestamps: List<Long>): Map<Long, List<Long>> {
        return timestamps.groupBy { timestamp ->
            getStartOfDay(timestamp)
        }
    }
    
    actual fun formatDateDDMMYY(timestamp: Long): String {
        val timeInterval: NSTimeInterval = timestamp.toDouble() / 1000.0
        val date = NSDate(timeInterval)
        
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
            date
        )
        
        val day = components.day.toString().padStart(2, '0')
        val month = components.month.toString().padStart(2, '0')
        val year = (components.year % 100).toString().padStart(2, '0')
        return "$day/$month/$year"
    }
    
    actual fun getCurrentYear(): Int {
        val date = NSDate()
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(NSCalendarUnitYear, date)
        return components.year.toInt()
    }
    
    actual fun getCurrentMonth(): Int {
        val date = NSDate()
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(NSCalendarUnitMonth, date)
        return (components.month - 1).toInt()
    }
    
    actual fun getCurrentDay(): Int {
        val date = NSDate()
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(NSCalendarUnitDay, date)
        return components.day.toInt()
    }
    
    actual fun parseMMDDYY(dateStr: String): Long? {
        return try {
            val parts = dateStr.split("/")
            if (parts.size == 3) {
                val calendar = NSCalendar.currentCalendar
                val components = platform.Foundation.NSDateComponents()
                components.month = parts[0].toLong()
                components.day = parts[1].toLong()
                components.year = 2000 + parts[2].toLong()
                
                val date = calendar.dateFromComponents(components)
                ((date?.timeIntervalSince1970 ?: 0.0) * 1000).toLong()
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    actual fun formatCurrentDateMMDDYY(): String {
        val date = NSDate()
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
            date
        )
        
        val day = components.day.toString().padStart(2, '0')
        val month = components.month.toString().padStart(2, '0')
        val year = (components.year % 100).toString().padStart(2, '0')
        return "$month/$day/$year"
    }
}

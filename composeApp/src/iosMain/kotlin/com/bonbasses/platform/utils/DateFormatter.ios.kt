package com.bonbasses.platform.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
actual object DateFormatter {
    private val dateFormatter = NSDateFormatter().apply {
        dateFormat = "MMM dd, yyyy"
        locale = NSLocale(localeIdentifier = "en_US")
    }
    
    private val timeFormatter = NSDateFormatter().apply {
        dateFormat = "HH:mm"
        locale = NSLocale(localeIdentifier = "en_US")
    }
    
    private val dateTimeFormatter = NSDateFormatter().apply {
        dateFormat = "MMM dd, yyyy HH:mm"
        locale = NSLocale(localeIdentifier = "en_US")
    }
    
    private val fullDateTimeFormatter = NSDateFormatter().apply {
        dateFormat = "yyyy-MM-dd HH:mm:ss"
        locale = NSLocale(localeIdentifier = "en_US")
    }
    
    actual fun formatDate(timestamp: Long): String {
        val date = NSDate.dateWithTimeIntervalSince1970(timestamp.toDouble() / 1000.0)
        return dateFormatter.stringFromDate(date) ?: ""
    }
    
    actual fun formatTime(timestamp: Long): String {
        val date = NSDate.dateWithTimeIntervalSince1970(timestamp.toDouble() / 1000.0)
        return timeFormatter.stringFromDate(date) ?: ""
    }
    
    actual fun formatDateTime(timestamp: Long): String {
        val date = NSDate.dateWithTimeIntervalSince1970(timestamp.toDouble() / 1000.0)
        return dateTimeFormatter.stringFromDate(date) ?: ""
    }
    
    actual fun formatDecimal(value: Double, decimals: Int): String {
        return NSString.localizedStringWithFormat("%.${decimals}f", value) as String
    }
    
    actual fun formatFullDateTime(timestamp: Long): String {
        val date = NSDate.dateWithTimeIntervalSince1970(timestamp.toDouble() / 1000.0)
        return fullDateTimeFormatter.stringFromDate(date) ?: ""
    }
}

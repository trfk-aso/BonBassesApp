package com.bonbasses.platform.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual object DateFormatter {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US)
    private val fullDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    
    actual fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    actual fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }
    
    actual fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }
    
    actual fun formatDecimal(value: Double, decimals: Int): String {
        return String.format(Locale.US, "%.${decimals}f", value)
    }
    
    actual fun formatFullDateTime(timestamp: Long): String {
        return fullDateTimeFormat.format(Date(timestamp))
    }
}

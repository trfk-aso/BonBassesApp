package com.bonbasses.platform.utils

expect object DateFormatter {
    fun formatDate(timestamp: Long): String
    fun formatTime(timestamp: Long): String
    fun formatDateTime(timestamp: Long): String
    fun formatDecimal(value: Double, decimals: Int = 1): String
    fun formatFullDateTime(timestamp: Long): String
}

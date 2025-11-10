package com.bonbasses.platform

expect class FileExporter {
    fun exportTxt(filename: String, content: String)
    fun exportCsv(filename: String, content: String)
}

expect fun createFileExporter(): FileExporter

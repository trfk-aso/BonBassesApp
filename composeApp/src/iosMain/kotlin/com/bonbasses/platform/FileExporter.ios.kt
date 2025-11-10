package com.bonbasses.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.UIKit.*

@OptIn(ExperimentalForeignApi::class)
actual class FileExporter {
    actual fun exportTxt(filename: String, content: String) {
        exportFile(filename, content)
    }
    
    actual fun exportCsv(filename: String, content: String) {
        exportFile(filename, content)
    }
    
    private fun exportFile(filename: String, content: String) {
        val documentsPath = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? String ?: return
        
        val filePath = "$documentsPath/$filename"
        val nsString = content as NSString
        nsString.writeToFile(
            filePath,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null
        )
        
        val fileURL = NSURL.fileURLWithPath(filePath)
        val activityViewController = UIActivityViewController(
            activityItems = listOf(fileURL),
            applicationActivities = null
        )
        
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }
}

actual fun createFileExporter(): FileExporter {
    return FileExporter()
}

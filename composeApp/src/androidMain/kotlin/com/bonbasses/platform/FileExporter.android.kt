package com.bonbasses.platform

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

actual class FileExporter {
    companion object {
        private var context: Context? = null
        
        fun init(context: Context) {
            this.context = context
        }
        
        internal fun getContext(): Context? = context
    }
    
    actual fun exportTxt(filename: String, content: String) {
        exportFile(filename, content, "text/plain")
    }
    
    actual fun exportCsv(filename: String, content: String) {
        exportFile(filename, content, "text/csv")
    }
    
    private fun exportFile(filename: String, content: String, mimeType: String) {
        val ctx = getContext() ?: return
        
        try {
            val file = File(ctx.cacheDir, filename)
            file.writeText(content)
            
            val uri = FileProvider.getUriForFile(
                ctx,
                "${ctx.packageName}.fileprovider",
                file
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(intent, "Export $filename")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

actual fun createFileExporter(): FileExporter {
    return FileExporter()
}

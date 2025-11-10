package com.bonbasses.platform

import android.content.Context
import android.media.MediaPlayer
import bonbassesapp.composeapp.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi

actual object TypingSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var appContext: Context? = null
    private var isInitialized = false
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    
    @OptIn(ExperimentalResourceApi::class)
    actual fun init(context: Any?) {
        if (context is Context) {
            appContext = context.applicationContext
            
            coroutineScope.launch {
                try {
                    val bytes = Res.readBytes("files/typing_click.mp3")
                    val tempFile = java.io.File(appContext!!.cacheDir, "typing_click.mp3")
                    tempFile.writeBytes(bytes)
                    
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(tempFile.absolutePath)
                        prepare()
                        setVolume(0.5f, 0.5f)
                        setOnCompletionListener {
                            it.seekTo(0)
                        }
                    }
                    isInitialized = true
                } catch (e: Exception) {

                }
            }
        }
    }
    
    actual fun playTypingSound() {
        if (!isInitialized || mediaPlayer == null) {
            return
        }
        
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.seekTo(0)
                } else {
                    player.start()
                }
            }
        } catch (e: Exception) {

        }
    }
}

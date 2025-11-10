package com.bonbasses.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bonbasses.data.database.initDatabaseContext
import com.bonbasses.data.preferences.initQuizPreferences
import com.bonbasses.platform.HapticFeedback
import com.bonbasses.platform.TypingSoundPlayer
import com.bonbasses.platform.FileExporter
import com.bonbasses.platform.iap.IAPManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initQuizPreferences(this)
        initDatabaseContext(this)
        HapticFeedback.init(this)
        TypingSoundPlayer.init(this)
        FileExporter.init(this)
        

        val iapManager = IAPManager.getInstance(this)
        iapManager.setActivity(this)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
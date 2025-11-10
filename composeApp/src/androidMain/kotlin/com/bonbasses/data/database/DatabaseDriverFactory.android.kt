package com.bonbasses.data.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.bonbasses.database.BonBassesDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = BonBassesDatabase.Schema,
            context = context,
            name = "bonbasses.db",
            callback = object : AndroidSqliteDriver.Callback(BonBassesDatabase.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("PRAGMA foreign_keys=ON")
                    
                    val cursor = db.query("PRAGMA table_info(WritingHistory)")
                    var hasTitleColumn = false
                    
                    while (cursor.moveToNext()) {
                        val columnName = cursor.getString(1)
                        if (columnName == "title") {
                            hasTitleColumn = true
                            break
                        }
                    }
                    cursor.close()
                    
                    if (!hasTitleColumn) {
                        db.execSQL("ALTER TABLE WritingHistory ADD COLUMN title TEXT NOT NULL DEFAULT 'Untitled'")
                    }
                }
            }
        )
    }
}

private var appContext: Context? = null

fun initDatabaseContext(context: Context) {
    appContext = context.applicationContext
}

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory(appContext ?: error("Database context not initialized. Call initDatabaseContext() first."))
}

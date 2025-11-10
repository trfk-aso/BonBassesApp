package com.bonbasses.data.database

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

expect fun createDatabaseDriverFactory(): DatabaseDriverFactory

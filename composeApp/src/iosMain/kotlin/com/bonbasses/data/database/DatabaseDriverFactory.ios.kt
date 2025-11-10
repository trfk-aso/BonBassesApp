package com.bonbasses.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.bonbasses.database.BonBassesDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = BonBassesDatabase.Schema,
            name = "bonbasses.db"
        )
    }
}

actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}

package com.altears.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.altears.db.AltEarsDatabase

actual fun createDatabase(): AltEarsDatabase {
    val driver = NativeSqliteDriver(AltEarsDatabase.Schema, "altears.db")
    return AltEarsDatabase(driver)
}

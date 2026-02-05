package com.altears.di

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.altears.db.AltEarsDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object DatabaseFactory : KoinComponent {
    private val context: Context by inject()
    
    fun create(): AltEarsDatabase {
        val driver = AndroidSqliteDriver(AltEarsDatabase.Schema, context, "altears.db")
        return AltEarsDatabase(driver)
    }
}

actual fun createDatabase(): AltEarsDatabase = DatabaseFactory.create()

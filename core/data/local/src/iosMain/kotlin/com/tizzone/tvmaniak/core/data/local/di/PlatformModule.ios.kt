package com.tizzone.tvmaniak.core.data.local.di

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.tizzone.tvmaniak.core.data.local.cache.TvManiakDatabase
import org.koin.dsl.module

/**
 * Koin module for database and iOS-specific dependencies.
 */
actual val platformModule =
    module {
        single {
            val driver = NativeSqliteDriver(TvManiakDatabase.Schema, "tvmaniak.db")
            TvManiakDatabaseWrapper(TvManiakDatabase(driver))
        }
    }

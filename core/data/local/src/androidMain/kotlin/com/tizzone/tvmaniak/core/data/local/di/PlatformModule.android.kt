package com.tizzone.tvmaniak.core.data.local.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.tizzone.tvmaniak.core.data.local.cache.TvManiakDatabase
import org.koin.dsl.module

actual val platformModule =
    module {
        single {
            val driver =
                AndroidSqliteDriver(TvManiakDatabase.Schema, get(), "tvmaniak.db")

            TvManiakDatabaseWrapper(TvManiakDatabase(driver))
        }
    }

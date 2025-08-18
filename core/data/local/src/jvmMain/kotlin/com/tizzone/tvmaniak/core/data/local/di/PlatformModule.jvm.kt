package com.tizzone.tvmaniak.core.data.local.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.tizzone.tvmaniak.core.data.local.cache.TvManiakDatabase
import org.koin.dsl.module

actual val platformModule =
    module {
        single {
            val driver =
                JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
                    .also { TvManiakDatabase.Schema.create(it) }
            TvManiakDatabaseWrapper(TvManiakDatabase(driver))
        }
    }

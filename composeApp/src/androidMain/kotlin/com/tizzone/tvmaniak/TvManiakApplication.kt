package com.tizzone.tvmaniak

import android.app.Application
import com.tizzone.tvmaniak.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TvManiakApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TvManiakApplication)
            modules(appModule)
        }
    }
}

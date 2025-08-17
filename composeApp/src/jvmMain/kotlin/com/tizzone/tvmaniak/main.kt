package com.tizzone.tvmaniak

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.tizzone.tvmaniak.di.appModule
import org.koin.core.context.GlobalContext.startKoin

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main() {
    startKoin {
        modules(appModule)
    }
    return application {
        Window(
            onCloseRequest = ::exitApplication,
            alwaysOnTop = true,
            title = "TVManiak",
        ) {
            val windowSizeClass = calculateWindowSizeClass()
            App(
                windowSizeClass = windowSizeClass,
                disableDiskCache = false,
            )
        }
    }
}

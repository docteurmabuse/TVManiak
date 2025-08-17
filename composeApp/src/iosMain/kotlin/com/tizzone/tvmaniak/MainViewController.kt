package com.tizzone.tvmaniak

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.window.ComposeUIViewController
import com.tizzone.tvmaniak.di.appModule
import org.koin.core.context.startKoin

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun MainViewController() =
    ComposeUIViewController(
        configure = {
            startKoin {
                modules(appModule)
            }
        },
    ) {
        App(
            windowSizeClass = calculateWindowSizeClass(),
            disableDiskCache = true,
        )
    }

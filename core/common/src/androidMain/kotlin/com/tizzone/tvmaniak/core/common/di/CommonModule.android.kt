package com.tizzone.tvmaniak.core.common.di

import kotlinx.coroutines.Dispatchers

actual fun provideIoDispatcher() = Dispatchers.IO

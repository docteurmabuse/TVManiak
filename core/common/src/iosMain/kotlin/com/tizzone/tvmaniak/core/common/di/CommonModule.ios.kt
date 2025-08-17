package com.tizzone.tvmaniak.core.common.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

actual fun provideIoDispatcher() = Dispatchers.IO

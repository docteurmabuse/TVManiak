package com.tizzone.tvmaniak.core.data.repository.di

import com.tizzone.tvmaniak.core.common.di.TvManiakDispatchers
import com.tizzone.tvmaniak.core.common.di.commonModule
import com.tizzone.tvmaniak.core.data.local.di.platformModule
import com.tizzone.tvmaniak.core.data.remote.di.remoteModule
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule =
    module {
        includes(commonModule, remoteModule, platformModule)
        single<TvShowRepository> {
            TvShowRepositoryImpl(
                tvManiakRemoteDataSource = get(),
                ioDispatcher = get(named(TvManiakDispatchers.IO)),
                tvManiakDatabase = get(),
            )
        }
    }

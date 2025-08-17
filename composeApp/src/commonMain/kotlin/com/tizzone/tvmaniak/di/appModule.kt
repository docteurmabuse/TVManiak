package com.tizzone.tvmaniak.di

import coil3.annotation.ExperimentalCoilApi
import coil3.network.CacheStrategy
import coil3.network.NetworkFetcher
import coil3.network.ktor3.asNetworkClient
import com.tizzone.tvmaniak.feature.tvShowDetails.di.tvShowDetailModule
import com.tizzone.tvmaniak.feature.tvshows.di.showsModule
import com.tizzone.tvmaniak.feature.watchlist.di.watchlistModule
import io.ktor.client.HttpClient
import org.koin.dsl.module

@OptIn(ExperimentalCoilApi::class)
val appModule =
    module {
        includes(showsModule, tvShowDetailModule, watchlistModule)
        single {
            NetworkFetcher.Factory(
                networkClient = { get<HttpClient>().asNetworkClient() },
                cacheStrategy = { CacheStrategy.DEFAULT },
            )
        }
    }

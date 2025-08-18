package com.tizzone.tvmaniak.core.domain.di

import com.tizzone.tvmaniak.core.data.repository.di.repositoryModule
import com.tizzone.tvmaniak.core.domain.tvshows.AddToWatchListUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.GetTvShowDetailsUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.GetTvShowsUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.GetWatchlistUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.RemoveFromWatchListUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.SearchTvShowsUseCase
import org.koin.dsl.module

val domainModule =
    module {
        includes(repositoryModule)
        factory { GetTvShowsUseCase(get()) }
        factory { GetTvShowDetailsUseCase(get()) }
        factory { SearchTvShowsUseCase(get()) }
        factory { AddToWatchListUseCase(get()) }
        factory { RemoveFromWatchListUseCase(get()) }
        factory { GetWatchlistUseCase(get()) }
    }

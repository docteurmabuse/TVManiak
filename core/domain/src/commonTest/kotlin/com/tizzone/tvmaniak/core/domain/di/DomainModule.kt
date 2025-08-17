package com.tizzone.tvmaniak.core.domain.di

import com.tizzone.tvmaniak.core.data.repository.tvshow.FakeTvShowRepositoryImpl
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository
import com.tizzone.tvmaniak.core.domain.tvshows.GetTvShowDetailsUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.GetTvShowsUseCase
import com.tizzone.tvmaniak.core.domain.tvshows.SearchTvShowsUseCase
import org.koin.dsl.module

val testDomainModule =
    module {
        single<TvShowRepository> { FakeTvShowRepositoryImpl() }
        single { FakeTvShowRepositoryImpl() }
        factory { GetTvShowsUseCase(get()) }
        factory { GetTvShowDetailsUseCase(get()) }
        factory { SearchTvShowsUseCase(get()) }
    }

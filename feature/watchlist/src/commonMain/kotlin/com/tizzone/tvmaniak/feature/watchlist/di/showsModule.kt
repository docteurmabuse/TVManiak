package com.tizzone.tvmaniak.feature.watchlist.di

import com.tizzone.tvmaniak.core.domain.di.domainModule
import com.tizzone.tvmaniak.feature.watchlist.presentation.TvShowsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val watchlistModule =
    module {
        includes(domainModule)
        viewModelOf(::TvShowsViewModel)
    }

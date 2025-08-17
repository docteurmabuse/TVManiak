package com.tizzone.tvmaniak.feature.tvshows.di

import com.tizzone.tvmaniak.core.domain.di.domainModule
import com.tizzone.tvmaniak.feature.tvshows.presentation.TvShowsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val showsModule =
    module {
        includes(domainModule)
        viewModelOf(::TvShowsViewModel)
    }

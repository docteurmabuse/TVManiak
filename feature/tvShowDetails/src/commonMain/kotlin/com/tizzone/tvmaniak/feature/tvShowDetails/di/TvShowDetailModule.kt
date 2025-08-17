package com.tizzone.tvmaniak.feature.tvShowDetails.di

import com.tizzone.tvmaniak.core.domain.di.domainModule
import com.tizzone.tvmaniak.feature.tvShowDetails.presentation.TvShowDetailsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tvShowDetailModule =
    module {
        includes(domainModule)
        viewModelOf(::TvShowDetailsViewModel)
    }

package com.tizzone.tvmaniak.core.domain.tvshows

import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository

class GetTvShowsUseCase(
    private val tvShowRepository: TvShowRepository,
) {
    operator fun invoke() = tvShowRepository.getTvShows()
}

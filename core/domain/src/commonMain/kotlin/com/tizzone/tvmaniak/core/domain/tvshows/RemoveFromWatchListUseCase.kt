package com.tizzone.tvmaniak.core.domain.tvshows

import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository

class RemoveFromWatchListUseCase(
    private val tvShowRepository: TvShowRepository,
) {
    suspend operator fun invoke(watchListTvShowId: Int) = tvShowRepository.removeTvShowFromWatchList(watchListTvShowId)
}

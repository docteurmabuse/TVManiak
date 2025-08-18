package com.tizzone.tvmaniak.core.domain.tvshows

import com.tizzone.tvmaniak.core.common.utils.fold
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository
import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetWatchlistUseCase(
    private val tvShowRepository: TvShowRepository,
) {
    operator fun invoke(): Flow<List<TvShowSummary>> =
        tvShowRepository
            .getWatchlistTvShows()
            .map { either ->
                either.fold(
                    onLeft = { emptyList() },
                    onRight = { it },
                )
            }
}

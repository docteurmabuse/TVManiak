package com.tizzone.tvmaniak.core.domain.tvshows

import com.tizzone.tvmaniak.core.common.utils.DatabaseError
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository

/**
 * Adds a TV show to the watchlist.
 *
 * This use case first checks if the TV show is already in the local database.
 * If the show is not found in the database or if the database is not available,
 * it attempts to fetch the show details from the API and caches them.
 * Finally, it adds the TV show to the watchlist.
 *
 * @property tvShowRepository The repository for accessing TV show data.
 */
class AddToWatchListUseCase(
    private val tvShowRepository: TvShowRepository,
) {
    suspend operator fun invoke(watchListTvShowId: Int): Either<DatabaseError, Unit> =
         tvShowRepository.addTvShowToWatchList(watchListTvShowId)
}

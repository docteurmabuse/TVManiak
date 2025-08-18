package com.tizzone.tvmaniak.core.domain.tvshows

import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.DatabaseError
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.common.utils.fold
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository
import com.tizzone.tvmaniak.core.model.TvShowDetailsWithCast
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

/**
 * Use case for retrieving details of a TV show, including its cast.
 *
 * This use case first attempts to fetch the show details from a remote source.
 * If the remote fetch is successful, the data is cached locally, and the watchlist status
 * for the show is determined.
 * If the remote fetch fails, it falls back to retrieving the details from the local cache.
 * If both remote and local sources fail, a [DatabaseError.OperationFailed] is returned.
 *
 * After successfully retrieving or falling back to local show details, the cast information
 * for the show is fetched. If fetching the cast fails, an empty list is returned for the cast.
 *
 * The final result is a [TvShowDetailsWithCast] object, which combines the show details
 * and its cast, wrapped in an [Either] to represent success or failure.
 *
 * @property tvShowRepository The repository responsible for fetching TV show data.
 */
class GetTvShowDetailsUseCase(
    private val tvShowRepository: TvShowRepository,
) {
    suspend operator fun invoke(showId: Int): Either<DatabaseError, TvShowDetailsWithCast> =
        coroutineScope {
            // Always fetch from remote to ensure we have high-quality images and complete data
            val remoteDeferred = async { tvShowRepository.getShowDetailsRemote(showId).first() }
            val remoteResult = remoteDeferred.await()

            val details =
                remoteResult.fold(
                    onLeft = {
                        // Remote failed, try local cache as fallback
                        val localResult = tvShowRepository.getShowById(showId).first()
                        localResult.fold(
                            onLeft = {
                                // Both remote and local failed
                                return@coroutineScope Either.Left(DatabaseError.OperationFailed)
                            },
                            onRight = { localShow ->
                                localShow
                            },
                        )
                    },
                    onRight = { remoteShow ->
                        // Cache the remote result for future use
                        tvShowRepository.insertOrUpdateShowDetail(remoteShow).fold(
                            onLeft = { error ->
                                Logger.w { "Failed to cache show details: $error" }
                            },
                            onRight = {
                                Logger.d { "Successfully cached show $showId details" }
                            },
                        )

                        // Get watchlist status
                        val watchlistResult = tvShowRepository.isTvShowInWatchList(showId).first()
                        val isInWatchlist =
                            watchlistResult.fold(
                                onLeft = { false },
                                onRight = { it },
                            )

                        remoteShow.copy(isInWatchlist = isInWatchlist)
                    },
                )

            // Fetch cast information
            val castDeferred = async { tvShowRepository.getShowCast(showId) }
            val castResult = castDeferred.await()

            val cast =
                castResult.fold(
                    onLeft = { emptyList() }, // If cast fails, return empty list
                    onRight = { it },
                )

            Either.Right(
                TvShowDetailsWithCast(
                    details = details,
                    cast = cast,
                ),
            )
        }
}

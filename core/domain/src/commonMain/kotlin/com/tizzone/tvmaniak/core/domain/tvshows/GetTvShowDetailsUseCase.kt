package com.tizzone.tvmaniak.core.domain.tvshows

import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.DatabaseError
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.common.utils.fold
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository
import com.tizzone.tvmaniak.core.model.ShowCastSummary
import com.tizzone.tvmaniak.core.model.TvShowDetail
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

data class TvShowDetailsWithCast(
    val details: TvShowDetail,
    val cast: List<ShowCastSummary>,
)

class GetTvShowDetailsUseCase(
    private val tvShowRepository: TvShowRepository,
) {
    suspend operator fun invoke(showId: Int): Either<DatabaseError, TvShowDetailsWithCast> =
        coroutineScope {
            // Always fetch from remote to ensure we have high-quality images and complete data
            val remoteDeferred = async { tvShowRepository.getShowDetailsRemote(showId).first() }
            val remoteResult = remoteDeferred.await()

            val details = remoteResult.fold(
                onLeft = {
                    // Remote failed, try local cache as fallback
                    val localResult = tvShowRepository.getShowDetails(showId).first()
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

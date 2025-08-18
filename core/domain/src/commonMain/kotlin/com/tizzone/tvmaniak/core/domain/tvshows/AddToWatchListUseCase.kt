package com.tizzone.tvmaniak.core.domain.tvshows

import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.DatabaseError
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.common.utils.fold
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository
import kotlinx.coroutines.flow.first

class AddToWatchListUseCase(
    private val tvShowRepository: TvShowRepository,
) {
    suspend operator fun invoke(watchListTvShowId: Int): Either<DatabaseError, Unit> {
        val showInDb = tvShowRepository.getShowById(watchListTvShowId).first()

        showInDb.fold(
            onLeft = { error ->
                when (error) {
                    DatabaseError.DatabaseNotAvailable -> {
                        Logger.d("AddToWatchListUseCase") { "Show $watchListTvShowId not found in cache, fetching from API" }

                        val showDetailsResult = tvShowRepository.getShowDetails(watchListTvShowId).first()
                        showDetailsResult.fold(
                            onLeft = { apiError ->
                                Logger.e("AddToWatchListUseCase") { "Failed to fetch show from API: $apiError" }
                                return Either.Left(DatabaseError.OperationFailed)
                            },
                            onRight = { _ ->
                                Logger.d("AddToWatchListUseCase") { "Show $watchListTvShowId fetched and cached" }
                            },
                        )
                    }

                    else -> {
                        Logger.e("AddToWatchListUseCase") { "Database error: $error" }
                        return Either.Left(error)
                    }
                }
            },
            onRight = { show ->
                if (show != null) {
                    Logger.d("AddToWatchListUseCase") { "Show $watchListTvShowId already in cache" }
                } else {
                    Logger.d("AddToWatchListUseCase") { "Show $watchListTvShowId not found, fetching from API" }

                    val showDetailsResult = tvShowRepository.getShowDetails(watchListTvShowId).first()
                    showDetailsResult.fold(
                        onLeft = { apiError ->
                            Logger.e("AddToWatchListUseCase") { "Failed to fetch show from API: $apiError" }
                            return Either.Left(DatabaseError.OperationFailed)
                        },
                        onRight = { _ ->
                            Logger.d("AddToWatchListUseCase") { "Show $watchListTvShowId fetched and cached" }
                        },
                    )
                }
            },
        )

        return tvShowRepository.addTvShowToWatchList(watchListTvShowId)
    }
}

package com.tizzone.tvmaniak.core.domain.tvshows

import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.fold
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository
import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException

class SearchTvShowsUseCase(
    private val tvShowRepository: TvShowRepository,
) {
    operator fun invoke(query: String): Flow<List<TvShowSummary>> =
        flow {
            try {
                Logger.d { "SearchTvShowsUseCase: Starting reactive search for query: $query" }

                // Get remote results as Flow
                val remoteFlow = tvShowRepository.searchTvShowsRemote(query)
                val remoteResult = remoteFlow.first()

                val baseResults =
                    remoteResult.fold(
                        onLeft = { error ->
                            Logger.e { "SearchTvShowsUseCase: Remote search failed ($error), falling back to local for query: $query" }
                            tvShowRepository
                                .searchTvShowsLocal(query)
                                .catch { e ->
                                    if (e !is CancellationException) {
                                        Logger.e { "SearchTvShowsUseCase: Local search also failed for query: $query" }
                                    }
                                    emit(emptyList())
                                }.first()
                        },
                        onRight = { results ->
                            Logger.d { "SearchTvShowsUseCase: Remote search successful, found ${results.size} results" }

                            // Cache the search results
                            if (results.isNotEmpty()) {
                                tvShowRepository.insertOrUpdateShows(results).fold(
                                    onLeft = { error ->
                                        Logger.w { "SearchTvShowsUseCase: Failed to cache search results: $error" }
                                    },
                                    onRight = {
                                        Logger.d { "SearchTvShowsUseCase: Successfully cached ${results.size} search results" }
                                    },
                                )
                            }

                            results
                        },
                    )

                if (baseResults.isEmpty()) {
                    emit(emptyList())
                    return@flow
                }

                // Create a flow that combines base results with watchlist changes
                val watchlistFlow = tvShowRepository.getWatchlistTvShows()

                watchlistFlow.collect { watchlistResult ->
                    val watchlistIds =
                        watchlistResult.fold(
                            onLeft = { emptySet<Int>() },
                            onRight = { watchlist -> watchlist.map { it.id }.toSet() },
                        )

                    val updatedResults =
                        baseResults.map { show ->
                            show.copy(isInWatchList = watchlistIds.contains(show.id))
                        }

                    emit(updatedResults)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Logger.e(e) { "SearchTvShowsUseCase: Exception during reactive search for query '$query'" }
                emit(emptyList())
            }
        }
}

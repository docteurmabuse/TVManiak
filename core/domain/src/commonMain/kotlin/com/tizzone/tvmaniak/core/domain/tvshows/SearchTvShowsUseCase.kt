package com.tizzone.tvmaniak.core.domain.tvshows

import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository
import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class SearchTvShowsUseCase(
    private val tvShowRepository: TvShowRepository,
) {
    suspend operator fun invoke(query: String): List<TvShowSummary> = coroutineScope {
        val localDeferred = async { tvShowRepository.searchTvShowsLocal(query) }
        val remoteDeferred = async { tvShowRepository.searchTvShowsRemote(query) }

        val localResults = localDeferred.await()
        val remoteResults = when (val result = remoteDeferred.await()) {
            is Either.Left -> {
                Logger.e("SearchTvShowsUseCase") { "Remote search failed: ${result.value}" }
                emptyList()
            }
            is Either.Right -> result.value
        }

        return@coroutineScope mergeSearchResults(localResults, remoteResults)
    }

    private fun mergeSearchResults(
        localResults: List<TvShowSummary>,
        remoteResults: List<TvShowSummary>
    ): List<TvShowSummary> {
        val merged = mutableMapOf<Int, TvShowSummary>()
        // Add local results first
        localResults.forEach { tvShow ->
            merged[tvShow.id] = tvShow
        }
        // Add or update with remote results if they're newer
        remoteResults.forEach { tvShow ->
            val existing = merged[tvShow.id]
            if (existing == null || tvShow.updated > existing.updated) {
                merged[tvShow.id] = tvShow
            }
        }
        // Return results sorted by score (highest to lowest)
        return merged.values.sortedByDescending { it.score ?: 0f }
    }
}

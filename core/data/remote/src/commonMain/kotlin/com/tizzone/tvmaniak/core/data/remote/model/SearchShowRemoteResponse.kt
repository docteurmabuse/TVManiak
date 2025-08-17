package com.tizzone.tvmaniak.core.data.remote.model

import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.serialization.Serializable

@Serializable
data class SearchShowRemoteResponse(
    val score: Float,
    val show: ShowRemoteResponse,
)

fun SearchShowRemoteResponse.toTvShowSummary(): TvShowSummary = show.toShowSummary(score)

fun List<SearchShowRemoteResponse>.toTvShowSummaryList(): List<TvShowSummary> = map { it.toTvShowSummary() }

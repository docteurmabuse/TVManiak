package com.tizzone.tvmaniak.feature.tvShowDetails.model

sealed class TvShowDetailEvent {
    data class LoadTvShowDetail(
        val showId: Int,
    ) : TvShowDetailEvent()

    data class AddToWatchList(
        val showId: Int,
    ) : TvShowDetailEvent()

    data class RemoveFromWatchList(
        val showId: Int,
    ) : TvShowDetailEvent()
}

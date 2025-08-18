package com.tizzone.tvmaniak.feature.tvshows.model

sealed interface TvShowsEvent {
    data object ToggleViewMode : TvShowsEvent

    data class UpdateSearchQuery(
        val query: String,
    ) : TvShowsEvent

    data class SearchTvShows(
        val query: String,
    ) : TvShowsEvent

    data class RemoveFromWatchList(
        val showId: Int,
    ) : TvShowsEvent

    data class AddToWatchList(
        val showId: Int,
    ) : TvShowsEvent
}

package com.tizzone.tvmaniak.feature.tvShowDetails.model

sealed class TvShowDetailEvent {
    data class LoadTvShowDetail(
        val showId: Int,
    ) : TvShowDetailEvent()
}

package com.tizzone.tvmaniak.feature.tvShowDetails.model

import com.tizzone.tvmaniak.core.model.ShowCastSummary
import com.tizzone.tvmaniak.core.model.TvShowDetail

sealed interface TvShowDetailState {
    data object Loading : TvShowDetailState

    data class Success(
        val tvShowDetail: TvShowDetail,
        val cast: List<ShowCastSummary> = emptyList(),
    ) : TvShowDetailState

    data class Error(
        val message: String,
    ) : TvShowDetailState
}

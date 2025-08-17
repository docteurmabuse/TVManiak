package com.tizzone.tvmaniak.core.data.repository.tvshow

import app.cash.paging.PagingData
import com.tizzone.tvmaniak.core.common.utils.ApiResponse
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.model.ShowCastSummary
import com.tizzone.tvmaniak.core.model.TvShowDetail
import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.coroutines.flow.Flow

interface TvShowRepository {
    fun getTvShows(): Flow<PagingData<TvShowSummary>>
    suspend fun getShowDetails(showId: Int): Either<ApiResponse, TvShowDetail>
    suspend fun searchTvShowsLocal(query: String): List<TvShowSummary>
    suspend fun searchTvShowsRemote(query: String): Either<ApiResponse, List<TvShowSummary>>
    suspend fun getShowCast(showId: Int): Either<ApiResponse, List<ShowCastSummary>>
}

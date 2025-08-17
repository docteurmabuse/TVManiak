package com.tizzone.tvmaniak.core.data.remote

import com.tizzone.tvmaniak.core.data.remote.model.CastRemoteResponse
import com.tizzone.tvmaniak.core.data.remote.model.SearchShowRemoteResponse
import com.tizzone.tvmaniak.core.data.remote.model.ShowRemoteResponse

interface TvManiakRemoteDataSource {
    suspend fun getShowsByPage(page: Int): List<ShowRemoteResponse>

    suspend fun getShowDetails(showId: Int): ShowRemoteResponse

    suspend fun searchTvShows(query: String): List<SearchShowRemoteResponse>

    suspend fun getShowCast(showId: Int): List<CastRemoteResponse>
}

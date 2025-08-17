package com.tizzone.tvmaniak.core.data.remote.ktor

import com.tizzone.tvmaniak.core.data.remote.TvManiakRemoteDataSource
import com.tizzone.tvmaniak.core.data.remote.model.CastRemoteResponse
import com.tizzone.tvmaniak.core.data.remote.model.SearchShowRemoteResponse
import com.tizzone.tvmaniak.core.data.remote.model.ShowRemoteResponse
import com.tizzone.tvmaniak.core.data.remote.utils.PAGE
import com.tizzone.tvmaniak.core.data.remote.utils.SHOWS_ROUTE
import com.tizzone.tvmaniak.core.data.remote.utils.SHOWS_SEARCH_ROUTE
import com.tizzone.tvmaniak.core.data.remote.utils.SHOW_CAST
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class KtorTvManiakRemote(
    private val tvManiakClient: HttpClient,
) : TvManiakRemoteDataSource {
    override suspend fun getShowsByPage(page: Int) =
        tvManiakClient
            .get("$SHOWS_ROUTE?$PAGE=$page")
            .body<List<ShowRemoteResponse>>()

    override suspend fun getShowDetails(showId: Int) =
        tvManiakClient
            .get("$SHOWS_ROUTE/$showId")
            .body<ShowRemoteResponse>()

    override suspend fun searchTvShows(query: String): List<SearchShowRemoteResponse> =
        tvManiakClient
            .get("$SHOWS_SEARCH_ROUTE?q=$query")
            .body<List<SearchShowRemoteResponse>>()

    override suspend fun getShowCast(showId: Int) =
        tvManiakClient
            .get("$SHOWS_ROUTE/$showId/$SHOW_CAST")
            .body<List<CastRemoteResponse>>()
}

package com.tizzone.tvmaniak.core.data.repository.tvshow

import app.cash.paging.PagingData
import com.tizzone.tvmaniak.core.common.utils.ApiResponse
import com.tizzone.tvmaniak.core.common.utils.DatabaseError
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.model.ShowCastSummary
import com.tizzone.tvmaniak.core.model.TvShowDetail
import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing TV show data.
 *
 * This interface defines the contract for accessing and manipulating TV show information,
 * including fetching lists of shows, retrieving details for specific shows,
 * searching for shows, managing watchlist status, and interacting with local and remote data sources.
 */
interface TvShowRepository {
    fun getTvShows(): Flow<PagingData<TvShowSummary>>

    fun getShowDetails(showId: Int): Flow<Either<ApiResponse, TvShowDetail>>

    fun getShowDetailsRemote(showId: Int): Flow<Either<ApiResponse, TvShowDetail>>

    fun searchTvShowsLocal(query: String): Flow<List<TvShowSummary>>

    fun searchTvShowsRemote(query: String): Flow<Either<ApiResponse, List<TvShowSummary>>>

    suspend fun getShowCast(showId: Int): Either<ApiResponse, List<ShowCastSummary>>

    suspend fun addTvShowToWatchList(showId: Int): Either<DatabaseError, Unit>

    suspend fun removeTvShowFromWatchList(showId: Int): Either<DatabaseError, Unit>

    fun isTvShowInWatchList(showId: Int): Flow<Either<DatabaseError, Boolean>>

    fun getShowById(showId: Int): Flow<Either<DatabaseError, TvShowDetail?>>

    fun getWatchlistTvShows(): Flow<Either<DatabaseError, List<TvShowSummary>>>

    suspend fun insertOrUpdateShows(shows: List<TvShowSummary>): Either<DatabaseError, Unit>

    suspend fun insertOrUpdateShowDetail(show: TvShowDetail): Either<DatabaseError, Unit>
}

package com.tizzone.tvmaniak.core.data.repository.tvshow

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import app.cash.paging.PagingData
import app.cash.sqldelight.paging3.QueryPagingSource
import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.ApiResponse
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.data.local.Tv_show
import com.tizzone.tvmaniak.core.data.local.di.TvManiakDatabaseWrapper
import com.tizzone.tvmaniak.core.data.remote.TvManiakRemoteDataSource
import com.tizzone.tvmaniak.core.data.remote.model.CastRemoteResponse
import com.tizzone.tvmaniak.core.data.remote.model.toShowCastSummary
import com.tizzone.tvmaniak.core.data.remote.model.toShowDetail
import com.tizzone.tvmaniak.core.data.remote.model.toTvShowSummaryList
import com.tizzone.tvmaniak.core.model.ShowCastSummary
import com.tizzone.tvmaniak.core.model.TvShowDetail
import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

class TvShowRepositoryImpl(
    private val tvManiakRemoteDataSource: TvManiakRemoteDataSource,
    private val tvManiakDatabase: TvManiakDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher,
) : TvShowRepository {
    val tvManiakQueries = tvManiakDatabase.instance?.tvManiakQueries

    @OptIn(ExperimentalPagingApi::class)
    override fun getTvShows(): Flow<PagingData<TvShowSummary>> {
        return if (tvManiakQueries != null) {
             Pager(
                config = PagingConfig(
                    pageSize = 250,
                    initialLoadSize = 250,
                    enablePlaceholders = false,
                    prefetchDistance = 125,
                    maxSize = 1000
                ),
                remoteMediator = TvShowRemoteMediator(
                    tvManiakRemoteDataSource,
                    tvManiakDatabase = tvManiakDatabase,
                    ioDispatcher = ioDispatcher
                ),
                pagingSourceFactory = {
                    QueryPagingSource(
                        countQuery = tvManiakQueries.countAllShows() ,
                        transacter = tvManiakQueries,
                        context = ioDispatcher,
                        queryProvider = { limit, offset ->
                           tvManiakQueries.getPagedShows(limit, offset)
                        }
                    )
                }
            ).flow.map { pagingData ->
                pagingData.map { entity ->
                    entity.toDomainModel()
                }
            }
        } else {
            Pager(
                config = PagingConfig(
                    pageSize = 250,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    object : PagingSource<Int, TvShowSummary>() {
                        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TvShowSummary> {
                            return LoadResult.Page(
                                data = emptyList(),
                                prevKey = null,
                                nextKey = null
                            )
                        }
                        override fun getRefreshKey(state: PagingState<Int, TvShowSummary>): Int? = null
                    }
                }
            ).flow
        }
    }

    override suspend fun getShowDetails(showId: Int): Either<ApiResponse, TvShowDetail> =
        withContext(ioDispatcher) {
            try {
                val showDetails = tvManiakRemoteDataSource.getShowDetails(showId)
                Either.Right(
                    showDetails.toShowDetail(),
                )
            } catch (e: IOException) {
                Logger.e(e.message ?: "Error getting show details", throwable = e)
                Either.Left(ApiResponse.IOException)
            } catch (e: Exception) {
                Logger.e(e.message ?: "Error getting show details", throwable = e)
                Either.Left(ApiResponse.HttpError)
            }
        }

    override suspend fun searchTvShowsLocal(query: String): List<TvShowSummary> =
        withContext(ioDispatcher) {
            try {
                val localResults = tvManiakQueries?.searchShowByName(query)
                    ?.executeAsList()
                    ?.map { localTvShow ->
                        localTvShow.toDomainModel()
                    }
                    ?: emptyList()
                localResults
            } catch (e: Exception) {
                Logger.e("Unexpected error searching local database: ${e.message}", throwable = e)
                emptyList()
            }
        }

    override suspend fun searchTvShowsRemote(query: String): Either<ApiResponse, List<TvShowSummary>> =
        withContext(ioDispatcher) {
            try {
                val searchResults = tvManiakRemoteDataSource.searchTvShows(query = query)
                val tvShowSummaries = searchResults.toTvShowSummaryList()

                // Save search results with scores to database
                tvManiakQueries?.transaction {
                    tvShowSummaries.forEach { tvShow ->
                        // Get existing page value if the show already exists
                        val existingShow = tvManiakQueries.getShowById(tvShow.id.toLong()).executeAsOneOrNull()
                        val pageValue = existingShow?.page ?: 0L

                        tvManiakQueries.insertShow(
                            id = tvShow.id.toLong(),
                            name = tvShow.name,
                            language = tvShow.language,
                            type = tvShow.type,
                            status = tvShow.status,
                            genres = tvShow.genres.joinToString(","),
                            rating = tvShow.rating?.toDouble(),
                            image_url = tvShow.smallImageUrl,
                            summary = tvShow.summary,
                            page = pageValue,
                            updated = tvShow.updated,
                            score = tvShow.score?.toDouble()
                        )
                    }
                }

                Either.Right(tvShowSummaries)
            } catch (e: IOException) {
                Logger.e(e.message ?: "IOException", throwable = e)
                Either.Left(ApiResponse.IOException)
            } catch (e: Exception) {
                Logger.e(e.message ?: "HttpException", throwable = e)
                Either.Left(ApiResponse.HttpError)
            }
        }

    override suspend fun getShowCast(showId: Int): Either<ApiResponse, List<ShowCastSummary>> =
        withContext(ioDispatcher) {
            try {
                val showCast = tvManiakRemoteDataSource.getShowCast(showId)
                Either.Right(
                    showCast.map(CastRemoteResponse::toShowCastSummary),
                )
            } catch (e: IOException) {
                Logger.e("Error getting show cast: ${e.message}", throwable = e)
                Either.Left(ApiResponse.IOException)
            } catch (e: Exception) {
                Logger.e("Error getting show cast: ${e.message}", throwable = e)
                Either.Left(ApiResponse.HttpError)
            }
        }
}

private fun Tv_show.toDomainModel(): TvShowSummary {
    return TvShowSummary(
        id = this.id.toInt(),
        name = this.name,
        summary = this.summary ?: "",
        type = this.type ?: "",
        language = this.language ?: "",
        genres = this.genres?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        status = this.status ?: "",
        rating = this.rating?.toFloat(),
        smallImageUrl = this.image_url ?: "",
        updated = this.updated,
        score = this.score?.toFloat()
    )
}

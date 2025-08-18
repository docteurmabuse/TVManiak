package com.tizzone.tvmaniak.core.data.repository.tvshow

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import app.cash.paging.PagingData
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneNotNull
import app.cash.sqldelight.paging3.QueryPagingSource
import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.ApiResponse
import com.tizzone.tvmaniak.core.common.utils.DatabaseError
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.common.utils.fold
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

/**
 * Implementation of the [TvShowRepository] interface.
 *
 * This class provides methods to interact with TV show data from both local and remote sources.
 * It uses [TvManiakRemoteDataSource] for fetching data from the network and [TvManiakDatabaseWrapper]
 * for accessing the local database. It also leverages [CoroutineDispatcher] for managing coroutine execution.
 *
 * @property tvManiakRemoteDataSource The remote data source for TV shows.
 * @property tvManiakDatabase The local database wrapper for TV shows.
 * @property ioDispatcher The coroutine dispatcher for I/O operations.
 */
class TvShowRepositoryImpl(
    private val tvManiakRemoteDataSource: TvManiakRemoteDataSource,
    private val tvManiakDatabase: TvManiakDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher,
) : TvShowRepository {
    val tvManiakQueries = tvManiakDatabase.instance?.tvManiakQueries

    @OptIn(ExperimentalPagingApi::class)
    override fun getTvShows(): Flow<PagingData<TvShowSummary>> =
        if (tvManiakQueries != null) {
            Pager(
                config =
                    PagingConfig(
                        pageSize = 250,
                        initialLoadSize = 250,
                        enablePlaceholders = false,
                        prefetchDistance = 125,
                        maxSize = 1000,
                    ),
                remoteMediator =
                    TvShowRemoteMediator(
                        tvManiakRemoteDataSource,
                        tvManiakDatabase = tvManiakDatabase,
                        ioDispatcher = ioDispatcher,
                    ),
                pagingSourceFactory = {
                    QueryPagingSource(
                        countQuery = tvManiakQueries.countAllShows(),
                        transacter = tvManiakQueries,
                        context = ioDispatcher,
                        queryProvider = { limit, offset ->
                            tvManiakQueries.getPagedShows(limit, offset)
                        },
                    )
                },
            ).flow.map { pagingData ->
                pagingData.map { entity ->
                    entity.toDomainModel()
                }
            }
        } else {
            Pager(
                config =
                    PagingConfig(
                        pageSize = 250,
                        enablePlaceholders = false,
                    ),
                pagingSourceFactory = {
                    object : PagingSource<Int, TvShowSummary>() {
                        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TvShowSummary> =
                            LoadResult.Page(
                                data = emptyList(),
                                prevKey = null,
                                nextKey = null,
                            )

                        override fun getRefreshKey(state: PagingState<Int, TvShowSummary>): Int? = null
                    }
                },
            ).flow
        }

    // Keep for backward compatibility - combines local cache with watchlist status
    override fun getShowDetails(showId: Int): Flow<Either<ApiResponse, TvShowDetail>> =
        combine(
            getShowById(showId),
            isTvShowInWatchList(showId),
        ) { localShow, watchlistStatus ->
            val localResult =
                localShow.fold(
                    onLeft = { null },
                    onRight = { it },
                )

            val isInWatchlist =
                watchlistStatus.fold(
                    onLeft = { false },
                    onRight = { it },
                )

            // Only return local cached data with updated watchlist status
            localResult?.let { cachedShow ->
                Either.Right(cachedShow.copy(isInWatchlist = isInWatchlist))
            } ?: Either.Left(ApiResponse.HttpError) // No local cache available
        }

    override fun getShowDetailsRemote(showId: Int): Flow<Either<ApiResponse, TvShowDetail>> =
        flow {
            try {
                val showDetails = tvManiakRemoteDataSource.getShowDetails(showId)
                val tvShowDetail = showDetails.toShowDetail()
                emit(Either.Right(tvShowDetail))
            } catch (e: IOException) {
                Logger.e(e.message ?: "Error getting show details from remote", throwable = e)
                emit(Either.Left(ApiResponse.IOException))
            } catch (e: Exception) {
                Logger.e(e.message ?: "Error getting show details from remote", throwable = e)
                emit(Either.Left(ApiResponse.HttpError))
            }
        }.flowOn(ioDispatcher)

    override fun searchTvShowsLocal(query: String): Flow<List<TvShowSummary>> =
        tvManiakQueries
            ?.searchShowByName(query)
            ?.asFlow()
            ?.mapToList(ioDispatcher)
            ?.map { localShows ->
                try {
                    localShows.map { localTvShow ->
                        localTvShow.toDomainModel()
                    }
                } catch (e: Exception) {
                    Logger.e("Error converting local search results to domain models: ${e.message}", throwable = e)
                    emptyList()
                }
            }
            ?: flowOf(emptyList())

    override fun searchTvShowsRemote(query: String): Flow<Either<ApiResponse, List<TvShowSummary>>> =
        flow {
            try {
                val searchResults = tvManiakRemoteDataSource.searchTvShows(query = query)
                val tvShowSummaries = searchResults.toTvShowSummaryList()
                emit(Either.Right(tvShowSummaries))
            } catch (e: IOException) {
                Logger.e(e.message ?: "IOException", throwable = e)
                emit(Either.Left(ApiResponse.IOException))
            } catch (e: Exception) {
                Logger.e(e.message ?: "HttpException", throwable = e)
                emit(Either.Left(ApiResponse.HttpError))
            }
        }.flowOn(ioDispatcher)

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

    override suspend fun addTvShowToWatchList(showId: Int): Either<DatabaseError, Unit> =
        withContext(ioDispatcher) {
            try {
                if (tvManiakQueries == null) {
                    Logger.w { "TvManiakQueries is null, cannot add to watchlist." }
                    return@withContext Either.Left(DatabaseError.DatabaseNotAvailable)
                }
                tvManiakQueries.addToWatchlist(showId.toLong())
                Either.Right(Unit)
            } catch (e: Exception) {
                Logger.e("Unexpected error adding show to watchlist: ${e.message}", throwable = e)
                Either.Left(DatabaseError.OperationFailed)
            }
        }

    override suspend fun removeTvShowFromWatchList(showId: Int): Either<DatabaseError, Unit> =
        withContext(ioDispatcher) {
            try {
                if (tvManiakQueries == null) {
                    Logger.w { "TvManiakQueries is null, cannot add to watchlist." }
                    return@withContext Either.Left(DatabaseError.DatabaseNotAvailable)
                }
                tvManiakQueries.removeFromWatchlist(showId.toLong())
                Either.Right(Unit)
            } catch (e: Exception) {
                Logger.e("Unexpected error adding show to watchlist: ${e.message}", throwable = e)
                Either.Left(DatabaseError.OperationFailed)
            }
        }

    override fun isTvShowInWatchList(showId: Int): Flow<Either<DatabaseError, Boolean>> =
        tvManiakQueries
            ?.isInWatchlist(showId.toLong())
            ?.asFlow()
            ?.mapToOneNotNull(ioDispatcher)
            ?.map { isInWatchlist ->
                try {
                    Either.Right(isInWatchlist)
                } catch (e: Exception) {
                    Logger.e("Error checking if show is in watchlist: ${e.message}", throwable = e)
                    Either.Left(DatabaseError.OperationFailed)
                }
            }
            ?: flowOf(Either.Left(DatabaseError.DatabaseNotAvailable))

    override fun getShowById(showId: Int): Flow<Either<DatabaseError, TvShowDetail?>> =
        tvManiakQueries
            ?.getShowById(showId.toLong())
            ?.asFlow()
            ?.mapToOneNotNull(ioDispatcher)
            ?.map { showDetail ->
                try {
                    val domainModel = showDetail.toDomainModel()
                    Either.Right(domainModel)
                } catch (e: Exception) {
                    Logger.e("Error converting show to domain model: ${e.message}", throwable = e)
                    Either.Left(DatabaseError.OperationFailed)
                }
            }
            ?: flowOf(Either.Left(DatabaseError.DatabaseNotAvailable))

    override fun getWatchlistTvShows(): Flow<Either<DatabaseError, List<TvShowSummary>>> =
        tvManiakQueries
            ?.getWatchlist()
            ?.asFlow()
            ?.mapToList(ioDispatcher)
            ?.map { watchlistShows ->
                try {
                    val domainModels = watchlistShows.map { it.toDomainModel() }
                    Either.Right(domainModels)
                } catch (e: Exception) {
                    Logger.e("Error converting watchlist shows to domain models: ${e.message}", throwable = e)
                    Either.Left(DatabaseError.OperationFailed)
                }
            }
            ?: flowOf(Either.Left(DatabaseError.DatabaseNotAvailable))

    override suspend fun insertOrUpdateShows(shows: List<TvShowSummary>): Either<DatabaseError, Unit> =
        withContext(ioDispatcher) {
            try {
                if (tvManiakQueries == null) {
                    Logger.w { "TvManiakQueries is null, cannot insert shows." }
                    return@withContext Either.Left(DatabaseError.DatabaseNotAvailable)
                }

                tvManiakQueries.transaction {
                    shows.forEach { tvShow ->
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
                            image_url = tvShow.imageUrl,
                            large_image_url = tvShow.largeImageUrl,
                            summary = tvShow.summary,
                            page = pageValue,
                            updated = tvShow.updated,
                            score = tvShow.score?.toDouble(),
                        )
                    }
                }
                Either.Right(Unit)
            } catch (e: Exception) {
                Logger.e("Unexpected error inserting shows: ${e.message}", throwable = e)
                Either.Left(DatabaseError.OperationFailed)
            }
        }

    override suspend fun insertOrUpdateShowDetail(show: TvShowDetail): Either<DatabaseError, Unit> =
        withContext(ioDispatcher) {
            try {
                if (tvManiakQueries == null) {
                    Logger.w { "TvManiakQueries is null, cannot insert show detail." }
                    return@withContext Either.Left(DatabaseError.DatabaseNotAvailable)
                }

                tvManiakQueries.insertShow(
                    id = show.id.toLong(),
                    name = show.name,
                    language = show.language,
                    type = show.type,
                    status = show.status,
                    genres = show.genres.joinToString(","),
                    rating = show.rating?.toDouble(),
                    image_url = show.largeImageUrl, // Use large for backward compatibility
                    large_image_url = show.largeImageUrl,
                    summary = show.summary,
                    page = 0,
                    updated = show.updated,
                    score = show.score?.toDouble() ?: 0.0,
                )
                Either.Right(Unit)
            } catch (e: Exception) {
                Logger.e("Unexpected error inserting show detail: ${e.message}", throwable = e)
                Either.Left(DatabaseError.OperationFailed)
            }
        }
}

package com.tizzone.tvmaniak.core.data.repository.tvshow

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingState
import app.cash.paging.LoadType
import app.cash.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.tizzone.tvmaniak.core.common.utils.ApiResponse
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.data.local.GetPagedShows
import com.tizzone.tvmaniak.core.data.local.Remote_keys
import com.tizzone.tvmaniak.core.data.local.di.TvManiakDatabaseWrapper
import com.tizzone.tvmaniak.core.data.remote.TvManiakRemoteDataSource
import com.tizzone.tvmaniak.core.data.remote.model.ShowRemoteResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

/**
 * RemoteMediator implementation for fetching and caching TV shows from a remote data source.
 * This class handles pagination, cache expiration, and data synchronization between the remote API
 * and the local database.
 *
 * @param tvManiakRemoteDataSource The remote data source for fetching TV shows.
 * @param tvManiakDatabase The local database wrapper for storing and retrieving TV show data.
 * @param ioDispatcher The coroutine dispatcher for performing I/O operations.
 */
@OptIn(ExperimentalPagingApi::class)
class TvShowRemoteMediator(
    private val tvManiakRemoteDataSource: TvManiakRemoteDataSource,
    private val tvManiakDatabase: TvManiakDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher,
) : RemoteMediator<Int, GetPagedShows>() {
    companion object {
        private const val STARTING_PAGE_INDEX = 0
        private const val CACHE_TIMEOUT_MS = 60 * 60 * 1000L
    }

    private val tvManiakQueries = tvManiakDatabase.instance?.tvManiakQueries

    override suspend fun initialize(): InitializeAction =
        withContext(ioDispatcher) {
            val cacheCheckResult = checkCacheExpiration()

            return@withContext when (cacheCheckResult) {
                is Either.Left -> {
                    Logger.e("TvShowRemoteMediator") { "Cache check failed: ${cacheCheckResult.value}" }
                    InitializeAction.LAUNCH_INITIAL_REFRESH
                }

                is Either.Right -> {
                    if (cacheCheckResult.value) {
                        InitializeAction.LAUNCH_INITIAL_REFRESH
                    } else {
                        InitializeAction.SKIP_INITIAL_REFRESH
                    }
                }
            }
        }

    private fun checkCacheExpiration(): Either<ApiResponse, Boolean> =
        try {
            if (tvManiakQueries == null) {
                Logger.w("TvShowRemoteMediator") { "Database queries null, cache considered expired" }
                Either.Right(true)
            } else {
                val isExpired = tvManiakQueries.isCacheExpired(CACHE_TIMEOUT_MS.toString()).executeAsOne() > 0
                Either.Right(isExpired)
            }
        } catch (e: Exception) {
            Logger.e("TvShowRemoteMediator", e) { "Failed to check cache expiration: ${e.message}" }
            Either.Left(ApiResponse.HttpError)
        }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GetPagedShows>,
    ): MediatorResult =
        withContext(ioDispatcher) {
            try {
                val page =
                    when (loadType) {
                        LoadType.REFRESH -> {
                            val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                            remoteKey?.next_key?.toInt()?.minus(1) ?: STARTING_PAGE_INDEX
                        }

                        LoadType.PREPEND -> {
                            val remoteKey = getRemoteKeyForFirstItem(state)
                            val prevKey = remoteKey?.prev_key?.toInt()
                            if (prevKey == null) {
                                return@withContext MediatorResult.Success(
                                    endOfPaginationReached = remoteKey != null,
                                )
                            }
                            prevKey
                        }

                        LoadType.APPEND -> {
                            val remoteKey = getRemoteKeyForLastItem(state)
                            val nextKey = remoteKey?.next_key?.toInt()
                            if (nextKey == null) {
                                return@withContext MediatorResult.Success(
                                    endOfPaginationReached = remoteKey != null,
                                )
                            }
                            nextKey
                        }
                    }
                val apiResult = getShowsByPageWithEither(page)
                when (apiResult) {
                    is Either.Left -> {
                        Logger.e("TvShowRemoteMediator") { "API call failed: ${apiResult.value}" }
                        return@withContext MediatorResult.Error(
                            Exception("Failed to fetch shows: ${apiResult.value}"),
                        )
                    }

                    is Either.Right -> {
                        val apiResponse = apiResult.value
                        val endOfPaginationReached = apiResponse.isEmpty()
                        val dbResult =
                            performDatabaseOperations(
                                loadType = loadType,
                                apiResponse = apiResponse,
                                page = page,
                                endOfPaginationReached = endOfPaginationReached,
                            )
                        when (dbResult) {
                            is Either.Left -> {
                                Logger.e("TvShowRemoteMediator") { "Database operation failed: ${dbResult.value}" }
                                return@withContext MediatorResult.Error(
                                    Exception("Database operation failed: ${dbResult.value}"),
                                )
                            }

                            is Either.Right -> {
                                return@withContext MediatorResult.Success(
                                    endOfPaginationReached = endOfPaginationReached,
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.e("TvShowRemoteMediator", e) { "Unexpected error in load(): ${e.message}" }
                MediatorResult.Error(e)
            }
        }

    private suspend fun getShowsByPageWithEither(page: Int): Either<ApiResponse, List<ShowRemoteResponse>> =
        try {
            val shows = tvManiakRemoteDataSource.getShowsByPage(page)
            Either.Right(shows)
        } catch (e: IOException) {
            Logger.e("TvShowRemoteMediator", e) { "IO Exception: ${e.message}" }
            Either.Left(ApiResponse.IOException)
        } catch (e: Exception) {
            Logger.e("TvShowRemoteMediator", e) { "HTTP Exception: ${e.message}" }
            Either.Left(ApiResponse.HttpError)
        }

    private fun performDatabaseOperations(
        loadType: LoadType,
        apiResponse: List<ShowRemoteResponse>,
        page: Int,
        endOfPaginationReached: Boolean,
    ): Either<ApiResponse, Unit> =
        try {
            tvManiakDatabase.instance?.transaction {
                // Clear tables on refresh
                if (loadType == LoadType.REFRESH) {
                    tvManiakQueries?.deleteAllRemoteKeys()
                    tvManiakQueries?.deleteAllShows()
                }

                // Calculate pagination keys
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                // Insert remote keys and shows
                apiResponse.forEach { showDto ->
                    tvManiakQueries?.insertRemoteKey(
                        show_id = showDto.id.toLong(),
                        prev_key = prevKey?.toLong(),
                        next_key = nextKey?.toLong(),
                    )

                    // Insert show
                    tvManiakQueries?.insertShow(
                        id = showDto.id.toLong(),
                        name = showDto.name,
                        language = showDto.language,
                        status = showDto.status,
                        type = showDto.type,
                        genres = showDto.genres.joinToString(","),
                        rating = showDto.rating?.average,
                        image_url = showDto.image?.medium,
                        large_image_url = showDto.image?.original,
                        summary = showDto.summary,
                        page = page.toLong(),
                        updated = showDto.updated,
                        score = 0.0,
                    )
                }

                // Update cache metadata
                if (loadType == LoadType.REFRESH) {
                    tvManiakQueries?.setCacheValid()
                }
            }
            Either.Right(Unit)
        } catch (e: Exception) {
            Logger.e("TvShowRemoteMediator", e) { "Database operation failed: ${e.message}" }
            Either.Left(ApiResponse.HttpError)
        }

    private fun getRemoteKeyForLastItem(state: PagingState<Int, GetPagedShows>): Remote_keys? =
        try {
            state.pages
                .lastOrNull { it.data.isNotEmpty() }
                ?.data
                ?.lastOrNull()
                ?.let { show ->
                    tvManiakQueries?.getRemoteKeyByShowId(show.id)?.executeAsOneOrNull()
                }
        } catch (e: Exception) {
            Logger.e("TvShowRemoteMediator", e) { "Failed to get remote key for last item: ${e.message}" }
            null
        }

    private fun getRemoteKeyForFirstItem(state: PagingState<Int, GetPagedShows>): Remote_keys? =
        try {
            state.pages
                .firstOrNull { it.data.isNotEmpty() }
                ?.data
                ?.firstOrNull()
                ?.let { show ->
                    tvManiakQueries?.getRemoteKeyByShowId(show.id)?.executeAsOneOrNull()
                }
        } catch (e: Exception) {
            Logger.e("TvShowRemoteMediator", e) { "Failed to get remote key for first item: ${e.message}" }
            null
        }

    private fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, GetPagedShows>): Remote_keys? =
        try {
            state.anchorPosition?.let { position ->
                state.closestItemToPosition(position)?.id?.let { showId ->
                    tvManiakQueries?.getRemoteKeyByShowId(showId)?.executeAsOneOrNull()
                }
            }
        } catch (e: Exception) {
            Logger.e("TvShowRemoteMediator", e) { "Failed to get remote key for current position: ${e.message}" }
            null
        }
}

package com.tizzone.tvmaniak.core.data.repository.tvshow

import app.cash.paging.PagingData
import com.tizzone.tvmaniak.core.common.utils.ApiResponse
import com.tizzone.tvmaniak.core.common.utils.DatabaseError
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.common.utils.fold
import com.tizzone.tvmaniak.core.model.ShowCastSummary
import com.tizzone.tvmaniak.core.model.TvShowDetail
import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Fake implementation of [TvShowRepository] for testing purposes.
 *
 * This repository simulates network delays and occasional errors for realistic testing scenarios.
 * It maintains an in-memory list of TV shows and a watchlist.
 *
 * The [random] property is used to introduce variability in delays and error occurrences.
 *
 * Key features:
 * - Provides a predefined list of fake TV shows.
 * - Simulates network delays for remote operations.
 * - Simulates occasional network and database errors.
 * - Manages an in-memory watchlist.
 * - Supports pagination for TV show listings.
 * - Supports local and remote search functionality with simulated relevance scoring.
 * - Allows insertion and updating of TV show summaries and details.
 */
class FakeTvShowRepositoryImpl : TvShowRepository {
    private val allFakeShows = mutableListOf<TvShowSummary>()
    private val watchlist = mutableSetOf<Int>()
    private val showsPerPage = 20

    @OptIn(ExperimentalTime::class)
    private val random = Random(Clock.System.now().toEpochMilliseconds())

    init {
        // Initialize with fake shows
        allFakeShows.addAll(getFakeShows())
    }

    fun getFakeShows(): List<TvShowSummary> =
        listOf(
            TvShowSummary(
                // Based on "Under the Dome"
                id = 1,
                name = "Under the Dome",
                summary =
                    "<p><b>Under the Dome</b> is the story of a small town that is suddenly and " +
                        "inexplicably sealed off from the rest of the world by an enormous transparent dome...</p>",
                type = "Scripted",
                language = "English",
                genres = listOf("Drama", "Science-Fiction", "Thriller"),
                status = "Ended",
                rating = 6.5f,
                imageUrl = "https://static.tvmaze.com/uploads/images/medium_portrait/81/202627.jpg",
                largeImageUrl = "https://static.tvmaze.com/uploads/images/original_untouched/81/202627.jpg",
                updated = 1704794122,
            ),
            TvShowSummary(
                // Based on "Person of Interest"
                id = 2,
                name = "Person of Interest",
                summary =
                    "<p>You are being watched. The government has a secret system, " +
                        "a machine that spies on you every hour of every day...</p>",
                type = "Scripted",
                language = "English",
                genres = listOf("Action", "Crime", "Science-Fiction"),
                status = "Ended",
                rating = 8.7f,
                imageUrl = "https://static.tvmaze.com/uploads/images/medium_portrait/163/407679.jpg",
                largeImageUrl = "https://static.tvmaze.com/uploads/images/original_untouched/163/407679.jpg",
                updated = 1743150150,
            ),
            TvShowSummary(
                // Based on "Bitten"
                id = 3,
                name = "Bitten",
                summary = "<p>Based on the critically acclaimed series of novels from Kelley Armstrong...</p>",
                type = "Scripted",
                language = "English",
                genres = listOf("Drama", "Horror", "Romance"),
                status = "Ended",
                rating = 7.4f,
                imageUrl = "https://static.tvmaze.com/uploads/images/medium_portrait/0/15.jpg",
                largeImageUrl = "https://static.tvmaze.com/uploads/images/original_untouched/0/15.jpg",
                updated = 1751862963,
            ),
        )

    override fun getTvShows(): Flow<PagingData<TvShowSummary>> =
        flow {
            // Initialize fake shows if empty
            if (allFakeShows.isEmpty()) {
                allFakeShows.addAll(getFakeShows())
            }
            // Emit PagingData with all fake shows
            emit(PagingData.from(allFakeShows))
        }

    override fun getShowDetails(showId: Int): Flow<Either<ApiResponse, TvShowDetail>> =
        flow {
            // Just combines local with watchlist status
            getShowById(showId).collect { localResult ->
                val show =
                    localResult.fold(
                        onLeft = { null },
                        onRight = { it },
                    )

                if (show != null) {
                    emit(Either.Right(show))
                } else {
                    emit(Either.Left(ApiResponse.HttpError))
                }
            }
        }

    override fun getShowDetailsRemote(showId: Int): Flow<Either<ApiResponse, TvShowDetail>> =
        flow {
            delay(150 + random.nextLong(0, 200))
            val showSummary = allFakeShows.find { it.id == showId }

            if (showSummary != null) {
                val detail =
                    TvShowDetail(
                        id = showSummary.id,
                        name = showSummary.name,
                        summary = showSummary.summary,
                        type = showSummary.type,
                        language = showSummary.language,
                        genres = showSummary.genres,
                        status = showSummary.status,
                        rating = showSummary.rating,
                        largeImageUrl = showSummary.imageUrl,
                        isInWatchlist = false, // Remote doesn't know about watchlist
                        updated = showSummary.updated,
                    )
                emit(Either.Right(detail))
            } else {
                emit(Either.Left(ApiResponse.HttpError))
            }
        }

    override fun searchTvShowsLocal(query: String): Flow<List<TvShowSummary>> =
        flow {
            delay(50 + random.nextLong(0, 100)) // Local search should be faster
            emit(searchTvShowsLocalSync(query))
        }

    private fun searchTvShowsLocalSync(query: String): List<TvShowSummary> {
        if (query.isBlank()) {
            return emptyList()
        }
        val lowerQuery = query.lowercase()
        val results =
            allFakeShows
                .filter {
                    it.name.lowercase().contains(lowerQuery) ||
                        (it.summary.lowercase().contains(lowerQuery)) ||
                        it.genres.any { genre -> genre.lowercase().contains(lowerQuery) }
                }.map { show ->
                    // Add a score based on relevance
                    val score =
                        when {
                            show.name.lowercase() == lowerQuery -> 100f
                            show.name.lowercase().startsWith(lowerQuery) -> 90f
                            show.name.lowercase().contains(lowerQuery) -> 80f
                            show.genres.any { it.lowercase() == lowerQuery } -> 70f
                            else -> 50f
                        }
                    show.copy(score = score)
                }.take(showsPerPage / 2) // Local might have fewer results
        return results
    }

    override fun searchTvShowsRemote(query: String): Flow<Either<ApiResponse, List<TvShowSummary>>> =
        flow {
            delay(250 + random.nextLong(0, 250)) // Remote search takes longer

            if (query.isBlank()) {
                emit(Either.Right(emptyList()))
                return@flow
            }

            // Simulate occasional network failures (reduced rate for tests)
            if (random.nextDouble() < 0.05) {
                emit(Either.Left(ApiResponse.HttpError))
                return@flow
            }

            val lowerQuery = query.lowercase()
            val results =
                allFakeShows
                    .filter {
                        it.name.lowercase().contains(lowerQuery) ||
                            (it.summary.lowercase().contains(lowerQuery)) ||
                            it.genres.any { genre -> genre.lowercase().contains(lowerQuery) }
                    }.map { show ->
                        // Add a score based on relevance (slightly different from local to simulate API scoring)
                        val score =
                            when {
                                show.name.lowercase() == lowerQuery -> 95f
                                show.name.lowercase().startsWith(lowerQuery) -> 85f
                                show.name.lowercase().contains(lowerQuery) -> 75f
                                show.genres.any { it.lowercase() == lowerQuery } -> 65f
                                else -> 45f
                            }
                        // Simulate remote results being slightly different (newer updates)
                        show.copy(
                            updated = show.updated + random.nextLong(1, 1000),
                            score = score,
                        )
                    }.take(showsPerPage)
            emit(Either.Right(results))
        }

    override suspend fun getShowCast(showId: Int): Either<ApiResponse, List<ShowCastSummary>> {
        TODO("Not yet implemented")
    }

    override suspend fun addTvShowToWatchList(showId: Int): Either<DatabaseError, Unit> {
        delay(50 + random.nextLong(0, 100))

        // Simulate occasional database failures
        if (random.nextDouble() < 0.02) {
            return Either.Left(DatabaseError.OperationFailed)
        }

        watchlist.add(showId)
        return Either.Right(Unit)
    }

    override suspend fun removeTvShowFromWatchList(showId: Int): Either<DatabaseError, Unit> {
        delay(50 + random.nextLong(0, 100))

        // Simulate occasional database failures
        if (random.nextDouble() < 0.02) {
            return Either.Left(DatabaseError.OperationFailed)
        }

        watchlist.remove(showId)
        return Either.Right(Unit)
    }

    override fun isTvShowInWatchList(showId: Int): Flow<Either<DatabaseError, Boolean>> =
        flow {
            delay(30 + random.nextLong(0, 50))

            // Simulate occasional database failures
            if (random.nextDouble() < 0.01) {
                emit(Either.Left(DatabaseError.OperationFailed))
            } else {
                emit(Either.Right(watchlist.contains(showId)))
            }
        }

    override fun getShowById(showId: Int): Flow<Either<DatabaseError, TvShowDetail?>> =
        flow {
            delay(30 + random.nextLong(0, 50))

            if (random.nextDouble() < 0.01) {
                emit(Either.Left(DatabaseError.OperationFailed))
                return@flow
            }

            val summaryShow =
                allFakeShows.find { it.id == showId }?.copy(
                    isInWatchList = watchlist.contains(showId),
                )

            val detailShow =
                summaryShow?.let { summary ->
                    TvShowDetail(
                        id = summary.id,
                        name = summary.name,
                        type = summary.type,
                        language = summary.language,
                        genres = summary.genres,
                        status = summary.status,
                        rating = summary.rating,
                        largeImageUrl = summary.imageUrl,
                        summary = summary.summary,
                        updated = summary.updated,
                        isInWatchlist = summary.isInWatchList,
                    )
                }

            emit(Either.Right(detailShow))
        }

    override fun getWatchlistTvShows(): Flow<Either<DatabaseError, List<TvShowSummary>>> =
        flow {
            try {
                val watchlistShows =
                    allFakeShows
                        .filter { show ->
                            watchlist.contains(show.id)
                        }.map { show ->
                            show.copy(isInWatchList = true)
                        }
                emit(Either.Right(watchlistShows))
            } catch (e: Exception) {
                emit(Either.Left(DatabaseError.OperationFailed))
            }
        }

    override suspend fun insertOrUpdateShows(shows: List<TvShowSummary>): Either<DatabaseError, Unit> {
        delay(50 + random.nextLong(0, 100))

        // Simulate occasional database failures
        if (random.nextDouble() < 0.02) {
            return Either.Left(DatabaseError.OperationFailed)
        }

        // Update the existing shows or add new ones
        shows.forEach { newShow ->
            val existingIndex = allFakeShows.indexOfFirst { it.id == newShow.id }
            if (existingIndex != -1) {
                allFakeShows[existingIndex] = newShow
            } else {
                allFakeShows.add(newShow)
            }
        }

        return Either.Right(Unit)
    }

    override suspend fun insertOrUpdateShowDetail(show: TvShowDetail): Either<DatabaseError, Unit> {
        delay(50 + random.nextLong(0, 100))

        // Simulate occasional database failures
        if (random.nextDouble() < 0.02) {
            return Either.Left(DatabaseError.OperationFailed)
        }

        // Convert TvShowDetail to TvShowSummary and update/add
        val summary =
            TvShowSummary(
                id = show.id,
                name = show.name,
                summary = show.summary,
                type = show.type,
                language = show.language,
                genres = show.genres,
                status = show.status,
                rating = show.rating,
                imageUrl = show.largeImageUrl,
                updated = show.updated,
                isInWatchList = show.isInWatchlist,
                score = show.score,
                largeImageUrl = show.largeImageUrl,
            )

        val existingIndex = allFakeShows.indexOfFirst { it.id == show.id }
        if (existingIndex != -1) {
            allFakeShows[existingIndex] = summary
        } else {
            allFakeShows.add(summary)
        }

        return Either.Right(Unit)
    }
}

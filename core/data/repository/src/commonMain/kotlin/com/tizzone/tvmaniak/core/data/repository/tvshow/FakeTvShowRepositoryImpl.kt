package com.tizzone.tvmaniak.core.data.repository.tvshow

import app.cash.paging.PagingData
import com.tizzone.tvmaniak.core.common.utils.ApiResponse
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.model.ShowCastSummary
import com.tizzone.tvmaniak.core.model.TvShowDetail
import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FakeTvShowRepositoryImpl : TvShowRepository {
    private val allFakeShows = mutableListOf<TvShowSummary>()
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
                smallImageUrl = "https://static.tvmaze.com/uploads/images/medium_portrait/81/202627.jpg",
                updated = 1704794122
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
                smallImageUrl = "https://static.tvmaze.com/uploads/images/medium_portrait/163/407679.jpg",
                updated = 1743150150
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
                smallImageUrl = "https://static.tvmaze.com/uploads/images/medium_portrait/0/15.jpg",
                updated = 1751862963
            ),
        )

    override fun getTvShows(): Flow<PagingData<TvShowSummary>> {
        return kotlinx.coroutines.flow.flow {
            // Initialize fake shows if empty
            if (allFakeShows.isEmpty()) {
                allFakeShows.addAll(getFakeShows())
            }

            // Emit PagingData with all fake shows
            emit(PagingData.from(allFakeShows))
        }
    }

    override suspend fun getShowDetails(showId: Int): Either<ApiResponse, TvShowDetail> {
        delay(150 + random.nextLong(0, 200))
        val showSummary = allFakeShows.find { it.id == showId }

        return if (showSummary != null) {
            val detail =
                TvShowDetail(
                    // Replace with your actual TvShowDetail constructor and fields
                    id = showSummary.id,
                    name = showSummary.name,
                    summary = showSummary.summary,
                    type = showSummary.type,
                    language = showSummary.language,
                    genres = showSummary.genres,
                    status = showSummary.status,
                    rating = showSummary.rating,
                    largeImageUrl = showSummary.smallImageUrl,
                )
            Either.Right(detail)
        } else {
            Either.Left(ApiResponse.HttpError)
        }
    }

    override suspend fun searchTvShowsLocal(query: String): List<TvShowSummary> {
        delay(50 + random.nextLong(0, 100)) // Local search should be faster
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
                    val score = when {
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

    override suspend fun searchTvShowsRemote(query: String): Either<ApiResponse, List<TvShowSummary>> {
        delay(250 + random.nextLong(0, 250)) // Remote search takes longer
        if (query.isBlank()) {
            return Either.Right(emptyList())
        }

        // Simulate occasional network failures (reduced rate for tests)
        if (random.nextDouble() < 0.05) {
            return Either.Left(ApiResponse.HttpError)
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
                    val score = when {
                        show.name.lowercase() == lowerQuery -> 95f
                        show.name.lowercase().startsWith(lowerQuery) -> 85f
                        show.name.lowercase().contains(lowerQuery) -> 75f
                        show.genres.any { it.lowercase() == lowerQuery } -> 65f
                        else -> 45f
                    }
                    // Simulate remote results being slightly different (newer updates)
                    show.copy(
                        updated = show.updated + random.nextLong(1, 1000),
                        score = score
                    )
                }.take(showsPerPage)
        return Either.Right(results)
    }

    override suspend fun getShowCast(showId: Int): Either<ApiResponse, List<ShowCastSummary>> {
        TODO("Not yet implemented")
    }

}

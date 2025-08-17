package com.tizzone.tvmaniak.core.domain.tvshows

import com.tizzone.tvmaniak.core.common.utils.ApiResponse
import com.tizzone.tvmaniak.core.common.utils.Either
import com.tizzone.tvmaniak.core.data.repository.tvshow.TvShowRepository
import com.tizzone.tvmaniak.core.model.ShowCastSummary
import com.tizzone.tvmaniak.core.model.TvShowDetail
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class TvShowDetailsWithCast(
    val details: TvShowDetail,
    val cast: List<ShowCastSummary>,
)

class GetTvShowDetailsUseCase(
    private val tvShowRepository: TvShowRepository,
) {
    suspend operator fun invoke(showId: Int): Either<ApiResponse, TvShowDetailsWithCast> =
        coroutineScope {
            val detailsDeferred = async { tvShowRepository.getShowDetails(showId) }
            val castDeferred = async { tvShowRepository.getShowCast(showId) }

            val detailsResult = detailsDeferred.await()
            val castResult = castDeferred.await()

            when {
                detailsResult is Either.Left -> {
                    when (detailsResult.value) {
                        is ApiResponse.HttpError -> Either.Left(ApiResponse.HttpError)
                        is ApiResponse.IOException -> Either.Left(ApiResponse.IOException)
                    }
                }

                castResult is Either.Left -> {
                    Either.Right(
                        TvShowDetailsWithCast(
                            details = (detailsResult as Either.Right).value,
                            cast = emptyList(),
                        ),
                    )
                }

                else -> {
                    Either.Right(
                        TvShowDetailsWithCast(
                            details = (detailsResult as Either.Right).value,
                            cast = (castResult as Either.Right).value,
                        ),
                    )
                }
            }
        }
}

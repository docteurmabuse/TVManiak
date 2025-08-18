package com.tizzone.tvmaniak.core.model

/**
 * Data class representing the details of a TV show along with its cast information.
 *
 * @property details The [TvShowDetail] containing the core information about the TV show.
 * @property cast A list of [ShowCastSummary] objects, each representing a cast member of the show.
 */
data class TvShowDetailsWithCast(
    val details: TvShowDetail,
    val cast: List<ShowCastSummary>,
)

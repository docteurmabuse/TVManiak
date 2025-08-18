package com.tizzone.tvmaniak.core.model

/**
 * Represents detailed information about a TV show.
 *
 * @property id The unique identifier of the TV show.
 * @property name The name of the TV show.
 * @property type The type of the TV show (e.g., Scripted, Animation).
 * @property language The primary language of the TV show.
 * @property genres A list of genres associated with the TV show.
 * @property status The current status of the TV show (e.g., Running, Ended).
 * @property rating The average rating of the TV show (nullable).
 * @property largeImageUrl The URL for a large image/poster of the TV show.
 * @property summary A brief summary or description of the TV show.
 * @property isInWatchlist Indicates whether the TV show is in the user's watchlist. Defaults to false.
 * @property score A score associated with the TV show (nullable).
 * @property updated A timestamp indicating when the TV show information was last updated.
 */
data class TvShowDetail(
    val id: Int,
    val name: String,
    val type: String,
    val language: String,
    val genres: List<String>,
    val status: String,
    val rating: Float? = null,
    val smallImageUrl: String,
    val largeImageUrl: String,
    val summary: String,
    val isInWatchlist: Boolean = false,
    val score: Float? = null,
    val updated: Long,
)

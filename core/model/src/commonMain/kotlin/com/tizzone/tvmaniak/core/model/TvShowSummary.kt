package com.tizzone.tvmaniak.core.model

/**
 * Data class representing a summary of a TV show.
 *
 * @property id The unique identifier of the TV show.
 * @property name The name of the TV show.
 * @property summary A brief summary or description of the TV show.
 * @property type The type of the TV show (e.g., Scripted, Animation).
 * @property language The primary language of the TV show.
 * @property genres A list of genres associated with the TV show.
 * @property status The current status of the TV show (e.g., Running, Ended).
 * @property rating The average rating of the TV show, if available.
 * @property imageUrl The URL for the medium-sized image/poster of the TV show.
 * @property largeImageUrl The URL for the large-sized image/poster of the TV show.
 * @property updated A timestamp indicating when the TV show information was last updated.
 * @property score A score associated with the TV show, potentially for ranking or relevance. Defaults to 0f.
 * @property isInWatchList A boolean indicating whether the TV show is currently in the user's watchlist. Defaults to false.
 */
data class TvShowSummary(
    val id: Int,
    val name: String,
    val summary: String,
    val type: String,
    val language: String,
    val genres: List<String>,
    val status: String,
    val rating: Float? = null,
    val imageUrl: String,
    val largeImageUrl: String,
    val updated: Long,
    val score: Float? = 0f,
    val isInWatchList: Boolean = false,
)

enum class Genre(
    val displayName: String,
) {
    ACTION("Action"),
    ADULT("Adult"),
    ADVENTURE("Adventure"),
    ANIME("Anime"),
    CHILDREN("Children"),
    COMEDY("Comedy"),
    CRIME("Crime"),
    DIY("DIY"),
    DRAMA("Drama"),
    ESPIONAGE("Espionage"),
    FAMILY("Family"),
    FANTASY("Fantasy"),
    FOOD("Food"),
    HISTORY("History"),
    HORROR("Horror"),
    LEGAL("Legal"),
    MEDICAL("Medical"),
    MUSIC("Music"),
    MYSTERY("Mystery"),
    NATURE("Nature"),
    ROMANCE("Romance"),
    SCIENCE_FICTION("ScienceFiction"),
    SPORTS("Sports"),
    SUPERNATURAL("Supernatural"),
    THRILLER("Thriller"),
    TRAVEL("Travel"),
    WAR("War"),
    WESTERN("Western"),
}

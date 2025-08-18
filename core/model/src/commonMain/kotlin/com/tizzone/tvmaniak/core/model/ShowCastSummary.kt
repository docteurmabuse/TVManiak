package com.tizzone.tvmaniak.core.model

/**
 * Data class representing a summary of a show's cast member.
 *
 * @property id The unique identifier of the cast member.
 * @property name The name of the cast member.
 * @property smallImageUrl The URL of a small image representing the cast member.
 */
data class ShowCastSummary(
    val id: Int,
    val name: String,
    val smallImageUrl: String,
)

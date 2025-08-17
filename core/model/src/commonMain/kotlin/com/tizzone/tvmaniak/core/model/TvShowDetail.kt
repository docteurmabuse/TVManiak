package com.tizzone.tvmaniak.core.model

data class TvShowDetail(
    val id: Int,
    val name: String,
    val type: String,
    val language: String,
    val genres: List<String>,
    val status: String,
    val rating: Float? = null,
    val largeImageUrl: String,
    val summary: String,
)

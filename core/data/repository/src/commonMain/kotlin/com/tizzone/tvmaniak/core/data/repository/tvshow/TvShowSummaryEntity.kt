package com.tizzone.tvmaniak.core.data.repository.tvshow

import com.tizzone.tvmaniak.core.data.local.GetPagedShows
import com.tizzone.tvmaniak.core.data.local.GetShowById
import com.tizzone.tvmaniak.core.data.local.GetWatchlist
import com.tizzone.tvmaniak.core.data.local.SearchShowByName
import com.tizzone.tvmaniak.core.model.TvShowDetail
import com.tizzone.tvmaniak.core.model.TvShowSummary

interface TvShowSummaryEntity {
    val id: Long
    val name: String
    val summary: String?
    val type: String?
    val language: String?
    val genres: String?
    val status: String?
    val rating: Double?
    val imageUrl: String?
    val largeImageUrl: String?
    val updated: Long
    val score: Double?
    val isInWatchlist: Long
}

fun TvShowSummaryEntity.toDomainModel(): TvShowSummary =
    TvShowSummary(
        id = this.id.toInt(),
        name = this.name,
        summary = this.summary ?: "",
        type = this.type ?: "",
        language = this.language ?: "",
        genres = this.genres?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        status = this.status ?: "",
        rating = this.rating?.toFloat(),
        imageUrl = this.imageUrl ?: "",
        largeImageUrl = this.largeImageUrl ?: "",
        updated = this.updated,
        score = this.score?.toFloat(),
        isInWatchList = this.isInWatchlist == 1L,
    )

private fun GetPagedShows.asEntity(): TvShowSummaryEntity =
    object : TvShowSummaryEntity {
        override val id: Long = this@asEntity.id
        override val name: String = this@asEntity.name
        override val summary: String? = this@asEntity.summary
        override val type: String? = this@asEntity.type
        override val language: String? = this@asEntity.language
        override val genres: String? = this@asEntity.genres
        override val status: String? = this@asEntity.status
        override val rating: Double? = this@asEntity.rating
        override val imageUrl: String? = this@asEntity.image_url
        override val largeImageUrl: String? = this@asEntity.large_image_url
        override val updated: Long = this@asEntity.updated
        override val score: Double? = this@asEntity.score
        override val isInWatchlist: Long = this@asEntity.is_in_watchlist
    }

private fun SearchShowByName.asEntity(): TvShowSummaryEntity =
    object : TvShowSummaryEntity {
        override val id: Long = this@asEntity.id
        override val name: String = this@asEntity.name
        override val summary: String? = this@asEntity.summary
        override val type: String? = this@asEntity.type
        override val language: String? = this@asEntity.language
        override val genres: String? = this@asEntity.genres
        override val status: String? = this@asEntity.status
        override val rating: Double? = this@asEntity.rating
        override val imageUrl: String? = this@asEntity.image_url
        override val largeImageUrl: String? = this@asEntity.large_image_url
        override val updated: Long = this@asEntity.updated
        override val score: Double? = this@asEntity.score
        override val isInWatchlist: Long = this@asEntity.is_in_watchlist
    }

private fun GetWatchlist.asEntity(): TvShowSummaryEntity =
    object : TvShowSummaryEntity {
        override val id: Long = this@asEntity.id
        override val name: String = this@asEntity.name
        override val summary: String? = this@asEntity.summary
        override val type: String? = this@asEntity.type
        override val language: String? = this@asEntity.language
        override val genres: String? = this@asEntity.genres
        override val status: String? = this@asEntity.status
        override val rating: Double? = this@asEntity.rating
        override val imageUrl: String? = this@asEntity.image_url
        override val largeImageUrl: String? = this@asEntity.large_image_url
        override val updated: Long = this@asEntity.updated
        override val score: Double? = this@asEntity.score
        override val isInWatchlist: Long = this@asEntity.is_in_watchlist
    }

fun GetPagedShows.toDomainModel(): TvShowSummary = this.asEntity().toDomainModel()

fun SearchShowByName.toDomainModel(): TvShowSummary = this.asEntity().toDomainModel()

fun GetWatchlist.toDomainModel(): TvShowSummary = this.asEntity().toDomainModel()

fun GetShowById.toDomainModel(): TvShowDetail =
    TvShowDetail(
        id = this.id.toInt(),
        name = this.name,
        summary = this.summary ?: "",
        type = this.type ?: "",
        language = this.language ?: "",
        genres = this.genres?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        status = this.status ?: "",
        rating = this.rating?.toFloat(),
        largeImageUrl = this.large_image_url ?: this.image_url ?: "",
        smallImageUrl = this.image_url ?: "",
        updated = this.updated,
        score = this.score?.toFloat(),
        isInWatchlist = this.is_in_watchlist == 1L,
    )

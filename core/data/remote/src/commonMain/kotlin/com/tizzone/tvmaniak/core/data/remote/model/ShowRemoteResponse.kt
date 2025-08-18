package com.tizzone.tvmaniak.core.data.remote.model

import com.tizzone.tvmaniak.core.model.TvShowDetail
import com.tizzone.tvmaniak.core.model.TvShowSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowRemoteResponse(
    val id: Int,
    val url: String,
    val name: String,
    val type: String,
    val language: String? = null,
    val genres: List<String> = emptyList(),
    val status: String,
    val runtime: Int? = null,
    val premiered: String? = null,
    val ended: String? = null,
    val officialSite: String? = null,
    val schedule: Schedule? = null,
    val rating: Rating? = null,
    val weight: Int,
    val network: Network? = null,
    val webChannel: WebChannel? = null,
    val externals: Externals,
    val image: Image? = null,
    val summary: String? = null,
    val updated: Long,
    @SerialName("_links") val links: Links,
    @SerialName("_embedded") val embedded: EmbeddedData? = null,
)

fun ShowRemoteResponse.toShowSummary(searchScore: Float?): TvShowSummary =
    TvShowSummary(
        id = id,
        name = name,
        summary = summary ?: "",
        type = type,
        language = language ?: "",
        genres = genres,
        status = status,
        rating = rating?.average?.toFloat() ?: 0f,
        imageUrl = image?.medium ?: "",
        largeImageUrl = image?.original ?: "",
        updated = updated,
        score = searchScore ?: 0f,
    )

fun ShowRemoteResponse.toShowDetail(): TvShowDetail =
    TvShowDetail(
        id = id,
        name = name,
        type = type,
        language = language ?: "",
        genres = genres,
        status = status,
        rating = rating?.average?.toFloat() ?: 0f,
        largeImageUrl = image?.original ?: "",
        summary = summary ?: "",
        updated = updated,
    )

@Serializable
data class Schedule(
    val time: String,
    val days: List<String> = emptyList(),
)

@Serializable
data class Rating(
    val average: Double? = null,
)

@Serializable
data class Network(
    val id: Int,
    val name: String,
    val country: Country? = null,
)

@Serializable
data class WebChannel(
    val id: Int,
    val name: String,
    val country: Country? = null,
)

@Serializable
data class Country(
    val name: String,
    val code: String,
    val timezone: String,
)

@Serializable
data class Externals(
    val tvrage: Int? = null,
    val thetvdb: Int? = null,
    val imdb: String? = null,
)

@Serializable
data class Image(
    val medium: String,
    val original: String,
)

@Serializable
data class Links(
    val self: Link,
    val previousepisode: Link? = null,
    val nextepisode: Link? = null,
)

@Serializable
data class Link(
    val href: String,
)

@Serializable
data class EmbeddedData(
    val episodes: List<Episode>? = null,
    val cast: List<CastMember>? = null,
    val crew: List<CrewMember>? = null,
)

@Serializable
data class Episode(
    val id: Int,
    val url: String? = null,
    val name: String,
    val season: Int,
    val number: Int? = null,
    val airdate: String? = null,
    val airtime: String? = null,
    val airstamp: String? = null,
    val runtime: Int? = null,
    val summary: String? = null,
    val image: Image? = null,
    @SerialName("_links") val links: Links,
)

@Serializable
data class CastMember(
    val person: Person,
    val character: Character,
    val self: Boolean = false,
    val voice: Boolean = false,
)

@Serializable
data class Person(
    val id: Int,
    val url: String? = null,
    val name: String,
    val country: Country? = null,
    val birthday: String? = null,
    val deathday: String? = null,
    val gender: String? = null,
    val image: Image? = null,
)

@Serializable
data class Character(
    val id: Int,
    val url: String? = null,
    val name: String,
    val image: Image? = null,
)

@Serializable
data class CrewMember(
    val type: String,
    val person: Person,
)

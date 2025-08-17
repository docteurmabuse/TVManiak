package com.tizzone.tvmaniak.core.data.remote.model

import com.tizzone.tvmaniak.core.model.ShowCastSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CastRemoteResponse(
    @SerialName("person")
    val person: PersonRemote,
    @SerialName("character")
    val character: CharacterRemote,
    @SerialName("self")
    val isSelf: Boolean,
    @SerialName("voice")
    val isVoice: Boolean,
)

@Serializable
data class PersonRemote(
    @SerialName("id")
    val id: Int,
    @SerialName("url")
    val url: String,
    @SerialName("name")
    val name: String,
    @SerialName("country")
    val country: CountryRemote? = null,
    @SerialName("birthday")
    val birthday: String? = null,
    @SerialName("deathday")
    val deathday: String? = null,
    @SerialName("gender")
    val gender: String? = null,
    @SerialName("image")
    val image: ImageRemote? = null,
    @SerialName("updated")
    val updated: Long? = null,
    @SerialName("_links")
    val links: PersonLinksRemote? = null,
)

@Serializable
data class CharacterRemote(
    @SerialName("id")
    val id: Int,
    @SerialName("url")
    val url: String,
    @SerialName("name")
    val name: String,
    @SerialName("image")
    val image: ImageRemote? = null,
    @SerialName("_links")
    val links: CharacterLinksRemote? = null,
)

@Serializable
data class CountryRemote(
    @SerialName("name")
    val name: String,
    @SerialName("code")
    val code: String,
    @SerialName("timezone")
    val timezone: String,
)

@Serializable
data class ImageRemote(
    @SerialName("medium")
    val medium: String? = null,
    @SerialName("original")
    val original: String? = null,
)

@Serializable
data class PersonLinksRemote(
    @SerialName("self")
    val self: LinkRemote,
)

@Serializable
data class CharacterLinksRemote(
    @SerialName("self")
    val self: LinkRemote,
)

@Serializable
data class LinkRemote(
    @SerialName("href")
    val href: String,
)

fun CastRemoteResponse.toShowCastSummary(): ShowCastSummary =
    ShowCastSummary(
        id = person.id,
        name = person.name,
        smallImageUrl = person.image?.medium ?: "",
    )

package com.tizzone.tvmaniak.core.data.remote.di

import com.tizzone.tvmaniak.core.data.remote.TvManiakRemoteDataSource
import com.tizzone.tvmaniak.core.data.remote.ktor.KtorTvManiakRemote
import com.tizzone.tvmaniak.core.data.remote.utils.TV_MAZE_API_HOST
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val remoteModule =
    module {
        single<TvManiakRemoteDataSource> {
            KtorTvManiakRemote(tvManiakClient = get())
        }
        single {
            Json {
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
                prettyPrint = true
            }
        }
        single {
            HttpClient {
                install(ContentNegotiation) {
                    json(get())
                }
                install(HttpCache)
                install(Logging) {
                    level = LogLevel.ALL
                }
                install(DefaultRequest) {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = TV_MAZE_API_HOST
                    }
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                }
            }
        }
    }

package com.tizzone.tvmaniak

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

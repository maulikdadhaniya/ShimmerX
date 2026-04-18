package com.maulik.shimmerx

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
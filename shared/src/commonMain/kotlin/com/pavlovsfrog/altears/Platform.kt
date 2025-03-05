package com.pavlovsfrog.altears

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
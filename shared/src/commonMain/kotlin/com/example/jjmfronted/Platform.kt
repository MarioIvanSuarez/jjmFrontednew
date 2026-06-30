package com.example.jjmfronted

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
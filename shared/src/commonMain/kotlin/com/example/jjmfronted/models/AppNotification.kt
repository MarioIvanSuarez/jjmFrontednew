package com.example.jjmfronted.models

import kotlinx.serialization.Serializable

@Serializable
data class AppNotification(
    val id: Int,
    val title: String,
    val message: String,
    val type: String = "SYSTEM",
    val createdAt: String,
    val isRead: Boolean = false,
    val relatedId: Int? = null,
    val userId: Int = 0
)

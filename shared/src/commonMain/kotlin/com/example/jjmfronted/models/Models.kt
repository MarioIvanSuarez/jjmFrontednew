package com.example.jjmfronted.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class AuthResponse(
    val token: String? = null,
    val user: User
)

@Serializable
data class User(
    val id: Int,
    val email: String,
    val name: String
)

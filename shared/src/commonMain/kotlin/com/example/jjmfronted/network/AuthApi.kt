package com.example.jjmfronted.network

import com.example.jjmfronted.models.AuthResponse
import com.example.jjmfronted.models.LoginRequest
import com.example.jjmfronted.models.RegisterRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

object AuthApi {

    private const val BASE_URL = "https://jjmbackend-production.up.railway.app"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return runCatching {
            val response = client.post("$BASE_URL/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }
            response.body<AuthResponse>()
        }
    }

    suspend fun register(email: String, password: String, name: String): Result<AuthResponse> {
        return runCatching {
            val response = client.post("$BASE_URL/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(email, password, name))
            }
            response.body<AuthResponse>()
        }
    }

    suspend fun health(): Result<Map<String, String>> {
        return runCatching {
            val response = client.get("$BASE_URL/health")
            response.body<Map<String, String>>()
        }
    }
}

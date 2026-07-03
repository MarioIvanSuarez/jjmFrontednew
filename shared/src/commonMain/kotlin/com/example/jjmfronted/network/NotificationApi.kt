package com.example.jjmfronted.network

import com.example.jjmfronted.models.AppNotification
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CreateNotificationRequest(
    val userId: Int,
    val title: String,
    val message: String,
    val type: String = "SYSTEM",
    val relatedId: Int? = null
)

object NotificationApi {

    private const val BASE_URL = "https://jjmbackend-production.up.railway.app"

    private var authToken: String? = null

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(jsonConfig)
        }
    }

    fun setToken(token: String?) {
        authToken = token
    }

    private fun HttpRequestBuilder.withAuth() {
        authToken?.let {
            header(HttpHeaders.Authorization, "Bearer $it")
        }
    }

    suspend fun getNotifications(): Result<List<AppNotification>> {
        return runCatching {
            client.get("$BASE_URL/notifications") {
                withAuth()
            }.body<List<AppNotification>>()
        }
    }

    suspend fun getUnreadCount(): Result<Int> {
        return runCatching {
            val response = client.get("$BASE_URL/notifications/unread-count") {
                withAuth()
            }
            val body = response.body<Map<String, Int>>()
            body["count"] ?: 0
        }
    }

    suspend fun markAsRead(notificationId: Int): Result<Unit> {
        return runCatching {
            client.put("$BASE_URL/notifications/$notificationId/read") {
                withAuth()
            }
        }
    }

    suspend fun markAllAsRead(): Result<Unit> {
        return runCatching {
            client.put("$BASE_URL/notifications/read-all") {
                withAuth()
            }
        }
    }

    suspend fun createNotification(request: CreateNotificationRequest): Result<AppNotification> {
        return runCatching {
            val response = client.post("$BASE_URL/notifications") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.body<AppNotification>()
        }
    }
}

package com.example.jjmfronted.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── Auth ────────────────────────────────────────────────────────────────────

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: String
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
    val name: String,
    val role: String? = null
)

enum class UserRole(val apiValue: String) {
    STUDENT("ESTUDIANTE"),
    COMPANY("EMPRESA"),
    ADMIN("ADMINISTRADOR");

    companion object {
        fun fromApi(value: String): UserRole = when (value) {
            "ESTUDIANTE" -> STUDENT
            "EMPRESA" -> COMPANY
            "ADMINISTRADOR" -> ADMIN
            else -> STUDENT
        }
        fun fromName(name: String): UserRole = when (name.uppercase()) {
            "STUDENT", "ESTUDIANTE" -> STUDENT
            "COMPANY", "EMPRESA" -> COMPANY
            "ADMIN", "ADMINISTRADOR" -> ADMIN
            else -> STUDENT
        }
    }
}

// ─── Vacantes ────────────────────────────────────────────────────────────────

@Serializable
data class Vacante(
    val id: Int,
    val companyId: Int,
    val companyName: String,
    val title: String,
    val description: String,
    val requirements: String? = null,
    val slots: Int,
    val area: String? = null,
    val duration: String? = null,
    val schedule: String? = null,
    val location: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Serializable
data class CreateVacanteRequest(
    val title: String,
    val description: String,
    val requirements: String? = null,
    val slots: Int,
    val area: String? = null,
    val duration: String? = null,
    val schedule: String? = null,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Serializable
data class UpdateVacanteRequest(
    val title: String? = null,
    val description: String? = null,
    val requirements: String? = null,
    val slots: Int? = null,
    val area: String? = null,
    val duration: String? = null,
    val schedule: String? = null,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

// ─── Postulaciones ───────────────────────────────────────────────────────────

@Serializable
data class Postulacion(
    val id: Int,
    val studentId: Int,
    val studentName: String = "",
    val studentEmail: String? = null,
    val vacanteId: Int,
    val vacanteTitle: String? = null,
    val status: String,
    val message: String? = null,
    val createdAt: String? = null
)

@Serializable
data class CreatePostulacionRequest(
    val vacanteId: Int,
    val message: String? = null
)

@Serializable
data class UpdatePostulacionStatusRequest(
    val status: String
)

// ─── Documents ───────────────────────────────────────────────────────────────

@Serializable
data class Document(
    val id: Int,
    val name: String,
    val type: String? = null,
    val fileUrl: String? = null,
    val uploadedBy: String? = null,
    val createdAt: String? = null,
    val studentName: String? = null
)

@Serializable
data class CreateDocumentRequest(
    val name: String,
    val type: String? = null,
    val fileUrl: String? = null,
    val studentId: Int? = null,
    val vacanteId: Int? = null
)

// ─── Attendance ──────────────────────────────────────────────────────────────

@Serializable
data class AttendanceRecord(
    val id: Int? = null,
    val studentId: Int,
    val studentName: String? = null,
    val date: String,
    val status: String,
    val notes: String? = null
)

// ─── Company ─────────────────────────────────────────────────────────────────

@Serializable
data class CompanyMapItem(
    val id: Int,
    @SerialName("companyName") val name: String? = null,
    val industry: String? = null,
    val address: String? = null,
    val location: String? = null,
    val description: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Serializable
data class CompanyProfile(
    val id: Int? = null,
    @SerialName("companyName") val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val website: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Serializable
data class UpdateCompanyProfileRequest(
    val description: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val website: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

// ─── Chat ────────────────────────────────────────────────────────────────────

@Serializable
data class ChatMessage(
    val id: Int? = null,
    val senderId: Int,
    val senderName: String? = null,
    val receiverId: Int,
    val receiverName: String? = null,
    val message: String,
    val timestamp: String? = null,
    val vacanteId: Int? = null
)

@Serializable
data class SendMessageRequest(
    val receiverId: Int,
    val message: String,
    val vacanteId: Int? = null
)

@Serializable
data class Conversation(
    val userId: Int,
    val userName: String? = null,
    val lastMessage: String? = null,
    val lastTimestamp: String? = null
)

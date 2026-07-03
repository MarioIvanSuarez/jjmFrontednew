package com.example.jjmfronted.network

import com.example.jjmfronted.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private val jsonConfig = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

object ApiClient {

    private const val BASE_URL = "https://jjmbackend-production.up.railway.app"

    private var authToken: String? = null

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(jsonConfig)
        }
    }

    fun setToken(token: String?) {
        authToken = token
    }

    fun getToken(): String? = authToken

    private fun HttpRequestBuilder.withAuth() {
        authToken?.let {
            header(HttpHeaders.Authorization, "Bearer $it")
        }
    }

    // ─── Auth ────────────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return runCatching {
            client.post("$BASE_URL/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }.body<AuthResponse>()
        }
    }

    suspend fun register(email: String, password: String, name: String, role: String): Result<AuthResponse> {
        return runCatching {
            client.post("$BASE_URL/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(email, password, name, role))
            }.body<AuthResponse>()
        }
    }

    suspend fun getMe(): Result<User> {
        return runCatching {
            client.get("$BASE_URL/auth/me") {
                withAuth()
            }.body<User>()
        }
    }

    // ─── Companies ───────────────────────────────────────────────────────────

    suspend fun getCompaniesMap(): Result<List<CompanyMapItem>> {
        return runCatching {
            client.get("$BASE_URL/companies/map").body<List<CompanyMapItem>>()
        }
    }

    suspend fun getMyCompanyProfile(): Result<CompanyProfile> {
        return runCatching {
            client.get("$BASE_URL/companies/profile") {
                withAuth()
            }.body<CompanyProfile>()
        }
    }

    suspend fun updateCompanyProfile(request: UpdateCompanyProfileRequest): Result<CompanyProfile> {
        return runCatching {
            client.put("$BASE_URL/companies/profile") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<CompanyProfile>()
        }
    }

    // ─── Vacantes ────────────────────────────────────────────────────────────

    suspend fun getVacantes(area: String? = null, location: String? = null): Result<List<Vacante>> {
        return runCatching {
            client.get("$BASE_URL/vacantes") {
                area?.let { parameter("area", it) }
                location?.let { parameter("location", it) }
            }.body<List<Vacante>>()
        }
    }

    suspend fun getVacanteDetail(id: Int): Result<Vacante> {
        return runCatching {
            client.get("$BASE_URL/vacantes/$id").body<Vacante>()
        }
    }

    suspend fun getMyVacantes(): Result<List<Vacante>> {
        return runCatching {
            client.get("$BASE_URL/vacantes/my") {
                withAuth()
            }.body<List<Vacante>>()
        }
    }

    suspend fun createVacante(request: CreateVacanteRequest): Result<Vacante> {
        return runCatching {
            client.post("$BASE_URL/vacantes") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<Vacante>()
        }
    }

    suspend fun updateVacante(id: Int, request: UpdateVacanteRequest): Result<Vacante> {
        return runCatching {
            client.put("$BASE_URL/vacantes/$id") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<Vacante>()
        }
    }

    suspend fun deleteVacante(id: Int): Result<Unit> {
        return runCatching {
            client.delete("$BASE_URL/vacantes/$id") {
                withAuth()
            }
        }
    }

    // ─── Postulaciones ───────────────────────────────────────────────────────

    suspend fun postular(request: CreatePostulacionRequest): Result<Postulacion> {
        return runCatching {
            val response = client.post("$BASE_URL/postulaciones") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.value == 409) {
                throw Exception("Ya te has postulado a esta vacante anteriormente")
            }
            if (response.status.value == 403) {
                throw Exception("No tienes permiso para postularte")
            }
            response.body<Postulacion>()
        }
    }

    suspend fun getMisPostulaciones(): Result<List<Postulacion>> {
        return runCatching {
            client.get("$BASE_URL/postulaciones/mis-postulaciones") {
                withAuth()
            }.body<List<Postulacion>>()
        }
    }

    suspend fun getPostulacionesByVacante(vacanteId: Int): Result<List<Postulacion>> {
        return runCatching {
            client.get("$BASE_URL/postulaciones/vacante/$vacanteId") {
                withAuth()
            }.body<List<Postulacion>>()
        }
    }

    suspend fun updatePostulacionStatus(id: Int, status: String): Result<Postulacion> {
        return runCatching {
            client.put("$BASE_URL/postulaciones/$id/status") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(UpdatePostulacionStatusRequest(status))
            }.body<Postulacion>()
        }
    }

    suspend fun getPostulacionesByCompany(): Result<List<Postulacion>> {
        return runCatching {
            client.get("$BASE_URL/postulaciones/empresa") {
                withAuth()
            }.body<List<Postulacion>>()
        }
    }

    suspend fun getMisEstudiantes(): Result<List<Postulacion>> {
        return runCatching {
            client.get("$BASE_URL/postulaciones/mis-estudiantes") {
                withAuth()
            }.body<List<Postulacion>>()
        }
    }

    // ─── Documents ───────────────────────────────────────────────────────────

    suspend fun uploadDocument(request: CreateDocumentRequest): Result<Document> {
        return runCatching {
            client.post("$BASE_URL/documents") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<Document>()
        }
    }

    suspend fun getMisDocumentos(): Result<List<Document>> {
        return runCatching {
            client.get("$BASE_URL/documents/mis-documentos") {
                withAuth()
            }.body<List<Document>>()
        }
    }

    suspend fun getDocumentosByVacante(vacanteId: Int): Result<List<Document>> {
        return runCatching {
            client.get("$BASE_URL/documents/vacante/$vacanteId") {
                withAuth()
            }.body<List<Document>>()
        }
    }

    suspend fun getDocumentosOficiales(): Result<List<Document>> {
        return runCatching {
            client.get("$BASE_URL/documents/oficiales").body<List<Document>>()
        }
    }

    suspend fun publishDocumentoOficial(request: CreateDocumentRequest): Result<Document> {
        return runCatching {
            client.post("$BASE_URL/documents/oficiales") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<Document>()
        }
    }

    // ─── Attendance ──────────────────────────────────────────────────────────

    suspend fun registerAttendance(request: AttendanceRecord): Result<AttendanceRecord> {
        return runCatching {
            client.post("$BASE_URL/attendance") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<AttendanceRecord>()
        }
    }

    suspend fun getStudentAttendance(studentId: Int): Result<List<AttendanceRecord>> {
        return runCatching {
            client.get("$BASE_URL/attendance/student/$studentId") {
                withAuth()
            }.body<List<AttendanceRecord>>()
        }
    }

    suspend fun updateAttendance(id: Int, request: AttendanceRecord): Result<AttendanceRecord> {
        return runCatching {
            client.put("$BASE_URL/attendance/$id") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<AttendanceRecord>()
        }
    }

    // ─── Chat ────────────────────────────────────────────────────────────────

    suspend fun sendMessage(request: SendMessageRequest): Result<ChatMessage> {
        return runCatching {
            client.post("$BASE_URL/chat/send") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<ChatMessage>()
        }
    }

    suspend fun getConversations(): Result<List<Conversation>> {
        return runCatching {
            client.get("$BASE_URL/chat/conversations") {
                withAuth()
            }.body<List<Conversation>>()
        }
    }

    suspend fun getConversation(userId: Int): Result<List<ChatMessage>> {
        return runCatching {
            client.get("$BASE_URL/chat/conversation/$userId") {
                withAuth()
            }.body<List<ChatMessage>>()
        }
    }
}

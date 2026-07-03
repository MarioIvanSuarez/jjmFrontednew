package com.example.jjmfronted.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.models.Postulacion
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyRequestsScreen(
    token: String,
    onBack: () -> Unit,
    onStudentClick: ((Postulacion) -> Unit)? = null
) {
    var postulaciones by remember { mutableStateOf<List<Postulacion>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    suspend fun loadPostulaciones() {
        ApiClient.setToken(token)
        val result = ApiClient.getPostulacionesByCompany()
        result.fold(
            onSuccess = { postulaciones = it },
            onFailure = { }
        )
        loading = false
    }

    LaunchedEffect(token) { loadPostulaciones() }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Solicitudes recibidas") },
            navigationIcon = {
                IconButton(onClick = onBack) { Text("<", fontSize = 20.sp, color = Color.White) }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Blue800, titleContentColor = Color.White, navigationIconContentColor = Color.White
            )
        )

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Blue800)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(postulaciones) { postulacion ->
                    RequestCard(
                        postulacion = postulacion,
                        token = token,
                        onClickStudent = { onStudentClick?.invoke(postulacion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestCard(postulacion: Postulacion, token: String, onClickStudent: (() -> Unit)? = null) {
    var currentStatus by remember { mutableStateOf(postulacion.status) }
    var updating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val statusColor = when (currentStatus) {
        "PENDIENTE" -> Amber
        "ACEPTADA" -> Green600
        "RECHAZADA" -> Red600
        else -> Gray600
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(10.dp), color = Blue50, modifier = Modifier.size(44.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("👤", fontSize = 22.sp)
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    TextButton(
                        onClick = { onClickStudent?.invoke() },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Text(postulacion.studentName, fontWeight = FontWeight.SemiBold, color = Blue700, fontSize = 14.sp)
                    }
                    Text(postulacion.vacanteTitle ?: "Vacante", color = Gray600, fontSize = 13.sp, modifier = Modifier.padding(start = 12.dp))
                    postulacion.createdAt?.let { Text(it, color = Gray400, fontSize = 11.sp, modifier = Modifier.padding(start = 12.dp)) }
                }
                Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.1f)) {
                    val statusText = when (currentStatus) {
                        "PENDIENTE" -> "Pendiente"
                        "ACEPTADA" -> "Aceptado"
                        "RECHAZADA" -> "Rechazado"
                        else -> currentStatus
                    }
                    Text(
                        statusText,
                        color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            if (currentStatus == "PENDIENTE" && !updating) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Gray200)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        onClick = {
                            updating = true
                            scope.launch {
                                ApiClient.setToken(token)
                                val result = ApiClient.updatePostulacionStatus(postulacion.id, "RECHAZADA")
                                result.fold(
                                    onSuccess = { currentStatus = it.status; updating = false },
                                    onFailure = { updating = false }
                                )
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Red600),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Rechazar") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            updating = true
                            scope.launch {
                                ApiClient.setToken(token)
                                val result = ApiClient.updatePostulacionStatus(postulacion.id, "ACEPTADA")
                                result.fold(
                                    onSuccess = { currentStatus = it.status; updating = false },
                                    onFailure = { updating = false }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Green600),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Aceptar") }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(
                        onClick = {
                            updating = true
                            scope.launch {
                                ApiClient.setToken(token)
                                val result = ApiClient.updatePostulacionStatus(postulacion.id, "PENDIENTE")
                                result.fold(
                                    onSuccess = { currentStatus = it.status; updating = false },
                                    onFailure = { updating = false }
                                )
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Espera") }
                }
            }
        }
    }
}

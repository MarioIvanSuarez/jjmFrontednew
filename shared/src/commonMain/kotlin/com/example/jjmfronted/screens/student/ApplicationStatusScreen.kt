package com.example.jjmfronted.screens.student

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationStatusScreen(
    token: String,
    onBack: () -> Unit
) {
    var postulaciones by remember { mutableStateOf<List<Postulacion>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(token) {
        ApiClient.setToken(token)
        val result = ApiClient.getMisPostulaciones()
        result.fold(
            onSuccess = { postulaciones = it },
            onFailure = { }
        )
        loading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Mis solicitudes") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Text("<", fontSize = 20.sp, color = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Blue800,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Blue800)
            }
        } else if (postulaciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📥", fontSize = 80.sp, color = Gray200)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No tienes solicitudes aún", color = Gray600)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(postulaciones) { app ->
                    ApplicationCard(app)
                }
            }
        }
    }
}

@Composable
fun ApplicationCard(postulacion: Postulacion) {
    val (statusEmoji, statusColor, statusText) = when (postulacion.status) {
        "PENDIENTE" -> Triple("⏳", Amber, "En revisión")
        "ACEPTADA" -> Triple("✓", Green600, "Aceptada")
        "RECHAZADA" -> Triple("✕", Red600, "Rechazada")
        else -> Triple("⏳", Gray600, postulacion.status)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = statusColor.copy(alpha = 0.1f), modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(statusEmoji, fontSize = 22.sp)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(postulacion.vacanteTitle ?: "Vacante", fontWeight = FontWeight.SemiBold, color = Gray900, fontSize = 15.sp)
                Text("Solicitud #${postulacion.vacanteId}", color = Gray600, fontSize = 13.sp)
                postulacion.createdAt?.let {
                    Text("Enviada: $it", color = Gray400, fontSize = 11.sp)
                }
            }
            Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.1f)) {
                Text(
                    statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

package com.example.jjmfronted.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.models.Postulacion
import com.example.jjmfronted.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    postulacion: Postulacion,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Perfil del estudiante") },
            navigationIcon = {
                IconButton(onClick = onBack) { Text("<", fontSize = 20.sp, color = Color.White) }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Blue800, titleContentColor = Color.White, navigationIconContentColor = Color.White
            )
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(Blue50),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    postulacion.studentName.take(2).uppercase(),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue800
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(postulacion.studentName, style = MaterialTheme.typography.headlineMedium, color = Gray900)
            postulacion.studentEmail?.let {
                Text(it, color = Gray600, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ProfileRow("👤", "Nombre", postulacion.studentName)
                    Divider(color = Gray200, modifier = Modifier.padding(vertical = 10.dp))
                    postulacion.studentEmail?.let {
                        ProfileRow("@", "Correo", it)
                        Divider(color = Gray200, modifier = Modifier.padding(vertical = 10.dp))
                    }
                    ProfileRow("🎓", "Rol", "Estudiante")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Detalles de la postulación", fontWeight = FontWeight.SemiBold, color = Gray900, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileRow("📄", "Vacante", postulacion.vacanteTitle ?: "N/A")
                    Divider(color = Gray200, modifier = Modifier.padding(vertical = 10.dp))
                    val statusText = when (postulacion.status) {
                        "PENDIENTE" -> "Pendiente"
                        "ACEPTADA" -> "Aceptada"
                        "RECHAZADA" -> "Rechazada"
                        else -> postulacion.status
                    }
                    val statusColor = when (postulacion.status) {
                        "PENDIENTE" -> Amber
                        "ACEPTADA" -> Green600
                        "RECHAZADA" -> Red600
                        else -> Gray600
                    }
                    ProfileRow("📌", "Estado", statusText, valueColor = statusColor)
                    postulacion.createdAt?.let {
                        Divider(color = Gray200, modifier = Modifier.padding(vertical = 10.dp))
                        ProfileRow("📅", "Fecha", it)
                    }
                    postulacion.message?.let {
                        Divider(color = Gray200, modifier = Modifier.padding(vertical = 10.dp))
                        ProfileRow("💬", "Mensaje", it)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileRow(emoji: String, label: String, value: String, valueColor: Color = Gray900) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, color = Gray600, fontSize = 12.sp)
            Text(value, color = valueColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

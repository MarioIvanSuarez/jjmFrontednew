package com.example.jjmfronted.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.models.Vacante
import com.example.jjmfronted.models.CreatePostulacionRequest
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacancyDetailScreen(
    vacancy: Vacante,
    token: String,
    onBack: () -> Unit,
    onApply: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var applied by remember { mutableStateOf(false) }
    var applying by remember { mutableStateOf(false) }
    var applyError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Detalle de vacante") },
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

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 2.dp) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(10.dp), color = Blue50, modifier = Modifier.size(48.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🏢", fontSize = 26.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(vacancy.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Gray900)
                            Text(vacancy.companyName, color = Blue700, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Gray200)
                    Spacer(modifier = Modifier.height(16.dp))

                    DetailRow("📄", "Descripción", vacancy.description)
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow("✓", "Requisitos", vacancy.requirements ?: "No especificados")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        HalfDetail("🎓", "Área", vacancy.area ?: "General")
                        Spacer(modifier = Modifier.width(12.dp))
                        HalfDetail("🕐", "Duración", vacancy.duration ?: "No especificada")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        HalfDetail("📍", "Ubicación", vacancy.location ?: "No especificada")
                        Spacer(modifier = Modifier.width(12.dp))
                        HalfDetail("🕐", "Horario", vacancy.schedule ?: "No especificado")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow("👥", "Cupos", "${vacancy.slots} disponibles")
                }
            }

            if (applyError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFEBEE)) {
                    Text(applyError!!, color = Red600, modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!applied && !applying) {
                        showDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !applying,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (applied) Green600 else Blue800
                )
            ) {
                if (applying) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text(if (applied) "✓" else "📨", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (applied) "Solicitud enviada" else "Aplicar a esta vacante",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar aplicación") },
            text = { Text("¿Estás seguro de aplicar a \"${vacancy.title}\" en ${vacancy.companyName}?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        applying = true
                        scope.launch {
                            ApiClient.setToken(token)
                            val result = ApiClient.postular(CreatePostulacionRequest(vacanteId = vacancy.id))
                            result.fold(
                                onSuccess = {
                                    applied = true
                                    applying = false
                                },
                                onFailure = { e ->
                                    applyError = e.message ?: "Error al aplicar"
                                    applying = false
                                }
                            )
                        }
                    }
                ) { Text("Confirmar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun DetailRow(emoji: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(emoji, fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, color = Gray600, fontSize = 12.sp)
            Text(value, color = Gray900, fontSize = 14.sp)
        }
    }
}

@Composable
private fun HalfDetail(emoji: String, label: String, value: String) {
    Surface(shape = RoundedCornerShape(10.dp), color = Gray100, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
            Text(emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(label, color = Gray600, fontSize = 11.sp)
                Text(value, color = Gray900, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

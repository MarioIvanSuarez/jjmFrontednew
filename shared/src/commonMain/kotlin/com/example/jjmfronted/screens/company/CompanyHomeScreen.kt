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
import com.example.jjmfronted.models.User
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyHomeScreen(
    user: User,
    token: String,
    onVacanciesClick: () -> Unit,
    onRequestsClick: () -> Unit,
    onAttendanceClick: () -> Unit,
    onDocumentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    onNotificationsClick: () -> Unit = {}
) {
    var companyName by remember { mutableStateOf("Empresa") }
    var myVacantesCount by remember { mutableStateOf("0") }
    var recentSolicitudes by remember { mutableStateOf<List<Postulacion>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(token) {
        ApiClient.setToken(token)
        val profileResult = ApiClient.getMyCompanyProfile()
        profileResult.fold(
            onSuccess = { companyName = it.name ?: "Mi Empresa" },
            onFailure = { }
        )
        val vacantesResult = ApiClient.getMyVacantes()
        vacantesResult.fold(
            onSuccess = { myVacantesCount = "${it.size}" },
            onFailure = { }
        )
        val solicitudesResult = ApiClient.getPostulacionesByCompany()
        solicitudesResult.fold(
            onSuccess = { recentSolicitudes = it.take(3) },
            onFailure = { }
        )
        loading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text(companyName, fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Blue800,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            ),
            actions = {
                IconButton(onClick = onNotificationsClick) {
                    Text("\uD83D\uDD14", fontSize = 20.sp, color = Color.White)
                }
                IconButton(onClick = onProfileClick) {
                    Text("👤", fontSize = 22.sp, color = Color.White)
                }
            }
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Blue800
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Panel de empresa", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Gestiona tus vacantes y alumnos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Blue800)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickActionCard("Vacantes", "$myVacantesCount activas", "💼", Blue800, onClick = onVacanciesClick, Modifier.weight(1f))
                        QuickActionCard("Solicitudes", "${recentSolicitudes.size} nuevas", "👥", Cyan600, onClick = onRequestsClick, Modifier.weight(1f))
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickActionCard("Asistencias", "Gestión", "📅", Green600, onClick = onAttendanceClick, Modifier.weight(1f))
                        QuickActionCard("Documentos", "Archivos", "📁", Amber, onClick = onDocumentsClick, Modifier.weight(1f))
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Últimas solicitudes", style = MaterialTheme.typography.titleMedium, color = Gray900)
                }
                items(recentSolicitudes) { app ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = RoundedCornerShape(8.dp), color = Blue50, modifier = Modifier.size(40.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("👤", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.studentName, fontWeight = FontWeight.Medium, color = Gray900)
                                Text(app.vacanteTitle ?: "Vacante", color = Gray600, fontSize = 13.sp)
                            }
                            app.createdAt?.let { Text(it, color = Gray400, fontSize = 11.sp) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(label: String, subtitle: String, emoji: String, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(shape = RoundedCornerShape(10.dp), color = color.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(emoji, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(label, fontWeight = FontWeight.SemiBold, color = Gray900, fontSize = 15.sp)
            Text(subtitle, color = Gray600, fontSize = 12.sp)
        }
    }
}

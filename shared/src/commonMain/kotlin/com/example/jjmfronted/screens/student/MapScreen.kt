package com.example.jjmfronted.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.map.InteractiveMap
import com.example.jjmfronted.map.MapMarker
import com.example.jjmfronted.models.Vacante
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    token: String,
    onBack: () -> Unit,
    onVacancyClick: ((Vacante) -> Unit)? = null
) {
    var vacantes by remember { mutableStateOf<List<Vacante>>(emptyList()) }
    var selectedVacancy by remember { mutableStateOf<Vacante?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(token) {
        val result = ApiClient.getVacantes()
        result.fold(
            onSuccess = { vacantes = it },
            onFailure = { }
        )
        loading = false
    }

    val markers = remember(vacantes) {
        vacantes.mapNotNull { v ->
            val lat = v.latitude
            val lng = v.longitude
            if (lat != null && lng != null) {
                MapMarker(
                    id = v.id,
                    name = v.title,
                    latitude = lat,
                    longitude = lng,
                    description = v.companyName
                )
            } else null
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Mapa de vacantes") },
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

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue800)
                }
            } else if (markers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().background(Blue50), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No hay vacantes con ubicación en el mapa", color = Gray600, fontSize = 14.sp)
                    }
                }
            } else {
                InteractiveMap(
                    markers = markers,
                    initialLatitude = markers.firstOrNull()?.latitude ?: 23.7369,
                    initialLongitude = markers.firstOrNull()?.longitude ?: -99.1412,
                    onMapClick = { _, _ -> },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        selectedVacancy?.let { v ->
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { onVacancyClick?.invoke(v) },
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(v.title, fontWeight = FontWeight.Bold, color = Gray900)
                            Text(v.companyName, color = Blue700, fontSize = 13.sp)
                        }
                        IconButton(onClick = { selectedVacancy = null }) {
                            Text("✕", fontSize = 18.sp, color = Gray900)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📍", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(v.location ?: "Sin ubicación", color = Gray600, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Toca para ver detalle →", color = Blue700, fontSize = 12.sp)
                }
            }
        } ?: Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Blue800
        ) {
            Text(
                "Toca un marcador en el mapa para ver detalles",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(16.dp),
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
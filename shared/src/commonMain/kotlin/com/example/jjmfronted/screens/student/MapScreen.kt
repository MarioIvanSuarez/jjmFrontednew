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
import com.example.jjmfronted.map.UserLocation
import com.example.jjmfronted.models.Vacante
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.sensor.SensorProvider
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
    var userLocationState by remember { mutableStateOf<UserLocation?>(null) }
    var isLocating by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }

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

    fun requestLocation() {
        val gps = SensorProvider.gps
        if (gps == null) {
            locationError = "GPS no disponible"
            return
        }
        isLocating = true
        locationError = null
        gps.startListening { location ->
            userLocationState = UserLocation(location.latitude, location.longitude)
            isLocating = false
            gps.stopListening()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Gray50)) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                } else {
                    InteractiveMap(
                        markers = markers,
                        initialLatitude = userLocationState?.latitude ?: markers.firstOrNull()?.latitude ?: 23.7369,
                        initialLongitude = userLocationState?.longitude ?: markers.firstOrNull()?.longitude ?: -99.1412,
                        onMapClick = { _, _ -> },
                        modifier = Modifier.fillMaxSize(),
                        userLocation = userLocationState
                    )
                }

                if (isLocating) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Blue800)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Obteniendo ubicación...", color = Gray800)
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { requestLocation() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Blue800,
            contentColor = Color.White
        ) {
            Text("📍", fontSize = 22.sp)
        }

        locationError?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { locationError = null }) {
                        Text("OK", color = Color.White)
                    }
                }
            ) {
                Text(error)
            }
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

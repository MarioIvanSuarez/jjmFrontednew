package com.example.jjmfronted.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.map.InteractiveMap
import com.example.jjmfronted.models.UpdateCompanyProfileRequest
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyEditProfileScreen(
    token: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onLogout: () -> Unit = {}
) {
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var showMapPicker by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(token) {
        ApiClient.setToken(token)
        val result = ApiClient.getMyCompanyProfile()
        result.fold(
            onSuccess = { profile ->
                description = profile.description ?: ""
                address = profile.address ?: ""
                phone = profile.phone ?: ""
                website = profile.website ?: ""
                latitude = profile.latitude?.toString() ?: ""
                longitude = profile.longitude?.toString() ?: ""
            },
            onFailure = { }
        )
        loading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Perfil de empresa") },
            navigationIcon = {
                IconButton(onClick = onBack) { Text("<", fontSize = 20.sp, color = Color.White) }
            },
            actions = {
                TextButton(
                    onClick = {
                        saving = true
                        saveError = null
                        scope.launch {
                            ApiClient.setToken(token)
                            val request = UpdateCompanyProfileRequest(
                                description = description.ifBlank { null },
                                address = address.ifBlank { null },
                                phone = phone.ifBlank { null },
                                website = website.ifBlank { null },
                                latitude = latitude.toDoubleOrNull(),
                                longitude = longitude.toDoubleOrNull()
                            )
                            val result = ApiClient.updateCompanyProfile(request)
                            result.fold(
                                onSuccess = { saving = false; onSaved() },
                                onFailure = { e ->
                                    saveError = e.message ?: "Error al guardar"
                                    saving = false
                                }
                            )
                        }
                    },
                    enabled = !saving
                ) {
                    if (saving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text("Guardar", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
                TextButton(onClick = onLogout) {
                    Text("Cerrar sesión", color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Normal)
                }
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
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Información de la empresa", style = MaterialTheme.typography.titleMedium, color = Gray900)

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Sitio web") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Ubicación en el mapa", style = MaterialTheme.typography.titleMedium, color = Gray900)

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = latitude,
                                onValueChange = { latitude = it },
                                label = { Text("Latitud") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = longitude,
                                onValueChange = { longitude = it },
                                label = { Text("Longitud") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth().height(150.dp).clickable { showMapPicker = true },
                            shape = RoundedCornerShape(10.dp),
                            color = Blue50
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🗺", fontSize = 36.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val lat = latitude.toDoubleOrNull()
                                    val lng = longitude.toDoubleOrNull()
                                    if (lat != null && lng != null) {
                                        Text("$lat, $lng", color = Blue800, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                        Text("Toca para cambiar ubicación", color = Gray600, fontSize = 12.sp)
                                    } else {
                                        Text("Toca para seleccionar ubicación", color = Blue700, fontSize = 14.sp)
                                        Text("o ingresa coordenadas manualmente", color = Gray600, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                if (saveError != null) {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFEBEE)) {
                        Text(saveError!!, color = Red600, modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        saving = true
                        saveError = null
                        scope.launch {
                            ApiClient.setToken(token)
                            val request = UpdateCompanyProfileRequest(
                                description = description.ifBlank { null },
                                address = address.ifBlank { null },
                                phone = phone.ifBlank { null },
                                website = website.ifBlank { null },
                                latitude = latitude.toDoubleOrNull(),
                                longitude = longitude.toDoubleOrNull()
                            )
                            val result = ApiClient.updateCompanyProfile(request)
                            result.fold(
                                onSuccess = { saving = false; onSaved() },
                                onFailure = { e ->
                                    saveError = e.message ?: "Error al guardar"
                                    saving = false
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !saving,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) {
                    if (saving) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text("💾", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar cambios", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (showMapPicker) {
            MapLocationPickerDialog(
                currentLat = latitude.toDoubleOrNull() ?: 23.7369,
                currentLng = longitude.toDoubleOrNull() ?: -99.1412,
                onConfirm = { lat, lng ->
                    latitude = lat.toString()
                    longitude = lng.toString()
                    showMapPicker = false
                },
                onDismiss = { showMapPicker = false }
            )
        }
    }
}

@Composable
private fun MapLocationPickerDialog(
    currentLat: Double,
    currentLng: Double,
    onConfirm: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var latInput by remember { mutableStateOf(currentLat.toString()) }
    var lngInput by remember { mutableStateOf(currentLng.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar ubicación", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                    InteractiveMap(
                        markers = emptyList(),
                        initialLatitude = currentLat,
                        initialLongitude = currentLng,
                        onMapClick = { lat, lng ->
                            latInput = lat.toString()
                            lngInput = lng.toString()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text("Toca el mapa para seleccionar la ubicación", color = Gray600, fontSize = 12.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = latInput,
                        onValueChange = { latInput = it },
                        label = { Text("Latitud") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = lngInput,
                        onValueChange = { lngInput = it },
                        label = { Text("Longitud") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val lat = latInput.toDoubleOrNull() ?: currentLat
                val lng = lngInput.toDoubleOrNull() ?: currentLng
                onConfirm(lat, lng)
            }) { Text("Confirmar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

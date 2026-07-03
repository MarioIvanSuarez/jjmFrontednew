package com.example.jjmfronted.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.jjmfronted.map.InteractiveMap
import com.example.jjmfronted.models.Vacante
import com.example.jjmfronted.models.CreateVacanteRequest
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyVacanciesScreen(
    token: String,
    onBack: () -> Unit
) {
    var showCreate by remember { mutableStateOf(false) }
    var vacancies by remember { mutableStateOf<List<Vacante>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    suspend fun loadVacantes() {
        ApiClient.setToken(token)
        val result = ApiClient.getMyVacantes()
        result.fold(
            onSuccess = { vacancies = it },
            onFailure = { }
        )
        loading = false
    }

    LaunchedEffect(token) {
        loadVacantes()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Gray50
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Gray50)) {
            TopAppBar(
                title = { Text("Mis vacantes") },
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
                    item {
                        Button(
                            onClick = { showCreate = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                        ) {
                            Text("+", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publicar nueva vacante", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(vacancies) { vacancy ->
                        CompanyVacancyCard(
                            vacancy = vacancy,
                            token = token,
                            onDeleted = {
                                scope.launch {
                                    loadVacantes()
                                    snackbarHostState.showSnackbar("Vacante eliminada")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreate) {
        CreateVacancyDialog(
            token = token,
            onDismiss = { showCreate = false },
            onCreated = {
                showCreate = false
                scope.launch {
                    loadVacantes()
                    snackbarHostState.showSnackbar("Vacante creada exitosamente")
                }
            }
        )
    }
}

@Composable
private fun CompanyVacancyCard(vacancy: Vacante, token: String, onDeleted: () -> Unit = {}) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(8.dp), color = Blue50, modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("💼", fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(vacancy.title, fontWeight = FontWeight.SemiBold, color = Gray900)
                    Text("${vacancy.slots} cupos · ${vacancy.duration ?: "Sin duración"}", color = Gray600, fontSize = 13.sp)
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (vacancy.status != "CERRADA") Green600.copy(alpha = 0.1f) else Red600.copy(alpha = 0.1f)
                ) {
                    Text(
                        if (vacancy.status != "CERRADA") "Activa" else "Cerrada",
                        color = if (vacancy.status != "CERRADA") Green600 else Red600,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { }) { Text("Editar", color = Blue700) }
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = { showDeleteConfirm = true }
                ) { Text("Eliminar", color = Red600) }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar vacante") },
            text = { Text("¿Estás seguro de eliminar \"${vacancy.title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        scope.launch {
                            ApiClient.setToken(token)
                            val result = ApiClient.deleteVacante(vacancy.id)
                            result.fold(
                                onSuccess = { onDeleted() },
                                onFailure = { }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red600)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun CreateVacancyDialog(
    token: String,
    onDismiss: () -> Unit,
    onCreated: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }
    var slots by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var creating by remember { mutableStateOf(false) }
    var showLocationPicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!creating) onDismiss() },
        title = { Text("Nueva vacante", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción *") }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = requirements, onValueChange = { requirements = it }, label = { Text("Requisitos") }, modifier = Modifier.fillMaxWidth(), minLines = 2, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = slots, onValueChange = { slots = it }, label = { Text("Cupos *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("Área") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duración") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = schedule, onValueChange = { schedule = it }, label = { Text("Horario") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Ubicación") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                Text("Coordenadas (toca el mapa)", style = MaterialTheme.typography.labelLarge, color = Gray600)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitud") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(10.dp), readOnly = true)
                    OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitud") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(10.dp), readOnly = true)
                }
                OutlinedButton(
                    onClick = { showLocationPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🗺 Seleccionar en mapa")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && slots.isNotBlank()) {
                        creating = true
                        scope.launch {
                            ApiClient.setToken(token)
                            val request = CreateVacanteRequest(
                                title = title,
                                description = description,
                                requirements = requirements.ifBlank { null },
                                slots = slots.toIntOrNull() ?: 1,
                                area = area.ifBlank { null },
                                duration = duration.ifBlank { null },
                                schedule = schedule.ifBlank { null },
                                location = location.ifBlank { null },
                                latitude = latitude.toDoubleOrNull(),
                                longitude = longitude.toDoubleOrNull()
                            )
                            val result = ApiClient.createVacante(request)
                            result.fold(
                                onSuccess = { onCreated() },
                                onFailure = { creating = false }
                            )
                        }
                    }
                },
                enabled = !creating && title.isNotBlank() && description.isNotBlank() && slots.isNotBlank()
            ) {
                if (creating) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                else Text("Publicar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, enabled = !creating) { Text("Cancelar") }
        }
    )

    if (showLocationPicker) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showLocationPicker = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                InteractiveMap(
                    markers = emptyList(),
                    initialLatitude = latitude.toDoubleOrNull() ?: 23.7369,
                    initialLongitude = longitude.toDoubleOrNull() ?: -99.1412,
                    onMapClick = { lat, lng ->
                        latitude = lat.toString()
                        longitude = lng.toString()
                    },
                    modifier = Modifier.fillMaxSize()
                )
                Column(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(16.dp)) {
                    TopAppBar(
                        title = { Text("Seleccionar ubicación en el mapa") },
                        navigationIcon = {
                            IconButton(onClick = { showLocationPicker = false }) {
                                Text("✕", fontSize = 18.sp, color = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Blue800,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                }
                Card(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Coordenadas seleccionadas", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = latitude, onValueChange = {}, label = { Text("Latitud") }, modifier = Modifier.weight(1f), readOnly = true, singleLine = true, shape = RoundedCornerShape(10.dp))
                            OutlinedTextField(value = longitude, onValueChange = {}, label = { Text("Longitud") }, modifier = Modifier.weight(1f), readOnly = true, singleLine = true, shape = RoundedCornerShape(10.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showLocationPicker = false },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                        ) { Text("Confirmar ubicación") }
                    }
                }
            }
        }
    }
}

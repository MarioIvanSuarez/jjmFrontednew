package com.example.jjmfronted.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.models.User
import com.example.jjmfronted.models.Vacante
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    user: User,
    token: String,
    onVacancyClick: (Vacante) -> Unit,
    onApplicationsClick: () -> Unit,
    onDocumentsClick: () -> Unit,
    onMapClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    onNotificationsClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var vacancies by remember { mutableStateOf<List<Vacante>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var filterArea by remember { mutableStateOf("") }
    var filterLocation by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    suspend fun loadVacantes(area: String? = null, location: String? = null) {
        ApiClient.setToken(token)
        val result = ApiClient.getVacantes(area = area, location = location)
        result.fold(
            onSuccess = { vacancies = it },
            onFailure = { }
        )
        loading = false
    }

    val uniqueAreas = remember(vacancies) {
        vacancies.mapNotNull { it.area }.distinct().sorted()
    }
    val uniqueLocations = remember(vacancies) {
        vacancies.mapNotNull { it.location }.distinct().sorted()
    }

    LaunchedEffect(token) {
        loadVacantes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("JJ&M Academic", fontWeight = FontWeight.Bold) },
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
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("🏠", fontSize = 16.sp) },
                    label = { Text("Vacantes") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Blue800, selectedTextColor = Blue800)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { onApplicationsClick(); selectedTab = 1 },
                    icon = { Text("📄", fontSize = 16.sp) },
                    label = { Text("Solicitudes") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Blue800, selectedTextColor = Blue800)
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { onDocumentsClick(); selectedTab = 2 },
                    icon = { Text("📤", fontSize = 16.sp) },
                    label = { Text("Documentos") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Blue800, selectedTextColor = Blue800)
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { onMapClick(); selectedTab = 3 },
                    icon = { Text("🗺", fontSize = 16.sp) },
                    label = { Text("Mapa") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Blue800, selectedTextColor = Blue800)
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Gray50)) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Blue800,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Text("Hola, ${user.name}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Encuentra tu próxima oportunidad", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar vacantes...", color = Color.White.copy(alpha = 0.6f)) },
                        leadingIcon = { Text("🔍", fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = filterArea.isNotEmpty(),
                            onClick = { showFilters = !showFilters },
                            label = { Text(if (filterArea.isNotEmpty()) "Área: $filterArea" else "Todas las áreas", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.White.copy(alpha = 0.2f),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = filterLocation.isNotEmpty(),
                            onClick = { showFilters = !showFilters },
                            label = { Text(if (filterLocation.isNotEmpty()) "Ubicación: $filterLocation" else "Todas las ubicaciones", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.White.copy(alpha = 0.2f),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
            if (showFilters) {
                Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 2.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Filtrar por área", style = MaterialTheme.typography.labelLarge, color = Gray900)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = filterArea.isEmpty(),
                                    onClick = {
                                        filterArea = ""
                                        scope.launch { loadVacantes(area = null, location = filterLocation.ifBlank { null }) }
                                    },
                                    label = { Text("Todas", fontSize = 12.sp) }
                                )
                            }
                            items(uniqueAreas) { area ->
                                FilterChip(
                                    selected = filterArea == area,
                                    onClick = {
                                        filterArea = area
                                        scope.launch { loadVacantes(area = area, location = filterLocation.ifBlank { null }) }
                                    },
                                    label = { Text(area, fontSize = 12.sp) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Filtrar por ubicación", style = MaterialTheme.typography.labelLarge, color = Gray900)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = filterLocation.isEmpty(),
                                    onClick = {
                                        filterLocation = ""
                                        scope.launch { loadVacantes(area = filterArea.ifBlank { null }, location = null) }
                                    },
                                    label = { Text("Todas", fontSize = 12.sp) }
                                )
                            }
                            items(uniqueLocations) { loc ->
                                FilterChip(
                                    selected = filterLocation == loc,
                                    onClick = {
                                        filterLocation = loc
                                        scope.launch { loadVacantes(area = filterArea.ifBlank { null }, location = loc) }
                                    },
                                    label = { Text(loc, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }
            }

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue800)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Vacantes disponibles", style = MaterialTheme.typography.titleMedium, color = Gray900)
                            Text("${vacancies.size} resultados", color = Gray600, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    items(vacancies) { vacancy ->
                        VacancyCard(vacancy, onClick = { onVacancyClick(vacancy) })
                    }
                }
            }
        }
    }
}

@Composable
fun VacancyCard(vacancy: Vacante, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Blue50,
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🏢", fontSize = 26.sp)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(vacancy.title, fontWeight = FontWeight.SemiBold, color = Gray900, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(vacancy.companyName, color = Blue700, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📍", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(vacancy.location ?: "Sin ubicación", color = Gray600, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("👥", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${vacancy.slots} cupos", color = Gray600, fontSize = 12.sp)
                }
            }
        }
    }
}

package com.example.jjmfronted.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.jjmfronted.models.MockVacancy
import com.example.jjmfronted.models.MockData
import com.example.jjmfronted.models.MockApplication
import com.example.jjmfronted.models.ApplicationStatus
import com.example.jjmfronted.models.User
import com.example.jjmfronted.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    user: User,
    onVacancyClick: (MockVacancy) -> Unit,
    onApplicationsClick: () -> Unit,
    onDocumentsClick: () -> Unit,
    onMapClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val vacancies = MockData.vacancies.filter { it.isActive }

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
                        value = "",
                        onValueChange = {},
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
                }
            }

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

@Composable
fun VacancyCard(vacancy: MockVacancy, onClick: () -> Unit) {
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
                    Text(vacancy.location, color = Gray600, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("👥", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${vacancy.slots - vacancy.appliedCount} cupos", color = Gray600, fontSize = 12.sp)
                }
            }
        }
    }
}

package com.example.jjmfronted.screens.admin

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
import com.example.jjmfronted.models.Document
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDeliveriesScreen(
    token: String,
    onBack: () -> Unit
) {
    var deliveries by remember { mutableStateOf<List<Document>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(token) {
        ApiClient.setToken(token)
        // Admin deliveries are documents uploaded by students - fetch all docs
        val result = ApiClient.getDocumentosOficiales()
        result.fold(
            onSuccess = { deliveries = it },
            onFailure = { }
        )
        loading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Entregas recibidas") },
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
        } else if (deliveries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 80.sp, color = Gray200)
                    Text("No hay entregas recibidas", color = Gray600)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(deliveries) { delivery ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE8F5E9), modifier = Modifier.size(40.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("📋", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(delivery.name, fontWeight = FontWeight.Medium, color = Gray900, fontSize = 14.sp)
                                Text("${delivery.uploadedBy ?: "Desconocido"} · ${delivery.createdAt ?: ""}", color = Gray600, fontSize = 12.sp)
                            }
                            IconButton(onClick = {}) {
                                Text("📥", fontSize = 16.sp, color = Blue600)
                            }
                        }
                    }
                }
            }
        }
    }
}

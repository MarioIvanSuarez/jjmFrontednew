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
import com.example.jjmfronted.models.Document
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDocumentsScreen(
    token: String,
    onBack: () -> Unit
) {
    var documents by remember { mutableStateOf<List<Document>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(token) {
        ApiClient.setToken(token)
        val result = ApiClient.getDocumentosOficiales()
        result.fold(
            onSuccess = { documents = it },
            onFailure = { }
        )
        loading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Gestión documental") },
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
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                    ) {
                        Text("📤", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Subir documento", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Documentos", style = MaterialTheme.typography.titleMedium, color = Gray900)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(documents) { doc ->
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
                                    Text("📄", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(doc.name, fontWeight = FontWeight.Medium, color = Gray900, fontSize = 14.sp)
                                Text("${doc.uploadedBy ?: ""} · ${doc.createdAt ?: ""}", color = Gray600, fontSize = 12.sp)
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

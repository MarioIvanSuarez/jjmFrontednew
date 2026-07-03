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
import com.example.jjmfronted.models.CreateDocumentRequest
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDocumentsScreen(
    token: String,
    onBack: () -> Unit
) {
    var documentos by remember { mutableStateOf<List<Document>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showCreateDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    suspend fun loadDocumentos() {
        ApiClient.setToken(token)
        val result = ApiClient.getDocumentosOficiales()
        result.fold(
            onSuccess = { documentos = it },
            onFailure = { }
        )
        loading = false
    }

    LaunchedEffect(token) { loadDocumentos() }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Formatos oficiales") },
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
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                    ) {
                        Text("+", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publicar nuevo formato", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(documentos) { doc ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(8.dp), color = Blue50, modifier = Modifier.size(40.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("📄", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(doc.name, fontWeight = FontWeight.Medium, color = Gray900, fontSize = 14.sp)
                                Text("${doc.type ?: "Documento"} · ${doc.createdAt ?: ""}", color = Gray600, fontSize = 12.sp)
                            }
                            IconButton(onClick = {}) { Text("✏", fontSize = 16.sp, color = Blue600) }
                            IconButton(onClick = {}) { Text("🗑", fontSize = 16.sp, color = Red600) }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateOfficialDocumentDialog(
            token = token,
            onDismiss = { showCreateDialog = false },
            onCreated = {
                showCreateDialog = false
                scope.launch { loadDocumentos() }
            }
        )
    }
}

@Composable
private fun CreateOfficialDocumentDialog(
    token: String,
    onDismiss: () -> Unit,
    onCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var fileUrl by remember { mutableStateOf("") }
    var creating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!creating) onDismiss() },
        title = { Text("Publicar formato oficial") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del documento *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tipo (PDF, DOCX...)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = fileUrl, onValueChange = { fileUrl = it }, label = { Text("URL del archivo") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        creating = true
                        scope.launch {
                            ApiClient.setToken(token)
                            val request = CreateDocumentRequest(
                                name = name,
                                type = type.ifBlank { null },
                                fileUrl = fileUrl.ifBlank { null }
                            )
                            ApiClient.publishDocumentoOficial(request).fold(
                                onSuccess = { onCreated() },
                                onFailure = { creating = false }
                            )
                        }
                    }
                },
                enabled = !creating && name.isNotBlank()
            ) {
                if (creating) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                else Text("Publicar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, enabled = !creating) { Text("Cancelar") }
        }
    )
}

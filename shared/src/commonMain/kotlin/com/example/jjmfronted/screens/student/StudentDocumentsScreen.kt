package com.example.jjmfronted.screens.student

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
import com.example.jjmfronted.models.MockData
import com.example.jjmfronted.models.MockDocument
import com.example.jjmfronted.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDocumentsScreen(onBack: () -> Unit) {
    val documents = MockData.documents.filter { it.uploadedBy != "Vinculación" }
    val officialDocs = MockData.documents.filter { it.uploadedBy == "Vinculación" }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Documentos") },
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

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Formatos oficiales", style = MaterialTheme.typography.titleMedium, color = Gray900)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(officialDocs) { doc -> DocumentCard(doc, isOfficial = true) }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Mis documentos", style = MaterialTheme.typography.titleMedium, color = Gray900)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(documents) { doc -> DocumentCard(doc, isOfficial = false) }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) {
                    Text("📤", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Subir documento", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DocumentCard(doc: MockDocument, isOfficial: Boolean) {
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
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isOfficial) Blue50 else Color(0xFFE8F5E9),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        if (isOfficial) "📄" else "📤",
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(doc.name, fontWeight = FontWeight.Medium, color = Gray900, fontSize = 14.sp)
                Text("${doc.type} · ${doc.uploadDate}", color = Gray600, fontSize = 12.sp)
            }
            IconButton(onClick = {}) {
                Text("📥", fontSize = 16.sp, color = Blue600)
            }
        }
    }
}

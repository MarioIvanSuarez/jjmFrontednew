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
import com.example.jjmfronted.models.MockData
import com.example.jjmfronted.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onDocumentsClick: () -> Unit,
    onDeliveriesClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Admin · Vinculación", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Blue800, titleContentColor = Color.White
            )
        )

        Surface(modifier = Modifier.fillMaxWidth(), color = Blue800) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Panel de administración", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Text("Gestiona formatos y estudiantes", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    AdminCard("Formatos", "Publica documentos oficiales", "📄", Blue800, onClick = onDocumentsClick, Modifier.weight(1f))
                    AdminCard("Entregas", "Revisa documentos recibidos", "📋", Green600, onClick = onDeliveriesClick, Modifier.weight(1f))
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Resumen", style = MaterialTheme.typography.titleMedium, color = Gray900)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        SummaryRow("Empresas registradas", "${MockData.companies.size}", Blue800)
                        Divider(color = Gray200, modifier = Modifier.padding(vertical = 10.dp))
                        SummaryRow("Vacantes activas", "${MockData.vacancies.size}", Blue600)
                        Divider(color = Gray200, modifier = Modifier.padding(vertical = 10.dp))
                        SummaryRow("Solicitudes recibidas", "${MockData.applications.size}", Cyan600)
                        Divider(color = Gray200, modifier = Modifier.padding(vertical = 10.dp))
                        SummaryRow("Documentos subidos", "${MockData.documents.size}", Green600)
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Red600)
                ) {
                    Text("🚪", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión")
                }
            }
        }
    }
}

@Composable
private fun AdminCard(label: String, desc: String, emoji: String, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(shape = RoundedCornerShape(10.dp), color = color.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(emoji, fontSize = 20.sp) }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(label, fontWeight = FontWeight.SemiBold, color = Gray900, fontSize = 15.sp)
            Text(desc, color = Gray600, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Gray600)
        Text(value, fontWeight = FontWeight.Bold, color = color)
    }
}

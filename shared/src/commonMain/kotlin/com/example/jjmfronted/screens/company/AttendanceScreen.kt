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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.models.AttendanceStatus
import com.example.jjmfronted.models.MockData
import com.example.jjmfronted.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(onBack: () -> Unit) {
    val attendance = MockData.attendance

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Asistencias") },
            navigationIcon = {
                IconButton(onClick = onBack) { Text("<", fontSize = 20.sp, color = Color.White) }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Blue800, titleContentColor = Color.White, navigationIconContentColor = Color.White
            )
        )

        Surface(modifier = Modifier.fillMaxWidth(), color = Blue50) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Asistencias", "${attendance.count { it.status == AttendanceStatus.PRESENT }}", Green600)
                StatItem("Faltas", "${attendance.count { it.status == AttendanceStatus.ABSENT }}", Red600)
                StatItem("Justificados", "${attendance.count { it.status == AttendanceStatus.JUSTIFIED }}", Amber)
                StatItem("Total", "${attendance.size}", Blue800)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Text("Registro de asistencia", style = MaterialTheme.typography.titleMedium, color = Gray900) }

            items(attendance) { record ->
                val (emoji, color, label) = when (record.status) {
                    AttendanceStatus.PRESENT -> Triple("✓", Green600, "Presente")
                    AttendanceStatus.ABSENT -> Triple("✕", Red600, "Falta")
                    AttendanceStatus.JUSTIFIED -> Triple("ℹ", Amber, "Justificado")
                }

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
                        Text(emoji, fontSize = 20.sp, color = color)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(record.date, fontWeight = FontWeight.Medium, color = Gray900)
                            if (record.justification.isNotEmpty()) {
                                Text(record.justification, color = Gray600, fontSize = 12.sp)
                            }
                        }
                        Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.1f)) {
                            Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, color = Gray600, fontSize = 12.sp)
    }
}

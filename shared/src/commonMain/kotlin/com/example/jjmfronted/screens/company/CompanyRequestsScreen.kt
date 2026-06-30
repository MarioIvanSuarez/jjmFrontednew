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
import com.example.jjmfronted.models.MockApplication
import com.example.jjmfronted.models.ApplicationStatus
import com.example.jjmfronted.models.MockData
import com.example.jjmfronted.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyRequestsScreen(onBack: () -> Unit) {
    val applications = MockData.applications

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Solicitudes recibidas") },
            navigationIcon = {
                IconButton(onClick = onBack) { Text("<", fontSize = 20.sp, color = Color.White) }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Blue800, titleContentColor = Color.White, navigationIconContentColor = Color.White
            )
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(applications) { application ->
                RequestCard(application)
            }
        }
    }
}

@Composable
private fun RequestCard(application: MockApplication) {
    var currentStatus by remember { mutableStateOf(application.status) }

    val statusColor = when (currentStatus) {
        ApplicationStatus.PENDING -> Amber
        ApplicationStatus.ACCEPTED -> Green600
        ApplicationStatus.REJECTED -> Red600
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(10.dp), color = Blue50, modifier = Modifier.size(44.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("👤", fontSize = 22.sp)
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(application.studentName, fontWeight = FontWeight.SemiBold, color = Gray900)
                    Text(application.vacancyTitle, color = Gray600, fontSize = 13.sp)
                    Text(application.appliedDate, color = Gray400, fontSize = 11.sp)
                }
                Surface(shape = RoundedCornerShape(20.dp), color = statusColor.copy(alpha = 0.1f)) {
                    val statusText = when (currentStatus) {
                        ApplicationStatus.PENDING -> "Pendiente"
                        ApplicationStatus.ACCEPTED -> "Aceptado"
                        ApplicationStatus.REJECTED -> "Rechazado"
                    }
                    Text(
                        statusText,
                        color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            if (currentStatus == ApplicationStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Gray200)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        onClick = { currentStatus = ApplicationStatus.REJECTED },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Red600),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Rechazar") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { currentStatus = ApplicationStatus.ACCEPTED },
                        colors = ButtonDefaults.buttonColors(containerColor = Green600),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Aceptar") }
                }
            }
        }
    }
}

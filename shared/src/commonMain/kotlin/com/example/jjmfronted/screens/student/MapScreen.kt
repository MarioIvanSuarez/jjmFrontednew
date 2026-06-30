package com.example.jjmfronted.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.models.MockData
import com.example.jjmfronted.models.MockCompany
import com.example.jjmfronted.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(onBack: () -> Unit) {
    var selectedCompany by remember { mutableStateOf<MockCompany?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Mapa de empresas") },
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Blue50)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    "📍 Mapa interactivo",
                    style = MaterialTheme.typography.titleMedium,
                    color = Blue800,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text(
                            "Aquí se mostraría el mapa con las ubicaciones de las empresas.\n\nActualmente hay ${MockData.companies.size} empresas registradas.",
                            color = Gray600,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                        ) {
                            MockData.companies.forEach { company ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable { selectedCompany = company },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Blue800),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${company.id}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(company.name, fontSize = 13.sp, color = Gray900)
                                }
                            }
                        }
                    }
                }
            }
        }

        selectedCompany?.let { company ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(company.name, fontWeight = FontWeight.Bold, color = Gray900, modifier = Modifier.weight(1f))
                        IconButton(onClick = { selectedCompany = null }) {
                            Text("✕", fontSize = 18.sp, color = Gray900)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(company.industry, color = Blue700, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📍", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(company.location, color = Gray600, fontSize = 12.sp)
                    }
                }
            }
        } ?: Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Blue800
        ) {
            Text(
                "Selecciona una empresa para ver detalles",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(16.dp),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

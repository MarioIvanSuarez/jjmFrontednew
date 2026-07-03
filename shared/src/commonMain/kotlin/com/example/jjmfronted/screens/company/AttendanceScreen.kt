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
import com.example.jjmfronted.models.AttendanceRecord
import com.example.jjmfronted.models.Postulacion
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    token: String,
    onBack: () -> Unit
) {
    var attendance by remember { mutableStateOf<List<AttendanceRecord>>(emptyList()) }
    var students by remember { mutableStateOf<List<Postulacion>>(emptyList()) }
    var selectedStudent by remember { mutableStateOf<Postulacion?>(null) }
    var loading by remember { mutableStateOf(true) }
    var studentsLoading by remember { mutableStateOf(true) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(token) {
        ApiClient.setToken(token)
        studentsLoading = true
        val result = ApiClient.getMisEstudiantes()
        result.fold(
            onSuccess = { students = it },
            onFailure = { }
        )
        studentsLoading = false
        loading = false
    }

    suspend fun loadAttendance(studentId: Int) {
        loading = true
        ApiClient.setToken(token)
        val result = ApiClient.getStudentAttendance(studentId)
        result.fold(
            onSuccess = { attendance = it },
            onFailure = { attendance = emptyList() }
        )
        loading = false
    }

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

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Seleccionar estudiante", style = MaterialTheme.typography.titleMedium, color = Gray900)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedStudent?.studentName ?: "Selecciona un estudiante...",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    students.forEach { student ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(student.studentName, fontWeight = FontWeight.Medium)
                                    Text(student.vacanteTitle ?: "", color = Gray600, fontSize = 12.sp)
                                }
                            },
                            onClick = {
                                selectedStudent = student
                                dropdownExpanded = false
                                scope.launch { loadAttendance(student.studentId) }
                            }
                        )
                    }
                }
            }

            if (studentsLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue800)
                }
            } else if (selectedStudent == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Selecciona un estudiante para ver sus asistencias", color = Gray600)
                }
            } else if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue800)
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(modifier = Modifier.fillMaxWidth(), color = Blue50, shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("Asistencias", "${attendance.count { it.status == "PRESENTE" }}", Green600)
                        StatItem("Faltas", "${attendance.count { it.status == "FALTA" }}", Red600)
                        StatItem("Justificados", "${attendance.count { it.status == "JUSTIFICADO" }}", Amber)
                        StatItem("Total", "${attendance.size}", Blue800)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text("Registro de asistencia", style = MaterialTheme.typography.titleMedium, color = Gray900)
                    }
                    items(attendance) { record ->
                        val (emoji, color, label) = when (record.status) {
                            "PRESENTE" -> Triple("✓", Green600, "Presente")
                            "FALTA" -> Triple("✕", Red600, "Falta")
                            "JUSTIFICADO" -> Triple("ℹ", Amber, "Justificado")
                            else -> Triple("?", Gray600, record.status)
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
                                    if (!record.notes.isNullOrEmpty()) {
                                        Text(record.notes!!, color = Gray600, fontSize = 12.sp)
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
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, color = Gray600, fontSize = 12.sp)
    }
}
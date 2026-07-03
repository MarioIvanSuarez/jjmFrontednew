package com.example.jjmfronted.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.models.UserRole
import com.example.jjmfronted.ui.theme.*

@Composable
fun RegisterScreen(
    onRegister: (email: String, password: String, name: String, role: UserRole) -> Unit,
    onNavigateToLogin: () -> Unit,
    isLoading: Boolean,
    error: String?
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Blue800, Blue600)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Crear tu cuenta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Únete a JJ&M Academic", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 32.dp)
        ) {
            Text("Tipo de usuario", style = MaterialTheme.typography.titleMedium, color = Gray900)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RoleChip("Estudiante", UserRole.STUDENT, selectedRole == UserRole.STUDENT) { selectedRole = UserRole.STUDENT }
                RoleChip("Empresa", UserRole.COMPANY, selectedRole == UserRole.COMPANY) { selectedRole = UserRole.COMPANY }
                RoleChip("Vinculaci\u00f3n", UserRole.ADMIN, selectedRole == UserRole.ADMIN) { selectedRole = UserRole.ADMIN }
            }

            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre completo") },
                leadingIcon = { Text("👤", fontSize = 16.sp, color = Blue600) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                leadingIcon = { Text("@", fontSize = 16.sp, color = Blue600) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Text("*", fontSize = 16.sp, color = Blue600) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "🙈" else "👁", fontSize = 16.sp, color = Gray600)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                leadingIcon = { Text("*", fontSize = 16.sp, color = Blue600) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                supportingText = if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                    { Text("Las contraseñas no coinciden", color = Red600) }
                } else null
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFEBEE)) {
                    Text(error, color = Red600, modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onRegister(email, password, name, selectedRole) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && password == confirmPassword,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue800)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("¿Ya tienes cuenta? ", color = Gray600)
                TextButton(onClick = onNavigateToLogin) {
                    Text("Inicia sesión", color = Blue700, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun RoleChip(label: String, role: UserRole, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal) },
        leadingIcon = {
            Text(
                when (role) {
                    UserRole.STUDENT -> "🎓"
                    UserRole.COMPANY -> "🏢"
                    UserRole.ADMIN -> "⚙"
                },
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Blue50,
            selectedLabelColor = Blue800
        )
    )
}

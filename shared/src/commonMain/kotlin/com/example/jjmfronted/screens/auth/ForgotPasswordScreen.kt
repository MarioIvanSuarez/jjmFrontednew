package com.example.jjmfronted.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.ui.theme.*

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(Gray50)
    ) {
        TopAppBar(
            title = { Text("Recuperar contraseña") },
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

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!sent) {
                Text("*", fontSize = 64.sp, color = Blue600)
                Spacer(modifier = Modifier.height(16.dp))
                Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.headlineMedium, color = Gray900)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ingresa tu correo y te enviaremos un enlace para restablecerla.", color = Gray600, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Text("@", color = Blue600, fontSize = 16.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { sent = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue800)
                ) {
                    Text("Enviar enlace", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Text("📧", fontSize = 72.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Correo enviado", style = MaterialTheme.typography.headlineMedium, color = Gray900)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Si el correo existe, recibirás las instrucciones en breve.", color = Gray600, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Volver al inicio de sesión")
                }
            }
        }
    }
}

package com.example.jjmfronted.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jjmfronted.models.AppNotification
import com.example.jjmfronted.network.NotificationApi
import com.example.jjmfronted.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    var notifications by remember { mutableStateOf<List<AppNotification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val result = NotificationApi.getNotifications()
        result.fold(
            onSuccess = { notifications = it },
            onFailure = { isLoading = false }
        )
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().background(Gray50)) {
        TopAppBar(
            title = { Text("Notificaciones") },
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

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("\uD83D\uDD14", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No hay notificaciones", color = Gray600, fontSize = 16.sp)
                    Text("Las novedades aparecer\u00e1n aqu\u00ed", color = Gray400, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications.sortedByDescending { !it.isRead }) { notification ->
                    NotificationCard(
                        notification = notification,
                        onMarkRead = {
                            scope.launch {
                                NotificationApi.markAsRead(notification.id)
                            }
                            notifications = notifications.map {
                                if (it.id == notification.id) it.copy(isRead = true) else it
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: AppNotification, onMarkRead: () -> Unit) {
    val (emoji, color) = when (notification.type) {
        "VACANCY_NEW" -> "\uD83D\uDC4B" to Blue600
        "APPLICATION_UPDATE" -> "\uD83D\uDCCB" to Amber
        "MESSAGE" -> "\uD83D\uDCAC" to Cyan600
        "DOCUMENT" -> "\uD83D\uDCC4" to Green600
        "REMINDER" -> "\u23F0" to Red600
        else -> "\u2699\uFE0F" to Gray600
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Blue50.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 0.dp else 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                        color = Gray900,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Blue600))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(notification.message, color = Gray600, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(notification.createdAt.take(10), color = Gray400, fontSize = 11.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    if (!notification.isRead) {
                        TextButton(
                            onClick = onMarkRead,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Marcar le\u00edda", color = Blue700, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

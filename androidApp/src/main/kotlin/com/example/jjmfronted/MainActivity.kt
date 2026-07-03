package com.example.jjmfronted

import android.Manifest
import android.content.pm.PackageManager
import com.example.jjmfronted.network.ApiClient
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.jjmfronted.models.User
import com.example.jjmfronted.notifications.NotificationHelper
import com.example.jjmfronted.worker.SyncWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)
        scheduleBackgroundSync()
        requestNotificationPermission()

        setContent {
            App(
                onUserChanged = { user ->
                    user?.let {
                        saveUserId(it.id)
                        saveToken(ApiClient.getToken() ?: "")
                    }
                }
            )
        }
    }

    private fun saveUserId(userId: Int) {
        getSharedPreferences("jjm_prefs", MODE_PRIVATE)
            .edit()
            .putInt("user_id", userId)
            .apply()
    }

    private fun saveToken(token: String) {
        getSharedPreferences("jjm_prefs", MODE_PRIVATE)
            .edit()
            .putString("auth_token", token)
            .apply()
    }

    fun getUserId(): Int {
        return getSharedPreferences("jjm_prefs", MODE_PRIVATE)
            .getInt("user_id", -1)
    }

    private fun scheduleBackgroundSync() {
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "jjm_sync_vacantes",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
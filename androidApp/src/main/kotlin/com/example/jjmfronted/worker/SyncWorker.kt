package com.example.jjmfronted.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.notifications.NotificationHelper

class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val PREFS_NAME = "jjm_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_LAST_COUNT = "last_postulaciones_count"
    }

    override suspend fun doWork(): Result {
        val prefs: SharedPreferences = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = prefs.getString(KEY_TOKEN, null) ?: return Result.success()

        ApiClient.setToken(token)

        val result = ApiClient.getMisPostulaciones()
        result.fold(
            onSuccess = { postulaciones ->
                val previousCount = prefs.getInt(KEY_LAST_COUNT, 0)
                val currentCount = postulaciones.size

                if (currentCount > previousCount) {
                    val newCount = currentCount - previousCount
                    NotificationHelper.showNotification(
                        applicationContext,
                        "Novedades en tus postulaciones",
                        "Tienes $newCount actualización(es) en tus solicitudes"
                    )
                }

                prefs.edit().putInt(KEY_LAST_COUNT, currentCount).apply()
            },
            onFailure = { }
        )

        return Result.success()
    }
}
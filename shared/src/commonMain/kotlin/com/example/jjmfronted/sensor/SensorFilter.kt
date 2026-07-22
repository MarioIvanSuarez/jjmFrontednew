package com.example.jjmfronted.sensor

import kotlin.math.*

object SensorFilter {
    private const val MIN_ACCURACY = 50.0f
    private var lastLocation: LocationData? = null
    private const val MIN_DISTANCE_METERS = 10.0

    fun filter(raw: LocationData): LocationData? {
        if (raw.accuracy > MIN_ACCURACY && raw.accuracy != 0f) return null
        val last = lastLocation
        if (last != null) {
            val distance = haversine(last.latitude, last.longitude, raw.latitude, raw.longitude)
            if (distance < MIN_DISTANCE_METERS) return null
        }
        lastLocation = raw
        return raw
    }

    fun reset() {
        lastLocation = null
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0
        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(toRadians(lat1)) * cos(toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    private fun toRadians(deg: Double): Double = deg * PI / 180.0
}

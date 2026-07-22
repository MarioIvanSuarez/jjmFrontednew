package com.example.jjmfronted.sensor

class SensorRepository(
    private val gpsProvider: GpsProvider = SensorProvider.gps ?: throw IllegalStateException("GpsProvider not initialized")
) {
    private var onLocationCallback: ((LocationData) -> Unit)? = null

    fun startTracking(onLocation: (LocationData) -> Unit) {
        onLocationCallback = onLocation
        gpsProvider.startListening { raw ->
            val filtered = SensorFilter.filter(raw)
            if (filtered != null) {
                onLocationCallback?.invoke(filtered)
            }
        }
    }

    fun stopTracking() {
        gpsProvider.stopListening()
        onLocationCallback = null
    }

    fun isGpsAvailable(): Boolean = gpsProvider.isAvailable()

    fun requestPermissions() {
        gpsProvider.requestPermissions()
    }
}

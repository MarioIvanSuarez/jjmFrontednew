package com.example.jjmfronted.sensor

interface GpsProvider {
    fun startListening(onLocationChanged: (LocationData) -> Unit)
    fun stopListening()
    fun isAvailable(): Boolean
    fun requestPermissions()
}

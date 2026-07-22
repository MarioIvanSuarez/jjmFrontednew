package com.example.jjmfronted.sensor

class IosGpsProvider : GpsProvider {

    override fun startListening(onLocationChanged: (LocationData) -> Unit) {
        println("iOS GPS not yet implemented")
    }

    override fun stopListening() {}

    override fun isAvailable(): Boolean = false

    override fun requestPermissions() {}
}

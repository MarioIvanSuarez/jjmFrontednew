package com.example.jjmfronted.sensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat

class AndroidGpsProvider(private val context: Context) : GpsProvider {

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationListener: LocationListener? = null
    private var callback: ((LocationData) -> Unit)? = null

    override fun startListening(onLocationChanged: (LocationData) -> Unit) {
        callback = onLocationChanged
        if (!hasPermission()) return

        locationListener?.let { locationManager.removeUpdates(it) }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val data = LocationData(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy,
                    timestamp = location.time
                )
                callback?.invoke(data)
            }

            override fun onProviderDisabled(provider: String?) {}
            override fun onProviderEnabled(provider: String?) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                10f,
                locationListener!!
            )
        } catch (_: Exception) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000L,
                    10f,
                    locationListener!!
                )
            } catch (_: Exception) {}
        }
    }

    override fun stopListening() {
        locationListener?.let { locationManager.removeUpdates(it) }
        locationListener = null
        callback = null
    }

    override fun isAvailable(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun requestPermissions() {}

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

package com.example.jjmfronted.map

import androidx.compose.runtime.Composable

data class MapMarker(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String? = null
)

data class UserLocation(
    val latitude: Double,
    val longitude: Double
)

@Composable
expect fun InteractiveMap(
    markers: List<MapMarker>,
    initialLatitude: Double,
    initialLongitude: Double,
    onMapClick: (Double, Double) -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    userLocation: UserLocation? = null
)
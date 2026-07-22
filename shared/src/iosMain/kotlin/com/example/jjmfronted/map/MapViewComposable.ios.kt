package com.example.jjmfronted.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
actual fun InteractiveMap(
    markers: List<MapMarker>,
    initialLatitude: Double,
    initialLongitude: Double,
    onMapClick: (Double, Double) -> Unit,
    modifier: Modifier,
    userLocation: UserLocation? = null
) {
    Box(
        modifier = modifier.fillMaxSize().background(Color(0xFFE3F2FD)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "🗺 Mapa interactivo (iOS)\n$initialLatitude, $initialLongitude\n${markers.size} empresas",
            fontSize = 14.sp,
            color = Color(0xFF757575)
        )
    }
}
package com.example.jjmfronted.map

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
actual fun InteractiveMap(
    markers: List<MapMarker>,
    initialLatitude: Double,
    initialLongitude: Double,
    onMapClick: (Double, Double) -> Unit,
    modifier: Modifier,
    userLocation: UserLocation? = null,
    onMarkerClick: ((MapMarker) -> Unit)? = null
) {
    val startPosition = LatLng(initialLatitude, initialLongitude)
    var clickedPosition by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPosition, if (userLocation != null) 15f else 6f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = false
        ),
        onMapClick = { latLng ->
            clickedPosition = latLng
            onMapClick(latLng.latitude, latLng.longitude)
        }
    ) {
        markers.forEach { m ->
            val position = LatLng(m.latitude, m.longitude)
            Marker(
                state = MarkerState(position = position),
                title = m.name,
                snippet = m.description ?: "",
                onClick = {
                    onMarkerClick?.invoke(m)
                    false
                }
            )
        }

        clickedPosition?.let { pos ->
            Marker(
                state = MarkerState(position = pos),
                title = "Ubicación seleccionada",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }

        userLocation?.let { ul ->
            val position = LatLng(ul.latitude, ul.longitude)
            Circle(
                center = position,
                radius = 50.0,
                fillColor = 0x334285F4.toInt(),
                strokeColor = 0xFF4285F4.toInt(),
                strokeWidth = 3f
            )
            Marker(
                state = MarkerState(position = position),
                title = "Tu ubicación",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }
    }
}

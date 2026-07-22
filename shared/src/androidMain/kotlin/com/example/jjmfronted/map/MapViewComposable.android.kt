package com.example.jjmfronted.map

import android.graphics.Paint
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Circle
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay

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
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var clickedPosition by remember { mutableStateOf<GeoPoint?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = java.io.File(context.cacheDir, "osmdroid")
            osmdroidTileCache = java.io.File(context.cacheDir, "osmdroid/tiles")
        }
    }

    LaunchedEffect(markers, userLocation, clickedPosition) {
        val map = mapView ?: return@LaunchedEffect
        map.overlays.removeAll { it !is MapEventsOverlay && it !is ScaleBarOverlay }

        markers.forEach { m ->
            val marker = Marker(map)
            marker.position = GeoPoint(m.latitude, m.longitude)
            marker.title = m.name
            marker.snippet = m.description ?: ""
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.setOnMarkerClickListener { _, _ ->
                onMarkerClick?.invoke(m)
                true
            }
            map.overlays.add(marker)
        }

        clickedPosition?.let { pos ->
            val marker = Marker(map)
            marker.position = pos
            marker.title = "Ubicación seleccionada"
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = Marker.defaultMarker(Marker.STYLE_RED)
            map.overlays.add(marker)
        }

        userLocation?.let { ul ->
            val circle = Circle(map)
            circle.center = GeoPoint(ul.latitude, ul.longitude)
            circle.radius = 50.0
            circle.fillPaint = Paint().apply { color = 0x334285F4.toInt() }
            circle.outlinePaint = Paint().apply {
                color = 0xFF4285F4.toInt()
                strokeWidth = 3f
            }
            map.overlays.add(circle)

            val marker = Marker(map)
            marker.position = GeoPoint(ul.latitude, ul.longitude)
            marker.title = "Tu ubicación"
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            map.overlays.add(marker)

            if (clickedPosition == null) {
                map.controller.animateTo(GeoPoint(ul.latitude, ul.longitude))
            }
        }

        map.invalidate()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                Lifecycle.Event.ON_DESTROY -> {
                    mapView?.onDetach()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView?.onDetach()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                controller.setZoom(6.0)
                controller.setCenter(GeoPoint(initialLatitude, initialLongitude))

                val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                        clickedPosition = p
                        onMapClick(p.latitude, p.longitude)
                        return false
                    }
                    override fun longPressHelper(p: GeoPoint): Boolean = false
                })
                overlays.add(0, eventsOverlay)

                val scaleBar = ScaleBarOverlay(this)
                scaleBar.setAlignBottom(true)
                scaleBar.setAlignRight(true)
                overlays.add(scaleBar)

                onResume()
                mapView = this
            }
        }
    )
}

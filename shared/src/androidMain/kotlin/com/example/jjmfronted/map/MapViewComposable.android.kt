package com.example.jjmfronted.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.math.*

private const val TILE_SIZE = 256

private data class TileKey(val x: Int, val y: Int, val zoom: Int)

private val tileProviders = listOf(
    "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
    "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png",
    "https://b.tile.openstreetmap.org/{z}/{x}/{y}.png",
    "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}"
)

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
    var zoom by remember { mutableFloatStateOf(13f) }
    var centerLat by remember { mutableDoubleStateOf(initialLatitude) }
    var centerLng by remember { mutableDoubleStateOf(initialLongitude) }
    var viewSize by remember { mutableStateOf(Offset.Zero) }
    val tileCache = remember { mutableMapOf<TileKey, Bitmap>() }
    var loadErrors by remember { mutableIntStateOf(0) }
    var loadedTiles by remember { mutableIntStateOf(0) }

    val zoomInt = zoom.toInt().coerceIn(3, 19)

    val visibleTiles = remember(centerLat, centerLng, zoomInt, viewSize) {
        calcVisibleTiles(centerLat, centerLng, zoomInt, viewSize.x.toInt(), viewSize.y.toInt())
    }

    LaunchedEffect(visibleTiles) {
        loadErrors = 0
        loadedTiles = 0
        val total = visibleTiles.size
        visibleTiles.map { tile ->
            async(Dispatchers.IO) {
                val key = TileKey(tile.x, tile.y, tile.zoom)
                if (key in tileCache) {
                    loadedTiles++
                    return@async
                }
                var success = false
                for (provider in tileProviders) {
                    try {
                        val url = provider
                            .replace("{z}", tile.zoom.toString())
                            .replace("{x}", tile.x.toString())
                            .replace("{y}", tile.y.toString())
                        val bitmap = URL(url).openStream().use { BitmapFactory.decodeStream(it) }
                        if (bitmap != null) {
                            tileCache[key] = bitmap
                            loadedTiles++
                            success = true
                            return@async
                        }
                    } catch (_: Exception) { continue }
                }
                if (!success) loadErrors++
            }
        }.forEach { it.await() }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .background(Color(0xFFE8E8E8))
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, gestureZoom, _ ->
                    val newZoom = (zoom * gestureZoom).coerceIn(3f, 19f)
                    if (newZoom != zoom) {
                        val (latBefore, lngBefore) = screenToWorld(centroid.x, centroid.y, centerLat, centerLng, zoom, viewSize.x, viewSize.y)
                        zoom = newZoom
                        val (latAfter, lngAfter) = screenToWorld(centroid.x, centroid.y, centerLat, centerLng, zoom, viewSize.x, viewSize.y)
                        centerLat += latBefore - latAfter
                        centerLng += lngBefore - lngAfter
                    } else {
                        val (lat1, lng1) = screenToWorld(0f, 0f, centerLat, centerLng, zoom, viewSize.x, viewSize.y)
                        val (lat2, lng2) = screenToWorld(pan.x, pan.y, centerLat, centerLng, zoom, viewSize.x, viewSize.y)
                        centerLat += lat1 - lat2
                        centerLng += lng1 - lng2
                    }
                }
            }
            .pointerInput(markers) {
                detectTapGestures { tapOffset ->
                    var tappedId: Int? = null
                    for (m in markers) {
                        val (mx, my) = latLngToScreen(m.latitude, m.longitude, centerLat, centerLng, zoom, viewSize.x, viewSize.y)
                        if ((tapOffset - Offset(mx, my)).getDistance() < 30f) {
                            tappedId = m.id
                            break
                        }
                    }
                    if (tappedId != null) {
                        onMarkerClick?.invoke(markers.first { it.id == tappedId })
                    } else {
                        val (lat, lng) = screenToWorld(tapOffset.x, tapOffset.y, centerLat, centerLng, zoom, viewSize.x, viewSize.y)
                        onMapClick(lat, lng)
                    }
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { viewSize = Offset(it.width.toFloat(), it.height.toFloat()) }
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val worldSize = TILE_SIZE shl zoomInt
            val centerWorldX = ((centerLng + 180.0) / 360.0 * worldSize)
            val centerWorldY = latToWorldY(centerLat, worldSize)

            visibleTiles.forEach { tile ->
                val bitmap = tileCache[TileKey(tile.x, tile.y, tile.zoom)]
                if (bitmap != null) {
                    val tileWorldX = tile.x * TILE_SIZE
                    val tileWorldY = tile.y * TILE_SIZE
                    val screenX = centerX + (tileWorldX - centerWorldX).toFloat()
                    val screenY = centerY + (tileWorldY - centerWorldY).toFloat()
                    drawImage(bitmap.asImageBitmap(), topLeft = Offset(screenX, screenY))
                }
            }

            userLocation?.let { ul ->
                val (sx, sy) = latLngToScreen(ul.latitude, ul.longitude, centerLat, centerLng, zoom, viewSize.x, viewSize.y)
                drawCircle(Color(0xFF4285F4), radius = 12f, center = Offset(sx, sy))
                drawCircle(Color(0x334285F4), radius = 28f, center = Offset(sx, sy))
            }
        }

        // Debug overlay
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(4.dp)
                .background(Color(0xCCFFFFFF), shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                "Zoom: ${zoom.toInt()} | Tiles: $loadedTiles/${visibleTiles.size} | Err: $loadErrors",
                fontSize = 10.sp,
                color = Color(0xFF616161)
            )
        }

        markers.forEach { m ->
            val (sx, sy) = latLngToScreen(m.latitude, m.longitude, centerLat, centerLng, zoom, viewSize.x, viewSize.y)
            Box(
                modifier = Modifier
                    .offset { IntOffset((sx - 12).toInt(), (sy - 36).toInt()) }
                    .width(24.dp)
                    .height(36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFE53935), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📍", fontSize = 14.sp)
                }
            }
        }
    }
}

// --- Web Mercator coordinate math ---

private fun latToWorldY(lat: Double, worldSize: Int): Double {
    val sinLat = sin(lat * PI / 180)
    return (1.0 - ln((1 + sinLat) / (1 - sinLat)) / (2 * PI)) / 2 * worldSize
}

private fun worldYToLat(worldY: Double, worldSize: Int): Double {
    val y = worldY / worldSize
    return (atan(sinh(PI * (1 - 2 * y)))) * 180 / PI
}

private fun screenToWorld(
    screenX: Float, screenY: Float,
    centerLat: Double, centerLng: Double,
    zoom: Float, viewW: Float, viewH: Float
): Pair<Double, Double> {
    val zi = zoom.toInt().coerceIn(3, 19)
    val ws = TILE_SIZE shl zi
    val cx = (centerLng + 180.0) / 360.0 * ws
    val cy = latToWorldY(centerLat, ws)
    val wx = cx + (screenX - viewW / 2)
    val wy = cy + (screenY - viewH / 2)
    val lng = wx / ws * 360.0 - 180.0
    val lat = worldYToLat(wy, ws)
    return Pair(lat, lng)
}

private fun latLngToScreen(
    lat: Double, lng: Double,
    centerLat: Double, centerLng: Double,
    zoom: Float, viewW: Float, viewH: Float
): Pair<Float, Float> {
    val zi = zoom.toInt().coerceIn(3, 19)
    val ws = TILE_SIZE shl zi
    val cx = (centerLng + 180.0) / 360.0 * ws
    val cy = latToWorldY(centerLat, ws)
    val tx = (lng + 180.0) / 360.0 * ws
    val ty = latToWorldY(lat, ws)
    return Pair((viewW / 2 + (tx - cx)).toFloat(), (viewH / 2 + (ty - cy)).toFloat())
}

private data class Tile(val x: Int, val y: Int, val zoom: Int)

private fun calcVisibleTiles(centerLat: Double, centerLng: Double, zoom: Int, viewW: Int, viewH: Int): List<Tile> {
    if (viewW <= 0 || viewH <= 0) return emptyList()
    val ws = TILE_SIZE shl zoom
    val cx = ((centerLng + 180.0) / 360.0 * ws).toInt()
    val cy = latToWorldY(centerLat, ws).toInt()
    val minTx = (cx - viewW / 2) / TILE_SIZE
    val maxTx = (cx + viewW / 2) / TILE_SIZE
    val minTy = (cy - viewH / 2) / TILE_SIZE
    val maxTy = (cy + viewH / 2) / TILE_SIZE
    val nt = 1 shl zoom

    return (minTx..maxTx).flatMap { tx ->
        (minTy..maxTy).map { ty ->
            Tile(((tx % nt) + nt) % nt, ((ty % nt) + nt) % nt, zoom)
        }
    }
}

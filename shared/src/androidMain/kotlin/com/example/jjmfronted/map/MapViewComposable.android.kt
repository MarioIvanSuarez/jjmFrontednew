package com.example.jjmfronted.map

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class MapJsBridge(
    private val onMapClick: (Double, Double) -> Unit
) {
    @JavascriptInterface
    fun onMapClicked(lat: String, lng: String) {
        val latitude = lat.toDoubleOrNull() ?: return
        val longitude = lng.toDoubleOrNull() ?: return
        onMapClick(latitude, longitude)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun InteractiveMap(
    markers: List<MapMarker>,
    initialLatitude: Double,
    initialLongitude: Double,
    onMapClick: (Double, Double) -> Unit,
    modifier: Modifier
) {
    val bridge = remember { MapJsBridge(onMapClick) }

    val markersJson = remember(markers) {
        markers.joinToString(",") { m ->
            """{id:${m.id},name:"${m.name.replace("\"","\\\"")}",lat:${m.latitude},lng:${m.longitude},desc:"${(m.description?:"").replace("\"","\\\"")}"}"""
        }
    }

    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body { margin:0; padding:0; }
                #map { width:100vw; height:100vh; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([$initialLatitude, $initialLongitude], 6);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 18,
                    attribution: '© OpenStreetMap'
                }).addTo(map);

                var markers = [$markersJson];
                markers.forEach(function(m) {
                    L.marker([m.lat, m.lng])
                        .addTo(map)
                        .bindPopup('<b>' + m.name + '</b><br>' + (m.desc || ''));
                });

                var marker = null;
                map.on('click', function(e) {
                    if (marker) map.removeLayer(marker);
                    marker = L.marker([e.latlng.lat, e.latlng.lng]).addTo(map);
                    if (window.MapBridge) {
                        window.MapBridge.onMapClicked(e.latlng.lat.toString(), e.latlng.lng.toString());
                    }
                });
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = false
                addJavascriptInterface(bridge, "MapBridge")
                webViewClient = WebViewClient()
                loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            }
        }
    )
}
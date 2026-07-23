package com.example.jjmfronted.map

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private class MapJsBridge {
    @Volatile
    var onMapClick: ((Double, Double) -> Unit)? = null

    @Volatile
    var onMarkerClickId: ((Int) -> Unit)? = null

    @JavascriptInterface
    fun onMapClicked(lat: String, lng: String) {
        val latitude = lat.toDoubleOrNull() ?: return
        val longitude = lng.toDoubleOrNull() ?: return
        onMapClick?.invoke(latitude, longitude)
    }

    @JavascriptInterface
    fun onMarkerClicked(id: String) {
        val markerId = id.toIntOrNull() ?: return
        onMarkerClickId?.invoke(markerId)
    }
}

@SuppressLint("SetJavaScriptEnabled")
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
    val bridge = remember { MapJsBridge() }
    val markerMap = remember(markers) { markers.associateBy { it.id } }
    var webView by remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(onMapClick, markerMap, onMarkerClick) {
        bridge.onMapClick = onMapClick
        bridge.onMarkerClickId = { id ->
            markerMap[id]?.let { onMarkerClick?.invoke(it) }
        }
    }

    LaunchedEffect(markers, userLocation) {
        if (webView != null) {
            val markersArr = markers.joinToString(",") { m ->
                """{"id":${m.id},"name":"${m.name.replace("\"","\\\"").replace("'","\\'")}","lat":${m.latitude},"lng":${m.longitude},"desc":"${(m.description?:"").replace("\"","\\\"").replace("'","\\'")}"}"""
            }
            val userPos = if (userLocation != null) """{"lat":${userLocation.latitude},"lng":${userLocation.longitude}}""" else "null"
            webView?.evaluateJavascript("updateMap([$markersArr], $userPos);", null)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = false
                settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                addJavascriptInterface(bridge, "MapBridge")
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(msg: android.webkit.ConsoleMessage): Boolean {
                        android.util.Log.d("MapWebView", "${msg.message()} (${msg.lineNumber()})")
                        return true
                    }
                }
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)
                        val markersArr = markers.joinToString(",") { m ->
                            """{"id":${m.id},"name":"${m.name.replace("\"","\\\"").replace("'","\\'")}","lat":${m.latitude},"lng":${m.longitude},"desc":"${(m.description?:"").replace("\"","\\\"").replace("'","\\'")}"}"""
                        }
                        val userPos = if (userLocation != null) """{"lat":${userLocation.latitude},"lng":${userLocation.longitude}}""" else "null"
                        view.evaluateJavascript("updateMap([$markersArr], $userPos);", null)
                    }
                }
                val html = generateMapHtml(initialLatitude, initialLongitude)
                loadDataWithBaseURL("https://unpkg.com/", html, "text/html", "UTF-8", null)
                webView = this
            }
        }
    )
}

private fun generateMapHtml(initialLatitude: Double, initialLongitude: Double): String {
    return """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <style>
        * { margin:0; padding:0; }
        html, body { width:100%; height:100%; background:#f5f5f5; }
        #map { width:100%; height:100%; }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        var map = L.map('map', { zoomControl: true }).setView([$initialLatitude, $initialLongitude], 6);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; OpenStreetMap'
        }).addTo(map);

        var vacancyMarkers = [];
        var clickedMarker = null;
        var userCircle = null;
        var userMarker = null;

        function updateMap(markersArr, userLoc) {
            vacancyMarkers.forEach(function(m) { map.removeLayer(m); });
            vacancyMarkers = [];

            if (clickedMarker) { map.removeLayer(clickedMarker); clickedMarker = null; }
            if (userCircle) { map.removeLayer(userCircle); userCircle = null; }
            if (userMarker) { map.removeLayer(userMarker); userMarker = null; }

            if (markersArr && markersArr.length > 0) {
                markersArr.forEach(function(m) {
                    var marker = L.marker([m.lat, m.lng])
                        .addTo(map)
                        .bindPopup('<b>' + m.name + '</b><br/>' + (m.desc || ''))
                        .on('click', function() {
                            if (window.MapBridge) {
                                window.MapBridge.onMarkerClicked(String(m.id));
                            }
                        });
                    vacancyMarkers.push(marker);
                });
                var group = L.featureGroup(vacancyMarkers);
                map.fitBounds(group.getBounds().pad(0.1));
            }

            if (userLoc) {
                userCircle = L.circle([userLoc.lat, userLoc.lng], {
                    radius: 50,
                    color: '#4285F4',
                    fillColor: '#4285F4',
                    fillOpacity: 0.2,
                    weight: 3
                }).addTo(map);
                userMarker = L.marker([userLoc.lat, userLoc.lng], {
                    icon: L.divIcon({
                        className: '',
                        html: '<div style="background:#4285F4;border:3px solid white;border-radius:50%;width:20px;height:20px;box-shadow:0 0 4px rgba(0,0,0,0.3);"></div>',
                        iconSize: [26, 26],
                        iconAnchor: [13, 13]
                    })
                }).addTo(map).bindPopup('<b>Tu ubicaci\u00f3n</b>');
                map.setView([userLoc.lat, userLoc.lng], Math.max(map.getZoom(), 13));
            }
        }

        map.on('click', function(e) {
            if (clickedMarker) map.removeLayer(clickedMarker);
            clickedMarker = L.marker([e.latlng.lat, e.latlng.lng]).addTo(map);
            if (window.MapBridge) {
                window.MapBridge.onMapClicked(String(e.latlng.lat), String(e.latlng.lng));
            }
        });
    </script>
</body>
</html>
""".trimIndent()
}

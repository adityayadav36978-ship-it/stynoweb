package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.repository.NearbyPlaceCategory
import com.example.data.repository.RealNearbyPlace
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoAmber
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

@Composable
fun StynoInteractiveMap(
    initialLatitude: Double,
    initialLongitude: Double,
    nearbyPlaces: List<RealNearbyPlace> = emptyList(),
    isLiveGps: Boolean = true,
    isDraggable: Boolean = true,
    onLocationChanged: (Double, Double) -> Unit = { _, _ -> },
    onRecenterGps: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var centerLat by remember(initialLatitude) { mutableDoubleStateOf(initialLatitude) }
    var centerLng by remember(initialLongitude) { mutableDoubleStateOf(initialLongitude) }
    var zoomLevel by remember { mutableFloatStateOf(15.0f) }
    var isReverseGeocoding by remember { mutableStateOf(false) }
    var selectedPoi by remember { mutableStateOf<RealNearbyPlace?>(null) }
    var isMapMoving by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    // Converts Lat/Lng to Slippy Map Tile numbers
    val n = 2.0.pow(zoomLevel.toInt().toDouble())
    val tileX = ((centerLng + 180.0) / 360.0 * n).toInt()
    val latRad = Math.toRadians(centerLat)
    val tileY = ((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * n).toInt()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE8ECEF))
    ) {
        // 1. Interactive Map Canvas with Real Map Tile Rendering
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isDraggable) {
                    if (isDraggable) {
                        detectDragGestures(
                            onDragStart = {
                                isMapMoving = true
                            },
                            onDragEnd = {
                                isMapMoving = false
                                debounceJob?.cancel()
                                debounceJob = coroutineScope.launch {
                                    delay(400)
                                    onLocationChanged(centerLat, centerLng)
                                }
                            },
                            onDragCancel = {
                                isMapMoving = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                // Drag delta to degrees conversion based on zoom
                                val latFactor = 0.000008 * 2.0.pow(16 - zoomLevel.toDouble())
                                val lngFactor = 0.000008 * 2.0.pow(16 - zoomLevel.toDouble())
                                centerLat += dragAmount.y * latFactor
                                centerLng -= dragAmount.x * lngFactor
                            }
                        )
                    }
                }
        ) {
            // Render 3x3 OSM Raster Tiles dynamically
            val currentZoom = zoomLevel.toInt().coerceIn(10, 18)
            Column(modifier = Modifier.fillMaxSize()) {
                for (dy in -1..1) {
                    Row(modifier = Modifier.weight(1f)) {
                        for (dx in -1..1) {
                            val curTileX = (tileX + dx).coerceAtLeast(0)
                            val curTileY = (tileY + dy).coerceAtLeast(0)
                            val tileUrl = "https://tile.openstreetmap.org/$currentZoom/$curTileX/$curTileY.png"

                            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(tileUrl)
                                        .addHeader("User-Agent", "StynoStayApp/1.0 (contact: support@styno.com)")
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Map Tile",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Stylized Map Grid & Accuracy Overlay Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val centerOffset = Offset(canvasWidth / 2f, canvasHeight / 2f)

                // GPS Accuracy radius circle
                drawCircle(
                    color = StynoBluePrimary.copy(alpha = 0.12f),
                    radius = 80f,
                    center = centerOffset
                )
                drawCircle(
                    color = StynoBluePrimary.copy(alpha = 0.4f),
                    radius = 80f,
                    center = centerOffset,
                    style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                )

                // Render Nearby Places Markers on Map relative to center
                nearbyPlaces.take(8).forEach { place ->
                    val dLat = place.latitude - centerLat
                    val dLng = place.longitude - centerLng
                    val scaleFactor = 2.0.pow(zoomLevel.toDouble()) * 300.0
                    val offsetX = (dLng * scaleFactor).toFloat()
                    val offsetY = -(dLat * scaleFactor).toFloat()

                    val poiPos = Offset(centerOffset.x + offsetX, centerOffset.y + offsetY)

                    if (poiPos.x in 0f..canvasWidth && poiPos.y in 0f..canvasHeight) {
                        val poiColor = when (place.category) {
                            NearbyPlaceCategory.TRANSIT_RAILWAY, NearbyPlaceCategory.TRANSIT_BUS, NearbyPlaceCategory.TRANSIT_AIRPORT -> Color(0xFF0288D1)
                            NearbyPlaceCategory.EDUCATION_COLLEGE, NearbyPlaceCategory.EDUCATION_SCHOOL -> Color(0xFF7B1FA2)
                            NearbyPlaceCategory.HEALTHCARE, NearbyPlaceCategory.PHARMACY -> Color(0xFFD32F2F)
                            NearbyPlaceCategory.MARKET, NearbyPlaceCategory.GROCERY, NearbyPlaceCategory.RESTAURANT, NearbyPlaceCategory.SHOPPING_MALL, NearbyPlaceCategory.FAMOUS_SHOPS -> Color(0xFFF57C00)
                            NearbyPlaceCategory.HOTEL_HOSTEL, NearbyPlaceCategory.PARK_NATURE -> Color(0xFF2E7D32)
                            NearbyPlaceCategory.TOURIST_LANDMARK, NearbyPlaceCategory.WORSHIP_TEMPLE, NearbyPlaceCategory.WORSHIP_MOSQUE, NearbyPlaceCategory.WORSHIP_CHURCH -> Color(0xFF8E24AA)
                            else -> Color(0xFF455A64)
                        }

                        drawCircle(
                            color = poiColor.copy(alpha = 0.25f),
                            radius = 18f,
                            center = poiPos
                        )
                        drawCircle(
                            color = poiColor,
                            radius = 9f,
                            center = poiPos
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 4f,
                            center = poiPos
                        )
                    }
                }
            }

            // Center Pin Indicator (Interactive Marker)
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = if (isMapMoving) (-12).dp else 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isLiveGps) StynoBluePrimary else StynoAccent,
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isLiveGps) Icons.Default.GpsFixed else Icons.Default.Place,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isMapMoving) "Moving Pin..." else if (isLiveGps) "Live GPS Pin" else "Selected Location",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Pin Icon
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Target Location Pin",
                        tint = if (isLiveGps) StynoBluePrimary else StynoAccent,
                        modifier = Modifier.size(42.dp)
                    )

                    // Pin Base Shadow
                    Box(
                        modifier = Modifier
                            .size(width = 12.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                }
            }
        }

        // Top Status Pill
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isLiveGps) StynoEmerald else StynoAmber)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Lat: %.4f • Lng: %.4f".format(centerLat, centerLng),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isDraggable) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Drag map to move pin",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Map Control Floating Action Buttons (Zoom & GPS Recenter)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Recenter GPS Button
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable {
                        centerLat = initialLatitude
                        centerLng = initialLongitude
                        onRecenterGps()
                        onLocationChanged(initialLatitude, initialLongitude)
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "Recenter My Location",
                        tint = StynoBluePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Zoom In (+)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (zoomLevel < 18f) zoomLevel += 1f
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom In",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Zoom Out (-)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (zoomLevel > 10f) zoomLevel -= 1f
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom Out",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Bottom Map Attributions / OpenStreetMap info
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "© OpenStreetMap contributors",
                fontSize = 9.sp,
                color = Color.DarkGray
            )
        }
    }
}

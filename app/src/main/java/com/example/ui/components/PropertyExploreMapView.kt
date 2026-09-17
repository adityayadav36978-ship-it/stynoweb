package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.DurationType
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.StynoViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * Enhanced Google Map View displaying Search Results as rich interactive Markers.
 *
 * Built using the official `maps-compose` library with:
 * - Real-time dynamic markers for all search results with price badges, ratings, and stay icons
 * - Synchronized horizontal search result card carousel linked to map markers
 * - Interactive marker selection with smooth camera centering animations
 * - Live filter chips (Hostels, Hotels, PGs, Flats, Quick Stays, Verified) directly on the map
 * - Layer switcher (Normal, Satellite, Terrain, Hybrid)
 * - "Fit All Results" (LatLngBounds auto-framing) and "My Location" GPS support
 * - Search query header with results counter and switch to list view
 */
@Composable
fun PropertyExploreMapView(
    properties: List<Property>,
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier,
    onPropertySelected: (Property) -> Unit,
    onSwitchToListView: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val verifiedOnly by viewModel.verifiedOnly.collectAsStateWithLifecycle()

    // Selected Property for active marker highlight and bottom carousel sync
    var selectedProperty by remember { mutableStateOf<Property?>(properties.firstOrNull()) }
    val carouselListState = rememberLazyListState()

    // Map configuration & camera
    val defaultCenter = remember(properties) {
        if (properties.isNotEmpty()) {
            LatLng(properties.first().latitude, properties.first().longitude)
        } else {
            LatLng(28.6139, 77.2090) // Delhi NCR center
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultCenter, 12.5f)
    }

    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var isClusteringEnabled by remember { mutableStateOf(true) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasLocationPermission) {
            viewModel.fetchCurrentLocation()
        }
    }

    // Auto-frame all search result properties when the search results change
    LaunchedEffect(properties) {
        if (properties.isNotEmpty()) {
            if (properties.size == 1) {
                val singleProp = properties.first()
                val target = LatLng(singleProp.latitude, singleProp.longitude)
                cameraPositionState.safeAnimate(
                    updateProvider = { CameraUpdateFactory.newLatLngZoom(target, 14.5f) },
                    fallbackTarget = target,
                    fallbackZoom = 14.5f,
                    durationMs = 700
                )
                selectedProperty = singleProp
            } else {
                val builder = LatLngBounds.builder()
                properties.forEach {
                    builder.include(LatLng(it.latitude, it.longitude))
                }
                cameraPositionState.safeAnimateBounds(
                    boundsBuilder = builder,
                    padding = 120,
                    fallbackTarget = defaultCenter,
                    fallbackZoom = 12f,
                    durationMs = 700
                )
            }
        }
    }

    // When selectedProperty changes, scroll the carousel to that property
    LaunchedEffect(selectedProperty) {
        selectedProperty?.let { prop ->
            val index = properties.indexOfFirst { it.id == prop.id }
            if (index >= 0) {
                carouselListState.animateScrollToItem(index)
            }
        }
    }

    // City quick jump centers
    val cityCenters = remember {
        listOf(
            "All Cities" to null,
            "Delhi NCR" to LatLng(28.5355, 77.3910),
            "Bengaluru" to LatLng(12.9716, 77.5946),
            "Mumbai" to LatLng(19.0760, 72.8777),
            "Pune" to LatLng(18.5204, 73.8567),
            "Kota" to LatLng(25.1388, 75.8362),
            "Hyderabad" to LatLng(17.3850, 78.4867),
            "Patna" to LatLng(25.5941, 85.1376),
            "Darbhanga" to LatLng(26.1542, 85.8918)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("property_explore_map_view")
    ) {
        // 1. Full-Screen Google Map using maps-compose with SDK initialization check
        GoogleMapContainer(
            modifier = Modifier.fillMaxSize(),
            fallbackTitle = "Explore Properties Map",
            fallbackSubtitle = "Displaying ${properties.size} properties in your selected locality",
            fallbackCoordinates = properties.firstOrNull()?.let { Pair(it.latitude, it.longitude) }
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapType = mapType,
                    isMyLocationEnabled = hasLocationPermission
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    myLocationButtonEnabled = false,
                    rotationGesturesEnabled = true,
                    scrollGesturesEnabled = true,
                    tiltGesturesEnabled = true,
                    zoomGesturesEnabled = true
                ),
                onMapClick = { latLng ->
                    GoogleMapsManager.logLifecycleEvent(
                        mapId = "PropertyExploreMap",
                        event = MapLifecycleEvent.MAP_CLICKED,
                        details = "Coordinates: ${latLng.latitude}, ${latLng.longitude}"
                    )
                }
            ) {
                // Efficient Google Maps Marker Clustering for high-density listing areas
                if (isClusteringEnabled && properties.size > 1) {
                    val clusterItems = remember(properties) {
                        properties.map { PropertyClusterItem(it) }
                    }

                    Clustering(
                        items = clusterItems,
                        onClusterClick = { cluster ->
                            GoogleMapsManager.logMarkerInteraction(
                                mapId = "PropertyExploreMap",
                                markerId = "cluster_${cluster.position.latitude}_${cluster.position.longitude}",
                                title = "${cluster.size} clustered listings",
                                latitude = cluster.position.latitude,
                                longitude = cluster.position.longitude
                            )
                            val clusterItemList = cluster.items.toList()
                            if (clusterItemList.isNotEmpty()) {
                                val firstProperty = clusterItemList.first().property
                                selectedProperty = firstProperty
                                val firstIndex = properties.indexOfFirst { it.id == firstProperty.id }
                                if (firstIndex >= 0) {
                                    coroutineScope.launch {
                                        carouselListState.animateScrollToItem(firstIndex)
                                    }
                                }

                                val firstPos = clusterItemList.first().position
                                val allSameLocation = clusterItemList.all {
                                    Math.abs(it.position.latitude - firstPos.latitude) < 0.0001 &&
                                    Math.abs(it.position.longitude - firstPos.longitude) < 0.0001
                                }

                                coroutineScope.launch {
                                    if (allSameLocation) {
                                        val targetZoom = (cameraPositionState.position.zoom + 2f).coerceAtMost(18f)
                                        cameraPositionState.safeAnimate(
                                            updateProvider = {
                                                CameraUpdateFactory.newLatLngZoom(firstPos, targetZoom)
                                            },
                                            fallbackTarget = firstPos,
                                            fallbackZoom = targetZoom,
                                            durationMs = 450
                                        )
                                    } else {
                                        val builder = LatLngBounds.builder()
                                        clusterItemList.forEach { builder.include(it.position) }
                                        cameraPositionState.safeAnimateBounds(
                                            boundsBuilder = builder,
                                            padding = 130,
                                            fallbackTarget = cluster.position,
                                            fallbackZoom = (cameraPositionState.position.zoom + 2f).coerceAtMost(18f),
                                            durationMs = 450
                                        )
                                    }
                                }
                            }
                            true
                        },
                        onClusterItemClick = { item ->
                            val property = item.property
                            GoogleMapsManager.logMarkerInteraction(
                                mapId = "PropertyExploreMap",
                                markerId = property.id,
                                title = property.name,
                                latitude = property.latitude,
                                longitude = property.longitude
                            )
                            selectedProperty = property
                            val index = properties.indexOfFirst { it.id == property.id }
                            coroutineScope.launch {
                                cameraPositionState.safeAnimate(
                                    updateProvider = { CameraUpdateFactory.newLatLngZoom(item.position, 15.5f) },
                                    fallbackTarget = item.position,
                                    fallbackZoom = 15.5f,
                                    durationMs = 450
                                )
                                if (index >= 0) {
                                    carouselListState.animateScrollToItem(index)
                                }
                            }
                            true
                        },
                        clusterContent = { cluster ->
                            PropertyClusterMarker(cluster = cluster)
                        },
                        clusterItemContent = { item ->
                            val isSelected = selectedProperty?.id == item.property.id
                            PropertyMapPriceMarker(
                                property = item.property,
                                isSelected = isSelected
                            )
                        }
                    )
                } else {
                    // Fallback individual markers when clustering is disabled or single item
                    properties.forEachIndexed { index, property ->
                        val isSelected = selectedProperty?.id == property.id
                        val latLng = LatLng(property.latitude, property.longitude)

                        MarkerComposable(
                            state = MarkerState(position = latLng),
                            title = property.name,
                            snippet = "₹${property.startingPrice.toInt()} • ${property.propertyType.displayName}",
                            onClick = {
                                GoogleMapsManager.logMarkerInteraction(
                                    mapId = "PropertyExploreMap",
                                    markerId = property.id,
                                    title = property.name,
                                    latitude = property.latitude,
                                    longitude = property.longitude
                                )
                                selectedProperty = property
                                coroutineScope.launch {
                                    cameraPositionState.safeAnimate(
                                        updateProvider = { CameraUpdateFactory.newLatLngZoom(latLng, 14.5f) },
                                        fallbackTarget = latLng,
                                        fallbackZoom = 14.5f,
                                        durationMs = 500
                                    )
                                    carouselListState.animateScrollToItem(index)
                                }
                                true
                            }
                        ) {
                            PropertyMapPriceMarker(
                                property = property,
                                isSelected = isSelected
                            )
                        }
                    }
                }
            }
        }

        // 2. Top Floating Controls (Search Context, Result Counter, Quick Filters & City Selector)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 10.dp, start = 12.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // View Mode & Result Counter Header Bar
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = StynoBluePrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Search Results on Map",
                                    tint = StynoBluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "${properties.size} Stays for \"$searchQuery\""
                                } else {
                                    "${properties.size} Stays on Map"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isClusteringEnabled && properties.size > 1) {
                                    "Google Maps • Clustering active in dense areas"
                                } else {
                                    "Google Maps • Tap marker to view details"
                                },
                                fontSize = 10.sp,
                                color = if (isClusteringEnabled && properties.size > 1) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isClusteringEnabled && properties.size > 1) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }

                    // Switch to List View Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSwitchToListView() }
                            .testTag("switch_to_list_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "List View",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "List View",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Quick Category & Filter Chips directly on top of Google Map
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // All Filter Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedCategory == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectCategory(null) }
                ) {
                    Text(
                        text = "All (${properties.size})",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedCategory == null) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Property Types Filter Chips
                PropertyType.entries.forEach { type ->
                    val isSelected = selectedCategory == type
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.selectCategory(if (isSelected) null else type)
                            }
                    ) {
                        Text(
                            text = type.displayName,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Verified Only Filter Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (verifiedOnly) StynoEmerald else MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.setVerifiedOnly(!verifiedOnly) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (verifiedOnly) Color.White else StynoEmerald,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Verified",
                            fontSize = 11.sp,
                            fontWeight = if (verifiedOnly) FontWeight.Bold else FontWeight.Medium,
                            color = if (verifiedOnly) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // High-Density Marker Clustering Toggle Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isClusteringEnabled) StynoBluePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isClusteringEnabled) StynoBluePrimary else Color.Transparent
                    ),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isClusteringEnabled = !isClusteringEnabled }
                        .testTag("clustering_chip_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = if (isClusteringEnabled) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isClusteringEnabled) "Clustered" else "All Pins",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isClusteringEnabled) StynoBluePrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // City Filter Chips
                cityCenters.forEach { (cityName, centerLatLng) ->
                    val isCitySelected = selectedCity.equals(cityName, ignoreCase = true) || (cityName == "All Cities" && selectedCity == "All Cities")
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCitySelected) StynoBluePrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.selectCity(if (cityName == "All Cities") "All Cities" else cityName)
                                if (centerLatLng != null) {
                                    coroutineScope.launch {
                                        cameraPositionState.safeAnimate(
                                            updateProvider = { CameraUpdateFactory.newLatLngZoom(centerLatLng, 12.5f) },
                                            fallbackTarget = centerLatLng,
                                            fallbackZoom = 12.5f,
                                            durationMs = 600
                                        )
                                    }
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = if (isCitySelected) Color.White else StynoBluePrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = cityName,
                                fontSize = 11.sp,
                                fontWeight = if (isCitySelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCitySelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 3. Floating Right Map Controls (Map Type, Fit All Bounds, My Location, Zoom In/Out)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Map Type Cycle Toggle (Normal -> Hybrid -> Satellite -> Terrain -> Normal)
            FloatingActionButton(
                onClick = {
                    mapType = when (mapType) {
                        MapType.NORMAL -> MapType.HYBRID
                        MapType.HYBRID -> MapType.TERRAIN
                        MapType.TERRAIN -> MapType.NORMAL
                        else -> MapType.NORMAL
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Map Style: ${mapType.name}",
                    modifier = Modifier.size(18.dp)
                )
            }

            // High-Density Marker Clustering Toggle FAB
            FloatingActionButton(
                onClick = { isClusteringEnabled = !isClusteringEnabled },
                shape = CircleShape,
                containerColor = if (isClusteringEnabled) StynoBluePrimary else MaterialTheme.colorScheme.surface,
                contentColor = if (isClusteringEnabled) Color.White else MaterialTheme.colorScheme.onSurface,
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                modifier = Modifier
                    .size(40.dp)
                    .testTag("clustering_toggle_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.LocationCity,
                    contentDescription = if (isClusteringEnabled) "Clustering: ON" else "Clustering: OFF",
                    modifier = Modifier.size(18.dp)
                )
            }

            // Fit All Search Results Markers Bounds
            if (properties.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        if (properties.size == 1) {
                            coroutineScope.launch {
                                val singleProp = properties.first()
                                val target = LatLng(singleProp.latitude, singleProp.longitude)
                                cameraPositionState.safeAnimate(
                                    updateProvider = { CameraUpdateFactory.newLatLngZoom(target, 14.5f) },
                                    fallbackTarget = target,
                                    fallbackZoom = 14.5f,
                                    durationMs = 600
                                )
                            }
                        } else {
                            val builder = LatLngBounds.builder()
                            properties.forEach { builder.include(LatLng(it.latitude, it.longitude)) }
                            coroutineScope.launch {
                                cameraPositionState.safeAnimateBounds(
                                    boundsBuilder = builder,
                                    padding = 120,
                                    fallbackTarget = defaultCenter,
                                    fallbackZoom = 12f,
                                    durationMs = 600
                                )
                            }
                        }
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = StynoBluePrimary,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = "Fit All Results",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // My Location
            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission) {
                        viewModel.fetchCurrentLocation()
                        val coords = viewModel.userCoordinates.value
                        if (coords != null) {
                            coroutineScope.launch {
                                val userTarget = LatLng(coords.latitude, coords.longitude)
                                cameraPositionState.safeAnimate(
                                    updateProvider = { CameraUpdateFactory.newLatLngZoom(userTarget, 14.5f) },
                                    fallbackTarget = userTarget,
                                    fallbackZoom = 14.5f,
                                    durationMs = 600
                                )
                            }
                        }
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "My Location",
                    modifier = Modifier.size(18.dp)
                )
            }

            // Zoom In
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        cameraPositionState.safeZoomIn(250)
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Zoom In",
                    modifier = Modifier.size(18.dp)
                )
            }

            // Zoom Out
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        cameraPositionState.safeZoomOut(250)
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Zoom Out",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 4. Empty State Floating Card when 0 Search Results match
        if (properties.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No Stays Found on Map",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No stays matched \"$searchQuery\"" else "Try adjusting your filters or selecting another city.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.resetFilters() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reset Filters", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 5. Bottom Horizontal Search Result Cards Carousel
        if (properties.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                LazyRow(
                    state = carouselListState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(properties) { index, prop ->
                        val isSelected = selectedProperty?.id == prop.id
                        PropertyMapCarouselCard(
                            property = prop,
                            isSelected = isSelected,
                            viewModel = viewModel,
                            onCardClick = {
                                selectedProperty = prop
                                val latLng = LatLng(prop.latitude, prop.longitude)
                                coroutineScope.launch {
                                    cameraPositionState.safeAnimate(
                                        updateProvider = { CameraUpdateFactory.newLatLngZoom(latLng, 14.5f) },
                                        fallbackTarget = latLng,
                                        fallbackZoom = 14.5f,
                                        durationMs = 500
                                    )
                                }
                            },
                            onDetailsClick = { onPropertySelected(prop) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Styled Pin with Price, Category Icon & Rating for Google Map Markers
 */
@Composable
fun PropertyMapPriceMarker(
    property: Property,
    isSelected: Boolean
) {
    val formattedPrice = remember(property.startingPrice) {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
            .format(property.startingPrice)
            .replace(".00", "")
    }

    val backgroundColor = if (isSelected) StynoAccent else StynoBluePrimary
    val textColor = Color.White

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = backgroundColor,
            shadowElevation = if (isSelected) 10.dp else 4.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = if (isSelected) 2.5.dp else 1.5.dp,
                color = Color.White
            )
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = if (isSelected) 10.dp else 8.dp,
                    vertical = if (isSelected) 6.dp else 4.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = when (property.propertyType) {
                        PropertyType.HOSTEL -> Icons.Default.Home
                        PropertyType.HOTEL -> Icons.Default.Hotel
                        PropertyType.PG -> Icons.Default.MeetingRoom
                        PropertyType.FLAT -> Icons.Default.LocationCity
                        PropertyType.QUICK_STAY -> Icons.Default.NightlightRound
                        else -> Icons.Default.LocationOn
                    },
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(if (isSelected) 14.dp else 12.dp)
                )

                Text(
                    text = formattedPrice,
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (isSelected) 13.sp else 11.sp
                )

                if (property.rating > 0) {
                    Text(
                        text = "★${property.rating}",
                        color = Color(0xFFFEF08A),
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSelected) 11.sp else 10.sp
                    )
                }
            }
        }

        // Pointer triangle at bottom of marker
        Box(
            modifier = Modifier
                .size(if (isSelected) 8.dp else 6.dp)
                .background(backgroundColor, RoundedCornerShape(1.dp))
        )
    }
}

/**
 * Compact Horizontal Card for Google Map search results carousel
 */
@Composable
fun PropertyMapCarouselCard(
    property: Property,
    isSelected: Boolean,
    viewModel: StynoViewModel,
    onCardClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    val context = LocalContext.current
    val savedPropertyIds by viewModel.savedPropertyIds.collectAsStateWithLifecycle()
    val isSaved = savedPropertyIds.contains(property.id)

    Card(
        modifier = Modifier
            .width(280.dp)
            .shadow(if (isSelected) 12.dp else 4.dp, RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) StynoAccent else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCardClick() }
            .testTag("map_carousel_card_${property.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Category, Gender & Verified Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = StynoBluePrimary
                    ) {
                        Text(
                            text = property.propertyType.displayName.uppercase(),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = property.genderSuitability.displayName,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (property.verificationStatus == VerificationStatus.VERIFIED) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = StynoEmerald,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.toggleSave(property.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isSaved) StynoAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Body: Photo Thumbnail + Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val drawableRes = remember(property.imageDrawableNames) {
                        val firstDrawable = property.imageDrawableNames.firstOrNull()
                        if (firstDrawable != null) {
                            val id = context.resources.getIdentifier(firstDrawable, "drawable", context.packageName)
                            if (id != 0) id else R.drawable.ic_launcher_foreground
                        } else {
                            R.drawable.ic_launcher_foreground
                        }
                    }

                    Image(
                        painter = painterResource(id = drawableRes),
                        contentDescription = property.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = property.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 1.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${property.area}, ${property.city}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val formatted = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
                            .format(property.startingPrice)
                            .replace(".00", "")

                        Text(
                            text = "$formatted${when (property.durationType) {
                                DurationType.HOURLY -> "/slot"
                                DurationType.DAILY -> "/night"
                                DurationType.WEEKLY -> "/wk"
                                DurationType.MONTHLY -> "/mo"
                                DurationType.YEARLY -> "/yr"
                            }}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "★${property.rating}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button
            Button(
                onClick = onDetailsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .testTag("carousel_view_details_${property.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("View Details", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

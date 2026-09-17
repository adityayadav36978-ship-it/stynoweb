package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.DurationType
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import com.example.data.repository.UserCoordinates
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.theme.StynoGold
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
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * Standard predefined city coordinates for seamless map auto-centering
 */
private val CITY_COORDINATES = mapOf(
    "Delhi NCR" to LatLng(28.6139, 77.2090),
    "Noida" to LatLng(28.5355, 77.3910),
    "Bengaluru" to LatLng(12.9716, 77.5946),
    "Mumbai" to LatLng(19.0760, 72.8777),
    "Hyderabad" to LatLng(17.3850, 78.4867),
    "Kota" to LatLng(25.2138, 75.8648),
    "Bagaha" to LatLng(27.0997, 84.0903)
)

/**
 * Dedicated Google Maps Component integrated directly onto the Homepage.
 * Displays real available properties in the user's current city with custom
 * markers, category stay icons, price badges, and interactive listing preview.
 */
@Composable
fun HomeCityGoogleMapView(
    properties: List<Property>,
    userCoordinates: UserCoordinates?,
    selectedCity: String,
    onCitySelected: ((String) -> Unit)? = null,
    isFetchingLocation: Boolean,
    onFetchLocation: () -> Unit,
    onPropertySelected: (Property) -> Unit,
    onSaveToggle: (String) -> Unit,
    savedPropertyIds: Set<String>,
    modifier: Modifier = Modifier,
    mapHeight: Dp = 350.dp,
    onToggleExpand: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Active city determination: prioritize user's GPS city, then selectedCity, then fallback
    var activeCity by remember(selectedCity, userCoordinates?.cityName) {
        val detected = userCoordinates?.cityName?.takeIf { it.isNotBlank() }
            ?: selectedCity.takeIf { it.isNotBlank() && it != "All Cities" }
            ?: "Delhi NCR"
        mutableStateOf(detected)
    }

    // Filter available properties in the active current city
    val currentCityProperties = remember(properties, activeCity) {
        val cityMatches = properties.filter { prop ->
            prop.city.equals(activeCity, ignoreCase = true) ||
                    prop.address.contains(activeCity, ignoreCase = true) ||
                    prop.area.contains(activeCity, ignoreCase = true)
        }
        if (cityMatches.isNotEmpty()) cityMatches else properties
    }

    // Currently highlighted property for interactive preview card
    var selectedProperty by remember(currentCityProperties) {
        mutableStateOf<Property?>(currentCityProperties.firstOrNull())
    }

    // Determine initial camera center based on user location, city coordinates, or property locations
    val initialCenter = remember(activeCity, userCoordinates, currentCityProperties) {
        if (userCoordinates != null && (userCoordinates.cityName.equals(activeCity, ignoreCase = true) || activeCity == "All Cities")) {
            LatLng(userCoordinates.latitude, userCoordinates.longitude)
        } else if (currentCityProperties.isNotEmpty()) {
            LatLng(currentCityProperties.first().latitude, currentCityProperties.first().longitude)
        } else {
            CITY_COORDINATES[activeCity] ?: LatLng(28.6139, 77.2090)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialCenter, 12.5f)
    }

    var mapType by remember { mutableStateOf(MapType.NORMAL) }
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
    ) { perms ->
        hasLocationPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasLocationPermission) {
            onFetchLocation()
        }
    }

    // Re-frame or animate camera when the current city or properties change
    LaunchedEffect(activeCity, currentCityProperties) {
        if (currentCityProperties.isNotEmpty()) {
            if (currentCityProperties.size == 1) {
                val prop = currentCityProperties.first()
                val target = LatLng(prop.latitude, prop.longitude)
                cameraPositionState.safeAnimate(
                    updateProvider = { CameraUpdateFactory.newLatLngZoom(target, 14.5f) },
                    fallbackTarget = target,
                    fallbackZoom = 14.5f,
                    durationMs = 600
                )
            } else {
                val builder = LatLngBounds.builder()
                currentCityProperties.forEach {
                    builder.include(LatLng(it.latitude, it.longitude))
                }
                cameraPositionState.safeAnimateBounds(
                    boundsBuilder = builder,
                    padding = 90,
                    fallbackTarget = initialCenter,
                    fallbackZoom = 12.5f,
                    durationMs = 600
                )
            }
            selectedProperty = currentCityProperties.firstOrNull()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_city_google_map_section")
    ) {
        // 1. Header with Current City & Available Listing Counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StynoBluePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = StynoBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Stays in $activeCity",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StynoBluePrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${currentCityProperties.size} Available",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = StynoBluePrimary,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = if (userCoordinates != null && userCoordinates.cityName.equals(activeCity, ignoreCase = true)) {
                            "Live GPS active • Centered on ${userCoordinates.areaName ?: activeCity}"
                        } else {
                            "Showing verified hostels, hotels & PGs on Google Maps"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Full Map CTA button
            IconButton(
                onClick = onToggleExpand,
                modifier = Modifier.testTag("home_map_expand_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Expand Full Map",
                    tint = StynoBluePrimary
                )
            }
        }

        // 2. City Selector Quick Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live GPS Chip (if available)
            if (userCoordinates?.cityName != null) {
                val isGpsActive = activeCity.equals(userCoordinates.cityName, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isGpsActive) StynoEmerald else MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = if (isGpsActive) 2.dp else 0.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            activeCity = userCoordinates.cityName ?: "Delhi NCR"
                            onCitySelected?.invoke(activeCity)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = if (isGpsActive) Color.White else StynoEmerald,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "GPS: ${userCoordinates.cityName}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isGpsActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isGpsActive) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Popular Indian Student & Working Professional Metros
            listOf("Delhi NCR", "Bengaluru", "Mumbai", "Hyderabad", "Kota", "Bagaha").forEach { cityName ->
                val isSelected = activeCity.equals(cityName, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = if (isSelected) 2.dp else 0.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            activeCity = cityName
                            onCitySelected?.invoke(cityName)
                            val cityCoord = CITY_COORDINATES[cityName]
                            if (cityCoord != null) {
                                coroutineScope.launch {
                                    cameraPositionState.safeAnimate(
                                        updateProvider = { CameraUpdateFactory.newLatLngZoom(cityCoord, 12.5f) },
                                        fallbackTarget = cityCoord,
                                        fallbackZoom = 12.5f,
                                        durationMs = 600
                                    )
                                }
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else StynoBluePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = cityName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 3. Main Google Maps Canvas wrapped in GoogleMapContainer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(mapHeight)
                .testTag("home_google_maps_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMapContainer(
                    modifier = Modifier.fillMaxSize(),
                    containerId = "home_city_map_$activeCity",
                    fallbackTitle = "Stays in $activeCity",
                    fallbackSubtitle = "Displaying ${currentCityProperties.size} verified properties in $activeCity",
                    fallbackCoordinates = Pair(initialCenter.latitude, initialCenter.longitude),
                    propertyName = "HomeMap"
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
                        )
                    ) {
                        // Custom interactive markers for each property in user's current city
                        currentCityProperties.forEach { property ->
                            val isSelected = selectedProperty?.id == property.id
                            val markerPosition = LatLng(property.latitude, property.longitude)

                            MarkerComposable(
                                state = MarkerState(position = markerPosition),
                                title = property.name,
                                snippet = "₹${property.startingPrice.toInt()} • ${property.propertyType.displayName}",
                                onClick = {
                                    selectedProperty = property
                                    coroutineScope.launch {
                                        cameraPositionState.safeAnimate(
                                            updateProvider = { CameraUpdateFactory.newLatLngZoom(markerPosition, 14.8f) },
                                            fallbackTarget = markerPosition,
                                            fallbackZoom = 14.8f,
                                            durationMs = 450
                                        )
                                    }
                                    true
                                }
                            ) {
                                HomePropertyMarkerPill(
                                    property = property,
                                    isSelected = isSelected
                                )
                            }
                        }
                    }
                }

                // 4. Floating Action Controls (Top-Right / Center-Right)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Full Screen Map Button
                    FloatingActionButton(
                        onClick = onToggleExpand,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("map_fab_fullscreen"),
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        contentColor = StynoBluePrimary,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Full Screen",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Fit All Markers in Current City Bounds
                    FloatingActionButton(
                        onClick = {
                            if (currentCityProperties.isNotEmpty()) {
                                val builder = LatLngBounds.builder()
                                currentCityProperties.forEach {
                                    builder.include(LatLng(it.latitude, it.longitude))
                                }
                                coroutineScope.launch {
                                    cameraPositionState.safeAnimateBounds(
                                        boundsBuilder = builder,
                                        padding = 80,
                                        fallbackTarget = initialCenter,
                                        fallbackZoom = 12.5f,
                                        durationMs = 500
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("map_fab_fit_bounds"),
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        contentColor = StynoBluePrimary,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CropFree,
                            contentDescription = "Fit Pins",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // My Location GPS Centering Button
                    FloatingActionButton(
                        onClick = {
                            if (hasLocationPermission) {
                                onFetchLocation()
                                if (userCoordinates != null) {
                                    val userLatLng = LatLng(userCoordinates.latitude, userCoordinates.longitude)
                                    coroutineScope.launch {
                                        cameraPositionState.safeAnimate(
                                            updateProvider = { CameraUpdateFactory.newLatLngZoom(userLatLng, 15f) },
                                            fallbackTarget = userLatLng,
                                            fallbackZoom = 15f,
                                            durationMs = 500
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
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("map_fab_my_location"),
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        contentColor = if (hasLocationPermission) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)
                    ) {
                        if (isFetchingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = StynoBluePrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "My Location",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Map Layer Switcher (Normal -> Satellite -> Terrain)
                    FloatingActionButton(
                        onClick = {
                            mapType = when (mapType) {
                                MapType.NORMAL -> MapType.HYBRID
                                MapType.HYBRID -> MapType.TERRAIN
                                else -> MapType.NORMAL
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("map_fab_layer"),
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        contentColor = StynoBluePrimary,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Map Layer",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 5. Interactive Selected Listing Preview Card overlay at bottom of the map
                androidx.compose.animation.AnimatedVisibility(
                    visible = selectedProperty != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp)
                ) {
                    selectedProperty?.let { prop ->
                        HomeMapSelectedListingCard(
                            property = prop,
                            isSaved = savedPropertyIds.contains(prop.id),
                            onCardClick = { onPropertySelected(prop) },
                            onSaveClick = { onSaveToggle(prop.id) },
                            onDismiss = { selectedProperty = null }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Price & Category Marker Pill for Google Maps
 */
@Composable
private fun HomePropertyMarkerPill(
    property: Property,
    isSelected: Boolean
) {
    val formattedPrice = remember(property.startingPrice) {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
            .format(property.startingPrice)
            .replace(".00", "")
    }

    val backgroundColor = if (isSelected) StynoAccent else StynoBluePrimary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = backgroundColor,
            shadowElevation = if (isSelected) 8.dp else 4.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = if (isSelected) 2.dp else 1.5.dp,
                color = Color.White
            )
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = if (isSelected) 9.dp else 7.dp,
                    vertical = if (isSelected) 5.dp else 4.dp
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
                    tint = Color.White,
                    modifier = Modifier.size(if (isSelected) 13.dp else 11.dp)
                )

                Text(
                    text = formattedPrice,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (isSelected) 12.sp else 11.sp
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

        // Downward pointer triangle
        Box(
            modifier = Modifier
                .size(if (isSelected) 7.dp else 5.dp)
                .background(backgroundColor, RoundedCornerShape(1.dp))
        )
    }
}

/**
 * Compact Selected Listing Floating Card inside Google Maps
 */
@Composable
private fun HomeMapSelectedListingCard(
    property: Property,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(16.dp))
            .border(
                width = 1.5.dp,
                color = StynoBluePrimary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCardClick() }
            .testTag("home_map_selected_card_${property.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row: Stay Type, Gender, Rating, and Dismiss
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
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
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
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
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
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Stay",
                            tint = if (isSaved) StynoAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Preview",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Body: Thumbnail + Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    PropertyPhotoItem(
                        imageName = property.imageDrawableNames.firstOrNull() ?: "img_hostel_modern",
                        propertyName = property.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
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
                            tint = StynoBluePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${property.area}, ${property.city}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
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
                            color = StynoBluePrimary
                        )

                        if (property.rating > 0) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = StynoGold,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = "${property.rating}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action: View Details CTA Button
            Button(
                onClick = onCardClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .testTag("map_selected_view_details_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
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

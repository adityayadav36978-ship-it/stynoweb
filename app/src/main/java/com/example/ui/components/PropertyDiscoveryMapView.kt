package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.DurationType
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import com.example.data.repository.LocationRepository
import com.example.data.repository.UserCoordinates
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Interactive Discovery Map View that pins available properties based on the user's
 * current GPS location fetched via LocationRepository.
 *
 * Supports:
 * - Dynamic GPS user pin with pulsing radar beacon and accuracy radius.
 * - Interactive property pins color-coded by PropertyType with price tags & verification badges.
 * - Zoom in / Zoom out & pinch-to-zoom + pan / drag gestures.
 * - Re-center on Live Location GPS button.
 * - Dynamic radius filtering (2km, 5km, 10km, 25km, All).
 * - Interactive property pin tap -> selected pin highlighting + floating bottom detail card.
 * - Compact embedded mode & expanded full-height mode.
 */
@Composable
fun PropertyDiscoveryMapView(
    properties: List<Property>,
    userCoordinates: UserCoordinates?,
    isFetchingLocation: Boolean,
    onFetchLocation: () -> Unit,
    onPropertySelected: (Property) -> Unit,
    onSaveToggle: (String) -> Unit,
    savedPropertyIds: Set<String> = emptySet(),
    modifier: Modifier = Modifier,
    mapHeight: Dp = 340.dp,
    isExpandedMode: Boolean = false,
    onToggleExpand: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    // Default reference location (Delhi NCR coordinates if GPS pending)
    val defaultLat = userCoordinates?.latitude ?: 28.5355
    val defaultLng = userCoordinates?.longitude ?: 77.3910

    var centerLat by remember { mutableDoubleStateOf(defaultLat) }
    var centerLng by remember { mutableDoubleStateOf(defaultLng) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    // Sync center when user coordinates update
    LaunchedEffect(userCoordinates) {
        if (userCoordinates != null) {
            centerLat = userCoordinates.latitude
            centerLng = userCoordinates.longitude
            panOffsetX = 0f
            panOffsetY = 0f
        }
    }

    var selectedPropertyId by remember { mutableStateOf<String?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<PropertyType?>(null) }
    var maxRadiusFilterKm by remember { mutableDoubleStateOf(35.0) }
    var isVerifiedOnly by remember { mutableStateOf(false) }

    // Filter properties based on radius, category, verification
    val filteredProperties = remember(properties, selectedCategoryFilter, maxRadiusFilterKm, isVerifiedOnly, userCoordinates) {
        properties.filter { prop ->
            val matchesCategory = selectedCategoryFilter == null || prop.propertyType == selectedCategoryFilter
            val matchesVerified = !isVerifiedOnly || prop.verificationStatus == VerificationStatus.VERIFIED
            val matchesRadius = if (userCoordinates != null) {
                val dist = prop.distanceKm
                dist <= maxRadiusFilterKm
            } else true
            matchesCategory && matchesVerified && matchesRadius
        }
    }

    val selectedProperty = remember(filteredProperties, selectedPropertyId) {
        filteredProperties.find { it.id == selectedPropertyId }
    }

    // Radar pulse animation for user's GPS marker
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isExpandedMode) Modifier.fillMaxSize() else Modifier.height(mapHeight))
            .clip(RoundedCornerShape(if (isExpandedMode) 0.dp else 20.dp))
            .border(
                width = if (isExpandedMode) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(if (isExpandedMode) 0.dp else 20.dp)
            )
            .testTag("property_discovery_map_view")
    ) {
        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = constraints.maxHeight.toFloat()

        // Approximate scale factor: degrees to canvas pixels at zoom 1.0
        val baseScale = (canvasWidth.coerceAtLeast(300f)) * 480f

        // Helper to convert (lat, lng) to screen coordinates
        fun toScreenCoord(lat: Double, lng: Double): Offset {
            val cosLat = cos(Math.toRadians(centerLat))
            val dx = ((lng - centerLng) * cosLat * baseScale * zoomLevel).toFloat()
            val dy = (-(lat - centerLat) * baseScale * zoomLevel).toFloat()
            return Offset(
                x = canvasWidth / 2f + dx + panOffsetX,
                y = canvasHeight / 2f + dy + panOffsetY
            )
        }

        // Screen positions of all properties for tap hit detection
        val markerPositions = remember(filteredProperties, centerLat, centerLng, zoomLevel, panOffsetX, panOffsetY, canvasWidth, canvasHeight) {
            filteredProperties.map { prop ->
                prop to toScreenCoord(prop.latitude, prop.longitude)
            }
        }

        val userScreenPos = remember(userCoordinates, centerLat, centerLng, zoomLevel, panOffsetX, panOffsetY, canvasWidth, canvasHeight) {
            val uLat = userCoordinates?.latitude ?: defaultLat
            val uLng = userCoordinates?.longitude ?: defaultLng
            toScreenCoord(uLat, uLng)
        }

        // 1. Vector Map Canvas (Background, Grid, Roads, User Radar, Radius Circle, Property Markers)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomLevel = (zoomLevel * zoom).coerceIn(0.4f, 3.5f)
                        panOffsetX += pan.x
                        panOffsetY += pan.y
                    }
                }
                .pointerInput(markerPositions) {
                    detectTapGestures { tapOffset ->
                        // Check if any property marker was tapped (hit radius 36dp = ~90px)
                        val hitMarker = markerPositions.find { (_, screenPos) ->
                            val distSq = (tapOffset.x - screenPos.x) * (tapOffset.x - screenPos.x) +
                                    (tapOffset.y - screenPos.y) * (tapOffset.y - screenPos.y)
                            distSq <= 3000f // ~55px radius
                        }

                        if (hitMarker != null) {
                            selectedPropertyId = hitMarker.first.id
                        } else {
                            // Tapped empty map area -> deselect
                            selectedPropertyId = null
                        }
                    }
                }
        ) {
            // Map Base Color (Modern Clean Slate Map)
            drawRect(color = Color(0xFFF1F5F9))

            // Draw Decorative Map Features (Urban Grid, Blocks, Parks, Waterways)
            drawStylizedMapTerrain(
                canvasWidth = size.width,
                canvasHeight = size.height,
                panX = panOffsetX,
                panY = panOffsetY,
                zoom = zoomLevel
            )

            // Draw Radius Rings around User Location
            if (userCoordinates != null) {
                val radiusPx = (maxRadiusFilterKm * baseScale * zoomLevel * 0.009f).toFloat().coerceIn(40f, 600f)
                drawCircle(
                    color = StynoBluePrimary.copy(alpha = 0.08f),
                    radius = radiusPx,
                    center = userScreenPos
                )
                drawCircle(
                    color = StynoBluePrimary.copy(alpha = 0.35f),
                    radius = radiusPx,
                    center = userScreenPos,
                    style = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                    )
                )
            }

            // Draw Connecting Route Line if a Property is Selected
            val activeSelected = markerPositions.find { it.first.id == selectedPropertyId }
            if (activeSelected != null) {
                val destPos = activeSelected.second
                drawLine(
                    color = StynoAccent,
                    start = userScreenPos,
                    end = destPos,
                    strokeWidth = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f))
                )
            }

            // Draw User GPS Beacon / Pin
            drawUserGpsPin(
                pos = userScreenPos,
                pulseRadius = pulseRadius * zoomLevel.coerceIn(0.8f, 1.5f),
                pulseAlpha = pulseAlpha
            )

            // Draw Property Map Markers
            markerPositions.forEach { (property, screenPos) ->
                val isSelected = property.id == selectedPropertyId
                drawPropertyMapMarker(
                    property = property,
                    screenPos = screenPos,
                    isSelected = isSelected,
                    textMeasurer = textMeasurer
                )
            }
        }

        // 2. Top Header Bar with Live Location Status, Radius, and Expand/Collapse Controls
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(10.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(StynoBluePrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = "Live GPS",
                                tint = StynoBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (userCoordinates != null) "${userCoordinates.areaName ?: userCoordinates.cityName ?: "Current Location"}" else "Detecting GPS Location...",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = StynoEmerald.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${filteredProperties.size} Pinned",
                                        color = StynoEmerald,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Live Map Discovery • Within ${maxRadiusFilterKm.toInt()} km radius",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Refresh GPS button
                        IconButton(
                            onClick = onFetchLocation,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("map_refresh_gps_btn")
                        ) {
                            if (isFetchingLocation) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = StynoBluePrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh GPS",
                                    tint = StynoBluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Expand / Minimize Map toggle button if provided
                        if (onToggleExpand != null) {
                            IconButton(
                                onClick = onToggleExpand,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("map_toggle_expand_btn")
                            ) {
                                Icon(
                                    imageVector = if (isExpandedMode) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = if (isExpandedMode) "Minimize Map" else "Expand Map",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Horizontal Filter Badges Row on Map
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "All Stays" Chip
                    MapFilterPill(
                        text = "All Stays",
                        isSelected = selectedCategoryFilter == null && !isVerifiedOnly,
                        onClick = {
                            selectedCategoryFilter = null
                            isVerifiedOnly = false
                        }
                    )

                    // Property Category Filter Pills
                    PropertyType.entries.forEach { type ->
                        MapFilterPill(
                            text = type.displayName,
                            isSelected = selectedCategoryFilter == type,
                            onClick = {
                                selectedCategoryFilter = if (selectedCategoryFilter == type) null else type
                            }
                        )
                    }

                    // Verified Stays Only Pill
                    MapFilterPill(
                        text = "Verified Only",
                        isSelected = isVerifiedOnly,
                        leadingIcon = Icons.Default.Verified,
                        onClick = { isVerifiedOnly = !isVerifiedOnly }
                    )

                    // Radius Filter Pills (5km, 15km, 35km)
                    listOf(5.0 to "5 km", 15.0 to "15 km", 35.0 to "All Distances").forEach { (rad, label) ->
                        MapFilterPill(
                            text = label,
                            isSelected = maxRadiusFilterKm == rad,
                            onClick = { maxRadiusFilterKm = rad }
                        )
                    }
                }
            }
        }

        // 3. Floating Map Controls (Zoom +, Zoom -, Re-center on User GPS)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Re-center on My Location
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 5.dp,
                modifier = Modifier.size(42.dp)
            ) {
                IconButton(
                    onClick = {
                        val uLat = userCoordinates?.latitude ?: defaultLat
                        val uLng = userCoordinates?.longitude ?: defaultLng
                        centerLat = uLat
                        centerLng = uLng
                        panOffsetX = 0f
                        panOffsetY = 0f
                        zoomLevel = 1.2f
                    },
                    modifier = Modifier.testTag("map_center_gps_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Center on my location",
                        tint = StynoBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Zoom In (+)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier.size(38.dp)
            ) {
                IconButton(
                    onClick = { zoomLevel = (zoomLevel * 1.3f).coerceAtMost(3.5f) },
                    modifier = Modifier.testTag("map_zoom_in_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom in",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Zoom Out (-)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier.size(38.dp)
            ) {
                IconButton(
                    onClick = { zoomLevel = (zoomLevel / 1.3f).coerceAtLeast(0.4f) },
                    modifier = Modifier.testTag("map_zoom_out_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom out",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 4. Selected Property Floating Detail Preview Card at Bottom
        AnimatedVisibility(
            visible = selectedProperty != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            selectedProperty?.let { prop ->
                val isSaved = savedPropertyIds.contains(prop.id)
                FloatingPropertyMapCard(
                    property = prop,
                    isSaved = isSaved,
                    onCardClick = { onPropertySelected(prop) },
                    onSaveClick = { onSaveToggle(prop.id) },
                    onClose = { selectedPropertyId = null }
                )
            }
        }
    }
}

/**
 * Filter Pill for Map View
 */
@Composable
private fun MapFilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = text,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

/**
 * Bottom Floating Card for the Selected Map Pin
 */
@Composable
private fun FloatingPropertyMapCard(
    property: Property,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val firstImage = property.imageDrawableNames.firstOrNull() ?: "img_hostel_modern"
    val resId = remember(firstImage) {
        context.resources.getIdentifier(firstImage, "drawable", context.packageName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onCardClick)
            .testTag("floating_map_property_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with Property Type Badge
            Box(
                modifier = Modifier
                    .size(width = 90.dp, height = 90.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (resId != 0) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = property.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (firstImage.startsWith("http")) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(firstImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = property.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(StynoBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(property.name.take(2), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // Type Badge Overlay
                Surface(
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 0.dp, bottomEnd = 8.dp),
                    color = getPropertyTypeColor(property.propertyType),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = property.propertyType.displayName,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Property Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = property.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close preview",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Location & Commute
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = StynoBluePrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${property.area}, ${property.city} • ${property.distanceKm} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Rating & Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${property.rating}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = " (${property.reviewCount})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${property.startingPrice.toInt()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = StynoBluePrimary
                        )
                        Text(
                            text = property.propertyType.defaultDuration,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Draws stylized vector map elements (roads, parks, gridlines, water features) on the canvas
 */
private fun DrawScope.drawStylizedMapTerrain(
    canvasWidth: Float,
    canvasHeight: Float,
    panX: Float,
    panY: Float,
    zoom: Float
) {
    // 1. Grid of Urban Blocks
    val gridSize = 80f * zoom
    val offsetX = (panX % gridSize + gridSize) % gridSize
    val offsetY = (panY % gridSize + gridSize) % gridSize

    var x = offsetX - gridSize
    while (x < canvasWidth + gridSize) {
        drawLine(
            color = Color(0xFFE2E8F0),
            start = Offset(x, 0f),
            end = Offset(x, canvasHeight),
            strokeWidth = 1.2f
        )
        x += gridSize
    }

    var y = offsetY - gridSize
    while (y < canvasHeight + gridSize) {
        drawLine(
            color = Color(0xFFE2E8F0),
            start = Offset(0f, y),
            end = Offset(canvasWidth, y),
            strokeWidth = 1.2f
        )
        y += gridSize
    }

    // 2. Green Zones (University Campuses, Public Parks)
    val parkPath = Path().apply {
        moveTo(canvasWidth * 0.15f + panX * 0.5f, canvasHeight * 0.25f + panY * 0.5f)
        lineTo(canvasWidth * 0.35f + panX * 0.5f, canvasHeight * 0.20f + panY * 0.5f)
        lineTo(canvasWidth * 0.40f + panX * 0.5f, canvasHeight * 0.40f + panY * 0.5f)
        lineTo(canvasWidth * 0.18f + panX * 0.5f, canvasHeight * 0.45f + panY * 0.5f)
        close()
    }
    drawPath(path = parkPath, color = Color(0xFFDCFCE7).copy(alpha = 0.8f))

    val campusPath = Path().apply {
        moveTo(canvasWidth * 0.65f + panX * 0.6f, canvasHeight * 0.60f + panY * 0.6f)
        lineTo(canvasWidth * 0.88f + panX * 0.6f, canvasHeight * 0.55f + panY * 0.6f)
        lineTo(canvasWidth * 0.92f + panX * 0.6f, canvasHeight * 0.78f + panY * 0.6f)
        lineTo(canvasWidth * 0.70f + panX * 0.6f, canvasHeight * 0.82f + panY * 0.6f)
        close()
    }
    drawPath(path = campusPath, color = Color(0xFFDCFCE7).copy(alpha = 0.8f))

    // 3. Waterway (River / Lake feature)
    val riverPath = Path().apply {
        moveTo(0f, canvasHeight * 0.7f + panY * 0.3f)
        cubicTo(
            canvasWidth * 0.3f, canvasHeight * 0.65f + panY * 0.3f,
            canvasWidth * 0.6f, canvasHeight * 0.85f + panY * 0.3f,
            canvasWidth, canvasHeight * 0.8f + panY * 0.3f
        )
    }
    drawPath(
        path = riverPath,
        color = Color(0xFFBAE6FD).copy(alpha = 0.6f),
        style = Stroke(width = 24f * zoom)
    )

    // 4. Main Arterial Roads / Expressways
    val roadColor = Color(0xFFCBD5E1)
    val highwayColor = Color(0xFFFDE68A)

    // Primary Expressway
    drawLine(
        color = highwayColor,
        start = Offset(0f, canvasHeight * 0.45f + panY * 0.8f),
        end = Offset(canvasWidth, canvasHeight * 0.35f + panY * 0.8f),
        strokeWidth = 14f * zoom
    )

    // Secondary Transit Corridors
    drawLine(
        color = roadColor,
        start = Offset(canvasWidth * 0.35f + panX * 0.8f, 0f),
        end = Offset(canvasWidth * 0.45f + panX * 0.8f, canvasHeight),
        strokeWidth = 10f * zoom
    )

    drawLine(
        color = roadColor,
        start = Offset(canvasWidth * 0.75f + panX * 0.8f, 0f),
        end = Offset(canvasWidth * 0.65f + panX * 0.8f, canvasHeight),
        strokeWidth = 10f * zoom
    )
}

/**
 * Draws the user's live GPS marker with pulsing radar beacon and accuracy dot
 */
private fun DrawScope.drawUserGpsPin(
    pos: Offset,
    pulseRadius: Float,
    pulseAlpha: Float
) {
    // 1. Radar Pulse Wave
    drawCircle(
        color = StynoBluePrimary.copy(alpha = pulseAlpha),
        radius = pulseRadius,
        center = pos
    )

    // 2. White Halo Ring
    drawCircle(
        color = Color.White,
        radius = 11f,
        center = pos
    )

    // 3. Inner Solid Blue Dot
    drawCircle(
        color = StynoBluePrimary,
        radius = 8f,
        center = pos
    )
}

/**
 * Draws an interactive property map marker with price tag pill, icon, and selected state halo
 */
private fun DrawScope.drawPropertyMapMarker(
    property: Property,
    screenPos: Offset,
    isSelected: Boolean,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val typeColor = getPropertyTypeColor(property.propertyType)
    val markerScale = if (isSelected) 1.25f else 1.0f

    val priceText = when (property.durationType) {
        DurationType.HOURLY -> "₹${property.startingPrice.toInt()}/slot"
        DurationType.DAILY -> "₹${property.startingPrice.toInt()}/n"
        DurationType.WEEKLY -> "₹${property.startingPrice.toInt()}/wk"
        DurationType.YEARLY -> "₹${property.startingPrice.toInt()}/yr"
        DurationType.MONTHLY -> {
            val price = property.startingPrice.toInt()
            if (price >= 1000) "₹${(price / 1000.0).let { if (it % 1.0 == 0.0) it.toInt().toString() else "%.1f".format(it) }}k" else "₹$price"
        }
    }

    // Selected Pin Pulsing Glow Halo
    if (isSelected) {
        drawCircle(
            color = StynoAccent.copy(alpha = 0.35f),
            radius = 28f * markerScale,
            center = screenPos
        )
        drawCircle(
            color = StynoAccent.copy(alpha = 0.6f),
            radius = 20f * markerScale,
            center = screenPos
        )
    }

    // Marker Shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.25f),
        radius = 14f * markerScale,
        center = screenPos.copy(y = screenPos.y + 2f)
    )

    // Main Marker Circle
    drawCircle(
        color = if (isSelected) StynoAccent else typeColor,
        radius = 13f * markerScale,
        center = screenPos
    )

    // White Inner Border Ring
    drawCircle(
        color = Color.White,
        radius = 13f * markerScale,
        center = screenPos,
        style = Stroke(width = 2.5f)
    )

    // Center Core Icon Dot
    drawCircle(
        color = Color.White,
        radius = 5f * markerScale,
        center = screenPos
    )

    // Floating Price Tag Label above Marker
    val priceLayoutResult = textMeasurer.measure(
        text = priceText,
        style = TextStyle(
            color = if (isSelected) Color.White else Color(0xFF0F172A),
            fontSize = if (isSelected) 11.sp else 10.sp,
            fontWeight = FontWeight.Bold
        )
    )

    val badgeWidth = priceLayoutResult.size.width + 16f
    val badgeHeight = priceLayoutResult.size.height + 10f
    val badgeTop = screenPos.y - 18f * markerScale - badgeHeight
    val badgeLeft = screenPos.x - (badgeWidth / 2f)

    // Price Badge Background Pill
    drawRoundRect(
        color = if (isSelected) StynoAccent else Color.White,
        topLeft = Offset(badgeLeft, badgeTop),
        size = Size(badgeWidth, badgeHeight),
        cornerRadius = CornerRadius(10f, 10f),
        style = androidx.compose.ui.graphics.drawscope.Fill
    )

    // Price Badge Outline Border
    drawRoundRect(
        color = if (isSelected) Color.White else Color(0xFFCBD5E1),
        topLeft = Offset(badgeLeft, badgeTop),
        size = Size(badgeWidth, badgeHeight),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = 1.5f)
    )

    // Draw Price Text
    drawText(
        textLayoutResult = priceLayoutResult,
        topLeft = Offset(badgeLeft + 8f, badgeTop + 5f)
    )
}

/**
 * Returns distinct theme color for each PropertyType
 */
fun getPropertyTypeColor(type: PropertyType): Color {
    return when (type) {
        PropertyType.HOSTEL -> StynoBluePrimary
        PropertyType.HOTEL -> Color(0xFF7C3AED) // Violet
        PropertyType.PG -> Color(0xFF0D9488)    // Teal
        PropertyType.ROOM -> Color(0xFF2563EB)  // Royal Blue
        PropertyType.FLAT -> StynoEmerald       // Emerald Green
        PropertyType.QUICK_STAY -> Color(0xFFD97706) // Amber/Gold
    }
}

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PropertyType
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

/**
 * Filter sections supported within the inline expandable filtering UI.
 */
enum class FilterSection(val title: String, val icon: ImageVector) {
    PRICE("Price", Icons.Default.FilterAlt),
    ACCOMMODATION_TYPE("Stay Type", Icons.Default.Home),
    DISTANCE("Distance", Icons.Default.NearMe)
}

/**
 * Modern, responsive Quick Filter Bar with real-time badges, category chips,
 * and direct toggle controls for Price, Accommodation Type, and Distance.
 */
@Composable
fun PropertyFilterBar(
    selectedCategory: PropertyType?,
    onCategorySelected: (PropertyType?) -> Unit,
    minPrice: Float?,
    maxPrice: Float?,
    maxDistanceKm: Double?,
    isFilterPanelOpen: Boolean,
    onToggleFilterPanel: () -> Unit,
    onOpenFullFilterSheet: () -> Unit,
    propertyTypeCounts: Map<PropertyType, Int> = emptyMap(),
    isMapView: Boolean,
    onToggleMapView: () -> Unit,
    isOfflineCachedOnly: Boolean,
    cachedCount: Int,
    onToggleOfflineCachedOnly: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPriceActive = (minPrice != null && minPrice > 100f) || (maxPrice != null && maxPrice < 39000f)
    val isDistanceActive = maxDistanceKm != null
    val isTypeActive = selectedCategory != null

    val totalActiveFilters = (if (isTypeActive) 1 else 0) +
            (if (isPriceActive) 1 else 0) +
            (if (isDistanceActive) 1 else 0)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .testTag("property_filter_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Primary Filter Trigger Pill with Active Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (totalActiveFilters > 0 || isFilterPanelOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onToggleFilterPanel() }
                .testTag("filter_trigger_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filters",
                    tint = if (totalActiveFilters > 0 || isFilterPanelOpen) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (totalActiveFilters > 0) "Filters ($totalActiveFilters)" else "Filters",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (totalActiveFilters > 0 || isFilterPanelOpen) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Price Range Filter Pill
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isPriceActive) StynoAccent else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onToggleFilterPanel() }
                .testTag("filter_price_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isPriceActive) "₹${(minPrice ?: 0f).toInt()} - ₹${(maxPrice ?: 40000f).toInt()}" else "₹ Price Range",
                    fontSize = 12.sp,
                    fontWeight = if (isPriceActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isPriceActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 3. Distance Filter Pill
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isDistanceActive) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onToggleFilterPanel() }
                .testTag("filter_distance_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = null,
                    tint = if (isDistanceActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isDistanceActive) "< ${maxDistanceKm!!.toInt()} km" else "Distance",
                    fontSize = 12.sp,
                    fontWeight = if (isDistanceActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isDistanceActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 4. Quick Accommodation Type Pills (Hostel, Hotel, PG, Flat, etc.)
        PropertyType.entries.forEach { type ->
            val isSelected = selectedCategory == type
            val count = propertyTypeCounts[type]
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        onCategorySelected(if (isSelected) null else type)
                    }
                    .testTag("filter_type_chip_${type.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (type) {
                        PropertyType.HOSTEL -> Icons.Default.CorporateFare
                        PropertyType.HOTEL -> Icons.Default.Hotel
                        PropertyType.PG -> Icons.Default.Home
                        PropertyType.FLAT -> Icons.Default.Apartment
                        PropertyType.ROOM -> Icons.Default.Home
                        PropertyType.QUICK_STAY -> Icons.Default.Bolt
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = type.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (count != null && count > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "($count)",
                            fontSize = 11.sp,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // 5. Offline Cached Toggle
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isOfflineCachedOnly) Color(0xFF0EA5E9) else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onToggleOfflineCachedOnly() }
                .testTag("offline_cached_filter_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.OfflinePin,
                    contentDescription = null,
                    tint = if (isOfflineCachedOnly) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isOfflineCachedOnly) "Cached ($cachedCount)" else "Offline ($cachedCount)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOfflineCachedOnly) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 6. Map vs List Toggle
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isMapView) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onToggleMapView() }
                .testTag("toggle_map_view_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isMapView) Icons.Default.List else Icons.Default.Map,
                    contentDescription = null,
                    tint = if (isMapView) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isMapView) "List" else "Map",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMapView) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Dedicated expandable Filtering UI Panel that allows deep control over:
 * - Price Range (min/max range slider, quick presets)
 * - Accommodation Type (Hostel, Hotel, PG, Flat, Room, Quick Stay with descriptions & counts)
 * - Distance Radius (slider from 1 km to 50 km, quick distance buttons, GPS notes)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyFilterExpandablePanel(
    selectedCategory: PropertyType?,
    onCategorySelected: (PropertyType?) -> Unit,
    minPrice: Float?,
    maxPrice: Float?,
    onPriceRangeChanged: (Float?, Float?) -> Unit,
    maxDistanceKm: Double?,
    onDistanceChanged: (Double?) -> Unit,
    matchingResultsCount: Int,
    propertyTypeCounts: Map<PropertyType, Int>,
    onResetAll: () -> Unit,
    onClosePanel: () -> Unit,
    onOpenFullFilterSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = FilterSection.entries

    var localPriceRange by remember(minPrice, maxPrice) {
        mutableStateOf((minPrice ?: 0f)..(maxPrice ?: 40000f))
    }
    var localDistance by remember(maxDistanceKm) {
        mutableStateOf(maxDistanceKm)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("property_filter_expandable_panel")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Filter title, results counter, and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Refine Search Filters",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "$matchingResultsCount stays found",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Tabs: Price, Stay Type, Distance
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, section ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = section.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = section.title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Content
            when (tabs[selectedTab]) {
                FilterSection.PRICE -> {
                    // Price Range Filter Content
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Budget / Month",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${localPriceRange.start.toInt()} — ₹${localPriceRange.endInclusive.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        RangeSlider(
                            value = localPriceRange,
                            onValueChange = { range ->
                                localPriceRange = range
                                onPriceRangeChanged(
                                    if (range.start > 100f) range.start else null,
                                    if (range.endInclusive < 39000f) range.endInclusive else null
                                )
                            },
                            valueRange = 0f..40000f,
                            steps = 39,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.testTag("filter_panel_price_slider")
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Quick Presets:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Budget (< ₹6k)" to (0f to 6000f),
                                "₹6k - ₹12k" to (6000f to 12000f),
                                "₹12k - ₹20k" to (12000f to 20000f),
                                "Luxury (₹20k+)" to (20000f to 40000f),
                                "Any Price" to (0f to 40000f)
                            ).forEach { (label, range) ->
                                val isSelected = (range.first == 0f && range.second == 40000f && minPrice == null && maxPrice == null) ||
                                        (minPrice == range.first && maxPrice == range.second)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            localPriceRange = range.first..range.second
                                            onPriceRangeChanged(
                                                if (range.first > 100f) range.first else null,
                                                if (range.second < 39000f) range.second else null
                                            )
                                        }
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                FilterSection.ACCOMMODATION_TYPE -> {
                    // Accommodation Type Filter Content
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Accommodation Type",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (selectedCategory != null) {
                                Text(
                                    text = "Show All Types",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable { onCategorySelected(null) }
                                        .padding(4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PropertyType.entries.forEach { type ->
                                val isSelected = selectedCategory == type
                                val count = propertyTypeCounts[type]
                                val icon = when (type) {
                                    PropertyType.HOSTEL -> Icons.Default.CorporateFare
                                    PropertyType.HOTEL -> Icons.Default.Hotel
                                    PropertyType.PG -> Icons.Default.Home
                                    PropertyType.FLAT -> Icons.Default.Apartment
                                    PropertyType.ROOM -> Icons.Default.Home
                                    PropertyType.QUICK_STAY -> Icons.Default.Bolt
                                }
                                val subtitle = when (type) {
                                    PropertyType.HOSTEL -> "Students & youth"
                                    PropertyType.HOTEL -> "Daily & premium"
                                    PropertyType.PG -> "Paying guest / meals"
                                    PropertyType.FLAT -> "1/2/3 BHK flats"
                                    PropertyType.ROOM -> "Single/shared room"
                                    PropertyType.QUICK_STAY -> "Hourly & short-term"
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            onCategorySelected(if (isSelected) null else type)
                                        }
                                        .testTag("filter_type_card_${type.name.lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = type.displayName,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = subtitle,
                                                fontSize = 10.sp,
                                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (count != null && count > 0) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = "$count",
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                FilterSection.DISTANCE -> {
                    // Distance Radius Filter Content
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Radius from Location",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (localDistance != null) "Within ${localDistance!!.toInt()} km" else "Any Distance",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Slider(
                            value = (localDistance?.toFloat() ?: 50f),
                            onValueChange = { value ->
                                val dist = if (value >= 48f) null else value.toDouble()
                                localDistance = dist
                                onDistanceChanged(dist)
                            },
                            valueRange = 1f..50f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.testTag("filter_panel_distance_slider")
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Quick Radius Presets:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                2.0 to "< 2 km (Walking)",
                                5.0 to "< 5 km (Short Ride)",
                                10.0 to "< 10 km (City Hub)",
                                25.0 to "< 25 km (Metro)",
                                null to "Any Distance"
                            ).forEach { (dist, label) ->
                                val isSelected = localDistance == dist
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            localDistance = dist
                                            onDistanceChanged(dist)
                                        }
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "📍 Distance is dynamically calculated relative to your current GPS coordinates or selected locality hub.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Bar (Reset, All Filters, Close/Done)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        localPriceRange = 0f..40000f
                        localDistance = null
                        onResetAll()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("filter_panel_reset_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reset All", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        onClosePanel()
                        onOpenFullFilterSheet()
                    },
                    modifier = Modifier
                        .weight(1.1f)
                        .testTag("filter_panel_more_filters_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("More Filters...", fontSize = 12.sp)
                }

                Button(
                    onClick = onClosePanel,
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("filter_panel_apply_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Done ($matchingResultsCount)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Active Applied Filter Chips Row with single-tap remove ('X') icons and
 * global "Reset All" button.
 */
@Composable
fun ActiveFilterChipsRow(
    selectedCategory: PropertyType?,
    onClearCategory: () -> Unit,
    minPrice: Float?,
    maxPrice: Float?,
    onClearPrice: () -> Unit,
    maxDistanceKm: Double?,
    onClearDistance: () -> Unit,
    verifiedOnly: Boolean,
    onClearVerifiedOnly: () -> Unit,
    selectedAmenities: Set<String>,
    onRemoveAmenity: (String) -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPriceActive = (minPrice != null && minPrice > 100f) || (maxPrice != null && maxPrice < 39000f)
    val hasAnyFilter = selectedCategory != null || isPriceActive || maxDistanceKm != null || verifiedOnly || selectedAmenities.isNotEmpty()

    if (!hasAnyFilter) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset All Button Chip
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.75f),
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { onResetAll() }
                .testTag("active_filter_reset_all")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear All",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Reset All",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Accommodation Type Active Chip
        if (selectedCategory != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onClearCategory() }
                    .testTag("active_filter_chip_type")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Type: ${selectedCategory.displayName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove category",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Price Range Active Chip
        if (isPriceActive) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StynoAccent.copy(alpha = 0.15f),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onClearPrice() }
                    .testTag("active_filter_chip_price")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Price: ₹${(minPrice ?: 0f).toInt()} - ₹${(maxPrice ?: 40000f).toInt()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StynoAccent
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove price filter",
                        modifier = Modifier.size(12.dp),
                        tint = StynoAccent
                    )
                }
            }
        }

        // Distance Active Chip
        if (maxDistanceKm != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StynoBluePrimary.copy(alpha = 0.15f),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onClearDistance() }
                    .testTag("active_filter_chip_distance")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Distance: < ${maxDistanceKm.toInt()} km",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StynoBluePrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove distance filter",
                        modifier = Modifier.size(12.dp),
                        tint = StynoBluePrimary
                    )
                }
            }
        }

        // Verified Only Chip
        if (verifiedOnly) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StynoEmerald.copy(alpha = 0.15f),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onClearVerifiedOnly() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Verified Only",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StynoEmerald
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove verified filter",
                        modifier = Modifier.size(12.dp),
                        tint = StynoEmerald
                    )
                }
            }
        }

        // Amenities Chips
        selectedAmenities.forEach { amenity ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onRemoveAmenity(amenity) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = amenity,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove $amenity filter",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

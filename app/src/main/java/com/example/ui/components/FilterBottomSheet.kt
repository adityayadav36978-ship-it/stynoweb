package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GenderSuitability
import com.example.data.model.PropertyType
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.SortOption

data class PricePreset(
    val label: String,
    val min: Float?,
    val max: Float?
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    selectedCategory: PropertyType?,
    selectedGender: GenderSuitability?,
    selectedSort: SortOption,
    selectedAmenities: Set<String>,
    verifiedOnly: Boolean,
    minPrice: Float?,
    maxPrice: Float?,
    minRating: Float?,
    maxDistance: Double? = null,
    selectedAttachedBathroom: Boolean? = null,
    selectedKitchen: Boolean? = null,
    selectedAc: Boolean? = null,
    selectedFood: Boolean? = null,
    selectedCleanliness: Boolean? = null,
    minReviews: Int? = null,
    availableAmenities: List<String> = emptyList(),
    propertyTypeCounts: Map<PropertyType, Int> = emptyMap(),
    matchingResultsCount: Int? = null,
    onApply: (
        category: PropertyType?,
        gender: GenderSuitability?,
        sort: SortOption,
        amenities: Set<String>,
        verifiedOnly: Boolean,
        minPrice: Float?,
        maxPrice: Float?,
        minRating: Float?,
        maxDistance: Double?
    ) -> Unit,
    onApplyExtended: ((
        category: PropertyType?,
        gender: GenderSuitability?,
        sort: SortOption,
        amenities: Set<String>,
        verifiedOnly: Boolean,
        minPrice: Float?,
        maxPrice: Float?,
        minRating: Float?,
        maxDistance: Double?,
        attachedBathroom: Boolean?,
        kitchen: Boolean?,
        ac: Boolean?,
        food: Boolean?,
        cleanliness: Boolean?,
        minReviews: Int?
    ) -> Unit)? = null,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var tempCategory by remember(selectedCategory) { mutableStateOf(selectedCategory) }
    var tempGender by remember(selectedGender) { mutableStateOf(selectedGender) }
    var tempSort by remember(selectedSort) { mutableStateOf(selectedSort) }
    var tempAmenities by remember(selectedAmenities) { mutableStateOf(selectedAmenities) }
    var tempVerifiedOnly by remember(verifiedOnly) { mutableStateOf(verifiedOnly) }
    var tempPriceRange by remember(minPrice, maxPrice) { mutableStateOf((minPrice ?: 0f)..(maxPrice ?: 40000f)) }
    var tempMinRating by remember(minRating) { mutableStateOf(minRating) }
    var tempMaxDistance by remember(maxDistance) { mutableStateOf(maxDistance) }
    var tempAttachedBathroom by remember(selectedAttachedBathroom) { mutableStateOf(selectedAttachedBathroom) }
    var tempKitchen by remember(selectedKitchen) { mutableStateOf(selectedKitchen) }
    var tempAc by remember(selectedAc) { mutableStateOf(selectedAc) }
    var tempFood by remember(selectedFood) { mutableStateOf(selectedFood) }
    var tempCleanliness by remember(selectedCleanliness) { mutableStateOf(selectedCleanliness) }
    var tempMinReviews by remember(minReviews) { mutableStateOf(minReviews) }
    var amenitySearchQuery by remember { mutableStateOf("") }

    val defaultAmenities = listOf(
        "High-Speed Wi-Fi",
        "3-Time Meals Included",
        "Air Conditioner",
        "Attached Washroom",
        "RO Purified Water",
        "24/7 Power Backup",
        "Biometric / CCTV Security",
        "Daily Housekeeping",
        "Washing Machine / Laundry",
        "Silent Study Library",
        "Covered Car Parking",
        "Gym / Fitness Area"
    )

    val combinedAmenities = remember(availableAmenities) {
        val set = LinkedHashSet<String>(defaultAmenities)
        availableAmenities.forEach { if (it.isNotBlank()) set.add(it) }
        set.toList()
    }

    val filteredAmenityOptions = remember(combinedAmenities, amenitySearchQuery) {
        if (amenitySearchQuery.isBlank()) {
            combinedAmenities
        } else {
            combinedAmenities.filter { it.contains(amenitySearchQuery.trim(), ignoreCase = true) }
        }
    }

    val pricePresets = listOf(
        PricePreset("All Prices", 0f, 40000f),
        PricePreset("Budget (< ₹6k)", 0f, 6000f),
        PricePreset("₹6k - ₹12k", 6000f, 12000f),
        PricePreset("₹12k - ₹20k", 12000f, 20000f),
        PricePreset("Luxury (₹20k+)", 20000f, 40000f)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Refine Properties",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Filter by Price, Stay Type & Firestore Amenities",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_filters_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Filters"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Accommodation Type Section (Hostel, PG, Hotel, Flat, Quick Stay)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACCOMMODATION TYPE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (tempCategory != null) {
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { tempCategory = null }
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
                    val isSelected = tempCategory == type
                    val icon: ImageVector = when (type) {
                        PropertyType.HOSTEL -> Icons.Default.CorporateFare
                        PropertyType.PG -> Icons.Default.Home
                        PropertyType.HOTEL -> Icons.Default.Hotel
                        PropertyType.FLAT -> Icons.Default.Apartment
                        PropertyType.ROOM -> Icons.Default.Home
                        PropertyType.QUICK_STAY -> Icons.Default.Bolt
                    }
                    val count = propertyTypeCounts[type]

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                tempCategory = if (isSelected) null else type
                            }
                            .testTag("filter_type_${type.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            if (count != null && count > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.background
                                ) {
                                    Text(
                                        text = "$count",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Price Range Filter Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRICE RANGE",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${tempPriceRange.start.toInt()} - ₹${tempPriceRange.endInclusive.toInt()} /mo",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    RangeSlider(
                        value = tempPriceRange,
                        onValueChange = { tempPriceRange = it },
                        valueRange = 0f..40000f,
                        steps = 39,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.testTag("price_range_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price Quick Presets
                    Text(
                        text = "Quick Presets:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        pricePresets.forEach { preset ->
                            val isActive = (preset.min == null || tempPriceRange.start == preset.min) &&
                                           (preset.max == null || tempPriceRange.endInclusive == preset.max)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        tempPriceRange = (preset.min ?: 0f)..(preset.max ?: 40000f)
                                    }
                            ) {
                                Text(
                                    text = preset.label,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Distance Radius Filter Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth().testTag("distance_filter_section")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DISTANCE RADIUS",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (tempMaxDistance != null) "Within ${tempMaxDistance!!.toInt()} km" else "Any Distance",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (tempMaxDistance != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Clear",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable { tempMaxDistance = null }
                                        .padding(2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Slider(
                        value = (tempMaxDistance?.toFloat() ?: 50f),
                        onValueChange = { value ->
                            tempMaxDistance = if (value >= 48f) null else value.toDouble()
                        },
                        valueRange = 1f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.testTag("distance_radius_slider")
                    )

                    Text(
                        text = "Quick Distance Presets:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            2.0 to "< 2 km",
                            5.0 to "< 5 km",
                            10.0 to "< 10 km",
                            25.0 to "< 25 km",
                            null to "Any Distance"
                        ).forEach { (dist, label) ->
                            val isActive = tempMaxDistance == dist
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        tempMaxDistance = dist
                                    }
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📍 Calculated from your current location or selected locality hub",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Gender & Occupancy Suitability
            Text(
                text = "GENDER & OCCUPANCY SUITABILITY",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GenderSuitability.entries.forEach { gender ->
                    val isSelected = tempGender == gender
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            tempGender = if (isSelected) null else gender
                        },
                        label = {
                            Text(
                                text = gender.displayName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Essential Quick Filters (Attached Bathroom, Kitchen, AC, Food, Cleanliness, Reviews)
            Text(
                text = "ESSENTIAL PROPERTY FILTERS",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Attached Bathroom
                FilterChip(
                    selected = tempAttachedBathroom == true,
                    onClick = {
                        tempAttachedBathroom = if (tempAttachedBathroom == true) null else true
                    },
                    label = { Text("🚿 Attached Bathroom", fontSize = 12.sp, fontWeight = if (tempAttachedBathroom == true) FontWeight.Bold else FontWeight.Medium) },
                    leadingIcon = if (tempAttachedBathroom == true) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )

                // Kitchen / Cooking
                FilterChip(
                    selected = tempKitchen == true,
                    onClick = {
                        tempKitchen = if (tempKitchen == true) null else true
                    },
                    label = { Text("🍳 Kitchen Available", fontSize = 12.sp, fontWeight = if (tempKitchen == true) FontWeight.Bold else FontWeight.Medium) },
                    leadingIcon = if (tempKitchen == true) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )

                // Air Conditioned (AC)
                FilterChip(
                    selected = tempAc == true,
                    onClick = {
                        tempAc = if (tempAc == true) null else true
                    },
                    label = { Text("❄️ Air Conditioned (AC)", fontSize = 12.sp, fontWeight = if (tempAc == true) FontWeight.Bold else FontWeight.Medium) },
                    leadingIcon = if (tempAc == true) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )

                // Food / Meals Service
                FilterChip(
                    selected = tempFood == true,
                    onClick = {
                        tempFood = if (tempFood == true) null else true
                    },
                    label = { Text("🍲 Food / Mess Included", fontSize = 12.sp, fontWeight = if (tempFood == true) FontWeight.Bold else FontWeight.Medium) },
                    leadingIcon = if (tempFood == true) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )

                // Cleanliness (4.5+ Rating)
                FilterChip(
                    selected = tempCleanliness == true,
                    onClick = {
                        tempCleanliness = if (tempCleanliness == true) null else true
                    },
                    label = { Text("✨ High Cleanliness (4.5+)", fontSize = 12.sp, fontWeight = if (tempCleanliness == true) FontWeight.Bold else FontWeight.Medium) },
                    leadingIcon = if (tempCleanliness == true) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )

                // Reviews (5+ Reviews)
                FilterChip(
                    selected = tempMinReviews != null && tempMinReviews!! > 0,
                    onClick = {
                        tempMinReviews = if (tempMinReviews != null) null else 5
                    },
                    label = { Text("⭐ 5+ Real Reviews", fontSize = 12.sp, fontWeight = if (tempMinReviews != null) FontWeight.Bold else FontWeight.Medium) },
                    leadingIcon = if (tempMinReviews != null) {
                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Firestore Stored Amenities & Facilities
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AMENITIES & FACILITIES",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Firestore Synced",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (tempAmenities.isNotEmpty()) {
                    Text(
                        text = "Clear (${tempAmenities.size})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { tempAmenities = emptySet() }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Amenity Quick Search Field
            OutlinedTextField(
                value = amenitySearchQuery,
                onValueChange = { amenitySearchQuery = it },
                placeholder = { Text("Search amenities (e.g., AC, Food, Wi-Fi, CCTV)...", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (amenitySearchQuery.isNotEmpty()) {
                        IconButton(onClick = { amenitySearchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Amenities List
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (filteredAmenityOptions.isEmpty()) {
                        Text(
                            text = "No matching amenities found",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    } else {
                        filteredAmenityOptions.forEach { amenity ->
                            val isChecked = tempAmenities.contains(amenity)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        tempAmenities = if (isChecked) {
                                            tempAmenities - amenity
                                        } else {
                                            tempAmenities + amenity
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        tempAmenities = if (checked) tempAmenities + amenity else tempAmenities - amenity
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        checkmarkColor = Color.White
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = amenity,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Verification & Safety Toggle
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = StynoEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Verified Stays Only",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Physically audited with 24/7 security standards",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = tempVerifiedOnly,
                        onCheckedChange = { tempVerifiedOnly = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = StynoEmerald
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Sorting Selection
            Text(
                text = "SORT PREFERENCE",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortOption.entries.forEach { option ->
                    val isSelected = tempSort == option
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { tempSort = option }
                    ) {
                        Text(
                            text = option.displayName,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        tempAttachedBathroom = null
                        tempKitchen = null
                        tempAc = null
                        tempFood = null
                        tempCleanliness = null
                        tempMinReviews = null
                        onReset()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reset_filter_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset All")
                }

                Button(
                    onClick = {
                        if (onApplyExtended != null) {
                            onApplyExtended(
                                tempCategory,
                                tempGender,
                                tempSort,
                                tempAmenities,
                                tempVerifiedOnly,
                                if (tempPriceRange.start > 100f) tempPriceRange.start else null,
                                if (tempPriceRange.endInclusive < 39000f) tempPriceRange.endInclusive else null,
                                tempMinRating,
                                tempMaxDistance,
                                tempAttachedBathroom,
                                tempKitchen,
                                tempAc,
                                tempFood,
                                tempCleanliness,
                                tempMinReviews
                            )
                        } else {
                            onApply(
                                tempCategory,
                                tempGender,
                                tempSort,
                                tempAmenities,
                                tempVerifiedOnly,
                                if (tempPriceRange.start > 100f) tempPriceRange.start else null,
                                if (tempPriceRange.endInclusive < 39000f) tempPriceRange.endInclusive else null,
                                tempMinRating,
                                tempMaxDistance
                            )
                        }
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1.6f)
                        .testTag("apply_filter_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = if (matchingResultsCount != null) "Apply Filters ($matchingResultsCount)" else "Apply Filters",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

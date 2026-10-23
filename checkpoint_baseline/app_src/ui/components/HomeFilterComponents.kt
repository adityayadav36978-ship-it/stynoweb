package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PropertyType
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

/**
 * Top quick-access filter action bar for the Home Screen.
 * Allows users to trigger the full Filter Bottom Sheet or quickly
 * see and adjust Price Range, Amenities, and Property Type (Hostel, PG, etc.).
 */
@Composable
fun HomeFilterActionBar(
    activeFilterCount: Int,
    selectedCategory: PropertyType?,
    minPrice: Float?,
    maxPrice: Float?,
    selectedAmenities: Set<String>,
    onOpenFilterSheet: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isAnyFilterActive = activeFilterCount > 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Primary "All Filters" Button with Active Count Badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isAnyFilterActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            border = BorderStroke(
                1.dp,
                if (isAnyFilterActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onOpenFilterSheet() }
                .testTag("home_filter_bottom_sheet_btn")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filter Options",
                    tint = if (isAnyFilterActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = if (isAnyFilterActive) "Filters ($activeFilterCount)" else "Filters",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isAnyFilterActive) FontWeight.Bold else FontWeight.SemiBold
                    ),
                    color = if (isAnyFilterActive) Color.White else MaterialTheme.colorScheme.onSurface
                )

                if (isAnyFilterActive) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(StynoAccent)
                    )
                }
            }
        }

        // 2. Property Type Shortcut Chip (Hostel, PG, Hotel, Flat, etc.)
        val isTypeActive = selectedCategory != null
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isTypeActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                if (isTypeActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onOpenFilterSheet() }
                .testTag("home_filter_type_chip")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                val icon: ImageVector = when (selectedCategory) {
                    PropertyType.HOSTEL -> Icons.Default.CorporateFare
                    PropertyType.PG -> Icons.Default.Home
                    PropertyType.HOTEL -> Icons.Default.Hotel
                    PropertyType.FLAT -> Icons.Default.Apartment
                    PropertyType.ROOM -> Icons.Default.Home
                    PropertyType.QUICK_STAY -> Icons.Default.Bolt
                    null -> Icons.Default.CorporateFare
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isTypeActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )

                Text(
                    text = selectedCategory?.displayName ?: "Property Type",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isTypeActive) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isTypeActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isTypeActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // 3. Price Range Shortcut Chip
        val isPriceActive = minPrice != null || maxPrice != null
        val priceLabel = when {
            minPrice != null && maxPrice != null -> "₹${minPrice.toInt()} - ₹${maxPrice.toInt()}"
            minPrice != null -> "₹${minPrice.toInt()}+"
            maxPrice != null -> "Up to ₹${maxPrice.toInt()}"
            else -> "Price Range"
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isPriceActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                if (isPriceActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onOpenFilterSheet() }
                .testTag("home_filter_price_chip")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CurrencyRupee,
                    contentDescription = null,
                    tint = if (isPriceActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = priceLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isPriceActive) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isPriceActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isPriceActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // 4. Amenities Shortcut Chip
        val isAmenitiesActive = selectedAmenities.isNotEmpty()
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isAmenitiesActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                if (isAmenitiesActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onOpenFilterSheet() }
                .testTag("home_filter_amenities_chip")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (isAmenitiesActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = if (isAmenitiesActive) "Amenities (${selectedAmenities.size})" else "Amenities",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isAmenitiesActive) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isAmenitiesActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isAmenitiesActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // 5. Clear All Filters Button (Visible only when filters active)
        if (isAnyFilterActive) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClearFilters() }
                    .testTag("home_clear_all_filters_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Filters",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Header card displayed at the top of the Home Screen listing feed when filters are active.
 * Summarizes the filtered state, provides removable chip tags, and direct access to modify filters.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilteredResultsHeaderCard(
    matchedCount: Int,
    selectedCategory: PropertyType?,
    minPrice: Float?,
    maxPrice: Float?,
    selectedAmenities: Set<String>,
    verifiedOnly: Boolean,
    onModifyFilters: () -> Unit,
    onClearFilters: () -> Unit,
    onRemoveCategory: () -> Unit,
    onRemovePrice: () -> Unit,
    onRemoveAmenity: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("filtered_results_header_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Filtered Stays ($matchedCount)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (matchedCount > 0) "Showing verified stays matching your criteria" else "No matching properties found",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (matchedCount > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onModifyFilters,
                        modifier = Modifier.testTag("edit_filters_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Edit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextButton(
                        onClick = onClearFilters,
                        modifier = Modifier.testTag("clear_active_filters_btn")
                    ) {
                        Text(
                            text = "Clear",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Removable Filter Chips FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Property Type Chip
                if (selectedCategory != null) {
                    FilterRemovableChip(
                        label = selectedCategory.displayName,
                        icon = Icons.Default.CorporateFare,
                        onRemove = onRemoveCategory,
                        testTag = "removable_type_chip"
                    )
                }

                // Price Range Chip
                if (minPrice != null || maxPrice != null) {
                    val label = when {
                        minPrice != null && maxPrice != null -> "₹${minPrice.toInt()} - ₹${maxPrice.toInt()}"
                        minPrice != null -> "₹${minPrice.toInt()}+"
                        else -> "Up to ₹${maxPrice!!.toInt()}"
                    }
                    FilterRemovableChip(
                        label = label,
                        icon = Icons.Default.CurrencyRupee,
                        onRemove = onRemovePrice,
                        testTag = "removable_price_chip"
                    )
                }

                // Verified Only Chip
                if (verifiedOnly) {
                    FilterRemovableChip(
                        label = "Verified Only",
                        icon = Icons.Default.Check,
                        onRemove = onClearFilters,
                        testTag = "removable_verified_chip"
                    )
                }

                // Amenity Chips
                selectedAmenities.forEach { amenity ->
                    FilterRemovableChip(
                        label = amenity,
                        icon = Icons.Default.Star,
                        onRemove = { onRemoveAmenity(amenity) },
                        testTag = "removable_amenity_${amenity.lowercase().replace(" ", "_")}"
                    )
                }
            }
        }
    }
}

/**
 * Individual pill chip that can be tapped to remove a specific filter.
 */
@Composable
private fun FilterRemovableChip(
    label: String,
    icon: ImageVector,
    onRemove: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onRemove() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove $label",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

/**
 * Empty state card shown when active filters yield zero matching properties on Home Screen.
 */
@Composable
fun FilterEmptyStateCard(
    onOpenFilterSheet: () -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("filter_empty_state_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "No Properties Match Your Filters",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Try adjusting your price range, choosing fewer amenities, or exploring all property types (Hostel, PG, Hotel, Flat).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onResetFilters,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("empty_state_reset_btn")
                ) {
                    Text("Clear All Filters", fontSize = 12.sp)
                }

                Button(
                    onClick = onOpenFilterSheet,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("empty_state_adjust_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Adjust Filters", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

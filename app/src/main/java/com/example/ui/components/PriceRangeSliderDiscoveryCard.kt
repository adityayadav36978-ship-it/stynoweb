package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

data class PriceRangePresetItem(
    val label: String,
    val minPrice: Float?,
    val maxPrice: Float?,
    val subtitle: String = ""
)

/**
 * Interactive Range Slider Card on the Main Discovery Screen.
 * Allows users to easily filter accommodations by a specific price per night range
 * with live visual feedback, quick price presets, and instant result discovery.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PriceRangeSliderDiscoveryCard(
    minPrice: Float?,
    maxPrice: Float?,
    onPriceRangeChanged: (min: Float?, max: Float?) -> Unit,
    onExploreResults: () -> Unit,
    matchedStayCount: Int,
    modifier: Modifier = Modifier,
    isNightlyMode: Boolean = true,
    onResetFilters: () -> Unit = { onPriceRangeChanged(null, null) }
) {
    var isExpanded by remember { mutableStateOf(true) }

    val minLimit = 0f
    val maxLimit = if (isNightlyMode) 15000f else 40000f
    val currentStart = (minPrice ?: minLimit).coerceIn(minLimit, maxLimit)
    val currentEnd = (maxPrice ?: maxLimit).coerceIn(minLimit, maxLimit)

    var localRange by remember(minPrice, maxPrice, isNightlyMode) {
        mutableStateOf(currentStart..currentEnd)
    }

    val pricePresets = remember(isNightlyMode) {
        if (isNightlyMode) {
            listOf(
                PriceRangePresetItem("All Rates", null, null, "Any budget"),
                PriceRangePresetItem("Budget (< ₹1,000)", 0f, 1000f, "Pocket-friendly"),
                PriceRangePresetItem("₹1,000 - ₹2,500", 1000f, 2500f, "Standard stays"),
                PriceRangePresetItem("₹2,500 - ₹5,000", 2500f, 5000f, "Deluxe & Boutique"),
                PriceRangePresetItem("Luxury (₹5,000+)", 5000f, 15000f, "Premium experience")
            )
        } else {
            listOf(
                PriceRangePresetItem("All Rates", null, null, "Any budget"),
                PriceRangePresetItem("Under ₹6,000", 0f, 6000f, "Budget friendly"),
                PriceRangePresetItem("₹6,000 - ₹12,000", 6000f, 12000f, "Student & Youth"),
                PriceRangePresetItem("₹12,000 - ₹20,000", 12000f, 20000f, "Working professionals"),
                PriceRangePresetItem("₹20,000+", 20000f, 40000f, "Premium apartments")
            )
        }
    }

    val isFilterActive = minPrice != null || maxPrice != null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("discovery_price_range_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isFilterActive) 1.5.dp else 1.dp,
            color = if (isFilterActive) StynoBluePrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with Title & Expand Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(StynoBluePrimary, StynoAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "Price Range Filter",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Price Per Night Range",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.2.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (isFilterActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StynoEmerald.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Active",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = StynoEmerald,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isFilterActive) {
                                "Filtering: ₹${localRange.start.toInt()} – ₹${localRange.endInclusive.toInt()} / night"
                            } else {
                                "Filter search results by specific price per night"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isFilterActive) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isFilterActive) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.testTag("toggle_price_range_expand_btn")
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse Price Range" else "Expand Price Range",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    // Range Value Badges Display
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MINIMUM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${localRange.start.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = StynoBluePrimary
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = StynoBluePrimary.copy(alpha = 0.12f),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "TO",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp
                                ),
                                color = StynoBluePrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "MAXIMUM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (localRange.endInclusive >= maxLimit) "₹${localRange.endInclusive.toInt()}+" else "₹${localRange.endInclusive.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = StynoBluePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Material 3 Range Slider
                    RangeSlider(
                        value = localRange,
                        onValueChange = { range ->
                            localRange = range
                            val effectiveMin = if (range.start > minLimit + 10f) range.start else null
                            val effectiveMax = if (range.endInclusive < maxLimit - 10f) range.endInclusive else null
                            onPriceRangeChanged(effectiveMin, effectiveMax)
                        },
                        valueRange = minLimit..maxLimit,
                        steps = 29,
                        colors = SliderDefaults.colors(
                            thumbColor = StynoBluePrimary,
                            activeTrackColor = StynoBluePrimary,
                            inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("discovery_price_range_slider")
                    )

                    // Quick Nightly Price Presets
                    Text(
                        text = "Quick Presets:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pricePresets.forEachIndexed { index, preset ->
                            val isSelected = if (preset.minPrice == null && preset.maxPrice == null) {
                                minPrice == null && maxPrice == null
                            } else {
                                val matchesMin = preset.minPrice != null && kotlin.math.abs((minPrice ?: minLimit) - preset.minPrice) < 50f
                                val matchesMax = preset.maxPrice != null && kotlin.math.abs((maxPrice ?: maxLimit) - preset.maxPrice) < 50f
                                matchesMin && matchesMax
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (preset.minPrice == null && preset.maxPrice == null) {
                                            localRange = minLimit..maxLimit
                                            onPriceRangeChanged(null, null)
                                        } else {
                                            val start = preset.minPrice ?: minLimit
                                            val end = preset.maxPrice ?: maxLimit
                                            localRange = start..end
                                            onPriceRangeChanged(preset.minPrice, preset.maxPrice)
                                        }
                                    }
                                    .testTag("price_preset_chip_$index")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = preset.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Action Strip with Result Counter & Explore Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Matched Stays Count Chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StynoBlueLight.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = StynoBluePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$matchedStayCount stays found",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = StynoBluePrimary
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isFilterActive) {
                                OutlinedButton(
                                    onClick = {
                                        localRange = minLimit..maxLimit
                                        onResetFilters()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    ),
                                    modifier = Modifier.testTag("reset_price_filter_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset Price Range",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Reset",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Button(
                                onClick = onExploreResults,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StynoBluePrimary
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),
                                modifier = Modifier.testTag("apply_price_filter_btn")
                            ) {
                                Text(
                                    text = "View Results",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "View Filtered Results",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

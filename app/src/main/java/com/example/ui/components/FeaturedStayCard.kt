package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DurationType
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import java.text.NumberFormat
import java.util.Locale

/**
 * Calculates accurate price per night for any property.
 * Accommodates Hotels (daily rates), Quick Stays (overnight slots),
 * and monthly PGs/Hostels/Flats (calculated daily equivalent).
 */
fun calculatePricePerNight(property: Property): Double {
    return when {
        property.dailyPrice > 0.0 -> property.dailyPrice
        property.durationType == DurationType.DAILY -> property.startingPrice
        property.propertyType == PropertyType.HOTEL -> property.startingPrice
        property.durationType == DurationType.HOURLY -> {
            // Find overnight / 24h option if available, otherwise 3.5x hourly starting rate
            val overnight = property.roomOptions.find {
                it.durationType == DurationType.DAILY ||
                    it.name.contains("24", ignoreCase = true) ||
                    it.name.contains("Overnight", ignoreCase = true)
            }
            overnight?.price ?: (property.startingPrice * 3.5).coerceAtLeast(699.0)
        }
        property.durationType == DurationType.MONTHLY -> {
            // If property has daily option in room options, use it, otherwise daily equivalent
            val dailyOption = property.roomOptions.find { it.durationType == DurationType.DAILY }
            dailyOption?.price ?: Math.round(property.startingPrice / 28.0).toDouble().coerceAtLeast(299.0)
        }
        else -> property.startingPrice
    }
}

/**
 * Formats price per night as Indian Rupee string, e.g. "₹2,499".
 */
fun formatPricePerNightOnly(property: Property): String {
    val price = calculatePricePerNight(property)
    return NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        .format(price)
        .replace(".00", "")
}

/**
 * Formats full price per night string, e.g. "₹2,499 / night".
 */
fun formatPricePerNight(property: Property): String {
    return "${formatPricePerNightOnly(property)} / night"
}

/**
 * Featured Stay Card designed for vertical lists below SearchBar.
 * Includes thumbnail image, property name, location, and price per night.
 */
@Composable
fun FeaturedStayCard(
    property: Property,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
            .testTag("featured_stay_card_${property.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Thumbnail Image with rounded corners & badges
            Box(
                modifier = Modifier
                    .size(width = 112.dp, height = 112.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("featured_stay_thumbnail_${property.id}")
            ) {
                val imageDrawable = property.imageDrawableNames.firstOrNull() ?: "img_hotel_suite"
                PropertyPhotoItem(
                    imageName = imageDrawable,
                    propertyName = property.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Rating overlay badge on top-left of thumbnail
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StynoAccent,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "%.1f".format(Locale.US, property.rating),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }

                // Property type tag on bottom of thumbnail
                Surface(
                    shape = RoundedCornerShape(topEnd = 6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = property.propertyType.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // 2. Stay Information Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Property Name and Favorite Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = property.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 19.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("featured_stay_title_${property.id}")
                    )

                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("featured_stay_save_${property.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isSaved) "Remove from saved" else "Save stay",
                            tint = if (isSaved) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Location Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("featured_stay_location_${property.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (property.area.isNotBlank()) "${property.area}, ${property.city}" else property.city,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Verification & Highlights Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (property.verificationStatus == VerificationStatus.VERIFIED) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = StynoEmerald,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Verified",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = StynoEmerald,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (property.shortFacilities.isNotEmpty()) {
                        Text(
                            text = "• ${property.shortFacilities.first()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Price Per Night Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("featured_stay_price_${property.id}"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "Price per night",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            val formattedPrice = formatPricePerNightOnly(property)
                            Text(
                                text = formattedPrice,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " / night",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 1.dp)
                            )
                        }
                    }

                    // View Stay Action
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.clickable { onCardClick() }
                    ) {
                        Text(
                            text = "View Stay →",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section Header for the Featured Stays vertical list.
 */
@Composable
fun FeaturedStaysHeader(
    count: Int,
    selectedCategory: PropertyType? = null,
    selectedCity: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("featured_stays_header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(StynoAccent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StynoAccent,
                    modifier = Modifier.size(14.dp)
                )
            }

            val titleSuffix = when {
                selectedCategory != null -> " ${selectedCategory.displayName}s"
                selectedCity != null -> " in $selectedCity"
                else -> ""
            }

            Text(
                text = "Featured Stays$titleSuffix",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = StynoBluePrimary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = StynoBluePrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = StynoEmerald,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Top Verified",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Vertical list of Featured Stays cards.
 */
@Composable
fun FeaturedStaysVerticalList(
    featuredStays: List<Property>,
    savedPropertyIds: Set<String>,
    onPropertyClick: (Property) -> Unit,
    onSaveClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("featured_stays_vertical_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        featuredStays.forEach { property ->
            FeaturedStayCard(
                property = property,
                isSaved = savedPropertyIds.contains(property.id),
                onCardClick = { onPropertyClick(property) },
                onSaveClick = { onSaveClick(property.id) }
            )
        }
    }
}

/**
 * Horizontal scrollable row of 'Property Type' chips (Hotel, PG, Hostel, Flat, etc.)
 * allowing users to filter the 'Featured Stays' list.
 */
@Composable
fun PropertyTypeFilterChipsRow(
    selectedCategory: PropertyType?,
    onCategorySelected: (PropertyType?) -> Unit,
    propertyTypeCounts: Map<PropertyType, Int> = emptyMap(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .testTag("featured_property_type_chips_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All Stays" Chip
        val isAllSelected = selectedCategory == null
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            border = BorderStroke(
                1.dp,
                if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onCategorySelected(null) }
                .testTag("featured_stay_chip_all")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (isAllSelected) Color.White else StynoAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "All Stays",
                    fontSize = 12.sp,
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Specific Property Type Chips (Hostel, PG, Flat, Room, Hotel, Quick Stay)
        val orderedTypes = listOf(
            PropertyType.HOSTEL,
            PropertyType.PG,
            PropertyType.FLAT,
            PropertyType.ROOM,
            PropertyType.HOTEL,
            PropertyType.QUICK_STAY
        )
        orderedTypes.forEach { type ->
            val isSelected = selectedCategory == type
            val count = propertyTypeCounts[type] ?: 0

            val (icon, tint) = when (type) {
                PropertyType.HOSTEL -> Icons.Default.Apartment to Color(0xFF7C3AED)
                PropertyType.PG -> Icons.Default.CorporateFare to Color(0xFF2563EB)
                PropertyType.FLAT -> Icons.Default.Home to Color(0xFF059669)
                PropertyType.ROOM -> Icons.Default.Hotel to Color(0xFFD97706)
                PropertyType.HOTEL -> Icons.Default.Hotel to Color(0xFFE11D48)
                PropertyType.QUICK_STAY -> Icons.Default.Bolt to Color(0xFFF59E0B)
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        if (isSelected) onCategorySelected(null) else onCategorySelected(type)
                    }
                    .testTag("featured_stay_chip_${type.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else tint,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = type.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    if (count > 0) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = count.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


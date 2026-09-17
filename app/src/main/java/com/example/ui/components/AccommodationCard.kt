package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.VerificationStatus
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import java.text.NumberFormat
import java.util.Locale

/**
 * Vertical LazyColumn displaying accommodation cards below the search bar.
 * Presents a scrollable list of accommodation stays with preview images,
 * titles, prices, ratings, and instant interaction callbacks.
 */
@Composable
fun AccommodationVerticalLazyColumn(
    accommodations: List<Property>,
    onAccommodationClick: (Property) -> Unit,
    onSaveClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    savedPropertyIds: Set<String> = emptySet(),
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    headerContent: (@Composable () -> Unit)? = null,
    emptyContent: (@Composable () -> Unit)? = null
) {
    if (accommodations.isEmpty() && emptyContent != null) {
        emptyContent()
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .testTag("accommodations_vertical_lazy_column"),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (headerContent != null) {
                item(key = "accommodation_list_header") {
                    headerContent()
                }
            }

            items(accommodations, key = { it.id }) { accommodation ->
                AccommodationCard(
                    accommodation = accommodation,
                    isSaved = savedPropertyIds.contains(accommodation.id),
                    onClick = { onAccommodationClick(accommodation) },
                    onSaveClick = { onSaveClick(accommodation.id) }
                )
            }
        }
    }
}

/**
 * Accommodation card presenting stay information:
 * - Preview Image with accommodation type tag and save heart button
 * - Title and verified status
 * - Rating and review count
 * - Price per month/night
 * - Location and top amenities
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccommodationCard(
    accommodation: Property,
    isSaved: Boolean,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedPrice = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        .format(accommodation.startingPrice)
        .replace(".00", "")

    val durationSuffix = when (accommodation.durationType) {
        DurationType.HOURLY -> "/slot"
        DurationType.DAILY -> "/night"
        DurationType.WEEKLY -> "/week"
        DurationType.MONTHLY -> "/month"
        DurationType.YEARLY -> "/year"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .softShadow(
                color = Color(0x140F172A),
                borderRadius = 20.dp,
                blurRadius = 14.dp,
                offsetY = 4.dp
            )
            .clip(RoundedCornerShape(20.dp))
            .pressClickEffect()
            .clickable { onClick() }
            .testTag("accommodation_card_${accommodation.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Preview Image with Overlay Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                PropertyPhotoItem(
                    imageName = accommodation.imageDrawableNames.firstOrNull() ?: "img_hostel_modern",
                    propertyName = accommodation.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle gradient overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x99000000)),
                                startY = 90f
                            )
                        )
                )

                // Top Floating Chips: Accommodation Type, Gender, and Wishlist Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Accommodation Type Tag (Hostel, PG, Flat, etc.)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StynoBluePrimary.copy(alpha = 0.92f)
                        ) {
                            Text(
                                text = accommodation.propertyType.displayName.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Gender Suitability Tag
                        if (accommodation.genderSuitability != GenderSuitability.ALL) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (accommodation.genderSuitability) {
                                    GenderSuitability.GIRLS_ONLY -> Color(0xFFBE185D).copy(alpha = 0.9f)
                                    GenderSuitability.BOYS_ONLY -> Color(0xFF0369A1).copy(alpha = 0.9f)
                                    GenderSuitability.FAMILY -> Color(0xFF7C3AED).copy(alpha = 0.9f)
                                    else -> Color(0xFF334155).copy(alpha = 0.9f)
                                }
                            ) {
                                Text(
                                    text = accommodation.genderSuitability.displayName,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Wishlist Save Button
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(36.dp)
                    ) {
                        IconButton(
                            onClick = onSaveClick,
                            modifier = Modifier.testTag("accommodation_save_${accommodation.id}")
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isSaved) "Remove from saved stays" else "Save stay",
                                tint = if (isSaved) Color(0xFFEF4444) else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Bottom Overlay: Verification & Availability
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (accommodation.verificationStatus == VerificationStatus.VERIFIED) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StynoEmerald.copy(alpha = 0.92f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified Stay",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "VERIFIED",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (accommodation.isAvailable) Color(0xCC059669) else Color(0xCCD97706)
                    ) {
                        Text(
                            text = if (accommodation.isAvailable) "● Available" else "● Few Rooms Left",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 2. Card Content (Title, Rating, Location, Facilities, Price)
            Column(modifier = Modifier.padding(14.dp)) {
                // Title and Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title
                    Text(
                        text = accommodation.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Rating Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7),
                        tonalElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${accommodation.rating}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = " (${accommodation.reviewCount})",
                                fontSize = 10.sp,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Location Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${accommodation.area}, ${accommodation.city}" +
                            if (accommodation.nearbyLandmark.isNotBlank()) " • ${accommodation.nearbyLandmark}" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Facilities Chips
                if (accommodation.shortFacilities.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        accommodation.shortFacilities.take(3).forEach { facility ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = facility,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Price & Call to Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Starts from",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = formattedPrice,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 19.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " $durationSuffix",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .pressClickEffect()
                            .testTag("accommodation_details_${accommodation.id}")
                    ) {
                        Text(
                            text = "View Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Direct AccommodationCard overload accepting explicit preview image, title, price, and rating.
 */
@Composable
fun AccommodationCard(
    imageName: String,
    title: String,
    price: String,
    rating: Float,
    reviewCount: Int = 0,
    accommodationType: String = "HOSTEL",
    location: String = "",
    isSaved: Boolean = false,
    onClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .pressClickEffect()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Preview Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                PropertyPhotoItem(
                    imageName = imageName,
                    propertyName = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Accommodation Type Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StynoBluePrimary.copy(alpha = 0.92f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = accommodationType.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Wishlist Button
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                ) {
                    IconButton(onClick = onSaveClick) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Save stay",
                            tint = if (isSaved) Color(0xFFEF4444) else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Details
            Column(modifier = Modifier.padding(14.dp)) {
                // Title and Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$rating",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            if (reviewCount > 0) {
                                Text(
                                    text = " ($reviewCount)",
                                    fontSize = 10.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }

                if (location.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Starts from",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = price,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View Details", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

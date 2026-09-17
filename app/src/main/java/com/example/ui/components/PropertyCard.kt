package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.VerificationStatus
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyCard(
    property: Property,
    isSaved: Boolean,
    onCardClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    bottomContent: (@Composable () -> Unit)? = null,
    animateEntrance: Boolean = true,
    staggerDelayMs: Int = 0
) {
    val density = LocalDensity.current
    val initialOffsetPx = with(density) { 36.dp.toPx() }
    val animOffsetY = remember(property.id) { Animatable(if (animateEntrance) initialOffsetPx else 0f) }
    val animAlpha = remember(property.id) { Animatable(if (animateEntrance) 0f else 1f) }

    if (animateEntrance) {
        LaunchedEffect(property.id) {
            if (staggerDelayMs > 0) {
                delay(staggerDelayMs.toLong())
            }
            launch {
                animOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 360,
                        easing = FastOutSlowInEasing
                    )
                )
            }
            launch {
                animAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 280,
                        easing = LinearOutSlowInEasing
                    )
                )
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                if (animateEntrance) {
                    translationY = animOffsetY.value
                    alpha = animAlpha.value
                }
            }
            .softShadow(
                color = Color(0x140F172A),
                borderRadius = 20.dp,
                blurRadius = 14.dp,
                offsetY = 4.dp
            )
            .clip(RoundedCornerShape(20.dp))
            .pressClickEffect()
            .clickable { onCardClick() }
            .testTag("property_card_${property.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Large Property Image with Overlay Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(205.dp)
            ) {
                PropertyPhotoItem(
                    imageName = property.imageDrawableNames.firstOrNull() ?: "img_hostel_modern",
                    propertyName = property.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for bottom text contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x99000000)),
                                startY = 100f
                            )
                        )
                )

                // Top Floating Chips: Category & Verification
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Category Chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StynoBluePrimary.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = property.propertyType.displayName.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Gender / Audience Chip
                        if (property.genderSuitability != GenderSuitability.ALL) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (property.genderSuitability) {
                                    GenderSuitability.GIRLS_ONLY -> Color(0xFFBE185D).copy(alpha = 0.9f)
                                    GenderSuitability.BOYS_ONLY -> Color(0xFF0369A1).copy(alpha = 0.9f)
                                    GenderSuitability.FAMILY -> Color(0xFF7C3AED).copy(alpha = 0.9f)
                                    else -> Color(0xFF334155).copy(alpha = 0.9f)
                                }
                            ) {
                                Text(
                                    text = property.genderSuitability.displayName,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Top Right Action Buttons (Share & Wishlist)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Social Share Button
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.95f),
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(36.dp)
                        ) {
                            val context = LocalContext.current
                            IconButton(
                                onClick = {
                                    val priceFormatted = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                                        .format(property.startingPrice)
                                        .replace(".00", "")
                                    val shareText = buildString {
                                        appendLine("🏡 ${property.name}")
                                        appendLine("📍 ${property.area}, ${property.city}")
                                        appendLine("💰 Starting: $priceFormatted/${if (property.durationType == DurationType.DAILY) "night" else "month"}")
                                        appendLine("⭐ Rating: ${property.rating}★ (${property.reviewCount} reviews)")
                                        appendLine("✨ Highlights: ${property.shortFacilities.take(3).joinToString(", ")}")
                                        appendLine()
                                        appendLine("View stay details on STYNO Accommodation:")
                                        append("https://styno.com/property/${property.id}")
                                    }

                                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out ${property.name} on STYNO")
                                        putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Property via"))
                                },
                                modifier = Modifier.testTag("share_button_${property.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share property with friends",
                                    tint = Color(0xFF334155),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Save Heart Button
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.95f),
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(36.dp)
                        ) {
                            IconButton(
                                onClick = onSaveClick,
                                modifier = Modifier.testTag("save_button_${property.id}")
                            ) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = if (isSaved) "Remove from saved stays" else "Save stay to shortlist",
                                    tint = if (isSaved) Color(0xFFEF4444) else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
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
                    if (property.verificationStatus == VerificationStatus.VERIFIED) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StynoEmerald.copy(alpha = 0.9f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "VERIFIED BY STYNO",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (property.isAvailable) Color(0xCC059669) else Color(0xCCD97706)
                    ) {
                        Text(
                            text = if (property.isAvailable) "● Available" else "● Limited Beds",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Card Body Information
            Column(modifier = Modifier.padding(14.dp)) {
                // Name and Rating Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = property.name,
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
                                text = "${property.rating}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = " (${property.reviewCount})",
                                fontSize = 10.sp,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Location & Landmark
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${property.area}, ${property.city} • ${property.nearbyLandmark}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Facilities Chips Row
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    property.shortFacilities.take(4).forEach { facility ->
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

                Spacer(modifier = Modifier.height(12.dp))

                // Divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pricing & CTA Row
                val formattedPrice = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                    .format(property.startingPrice)
                    .replace(".00", "")

                val durationSuffix = when (property.durationType) {
                    DurationType.HOURLY -> "/slot"
                    DurationType.DAILY -> "/night"
                    DurationType.WEEKLY -> "/week"
                    DurationType.MONTHLY -> "/month"
                    DurationType.YEARLY -> "/year"
                }

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
                        onClick = onCardClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .pressClickEffect()
                            .testTag("view_details_${property.id}")
                    ) {
                        Text(
                            text = "View Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                if (bottomContent != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    bottomContent()
                }
            }
        }
    }
}

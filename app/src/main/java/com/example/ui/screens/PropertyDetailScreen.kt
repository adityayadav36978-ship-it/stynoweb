package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import com.example.ui.components.PropertyLocationMapView
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.Review
import com.example.data.model.RoomOption
import com.example.data.model.VerificationStatus
import com.example.ui.components.DynamicPriceBreakdownCard
import com.example.ui.components.DynamicPriceCalculation
import com.example.ui.components.PropertyImageCarousel
import com.example.ui.components.PropertyPriceTrendsCard
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyDetailScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val property = viewModel.selectedProperty.collectAsStateWithLifecycle().value

    if (property == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Property not found", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { viewModel.navigateBack() }) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    val savedIds by viewModel.savedPropertyIds.collectAsStateWithLifecycle()
    val isSaved = savedIds.contains(property.id)
    val propertyReviewsMap by viewModel.propertyReviews.collectAsStateWithLifecycle()
    val reviews = propertyReviewsMap[property.id] ?: remember(property.id) { viewModel.getReviews(property.id) }
    val foodMenu = remember(property.id) { viewModel.getFoodMenu(property.id) }

    var selectedRoomOption by remember { mutableStateOf(property.roomOptions.firstOrNull()) }
    var activeCalculation by remember { mutableStateOf<DynamicPriceCalculation?>(null) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewFilter by remember { mutableStateOf("All") }
    var reviewSort by remember { mutableStateOf("Recent") }

    val filteredReviews = remember(reviews, reviewFilter, reviewSort) {
        val list = reviews.filter { r ->
            when (reviewFilter) {
                "5 ★" -> r.rating >= 4.9f
                "4 ★+" -> r.rating >= 4.0f
                "Verified" -> r.verifiedStay
                "With Reply" -> !r.ownerResponse.isNullOrBlank()
                else -> true
            }
        }
        when (reviewSort) {
            "Highest" -> list.sortedByDescending { it.rating }
            "Helpful" -> list.sortedByDescending { it.helpfulCount }
            else -> list
        }
    }

    val images = property.imageDrawableNames.ifEmpty { listOf("img_hostel_modern") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 1. Photo Gallery Carousel with Animated Pagination Indicators, Thumbnails & Lightbox
            item {
                PropertyImageCarousel(
                    images = images,
                    propertyName = property.name,
                    carouselHeight = 290.dp,
                    showThumbnails = true,
                    topOverlay = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.9f),
                                shadowElevation = 4.dp,
                                modifier = Modifier.size(40.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.navigateBack() },
                                    modifier = Modifier.testTag("detail_back_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.Black
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.9f),
                                    shadowElevation = 4.dp,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            sharePropertyDetails(context, property)
                                        },
                                        modifier = Modifier.testTag("detail_share_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.95f),
                                    shadowElevation = 4.dp,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.toggleSave(property.id) },
                                        modifier = Modifier.testTag("detail_save_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                            contentDescription = if (isSaved) "Remove from favorites" else "Add to favorites",
                                            tint = if (isSaved) Color(0xFFEF4444) else Color(0xFF1E293B),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }

            // 2. Property Overview & Verification Banner
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Category & Gender Tags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StynoBluePrimary
                        ) {
                            Text(
                                text = property.propertyType.displayName.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = property.genderSuitability.displayName,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = property.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Address & Landmark
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${property.address}, ${property.city} • ${property.nearbyLandmark}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Verified Badge Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = StynoEmerald.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = StynoEmerald,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "STYNO 100% Verified Property",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    text = "Physical audit, warden KYC & security verification completed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Action Action Row: Share, Call Host, View on Map
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { sharePropertyDetails(context, property) }
                                .testTag("overview_share_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Stay",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Share Stay",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${property.ownerInfo.phone}"))
                                    context.startActivity(dialIntent)
                                }
                                .testTag("overview_call_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call Host",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Call Host",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Room Database Offline Access Indicator
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0284C7).copy(alpha = 0.25f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OfflinePin,
                                        contentDescription = "Offline Cache",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Cached in Room Database",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = StynoEmerald.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "Offline Ready ⚡",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StynoEmerald,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Full pricing, room options, food menu & contacts stored on-device",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Room & Occupancy Options Selector
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Available Room Options",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    property.roomOptions.forEach { option ->
                        val isSelected = selectedRoomOption?.id == option.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedRoomOption = option }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = option.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "${option.sharingType} • ${if (option.isAcAvailable) "AC Room" else "Non-AC"} • Security Deposit: ₹${option.securityDeposit.toInt()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (option.availableBeds > 0) {
                                        Text(
                                            text = "● ${option.availableBeds} units available",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = StynoEmerald
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    val formatted = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                                        .format(option.price)
                                        .replace(".00", "")
                                    Text(
                                        text = formatted,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = when (option.durationType) {
                                            DurationType.HOURLY -> "/slot"
                                            DurationType.DAILY -> "/night"
                                            DurationType.WEEKLY -> "/week"
                                            DurationType.MONTHLY -> "/month"
                                            DurationType.YEARLY -> "/year"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Dynamic Live Stay Dates & Price Breakdown Card
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DynamicPriceBreakdownCard(
                        property = property,
                        selectedRoomOption = selectedRoomOption,
                        onProceedToBooking = { checkIn, checkOut, duration, totalPayable ->
                            viewModel.startBookingFlow(
                                property = property,
                                preSelectedRoom = selectedRoomOption,
                                checkInDate = checkIn,
                                checkOutDate = checkOut,
                                durationText = duration,
                                calculatedTotal = totalPayable,
                                discountAmount = activeCalculation?.couponDiscount
                            )
                        },
                        onCalculationChanged = { calc ->
                            activeCalculation = calc
                        }
                    )
                }
            }

            // 5. Historical Price Trends & Best Time to Book Analytics
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    PropertyPriceTrendsCard(property = property)
                }
            }

            // 5. Daily Canteen Food Menu Section (if applicable)
            if (property.hasCanteenMenu) {
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RestaurantMenu,
                                    contentDescription = null,
                                    tint = StynoAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Daily Canteen Food Menu",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = "FSSAI 4.9 ★ Hygiene",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "TODAY'S FRESH MEAL SCHEDULE (${foodMenu.todayDate})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                foodMenu.meals.forEach { meal ->
                                    MealRow("${meal.mealType} (${meal.timing})", meal.items.joinToString(", "))
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "✨ Hygiene Score: ${foodMenu.hygieneScore} / 5.0 • FSSAI Certified Kitchen",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = StynoAccent
                                )
                            }
                        }
                    }
                }
            }

            // 5. All Amenities & Facilities Grid
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Facilities & Amenities",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        property.allAmenities.forEach { amenity ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clip(RoundedCornerShape(10.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StynoEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = amenity,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. House Rules & Timings
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Rules & Stay Policies",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TimingItem("Check-In", property.checkInTime)
                                TimingItem("Check-Out", property.checkOutTime)
                                TimingItem("Gate Closing", property.gateClosingTime)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            property.rules.forEach { rule ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Policy,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = rule.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = rule.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6.5. Geographical Location & Google Maps View
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    PropertyLocationMapView(property = property)
                }
            }

            // 7. Property Manager & Host Contact Card
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Property Warden & Host",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = property.ownerInfo.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${property.ownerInfo.responseTime} • ${property.ownerInfo.joinedDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${property.ownerInfo.phone}"))
                                        context.startActivity(dialIntent)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { viewModel.navigateTo(Screen.CHAT) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Chat", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 7.5 Optional Station/Airport Pickup Service Card
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Optional Station / Airport Pickup",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Directions,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Verified PG Shuttle & Cab Pickup Available",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Add during checkout from ₹249 with live driver contact",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 8. Reviews & Ratings Section
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("reviews_section")
                ) {
                    // Header with Title & Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Resident Reviews & Ratings",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${reviews.size} verified resident feedback submissions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { showReviewDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("write_review_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Write Review", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rating Summary Hero Scorecard
                    val avgRating = if (reviews.isNotEmpty()) {
                        reviews.map { it.rating }.average().toFloat()
                    } else property.rating

                    val fiveStarCount = reviews.count { it.rating >= 4.9f }
                    val fourStarCount = reviews.count { it.rating in 4.0f..4.89f }
                    val threeStarCount = reviews.count { it.rating in 3.0f..3.99f }
                    val twoStarCount = reviews.count { it.rating in 2.0f..2.99f }
                    val oneStarCount = reviews.count { it.rating < 2.0f }
                    val totalRevCount = reviews.size.coerceAtLeast(1)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left: Big Score
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(end = 16.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.1f", avgRating),
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 38.sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        (1..5).forEach { star ->
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (star <= Math.round(avgRating)) Color(0xFFF59E0B) else Color(0xFFD1D5DB),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "out of 5.0",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Right: Star distribution bars
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    StarDistributionRow("5★", fiveStarCount, totalRevCount, Color(0xFF10B981))
                                    StarDistributionRow("4★", fourStarCount, totalRevCount, Color(0xFF3B82F6))
                                    StarDistributionRow("3★", threeStarCount, totalRevCount, Color(0xFFF59E0B))
                                    StarDistributionRow("2★", twoStarCount, totalRevCount, Color(0xFFF97316))
                                    StarDistributionRow("1★", oneStarCount, totalRevCount, Color(0xFFEF4444))
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )

                            // Average Dimension Score Chips
                            Text(
                                text = "Category Performance Scores",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val avgClean = if (reviews.isNotEmpty()) reviews.map { it.breakdown.cleanliness }.average().toFloat() else 4.9f
                            val avgFood = if (reviews.isNotEmpty()) reviews.map { it.breakdown.foodQuality }.average().toFloat() else 4.8f
                            val avgSafety = if (reviews.isNotEmpty()) reviews.map { it.breakdown.safety }.average().toFloat() else 5.0f
                            val avgStaff = if (reviews.isNotEmpty()) reviews.map { it.breakdown.staffBehavior }.average().toFloat() else 4.9f
                            val avgRoom = if (reviews.isNotEmpty()) reviews.map { it.breakdown.roomQuality }.average().toFloat() else 4.8f
                            val avgLoc = if (reviews.isNotEmpty()) reviews.map { it.breakdown.location }.average().toFloat() else 4.7f
                            val avgValue = if (reviews.isNotEmpty()) reviews.map { it.breakdown.valueForMoney }.average().toFloat() else 4.8f

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CategoryScorePill("🧹 Cleanliness", avgClean)
                                CategoryScorePill("🍲 Food Quality", avgFood)
                                CategoryScorePill("🛡️ Safety", avgSafety)
                                CategoryScorePill("🤝 Staff & Warden", avgStaff)
                                CategoryScorePill("🛏️ Rooms", avgRoom)
                                CategoryScorePill("📍 Location", avgLoc)
                                CategoryScorePill("💰 Value", avgValue)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Filter & Sort Controls
                    Text(
                        text = "Filter & Sort Reviews",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filterOptions = listOf(
                            "All" to "All (${reviews.size})",
                            "5 ★" to "5 ★ ($fiveStarCount)",
                            "4 ★+" to "4 ★+ (${fiveStarCount + fourStarCount})",
                            "Verified" to "Verified Residents",
                            "With Reply" to "Host Responses"
                        )
                        items(filterOptions) { (key, label) ->
                            val isSelected = reviewFilter == key
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { reviewFilter = key }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sort row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Sort by:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        val sortOptions = listOf(
                            "Recent" to "Most Recent",
                            "Highest" to "Highest Rated",
                            "Helpful" to "Most Helpful"
                        )
                        sortOptions.forEach { (key, label) ->
                            val isSelected = reviewSort == key
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { reviewSort = key }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // List View of User Submitted Reviews
                    if (filteredReviews.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No reviews found for this filter",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Try switching the filter or submit your own review.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        filteredReviews.forEach { review ->
                            ReviewCard(
                                review = review,
                                onHelpfulClick = {
                                    viewModel.voteHelpfulReview(property.id, review.id)
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }

            // 9. Report Concern / File Instant Complaint Banner
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFEF2F2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Experiencing an issue with this stay?",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = "File an Instant Action Complaint for geyser, electricity, hygiene or safety.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.openComplaintDialog(property) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Report", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

        }

        // Sticky Bottom Booking Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentPrice = activeCalculation?.totalPayable ?: (selectedRoomOption?.price ?: property.startingPrice)
                val formatted = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                    .format(currentPrice)
                    .replace(".00", "")

                Column {
                    Text(
                        text = if (activeCalculation != null) "${activeCalculation?.durationSummaryText} • Total Cost" else (selectedRoomOption?.name ?: "Standard Room"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatted,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (activeCalculation == null) {
                            Text(
                                text = when (property.durationType) {
                                    DurationType.HOURLY -> " /slot"
                                    DurationType.DAILY -> " /night"
                                    DurationType.WEEKLY -> " /week"
                                    DurationType.MONTHLY -> " /month"
                                    DurationType.YEARLY -> " /year"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = " total",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = StynoEmerald
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { sharePropertyDetails(context, property) },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        modifier = Modifier.testTag("detail_bottom_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Property",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Share",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.startBookingFlow(
                                property = property,
                                preSelectedRoom = selectedRoomOption,
                                checkInDate = activeCalculation?.formattedCheckIn,
                                checkOutDate = activeCalculation?.formattedCheckOut,
                                durationText = activeCalculation?.durationSummaryText,
                                calculatedTotal = activeCalculation?.totalPayable,
                                discountAmount = activeCalculation?.couponDiscount
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("book_now_button")
                    ) {
                        Text("Book Now", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }

    // Write Review Dialog with 8-Category Rating Breakdown
    if (showReviewDialog) {
        var reviewTitle by remember { mutableStateOf("") }
        var reviewText by remember { mutableStateOf("") }
        var reviewerName by remember { mutableStateOf(viewModel.userName.value.ifBlank { "Verified Resident" }) }
        var cleanlinessScore by remember { mutableStateOf(5.0f) }
        var foodScore by remember { mutableStateOf(4.8f) }
        var staffScore by remember { mutableStateOf(4.9f) }
        var safetyScore by remember { mutableStateOf(5.0f) }
        var roomQualityScore by remember { mutableStateOf(4.8f) }
        var locationScore by remember { mutableStateOf(4.7f) }
        var valueScore by remember { mutableStateOf(4.8f) }

        val overallAvg = (cleanlinessScore + foodScore + staffScore + safetyScore + roomQualityScore + locationScore + valueScore) / 7.0f

        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Write a Verified Review", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        Text(
                            text = "Rate each dimension of your stay at ${property.name}:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        RatingSliderItem(label = "🧹 Cleanliness & Hygiene", score = cleanlinessScore, onScoreChange = { cleanlinessScore = it })
                        RatingSliderItem(label = "🍲 Food Taste & Quality", score = foodScore, onScoreChange = { foodScore = it })
                        RatingSliderItem(label = "🤝 Staff & Warden Behavior", score = staffScore, onScoreChange = { staffScore = it })
                        RatingSliderItem(label = "🛡️ Safety & Biometric Security", score = safetyScore, onScoreChange = { safetyScore = it })
                        RatingSliderItem(label = "🛏️ Room & Bed Quality", score = roomQualityScore, onScoreChange = { roomQualityScore = it })
                        RatingSliderItem(label = "📍 Location & Transit Convenience", score = locationScore, onScoreChange = { locationScore = it })
                        RatingSliderItem(label = "💰 Value for Money & Pricing", score = valueScore, onScoreChange = { valueScore = it })

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Calculated Overall Rating:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF92400E))
                                Text("${String.format(Locale.US, "%.1f", overallAvg)} ★", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF92400E))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = reviewTitle,
                            onValueChange = { reviewTitle = it },
                            label = { Text("Review Headline") },
                            placeholder = { Text("e.g. Great food, supportive warden & clean rooms") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            label = { Text("Detailed Review") },
                            placeholder = { Text("Share details about food quality, Wi-Fi speed, gate security, hot water, and cleanliness...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newReview = Review(
                            id = "rev-${System.currentTimeMillis()}",
                            propertyId = property.id,
                            userName = reviewerName,
                            userRole = "Verified Resident (Styno App)",
                            rating = (Math.round(overallAvg * 10.0) / 10.0).toFloat(),
                            date = "Today",
                            title = reviewTitle.ifBlank { "Great verified stay experience" },
                            comment = reviewText.ifBlank { "Living here was comfortable. High-speed Wi-Fi, great meals, and 24/7 security." },
                            breakdown = com.example.data.model.RatingBreakdown(
                                cleanliness = cleanlinessScore,
                                foodQuality = foodScore,
                                staffBehavior = staffScore,
                                safety = safetyScore,
                                roomQuality = roomQualityScore,
                                location = locationScore,
                                valueForMoney = valueScore,
                                overall = overallAvg
                            ),
                            helpfulCount = 1,
                            verifiedStay = true
                        )
                        viewModel.addReview(newReview)
                        showReviewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RatingSliderItem(
    label: String,
    score: Float,
    onScoreChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("${String.format(Locale.US, "%.1f", score)} ★", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..5).forEach { star ->
                val filled = star <= score
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "$star Stars",
                    tint = if (filled) Color(0xFFD97706) else Color(0xFFD1D5DB),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onScoreChange(star.toFloat()) }
                )
            }
        }
    }
}


@Composable
private fun TimingItem(label: String, time: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = time, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun MealRow(title: String, dish: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• $title: ",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = dish,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ReviewCard(
    review: Review,
    onHelpfulClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("review_card_${review.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // User Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val initials = review.userName.split(" ")
                        .take(2)
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .joinToString("")
                        .ifBlank { "U" }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = initials,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = review.userName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (review.verifiedStay) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Resident",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Text(
                            text = "${review.userRole} • ${review.date}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Rating Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${String.format(Locale.US, "%.1f", review.rating)} ★",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Star Row
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                (1..5).forEach { star ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (star <= Math.round(review.rating)) Color(0xFFF59E0B) else Color(0xFFE5E7EB),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Review Headline
            Text(
                text = review.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Full Comment
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 8-Dimension Rating Breakdown Chips
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MiniRatingTag("🧹 Cleanliness", review.breakdown.cleanliness)
                    MiniRatingTag("🍲 Food Quality", review.breakdown.foodQuality)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MiniRatingTag("🤝 Staff & Warden", review.breakdown.staffBehavior)
                    MiniRatingTag("🛡️ Safety & Gate", review.breakdown.safety)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MiniRatingTag("🛏️ Room Quality", review.breakdown.roomQuality)
                    MiniRatingTag("📍 Location", review.breakdown.location)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MiniRatingTag("💰 Value for Money", review.breakdown.valueForMoney)
                    MiniRatingTag("⭐ Overall", review.breakdown.overall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions row: Helpful Button + Verified Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onHelpfulClick() }
                        .testTag("helpful_button_${review.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Helpful",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Helpful (${review.helpfulCount})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (review.verifiedStay) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Verified Stay",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF059669)
                        )
                    }
                }
            }

            // Official Host Response if available
            if (!review.ownerResponse.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEFF6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Official Host Response",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF1E40AF)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "• ${review.ownerResponseDate ?: "Host Reply"}",
                                fontSize = 10.sp,
                                color = Color(0xFF3B82F6)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = review.ownerResponse,
                            fontSize = 11.sp,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StarDistributionRow(
    label: String,
    count: Int,
    total: Int,
    barColor: Color
) {
    val fraction = if (total > 0) (count.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(22.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$count",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(18.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun CategoryScorePill(label: String, score: Float) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${String.format(Locale.US, "%.1f", score)}★",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD97706)
            )
        }
    }
}

@Composable
private fun MiniRatingTag(label: String, score: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "${String.format(Locale.US, "%.1f", score)}★", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
    }
}

/**
 * Fires the Android Native Sharing Intent (Intent.ACTION_SEND) to share complete
 * property details, location, room pricing, ratings, and booking link with others.
 */
fun sharePropertyDetails(context: android.content.Context, property: Property) {
    val priceFormatted = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        .format(property.startingPrice)
        .replace(".00", "")
    val shareText = buildString {
        appendLine("🏡 ${property.name}")
        appendLine("🏷️ ${property.propertyType.displayName.uppercase()} • ${property.genderSuitability.displayName}")
        appendLine("📍 ${property.address}, ${property.area}, ${property.city}")
        appendLine("📌 Near: ${property.nearbyLandmark}")
        appendLine("💰 Starting Price: $priceFormatted/${if (property.durationType == DurationType.DAILY) "night" else "month"}")
        appendLine("⭐ Rating: ${property.rating}★ (${property.reviewCount} verified reviews)")
        if (property.shortFacilities.isNotEmpty()) {
            appendLine("✨ Highlights: ${property.shortFacilities.joinToString(", ")}")
        }
        appendLine("🛡️ Verified: 100% Styno KYC & Security Verified")
        appendLine("📞 Warden / Host: ${property.ownerInfo.name} (${property.ownerInfo.phone})")
        appendLine()
        appendLine("Explore rooms, daily canteen food menu & book directly on STYNO:")
        append("https://styno.com/property/${property.id}")
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Check out ${property.name} on STYNO Accommodation")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share Property Details via"))
}



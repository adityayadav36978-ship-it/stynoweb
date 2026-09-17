package com.example.ui.screens.wizard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AmenityPricingType
import com.example.data.model.FoodPricingModel
import com.example.data.model.GenderSuitability
import com.example.data.model.PropertyType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step8CustomerPreview(
    state: PropertyWizardState,
    ownerName: String,
    ownerPhone: String,
    onEditStep: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val durationLabel = if (state.propertyType == PropertyType.HOTEL) "/night" else "/month"
    val primaryPhoto = state.photos.firstOrNull { it.isHero } ?: state.photos.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step Banner
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 8 of 10: Customer Live Preview",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "This is an exact replication of how prospective tenants and guests will experience your listing on Styno.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Preview Notice Chip
        Surface(
            color = Color(0xFF1565C0),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Text("Customer View Simulation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Surface(
                    color = Color.White.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "VERIFIED PREVIEW",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // 1. Hero Media Card
        Card(
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (primaryPhoto?.bitmap != null) {
                    Image(
                        bitmap = primaryPhoto.bitmap.asImageBitmap(),
                        contentDescription = state.propertyName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Apartment, contentDescription = null, modifier = Modifier.size(54.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(state.propertyType.displayName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Badges overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = Color(0xFF2E7D32),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text("STYNO VERIFIED", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${state.photos.size.coerceAtLeast(1)} Photos",
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Bottom gradient label overlay
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(6.dp)) {
                                Text(state.propertyType.displayName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Surface(color = Color(0xFFE65100), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    if (state.propertyType == PropertyType.HOSTEL) state.categoryConfig.hostelGender.name.replace("_", " ") else "OPEN TO ALL",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Starts ₹${state.startingPrice.toInt()} $durationLabel",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 2. Title, Rating & Address Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.propertyName.ifBlank { "Styno ${state.propertyType.displayName}" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onEditStep(2) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Info", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color(0xFF2E7D32),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text("4.8", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                    Text("• New Listing by Verified Host", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = "${state.address.ifBlank { "Sector 62" }}, ${state.area.ifBlank { "IT Hub" }}, ${state.city}${if (state.postalCode.isNotBlank()) " - ${state.postalCode}" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (state.landmark.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Landmark: ${state.landmark}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 3. GPS Location Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Exact GPS Location", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { onEditStep(2) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit GPS", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Lat: ${"%.4f".format(state.gpsLatitude)}° N, Long: ${"%.4f".format(state.gpsLongitude)}° E",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Pinned accurately on Styno In-App Map with automated transit radius",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4. Room Configurations & Pricing
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Room Options & Tariffs", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { onEditStep(4) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Rooms", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                state.rooms.forEach { room ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(room.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${room.availableRooms} Available", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                            Text("Sharing: ${room.sharingType} • ${if (room.hasAttachedBath) "Attached Washroom" else "Shared Washroom"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (room.isAcAvailable) {
                                    Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp)) {
                                        Text(
                                            text = "AC: ₹${room.acPrice.toInt()}$durationLabel",
                                            color = Color(0xFF1565C0),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                if (room.isNonAcAvailable) {
                                    Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(8.dp)) {
                                        Text(
                                            text = "Non-AC: ₹${room.nonAcPrice.toInt()}$durationLabel",
                                            color = Color(0xFFE65100),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Amenities: Free vs Paid
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Amenities & Services", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { onEditStep(5) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Amenities", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                // Free Amenities Chips
                val freeList = state.amenities.filter { it.status == AmenityPricingType.FREE }
                if (freeList.isNotEmpty()) {
                    Text("Free / Included in Rent:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        freeList.forEach { freeItem ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(freeItem.name, fontSize = 11.sp) },
                                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp)) }
                            )
                        }
                    }
                }

                // Paid Amenities Chips
                val paidList = state.amenities.filter { it.status == AmenityPricingType.PAID }
                if (paidList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Paid Add-On Services:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFFFF8F00))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        paidList.forEach { paidItem ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text("${paidItem.name} (+₹${paidItem.priceAmount.toInt()}${paidItem.priceUnit})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                icon = { Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFFFF8F00), modifier = Modifier.size(14.dp)) }
                            )
                        }
                    }
                }
            }
        }

        // 6. Food Services Preview
        if (state.foodConfig.hasFoodService) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color(state.foodConfig.dietaryType.badgeColorHex))
                            Text("Food & Mess Service", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { onEditStep(6) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Food", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Surface(
                        color = Color(state.foodConfig.dietaryType.badgeColorHex).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = state.foodConfig.dietaryType.displayName,
                            color = Color(state.foodConfig.dietaryType.badgeColorHex),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "Pricing: ${state.foodConfig.pricingModel.displayName}${if (state.foodConfig.pricingModel == FoodPricingModel.MONTHLY_MESS) " (₹${state.foodConfig.monthlyCharge.toInt()}/mo)" else ""}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    // Meals list
                    state.foodConfig.meals.filter { it.isEnabled }.forEach { meal ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• ${meal.name}", fontSize = 12.sp)
                            Text(meal.timings, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // 7. Host & Property Manager Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ownerName.ifBlank { "Verified Styno Host" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Contact: ${state.ownerPhone.ifBlank { ownerPhone }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "⚡ Responds within 15 minutes • Top Rated Host",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 8. Sticky Customer Bottom Action Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Starts From", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "₹${state.startingPrice.toInt()} $durationLabel",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = { /* Customer booking action */ },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("btn_customer_book_preview")
                ) {
                    Text("Customer Reserve Button")
                }
            }
        }
    }
}

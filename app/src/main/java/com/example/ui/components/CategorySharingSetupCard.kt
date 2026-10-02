package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GenderSuitability
import com.example.data.model.PropertyType
import com.example.ui.screens.wizard.PropertyWizardState
import com.example.ui.theme.StynoEmerald

/**
 * Category-based Sharing & Audience Setup Component for the STYNO Owner Section.
 * Implements strict rules:
 * 1. HOTEL: Never allows sharing (strictly private room/unit).
 * 2. FLAT: Yes/No sharing toggle; if yes, collect capacity & available spaces.
 * 3. PG: Yes/No sharing with single, double, triple, 4-sharing, 5+ options.
 * 4. HOSTEL: Yes/No sharing with multiple occupancy levels.
 * 5. ROOM: Private Room vs Sharing Room selector with capacity & beds.
 * 6. QUICK STAY: Optional sharing for transit dorm/pod slots.
 * 7. Audience Safety: Boys share only with Boys, Girls only with Girls, Men only with Men, Women only with Women.
 * 8. Couples and Family: Always strictly private.
 */
@Composable
fun CategorySharingSetupCard(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit,
    modifier: Modifier = Modifier
) {
    val propertyType = state.propertyType
    val isHotel = propertyType == PropertyType.HOTEL
    val isFamilyOrCoupleAudience = state.targetAudience == GenderSuitability.FAMILY ||
            state.targetAudience == GenderSuitability.COUPLES

    // Enforce safety: Hotel, Couples and Family are strictly private
    LaunchedEffect(isHotel, isFamilyOrCoupleAudience) {
        if ((isHotel || isFamilyOrCoupleAudience) && state.isSharingAvailable) {
            onUpdateState(state.copy(isSharingAvailable = false, sharingCapacity = 1, availableSpaces = 1))
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_sharing_setup_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isHotel) MaterialTheme.colorScheme.primaryContainer else if (state.isSharingAvailable) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isHotel) Icons.Default.Hotel else if (state.isSharingAvailable) Icons.Default.Groups else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isHotel) MaterialTheme.colorScheme.primary else if (state.isSharingAvailable) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sharing & Audience Setup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Configured for ${propertyType.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isHotel || !state.isSharingAvailable) Color(0xFFF1F5F9) else Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = if (isHotel) "Private Only" else if (state.isSharingAvailable) "${state.sharingCapacity}-Sharing" else "Private Unit",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHotel || !state.isSharingAvailable) Color(0xFF475569) else Color(0xFF166534)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // ==========================================
            // 1. SELECT TARGET AUDIENCE / GENDER
            // ==========================================
            Text(
                text = "1. Eligible Audience / Occupant Category *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            val audienceOptions = when (propertyType) {
                PropertyType.HOTEL -> listOf(
                    GenderSuitability.EVERYONE to "Everyone / Open to All",
                    GenderSuitability.COUPLES to "Couples (Valid ID)",
                    GenderSuitability.FAMILY to "Family",
                    GenderSuitability.BUSINESS_TRAVELERS to "Business Travelers",
                    GenderSuitability.TOURISTS to "Tourists"
                )
                PropertyType.HOSTEL -> listOf(
                    GenderSuitability.BOYS_ONLY to "Boys Only",
                    GenderSuitability.GIRLS_ONLY to "Girls Only",
                    GenderSuitability.CO_ED to "Co-Ed (Separate Wings)"
                )
                PropertyType.PG -> listOf(
                    GenderSuitability.BOYS_ONLY to "Boys Only",
                    GenderSuitability.GIRLS_ONLY to "Girls Only",
                    GenderSuitability.MEN_ONLY to "Men Only",
                    GenderSuitability.WOMEN_ONLY to "Women Only",
                    GenderSuitability.STUDENTS to "Students Only",
                    GenderSuitability.WORKING_PROFESSIONALS to "Working Professionals",
                    GenderSuitability.CO_ED to "Co-Ed"
                )
                PropertyType.FLAT -> listOf(
                    GenderSuitability.FAMILY to "Family",
                    GenderSuitability.COUPLES to "Couples",
                    GenderSuitability.BOYS_ONLY to "Boys Only",
                    GenderSuitability.GIRLS_ONLY to "Girls Only",
                    GenderSuitability.WORKING_PROFESSIONALS to "Working Execs",
                    GenderSuitability.STUDENTS to "Students"
                )
                PropertyType.ROOM -> listOf(
                    GenderSuitability.BOYS_ONLY to "Boys Only",
                    GenderSuitability.GIRLS_ONLY to "Girls Only",
                    GenderSuitability.MEN_ONLY to "Men Only",
                    GenderSuitability.WOMEN_ONLY to "Women Only",
                    GenderSuitability.FAMILY to "Family",
                    GenderSuitability.COUPLES to "Couples"
                )
                PropertyType.QUICK_STAY -> listOf(
                    GenderSuitability.ALL to "All Travelers",
                    GenderSuitability.BOYS_ONLY to "Boys Only",
                    GenderSuitability.GIRLS_ONLY to "Girls Only",
                    GenderSuitability.COUPLES to "Couples",
                    GenderSuitability.FAMILY to "Family"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                audienceOptions.forEach { (suitability, label) ->
                    val isSelected = state.targetAudience == suitability
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val willBeCoupleOrFamily = suitability == GenderSuitability.FAMILY || suitability == GenderSuitability.COUPLES
                            onUpdateState(
                                state.copy(
                                    targetAudience = suitability,
                                    isSharingAvailable = if (willBeCoupleOrFamily) false else state.isSharingAvailable,
                                    sharingCapacity = if (willBeCoupleOrFamily) 1 else state.sharingCapacity,
                                    availableSpaces = if (willBeCoupleOrFamily) 1 else state.availableSpaces
                                )
                            )
                        },
                        label = { Text(label, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null
                    )
                }
            }

            // Safety Warning if Couple or Family
            if (isFamilyOrCoupleAudience) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF3E8FF),
                    border = BorderStroke(1.dp, Color(0xFFD8B4FE))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(20.dp))
                        Text(
                            text = "Family & Couple Safety Rule: Bookings for couples and families are ALWAYS 100% private. Shared occupancy is strictly disabled.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF581C87),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ==========================================
            // 2. SHARING AVAILABILITY BY CATEGORY
            // ==========================================
            if (isHotel) {
                // Hotel: Explicitly NO sharing
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Hotel rooms are treated as private room/suite bookings. Hotel listings cannot be configured for shared room occupancy.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else if (!isFamilyOrCoupleAudience) {
                Text(
                    text = when (propertyType) {
                        PropertyType.ROOM -> "2. Room Occupancy Type *"
                        PropertyType.FLAT -> "2. Is Sharing Available for Flat? *"
                        PropertyType.QUICK_STAY -> "2. Shared Transit Pods / Dorm Slots *"
                        else -> "2. Is Sharing Available? *"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (propertyType == PropertyType.ROOM) {
                    // Room: "Private Room" vs "Sharing Room"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            false to "🔒 Private Room",
                            true to "👥 Sharing Room"
                        ).forEach { (isShared, label) ->
                            val selected = state.isSharingAvailable == isShared
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onUpdateState(
                                            state.copy(
                                                isSharingAvailable = isShared,
                                                sharingCapacity = if (isShared) 2 else 1,
                                                availableSpaces = if (isShared) 2 else 1
                                            )
                                        )
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.5.dp, if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = selected,
                                        onClick = null,
                                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                    )
                                    Text(
                                        text = label,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Flat, PG, Hostel, Quick Stay: Yes / No Sharing switch/chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            false to "No (Private Unit)",
                            true to "Yes (Sharing Available)"
                        ).forEach { (isShared, label) ->
                            val selected = state.isSharingAvailable == isShared
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onUpdateState(
                                            state.copy(
                                                isSharingAvailable = isShared,
                                                sharingCapacity = if (isShared) (if (state.sharingCapacity >= 2) state.sharingCapacity else 2) else 1,
                                                availableSpaces = if (isShared) (if (state.availableSpaces >= 1) state.availableSpaces else 2) else 1
                                            )
                                        )
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) (if (isShared) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.primaryContainer) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.5.dp, if (selected) (if (isShared) StynoEmerald else MaterialTheme.colorScheme.primary) else Color.Transparent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = selected,
                                        onClick = null,
                                        colors = RadioButtonDefaults.colors(selectedColor = if (isShared) StynoEmerald else MaterialTheme.colorScheme.primary)
                                    )
                                    Text(
                                        text = label,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 3. SHARING CAPACITY & AVAILABLE SPACES
                // ==========================================
                AnimatedVisibility(visible = state.isSharingAvailable) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "3. Sharing Capacity (Total Beds/Occupants in Unit) *",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        val capacityChips = when (propertyType) {
                            PropertyType.PG -> listOf(2 to "2-Sharing", 3 to "3-Sharing", 4 to "4-Sharing", 5 to "5-Sharing", 6 to "6+ Sharing")
                            PropertyType.HOSTEL -> listOf(2 to "2-Sharing", 3 to "3-Sharing", 4 to "4-Sharing", 6 to "6-Dorm", 8 to "8-Dormitory")
                            PropertyType.FLAT -> listOf(2 to "2 Persons", 3 to "3 Persons", 4 to "4 Persons", 5 to "5+ Persons")
                            PropertyType.ROOM -> listOf(2 to "2-Sharing Room", 3 to "3-Sharing Room", 4 to "4-Sharing Room")
                            else -> listOf(2 to "2 Slots", 4 to "4 Slots", 6 to "6 Slots", 8 to "8 Slots")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            capacityChips.forEach { (cap, label) ->
                                val isSelected = state.sharingCapacity == cap
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        val newSpaces = state.availableSpaces.coerceAtMost(cap)
                                        onUpdateState(state.copy(sharingCapacity = cap, availableSpaces = newSpaces))
                                    },
                                    label = { Text(label, fontSize = 12.sp) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null
                                )
                            }
                        }

                        // Stepper / Field for Custom Capacity & Currently Available Beds
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Capacity Input
                            OutlinedTextField(
                                value = state.sharingCapacity.toString(),
                                onValueChange = { input ->
                                    val cap = input.filter { it.isDigit() }.toIntOrNull() ?: 2
                                    val safeCap = cap.coerceIn(2, 20)
                                    val safeSpaces = state.availableSpaces.coerceAtMost(safeCap)
                                    onUpdateState(state.copy(sharingCapacity = safeCap, availableSpaces = safeSpaces))
                                },
                                label = { Text("Total Capacity") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = { Icon(Icons.Default.Group, contentDescription = null) },
                                modifier = Modifier.weight(1f)
                            )

                            // Available Spaces Input
                            OutlinedTextField(
                                value = state.availableSpaces.toString(),
                                onValueChange = { input ->
                                    val spaces = input.filter { it.isDigit() }.toIntOrNull() ?: 1
                                    val safeSpaces = spaces.coerceIn(0, state.sharingCapacity)
                                    onUpdateState(state.copy(availableSpaces = safeSpaces))
                                },
                                label = { Text("Available Beds/Spaces") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = { Icon(Icons.Default.Bed, contentDescription = null) },
                                isError = state.availableSpaces > state.sharingCapacity,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Validation Warnings
                        if (state.availableSpaces > state.sharingCapacity) {
                            Text(
                                text = "⚠️ Available spaces (${state.availableSpaces}) cannot exceed total sharing capacity (${state.sharingCapacity}).",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (state.availableSpaces == 0) {
                            Text(
                                text = "ℹ️ Available spaces set to 0. Listing will appear as 'Sold Out' on search.",
                                color = Color(0xFFD97706),
                                fontSize = 12.sp
                            )
                        }

                        // Gender Safety Explanation Banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(18.dp))
                                Text(
                                    text = "STYNO Safe Sharing: This ${state.sharingCapacity}-sharing listing will only be shown to and shared with ${state.targetAudience.displayName}. Incompatible audiences are strictly separated.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1E40AF),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

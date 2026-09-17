package com.example.ui.screens.wizard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PropertyType

data class PropertyTypeCardInfo(
    val type: PropertyType,
    val icon: ImageVector,
    val headline: String,
    val badge: String,
    val description: String,
    val dedicatedFormHighlights: List<String>
)

val propertyTypeCardList = listOf(
    PropertyTypeCardInfo(
        type = PropertyType.HOSTEL,
        icon = Icons.Default.Apartment,
        headline = "Hostel / Student Residence",
        badge = "Students & Aspirants",
        description = "Ideal for students and competitive exam aspirants with shared or single rooms, mess meals, and warden supervision.",
        dedicatedFormHighlights = listOf("Boys / Girls / Co-ed selection", "Resident warden & curfew rules", "Study hall & mess plans")
    ),
    PropertyTypeCardInfo(
        type = PropertyType.PG,
        icon = Icons.Default.HomeWork,
        headline = "PG (Paying Guest)",
        badge = "Students & Working Execs",
        description = "Homelike managed stays with individual or twin sharing rooms, furnished setups, and flexible notice periods.",
        dedicatedFormHighlights = listOf("Target audience filters", "Notice & lock-in period setup", "Sub-meter electricity billing")
    ),
    PropertyTypeCardInfo(
        type = PropertyType.FLAT,
        icon = Icons.Default.Domain,
        headline = "Flat / Apartment",
        badge = "1-4 BHK • Families & Groups",
        description = "Independent gated apartments with complete privacy, kitchen, hall, and private society amenities.",
        dedicatedFormHighlights = listOf("1-4 BHK & furnishing options", "Society maintenance charges", "Reserved parking configuration")
    ),
    PropertyTypeCardInfo(
        type = PropertyType.HOTEL,
        icon = Icons.Default.Hotel,
        headline = "Hotel / Boutique Stay",
        badge = "Per-Night • Travelers & Tourists",
        description = "Daily booking accommodation with 24/7 front desk, room service, daily linen change, and couple-friendly tags.",
        dedicatedFormHighlights = listOf("Per-day/night tariff mode", "Couple-friendly & local ID acceptance", "24/7 reception & housekeeping")
    ),
    PropertyTypeCardInfo(
        type = PropertyType.ROOM,
        icon = Icons.Default.MeetingRoom,
        headline = "Independent Single Room",
        badge = "Single Room • Solo Stay",
        description = "Private independent room with dedicated or attached washroom, balcony, and optional kitchenette.",
        dedicatedFormHighlights = listOf("Private entrance & layout", "Attached washroom options", "Kitchenette access rules")
    ),
    PropertyTypeCardInfo(
        type = PropertyType.QUICK_STAY,
        icon = Icons.Default.Bolt,
        headline = "Quick Stay",
        badge = "1–24 Hours • All Travelers",
        description = "Short-duration stays for flexible hours, transit, work, meetings, or quick rest.",
        dedicatedFormHighlights = listOf("1, 2, 3, 4, 5, 10, 12, 24 Hours hourly pricing", "Instant check-in ready with digital pass", "Shower, fresh-up kit & luggage locker")
    )
)

@Composable
fun Step1PropertyType(
    selectedType: PropertyType,
    onTypeSelected: (PropertyType) -> Unit,
    modifier: Modifier = Modifier
) {
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
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 1 of 10: Select Property Type",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choosing your property type loads a dedicated, tailored listing form with relevant pricing durations and compliance fields.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text(
            text = "What kind of stay are you listing?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Cards list
        propertyTypeCardList.forEach { cardInfo ->
            val isSelected = selectedType == cardInfo.type
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            val containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTypeSelected(cardInfo.type) }
                    .testTag("property_type_card_${cardInfo.type.name.lowercase()}"),
                shape = RoundedCornerShape(16.dp),
                color = containerColor,
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
                tonalElevation = if (isSelected) 3.dp else 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = cardInfo.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = cardInfo.headline,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = cardInfo.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Radio check indicator
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .border(
                                    2.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = cardInfo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Tailored form highlights
                    AnimatedVisibility(visible = isSelected) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Dedicated Form Features for ${cardInfo.type.displayName}:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                cardInfo.dedicatedFormHighlights.forEach { highlight ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = highlight,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

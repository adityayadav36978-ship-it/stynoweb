package com.example.ui.screens.wizard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AmenityItem
import com.example.data.model.AmenityPricingType

fun amenityIconFor(id: String): ImageVector {
    return when (id) {
        "wifi" -> Icons.Default.Wifi
        "ac" -> Icons.Default.AcUnit
        "laundry" -> Icons.Default.LocalLaundryService
        "water" -> Icons.Default.WaterDrop
        "power" -> Icons.Default.Bolt
        "security" -> Icons.Default.Security
        "cleaning" -> Icons.Default.CleaningServices
        "parking" -> Icons.Default.LocalParking
        "gym" -> Icons.Default.FitnessCenter
        "study" -> Icons.Default.MenuBook
        "geyser" -> Icons.Default.HotTub
        "fridge" -> Icons.Default.Kitchen
        "elevator" -> Icons.Default.Elevator
        "tv" -> Icons.Default.Tv
        "caretaker" -> Icons.Default.SupportAgent
        else -> Icons.Default.CheckCircle
    }
}

@Composable
fun Step5Amenities(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit,
    modifier: Modifier = Modifier
) {
    val freeCount = state.amenities.count { it.status == AmenityPricingType.FREE }
    val paidCount = state.amenities.count { it.status == AmenityPricingType.PAID }
    val notAvailCount = state.amenities.count { it.status == AmenityPricingType.NOT_AVAILABLE }

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
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 5 of 10: Amenities & Facilities",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Categorize each facility as Free (Included in rent), Paid (Extra charge with price field), or Not Available.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Summary Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF2E7D32), CircleShape))
                    Text(text = "$freeCount Free", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFFF8F00), CircleShape))
                    Text(text = "$paidCount Paid Add-on", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFF8F00))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(Color.Gray, CircleShape))
                    Text(text = "$notAvailCount N/A", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Gray)
                }
            }
        }

        // Amenities List
        state.amenities.forEachIndexed { index, item ->
            AmenityPricingRowCard(
                item = item,
                onUpdate = { updatedItem ->
                    val updated = state.amenities.toMutableList()
                    updated[index] = updatedItem
                    onUpdateState(state.copy(amenities = updated))
                }
            )
        }
    }
}

@Composable
fun AmenityPricingRowCard(
    item: AmenityItem,
    onUpdate: (AmenityItem) -> Unit
) {
    val containerBorder = when (item.status) {
        AmenityPricingType.FREE -> BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f))
        AmenityPricingType.PAID -> BorderStroke(1.dp, Color(0xFFFF8F00).copy(alpha = 0.5f))
        AmenityPricingType.NOT_AVAILABLE -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    }

    val iconColor = when (item.status) {
        AmenityPricingType.FREE -> Color(0xFF2E7D32)
        AmenityPricingType.PAID -> Color(0xFFFF8F00)
        AmenityPricingType.NOT_AVAILABLE -> Color.Gray
    }

    OutlinedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = when (item.status) {
                AmenityPricingType.FREE -> Color(0xFF2E7D32).copy(alpha = 0.04f)
                AmenityPricingType.PAID -> Color(0xFFFF8F00).copy(alpha = 0.04f)
                AmenityPricingType.NOT_AVAILABLE -> MaterialTheme.colorScheme.surface
            }
        ),
        border = containerBorder,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = amenityIconFor(item.id),
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (item.status == AmenityPricingType.NOT_AVAILABLE) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                    if (item.status == AmenityPricingType.PAID && item.priceAmount > 0) {
                        Text(
                            text = "Extra Charge: ₹${item.priceAmount.toInt()}${item.priceUnit}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFF8F00),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3-Way Segmented Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Free Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (item.status == AmenityPricingType.FREE) Color(0xFF2E7D32) else Color.Transparent)
                        .clickable { onUpdate(item.copy(status = AmenityPricingType.FREE)) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Free",
                        color = if (item.status == AmenityPricingType.FREE) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (item.status == AmenityPricingType.FREE) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }

                // Paid Button
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (item.status == AmenityPricingType.PAID) Color(0xFFFF8F00) else Color.Transparent)
                        .clickable {
                            val defaultPrice = if (item.priceAmount <= 0) 500.0 else item.priceAmount
                            onUpdate(item.copy(status = AmenityPricingType.PAID, priceAmount = defaultPrice))
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Paid / Extra",
                        color = if (item.status == AmenityPricingType.PAID) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (item.status == AmenityPricingType.PAID) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }

                // Not Available Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (item.status == AmenityPricingType.NOT_AVAILABLE) MaterialTheme.colorScheme.outline else Color.Transparent)
                        .clickable { onUpdate(item.copy(status = AmenityPricingType.NOT_AVAILABLE)) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Not Available",
                        color = if (item.status == AmenityPricingType.NOT_AVAILABLE) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (item.status == AmenityPricingType.NOT_AVAILABLE) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }

            // Inline Price Fields if Paid
            AnimatedVisibility(visible = item.status == AmenityPricingType.PAID) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (item.priceAmount > 0) item.priceAmount.toInt().toString() else "",
                        onValueChange = { onUpdate(item.copy(priceAmount = it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Price (₹)") },
                        placeholder = { Text("500") },
                        leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    // Unit selector
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("/month", "/day", "/use").forEach { unit ->
                            FilterChip(
                                selected = item.priceUnit == unit,
                                onClick = { onUpdate(item.copy(priceUnit = unit)) },
                                label = { Text(unit, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }
    }
}

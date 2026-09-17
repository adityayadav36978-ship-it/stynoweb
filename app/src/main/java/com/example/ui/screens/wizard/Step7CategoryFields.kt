package com.example.ui.screens.wizard

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
import androidx.compose.material.icons.outlined.*
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

@Composable
fun Step7CategoryFields(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit,
    modifier: Modifier = Modifier
) {
    val cat = state.categoryConfig

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
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 7 of 10: Category-Specific Fields (${state.propertyType.displayName})",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dedicated fields tailored specifically to ${state.propertyType.displayName} operations and compliance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        when (state.propertyType) {
            PropertyType.HOSTEL -> HostelSpecificForm(state, onUpdateState)
            PropertyType.PG -> PgSpecificForm(state, onUpdateState)
            PropertyType.FLAT -> FlatSpecificForm(state, onUpdateState)
            PropertyType.HOTEL -> HotelSpecificForm(state, onUpdateState)
            PropertyType.ROOM -> RoomSpecificForm(state, onUpdateState)
            PropertyType.QUICK_STAY -> QuickStaySpecificForm(state, onUpdateState)
        }
    }
}

@Composable
fun HostelSpecificForm(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit
) {
    val cat = state.categoryConfig

    OutlinedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Hostel Gender Classification *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    GenderSuitability.BOYS_ONLY to "Boys Hostel",
                    GenderSuitability.GIRLS_ONLY to "Girls Hostel",
                    GenderSuitability.CO_ED to "Co-Ed Hostel"
                ).forEach { (gender, label) ->
                    val isSelected = cat.hostelGender == gender
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdateState(state.copy(categoryConfig = cat.copy(hostelGender = gender))) },
                        label = { Text(label, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Resident Warden
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Resident Warden on Premises", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Supervision & emergency contact for residents", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = cat.hasResidentWarden,
                    onCheckedChange = { onUpdateState(state.copy(categoryConfig = cat.copy(hasResidentWarden = it))) }
                )
            }

            if (cat.hasResidentWarden) {
                OutlinedTextField(
                    value = cat.wardenContact,
                    onValueChange = { onUpdateState(state.copy(categoryConfig = cat.copy(wardenContact = it))) },
                    label = { Text("Warden / Caretaker Contact Number") },
                    placeholder = { Text("+91 98765 43210") },
                    leadingIcon = { Icon(Icons.Default.SupportAgent, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Biometric Attendance & Study Hall
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Biometric / Digital Attendance Gate", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Switch(
                    checked = cat.hasBiometricAttendance,
                    onCheckedChange = { onUpdateState(state.copy(categoryConfig = cat.copy(hasBiometricAttendance = it))) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("24x7 Silent Study Hall / Library", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Switch(
                    checked = cat.hasSilentStudyHall,
                    onCheckedChange = { onUpdateState(state.copy(categoryConfig = cat.copy(hasSilentStudyHall = it))) }
                )
            }

            OutlinedTextField(
                value = cat.visitorPolicy,
                onValueChange = { onUpdateState(state.copy(categoryConfig = cat.copy(visitorPolicy = it))) },
                label = { Text("Visitor & Guest Policy") },
                placeholder = { Text("e.g. Parents allowed in reception lounge up to 7:30 PM. No outside guests in rooms.") },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PgSpecificForm(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit
) {
    val cat = state.categoryConfig

    OutlinedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Target PG Occupants *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Working Professionals Only",
                    "Students Only",
                    "Open to All (Students & Execs)",
                    "Women / Girls Only",
                    "Men / Boys Only"
                ).forEach { target ->
                    FilterChip(
                        selected = cat.pgTargetGroup == target,
                        onClick = { onUpdateState(state.copy(categoryConfig = cat.copy(pgTargetGroup = target))) },
                        label = { Text(target, fontSize = 12.sp) }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Notice Period & Lock-in
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = "${cat.noticePeriodDays} Days",
                    onValueChange = {
                        val days = it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 30
                        onUpdateState(state.copy(categoryConfig = cat.copy(noticePeriodDays = days)))
                    },
                    label = { Text("Notice Period") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = "${cat.lockInPeriodMonths} Month(s)",
                    onValueChange = {
                        val m = it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 1
                        onUpdateState(state.copy(categoryConfig = cat.copy(lockInPeriodMonths = m)))
                    },
                    label = { Text("Lock-in Period") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = cat.electricityBilling,
                onValueChange = { onUpdateState(state.copy(categoryConfig = cat.copy(electricityBilling = it))) },
                label = { Text("Electricity Billing Scheme") },
                placeholder = { Text("e.g. As per sub-meter consumption (₹9/unit) or Included") },
                leadingIcon = { Icon(Icons.Default.ElectricBolt, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Overnight Guests Allowed (with prior notice)", fontSize = 14.sp)
                Switch(
                    checked = cat.guestsAllowedAtNight,
                    onCheckedChange = { onUpdateState(state.copy(categoryConfig = cat.copy(guestsAllowedAtNight = it))) }
                )
            }
        }
    }
}

@Composable
fun FlatSpecificForm(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit
) {
    val cat = state.categoryConfig

    OutlinedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "BHK Configuration *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("1 RK", "1 BHK", "2 BHK", "3 BHK", "4 BHK", "Penthouse / Duplex").forEach { bhk ->
                    FilterChip(
                        selected = cat.bhkConfig == bhk,
                        onClick = { onUpdateState(state.copy(categoryConfig = cat.copy(bhkConfig = bhk))) },
                        label = { Text(bhk, fontSize = 12.sp) }
                    )
                }
            }

            Text(
                text = "Furnishing Status *",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Fully Furnished", "Semi-Furnished", "Unfurnished").forEach { furn ->
                    FilterChip(
                        selected = cat.furnishingStatus == furn,
                        onClick = { onUpdateState(state.copy(categoryConfig = cat.copy(furnishingStatus = furn))) },
                        label = { Text(furn, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            OutlinedTextField(
                value = cat.societyName,
                onValueChange = { onUpdateState(state.copy(categoryConfig = cat.copy(societyName = it))) },
                label = { Text("Gated Society / Apartment Complex Name") },
                placeholder = { Text("e.g. Apex Athena, Sector 75") },
                leadingIcon = { Icon(Icons.Default.Apartment, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = if (cat.monthlyMaintenance > 0) cat.monthlyMaintenance.toInt().toString() else "",
                    onValueChange = { onUpdateState(state.copy(categoryConfig = cat.copy(monthlyMaintenance = it.toDoubleOrNull() ?: 0.0))) },
                    label = { Text("Society Maintenance (₹/mo)") },
                    placeholder = { Text("1500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = "${cat.balconyCount} Balconies",
                    onValueChange = {
                        val count = it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 1
                        onUpdateState(state.copy(categoryConfig = cat.copy(balconyCount = count)))
                    },
                    label = { Text("Balconies") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = cat.reservedParkingType,
                onValueChange = { onUpdateState(state.copy(categoryConfig = cat.copy(reservedParkingType = it))) },
                label = { Text("Reserved Parking Allocation") },
                placeholder = { Text("e.g. 1 Covered Car + 1 Bike Parking") },
                leadingIcon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun HotelSpecificForm(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit
) {
    val cat = state.categoryConfig

    OutlinedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Hotel Category & Star Rating",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Budget Hotel", "3-Star Boutique", "4-Star Executive", "Heritage Resort").forEach { star ->
                    FilterChip(
                        selected = cat.starCategory == star,
                        onClick = { onUpdateState(state.copy(categoryConfig = cat.copy(starCategory = star))) },
                        label = { Text(star, fontSize = 12.sp) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Couple Friendly (Local ID Accepted)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Unmarried couples allowed with valid government ID", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = cat.isCoupleFriendly,
                    onCheckedChange = { onUpdateState(state.copy(categoryConfig = cat.copy(isCoupleFriendly = it))) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("24/7 Reception & Front Desk", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Switch(
                    checked = cat.has24x7FrontDesk,
                    onCheckedChange = { onUpdateState(state.copy(categoryConfig = cat.copy(has24x7FrontDesk = it))) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("In-room Dining & Room Service", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Switch(
                    checked = cat.hasRoomService,
                    onCheckedChange = { onUpdateState(state.copy(categoryConfig = cat.copy(hasRoomService = it))) }
                )
            }
        }
    }
}

@Composable
fun RoomSpecificForm(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit
) {
    val cat = state.categoryConfig

    OutlinedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Independent Room Layout",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = cat.roomLayoutType,
                onValueChange = { onUpdateState(state.copy(categoryConfig = cat.copy(roomLayoutType = it))) },
                label = { Text("Room Privacy & Layout") },
                placeholder = { Text("e.g. Independent Room with Private Balcony & Rooftop Access") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cat.washroomAccessType,
                onValueChange = { onUpdateState(state.copy(categoryConfig = cat.copy(washroomAccessType = it))) },
                label = { Text("Washroom Setup") },
                placeholder = { Text("e.g. Attached Private Western Washroom") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cat.kitchenFacilityType,
                onValueChange = { onUpdateState(state.copy(categoryConfig = cat.copy(kitchenFacilityType = it))) },
                label = { Text("Kitchen Access") },
                placeholder = { Text("e.g. Private Modular Kitchenette with Gas Stove") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickStaySpecificForm(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit
) {
    val cat = state.categoryConfig

    OutlinedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = "Quick Stay Hourly & Slot Pricing *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Set exact slot durations and pricing for transit travelers and daytime rests.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 1-Hour Slot Price
            OutlinedTextField(
                value = if (cat.quickStay1HourPrice > 0) cat.quickStay1HourPrice.toInt().toString() else "",
                onValueChange = {
                    val price = it.toDoubleOrNull() ?: 0.0
                    onUpdateState(state.copy(categoryConfig = cat.copy(quickStay1HourPrice = price, quickStayHourlyPrice = price)))
                },
                label = { Text("1 Hour Slot Price (₹) *") },
                placeholder = { Text("199") },
                leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("quick_stay_1h_price_input")
            )

            // 2-Hour Slot Price
            OutlinedTextField(
                value = if (cat.quickStay2HoursPrice > 0) cat.quickStay2HoursPrice.toInt().toString() else "",
                onValueChange = {
                    val price = it.toDoubleOrNull() ?: 0.0
                    onUpdateState(state.copy(categoryConfig = cat.copy(quickStay2HoursPrice = price)))
                },
                label = { Text("2 Hours Slot Price (₹) *") },
                placeholder = { Text("299") },
                leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("quick_stay_2h_price_input")
            )

            // 3-Hour Slot Price
            OutlinedTextField(
                value = if (cat.quickStay3HoursPrice > 0) cat.quickStay3HoursPrice.toInt().toString() else "",
                onValueChange = {
                    val price = it.toDoubleOrNull() ?: 0.0
                    onUpdateState(state.copy(categoryConfig = cat.copy(quickStay3HoursPrice = price)))
                },
                label = { Text("3 Hours Slot Price (₹) *") },
                placeholder = { Text("399") },
                leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("quick_stay_3h_price_input")
            )

            // 4-Hour Slot Price
            OutlinedTextField(
                value = if (cat.quickStay4HoursPrice > 0) cat.quickStay4HoursPrice.toInt().toString() else "",
                onValueChange = {
                    val price = it.toDoubleOrNull() ?: 0.0
                    onUpdateState(state.copy(categoryConfig = cat.copy(quickStay4HoursPrice = price)))
                },
                label = { Text("4 Hours Slot Price (₹) *") },
                placeholder = { Text("499") },
                leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("quick_stay_4h_price_input")
            )

            // 5-Hour Slot Price
            OutlinedTextField(
                value = if (cat.quickStay5HoursPrice > 0) cat.quickStay5HoursPrice.toInt().toString() else "",
                onValueChange = {
                    val price = it.toDoubleOrNull() ?: 0.0
                    onUpdateState(state.copy(categoryConfig = cat.copy(quickStay5HoursPrice = price)))
                },
                label = { Text("5 Hours Slot Price (₹) *") },
                placeholder = { Text("599") },
                leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("quick_stay_5h_price_input")
            )

            // 10-Hour Slot Price
            OutlinedTextField(
                value = if (cat.quickStay10HoursPrice > 0) cat.quickStay10HoursPrice.toInt().toString() else "",
                onValueChange = {
                    val price = it.toDoubleOrNull() ?: 0.0
                    onUpdateState(state.copy(categoryConfig = cat.copy(quickStay10HoursPrice = price)))
                },
                label = { Text("10 Hours Slot Price (₹) *") },
                placeholder = { Text("899") },
                leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("quick_stay_10h_price_input")
            )

            // 12-Hour Slot Price
            OutlinedTextField(
                value = if (cat.quickStay12HoursPrice > 0) cat.quickStay12HoursPrice.toInt().toString() else "",
                onValueChange = {
                    val price = it.toDoubleOrNull() ?: 0.0
                    onUpdateState(state.copy(categoryConfig = cat.copy(quickStay12HoursPrice = price)))
                },
                label = { Text("12 Hours Slot Price (₹) *") },
                placeholder = { Text("999") },
                leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("quick_stay_12h_price_input")
            )

            // 24-Hour Day Stay Price
            OutlinedTextField(
                value = if (cat.quickStay24HoursPrice > 0) cat.quickStay24HoursPrice.toInt().toString() else "",
                onValueChange = {
                    val price = it.toDoubleOrNull() ?: 0.0
                    onUpdateState(state.copy(categoryConfig = cat.copy(quickStay24HoursPrice = price)))
                },
                label = { Text("24 Hours / Full Day Stay Price (₹) *") },
                placeholder = { Text("1499") },
                leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("quick_stay_24h_price_input")
            )

            // Available Slots
            OutlinedTextField(
                value = cat.quickStayAvailableSlots.toString(),
                onValueChange = {
                    val slots = it.toIntOrNull() ?: 1
                    onUpdateState(state.copy(categoryConfig = cat.copy(quickStayAvailableSlots = slots)))
                },
                label = { Text("Total Available Slots / Pods *") },
                placeholder = { Text("4") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("quick_stay_slots_input")
            )

            // Instant Check-in Toggle
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Instant Check-in Ready ⚡",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Guests can book and check in immediately without prior notice",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = cat.quickStayInstantCheckIn,
                        onCheckedChange = {
                            onUpdateState(state.copy(categoryConfig = cat.copy(quickStayInstantCheckIn = it)))
                        }
                    )
                }
            }

            // Quick Stay Facilities
            Text(
                text = "Transit & Quick Stay Facilities Provided:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            val standardFacilities = listOf(
                "Shower & Fresh-up Kit",
                "High-Speed Wi-Fi",
                "Luggage Storage Locker",
                "Quiet AC Room",
                "Power Backup & Charging Station",
                "Hot Water & Clean Towels",
                "Complimentary Tea / Coffee"
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                standardFacilities.forEach { facility ->
                    val isChecked = cat.quickStayFacilities.contains(facility)
                    FilterChip(
                        selected = isChecked,
                        onClick = {
                            val newFacilities = if (isChecked) {
                                cat.quickStayFacilities - facility
                            } else {
                                cat.quickStayFacilities + facility
                            }
                            onUpdateState(state.copy(categoryConfig = cat.copy(quickStayFacilities = newFacilities)))
                        },
                        label = { Text(facility, fontSize = 12.sp) },
                        leadingIcon = if (isChecked) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

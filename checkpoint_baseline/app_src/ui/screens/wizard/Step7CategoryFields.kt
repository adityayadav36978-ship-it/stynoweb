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

        // Category-Specific Sharing & Audience Configuration
        com.example.ui.components.CategorySharingSetupCard(
            state = state,
            onUpdateState = onUpdateState
        )

        when (state.propertyType) {
            PropertyType.HOSTEL -> HostelSpecificForm(state, onUpdateState)
            PropertyType.PG -> PgSpecificForm(state, onUpdateState)
            PropertyType.FLAT -> FlatSpecificForm(state, onUpdateState)
            PropertyType.HOTEL -> HotelSpecificForm(state, onUpdateState)
            PropertyType.ROOM -> RoomSpecificForm(state, onUpdateState)
            PropertyType.QUICK_STAY -> QuickStaySpecificForm(state, onUpdateState)
        }

        // Universal STYNO Girls Safety Verification Application Section
        Spacer(modifier = Modifier.height(10.dp))
        GirlsSafetyVerificationApplicationSection(state = state, onUpdateState = onUpdateState)
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

@Composable
fun GirlsSafetyVerificationApplicationSection(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit,
    modifier: Modifier = Modifier
) {
    val submission = state.girlsSafetySubmission

    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (submission.applyForVerification) Color(0xFFFFF1F2) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.5.dp,
            if (submission.applyForVerification) Color(0xFFFB7185) else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Shield Icon & Official Verification Explainer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE11D48)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Girls Safety Verified",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Apply for Girls Safety Verification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (submission.applyForVerification) Color(0xFF9F1239) else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Official STYNO Trust & Safety Audit Program",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (submission.applyForVerification) Color(0xFFBE123C) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = submission.applyForVerification,
                    onCheckedChange = { checked ->
                        onUpdateState(
                            state.copy(
                                girlsSafetySubmission = submission.copy(applyForVerification = checked)
                            )
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFE11D48)
                    ),
                    modifier = Modifier.testTag("switch_girls_safety_apply")
                )
            }

            // Trust Policy Explainer Banner
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (submission.applyForVerification) Color(0xFFFFE4E6) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, if (submission.applyForVerification) Color(0xFFFDA4AF) else Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = if (submission.applyForVerification) Color(0xFFBE123C) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Text(
                        text = if (submission.applyForVerification) {
                            "⚠️ MANDATORY STYNO RULE: Owners CANNOT manually grant, toggle or display the 'Girls Safety Verified' badge. Upon publication, your listing will remain in 'Pending STYNO Audit' status until physically verified & approved by the STYNO Trust & Safety Team."
                        } else {
                            "Enable this application if your property offers dedicated safety provisions for female residents (female warden, biometric locks, 24/7 CCTV, emergency protocols)."
                        },
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = if (submission.applyForVerification) Color(0xFF881337) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (submission.applyForVerification) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            // Expanded Dossier Fields when owner chooses to apply
            if (submission.applyForVerification) {
                HorizontalDivider(color = Color(0xFFFECDD3))

                Text(
                    text = "Required Safety Verification Dossier *",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9F1239)
                )

                // 1. Female Resident Warden
                SafetyAuditToggleRow(
                    title = "Female Resident Warden on premises 24/7",
                    subtitle = "A dedicated female warden resides on property to assist female guests at all hours.",
                    checked = submission.femaleWardenPresent,
                    onCheckedChange = {
                        onUpdateState(state.copy(girlsSafetySubmission = submission.copy(femaleWardenPresent = it)))
                    }
                )

                if (submission.femaleWardenPresent) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = submission.femaleWardenName,
                            onValueChange = {
                                onUpdateState(state.copy(girlsSafetySubmission = submission.copy(femaleWardenName = it)))
                            },
                            label = { Text("Warden Full Name *") },
                            placeholder = { Text("e.g., Sunita Deshmukh") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_warden_name")
                        )
                        OutlinedTextField(
                            value = submission.femaleWardenPhone,
                            onValueChange = {
                                onUpdateState(state.copy(girlsSafetySubmission = submission.copy(femaleWardenPhone = it)))
                            },
                            label = { Text("Warden Emergency Phone *") },
                            placeholder = { Text("+91 98XXX XXXXX") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_warden_phone")
                        )
                    }
                }

                // 2. CCTV Coverage
                SafetyAuditToggleRow(
                    title = "24/7 CCTV Surveillance in Common Areas & Gates",
                    subtitle = "Continuous recording covering main gate, lobby, hallways, and perimeter (zero cameras in private rooms).",
                    checked = submission.cctvCoverageCommonAreas,
                    onCheckedChange = {
                        onUpdateState(state.copy(girlsSafetySubmission = submission.copy(cctvCoverageCommonAreas = it)))
                    }
                )

                // 3. Biometric / Smart Lock Access
                SafetyAuditToggleRow(
                    title = "Biometric / RFID Keycard Entry Gates",
                    subtitle = "Keypad, fingerprint or RFID card entry preventing unauthorized trespassers.",
                    checked = submission.biometricOrSmartLock,
                    onCheckedChange = {
                        onUpdateState(state.copy(girlsSafetySubmission = submission.copy(biometricOrSmartLock = it)))
                    }
                )

                // 4. Night Curfew / Gate Locking Timing
                OutlinedTextField(
                    value = submission.curfewOrGateLockTime,
                    onValueChange = {
                        onUpdateState(state.copy(girlsSafetySubmission = submission.copy(curfewOrGateLockTime = it)))
                    },
                    label = { Text("Gate Closing / Night Curfew Time *") },
                    placeholder = { Text("e.g. 10:00 PM") },
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_curfew_time")
                )

                // 5. Visitor Log Maintained
                SafetyAuditToggleRow(
                    title = "Strict Visitor Entry & Exit Log Register",
                    subtitle = "All guests, couriers, and maintenance personnel must show ID and register in visitor book.",
                    checked = submission.visitorLogMaintained,
                    onCheckedChange = {
                        onUpdateState(state.copy(girlsSafetySubmission = submission.copy(visitorLogMaintained = it)))
                    }
                )

                // 6. Police Verification of Staff
                SafetyAuditToggleRow(
                    title = "Police Verification Completed for all Staff",
                    subtitle = "Local police background checks completed for wardens, security, and housekeeping staff.",
                    checked = submission.policeVerificationCompleted,
                    onCheckedChange = {
                        onUpdateState(state.copy(girlsSafetySubmission = submission.copy(policeVerificationCompleted = it)))
                    }
                )

                // 7. Background Checks for Employees
                SafetyAuditToggleRow(
                    title = "Identity & Criminal Background Checks on Record",
                    subtitle = "Government ID and background verification records kept on file.",
                    checked = submission.backgroundCheckStaff,
                    onCheckedChange = {
                        onUpdateState(state.copy(girlsSafetySubmission = submission.copy(backgroundCheckStaff = it)))
                    }
                )

                // 8. Fire Safety & Emergency Exits
                SafetyAuditToggleRow(
                    title = "Fire Extinguishers & Clear Emergency Exits",
                    subtitle = "Functional fire safety equipment, first aid station, and unblocked emergency exit routes.",
                    checked = submission.fireSafetyAndEmergencyExits,
                    onCheckedChange = {
                        onUpdateState(state.copy(girlsSafetySubmission = submission.copy(fireSafetyAndEmergencyExits = it)))
                    }
                )

                // 9. Documents Upload Simulation
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Safety & Compliance Documents Attached:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9F1239)
                    )

                    val defaultDocs = listOf(
                        "Police Verification Certificate (Wardens & Staff)",
                        "Fire Department Safety NOC",
                        "CCTV Layout Plan & Monitoring Protocol",
                        "Building Society Permission Letter"
                    )

                    defaultDocs.forEach { docName ->
                        val isAttached = submission.safetyDocumentsUploaded.contains(docName)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isAttached) Color(0xFFFCE7F3) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isAttached) Color(0xFFF472B6) else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val updatedDocs = if (isAttached) {
                                        submission.safetyDocumentsUploaded - docName
                                    } else {
                                        submission.safetyDocumentsUploaded + docName
                                    }
                                    onUpdateState(state.copy(girlsSafetySubmission = submission.copy(safetyDocumentsUploaded = updatedDocs)))
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isAttached) Icons.Default.CheckCircle else Icons.Default.Description,
                                        contentDescription = null,
                                        tint = if (isAttached) Color(0xFFBE185D) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = docName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isAttached) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isAttached) Color(0xFF831843) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = if (isAttached) "Attached ✓" else "+ Attach",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAttached) Color(0xFFBE185D) else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // 10. Owner Remarks / Protocol Details
                OutlinedTextField(
                    value = submission.safetyRemarksByOwner,
                    onValueChange = {
                        onUpdateState(state.copy(girlsSafetySubmission = submission.copy(safetyRemarksByOwner = it)))
                    },
                    label = { Text("Special Safety Remarks for Audit Team") },
                    placeholder = { Text("Detail any additional security provisions: e.g. female guards on night shifts, SOS emergency buzzer in rooms, warden room location, etc.") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth().testTag("input_safety_remarks")
                )

                // Status Badge Reminder Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, Color(0xFFFCD34D))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Application Status: VERIFICATION PENDING",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F)
                            )
                            Text(
                                text = "STYNO audit team will schedule an on-site physical inspection. Only STYNO Admin can approve or suspend this badge.",
                                fontSize = 10.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SafetyAuditToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (checked) Color(0xFFFFEDD5).copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, if (checked) Color(0xFFFDBA74) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFE11D48)
                )
            )
        }
    }
}

package com.example.ui.screens.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Property
import com.example.data.model.QuickStayConfig
import com.example.data.model.QuickStayOption
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

private val PRESET_PRICES = listOf(199.0, 299.0, 399.0, 599.0)
private val ALL_DURATIONS = listOf(1, 2, 3, 4, 5, 10, 12, 24)

private val DEFAULT_FACILITIES = listOf(
    "Shower & Fresh-up Kit",
    "High-Speed Wi-Fi",
    "Luggage Storage Locker",
    "Quiet AC Room",
    "Power Backup & Charging Station",
    "Hot Water & Clean Towels",
    "Complimentary Tea / Coffee"
)

private val DEFAULT_RULES = listOf(
    "Valid Government ID required at reception",
    "Strict checkout timing at duration expiry",
    "Quiet transit hours observed for resting guests",
    "No smoking or alcohol on premises"
)

@Composable
fun OwnerQuickStaySection(
    properties: List<Property>,
    onSaveConfig: (propertyId: String, config: QuickStayConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    if (properties.isEmpty()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = StynoBluePrimary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Properties Listed Yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Please add a property listing first before configuring Quick Stay hourly options.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    var selectedPropertyIndex by remember { mutableIntStateOf(0) }
    val currentProperty = properties.getOrNull(selectedPropertyIndex) ?: properties.first()

    // Local editing state initialized from currentProperty.quickStayConfig
    var isEnabled by remember(currentProperty.id) { mutableStateOf(currentProperty.quickStayConfig.isEnabled) }
    var isAvailableNow by remember(currentProperty.id) { mutableStateOf(currentProperty.quickStayConfig.isAvailableNow) }
    var roomCapacity by remember(currentProperty.id) { mutableIntStateOf(currentProperty.quickStayConfig.roomCapacity) }
    var availableSlots by remember(currentProperty.id) { mutableIntStateOf(currentProperty.quickStayConfig.availableSlots) }
    var hourlyCheckInTime by remember(currentProperty.id) { mutableStateOf(currentProperty.quickStayConfig.hourlyCheckInTime) }
    var applicableConditions by remember(currentProperty.id) { mutableStateOf(currentProperty.quickStayConfig.applicableConditions) }
    var rulesList by remember(currentProperty.id) { mutableStateOf(currentProperty.quickStayConfig.rules) }
    var selectedFacilities by remember(currentProperty.id) { mutableStateOf(currentProperty.quickStayConfig.facilities.toSet()) }

    // Map of duration to pair of (enabled, price)
    var durationPricingMap by remember(currentProperty.id) {
        val map = mutableMapOf<Int, Pair<Boolean, Double>>()
        val existingOptions = currentProperty.quickStayConfig.configuredOptions.associateBy { it.durationHours }

        ALL_DURATIONS.forEach { d ->
            val opt = existingOptions[d]
            val defaultPrice = when (d) {
                1 -> 199.0
                2 -> 299.0
                3 -> 299.0
                4 -> 399.0
                5 -> 399.0
                10 -> 599.0
                12 -> 599.0
                24 -> 999.0
                else -> 299.0
            }
            if (opt != null) {
                map[d] = Pair(opt.isEnabled, opt.price)
            } else {
                map[d] = Pair(true, defaultPrice)
            }
        }
        mutableStateOf(map)
    }

    var showSavedNotification by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StynoBluePrimary.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, StynoBluePrimary.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(StynoBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quick Stay Hourly Management",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Offer flexible 1h–24h transit stays at ₹199, ₹299, ₹399, ₹599 for daytime & transit guests.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Property Selector Chips (if multiple properties)
        if (properties.size > 1) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Select Property to Configure:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(properties.indices.toList()) { index ->
                        val prop = properties[index]
                        val isSelected = index == selectedPropertyIndex
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedPropertyIndex = index
                                showSavedNotification = false
                            },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(prop.name.ifBlank { "Property ${index + 1}" })
                                    if (prop.quickStayConfig.isEnabled) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Bolt,
                                            contentDescription = "Quick Stay Active",
                                            tint = StynoBluePrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StynoBluePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Master Enable / Disable Toggle Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isEnabled) StynoEmerald.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            border = BorderStroke(
                1.dp,
                if (isEnabled) StynoEmerald.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Quick Stay Bookings",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isEnabled) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isEnabled) "ACTIVE IN SEARCH" else "DISABLED",
                                color = if (isEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isEnabled)
                            "Guests searching for Quick Stay will see ${currentProperty.name} with instant hourly booking options."
                        else
                            "Turn ON to start receiving daytime, commuter, and short transit bookings.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { isEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = StynoEmerald, checkedTrackColor = StynoEmerald.copy(alpha = 0.5f))
                )
            }
        }

        AnimatedVisibility(visible = isEnabled) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Section 1: Duration Slots & Pricing
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Available Durations & Hourly Pricing",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Presets: ₹199-₹599",
                                fontSize = 11.sp,
                                color = StynoBluePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "Toggle the durations you want to offer. Select from standard STYNO presets (₹199, ₹299, ₹399, ₹599) or enter custom rates.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Duration Configuration Rows
                        ALL_DURATIONS.forEach { duration ->
                            val currentSetting = durationPricingMap[duration] ?: Pair(true, 299.0)
                            val isDurationEnabled = currentSetting.first
                            val price = currentSetting.second

                            DurationConfigRow(
                                durationHours = duration,
                                isEnabled = isDurationEnabled,
                                price = price,
                                onToggle = { enabled ->
                                    val updated = durationPricingMap.toMutableMap()
                                    updated[duration] = Pair(enabled, price)
                                    durationPricingMap = updated
                                },
                                onPriceChange = { newPrice ->
                                    val updated = durationPricingMap.toMutableMap()
                                    updated[duration] = Pair(isDurationEnabled, newPrice)
                                    durationPricingMap = updated
                                }
                            )
                        }
                    }
                }

                // Section 2: Room Capacity & Real-Time Availability
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = StynoAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Availability, Capacity & Check-in",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Instant Availability Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Available Now for Instant Check-In", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Guests can book and arrive immediately", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = isAvailableNow, onCheckedChange = { isAvailableNow = it })
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Room Capacity & Slots
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Max Room Capacity", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(1, 2, 3, 4).forEach { cap ->
                                        FilterChip(
                                            selected = roomCapacity == cap,
                                            onClick = { roomCapacity = cap },
                                            label = { Text("$cap Guest${if (cap > 1) "s" else ""}") }
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Available Transit Pods / Rooms", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Total concurrent transit slots available", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (availableSlots > 1) availableSlots-- },
                                    enabled = availableSlots > 1
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease slots")
                                }
                                Text(
                                    text = "$availableSlots Slots",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                IconButton(
                                    onClick = { if (availableSlots < 20) availableSlots++ }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase slots")
                                }
                            }
                        }

                        OutlinedTextField(
                            value = hourlyCheckInTime,
                            onValueChange = { hourlyCheckInTime = it },
                            label = { Text("Check-in Window & Hours") },
                            placeholder = { Text("e.g. 24/7 Any Time or 6:00 AM - 11:00 PM") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                // Section 3: Rules & Applicable Conditions
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Rules & Applicable Conditions",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Standard Transit Rules for Guests:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        DEFAULT_RULES.forEach { rule ->
                            val isRuleActive = rulesList.contains(rule)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isRuleActive,
                                    onCheckedChange = { checked ->
                                        rulesList = if (checked) {
                                            rulesList + rule
                                        } else {
                                            rulesList - rule
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(rule, fontSize = 13.sp)
                            }
                        }

                        OutlinedTextField(
                            value = applicableConditions,
                            onValueChange = { applicableConditions = it },
                            label = { Text("Applicable Conditions & Transit Policies") },
                            placeholder = { Text("e.g. Overstay charged at ₹100/hr. Extension subject to pod availability.") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                }

                // Section 4: Transit Facilities
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Transit Amenities & Fresh-up Facilities",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DEFAULT_FACILITIES.forEach { facility ->
                            val isChecked = selectedFacilities.contains(facility)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedFacilities = if (checked) {
                                            selectedFacilities + facility
                                        } else {
                                            selectedFacilities - facility
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(facility, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Saved Success Feedback Banner
        AnimatedVisibility(visible = showSavedNotification) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = StynoEmerald.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Quick Stay settings saved! Real-time search and booking pricing are now active.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StynoEmerald
                    )
                }
            }
        }

        // Save Action Button
        Button(
            onClick = {
                val configuredOptions = ALL_DURATIONS.map { duration ->
                    val setting = durationPricingMap[duration] ?: Pair(true, 299.0)
                    QuickStayOption(
                        durationHours = duration,
                        price = setting.second,
                        isEnabled = setting.first
                    )
                }

                val p1 = durationPricingMap[1]?.second ?: 199.0
                val p2 = durationPricingMap[2]?.second ?: 299.0
                val p3 = durationPricingMap[3]?.second ?: 299.0
                val p4 = durationPricingMap[4]?.second ?: 399.0
                val p5 = durationPricingMap[5]?.second ?: 399.0
                val p10 = durationPricingMap[10]?.second ?: 599.0
                val p12 = durationPricingMap[12]?.second ?: 599.0
                val p24 = durationPricingMap[24]?.second ?: 999.0

                val newConfig = QuickStayConfig(
                    isEnabled = isEnabled,
                    hourlyPrice = p1,
                    slot1HourPrice = p1,
                    slot2HoursPrice = p2,
                    slot3HoursPrice = p3,
                    slot4HoursPrice = p4,
                    slot5HoursPrice = p5,
                    slot6HoursPrice = p5,
                    slot10HoursPrice = p10,
                    slot12HoursPrice = p12,
                    slot24HoursPrice = p24,
                    availableDurationsHours = configuredOptions.filter { it.isEnabled }.map { it.durationHours },
                    configuredOptions = configuredOptions,
                    isAvailableNow = isAvailableNow,
                    roomCapacity = roomCapacity,
                    availableSlots = availableSlots,
                    rules = rulesList,
                    applicableConditions = applicableConditions,
                    facilities = selectedFacilities.toList(),
                    checkInInstructions = "Instant check-in at reception with valid ID.",
                    cancellationPolicy = "Free cancellation up to 1 hour before booked slot.",
                    luggageStorageAvailable = selectedFacilities.contains("Luggage Storage Locker"),
                    instantShowerAccess = selectedFacilities.contains("Shower & Fresh-up Kit"),
                    hourlyCheckInTime = hourlyCheckInTime.ifBlank { "24/7 Any Time" },
                    instantCheckIn = isAvailableNow,
                    transitFacilities = selectedFacilities.toList()
                )

                onSaveConfig(currentProperty.id, newConfig)
                showSavedNotification = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Quick Stay Configuration", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun DurationConfigRow(
    durationHours: Int,
    isEnabled: Boolean,
    price: Double,
    onToggle: (Boolean) -> Unit,
    onPriceChange: (Double) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isEnabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
        border = BorderStroke(
            0.5.dp,
            if (isEnabled) StynoBluePrimary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isEnabled, onCheckedChange = onToggle)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$durationHours ${if (durationHours == 1) "Hour" else "Hours"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isEnabled) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StynoBluePrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "₹${price.toInt()}",
                            fontWeight = FontWeight.Bold,
                            color = StynoBluePrimary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (isEnabled) {
                // Preset choice chips: ₹199, ₹299, ₹399, ₹599
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Presets:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    PRESET_PRICES.forEach { preset ->
                        val isSelected = price == preset
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .height(26.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            onClick = { onPriceChange(preset) }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text(
                                    text = "₹${preset.toInt()}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

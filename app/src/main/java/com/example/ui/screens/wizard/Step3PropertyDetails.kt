package com.example.ui.screens.wizard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

val checkInChips = listOf("10:00 AM", "11:00 AM", "12:00 PM", "02:00 PM", "24x7 Flexible")
val checkOutChips = listOf("10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM")
val curfewChips = listOf("10:00 PM", "10:30 PM", "11:00 PM", "No Curfew (24x7 Entry)")

@Composable
fun Step3PropertyDetails(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
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
                        imageVector = Icons.Default.MeetingRoom,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 3 of 10: Property Details & Timings",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Specify room inventory, floor details, immediate availability, and check-in/out policies.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section: Availability
        OutlinedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Immediate Availability",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (state.isAvailableImmediately) "Ready for immediate move-in / guest bookings" else "Available from a specified future date",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = state.isAvailableImmediately,
                        onCheckedChange = { onUpdateState(state.copy(isAvailableImmediately = it)) },
                        modifier = Modifier.testTag("switch_immediate_availability")
                    )
                }

                AnimatedVisibility(visible = !state.isAvailableImmediately) {
                    OutlinedTextField(
                        value = state.availableFromDate,
                        onValueChange = { onUpdateState(state.copy(availableFromDate = it)) },
                        label = { Text("Available From Date") },
                        placeholder = { Text("e.g. 1st of Next Month") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section: Room Counts & Capacity
        OutlinedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Inventory & Room Counts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Rooms
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Total Rooms / Units", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledIconButton(
                                onClick = { if (state.totalRooms > 1) onUpdateState(state.copy(totalRooms = state.totalRooms - 1)) },
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "${state.totalRooms}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            FilledIconButton(
                                onClick = { onUpdateState(state.copy(totalRooms = state.totalRooms + 1)) },
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Available Rooms
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Available Rooms / Beds", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledIconButton(
                                onClick = { if (state.availableRooms > 0) onUpdateState(state.copy(availableRooms = state.availableRooms - 1)) },
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "${state.availableRooms}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            FilledIconButton(
                                onClick = { if (state.availableRooms < state.totalRooms) onUpdateState(state.copy(availableRooms = state.availableRooms + 1)) },
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Section: Floor & Lift
        OutlinedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Floor & Building Accessibility",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = state.floorNumber,
                    onValueChange = { onUpdateState(state.copy(floorNumber = it)) },
                    label = { Text("Floor Number & Building Height") },
                    placeholder = { Text("e.g. 2nd of 4 Floors, Ground Floor") },
                    leadingIcon = { Icon(Icons.Default.Stairs, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("input_floor_number")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Elevator, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("Elevator / Lift Available", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Building has functional passenger lift", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Switch(
                        checked = state.hasElevator,
                        onCheckedChange = { onUpdateState(state.copy(hasElevator = it)) },
                        modifier = Modifier.testTag("switch_elevator")
                    )
                }
            }
        }

        // Section: Timings (Check-in, Check-out, Gate Closing)
        OutlinedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Check-in, Check-out & Gate Closing Times",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // Check-in Time
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Check-in Time", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        checkInChips.forEach { timeStr ->
                            FilterChip(
                                selected = state.checkInTime == timeStr,
                                onClick = { onUpdateState(state.copy(checkInTime = timeStr)) },
                                label = { Text(timeStr, fontSize = 12.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = state.checkInTime,
                        onValueChange = { onUpdateState(state.copy(checkInTime = it)) },
                        label = { Text("Custom Check-in Time") },
                        leadingIcon = { Icon(Icons.Default.Login, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_check_in_time")
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Check-out Time
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Check-out Time", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        checkOutChips.forEach { timeStr ->
                            FilterChip(
                                selected = state.checkOutTime == timeStr,
                                onClick = { onUpdateState(state.copy(checkOutTime = timeStr)) },
                                label = { Text(timeStr, fontSize = 12.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = state.checkOutTime,
                        onValueChange = { onUpdateState(state.copy(checkOutTime = it)) },
                        label = { Text("Custom Check-out Time") },
                        leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_check_out_time")
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Gate Closing / Curfew Time
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Gate Closing / Curfew Time", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        curfewChips.forEach { timeStr ->
                            FilterChip(
                                selected = state.gateClosingTime == timeStr,
                                onClick = { onUpdateState(state.copy(gateClosingTime = timeStr)) },
                                label = { Text(timeStr, fontSize = 12.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = state.gateClosingTime,
                        onValueChange = { onUpdateState(state.copy(gateClosingTime = it)) },
                        label = { Text("Custom Curfew / Gate Closing Time") },
                        leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_curfew_time")
                    )
                }
            }
        }
    }
}

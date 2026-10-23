package com.example.ui.screens.wizard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DurationType
import com.example.data.model.PropertyType
import com.example.data.model.RoomTypeConfig
import java.util.UUID

@Composable
fun Step4RoomPricing(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddRoomDialog by remember { mutableStateOf(false) }

    val durationLabel = when (state.propertyType) {
        PropertyType.HOTEL -> "/night"
        PropertyType.QUICK_STAY -> "/slot"
        else -> "/month"
    }

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
                        imageVector = Icons.Default.CurrencyRupee,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 4 of 10: Room Types & AC/Non-AC Pricing",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (state.propertyType == PropertyType.QUICK_STAY)
                            "Configure your Quick Stay day-use pods/rooms and flexible duration pricing slots."
                        else
                            "Configure multiple room categories. Specify distinct AC vs Non-AC tariffs, security deposits, and available inventory.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Configured Room Types (${state.rooms.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            FilledTonalButton(
                onClick = { showAddRoomDialog = true },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_add_room_type")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Room Type")
            }
        }

        // Room Cards List
        state.rooms.forEachIndexed { index, room ->
            RoomConfigCard(
                room = room,
                durationLabel = durationLabel,
                onUpdate = { updatedRoom ->
                    val updatedList = state.rooms.toMutableList()
                    updatedList[index] = updatedRoom
                    onUpdateState(state.copy(rooms = updatedList))
                },
                onDelete = {
                    if (state.rooms.size > 1) {
                        val updatedList = state.rooms.filterIndexed { i, _ -> i != index }
                        onUpdateState(state.copy(rooms = updatedList))
                    }
                },
                canDelete = state.rooms.size > 1
            )
        }
    }

    if (showAddRoomDialog) {
        AddRoomTypeDialog(
            propertyType = state.propertyType,
            durationLabel = durationLabel,
            onDismiss = { showAddRoomDialog = false },
            onAdd = { newRoom ->
                onUpdateState(state.copy(rooms = state.rooms + newRoom))
                showAddRoomDialog = false
            }
        )
    }
}

@Composable
fun RoomConfigCard(
    room: RoomTypeConfig,
    durationLabel: String,
    onUpdate: (RoomTypeConfig) -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean
) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Name & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sharing: ${room.sharingType} • ${if (room.hasAttachedBath) "Attached Bath" else "Shared Bath"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Room", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // AC Option Section
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (room.isAcAvailable) Color(0xFFE3F2FD) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = BorderStroke(1.dp, if (room.isAcAvailable) Color(0xFF1976D2) else Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AcUnit, contentDescription = null, tint = if (room.isAcAvailable) Color(0xFF1976D2) else Color.Gray)
                            Text("Air Conditioner (AC) Option", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Switch(
                            checked = room.isAcAvailable,
                            onCheckedChange = { onUpdate(room.copy(isAcAvailable = it)) }
                        )
                    }

                    AnimatedVisibility(visible = room.isAcAvailable) {
                        OutlinedTextField(
                            value = if (room.acPrice > 0) room.acPrice.toInt().toString() else "",
                            onValueChange = { onUpdate(room.copy(acPrice = it.toDoubleOrNull() ?: 0.0)) },
                            label = { Text("AC Rent (₹ $durationLabel) *") },
                            placeholder = { Text("8500") },
                            leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Non-AC Option Section
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (room.isNonAcAvailable) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = BorderStroke(1.dp, if (room.isNonAcAvailable) Color(0xFFFF9800) else Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.ModeFanOff, contentDescription = null, tint = if (room.isNonAcAvailable) Color(0xFFFF9800) else Color.Gray)
                            Text("Non-AC (Air Cooled) Option", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Switch(
                            checked = room.isNonAcAvailable,
                            onCheckedChange = { onUpdate(room.copy(isNonAcAvailable = it)) }
                        )
                    }

                    AnimatedVisibility(visible = room.isNonAcAvailable) {
                        OutlinedTextField(
                            value = if (room.nonAcPrice > 0) room.nonAcPrice.toInt().toString() else "",
                            onValueChange = { onUpdate(room.copy(nonAcPrice = it.toDoubleOrNull() ?: 0.0)) },
                            label = { Text("Non-AC Rent (₹ $durationLabel) *") },
                            placeholder = { Text("6500") },
                            leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Security Deposit & Attached Bath
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = if (room.securityDeposit > 0) room.securityDeposit.toInt().toString() else "",
                    onValueChange = { onUpdate(room.copy(securityDeposit = it.toDoubleOrNull() ?: 0.0)) },
                    label = { Text("Security Deposit (₹)") },
                    placeholder = { Text("5000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = room.availableRooms.toString(),
                    onValueChange = { onUpdate(room.copy(availableRooms = it.toIntOrNull() ?: 0)) },
                    label = { Text("Available Units") },
                    placeholder = { Text("2") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.8f)
                )
            }

            // Attached Bath Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Private Attached Bathroom", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = room.hasAttachedBath,
                    onCheckedChange = { onUpdate(room.copy(hasAttachedBath = it)) }
                )
            }
        }
    }
}

@Composable
fun AddRoomTypeDialog(
    propertyType: PropertyType,
    durationLabel: String,
    onDismiss: () -> Unit,
    onAdd: (RoomTypeConfig) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sharing by remember { mutableStateOf("Private / Single") }
    var isAc by remember { mutableStateOf(true) }
    var acPrice by remember { mutableStateOf("8000") }
    var isNonAc by remember { mutableStateOf(false) }
    var nonAcPrice by remember { mutableStateOf("6000") }
    var deposit by remember { mutableStateOf("5000") }
    var attachedBath by remember { mutableStateOf(true) }
    var availableCount by remember { mutableStateOf("2") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Room Configuration", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Room Name *") },
                    placeholder = { Text("e.g. Master Bedroom, 4-Sharing Dorm") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sharing,
                    onValueChange = { sharing = it },
                    label = { Text("Sharing Type") },
                    placeholder = { Text("e.g. 2-Sharing, Single Private") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Has AC Option", fontSize = 13.sp)
                    Switch(checked = isAc, onCheckedChange = { isAc = it })
                }
                if (isAc) {
                    OutlinedTextField(
                        value = acPrice,
                        onValueChange = { acPrice = it },
                        label = { Text("AC Price (₹ $durationLabel)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Has Non-AC Option", fontSize = 13.sp)
                    Switch(checked = isNonAc, onCheckedChange = { isNonAc = it })
                }
                if (isNonAc) {
                    OutlinedTextField(
                        value = nonAcPrice,
                        onValueChange = { nonAcPrice = it },
                        label = { Text("Non-AC Price (₹ $durationLabel)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = deposit,
                    onValueChange = { deposit = it },
                    label = { Text("Security Deposit (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(
                        RoomTypeConfig(
                            id = UUID.randomUUID().toString(),
                            name = name.ifBlank { "Room Type" },
                            sharingType = sharing,
                            isAcAvailable = isAc,
                            acPrice = acPrice.toDoubleOrNull() ?: 0.0,
                            isNonAcAvailable = isNonAc,
                            nonAcPrice = nonAcPrice.toDoubleOrNull() ?: 0.0,
                            hasAttachedBath = attachedBath,
                            securityDeposit = deposit.toDoubleOrNull() ?: 0.0,
                            totalRooms = 4,
                            availableRooms = availableCount.toIntOrNull() ?: 1
                        )
                    )
                }
            ) {
                Text("Add Configuration")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

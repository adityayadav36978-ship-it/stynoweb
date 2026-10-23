package com.example.ui.screens.wizard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PropertyDraftEntity

@Composable
fun Step9PublishAndDrafts(
    state: PropertyWizardState,
    savedDrafts: List<PropertyDraftEntity>,
    onSaveDraft: () -> Unit,
    onPublish: () -> Unit,
    onDelete: () -> Unit,
    onEditStep: (Int) -> Unit,
    onResumeDraft: (PropertyDraftEntity) -> Unit,
    onDeleteDraftById: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showDraftsModal by remember { mutableStateOf(false) }

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
                        imageVector = Icons.Default.Publish,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 9 of 10: Publish, Save Draft & Management",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Publish live to Styno search catalog, proceed to payout setup, save draft, or edit any section.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Main Actions Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Listing Finalization Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // 1. Next: Step 10 Payment Setup Button
                Button(
                    onClick = { onEditStep(10) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_proceed_to_payment_step")
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Proceed to Step 10: Payment Setup", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                // 2. Publish Live Button
                FilledTonalButton(
                    onClick = onPublish,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF2E7D32), contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_publish_listing")
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Property Live", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                // 3. Save Draft Button
                OutlinedButton(
                    onClick = onSaveDraft,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_draft")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save as Draft (Resume Later)", fontWeight = FontWeight.SemiBold)
                }

                // 4. Delete / Discard Option
                TextButton(
                    onClick = { showDeleteConfirmDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_delete_listing_option")
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (state.editingOriginalPropertyId != null) "Delete Live Listing" else "Discard / Delete Draft")
                }
            }
        }

        // Section: Quick Edit Jump-to-Step
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Edit Any Listing Section",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                listOf(
                    1 to ("Step 1: Property Type" to "Type: ${state.propertyType.displayName}"),
                    2 to ("Step 2: Basic Info" to "${state.propertyName.ifBlank { "Untitled" }} • ${state.city}"),
                    3 to ("Step 3: Property Details" to "${state.totalRooms} Rooms • Floor: ${state.floorNumber}"),
                    4 to ("Step 4: Rooms & Pricing" to "${state.rooms.size} Room Type(s) • AC & Non-AC"),
                    5 to ("Step 5: Amenities" to "${state.amenities.count { it.status == com.example.data.model.AmenityPricingType.FREE }} Free • ${state.amenities.count { it.status == com.example.data.model.AmenityPricingType.PAID }} Paid"),
                    6 to ("Step 6: Food Services" to if (state.foodConfig.hasFoodService) state.foodConfig.dietaryType.displayName else "No food service"),
                    7 to ("Step 7: Category Fields" to "${state.propertyType.displayName} Specific Fields"),
                    8 to ("Step 8: Customer Preview" to "Public Listing Card & Details Preview"),
                    9 to ("Step 9: Publish & Management" to "Draft & Catalog Visibility"),
                    10 to ("Step 10: Payment Setup" to if (state.isPaymentSetupCompleted || state.ownerPaymentDetails.isConfigured) "Payout Channel Configured (${state.ownerPaymentDetails.settlementMode})" else "Setup Payout Account / UPI")
                ).forEach { (stepNum, info) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onEditStep(stepNum) }
                            .testTag("btn_quick_edit_step_$stepNum"),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(info.first, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(info.second, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Edit", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Section: Saved Drafts Drawer Link
        if (savedDrafts.isNotEmpty()) {
            OutlinedCard(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDraftsModal = true }
                    .testTag("btn_view_saved_drafts")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("Saved Property Drafts (${savedDrafts.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Resume editing or delete previous drafts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text(if (state.editingOriginalPropertyId != null) "Delete Live Listing?" else "Discard Draft?") },
            text = {
                Text(
                    if (state.editingOriginalPropertyId != null)
                        "Are you sure you want to permanently delete '${state.propertyName}'? It will be removed from customer search results and owner dashboard."
                    else
                        "Are you sure you want to discard this draft? Unsaved changes will be cleared."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("btn_confirm_delete_listing")
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Saved Drafts Sheet / Dialog
    if (showDraftsModal) {
        AlertDialog(
            onDismissRequest = { showDraftsModal = false },
            title = { Text("Saved Property Drafts (${savedDrafts.size})", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    savedDrafts.forEach { draft ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            onResumeDraft(draft)
                                            showDraftsModal = false
                                        }
                                ) {
                                    Text(
                                        text = draft.propertyName.ifBlank { "Untitled ${draft.propertyType} Draft" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${draft.propertyType} • Step ${draft.currentStep}/9 • ${draft.city}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row {
                                    TextButton(
                                        onClick = {
                                            onResumeDraft(draft)
                                            showDraftsModal = false
                                        }
                                    ) {
                                        Text("Resume", fontSize = 12.sp)
                                    }

                                    IconButton(onClick = { onDeleteDraftById(draft.id) }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDraftsModal = false }) {
                    Text("Close")
                }
            }
        )
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.VerificationStatus
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val complaints by viewModel.complaints.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "STYNO Platform Admin",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Listing audit, safety verification & KYC approvals",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminMetricCard("Total Stays", "${allProperties.size}", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    AdminMetricCard("Bookings", "${bookings.size}", StynoEmerald, Modifier.weight(1f))
                    AdminMetricCard("Complaints", "${complaints.size}", StynoAccent, Modifier.weight(1f))
                }
            }

            // Resident Complaint Management Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Instant Action Resident Complaints (${complaints.count { it.status.name != "RESOLVED" }} Active)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(complaints) { complaint ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = when (complaint.priority) {
                            com.example.data.model.ComplaintPriority.INSTANT_ACTION -> Color(0xFFEF4444).copy(alpha = 0.5f)
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${complaint.category.displayName} • ${complaint.propertyName ?: "General"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (complaint.status) {
                                    com.example.data.model.ComplaintStatus.RESOLVED -> StynoEmerald.copy(alpha = 0.15f)
                                    com.example.data.model.ComplaintStatus.IN_PROGRESS -> StynoBluePrimary.copy(alpha = 0.15f)
                                    com.example.data.model.ComplaintStatus.ASSIGNED -> Color(0xFFFEF3C7)
                                    com.example.data.model.ComplaintStatus.SUBMITTED -> Color(0xFFFEE2E2)
                                    else -> Color(0xFFF1F5F9)
                                }
                            ) {
                                Text(
                                    text = complaint.status.displayName,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (complaint.status) {
                                        com.example.data.model.ComplaintStatus.RESOLVED -> StynoEmerald
                                        com.example.data.model.ComplaintStatus.IN_PROGRESS -> StynoBluePrimary
                                        com.example.data.model.ComplaintStatus.ASSIGNED -> Color(0xFF92400E)
                                        com.example.data.model.ComplaintStatus.SUBMITTED -> Color(0xFFDC2626)
                                        else -> Color(0xFF475569)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = complaint.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = complaint.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resident: ${complaint.userName} (${complaint.userPhone})",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = { viewModel.openComplaintDetail(complaint) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Update Status", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Verification & Audit Desk",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }


            items(allProperties) { property ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = property.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Host: ${property.ownerInfo.name} • ${property.city}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (property.verificationStatus) {
                                    VerificationStatus.VERIFIED -> StynoEmerald.copy(alpha = 0.15f)
                                    VerificationStatus.UNDER_REVIEW -> Color(0xFFFEF3C7)
                                    VerificationStatus.PENDING -> Color(0xFFE2E8F0)
                                    VerificationStatus.REJECTED -> Color(0xFFFEE2E2)
                                }
                            ) {
                                Text(
                                    text = property.verificationStatus.displayName,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (property.verificationStatus) {
                                        VerificationStatus.VERIFIED -> StynoEmerald
                                        VerificationStatus.UNDER_REVIEW -> Color(0xFF92400E)
                                        VerificationStatus.PENDING -> Color(0xFF475569)
                                        VerificationStatus.REJECTED -> Color(0xFFDC2626)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Amenities: ${property.shortFacilities.take(3).joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (property.verificationStatus != VerificationStatus.VERIFIED) {
                                    Button(
                                        onClick = { viewModel.updateVerificationStatus(property.id, VerificationStatus.VERIFIED) },
                                        colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Approve", fontSize = 11.sp)
                                    }
                                }

                                OutlinedButton(
                                    onClick = { viewModel.openPropertyDetails(property) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Audit Site", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = color))
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

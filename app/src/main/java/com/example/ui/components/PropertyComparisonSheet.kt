package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Property
import com.example.data.model.VerificationStatus
import com.example.ui.theme.StynoAmber
import com.example.ui.theme.StynoGreen
import com.example.ui.theme.StynoIndigo
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyComparisonSheet(
    viewModel: StynoViewModel,
    properties: List<Property>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier
            .fillMaxHeight(0.9f)
            .testTag("property_comparison_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Top Bar
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(StynoIndigo.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = null,
                                tint = StynoIndigo,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Side-by-Side Comparison",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Comparing ${properties.size} properties",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            HorizontalDivider()

            if (properties.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No properties selected for comparison.\nSelect properties in Saved tab to compare.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                val prop1 = properties[0]
                val prop2 = properties.getOrNull(1)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Headers Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ComparisonHeaderCard(
                            property = prop1,
                            modifier = Modifier.weight(1f),
                            onViewDetails = {
                                onDismiss()
                                viewModel.openPropertyDetails(prop1)
                            }
                        )

                        if (prop2 != null) {
                            ComparisonHeaderCard(
                                property = prop2,
                                modifier = Modifier.weight(1f),
                                onViewDetails = {
                                    onDismiss()
                                    viewModel.openPropertyDetails(prop2)
                                }
                            )
                        } else {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(180.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+ Select 2nd property\nfrom Saved Stays",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider()

                    // Metrics Comparison Matrix
                    ComparisonRow("Starting Price", "₹${prop1.startingPrice.toInt()} / ${prop1.durationType.name.lowercase()}", prop2?.let { "₹${it.startingPrice.toInt()} / ${it.durationType.name.lowercase()}" } ?: "-")
                    ComparisonRow("Property Type", prop1.propertyType.displayName, prop2?.propertyType?.displayName ?: "-")
                    ComparisonRow("Suitability", prop1.genderSuitability.displayName, prop2?.genderSuitability?.displayName ?: "-")
                    ComparisonRow("Rating & Reviews", "★ ${prop1.rating} (${prop1.reviewCount})", prop2?.let { "★ ${it.rating} (${it.reviewCount})" } ?: "-")
                    ComparisonRow("Verification", if (prop1.verificationStatus == VerificationStatus.VERIFIED) "✓ Verified by STYNO" else "Pending Audit", prop2?.let { if (it.verificationStatus == VerificationStatus.VERIFIED) "✓ Verified by STYNO" else "Pending Audit" } ?: "-")
                    ComparisonRow("Location", "${prop1.area}, ${prop1.city}", prop2?.let { "${it.area}, ${it.city}" } ?: "-")
                    ComparisonRow("Distance", "${prop1.distanceKm} km from hub", prop2?.let { "${it.distanceKm} km from hub" } ?: "-")
                    ComparisonRow("Security Deposit", "₹${prop1.roomOptions.firstOrNull()?.securityDeposit?.toInt() ?: 0} (Refundable)", prop2?.let { "₹${it.roomOptions.firstOrNull()?.securityDeposit?.toInt() ?: 0} (Refundable)" } ?: "-")
                    ComparisonRow("Curfew / Gate Rule", prop1.rules.firstOrNull { it.title.contains("Curfew", true) }?.description ?: "Flexible Gate Access", prop2?.rules?.firstOrNull { it.title.contains("Curfew", true) }?.description ?: "Flexible Gate Access")
                    ComparisonRow("Key Amenities", prop1.shortFacilities.take(3).joinToString(", "), prop2?.shortFacilities?.take(3)?.joinToString(", ") ?: "-")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onDismiss()
                                viewModel.startBookingFlow(prop1)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StynoIndigo)
                        ) {
                            Text("Book Stay 1", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        if (prop2 != null) {
                            Button(
                                onClick = {
                                    onDismiss()
                                    viewModel.startBookingFlow(prop2)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StynoIndigo)
                            ) {
                                Text("Book Stay 2", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonHeaderCard(
    property: Property,
    modifier: Modifier = Modifier,
    onViewDetails: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StynoIndigo.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = property.propertyType.displayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StynoIndigo
                    )
                }

                if (property.verificationStatus == VerificationStatus.VERIFIED) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = StynoGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = property.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = property.area,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onViewDetails,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Details", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ComparisonRow(
    metricTitle: String,
    val1: String,
    val2: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = metricTitle.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = val1,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = val2,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

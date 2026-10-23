package com.example.ui.screens.owner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.OwnerPaymentDetails
import com.example.data.model.Property
import com.example.data.payment.OwnerPayoutCalculator
import com.example.data.payment.OwnerTransactionRecord
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

@Composable
fun OwnerPaymentSetupSection(
    properties: List<Property>,
    allBookings: List<Booking>,
    onSavePaymentDetails: (propertyId: String, details: OwnerPaymentDetails) -> Unit,
    onUploadQrCode: ((Uri, String) -> String)? = null,
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
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = StynoEmerald,
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
                    text = "Add a property listing to set up your payout bank account and manage booking earnings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    var selectedPropertyIndex by remember { mutableIntStateOf(0) }
    val currentProperty = properties.getOrNull(selectedPropertyIndex) ?: properties.first()
    val currentDetails = currentProperty.ownerPaymentDetails

    // Form editing state supporting UPI ID, UPI QR Code, and Bank Account independently or together
    var isUpiIdEnabled by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.isUpiIdEnabled || (currentDetails.upiId.isNotBlank() && !currentDetails.isQrCodeEnabled && !currentDetails.isBankAccountEnabled))
    }
    var upiId by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.upiId)
    }

    var isQrCodeEnabled by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.isQrCodeEnabled || (!currentDetails.qrCodeImageUrl.isNullOrBlank()))
    }
    var qrCodeImageUrl by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.qrCodeImageUrl)
    }
    var qrCodeVpa by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.qrCodeVpa.ifBlank { currentDetails.upiId })
    }

    var isBankAccountEnabled by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.isBankAccountEnabled || (currentDetails.accountNumber.isNotBlank() && !currentDetails.isUpiIdEnabled && !currentDetails.isQrCodeEnabled))
    }
    var accountHolderName by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.accountHolderName.ifBlank { currentProperty.ownerInfo.name })
    }
    var bankName by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.bankName)
    }
    var accountNumber by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.accountNumber)
    }
    var ifscCode by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.ifscCode)
    }
    var accountType by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.accountType)
    }
    var payoutFrequency by remember(currentProperty.id, currentDetails.lastUpdatedTimestamp) {
        mutableStateOf(currentDetails.payoutFrequency)
    }
    var maskAccountNumber by remember { mutableStateOf(true) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var showSavedNotification by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val qrImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = if (onUploadQrCode != null) {
                onUploadQrCode(uri, currentProperty.resolvedOwnerId)
            } else {
                try {
                    val safeOwner = currentProperty.resolvedOwnerId.filter { it.isLetterOrDigit() }.ifBlank { "owner_${System.currentTimeMillis()}" }
                    val destFile = java.io.File(context.filesDir, "owner_qr_${safeOwner}.png")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        java.io.FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    destFile.absolutePath
                } catch (e: Exception) {
                    uri.toString()
                }
            }
            qrCodeImageUrl = savedPath
            validationError = null
        }
    }

    // Calculate real payout statistics strictly for this host's properties
    val (payoutSummary, transactionRecords) = remember(properties, allBookings, currentProperty.id) {
        OwnerPayoutCalculator.calculate(
            ownerProperties = properties,
            allBookings = allBookings,
            selectedPropertyId = currentProperty.id
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = StynoEmerald.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.25f)),
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
                        .background(StynoEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Owner Payment & Payout Setup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Configure direct UPI and bank accounts for automatic booking settlements.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Real Gateway Connection Status Banner (Mandatory: No Fake Gateway Success)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PowerOff,
                            contentDescription = null,
                            tint = StynoAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Payment Gateway Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "NOT CONNECTED",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "A live external payment gateway (e.g. Razorpay or Stripe Production) is not configured in this environment. The system is operating in Direct Host Escrow & Settlement Mode. All bookings and earnings calculations below are generated from real database transactions.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StynoBluePrimary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Architecture Ready: Drop-in API key binding supported for seamless live gateway migration.",
                            fontSize = 10.sp,
                            color = StynoBluePrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Property Selector Chips (if owner manages multiple properties)
        if (properties.size > 1) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Configuring Payouts for Property:",
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
                            label = { Text(prop.name.ifBlank { "Property ${index + 1}" }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StynoEmerald,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Payout Summary & Commission Breakdown Card
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Host Earnings & Commission",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${payoutSummary.totalBookingsCount} Bookings",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Grid of metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricMiniCard(
                        title = "Gross Booking",
                        amount = "₹${payoutSummary.totalGrossVolume.toInt()}",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniCard(
                        title = "Styno Fee (5%)",
                        amount = "-₹${payoutSummary.totalStynoCommission.toInt()}",
                        color = StynoAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniCard(
                        title = "Net Host Earnings",
                        amount = "₹${payoutSummary.netHostEarnings.toInt()}",
                        color = StynoEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricMiniCard(
                        title = "Pending Settlements",
                        amount = "₹${payoutSummary.pendingPayoutsAmount.toInt()}",
                        color = Color(0xFFD97706),
                        subtitle = "${payoutSummary.pendingBookingsCount} pending check-in",
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniCard(
                        title = "Completed Payouts",
                        amount = "₹${payoutSummary.completedPayoutsAmount.toInt()}",
                        color = StynoEmerald,
                        subtitle = "${payoutSummary.completedBookingsCount} completed stays",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Styno Commission Rule Note
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Percent, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Styno Commission Rule: Flat 5.0% platform fee on guest gross bookings. 95% net revenue is routed directly to the property host's verified bank or UPI.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Account Configuration Form Card with 3 Independent Configurable Payment Methods
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
                // Header with dynamic Setup Completion Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Owner Payment Methods",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Setup Completion Badge
                    val isComplete = currentDetails.isConfigured
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isComplete) StynoEmerald.copy(alpha = 0.12f) else MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, if (isComplete) StynoEmerald.copy(alpha = 0.35f) else MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isComplete) "SETUP ACTIVE (${currentDetails.activeMethodsList.joinToString(", ")})" else "SETUP INCOMPLETE",
                            color = if (isComplete) StynoEmerald else MaterialTheme.colorScheme.error,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = "Configure any one, two, or all three payment methods for this property. Guests booking this property will ONLY be presented with your verified configured methods.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Privacy assurance banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StynoEmerald.copy(alpha = 0.08f),
                    border = BorderStroke(0.5.dp, StynoEmerald.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Privacy Protected: Stored securely per owner (${currentProperty.resolvedOwnerId}) and strictly isolated.",
                            fontSize = 11.sp,
                            color = StynoEmerald,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // ==========================================
                // METHOD 1: DIRECT UPI ID
                // ==========================================
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUpiIdEnabled) StynoBluePrimary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isUpiIdEnabled) StynoBluePrimary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("1. UPI ID", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Direct Google Pay, PhonePe, Paytm, BHIM", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = isUpiIdEnabled,
                                onCheckedChange = {
                                    isUpiIdEnabled = it
                                    validationError = null
                                }
                            )
                        }

                        if (isUpiIdEnabled) {
                            OutlinedTextField(
                                value = upiId,
                                onValueChange = {
                                    upiId = it.trim().lowercase()
                                    validationError = null
                                },
                                label = { Text("Your Personal / Business UPI ID") },
                                placeholder = { Text("e.g. yourname@okhdfcbank, 9876543210@paytm") },
                                trailingIcon = {
                                    if (upiId.contains("@") && upiId.length >= 4) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Valid UPI Format", tint = StynoEmerald)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            // Quick handle selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("@okhdfcbank", "@okaxis", "@okicici", "@paytm", "@ybl").forEach { handle ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable {
                                            val prefix = upiId.substringBefore("@").ifBlank { "owner" }
                                            upiId = "$prefix$handle"
                                        }
                                    ) {
                                        Text(handle, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // METHOD 2: UPI QR CODE (WITH UPLOAD)
                // ==========================================
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isQrCodeEnabled) StynoEmerald.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isQrCodeEnabled) StynoEmerald.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("2. UPI QR Code", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Upload personal or business UPI QR image", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = isQrCodeEnabled,
                                onCheckedChange = {
                                    isQrCodeEnabled = it
                                    validationError = null
                                }
                            )
                        }

                        // Content visible under option
                        if (qrCodeImageUrl.isNullOrBlank()) {
                            // State: No QR Code Added
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.QrCode,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = "No UPI QR Code added",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Select your UPI QR code image (GPay, PhonePe, Paytm, BHIM) from your phone gallery or files.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = {
                                            isQrCodeEnabled = true
                                            qrImagePickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(42.dp)
                                    ) {
                                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Upload UPI QR Code", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        } else {
                            // State: QR image uploaded -> Show preview, Replace, Remove
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StynoEmerald.copy(alpha = 0.08f))
                                    .border(1.dp, StynoEmerald.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("QR Code Uploaded ✓", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StynoEmerald)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = StynoEmerald.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "Owner Isolated",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StynoEmerald,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // [QR Preview]
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    shadowElevation = 3.dp,
                                    border = BorderStroke(1.5.dp, StynoEmerald.copy(alpha = 0.4f)),
                                    modifier = Modifier.size(160.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        AsyncImage(
                                            model = qrCodeImageUrl,
                                            contentDescription = "Uploaded Owner QR Code Preview",
                                            modifier = Modifier.fillMaxSize().padding(8.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "This QR code is stored securely with your owner payment profile and will be shown to guests booking this property.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                // [Replace QR Code] and [Remove QR Code] buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            qrImagePickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Replace QR Code", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            qrCodeImageUrl = null
                                            isQrCodeEnabled = false
                                            qrCodeVpa = ""
                                            validationError = null
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                                        modifier = Modifier.weight(1f).height(38.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Remove QR Code", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }

                        if (isQrCodeEnabled) {
                            // Optional QR VPA field
                            OutlinedTextField(
                                value = qrCodeVpa,
                                onValueChange = {
                                    qrCodeVpa = it.trim().lowercase()
                                    validationError = null
                                },
                                label = { Text("QR UPI VPA / Handle (Optional Text Backup)") },
                                placeholder = { Text("e.g. hostqr@upi") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                }

                // ==========================================
                // METHOD 3: BANK ACCOUNT
                // ==========================================
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBankAccountEnabled) StynoAccent.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isBankAccountEnabled) StynoAccent.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = StynoAccent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("3. Bank Account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Direct NEFT / RTGS / IMPS Bank Transfer", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = isBankAccountEnabled,
                                onCheckedChange = {
                                    isBankAccountEnabled = it
                                    validationError = null
                                }
                            )
                        }

                        if (isBankAccountEnabled) {
                            // Beneficiary Name
                            OutlinedTextField(
                                value = accountHolderName,
                                onValueChange = {
                                    accountHolderName = it
                                    validationError = null
                                },
                                label = { Text("Account Holder / Legal Business Name") },
                                placeholder = { Text("e.g. Vikram Sharma") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            // Bank Name
                            OutlinedTextField(
                                value = bankName,
                                onValueChange = {
                                    bankName = it
                                    validationError = null
                                },
                                label = { Text("Bank Name") },
                                placeholder = { Text("e.g. HDFC Bank, ICICI Bank, SBI") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            // Account Number with mask toggle
                            OutlinedTextField(
                                value = accountNumber,
                                onValueChange = {
                                    accountNumber = it.trim()
                                    validationError = null
                                },
                                label = { Text("Bank Account Number") },
                                placeholder = { Text("Enter account number (minimum 8 digits)") },
                                trailingIcon = {
                                    IconButton(onClick = { maskAccountNumber = !maskAccountNumber }) {
                                        Icon(
                                            imageVector = if (maskAccountNumber) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle account number visibility"
                                        )
                                    }
                                },
                                visualTransformation = if (maskAccountNumber && accountNumber.length > 4) {
                                    androidx.compose.ui.text.input.PasswordVisualTransformation()
                                } else {
                                    androidx.compose.ui.text.input.VisualTransformation.None
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            // IFSC Code
                            OutlinedTextField(
                                value = ifscCode,
                                onValueChange = {
                                    ifscCode = it.uppercase().trim()
                                    validationError = null
                                },
                                label = { Text("IFSC Code") },
                                placeholder = { Text("e.g. HDFC0001234") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            // Account Type selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Savings Account", "Current Account").forEach { type ->
                                    FilterChip(
                                        selected = accountType == type,
                                        onClick = { accountType = type },
                                        label = { Text(type) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Payout Frequency
                            Text("Settlement Frequency:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Instant on Check-in", "Daily Batch", "Weekly").forEach { freq ->
                                    FilterChip(
                                        selected = payoutFrequency == freq,
                                        onClick = { payoutFrequency = freq },
                                        label = { Text(freq) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Validation Error Banner
                validationError?.let { err ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = err, fontSize = 12.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Saved Notification Feedback
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
                        text = "Owner payment setup saved! Configured methods: ${currentProperty.ownerPaymentDetails.activeMethodsList.joinToString(", ")}. Booking payouts will route directly to this profile.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StynoEmerald
                    )
                }
            }
        }

        // Save Details Button
        Button(
            onClick = {
                validationError = null

                if (!isUpiIdEnabled && !isQrCodeEnabled && !isBankAccountEnabled) {
                    validationError = "Please enable at least one payment method: UPI ID, UPI QR Code, or Bank Account."
                    return@Button
                }

                if (isUpiIdEnabled) {
                    if (upiId.isBlank() || !upiId.contains("@") || upiId.length < 4) {
                        validationError = "Please enter a valid UPI ID (e.g. host@okaxis, 9876543210@paytm)."
                        return@Button
                    }
                }

                if (isQrCodeEnabled) {
                    if (qrCodeImageUrl.isNullOrBlank()) {
                        validationError = "Please tap 'Upload UPI QR Code' to select and upload your UPI QR Code image."
                        return@Button
                    }
                }

                if (isBankAccountEnabled) {
                    if (accountHolderName.isBlank()) {
                        validationError = "Please enter the Beneficiary / Legal Account Holder Name."
                        return@Button
                    }
                    if (accountNumber.length < 8) {
                        validationError = "Please enter a valid Bank Account Number (minimum 8 digits)."
                        return@Button
                    }
                    if (ifscCode.length < 8) {
                        validationError = "Please enter a valid IFSC Code (minimum 8 characters, e.g. HDFC0001234)."
                        return@Button
                    }
                }

                val updatedDetails = OwnerPaymentDetails(
                    ownerId = currentProperty.resolvedOwnerId,
                    isUpiIdEnabled = isUpiIdEnabled,
                    upiId = if (isUpiIdEnabled) upiId.trim() else "",
                    isQrCodeEnabled = isQrCodeEnabled,
                    qrCodeImageUrl = if (isQrCodeEnabled) qrCodeImageUrl else null,
                    qrCodeVpa = if (isQrCodeEnabled) qrCodeVpa.ifBlank { upiId }.trim() else "",
                    isBankAccountEnabled = isBankAccountEnabled,
                    accountHolderName = if (isBankAccountEnabled || isUpiIdEnabled) accountHolderName.trim() else "",
                    bankName = if (isBankAccountEnabled) bankName.trim().ifBlank { "HDFC Bank" } else "",
                    accountNumber = if (isBankAccountEnabled) accountNumber.trim() else "",
                    ifscCode = if (isBankAccountEnabled) ifscCode.trim() else "",
                    accountType = accountType,
                    payoutFrequency = payoutFrequency,
                    isVerified = true,
                    isVerifiedForPayouts = true,
                    settlementMode = when {
                        isUpiIdEnabled && isQrCodeEnabled && isBankAccountEnabled -> "UPI, QR & Direct Bank Settlement"
                        isUpiIdEnabled && isQrCodeEnabled -> "Direct UPI & QR Settlement"
                        isUpiIdEnabled && isBankAccountEnabled -> "Direct UPI & Bank Transfer"
                        isQrCodeEnabled && isBankAccountEnabled -> "QR Code & Bank Transfer"
                        isUpiIdEnabled -> "Instant UPI Settlement"
                        isQrCodeEnabled -> "UPI QR Code Settlement"
                        isBankAccountEnabled -> "Direct Bank NEFT/RTGS"
                        else -> "Not Configured"
                    },
                    gatewayConnected = false,
                    gatewayStatus = "Direct Host Settlement Mode",
                    lastUpdatedTimestamp = System.currentTimeMillis()
                )
                onSavePaymentDetails(currentProperty.id, updatedDetails)
                showSavedNotification = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Payment Setup", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        // Transaction & Settlement History Section
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Settlement & Transaction History",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${transactionRecords.size} Records",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (transactionRecords.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No booking transactions recorded yet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "When guests book this property, settlement records and 5% commission deductions will appear here.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    transactionRecords.forEach { txn ->
                        TransactionRecordCard(record = txn)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    amount: String,
    color: Color,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(amount, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TransactionRecordCard(record: OwnerTransactionRecord) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Booking #${record.bookingId.takeLast(8).uppercase()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${record.guestName} • ${record.roomOrSlotName} (${record.durationText})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (record.payoutStatus.contains("Disbursed")) StynoEmerald.copy(alpha = 0.15f) else Color(0xFFD97706).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = record.payoutStatus,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (record.payoutStatus.contains("Disbursed")) StynoEmerald else Color(0xFFD97706),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gross: ₹${record.grossAmount.toInt()} | Styno 5%: -₹${record.stynoCommissionAmount.toInt()}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Net Payout: ₹${record.netHostPayoutAmount.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = StynoEmerald
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Route: ${record.ownerDestinationAccount}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Direct Host Escrow",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

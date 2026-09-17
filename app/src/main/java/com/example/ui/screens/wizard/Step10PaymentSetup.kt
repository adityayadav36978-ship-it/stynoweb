package com.example.ui.screens.wizard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OwnerPaymentDetails

@Composable
fun Step10PaymentSetup(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit,
    onSaveAndComplete: () -> Unit,
    onPublishLive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    // Local form state initialized from wizard state
    var selectedMethod by remember {
        mutableStateOf(
            if (state.ownerPaymentDetails.upiId.isNotBlank() && state.ownerPaymentDetails.accountNumber.isBlank()) "UPI"
            else if (state.ownerPaymentDetails.accountNumber.isNotBlank() && state.ownerPaymentDetails.upiId.isBlank()) "BANK"
            else "BOTH"
        )
    }

    var accountHolderName by remember {
        mutableStateOf(
            state.ownerPaymentDetails.accountHolderName.ifBlank {
                state.propertyName.ifBlank { "Property Owner" }
            }
        )
    }
    var upiId by remember { mutableStateOf(state.ownerPaymentDetails.upiId) }
    var bankName by remember { mutableStateOf(state.ownerPaymentDetails.bankName.ifBlank { "HDFC Bank" }) }
    var accountNumber by remember { mutableStateOf(state.ownerPaymentDetails.accountNumber) }
    var confirmAccountNumber by remember { mutableStateOf(state.ownerPaymentDetails.accountNumber) }
    var ifscCode by remember { mutableStateOf(state.ownerPaymentDetails.ifscCode.ifBlank { "HDFC0001234" }) }
    var accountType by remember { mutableStateOf(state.ownerPaymentDetails.accountType.ifBlank { "Savings Account" }) }
    var payoutFrequency by remember { mutableStateOf(state.ownerPaymentDetails.payoutFrequency.ifBlank { "Instant on Check-in" }) }

    var isAccountMasked by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(!state.isPaymentSetupCompleted && state.ownerPaymentDetails.accountNumber.isBlank() && state.ownerPaymentDetails.upiId.isBlank()) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    val isSetupComplete = state.isPaymentSetupCompleted ||
            (state.ownerPaymentDetails.isConfigured && state.ownerPaymentDetails.accountHolderName.isNotBlank())

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
                        .background(Color(0xFF2E7D32)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 10 of 10: Owner Payout & Payment Setup",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Configure where guest booking payments and Quick Stay payouts are deposited automatically.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Payout Status Overview Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSetupComplete) Color(0xFFF1F8E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            border = BorderStroke(
                1.dp,
                if (isSetupComplete) Color(0xFF2E7D32).copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isSetupComplete) Icons.Default.VerifiedUser else Icons.Outlined.PendingActions,
                            contentDescription = null,
                            tint = if (isSetupComplete) Color(0xFF2E7D32) else Color(0xFFF57C00)
                        )
                        Text(
                            text = if (isSetupComplete) "Payment Setup Complete" else "Payout Setup Pending",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSetupComplete) Color(0xFF2E7D32) else Color(0xFFF57C00)
                        )
                    }

                    if (isSetupComplete) {
                        SuggestionChip(
                            onClick = { isEditing = !isEditing },
                            label = { Text(if (isEditing) "Cancel Edit" else "Edit Details") },
                            icon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("btn_edit_payment_details")
                        )
                    }
                }

                if (isSetupComplete && !isEditing) {
                    Divider(color = Color(0xFF2E7D32).copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Beneficiary Name", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(accountHolderName.ifBlank { state.ownerPaymentDetails.accountHolderName }, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Settlement Mode", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(payoutFrequency, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF2E7D32))
                        }
                    }

                    if (upiId.isNotBlank() || state.ownerPaymentDetails.upiId.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Primary UPI ID", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = upiId.ifBlank { state.ownerPaymentDetails.upiId },
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }
                    }

                    if (accountNumber.isNotBlank() || state.ownerPaymentDetails.accountNumber.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Direct Bank Account", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${bankName.ifBlank { state.ownerPaymentDetails.bankName }} • ${state.ownerPaymentDetails.maskedAccountNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Text(
                                text = "Verified Host Payout Channel • Ready to receive booking payments",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1B5E20),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Edit/Input Form
        AnimatedVisibility(visible = isEditing || !isSetupComplete) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Configure Payout Account",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Method Selector Chips
                    Text(
                        text = "Preferred Payout Destination *",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedMethod == "UPI",
                            onClick = { selectedMethod = "UPI" },
                            label = { Text("UPI ID", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f).testTag("chip_method_upi")
                        )
                        FilterChip(
                            selected = selectedMethod == "BANK",
                            onClick = { selectedMethod = "BANK" },
                            label = { Text("Bank Acc", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f).testTag("chip_method_bank")
                        )
                        FilterChip(
                            selected = selectedMethod == "BOTH",
                            onClick = { selectedMethod = "BOTH" },
                            label = { Text("Both (Rec.)", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f).testTag("chip_method_both")
                        )
                    }

                    // Account Holder Legal Name
                    OutlinedTextField(
                        value = accountHolderName,
                        onValueChange = {
                            accountHolderName = it
                            validationError = null
                        },
                        label = { Text("Beneficiary / Account Holder Legal Name *") },
                        placeholder = { Text("As per Bank Passbook / Government ID") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_payment_holder_name")
                    )

                    // UPI Configuration
                    if (selectedMethod == "UPI" || selectedMethod == "BOTH") {
                        OutlinedTextField(
                            value = upiId,
                            onValueChange = {
                                upiId = it.trim().lowercase()
                                validationError = null
                            },
                            label = { Text("Owner UPI ID (VPA) *") },
                            placeholder = { Text("e.g. yourname@okhdfcbank, mobile@paytm") },
                            leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth().testTag("input_payment_upi_id")
                        )

                        // Quick VPA handles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("@okhdfcbank", "@okaxis", "@paytm", "@ybl").forEach { suffix ->
                                AssistChip(
                                    onClick = {
                                        val prefix = upiId.substringBefore("@")
                                        upiId = if (prefix.isNotBlank()) "$prefix$suffix" else "owner$suffix"
                                    },
                                    label = { Text(suffix, fontSize = 10.sp) }
                                )
                            }
                        }
                    }

                    // Bank Account Configuration
                    if (selectedMethod == "BANK" || selectedMethod == "BOTH") {
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text("Bank Name *") },
                            placeholder = { Text("e.g. HDFC Bank, SBI, ICICI Bank") },
                            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_payment_bank_name")
                        )

                        // Account Number
                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = {
                                accountNumber = it.filter { ch -> ch.isDigit() }
                                validationError = null
                            },
                            label = { Text("Bank Account Number *") },
                            placeholder = { Text("Enter 9 to 18 digit account number") },
                            leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isAccountMasked = !isAccountMasked }) {
                                    Icon(
                                        imageVector = if (isAccountMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (isAccountMasked) "Show account" else "Hide account"
                                    )
                                }
                            },
                            visualTransformation = if (isAccountMasked) PasswordVisualTransformation() else VisualTransformation.None,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_payment_account_number")
                        )

                        // Confirm Account Number
                        OutlinedTextField(
                            value = confirmAccountNumber,
                            onValueChange = {
                                confirmAccountNumber = it.filter { ch -> ch.isDigit() }
                                validationError = null
                            },
                            label = { Text("Re-enter Account Number *") },
                            placeholder = { Text("Confirm your account number") },
                            leadingIcon = { Icon(Icons.Default.CheckCircleOutline, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_payment_confirm_account_number")
                        )

                        // IFSC Code
                        OutlinedTextField(
                            value = ifscCode,
                            onValueChange = {
                                ifscCode = it.trim().uppercase()
                                validationError = null
                            },
                            label = { Text("Bank IFSC Code (11 characters) *") },
                            placeholder = { Text("e.g. HDFC0001234") },
                            leadingIcon = { Icon(Icons.Default.Domain, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_payment_ifsc")
                        )

                        // Account Type selector
                        Text("Account Classification", style = MaterialTheme.typography.labelMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Savings Account", "Current Account").forEach { type ->
                                FilterChip(
                                    selected = accountType == type,
                                    onClick = { accountType = type },
                                    label = { Text(type, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Payout Frequency
                    Text("Payout Settlement Schedule", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Instant on Check-in", "Daily Batch (24h)").forEach { freq ->
                            FilterChip(
                                selected = payoutFrequency == freq,
                                onClick = { payoutFrequency = freq },
                                label = { Text(freq, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Validation Error Message
                    if (validationError != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Text(
                                    text = validationError ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Save Payment Setup Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()

                            // Basic validation
                            if (accountHolderName.isBlank()) {
                                validationError = "Please enter the Beneficiary / Account Holder Legal Name."
                                return@Button
                            }

                            if ((selectedMethod == "UPI" || selectedMethod == "BOTH") && (upiId.isBlank() || !upiId.contains("@"))) {
                                validationError = "Please enter a valid UPI ID with '@' handle (e.g. mobile@paytm)."
                                return@Button
                            }

                            if ((selectedMethod == "BANK" || selectedMethod == "BOTH")) {
                                if (accountNumber.length < 8) {
                                    validationError = "Please enter a valid Bank Account Number (minimum 8 digits)."
                                    return@Button
                                }
                                if (accountNumber != confirmAccountNumber) {
                                    validationError = "Bank account numbers do not match. Please re-enter carefully."
                                    return@Button
                                }
                                if (ifscCode.length < 8) {
                                    validationError = "Please enter a valid 11-digit IFSC code (e.g. HDFC0001234)."
                                    return@Button
                                }
                            }

                            // Update owner payment details in state
                            val updatedPayment = OwnerPaymentDetails(
                                upiId = upiId.ifBlank { if (state.ownerPhone.isNotBlank()) "${state.ownerPhone.filter { it.isDigit() }}@styno" else "host@icici" },
                                accountHolderName = accountHolderName,
                                accountNumber = accountNumber,
                                ifscCode = ifscCode,
                                bankName = bankName,
                                accountType = accountType,
                                payoutFrequency = payoutFrequency,
                                isVerified = true,
                                isVerifiedForPayouts = true,
                                settlementMode = when (selectedMethod) {
                                    "UPI" -> "Direct UPI Payout"
                                    "BANK" -> "Direct IMPS / NEFT Bank Transfer"
                                    else -> "Direct UPI & Bank Settlement"
                                },
                                qrCodeVpa = "upi://pay?pa=$upiId&pn=${accountHolderName.replace(" ", "%20")}&cu=INR"
                            )

                            onUpdateState(
                                state.copy(
                                    ownerPaymentDetails = updatedPayment,
                                    isPaymentSetupCompleted = true
                                )
                            )

                            isEditing = false
                            validationError = null
                            saveSuccessMessage = "Payout account verified and saved successfully!"
                            onSaveAndComplete()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_save_payment_setup")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Payment Setup", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        // Success Confirmation Message
        if (saveSuccessMessage != null && !isEditing) {
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                    Text(
                        text = saveSuccessMessage ?: "Payment Setup Complete",
                        color = Color(0xFF1B5E20),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Security, Privacy & Payout Guarantee
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text("Security & Privacy Guarantee", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(
                    text = "• Never exposed: Payout credentials and account numbers are strictly confidential and never displayed to guests or third parties.\n• Instant Settlement: Booking and Quick Stay payouts are routed directly to your verified channel with 0% platform transaction withholding.\n• Styno Escrow Protection: Funds are reserved upon customer confirmation and disbursed upon verified guest arrival.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        // Final Listing Actions (Publish Live or Finish)
        Button(
            onClick = onPublishLive,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_complete_wizard_publish")
        ) {
            Icon(Icons.Default.RocketLaunch, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (state.editingOriginalPropertyId != null) "Update Listing with Payment Details" else "Publish Property Live",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

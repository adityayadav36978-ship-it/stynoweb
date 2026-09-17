package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.payment.CardBrand
import com.example.data.payment.StripeCardInput
import com.example.data.payment.StripePaymentManager
import com.example.data.payment.StripePaymentResult
import com.example.data.payment.StripePaymentStage
import com.example.data.payment.StripePaymentType
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.theme.StynoGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StripePaymentSheetDialog(
    amountToPay: Double,
    propertyName: String,
    roomTypeName: String,
    guestName: String,
    guestEmail: String,
    guestPhone: String,
    initialPaymentType: StripePaymentType = StripePaymentType.CARD,
    onDismiss: () -> Unit,
    onPaymentSuccess: (StripePaymentResult) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedTab by remember { mutableStateOf(initialPaymentType) }

    // Card Input State
    var cardInput by remember {
        mutableStateOf(
            StripeCardInput(
                cardNumber = "4242 4242 4242 4242",
                cardholderName = guestName.ifBlank { "Aditya Yadav" },
                expiryMonthYear = "12/28",
                cvc = "123",
                saveCard = true
            )
        )
    }
    var showCvc by remember { mutableStateOf(false) }

    // UPI State
    var selectedUpiApp by remember { mutableStateOf("Google Pay") }
    var customUpiId by remember { mutableStateOf("aditya@okhdfcbank") }

    // Net Banking State
    var selectedBank by remember { mutableStateOf("HDFC Bank") }

    // Common Payment Flow State
    var paymentStage by remember { mutableStateOf(StripePaymentStage.IDLE) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var show3dsDialog by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("123456") }

    val isProcessing = paymentStage == StripePaymentStage.CREATING_INTENT ||
            paymentStage == StripePaymentStage.TOKENIZING_CARD ||
            paymentStage == StripePaymentStage.CONFIRMING_WITH_STRIPE ||
            paymentStage == StripePaymentStage.PROCESSING_UPI_REQUEST ||
            paymentStage == StripePaymentStage.WAITING_UPI_APPROVAL ||
            paymentStage == StripePaymentStage.VERIFYING_QR_PAYMENT

    ModalBottomSheet(
        onDismissRequest = {
            if (!isProcessing) {
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        modifier = Modifier.testTag("stripe_payment_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StynoBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "stripe",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Stripe Payment Sheet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = StynoEmerald,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "256-bit Encrypted • PCI-DSS Level 1",
                                style = MaterialTheme.typography.labelSmall,
                                color = StynoEmerald,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    enabled = !isProcessing
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Booking & Price Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = propertyName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            text = roomTypeName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Payable Amount",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${amountToPay.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Mode Segmented Tab Selector
            Text(
                text = "PAYMENT METHOD",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StripePaymentType.values().forEach { type ->
                    val isTabSelected = selectedTab == type
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isTabSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shadowElevation = if (isTabSelected) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = !isProcessing) {
                                selectedTab = type
                                errorMessage = null
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = when (type) {
                                    StripePaymentType.CARD -> Icons.Default.CreditCard
                                    StripePaymentType.UPI -> Icons.Default.PhoneAndroid
                                    StripePaymentType.QR -> Icons.Default.QrCode
                                    StripePaymentType.NET_BANKING -> Icons.Default.AccountBalance
                                },
                                contentDescription = type.label,
                                tint = if (isTabSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = type.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isTabSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TAB 1: STRIPE CARD PAYMENT
            if (selectedTab == StripePaymentType.CARD) {
                VisualCardPreview(cardInput = cardInput)

                Spacer(modifier = Modifier.height(14.dp))

                // Fast Demo Test Cards Carousel
                Text(
                    text = "⚡ Quick Fill Test Cards:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StripePaymentManager.TEST_CARDS.forEach { testCard ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (cardInput.cleanCardNumber == testCard.number.replace(" ", "")) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (cardInput.cleanCardNumber == testCard.number.replace(" ", "")) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                }
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(enabled = !isProcessing) {
                                    cardInput = cardInput.copy(
                                        cardNumber = testCard.number,
                                        expiryMonthYear = testCard.expiry,
                                        cvc = testCard.cvc,
                                        cardholderName = testCard.name
                                    )
                                    errorMessage = null
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = testCard.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (testCard.willDecline) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurface
                                )
                                if (testCard.requires3ds) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = StynoGold.copy(alpha = 0.2f)
                                    ) {
                                        Text("3DS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StynoGold, modifier = Modifier.padding(horizontal = 3.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Number Input
                OutlinedTextField(
                    value = cardInput.cardNumber,
                    onValueChange = { input ->
                        val clean = input.filter { it.isDigit() }.take(16)
                        val formatted = clean.chunked(4).joinToString(" ")
                        cardInput = cardInput.copy(cardNumber = formatted)
                        errorMessage = null
                    },
                    label = { Text("Card Number") },
                    placeholder = { Text("4242 4242 4242 4242") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        Text(
                            text = cardInput.brand.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stripe_card_number_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Expiry & CVC Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = cardInput.expiryMonthYear,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }.take(4)
                            val formatted = if (digits.length >= 3) {
                                "${digits.take(2)}/${digits.substring(2)}"
                            } else {
                                digits
                            }
                            cardInput = cardInput.copy(expiryMonthYear = formatted)
                            errorMessage = null
                        },
                        label = { Text("Expires (MM/YY)") },
                        placeholder = { Text("12/28") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stripe_expiry_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = cardInput.cvc,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }.take(4)
                            cardInput = cardInput.copy(cvc = clean)
                            errorMessage = null
                        },
                        label = { Text("CVC / CVV") },
                        placeholder = { Text("123") },
                        visualTransformation = if (showCvc) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showCvc = !showCvc }) {
                                Icon(
                                    imageVector = if (showCvc) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle CVC Visibility"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stripe_cvc_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cardholder Name
                OutlinedTextField(
                    value = cardInput.cardholderName,
                    onValueChange = {
                        cardInput = cardInput.copy(cardholderName = it)
                        errorMessage = null
                    },
                    label = { Text("Cardholder Name") },
                    placeholder = { Text("Aditya Yadav") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stripe_cardholder_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Save Card Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = cardInput.saveCard,
                        onCheckedChange = { cardInput = cardInput.copy(saveCard = it) },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Save card securely via Stripe Vault for 1-tap future checkouts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // TAB 2: STRIPE UPI (Google Pay, PhonePe, Paytm, BHIM, Custom VPA ID)
            if (selectedTab == StripePaymentType.UPI) {
                Text(
                    text = "Select UPI App for Instant Intent",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                val upiApps = listOf(
                    Triple("Google Pay", "Instant 1-Tap UPI", Color(0xFF4285F4)),
                    Triple("PhonePe", "Direct Bank Account", Color(0xFF6739B7)),
                    Triple("Paytm UPI", "Paytm Payments Bank", Color(0xFF00B9F5)),
                    Triple("BHIM UPI", "National NPCI Gateway", Color(0xFF00897B))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    upiApps.forEach { (appName, appDesc, appColor) ->
                        val isAppSelected = selectedUpiApp == appName
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isAppSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isAppSelected) 1.5.dp else 1.dp,
                                color = if (isAppSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = !isProcessing) {
                                    selectedUpiApp = appName
                                    customUpiId = "${guestName.lowercase().replace(" ", "")}@${appName.lowercase().take(4)}"
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(appColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = appName,
                                        tint = appColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = appName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Or Enter Any Verified UPI ID / VPA:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = customUpiId,
                    onValueChange = {
                        customUpiId = it
                        errorMessage = null
                    },
                    label = { Text("Virtual Payment Address (VPA)") },
                    placeholder = { Text("yourname@okaxis") },
                    leadingIcon = {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = StynoAccent)
                    },
                    trailingIcon = {
                        if (customUpiId.contains("@") && customUpiId.length > 5) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Valid UPI", tint = StynoEmerald)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stripe_upi_id_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("@okhdfcbank", "@okaxis", "@okicici", "@paytm", "@ybl", "@ibl").forEach { handle ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    val prefix = customUpiId.substringBefore("@").ifBlank { "aditya" }
                                    customUpiId = "$prefix$handle"
                                }
                        ) {
                            Text(
                                text = handle,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // TAB 3: DYNAMIC BHARAT / STRIPE UPI QR CODE
            if (selectedTab == StripePaymentType.QR) {
                StripeDynamicQrContent(
                    amountToPay = amountToPay,
                    guestName = guestName,
                    isProcessing = isProcessing,
                    onSimulatePaid = {
                        coroutineScope.launch {
                            errorMessage = null
                            val result = StripePaymentManager.processStripeQrPayment(
                                context = context,
                                amountInr = amountToPay,
                                bookingDescription = "Booking for $propertyName ($roomTypeName)",
                                customerEmail = guestEmail,
                                customerName = guestName,
                                onStageChanged = { stage -> paymentStage = stage }
                            )

                            if (result.isSuccess) {
                                onPaymentSuccess(result)
                            } else {
                                errorMessage = result.errorMessage ?: "QR Payment failed."
                            }
                        }
                    }
                )
            }

            // TAB 4: NET BANKING (MAJOR INDIAN BANKS)
            if (selectedTab == StripePaymentType.NET_BANKING) {
                Text(
                    text = "Select Your Bank for Direct Gateway Routing",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                val banks = listOf("HDFC Bank", "ICICI Bank", "State Bank of India", "Axis Bank", "Kotak Mahindra", "Punjab National Bank")

                banks.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { bank ->
                            val isSelectedBank = selectedBank == bank
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelectedBank) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelectedBank) 1.5.dp else 1.dp,
                                    color = if (isSelectedBank) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable(enabled = !isProcessing) {
                                        selectedBank = bank
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = if (isSelectedBank) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = bank,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelectedBank) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Error Banner
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = Color(0xFF991B1B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Payment Action Button
            if (!isProcessing) {
                Button(
                    onClick = {
                        when (selectedTab) {
                            StripePaymentType.CARD -> {
                                if (cardInput.cleanCardNumber.length < 15) {
                                    errorMessage = "Please enter a valid 16-digit card number."
                                    return@Button
                                }
                                if (cardInput.expiryMonthYear.length < 4) {
                                    errorMessage = "Please enter valid card expiry (MM/YY)."
                                    return@Button
                                }
                                if (cardInput.cvc.length < 3) {
                                    errorMessage = "Please enter a valid 3 or 4-digit CVC code."
                                    return@Button
                                }

                                val cleanNum = cardInput.cleanCardNumber
                                if (cleanNum.startsWith("400000276000") || cleanNum.endsWith("3184")) {
                                    show3dsDialog = true
                                    return@Button
                                }

                                coroutineScope.launch {
                                    errorMessage = null
                                    val result = StripePaymentManager.processStripePayment(
                                        context = context,
                                        cardInput = cardInput,
                                        amountInr = amountToPay,
                                        bookingDescription = "Booking for $propertyName ($roomTypeName)",
                                        customerEmail = guestEmail,
                                        customerName = guestName,
                                        onStageChanged = { stage -> paymentStage = stage }
                                    )

                                    if (result.isSuccess) {
                                        onPaymentSuccess(result)
                                    } else {
                                        errorMessage = result.errorMessage ?: "Payment could not be completed."
                                    }
                                }
                            }
                            StripePaymentType.UPI -> {
                                if (!customUpiId.contains("@") || customUpiId.length < 4) {
                                    errorMessage = "Please enter a valid UPI ID (e.g. name@bank)."
                                    return@Button
                                }

                                coroutineScope.launch {
                                    errorMessage = null
                                    val result = StripePaymentManager.processStripeUpiPayment(
                                        context = context,
                                        upiIdOrApp = customUpiId,
                                        amountInr = amountToPay,
                                        bookingDescription = "Booking for $propertyName ($roomTypeName)",
                                        customerEmail = guestEmail,
                                        customerName = guestName,
                                        onStageChanged = { stage -> paymentStage = stage }
                                    )

                                    if (result.isSuccess) {
                                        onPaymentSuccess(result)
                                    } else {
                                        errorMessage = result.errorMessage ?: "UPI Payment failed."
                                    }
                                }
                            }
                            StripePaymentType.QR -> {
                                coroutineScope.launch {
                                    errorMessage = null
                                    val result = StripePaymentManager.processStripeQrPayment(
                                        context = context,
                                        amountInr = amountToPay,
                                        bookingDescription = "Booking for $propertyName ($roomTypeName)",
                                        customerEmail = guestEmail,
                                        customerName = guestName,
                                        onStageChanged = { stage -> paymentStage = stage }
                                    )

                                    if (result.isSuccess) {
                                        onPaymentSuccess(result)
                                    } else {
                                        errorMessage = result.errorMessage ?: "QR Payment failed."
                                    }
                                }
                            }
                            StripePaymentType.NET_BANKING -> {
                                coroutineScope.launch {
                                    errorMessage = null
                                    val result = StripePaymentManager.processStripeNetBankingPayment(
                                        context = context,
                                        bankName = selectedBank,
                                        amountInr = amountToPay,
                                        bookingDescription = "Booking for $propertyName ($roomTypeName)",
                                        customerEmail = guestEmail,
                                        customerName = guestName,
                                        onStageChanged = { stage -> paymentStage = stage }
                                    )

                                    if (result.isSuccess) {
                                        onPaymentSuccess(result)
                                    } else {
                                        errorMessage = result.errorMessage ?: "Net Banking failed."
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("stripe_pay_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (selectedTab) {
                            StripePaymentType.CARD -> "Pay ₹${amountToPay.toInt()} with Card (Stripe SDK)"
                            StripePaymentType.UPI -> "Pay ₹${amountToPay.toInt()} via UPI"
                            StripePaymentType.QR -> "Verify & Confirm QR Payment (₹${amountToPay.toInt()})"
                            StripePaymentType.NET_BANKING -> "Proceed with $selectedBank (₹${amountToPay.toInt()})"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            } else {
                // Processing Animation Box
                PaymentProcessingBox(stage = paymentStage)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer assurance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "End-to-End Encrypted via Stripe Payments API & RBI Guidelines",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // 3D Secure / OTP Simulation Dialog
    if (show3dsDialog) {
        BasicAlertDialog(
            onDismissRequest = { show3dsDialog = false }
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Issuer 3D Secure Verification",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StynoBluePrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Verified by Visa / RuPay",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StynoBluePrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter 6-Digit Bank One-Time Password",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "OTP sent to registered mobile number ending with •••• 4210 for ₹${amountToPay.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) otpInput = it },
                        placeholder = { Text("123456") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stripe_3ds_otp_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            show3dsDialog = false
                            coroutineScope.launch {
                                val result = StripePaymentManager.processStripePayment(
                                    context = context,
                                    cardInput = cardInput,
                                    amountInr = amountToPay,
                                    bookingDescription = "Booking for $propertyName ($roomTypeName)",
                                    customerEmail = guestEmail,
                                    customerName = guestName,
                                    onStageChanged = { stage -> paymentStage = stage }
                                )

                                if (result.isSuccess) {
                                    onPaymentSuccess(result)
                                } else {
                                    errorMessage = result.errorMessage ?: "Payment could not be completed."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Authenticate & Pay ₹${amountToPay.toInt()}")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            otpInput = "123456"
                            Toast.makeText(context, "New OTP Sent: 123456", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Resend Bank OTP")
                    }
                }
            }
        }
    }
}

@Composable
private fun StripeDynamicQrContent(
    amountToPay: Double,
    guestName: String,
    isProcessing: Boolean,
    onSimulatePaid: () -> Unit
) {
    val context = LocalContext.current
    var secondsRemaining by remember { mutableIntStateOf(299) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
    }

    val mins = secondsRemaining / 60
    val secs = secondsRemaining % 60
    val formattedTime = String.format("%02d:%02d", mins, secs)

    val vpaString = "upi://pay?pa=styno.stays@icici&pn=Styno%20Stays&am=${amountToPay.toInt()}&cu=INR&tn=HostelStayBooking"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dynamic Bharat QR Code",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (secondsRemaining > 60) StynoEmerald.copy(alpha = 0.15f) else Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = "Expires in $formattedTime",
                        color = if (secondsRemaining > 60) StynoEmerald else Color(0xFFDC2626),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Realistic High Resolution Vector QR Pattern
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(190.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(160.dp)) {
                        val cellSize = size.width / 21f
                        val darkColor = Color(0xFF0F172A)

                        // Draw Corner Position Detection Squares
                        fun drawFinderPattern(startX: Float, startY: Float) {
                            drawRect(
                                color = darkColor,
                                topLeft = Offset(startX, startY),
                                size = Size(cellSize * 7, cellSize * 7)
                            )
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(startX + cellSize, startY + cellSize),
                                size = Size(cellSize * 5, cellSize * 5)
                            )
                            drawRect(
                                color = darkColor,
                                topLeft = Offset(startX + cellSize * 2, startY + cellSize * 2),
                                size = Size(cellSize * 3, cellSize * 3)
                            )
                        }

                        drawFinderPattern(0f, 0f)
                        drawFinderPattern(cellSize * 14, 0f)
                        drawFinderPattern(0f, cellSize * 14)

                        // Draw QR data cells based on deterministic pattern
                        for (r in 0 until 21) {
                            for (c in 0 until 21) {
                                val inTopLeft = r < 7 && c < 7
                                val inTopRight = r < 7 && c >= 14
                                val inBottomLeft = r >= 14 && c < 7
                                val inCenterLogo = r in 9..11 && c in 9..11

                                if (!inTopLeft && !inTopRight && !inBottomLeft && !inCenterLogo) {
                                    val shouldFill = ((r * 7 + c * 13 + (amountToPay.toInt() % 7)) % 3 == 0) ||
                                            (r == 8 && c % 2 == 0) || (c == 8 && r % 2 == 0)
                                    if (shouldFill) {
                                        drawRect(
                                            color = darkColor,
                                            topLeft = Offset(c * cellSize, r * cellSize),
                                            size = Size(cellSize, cellSize)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Center STYNO badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StynoBluePrimary,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("S", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Scan with Any UPI App (GPay, PhonePe, Paytm, CRED)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", "styno.stays@icici"))
                        Toast.makeText(context, "UPI ID copied: styno.stays@icici", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UPI ID: styno.stays@icici",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy UPI ID", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onSimulatePaid,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simulate App Scan & Pay ₹${amountToPay.toInt()}", fontWeight = FontWeight.Bold, color = StynoEmerald)
            }
        }
    }
}

@Composable
private fun VisualCardPreview(cardInput: StripeCardInput) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E3A8A),
            Color(0xFF2563EB)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Chip & Brand
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp, 26.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFFEAB308))
                            .border(1.dp, Color(0xFFFDE047), RoundedCornerShape(5.dp))
                    )

                    Text(
                        text = cardInput.brand.displayName.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                // Middle: Card Number
                Text(
                    text = if (cardInput.cardNumber.isBlank()) "•••• •••• •••• ••••" else cardInput.cardNumber,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )

                // Bottom Row: Name & Expiry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "CARDHOLDER",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (cardInput.cardholderName.isBlank()) "ADITYA YADAV" else cardInput.cardholderName.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "EXPIRES",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (cardInput.expiryMonthYear.isBlank()) "MM/YY" else cardInput.expiryMonthYear,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentProcessingBox(stage: StripePaymentStage) {
    val stageTitle = when (stage) {
        StripePaymentStage.CREATING_INTENT -> "Creating Secure Stripe Payment Intent..."
        StripePaymentStage.TOKENIZING_CARD -> "Encrypting & Tokenizing Card via Stripe SDK..."
        StripePaymentStage.AUTHENTICATING_3DS -> "Performing 3D Secure Bank Authorization..."
        StripePaymentStage.PROCESSING_UPI_REQUEST -> "Sending Instant UPI Collect Request..."
        StripePaymentStage.WAITING_UPI_APPROVAL -> "Waiting for UPI App Approval..."
        StripePaymentStage.VERIFYING_QR_PAYMENT -> "Verifying Dynamic Bharat QR Code Scan..."
        StripePaymentStage.CONFIRMING_WITH_STRIPE -> "Confirming Payment with Stripe Gateway..."
        StripePaymentStage.SUCCESS -> "Payment Successfully Verified! 🎉"
        else -> "Processing Payment..."
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stageTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Do not press back or close the application.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

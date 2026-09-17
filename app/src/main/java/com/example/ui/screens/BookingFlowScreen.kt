package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DurationType
import com.example.data.payment.StripePaymentType
import com.example.ui.components.DateRangeSummaryCard
import com.example.ui.components.DynamicPriceBreakdownCard
import com.example.ui.components.DynamicPriceCalculation
import com.example.ui.components.PropertyDateRangePickerDialog
import com.example.ui.components.StayDate
import com.example.ui.components.StripePaymentSheetDialog
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.theme.StynoGold
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun BookingFlowScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val draft by viewModel.bookingDraft.collectAsStateWithLifecycle()
    val currentStep by viewModel.bookingStep.collectAsStateWithLifecycle()
    val showStripeSheet by viewModel.showStripePaymentSheet.collectAsStateWithLifecycle()
    val property = draft.property

    if (property == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No property selected for booking", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { viewModel.navigateBack() }) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    var guestDetailsError by remember { mutableStateOf<String?>(null) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    val room = draft.selectedRoom ?: property.roomOptions.firstOrNull()
    val basePrice = room?.price ?: property.startingPrice
    val serviceFee = if (property.durationType == DurationType.HOURLY) 49.0 else 149.0
    val discount = draft.discountAmount
    val finalTotal = draft.calculatedTotal ?: (basePrice + serviceFee - discount).coerceAtLeast(0.0)

    val initialPaymentType = remember(draft.selectedPaymentMethod) {
        when {
            draft.selectedPaymentMethod.contains("UPI", ignoreCase = true) -> StripePaymentType.UPI
            draft.selectedPaymentMethod.contains("QR", ignoreCase = true) || draft.selectedPaymentMethod.contains("Token", ignoreCase = true) -> StripePaymentType.QR
            draft.selectedPaymentMethod.contains("Bank", ignoreCase = true) || draft.selectedPaymentMethod.contains("Net", ignoreCase = true) -> StripePaymentType.NET_BANKING
            else -> StripePaymentType.CARD
        }
    }

    // Stripe SDK Modal Payment Sheet (Cards, UPI, QR & Net Banking)
    if (showStripeSheet) {
        StripePaymentSheetDialog(
            amountToPay = finalTotal,
            propertyName = property.name,
            roomTypeName = room?.name ?: "Standard Accommodation",
            guestName = draft.guestName,
            guestEmail = draft.guestEmail,
            guestPhone = draft.guestPhone,
            initialPaymentType = initialPaymentType,
            onDismiss = { viewModel.dismissStripePaymentSheet() },
            onPaymentSuccess = { stripeResult ->
                viewModel.completeBookingWithStripe(stripeResult)
            }
        )
    }

    // Property Date Range Selection Dialog
    if (showDateRangePicker) {
        val inCal = draft.checkInDate?.let { dateStr -> StayDate.parse(dateStr)?.toCalendar() }
        val outCal = draft.checkOutDate?.let { dateStr -> StayDate.parse(dateStr)?.toCalendar() }
        PropertyDateRangePickerDialog(
            initialCheckInCal = inCal,
            initialCheckOutCal = outCal,
            propertyName = property.name,
            onDismiss = { showDateRangePicker = false },
            onRangeSelected = { selectedInCal, selectedOutCal, nights ->
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                val newCheckIn = sdf.format(selectedInCal.time)
                val newCheckOut = sdf.format(selectedOutCal.time)
                val newDurationText = if (nights == 1) "1 Night" else "$nights Nights"
                viewModel.updateBookingDraft {
                    it.copy(
                        checkInDate = newCheckIn,
                        checkOutDate = newCheckOut,
                        durationText = newDurationText
                    )
                }
                showDateRangePicker = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
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
                IconButton(onClick = {
                    if (currentStep > 1) {
                        viewModel.setBookingStep(currentStep - 1)
                    } else {
                        viewModel.navigateBack()
                    }
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Book Your Stay",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Step $currentStep of 3: ${
                            when (currentStep) {
                                1 -> "Dates & Room Option"
                                2 -> "Guest Details"
                                else -> "Payment & Confirmation"
                            }
                        }",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Stepper Progress Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..3).forEach { stepNum ->
                val isCompleted = currentStep > stepNum
                val isCurrent = currentStep == stepNum

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted || isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text(
                            text = "$stepNum",
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                if (stepNum < 3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .padding(horizontal = 8.dp)
                            .background(if (currentStep > stepNum) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
        }

        // Body Content per Step
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // STEP 1: Room & Duration Selection
            if (currentStep == 1) {
                item {
                    Text(
                        text = "Selected Stay",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = property.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = "${property.area}, ${property.city}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date Range Summary Card with Interactive Change Button
                    DateRangeSummaryCard(
                        checkInDate = draft.checkInDate ?: "28 Aug 2026",
                        checkOutDate = draft.checkOutDate ?: "31 Aug 2026",
                        durationText = draft.durationText ?: "3 Nights",
                        onEditDatesClicked = {
                            showDateRangePicker = true
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Choose Room / Bed Option",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    property.roomOptions.forEach { opt ->
                        val isSelected = room?.id == opt.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.updateBookingDraft { it.copy(selectedRoom = opt) }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.updateBookingDraft { it.copy(selectedRoom = opt) } },
                                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(text = opt.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = "${opt.sharingType} • ${if (opt.isAcAvailable) "AC" else "Non-AC"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Text(
                                    text = "₹${opt.price.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Stay Dates & Live Price Breakdown Card
                    DynamicPriceBreakdownCard(
                        property = property,
                        selectedRoomOption = room,
                        onProceedToBooking = { checkIn, checkOut, duration, totalPayable ->
                            viewModel.updateBookingDraft {
                                it.copy(
                                    checkInDate = checkIn,
                                    checkOutDate = checkOut,
                                    durationText = duration,
                                    calculatedTotal = totalPayable
                                )
                            }
                            viewModel.setBookingStep(2)
                        },
                        onCalculationChanged = { calc ->
                            viewModel.updateBookingDraft {
                                it.copy(
                                    checkInDate = calc.formattedCheckIn,
                                    checkOutDate = calc.formattedCheckOut,
                                    durationText = calc.durationSummaryText,
                                    durationDiscount = calc.durationDiscount,
                                    calculatedTotal = calc.totalPayable
                                )
                            }
                        }
                    )
                }
            }

            // STEP 2: Guest Details & Verification
            if (currentStep == 2) {
                item {
                    Text(
                        text = "Resident / Guest Information",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Required for Styno verified gate pass and digital check-in.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = draft.guestName,
                        onValueChange = { name ->
                            guestDetailsError = null
                            viewModel.updateBookingDraft { it.copy(guestName = name) }
                        },
                        label = { Text("Full Legal Name (as per Govt ID)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guest_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = draft.guestPhone,
                        onValueChange = { phone ->
                            guestDetailsError = null
                            viewModel.updateBookingDraft { it.copy(guestPhone = phone) }
                        },
                        label = { Text("Mobile Number (for OTP & Passcode)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guest_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    if (guestDetailsError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = guestDetailsError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = draft.guestEmail,
                        onValueChange = { email -> viewModel.updateBookingDraft { it.copy(guestEmail = email) } },
                        label = { Text("Email Address (for Invoice & Receipt)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Government ID Type for Move-In Check",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    listOf("Aadhaar Card", "College Student ID", "Driving License", "Passport").forEach { idType ->
                        val isSelected = draft.guestIdType == idType
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.updateBookingDraft { it.copy(guestIdType = idType) }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.updateBookingDraft { it.copy(guestIdType = idType) } },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = idType, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Optional Station / Airport Pickup Option
                    val pickupConfig = remember(property.id) { viewModel.getPickupConfig(property.id) }
                    Text(
                        text = "Optional Station / Airport Pickup Service",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Verified driver with luggage assistance directly to property reception.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (draft.selectedPickupOption == null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (draft.selectedPickupOption == null) 1.5.dp else 1.dp,
                            color = if (draft.selectedPickupOption == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.updateBookingDraft { it.copy(selectedPickupOption = null) } }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = draft.selectedPickupOption == null,
                                onClick = { viewModel.updateBookingDraft { it.copy(selectedPickupOption = null) } }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("No pickup needed (I will arrive directly)", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    pickupConfig.hubs.forEach { hub ->
                        val isHubSelected = draft.selectedPickupOption?.hubName == hub.hubName
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isHubSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isHubSelected) 1.5.dp else 1.dp,
                                color = if (isHubSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.updateBookingDraft {
                                        it.copy(selectedPickupOption = hub)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isHubSelected,
                                        onClick = {
                                            viewModel.updateBookingDraft {
                                                it.copy(selectedPickupOption = hub)
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(hub.hubName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${hub.vehicleType} • ~${hub.estimatedArrivalMins} mins", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Text("₹${hub.price.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // STEP 3: Price Breakdown & Payment Method
            if (currentStep == 3) {
                item {
                    // Coupon Code Box
                    var couponInput by remember { mutableStateOf("") }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Discount, contentDescription = null, tint = StynoAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it },
                                placeholder = { Text("Coupon Code (e.g. STYNOFIRST)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (couponInput.equals("STYNOFIRST", ignoreCase = true) || couponInput.equals("WELCOME500", ignoreCase = true)) {
                                        viewModel.updateBookingDraft { it.copy(discountAmount = 500.0) }
                                    } else if (couponInput.equals("STUDENT10", ignoreCase = true)) {
                                        viewModel.updateBookingDraft { it.copy(discountAmount = 300.0) }
                                    }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Apply")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Price Summary",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            PriceLineItem("Stay Rent (${room?.name ?: "Standard"})", "₹${basePrice.toInt()}")
                            PriceLineItem("Styno Platform & Safety Fee", "₹${serviceFee.toInt()}")
                            draft.selectedPickupOption?.let { pickup ->
                                PriceLineItem("Station / Airport Pickup (${pickup.hubName})", "₹${pickup.price.toInt()}")
                            }
                            if (discount > 0) {
                                PriceLineItem("Promo Code Discount", "-₹${discount.toInt()}", isDiscount = true)
                            }
                            if ((room?.securityDeposit ?: 0.0) > 0) {
                                PriceLineItem("Refundable Security Deposit", "₹${(room?.securityDeposit ?: 0.0).toInt()}")
                            }

                            val pickupPrice = draft.selectedPickupOption?.price ?: 0.0
                            val calculatedPayable = (basePrice + serviceFee + pickupPrice - discount).coerceAtLeast(0.0)

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Payable Now",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                                )
                                Text(
                                    text = "₹${calculatedPayable.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }

                    if (property.ownerPaymentDetails.isConfigured || property.ownerPaymentDetails.upiId.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StynoEmerald.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(StynoEmerald.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Host Verified for Secure Payout",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Payout: ${property.ownerPaymentDetails.settlementMode.ifBlank { "Direct UPI Settlement" }} • Host: ${property.ownerPaymentDetails.accountHolderName.ifBlank { property.ownerInfo.name }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Method Options
                    Text(
                        text = "Select Payment Method",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val paymentMethods = listOf(
                        Triple(
                            "Credit / Debit Card (Stripe SDK)",
                            "Visa, Mastercard, RuPay, Amex • 256-bit encryption",
                            Icons.Default.CreditCard
                        ),
                        Triple(
                            "UPI (Google Pay / PhonePe / Paytm)",
                            "Instant UPI transfer or custom VPA ID",
                            Icons.Default.PhoneAndroid
                        ),
                        Triple(
                            "Dynamic Bharat QR Code",
                            "Scan & Pay using any UPI / banking app",
                            Icons.Default.QrCode
                        ),
                        Triple(
                            "Net Banking (All Major Banks)",
                            "HDFC, ICICI, SBI, Axis, Kotak & more",
                            Icons.Default.AccountBalance
                        )
                    )

                    paymentMethods.forEach { (method, subtitle, icon) ->
                        val isSelected = draft.selectedPaymentMethod == method ||
                                (draft.selectedPaymentMethod.contains("Card", ignoreCase = true) && method.contains("Card", ignoreCase = true)) ||
                                (draft.selectedPaymentMethod.contains("UPI", ignoreCase = true) && method.contains("UPI", ignoreCase = true)) ||
                                ((draft.selectedPaymentMethod.contains("QR", ignoreCase = true) || draft.selectedPaymentMethod.contains("Token", ignoreCase = true)) && method.contains("QR", ignoreCase = true)) ||
                                (draft.selectedPaymentMethod.contains("Bank", ignoreCase = true) && method.contains("Bank", ignoreCase = true))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.updateBookingDraft { it.copy(selectedPaymentMethod = method) }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.updateBookingDraft { it.copy(selectedPaymentMethod = method) } },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = method, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = StynoBluePrimary
                                        ) {
                                            Text(
                                                text = "STRIPE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "256-bit Bank Grade Encrypted Safe Checkout with Stripe Payment Sheet",
                            style = MaterialTheme.typography.labelSmall,
                            color = StynoEmerald
                        )
                    }
                }
            }
        }

        // Bottom CTA Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Payable Amount",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${finalTotal.toInt()}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = {
                        if (currentStep == 2) {
                            if (draft.guestName.isBlank() || draft.guestPhone.trim().length < 10) {
                                guestDetailsError = "Please enter your legal name and a valid 10-digit mobile number."
                                return@Button
                            }
                            guestDetailsError = null
                        }
                        if (currentStep < 3) {
                            viewModel.setBookingStep(currentStep + 1)
                        } else {
                            viewModel.openStripePaymentSheet()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("booking_flow_action_button")
                ) {
                    if (currentStep == 3) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = if (currentStep < 3) "Continue" else "Pay with Stripe Sheet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceLineItem(title: String, amount: String, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDiscount) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (isDiscount) StynoEmerald else MaterialTheme.colorScheme.onSurface
        )
    }
}

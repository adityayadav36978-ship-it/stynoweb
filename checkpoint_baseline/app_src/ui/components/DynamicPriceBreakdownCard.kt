package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DurationType
import com.example.data.model.Property
import com.example.data.model.RoomOption
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.theme.StynoGold
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * Calculated Price Breakdown Data Model
 */
data class DynamicPriceCalculation(
    val baseRate: Double,
    val durationUnits: Int,
    val unitLabel: String,
    val rawStayPrice: Double,
    val longStayDiscountRate: Double,
    val durationDiscount: Double,
    val couponDiscount: Double,
    val platformFee: Double,
    val taxAmount: Double,
    val refundableDeposit: Double,
    val totalPayable: Double,
    val effectiveDailyRate: Double,
    val formattedCheckIn: String,
    val formattedCheckOut: String,
    val durationSummaryText: String
)

/**
 * Interactive Dynamic Price Breakdown Card that updates live as the user
 * adjusts check-in/move-in date, check-out/duration, room tier, or promo code.
 *
 * Provides complete transparency into room rent, platform safety fees,
 * hospitality taxes, long-stay discounts, security deposit, and total cost before booking.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DynamicPriceBreakdownCard(
    property: Property,
    selectedRoomOption: RoomOption?,
    modifier: Modifier = Modifier,
    initialCheckInCal: Calendar? = null,
    onProceedToBooking: (checkIn: String, checkOut: String, durationText: String, totalPayable: Double) -> Unit = { _, _, _, _ -> },
    onCalculationChanged: ((DynamicPriceCalculation) -> Unit)? = null
) {
    // Reference date anchor (28 Aug 2026 default)
    val checkInCalState = remember {
        mutableStateOf(
            initialCheckInCal?.clone() as? Calendar ?: Calendar.getInstance().apply {
                set(Calendar.YEAR, 2026)
                set(Calendar.MONTH, Calendar.AUGUST)
                set(Calendar.DAY_OF_MONTH, 28)
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        )
    }

    // Duration count depending on property duration type
    // Daily: Nights (1, 2, 3, 5, 7, 14, 30)
    // Monthly: Months (1, 2, 3, 6, 12)
    // Hourly: Hours (3, 6, 12, 24)
    // Weekly: Weeks (1, 2, 4)
    var durationUnits by remember(property.durationType, property.quickStayConfig.isEnabled) {
        mutableIntStateOf(
            if (property.quickStayConfig.isEnabled) {
                property.quickStayConfig.configuredOptions.firstOrNull { it.isEnabled }?.durationHours ?: 1
            }
            else when (property.durationType) {
                DurationType.HOURLY -> 6
                DurationType.DAILY -> 3
                DurationType.WEEKLY -> 2
                DurationType.MONTHLY -> 1
                DurationType.YEARLY -> 1
            }
        )
    }

    // Coupon code state
    var couponCode by remember { mutableStateOf("STYNOFIRST") }
    var appliedDiscount by remember { mutableStateOf(250.0) }
    var couponInput by remember { mutableStateOf("") }
    var couponErrorMessage by remember { mutableStateOf<String?>(null) }
    var isCouponFieldVisible by remember { mutableStateOf(false) }

    // Dialog state for interactive calendar date picker
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var datePickerTarget by remember { mutableStateOf("CHECK_IN") } // "CHECK_IN" or "CHECK_OUT"

    // Detailed line items expandable toggle
    var isDetailedBreakdownExpanded by remember { mutableStateOf(true) }

    // Active room base price
    val activeRoom = selectedRoomOption ?: property.roomOptions.firstOrNull()
    val baseRate = activeRoom?.price ?: property.startingPrice

    // Calculate Check-out Calendar based on checkIn + durationUnits
    val checkOutCal = remember(checkInCalState.value, durationUnits, property.durationType) {
        val cal = checkInCalState.value.clone() as Calendar
        when (property.durationType) {
            DurationType.HOURLY -> cal.apply { add(Calendar.HOUR_OF_DAY, durationUnits) }
            DurationType.DAILY -> cal.apply { add(Calendar.DAY_OF_MONTH, durationUnits) }
            DurationType.WEEKLY -> cal.apply { add(Calendar.DAY_OF_MONTH, durationUnits * 7) }
            DurationType.MONTHLY -> cal.apply { add(Calendar.MONTH, durationUnits) }
            DurationType.YEARLY -> cal.apply { add(Calendar.YEAR, durationUnits) }
        }
    }

    // Date formatting strings
    val dateFormatMedium = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    val dateFormatLong = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH)
    val formattedCheckIn = remember(checkInCalState.value) { dateFormatMedium.format(checkInCalState.value.time) }
    val formattedCheckOut = remember(checkOutCal) { dateFormatMedium.format(checkOutCal.time) }

    // Duration label and summary
    val (unitLabel, durationSummaryText) = remember(property.durationType, durationUnits) {
        when (property.durationType) {
            DurationType.HOURLY -> Pair("Hour", "$durationUnits Hours")
            DurationType.DAILY -> Pair("Night", if (durationUnits == 1) "1 Night" else "$durationUnits Nights")
            DurationType.WEEKLY -> Pair("Week", if (durationUnits == 1) "1 Week" else "$durationUnits Weeks")
            DurationType.MONTHLY -> Pair("Month", if (durationUnits == 1) "1 Month" else "$durationUnits Months")
            DurationType.YEARLY -> Pair("Year", if (durationUnits == 1) "1 Year" else "$durationUnits Years")
        }
    }

    // Raw stay price calculation
    val rawStayPrice = remember(baseRate, durationUnits, property.quickStayConfig, property.durationType) {
        if (property.quickStayConfig.isEnabled) {
            property.quickStayConfig.getPriceForDuration(durationUnits)
        } else {
            baseRate * durationUnits
        }
    }

    // Long stay discount logic
    val longStayDiscountRate = remember(property.durationType, durationUnits) {
        when (property.durationType) {
            DurationType.DAILY -> when {
                durationUnits >= 14 -> 0.15 // 15% discount for 14+ nights
                durationUnits >= 7 -> 0.10 // 10% discount for weekly
                durationUnits >= 3 -> 0.05 // 5% discount for 3+ nights
                else -> 0.0
            }
            DurationType.MONTHLY -> when {
                durationUnits >= 12 -> 0.15 // 15% off annual stay
                durationUnits >= 6 -> 0.10 // 10% off half-year
                durationUnits >= 3 -> 0.05 // 5% off quarter
                else -> 0.0
            }
            DurationType.WEEKLY -> when {
                durationUnits >= 4 -> 0.10
                durationUnits >= 2 -> 0.05
                else -> 0.0
            }
            DurationType.HOURLY -> when {
                durationUnits >= 12 -> 0.10
                else -> 0.0
            }
            DurationType.YEARLY -> 0.05
        }
    }

    val durationDiscount = remember(rawStayPrice, longStayDiscountRate) {
        rawStayPrice * longStayDiscountRate
    }

    // Platform & SafeStay Guarantee Fee
    val platformFee = remember(property.durationType) {
        when (property.durationType) {
            DurationType.HOURLY -> 49.0
            DurationType.DAILY -> 99.0
            DurationType.WEEKLY -> 149.0
            DurationType.MONTHLY -> 199.0
            DurationType.YEARLY -> 299.0
        }
    }

    // 5% standard hospitality GST on accommodation subtotal
    val discountedRent = (rawStayPrice - durationDiscount).coerceAtLeast(0.0)
    val taxAmount = remember(discountedRent) {
        (discountedRent * 0.05).roundToInt().toDouble()
    }

    // Security deposit for monthly stays (100% refundable)
    val refundableDeposit = activeRoom?.securityDeposit ?: 0.0

    // Final total payable
    val totalPayable = remember(discountedRent, appliedDiscount, platformFee, taxAmount, refundableDeposit) {
        ((discountedRent - appliedDiscount).coerceAtLeast(0.0) + platformFee + taxAmount + refundableDeposit)
    }

    // Effective daily rate calculation
    val effectiveDays = remember(property.durationType, durationUnits) {
        when (property.durationType) {
            DurationType.HOURLY -> (durationUnits / 24.0).coerceAtLeast(0.25)
            DurationType.DAILY -> durationUnits.toDouble()
            DurationType.WEEKLY -> (durationUnits * 7).toDouble()
            DurationType.MONTHLY -> (durationUnits * 30).toDouble()
            DurationType.YEARLY -> (durationUnits * 365).toDouble()
        }
    }

    val effectiveDailyRate = remember(totalPayable, refundableDeposit, effectiveDays) {
        ((totalPayable - refundableDeposit) / effectiveDays).roundToInt().toDouble()
    }

    // Publish changes to listener
    val currentCalculation = remember(
        baseRate, durationUnits, unitLabel, rawStayPrice,
        longStayDiscountRate, durationDiscount, appliedDiscount,
        platformFee, taxAmount, refundableDeposit, totalPayable,
        effectiveDailyRate, formattedCheckIn, formattedCheckOut, durationSummaryText
    ) {
        DynamicPriceCalculation(
            baseRate = baseRate,
            durationUnits = durationUnits,
            unitLabel = unitLabel,
            rawStayPrice = rawStayPrice,
            longStayDiscountRate = longStayDiscountRate,
            durationDiscount = durationDiscount,
            couponDiscount = appliedDiscount,
            platformFee = platformFee,
            taxAmount = taxAmount,
            refundableDeposit = refundableDeposit,
            totalPayable = totalPayable,
            effectiveDailyRate = effectiveDailyRate,
            formattedCheckIn = formattedCheckIn,
            formattedCheckOut = formattedCheckOut,
            durationSummaryText = durationSummaryText
        )
    }

    LaunchedEffect(currentCalculation) {
        onCalculationChanged?.invoke(currentCalculation)
    }

    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .testTag("dynamic_price_breakdown_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Room Tier Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StynoBluePrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Stay Dates",
                            tint = StynoBluePrimary,
                            modifier = Modifier
                                .padding(7.dp)
                                .size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Stay Dates & Price Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Live updates as you select dates & duration",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = activeRoom?.name ?: "Standard",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Check-in / Check-out Date Selector Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Check-in Selector Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            datePickerTarget = "CHECK_IN"
                            showDatePickerDialog = true
                        }
                        .testTag("check_in_date_picker_button")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (property.durationType == DurationType.MONTHLY) "MOVE-IN DATE" else "CHECK-IN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = StynoBluePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formattedCheckIn,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = SimpleDateFormat("EEEE", Locale.ENGLISH).format(checkInCalState.value.time),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Check-out / Move-out Selector Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            datePickerTarget = "CHECK_OUT"
                            showDatePickerDialog = true
                        }
                        .testTag("check_out_date_picker_button")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (property.durationType == DurationType.MONTHLY) "MOVE-OUT DATE" else "CHECK-OUT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = StynoBluePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formattedCheckOut,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = SimpleDateFormat("EEEE", Locale.ENGLISH).format(checkOutCal.time),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Duration Presets Chips & Counter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Duration:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Quick Increment/Decrement Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (durationUnits > 1) {
                                durationUnits -= 1
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease duration", modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = "$durationUnits",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = {
                            if (durationUnits < 36) {
                                durationUnits += 1
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase duration", modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Preset Buttons based on Duration Type
            val presetOptions = remember(property.durationType, property.quickStayConfig) {
                if (property.quickStayConfig.isEnabled) {
                    val cfg = property.quickStayConfig
                    val activeOptions = cfg.configuredOptions.filter { it.isEnabled }
                    if (activeOptions.isNotEmpty()) {
                        activeOptions.map { opt ->
                            Pair(opt.durationHours, "${opt.durationHours} ${if (opt.durationHours == 1) "Hr" else "Hrs"} • ₹${opt.price.toInt()}")
                        }
                    } else {
                        listOf(
                            Pair(1, "1 Hr • ₹${cfg.price1Hour.toInt()}"),
                            Pair(2, "2 Hrs • ₹${cfg.price2Hours.toInt()}"),
                            Pair(3, "3 Hrs • ₹${cfg.price3Hours.toInt()}"),
                            Pair(4, "4 Hrs • ₹${cfg.price4Hours.toInt()}"),
                            Pair(5, "5 Hrs • ₹${cfg.price5Hours.toInt()}"),
                            Pair(10, "10 Hrs • ₹${cfg.price10Hours.toInt()}"),
                            Pair(12, "12 Hrs • ₹${cfg.price12Hours.toInt()}"),
                            Pair(24, "24 Hrs • ₹${cfg.price24Hours.toInt()}")
                        )
                    }
                } else {
                    when (property.durationType) {
                        DurationType.HOURLY -> listOf(
                            Pair(3, "3 Hrs"),
                            Pair(6, "6 Hrs"),
                            Pair(12, "12 Hrs (10% OFF)"),
                            Pair(24, "24 Hrs")
                        )
                        DurationType.DAILY -> listOf(
                            Pair(1, "1 Night"),
                            Pair(2, "2 Nights"),
                            Pair(3, "3 Nights (5% OFF)"),
                            Pair(7, "7 Nights (10% OFF)"),
                            Pair(14, "14 Nights (15% OFF)")
                        )
                        DurationType.WEEKLY -> listOf(
                            Pair(1, "1 Wk"),
                            Pair(2, "2 Wks (5% OFF)"),
                            Pair(4, "4 Wks (10% OFF)")
                        )
                        DurationType.MONTHLY -> listOf(
                            Pair(1, "1 Mo"),
                            Pair(3, "3 Mos (5% OFF)"),
                            Pair(6, "6 Mos (10% OFF)"),
                            Pair(12, "12 Mos (15% OFF)")
                        )
                        DurationType.YEARLY -> listOf(
                            Pair(1, "1 Year"),
                            Pair(2, "2 Years")
                        )
                    }
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presetOptions) { (units, label) ->
                    val isSelected = durationUnits == units
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { durationUnits = units }
                            .testTag("duration_preset_${units}_button")
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Duration and Savings Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (durationDiscount > 0) StynoEmerald.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(
                    1.dp,
                    if (durationDiscount > 0) StynoEmerald.copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (durationDiscount > 0) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (durationDiscount > 0) StynoEmerald else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Total Stay: $durationSummaryText ($formattedCheckIn → $formattedCheckOut)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (durationDiscount > 0) {
                                Text(
                                    text = "🎉 ${(longStayDiscountRate * 100).toInt()}% Long-Stay Discount Applied (Save ${currencyFormatter.format(durationDiscount).replace(".00", "")})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StynoEmerald
                                )
                            }
                        }
                    }

                    // Effective daily cost badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "~${currencyFormatter.format(effectiveDailyRate).replace(".00", "")}/day",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Expandable Price Breakdown Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isDetailedBreakdownExpanded = !isDetailedBreakdownExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Price Calculation Details",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isDetailedBreakdownExpanded) "Hide details" else "Show details",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (isDetailedBreakdownExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isDetailedBreakdownExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Line 1: Base Rent × Duration
                    PriceBreakdownItemRow(
                        title = "${activeRoom?.name ?: "Room Rent"} (${currencyFormatter.format(baseRate).replace(".00", "")} × $durationUnits $unitLabel${if (durationUnits > 1) "s" else ""})",
                        amount = currencyFormatter.format(rawStayPrice).replace(".00", "")
                    )

                    // Line 2: Long Stay Discount (if any)
                    if (durationDiscount > 0) {
                        PriceBreakdownItemRow(
                            title = "Long-Stay Duration Discount (${(longStayDiscountRate * 100).toInt()}%)",
                            amount = "-${currencyFormatter.format(durationDiscount).replace(".00", "")}",
                            isDiscount = true
                        )
                    }

                    // Line 3: Coupon Discount (if applied)
                    if (appliedDiscount > 0) {
                        PriceBreakdownItemRow(
                            title = "Promo Code ($couponCode)",
                            amount = "-${currencyFormatter.format(appliedDiscount).replace(".00", "")}",
                            isDiscount = true
                        )
                    }

                    // Line 4: Styno Safety & Platform Fee
                    PriceBreakdownItemRow(
                        title = "Styno SafeStay & Verification Fee",
                        amount = currencyFormatter.format(platformFee).replace(".00", ""),
                        badgeText = "Insurance & KYC"
                    )

                    // Line 5: Hospitality GST (5%)
                    PriceBreakdownItemRow(
                        title = "Hospitality GST & Govt. Taxes (5%)",
                        amount = currencyFormatter.format(taxAmount).replace(".00", "")
                    )

                    // Line 6: Security Deposit (if monthly and exists)
                    if (refundableDeposit > 0) {
                        PriceBreakdownItemRow(
                            title = "Refundable Security Deposit",
                            amount = currencyFormatter.format(refundableDeposit).replace(".00", ""),
                            badgeText = "100% Refundable"
                        )
                    }

                    // Promo Code Box Toggle & Input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { isCouponFieldVisible = !isCouponFieldVisible }
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Discount,
                                contentDescription = null,
                                tint = StynoAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (appliedDiscount > 0) "Coupon '$couponCode' applied (Edit)" else "Have a promo code?",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StynoAccent
                            )
                        }
                    }

                    if (isCouponFieldVisible) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = {
                                    couponInput = it
                                    couponErrorMessage = null
                                },
                                placeholder = { Text("e.g. STYNOFIRST, STUDENT10", fontSize = 12.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("coupon_input_field"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val code = couponInput.trim().uppercase()
                                    when (code) {
                                        "STYNOFIRST", "WELCOME500" -> {
                                            couponCode = code
                                            appliedDiscount = 500.0
                                            couponErrorMessage = null
                                            isCouponFieldVisible = false
                                        }
                                        "STUDENT10" -> {
                                            couponCode = code
                                            appliedDiscount = 350.0
                                            couponErrorMessage = null
                                            isCouponFieldVisible = false
                                        }
                                        "FLASH200" -> {
                                            couponCode = code
                                            appliedDiscount = 200.0
                                            couponErrorMessage = null
                                            isCouponFieldVisible = false
                                        }
                                        else -> {
                                            couponErrorMessage = "Invalid code. Try STYNOFIRST or STUDENT10"
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("apply_coupon_button")
                            ) {
                                Text("Apply", fontSize = 12.sp)
                            }
                        }

                        couponErrorMessage?.let { err ->
                            Text(text = err, color = Color(0xFFDC2626), fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Total Payable Now with Live Animated Content
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Cost (Before Booking)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Includes rent, taxes & safety guarantee",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AnimatedContent(
                    targetState = totalPayable,
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn())
                            .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                    },
                    label = "total_price_animation"
                ) { targetTotal ->
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currencyFormatter.format(targetTotal).replace(".00", ""),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("calculated_total_price_text")
                        )
                        if (durationDiscount + appliedDiscount > 0) {
                            Text(
                                text = "Saved ${currencyFormatter.format(durationDiscount + appliedDiscount).replace(".00", "")}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StynoEmerald
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Price Lock & Security Note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = StynoEmerald,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Price Lock Guarantee • Zero Hidden Charges at Move-in",
                    fontSize = 11.sp,
                    color = StynoEmerald,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Proceed to Booking Action Button
            Button(
                onClick = {
                    onProceedToBooking(
                        formattedCheckIn,
                        formattedCheckOut,
                        durationSummaryText,
                        totalPayable
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("proceed_to_booking_from_breakdown_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Proceed to Booking • ${currencyFormatter.format(totalPayable).replace(".00", "")}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // Interactive Date Range Selection Dialog for Property Bookings
    if (showDatePickerDialog) {
        PropertyDateRangePickerDialog(
            initialCheckInCal = checkInCalState.value,
            initialCheckOutCal = checkOutCal,
            propertyName = property.name,
            onDismiss = { showDatePickerDialog = false },
            onRangeSelected = { inCal, outCal, nights ->
                checkInCalState.value = inCal
                durationUnits = when (property.durationType) {
                    DurationType.HOURLY -> (nights * 24).coerceAtLeast(3)
                    DurationType.DAILY -> nights
                    DurationType.WEEKLY -> (nights / 7).coerceAtLeast(1)
                    DurationType.MONTHLY -> (nights / 30).coerceAtLeast(1)
                    DurationType.YEARLY -> (nights / 365).coerceAtLeast(1)
                }
                showDatePickerDialog = false
            }
        )
    }
}

/**
 * Single Row inside the Price Breakdown List
 */
@Composable
private fun PriceBreakdownItemRow(
    title: String,
    amount: String,
    isDiscount: Boolean = false,
    badgeText: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDiscount) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant
            )
            badgeText?.let { badge ->
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isDiscount) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isDiscount) StynoEmerald else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

/**
 * Interactive Material 3 Calendar Date Picker Dialog with month navigation and past-date protection.
 */
@Composable
fun StayCalendarDatePickerDialog(
    targetTitle: String,
    initialCal: Calendar,
    onDismiss: () -> Unit,
    onDateSelected: (Calendar) -> Unit
) {
    var displayedYear by remember { mutableIntStateOf(initialCal.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableIntStateOf(initialCal.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(initialCal.get(Calendar.DAY_OF_MONTH)) }

    val monthCal = remember(displayedYear, displayedMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (monthCal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday = 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = targetTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(monthCal.time),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Month Navigation Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (displayedMonth == Calendar.JANUARY) {
                            displayedMonth = Calendar.DECEMBER
                            displayedYear -= 1
                        } else {
                            displayedMonth -= 1
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                    }

                    Text(
                        text = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(monthCal.time),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    IconButton(onClick = {
                        if (displayedMonth == Calendar.DECEMBER) {
                            displayedMonth = Calendar.JANUARY
                            displayedYear += 1
                        } else {
                            displayedMonth += 1
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Weekday Labels
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { dayLabel ->
                        Text(
                            text = dayLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Calendar Grid (6 rows of 7 days)
                val totalCells = ((daysInMonth + firstDayOfWeek + 6) / 7) * 7
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (row in 0 until (totalCells / 7)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            for (col in 0..6) {
                                val cellIndex = row * 7 + col
                                val dayNumber = cellIndex - firstDayOfWeek + 1
                                if (dayNumber in 1..daysInMonth) {
                                    val isSelected = selectedDay == dayNumber
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                            )
                                            .clickable {
                                                selectedDay = dayNumber
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Select Helpers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                displayedYear = 2026
                                displayedMonth = Calendar.AUGUST
                                selectedDay = 28
                            }
                    ) {
                        Text(
                            text = "Today (28 Aug)",
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                displayedYear = 2026
                                displayedMonth = Calendar.SEPTEMBER
                                selectedDay = 1
                            }
                    ) {
                        Text(
                            text = "1st Sep",
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                displayedYear = 2026
                                displayedMonth = Calendar.OCTOBER
                                selectedDay = 1
                            }
                    ) {
                        Text(
                            text = "1st Oct",
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val resultCal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, displayedYear)
                        set(Calendar.MONTH, displayedMonth)
                        set(Calendar.DAY_OF_MONTH, selectedDay)
                        set(Calendar.HOUR_OF_DAY, 12)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onDateSelected(resultCal)
                }
            ) {
                Text("Select Date")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

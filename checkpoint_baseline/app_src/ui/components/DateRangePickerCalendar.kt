package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Lightweight, zero-dependency date representation for stay calculations.
 * 1-indexed month: 1 = Jan, 12 = Dec for natural clarity.
 */
data class StayDate(
    val year: Int,
    val month: Int, // 1 to 12
    val day: Int    // 1 to 31
) : Comparable<StayDate> {

    override fun compareTo(other: StayDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }

    fun isBefore(other: StayDate): Boolean = compareTo(other) < 0
    fun isAfter(other: StayDate): Boolean = compareTo(other) > 0
    fun isOnOrAfter(other: StayDate): Boolean = compareTo(other) >= 0
    fun isOnOrBefore(other: StayDate): Boolean = compareTo(other) <= 0

    fun toCalendar(): Calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    fun format(pattern: String = "dd MMM yyyy"): String {
        val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
        return sdf.format(toCalendar().time)
    }

    fun plusDays(days: Int): StayDate {
        val cal = toCalendar()
        cal.add(Calendar.DAY_OF_MONTH, days)
        return fromCalendar(cal)
    }

    fun plusMonths(months: Int): StayDate {
        val cal = toCalendar()
        cal.add(Calendar.MONTH, months)
        return fromCalendar(cal)
    }

    companion object {
        fun fromCalendar(cal: Calendar): StayDate {
            return StayDate(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH) + 1,
                day = cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun today(): StayDate {
            // Anchor default to 2026 app timeline or real current date
            val cal = Calendar.getInstance()
            // If year is before 2026, align to 28 Aug 2026 reference timeline
            if (cal.get(Calendar.YEAR) < 2026) {
                return StayDate(2026, 8, 28)
            }
            return fromCalendar(cal)
        }

        fun parse(dateStr: String): StayDate? {
            val patterns = listOf("dd MMM yyyy", "yyyy-MM-dd", "d MMM yyyy", "dd/MM/yyyy")
            for (pattern in patterns) {
                try {
                    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
                    sdf.isLenient = false
                    val date = sdf.parse(dateStr.trim())
                    if (date != null) {
                        val cal = Calendar.getInstance().apply { time = date }
                        return fromCalendar(cal)
                    }
                } catch (_: Exception) { }
            }
            return null
        }

        fun daysBetween(start: StayDate, end: StayDate): Int {
            val startMs = start.toCalendar().timeInMillis
            val endMs = end.toCalendar().timeInMillis
            val diffMs = endMs - startMs
            return (diffMs / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        }
    }
}

/**
 * Which field the user is currently targeting in the calendar.
 */
enum class DateRangeTarget {
    CHECK_IN,
    CHECK_OUT
}

/**
 * Holds complete date range selection state and duration metrics.
 */
data class DateRangeState(
    val checkInDate: StayDate? = null,
    val checkOutDate: StayDate? = null,
    val activeTarget: DateRangeTarget = DateRangeTarget.CHECK_IN
) {
    val durationNights: Int
        get() = if (checkInDate != null && checkOutDate != null) {
            StayDate.daysBetween(checkInDate, checkOutDate)
        } else 0

    val isCompleteRange: Boolean
        get() = checkInDate != null && checkOutDate != null && checkOutDate.isAfter(checkInDate)

    val durationText: String
        get() = when {
            checkInDate == null && checkOutDate == null -> "Select dates"
            checkInDate != null && checkOutDate == null -> "Choose check-out"
            durationNights <= 0 -> "0 Nights"
            durationNights == 1 -> "1 Night"
            durationNights < 30 -> "$durationNights Nights"
            durationNights in 30..31 -> "1 Month (${durationNights}d)"
            durationNights in 32..89 -> "${durationNights / 30} Months (${durationNights}d)"
            durationNights in 90..92 -> "3 Months (Semester)"
            durationNights in 180..184 -> "6 Months"
            else -> "$durationNights Nights (${durationNights / 30}m)"
        }
}

/**
 * Interactive Date Range Selection Calendar View.
 * Displays check-in & check-out dual tabs, quick duration presets,
 * month navigation, and an interactive continuous range highlighted calendar grid.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DateRangeSelectionCalendar(
    initialCheckIn: StayDate? = null,
    initialCheckOut: StayDate? = null,
    minStayDate: StayDate = StayDate.today(),
    propertyName: String? = null,
    onRangeChanged: (checkIn: StayDate?, checkOut: StayDate?, durationNights: Int) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    // Current active range selection state
    var checkIn by remember { mutableStateOf(initialCheckIn ?: minStayDate) }
    var checkOut by remember {
        mutableStateOf(
            initialCheckOut ?: (initialCheckIn?.plusDays(3) ?: minStayDate.plusDays(3))
        )
    }
    var activeTarget by remember { mutableStateOf(DateRangeTarget.CHECK_OUT) }

    // Displayed month in the calendar grid
    var displayedYear by remember {
        mutableIntStateOf(checkIn?.year ?: minStayDate.year)
    }
    var displayedMonth by remember {
        mutableIntStateOf(checkIn?.month ?: minStayDate.month) // 1..12
    }

    // Days in current month and first day of week (Monday = 0)
    val monthCal = remember(displayedYear, displayedMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (monthCal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Mon = 0, Sun = 6

    val durationNights = remember(checkIn, checkOut) {
        if (checkIn != null && checkOut != null && checkOut!!.isAfter(checkIn!!)) {
            StayDate.daysBetween(checkIn!!, checkOut!!)
        } else 0
    }

    LaunchedEffect(checkIn, checkOut, durationNights) {
        onRangeChanged(checkIn, checkOut, durationNights)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("date_range_selection_calendar")
    ) {
        // 1. Dual Tab Summary (Check-In | Duration Badge | Check-Out)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Check-In Box
            val isCheckInActive = activeTarget == DateRangeTarget.CHECK_IN
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isCheckInActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(
                    width = if (isCheckInActive) 2.dp else 1.dp,
                    color = if (isCheckInActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { activeTarget = DateRangeTarget.CHECK_IN }
                    .testTag("date_range_check_in_tab")
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHECK-IN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCheckInActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isCheckInActive) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(6.dp)
                            ) {}
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = checkIn?.format("EEE, dd MMM") ?: "Select Date",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = checkIn?.format("yyyy") ?: "",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Duration Pill Indicator in the middle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = StynoBluePrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, StynoBluePrimary.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.East,
                            contentDescription = null,
                            tint = StynoBluePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when {
                        durationNights <= 0 -> "Pick check-out"
                        durationNights == 1 -> "1 night"
                        durationNights < 30 -> "$durationNights nights"
                        durationNights in 30..31 -> "1 month"
                        else -> "$durationNights nights"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (durationNights > 0) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Check-Out Box
            val isCheckOutActive = activeTarget == DateRangeTarget.CHECK_OUT
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isCheckOutActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(
                    width = if (isCheckOutActive) 2.dp else 1.dp,
                    color = if (isCheckOutActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { activeTarget = DateRangeTarget.CHECK_OUT }
                    .testTag("date_range_check_out_tab")
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHECK-OUT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCheckOutActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isCheckOutActive) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(6.dp)
                            ) {}
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = checkOut?.format("EEE, dd MMM") ?: "Select Date",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = checkOut?.format("yyyy") ?: "",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Quick Stay Duration Presets (Weekend, 1 Week, 2 Weeks, 1 Month, 3 Months)
        Text(
            text = "Quick Presets:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val presets = listOf(
                Pair("Weekend (2N)", 2),
                Pair("3 Nights", 3),
                Pair("1 Week (7N)", 7),
                Pair("2 Weeks (14N)", 14),
                Pair("1 Month (30d)", 30),
                Pair("3 Months", 90),
                Pair("6 Months", 180)
            )

            presets.forEach { (label, days) ->
                val isSelectedPreset = durationNights == days
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelectedPreset) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(
                        width = if (isSelectedPreset) 1.5.dp else 1.dp,
                        color = if (isSelectedPreset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            val baseCheckIn = checkIn ?: minStayDate
                            checkIn = baseCheckIn
                            checkOut = baseCheckIn.plusDays(days)
                            activeTarget = DateRangeTarget.CHECK_OUT
                        }
                        .testTag("preset_$days")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelectedPreset) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelectedPreset) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelectedPreset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Month Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (displayedMonth == 1) {
                        displayedMonth = 12
                        displayedYear -= 1
                    } else {
                        displayedMonth -= 1
                    }
                },
                modifier = Modifier.testTag("calendar_prev_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(monthCal.time),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (activeTarget == DateRangeTarget.CHECK_IN) "Select check-in date" else "Select check-out date",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = {
                    if (displayedMonth == 12) {
                        displayedMonth = 1
                        displayedYear += 1
                    } else {
                        displayedMonth += 1
                    }
                },
                modifier = Modifier.testTag("calendar_next_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Weekday Column Headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEachIndexed { index, dayName ->
                val isWeekend = index >= 5
                Text(
                    text = dayName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWeekend) StynoAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 5. Interactive Calendar Grid with Continuous Range Highlight
        val totalCells = ((daysInMonth + firstDayOfWeek + 6) / 7) * 7
        val rowCount = totalCells / 7

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (row in 0 until rowCount) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - firstDayOfWeek + 1

                        if (dayNumber in 1..daysInMonth) {
                            val currentDate = StayDate(displayedYear, displayedMonth, dayNumber)
                            val isPastDate = currentDate.isBefore(minStayDate)

                            val isStart = checkIn != null && currentDate == checkIn
                            val isEnd = checkOut != null && currentDate == checkOut
                            val isInRange = checkIn != null && checkOut != null &&
                                    currentDate.isAfter(checkIn!!) && currentDate.isBefore(checkOut!!)
                            val isStartAndEnd = isStart && isEnd

                            // Day Cell with continuous range background band
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Background highlight strip for range
                                if (isInRange) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                shape = when {
                                                    col == 0 -> RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                                    col == 6 -> RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                                    else -> RoundedCornerShape(0.dp)
                                                }
                                            )
                                    )
                                } else if (isStart && checkOut != null && checkOut!!.isAfter(checkIn!!)) {
                                    // Start of multi-day range: right half tinted
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.5f)
                                                .align(Alignment.CenterEnd)
                                                .height(32.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                    shape = if (col == 6) RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp) else RoundedCornerShape(0.dp)
                                                )
                                        )
                                    }
                                } else if (isEnd && checkIn != null && checkOut!!.isAfter(checkIn!!)) {
                                    // End of multi-day range: left half tinted
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.5f)
                                                .align(Alignment.CenterStart)
                                                .height(32.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                    shape = if (col == 0) RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp) else RoundedCornerShape(0.dp)
                                                )
                                        )
                                    }
                                }

                                // Interactive Day Circle / Button
                                val isSelectedBorder = currentDate == minStayDate && !isStart && !isEnd
                                val circleColor = when {
                                    isStart || isEnd -> MaterialTheme.colorScheme.primary
                                    else -> Color.Transparent
                                }

                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(circleColor)
                                        .then(
                                            if (isSelectedBorder) Modifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                            else Modifier
                                        )
                                        .clickable(enabled = !isPastDate) {
                                            // Handle interactive range selection logic
                                            when {
                                                activeTarget == DateRangeTarget.CHECK_IN -> {
                                                    checkIn = currentDate
                                                    // If check-out exists and is on or before new check-in, push check-out forward
                                                    if (checkOut != null && checkOut!!.isOnOrBefore(currentDate)) {
                                                        checkOut = currentDate.plusDays(1)
                                                    }
                                                    activeTarget = DateRangeTarget.CHECK_OUT
                                                }
                                                activeTarget == DateRangeTarget.CHECK_OUT -> {
                                                    if (checkIn == null || currentDate.isBefore(checkIn!!)) {
                                                        // Tapped before check-in -> set as new check-in
                                                        checkIn = currentDate
                                                        checkOut = currentDate.plusDays(1)
                                                        activeTarget = DateRangeTarget.CHECK_OUT
                                                    } else if (currentDate == checkIn) {
                                                        // Same day: set check-out to next day minimum
                                                        checkOut = currentDate.plusDays(1)
                                                    } else {
                                                        // Valid check-out
                                                        checkOut = currentDate
                                                    }
                                                }
                                            }
                                        }
                                        .testTag("date_cell_${displayedYear}_${displayedMonth}_$dayNumber"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            fontSize = 12.sp,
                                            fontWeight = if (isStart || isEnd || isInRange) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isPastDate -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
                                                isStart || isEnd -> Color.White
                                                isInRange -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            // Empty cell spacer
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 6. Selection Helper & Status Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        checkIn != null && checkOut != null -> "Total Stay: ${durationNights} night${if (durationNights == 1) "" else "s"}"
                        checkIn != null -> "Now tap check-out date"
                        else -> "Tap check-in date"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick reset button
            TextButton(
                onClick = {
                    checkIn = minStayDate
                    checkOut = minStayDate.plusDays(3)
                    activeTarget = DateRangeTarget.CHECK_IN
                },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset", fontSize = 11.sp)
            }
        }
    }
}

/**
 * Modal Dialog for Property Date Range Selection.
 * Allows users to choose check-in and check-out dates with real-time confirmation.
 */
@Composable
fun PropertyDateRangePickerDialog(
    initialCheckInCal: Calendar? = null,
    initialCheckOutCal: Calendar? = null,
    minDateCal: Calendar? = null,
    propertyName: String? = null,
    onDismiss: () -> Unit,
    onRangeSelected: (checkInCal: Calendar, checkOutCal: Calendar, durationNights: Int) -> Unit
) {
    val initialStayCheckIn = remember {
        initialCheckInCal?.let { StayDate.fromCalendar(it) } ?: StayDate.today()
    }
    val initialStayCheckOut = remember {
        initialCheckOutCal?.let { StayDate.fromCalendar(it) } ?: initialStayCheckIn.plusDays(3)
    }
    val minStayDate = remember {
        minDateCal?.let { StayDate.fromCalendar(it) } ?: StayDate.today()
    }

    var selectedCheckIn by remember { mutableStateOf<StayDate?>(initialStayCheckIn) }
    var selectedCheckOut by remember { mutableStateOf<StayDate?>(initialStayCheckOut) }
    var calculatedNights by remember { mutableIntStateOf(StayDate.daysBetween(initialStayCheckIn, initialStayCheckOut)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .heightIn(max = 720.dp)
            .testTag("property_date_range_picker_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = StynoBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select Stay Dates",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    propertyName?.let { name ->
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Clear, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                DateRangeSelectionCalendar(
                    initialCheckIn = selectedCheckIn,
                    initialCheckOut = selectedCheckOut,
                    minStayDate = minStayDate,
                    propertyName = propertyName,
                    onRangeChanged = { inDate, outDate, nights ->
                        selectedCheckIn = inDate
                        selectedCheckOut = outDate
                        calculatedNights = nights
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val inDate = selectedCheckIn ?: minStayDate
                    val outDate = selectedCheckOut ?: inDate.plusDays(1)
                    val inCal = inDate.toCalendar()
                    val outCal = outDate.toCalendar()
                    val nights = StayDate.daysBetween(inDate, outDate).coerceAtLeast(1)
                    onRangeSelected(inCal, outCal, nights)
                    onDismiss()
                },
                enabled = selectedCheckIn != null && selectedCheckOut != null && calculatedNights > 0,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_date_range_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (calculatedNights > 0) "Confirm ($calculatedNights Nights)" else "Select Check-out",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Clean embeddable Card displaying the selected stay date range with an
 * interactive "Change Dates" button that invokes the Date Range Selection Dialog.
 */
@Composable
fun DateRangeSummaryCard(
    checkInDate: String,
    checkOutDate: String,
    durationText: String,
    onEditDatesClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("date_range_summary_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StynoBluePrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = StynoBluePrimary,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Booking Date Range",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = durationText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CHECK-IN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = checkInDate,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = Icons.Default.East,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(16.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CHECK-OUT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = checkOutDate,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = onEditDatesClicked,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("edit_date_range_button")
                ) {
                    Text("Change", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

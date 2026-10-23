package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.PropertyType
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Robust Stay Calendar helper to parse date strings from multiple formats.
 */
object StayDateHelper {
    private val dateFormats = listOf(
        SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH),
        SimpleDateFormat("d MMM yyyy", Locale.ENGLISH),
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
        SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH),
        SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    )

    fun parseDate(dateStr: String?): Calendar? {
        if (dateStr.isNullOrBlank()) return null
        val trimmed = dateStr.trim()
        for (fmt in dateFormats) {
            try {
                val parsed = fmt.parse(trimmed)
                if (parsed != null) {
                    val cal = Calendar.getInstance()
                    cal.time = parsed
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    return cal
                }
            } catch (_: Exception) {
                // Try next pattern
            }
        }
        return null
    }

    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isDateBetweenInclusive(target: Calendar, start: Calendar, end: Calendar): Boolean {
        val targetDay = target.clone() as Calendar
        targetDay.set(Calendar.HOUR_OF_DAY, 0)
        targetDay.set(Calendar.MINUTE, 0)
        targetDay.set(Calendar.SECOND, 0)
        targetDay.set(Calendar.MILLISECOND, 0)

        val startDay = start.clone() as Calendar
        startDay.set(Calendar.HOUR_OF_DAY, 0)
        startDay.set(Calendar.MINUTE, 0)
        startDay.set(Calendar.SECOND, 0)
        startDay.set(Calendar.MILLISECOND, 0)

        val endDay = end.clone() as Calendar
        endDay.set(Calendar.HOUR_OF_DAY, 0)
        endDay.set(Calendar.MINUTE, 0)
        endDay.set(Calendar.SECOND, 0)
        endDay.set(Calendar.MILLISECOND, 0)

        return (targetDay == startDay || targetDay == endDay || (targetDay.after(startDay) && targetDay.before(endDay)))
    }

    fun formatDisplayDate(cal: Calendar): String {
        return SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH).format(cal.time)
    }

    fun formatMonthYear(cal: Calendar): String {
        return SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(cal.time)
    }
}

/**
 * Interactive Persistent Stay Calendar Component for the User Profile.
 * Visualizes upcoming, active, and past stays across months.
 */
@Composable
fun PersistentStaysCalendar(
    bookings: List<Booking>,
    modifier: Modifier = Modifier,
    onViewBookingReceipt: (Booking) -> Unit = {},
    onCancelBooking: (Booking) -> Unit = {},
    onExploreStaysClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Calendar state: Initialized around August 2026
    val currentMonthCalendar = remember {
        Calendar.getInstance().apply {
            // Anchor to current reference date (August 2026)
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.AUGUST)
            set(Calendar.DAY_OF_MONTH, 28)
        }
    }

    var displayedYear by remember { mutableIntStateOf(currentMonthCalendar.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableIntStateOf(currentMonthCalendar.get(Calendar.MONTH)) }

    // Selected date for preview (defaults to today / Aug 28, 2026)
    val todayCal = remember {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.AUGUST)
            set(Calendar.DAY_OF_MONTH, 28)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    var selectedCalendarDate by remember {
        mutableStateOf(todayCal.clone() as Calendar)
    }

    // Filter mode: "ALL", "UPCOMING", "PAST"
    var stayFilterMode by remember { mutableStateOf("ALL") }

    // Persistent stay notes & reminders stored in component state / persistent store
    val stayReminders = remember { mutableStateMapOf<String, Boolean>() }
    var showReminderDialogForBooking by remember { mutableStateOf<Booking?>(null) }

    // Precalculate bookings with parsed dates
    val parsedBookings = remember(bookings) {
        bookings.map { booking ->
            val startCal = StayDateHelper.parseDate(booking.checkInDate) ?: Calendar.getInstance().apply {
                timeInMillis = booking.bookedAtTimestamp
            }
            val endCal = StayDateHelper.parseDate(booking.checkOutDate) ?: (startCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, 1)
            }
            ParsedBookingItem(booking, startCal, endCal)
        }
    }

    // Filter bookings based on stayFilterMode
    val filteredBookings = remember(parsedBookings, stayFilterMode) {
        when (stayFilterMode) {
            "UPCOMING" -> parsedBookings.filter {
                it.booking.status == BookingStatus.UPCOMING || it.booking.status == BookingStatus.ACTIVE
            }
            "PAST" -> parsedBookings.filter {
                it.booking.status == BookingStatus.COMPLETED || it.booking.status == BookingStatus.CANCELLED
            }
            else -> parsedBookings
        }
    }

    // Calendar month calculations
    val calendarDays = remember(displayedYear, displayedMonth, filteredBookings) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        // Convert SUNDAY (1) -> 6, MONDAY (2) -> 0, etc. for Monday-based week start
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val leadingEmptyDays = (firstDayOfWeek - Calendar.MONDAY + 7) % 7

        val list = mutableListOf<CalendarDayInfo>()

        // Add leading days from previous month
        val prevMonthCal = (cal.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
        val maxPrevDays = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in (maxPrevDays - leadingEmptyDays + 1)..maxPrevDays) {
            val dayCal = (prevMonthCal.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, i) }
            val matchingStays = filteredBookings.filter { StayDateHelper.isDateBetweenInclusive(dayCal, it.startCal, it.endCal) }
            list.add(CalendarDayInfo(dayCal, isCurrentMonth = false, matchingStays = matchingStays))
        }

        // Add current month days
        for (day in 1..daysInMonth) {
            val dayCal = (cal.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
            val matchingStays = filteredBookings.filter { StayDateHelper.isDateBetweenInclusive(dayCal, it.startCal, it.endCal) }
            list.add(CalendarDayInfo(dayCal, isCurrentMonth = true, matchingStays = matchingStays))
        }

        // Trailing days to fill 7-col grid (up to multiple of 7)
        val remaining = (7 - (list.size % 7)) % 7
        val nextMonthCal = (cal.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
        for (day in 1..remaining) {
            val dayCal = (nextMonthCal.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, day) }
            val matchingStays = filteredBookings.filter { StayDateHelper.isDateBetweenInclusive(dayCal, it.startCal, it.endCal) }
            list.add(CalendarDayInfo(dayCal, isCurrentMonth = false, matchingStays = matchingStays))
        }

        list
    }

    // Stays matching the currently selected day
    val selectedDayStays = remember(selectedCalendarDate, filteredBookings) {
        filteredBookings.filter {
            StayDateHelper.isDateBetweenInclusive(selectedCalendarDate, it.startCal, it.endCal)
        }
    }

    // Counts for stat bar
    val upcomingCount = remember(parsedBookings) {
        parsedBookings.count { it.booking.status == BookingStatus.UPCOMING || it.booking.status == BookingStatus.ACTIVE }
    }
    val completedCount = remember(parsedBookings) {
        parsedBookings.count { it.booking.status == BookingStatus.COMPLETED }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("persistent_stays_calendar")
    ) {
        // Top Card Container for Month Navigator & Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header Row: Month / Year Title + Prev / Next Navigators + Today Jump
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayedCal = remember(displayedYear, displayedMonth) {
                        Calendar.getInstance().apply {
                            set(Calendar.YEAR, displayedYear)
                            set(Calendar.MONTH, displayedMonth)
                            set(Calendar.DAY_OF_MONTH, 1)
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Calendar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = StayDateHelper.formatMonthYear(displayedCal),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Stay schedule & check-in timeline",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Quick Jump to Current Month
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    displayedYear = todayCal.get(Calendar.YEAR)
                                    displayedMonth = todayCal.get(Calendar.MONTH)
                                    selectedCalendarDate = todayCal.clone() as Calendar
                                }
                                .testTag("btn_calendar_today")
                        ) {
                            Text(
                                text = "Today",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Previous Month Button
                        IconButton(
                            onClick = {
                                if (displayedMonth == Calendar.JANUARY) {
                                    displayedMonth = Calendar.DECEMBER
                                    displayedYear -= 1
                                } else {
                                    displayedMonth -= 1
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .testTag("btn_calendar_prev_month")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Month",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Next Month Button
                        IconButton(
                            onClick = {
                                if (displayedMonth == Calendar.DECEMBER) {
                                    displayedMonth = Calendar.JANUARY
                                    displayedYear += 1
                                } else {
                                    displayedMonth += 1
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .testTag("btn_calendar_next_month")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Month",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Stay Status Summary & Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = stayFilterMode == "ALL",
                        onClick = { stayFilterMode = "ALL" },
                        label = { Text("All Stays (${bookings.size})", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(13.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = stayFilterMode == "UPCOMING",
                        onClick = { stayFilterMode = "UPCOMING" },
                        label = { Text("Upcoming ($upcomingCount)", fontSize = 11.sp) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StynoBluePrimary)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StynoBluePrimary,
                            selectedLabelColor = Color.White
                        )
                    )

                    FilterChip(
                        selected = stayFilterMode == "PAST",
                        onClick = { stayFilterMode = "PAST" },
                        label = { Text("Past ($completedCount)", fontSize = 11.sp) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF64748B))
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF475569),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Days of the Week Header (Mon - Sun)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { index, dayLetter ->
                        val isWeekend = index >= 5
                        Text(
                            text = dayLetter,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isWeekend) StynoAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Day Cells Grid (7 columns per row)
                val rows = calendarDays.chunked(7)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rows.forEach { rowDays ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            rowDays.forEach { dayInfo ->
                                CalendarDayCell(
                                    dayInfo = dayInfo,
                                    isToday = StayDateHelper.isSameDay(dayInfo.dateCal, todayCal),
                                    isSelected = StayDateHelper.isSameDay(dayInfo.dateCal, selectedCalendarDate),
                                    onClick = {
                                        selectedCalendarDate = dayInfo.dateCal.clone() as Calendar
                                        // If clicked an out-of-month date, smoothly change month view as well
                                        if (!dayInfo.isCurrentMonth) {
                                            displayedYear = dayInfo.dateCal.get(Calendar.YEAR)
                                            displayedMonth = dayInfo.dateCal.get(Calendar.MONTH)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Calendar Stay Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarLegendItem(color = StynoEmerald, label = "Active Stay")
                    CalendarLegendItem(color = StynoBluePrimary, label = "Upcoming")
                    CalendarLegendItem(color = Color(0xFF64748B), label = "Past Stay")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Date Header & Stay Details Preview
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Selected Date",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = StayDateHelper.formatDisplayDate(selectedCalendarDate),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (selectedDayStays.isNotEmpty()) "${selectedDayStays.size} scheduled stay(s)" else "No scheduled stay",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selectedDayStays.isNotEmpty()) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (selectedDayStays.isEmpty()) {
                        // Quick jump to nearest upcoming stay
                        val nextUpcoming = parsedBookings
                            .filter { it.booking.status == BookingStatus.UPCOMING || it.booking.status == BookingStatus.ACTIVE }
                            .minByOrNull { it.startCal.timeInMillis }

                        if (nextUpcoming != null) {
                            TextButton(
                                onClick = {
                                    selectedCalendarDate = nextUpcoming.startCal.clone() as Calendar
                                    displayedYear = nextUpcoming.startCal.get(Calendar.YEAR)
                                    displayedMonth = nextUpcoming.startCal.get(Calendar.MONTH)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Jump to Next Stay ➔",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // If stays exist on selected date, display detailed stay card
                if (selectedDayStays.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    selectedDayStays.forEach { parsedItem ->
                        val booking = parsedItem.booking
                        val isReminderSet = stayReminders[booking.id] == true

                        SelectedDayStayCard(
                            booking = booking,
                            isReminderSet = isReminderSet,
                            onToggleReminder = {
                                val current = stayReminders[booking.id] == true
                                stayReminders[booking.id] = !current
                                Toast.makeText(
                                    context,
                                    if (!current) "Stay check-in reminder saved to calendar!" else "Reminder removed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onViewReceipt = { onViewBookingReceipt(booking) },
                            onCancelBooking = { onCancelBooking(booking) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No accommodations booked for this day. Select a marked date on the calendar above or book a new verified stay.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // All Stays Timeline Section (Chronological Breakdown)
        Text(
            text = "STAY SCHEDULE TIMELINE (${filteredBookings.size})",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (filteredBookings.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Hotel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No stays matching current filter",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Browse hostels, PGs, hotels, and quick stays to schedule your next visit.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onExploreStaysClick,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Browse Verified Accommodations")
                    }
                }
            }
        } else {
            // Display Chronological Timeline items
            filteredBookings.sortedBy { it.startCal.timeInMillis }.forEach { parsedItem ->
                CalendarTimelineStayItem(
                    parsedItem = parsedItem,
                    isSelected = StayDateHelper.isSameDay(parsedItem.startCal, selectedCalendarDate) ||
                            StayDateHelper.isDateBetweenInclusive(selectedCalendarDate, parsedItem.startCal, parsedItem.endCal),
                    onClick = {
                        selectedCalendarDate = parsedItem.startCal.clone() as Calendar
                        displayedYear = parsedItem.startCal.get(Calendar.YEAR)
                        displayedMonth = parsedItem.startCal.get(Calendar.MONTH)
                    },
                    onViewReceipt = { onViewBookingReceipt(parsedItem.booking) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Single Calendar Day Cell displaying date number and stay indicator badges.
 */
@Composable
private fun CalendarDayCell(
    dayInfo: CalendarDayInfo,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNumber = dayInfo.dateCal.get(Calendar.DAY_OF_MONTH)
    val hasStays = dayInfo.matchingStays.isNotEmpty()

    // Determine highest priority stay status for coloring
    val primaryStay = dayInfo.matchingStays.firstOrNull()
    val hasActiveStay = dayInfo.matchingStays.any { it.booking.status == BookingStatus.ACTIVE }
    val hasUpcomingStay = dayInfo.matchingStays.any { it.booking.status == BookingStatus.UPCOMING }
    val hasCompletedStay = dayInfo.matchingStays.any { it.booking.status == BookingStatus.COMPLETED }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    hasStays && dayInfo.isCurrentMonth -> when {
                        hasActiveStay -> StynoEmerald.copy(alpha = 0.15f)
                        hasUpcomingStay -> StynoBluePrimary.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                    isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else -> Color.Transparent
                }
            )
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                } else if (hasStays && !isSelected && dayInfo.isCurrentMonth) {
                    Modifier.border(
                        1.dp,
                        if (hasActiveStay) StynoEmerald.copy(alpha = 0.5f) else StynoBluePrimary.copy(alpha = 0.4f),
                        RoundedCornerShape(10.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayNumber.toString(),
                fontSize = 12.sp,
                fontWeight = if (isSelected || isToday || hasStays) FontWeight.ExtraBold else FontWeight.Normal,
                color = when {
                    isSelected -> Color.White
                    !dayInfo.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    isToday -> MaterialTheme.colorScheme.primary
                    hasActiveStay -> StynoEmerald
                    hasUpcomingStay -> StynoBluePrimary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Stay Indicator dots
            if (hasStays) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    dayInfo.matchingStays.take(3).forEach { stay ->
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> Color.White
                                        stay.booking.status == BookingStatus.ACTIVE -> StynoEmerald
                                        stay.booking.status == BookingStatus.UPCOMING -> StynoBluePrimary
                                        stay.booking.status == BookingStatus.COMPLETED -> Color(0xFF64748B)
                                        else -> Color(0xFFEF4444)
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Rich Card for the stay falling on the selected calendar date.
 */
@Composable
private fun SelectedDayStayCard(
    booking: Booking,
    isReminderSet: Boolean,
    onToggleReminder: () -> Unit,
    onViewReceipt: () -> Unit,
    onCancelBooking: () -> Unit
) {
    val context = LocalContext.current

    val imageRes = when (booking.propertyImage) {
        "img_hostel_modern" -> R.drawable.img_hostel_modern
        "img_pg_room" -> R.drawable.img_pg_room
        "img_hotel_suite" -> R.drawable.img_hotel_suite
        "img_apartment_flat" -> R.drawable.img_apartment_flat
        else -> R.drawable.img_hostel_modern
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = booking.propertyName,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (booking.status) {
                                BookingStatus.ACTIVE -> StynoEmerald.copy(alpha = 0.15f)
                                BookingStatus.UPCOMING -> StynoBluePrimary.copy(alpha = 0.15f)
                                BookingStatus.COMPLETED -> Color(0xFF64748B).copy(alpha = 0.15f)
                                BookingStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                            }
                        ) {
                            Text(
                                text = booking.status.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (booking.status) {
                                    BookingStatus.ACTIVE -> StynoEmerald
                                    BookingStatus.UPCOMING -> StynoBluePrimary
                                    BookingStatus.COMPLETED -> Color(0xFF475569)
                                    BookingStatus.CANCELLED -> MaterialTheme.colorScheme.error
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = booking.id,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = booking.propertyName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${booking.propertyType.displayName} • ${booking.roomTypeName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            // Check-in & Check-out dates and times
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Check-In", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${booking.checkInDate} (${booking.checkInTime})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Check-Out", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = booking.checkOutDate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Passcode & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Digital Passcode Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Gate Passcode", booking.digitalPasscode))
                            Toast.makeText(context, "Passcode ${booking.digitalPasscode} copied!", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Key, contentDescription = "Passcode", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Passcode: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(booking.digitalPasscode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(11.dp))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Stay Reminder Toggle
                    IconButton(
                        onClick = onToggleReminder,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isReminderSet) Icons.Default.NotificationsActive else Icons.Default.Alarm,
                            contentDescription = "Stay Reminder",
                            tint = if (isReminderSet) StynoAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Host Call Button
                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${booking.propertyContactPhone}"))
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Host: ${booking.propertyContactPhone}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Host",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // View Receipt / Pass Button
                    OutlinedButton(
                        onClick = onViewReceipt,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Chronological Stay Item for Timeline view.
 */
@Composable
private fun CalendarTimelineStayItem(
    parsedItem: ParsedBookingItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onViewReceipt: () -> Unit
) {
    val booking = parsedItem.booking
    val isUpcomingOrActive = booking.status == BookingStatus.UPCOMING || booking.status == BookingStatus.ACTIVE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date Badge on Left
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when (booking.status) {
                            BookingStatus.ACTIVE -> StynoEmerald.copy(alpha = 0.15f)
                            BookingStatus.UPCOMING -> StynoBluePrimary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = parsedItem.startCal.get(Calendar.DAY_OF_MONTH).toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when (booking.status) {
                            BookingStatus.ACTIVE -> StynoEmerald
                            BookingStatus.UPCOMING -> StynoBluePrimary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = SimpleDateFormat("MMM", Locale.ENGLISH).format(parsedItem.startCal.time).uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = booking.propertyName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (booking.status) {
                            BookingStatus.ACTIVE -> StynoEmerald.copy(alpha = 0.15f)
                            BookingStatus.UPCOMING -> StynoBluePrimary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = booking.status.displayName,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (booking.status) {
                                BookingStatus.ACTIVE -> StynoEmerald
                                BookingStatus.UPCOMING -> StynoBluePrimary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${booking.checkInDate} ➔ ${booking.checkOutDate} • ${booking.durationText}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${booking.address} • Passcode: ${booking.digitalPasscode}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onViewReceipt,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = "View Pass",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CalendarLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Internal parsed data models for calendar logic.
 */
data class ParsedBookingItem(
    val booking: Booking,
    val startCal: Calendar,
    val endCal: Calendar
)

data class CalendarDayInfo(
    val dateCal: Calendar,
    val isCurrentMonth: Boolean,
    val matchingStays: List<ParsedBookingItem>
)

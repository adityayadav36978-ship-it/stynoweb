package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import java.text.NumberFormat
import java.util.Locale

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun MyBookingsScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Upcoming", "Completed", "Cancelled")

    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }

    val filteredBookings = when (selectedTabIndex) {
        0 -> bookings.filter { it.status == BookingStatus.UPCOMING || it.status == BookingStatus.ACTIVE }
        1 -> bookings.filter { it.status == BookingStatus.COMPLETED }
        else -> bookings.filter { it.status == BookingStatus.CANCELLED }
    }

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
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = "My Stay Bookings",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        // Bookings List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (filteredBookings.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No ${tabTitles[selectedTabIndex].lowercase()} bookings found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Explore top hostels, rooms, PGs, hotels & quick stays on STYNO.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.navigateTo(Screen.HOME) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Find a Stay")
                        }
                    }
                }
            } else {
                items(filteredBookings) { booking ->
                    val imageResId = context.resources.getIdentifier(booking.propertyImage, "drawable", context.packageName)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_card_${booking.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Row: Status badge & ID
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (booking.status) {
                                        BookingStatus.UPCOMING -> StynoEmerald.copy(alpha = 0.15f)
                                        BookingStatus.ACTIVE -> StynoBluePrimary.copy(alpha = 0.15f)
                                        BookingStatus.COMPLETED -> Color(0xFFE2E8F0)
                                        BookingStatus.CANCELLED -> Color(0xFFFEE2E2)
                                    }
                                ) {
                                    Text(
                                        text = booking.status.displayName.uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (booking.status) {
                                            BookingStatus.UPCOMING -> StynoEmerald
                                            BookingStatus.ACTIVE -> StynoBluePrimary
                                            BookingStatus.COMPLETED -> Color(0xFF475569)
                                            BookingStatus.CANCELLED -> Color(0xFFDC2626)
                                        }
                                    )
                                }

                                Text(
                                    text = booking.id,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Property & Room details
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (imageResId != 0) {
                                    Image(
                                        painter = painterResource(id = imageResId),
                                        contentDescription = booking.propertyName,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(StynoBluePrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("S", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = booking.propertyName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = booking.roomTypeName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Dates: ${booking.checkInDate} • ${booking.durationText}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Passcode Pill (if upcoming/active)
                            if (booking.status == BookingStatus.UPCOMING || booking.status == BookingStatus.ACTIVE) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Key, contentDescription = "Gate Passcode icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "Gate Access Passcode:", style = MaterialTheme.typography.labelSmall)
                                        }
                                        Text(
                                            text = booking.digitalPasscode,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "₹${booking.finalAmount.toInt()}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (booking.status == BookingStatus.UPCOMING) {
                                        OutlinedButton(
                                            onClick = { bookingToCancel = booking },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Cancel", fontSize = 12.sp, color = Color(0xFFDC2626))
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(booking.propertyName + " " + booking.address)}")
                                            context.startActivity(Intent(Intent.ACTION_VIEW, mapUri))
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Directions, contentDescription = "Get directions", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Directions", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.navigateTo(Screen.CHAT) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Message, contentDescription = "Chat with host", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Chat", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Cancellation Dialog
    if (bookingToCancel != null) {
        val target = bookingToCancel!!
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            title = { Text("Cancel Stay Booking?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to cancel booking ${target.id} at ${target.propertyName}?\n\nEligible refund of ₹${target.finalAmount.toInt()} will be initiated to your original payment method (${target.paymentMethod}) within 24 hours."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelBooking(target.id)
                        bookingToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Confirm Cancellation")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) {
                    Text("Keep Booking")
                }
            }
        )
    }
}

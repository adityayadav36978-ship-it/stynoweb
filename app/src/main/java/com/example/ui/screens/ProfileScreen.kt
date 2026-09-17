package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.ProfileSyncStatus
import com.example.data.util.ImageStorageHelper
import com.example.ui.components.CameraDocumentPickerSheet
import com.example.ui.components.CaptureMode
import com.example.ui.components.PersistentStaysCalendar
import com.example.ui.components.StynoAppLogo
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoAmber
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import com.example.ui.viewmodel.UserRole
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val profileSyncStatus by viewModel.profileSyncStatus.collectAsStateWithLifecycle()
    val profileSyncMessage by viewModel.profileSyncMessage.collectAsStateWithLifecycle()
    val wishlistSyncState by viewModel.wishlistSyncState.collectAsStateWithLifecycle()

    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userPhone by viewModel.userPhone.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val currentRole by viewModel.userRole.collectAsStateWithLifecycle()
    val isKycVerified by viewModel.isKycVerified.collectAsStateWithLifecycle()
    val kycDocType by viewModel.kycDocType.collectAsStateWithLifecycle()
    val kycMaskedId by viewModel.kycMaskedId.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val complaints by viewModel.complaints.collectAsStateWithLifecycle()
    val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
    val wishlistCollections by viewModel.wishlistCollectionsWithCounts.collectAsStateWithLifecycle()

    // Room Database Offline State
    val isSimulatingOffline by viewModel.isSimulatingOfflineMode.collectAsStateWithLifecycle()
    val cachedPropertyCount by viewModel.cachedPropertyCount.collectAsStateWithLifecycle()
    val frequentlyAccessed by viewModel.frequentlyAccessedProperties.collectAsStateWithLifecycle()
    val recentCachedSearches by viewModel.recentCachedSearches.collectAsStateWithLifecycle()
    val cacheActionMessage by viewModel.cacheActionMessage.collectAsStateWithLifecycle()

    var selectedTopTab by remember { mutableIntStateOf(0) } // 0: Profile, 1: Bookings, 2: Wishlist, 3: Settings
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showProfilePhotoCameraSheet by remember { mutableStateOf(false) }
    var showDocCameraSheet by remember { mutableStateOf(false) }
    var selectedBookingForDetail by remember { mutableStateOf<Booking?>(null) }
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }
    var bookingStatusFilter by remember { mutableStateOf("ALL") }
    var bookingsTabSubMode by remember { mutableStateOf("CALENDAR") } // "CALENDAR" or "LIST"

    // Settings local toggles
    var notifyBookings by remember { mutableStateOf(true) }
    var notifyPriceDrops by remember { mutableStateOf(true) }
    var notifyCurfew by remember { mutableStateOf(true) }
    var biometricCheckIn by remember { mutableStateOf(true) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    // Camera Sheets for Profile and Documents
    if (showProfilePhotoCameraSheet) {
        CameraDocumentPickerSheet(
            mode = CaptureMode.PROFILE_PHOTO,
            title = "Update Profile Photo",
            onDismiss = { showProfilePhotoCameraSheet = false },
            onImageCaptured = { _, base64, localPath ->
                viewModel.uploadProfilePhoto(base64, localPath)
                showProfilePhotoCameraSheet = false
                Toast.makeText(context, "Profile photo updated & synced to Firestore!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showDocCameraSheet) {
        CameraDocumentPickerSheet(
            mode = CaptureMode.DOCUMENT_SCAN,
            title = "Scan ID Document",
            documentType = kycDocType.ifEmpty { "Aadhaar Card" },
            onDismiss = { showDocCameraSheet = false },
            onImageCaptured = { _, base64, localPath ->
                viewModel.uploadVerificationDocument(
                    docType = kycDocType.ifEmpty { "Aadhaar Card" },
                    docNumberMasked = kycMaskedId.ifEmpty { "XXXX-XXXX-4812" },
                    base64 = base64,
                    localPath = localPath
                )
                showDocCameraSheet = false
                Toast.makeText(context, "Document scanned & uploaded to Firestore storage!", Toast.LENGTH_LONG).show()
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Top Bar with Live Firestore Sync Indicator
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Resident Profile",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pulls live data & bookings from Cloud Firestore",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Firestore Cloud Sync status badge & refresh trigger
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = when (profileSyncStatus) {
                            ProfileSyncStatus.SYNCING -> MaterialTheme.colorScheme.primaryContainer
                            ProfileSyncStatus.SYNCED -> StynoEmerald.copy(alpha = 0.15f)
                            ProfileSyncStatus.OFFLINE -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.refreshUserProfileFromFirestore()
                                viewModel.syncBookingsWithFirestore()
                                Toast.makeText(context, "Syncing profile with Firestore...", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (profileSyncStatus) {
                                ProfileSyncStatus.SYNCING -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Syncing...",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                ProfileSyncStatus.SYNCED -> {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = "Cloud Synced",
                                        tint = StynoEmerald,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Firestore Synced",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StynoEmerald
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = "Sync",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Sync Cloud",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Top Segment Navigation Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTopTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 8.dp
                ) {
                    Tab(
                        selected = selectedTopTab == 0,
                        onClick = { selectedTopTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Profile", fontWeight = if (selectedTopTab == 0) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_profile")
                    )
                    Tab(
                        selected = selectedTopTab == 1,
                        onClick = { selectedTopTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Bookings (${bookings.size})", fontWeight = if (selectedTopTab == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_bookings")
                    )
                    Tab(
                        selected = selectedTopTab == 2,
                        onClick = { selectedTopTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Wishlist (${wishlistItems.size})", fontWeight = if (selectedTopTab == 2) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_wishlist")
                    )
                    Tab(
                        selected = selectedTopTab == 3,
                        onClick = { selectedTopTab = 3 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Settings", fontWeight = if (selectedTopTab == 3) FontWeight.Bold else FontWeight.Normal)
                            }
                        },
                        modifier = Modifier.testTag("tab_settings")
                    )
                }
            }
        }

        // Tab Content
        if (selectedTopTab == 0) {
            // TAB 0: PROFILE & PERSONAL CONTACT INFO
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main User Hero Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val profileBitmap = remember(userProfile.profilePhotoBase64) {
                                    ImageStorageHelper.base64ToBitmap(userProfile.profilePhotoBase64.orEmpty())
                                }

                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clickable { showProfilePhotoCameraSheet = true }
                                        .testTag("btn_change_avatar_camera"),
                                    contentAlignment = Alignment.BottomEnd
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, StynoBluePrimary, CircleShape)
                                            .background(
                                                Brush.linearGradient(listOf(StynoBluePrimary, StynoBlueLight))
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (profileBitmap != null) {
                                            Image(
                                                bitmap = profileBitmap.asImageBitmap(),
                                                contentDescription = "Profile Photo",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Text(
                                                text = (userProfile.fullName.ifBlank { userName }).take(1).uppercase(),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 28.sp
                                            )
                                        }
                                    }

                                    // Camera Badge Overlay
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(StynoBluePrimary)
                                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Update Profile Photo",
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = userProfile.fullName.ifBlank { userName },
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = "KYC Verified",
                                            tint = StynoEmerald,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Text(
                                        text = userProfile.phoneNumber.ifBlank { userPhone },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = userProfile.email.ifBlank { userEmail },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showEditProfileDialog = true },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("edit_profile_btn")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Key Stat Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatBadgeItem(
                                    title = "Stays Booked",
                                    value = "${bookings.size}",
                                    icon = Icons.Default.CalendarMonth,
                                    onClick = { selectedTopTab = 1 }
                                )
                                StatBadgeItem(
                                    title = "KYC Status",
                                    value = "Verified ✓",
                                    icon = Icons.Default.Shield,
                                    color = StynoEmerald,
                                    onClick = { viewModel.setShowKycDialog(true) }
                                )
                                StatBadgeItem(
                                    title = "Member Since",
                                    value = userProfile.joinedDate,
                                    icon = Icons.Default.Badge,
                                    onClick = {}
                                )
                            }
                        }
                    }
                }

                // Persistent Calendar View & Stay Schedule Visualization
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MY STAYS CALENDAR & SCHEDULE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = {
                                    selectedTopTab = 1
                                    bookingsTabSubMode = "CALENDAR"
                                },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "Full Calendar ➔",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        PersistentStaysCalendar(
                            bookings = bookings,
                            onViewBookingReceipt = { selectedBookingForDetail = it },
                            onCancelBooking = { bookingToCancel = it },
                            onExploreStaysClick = { viewModel.navigateTo(Screen.HOME) }
                        )
                    }
                }

                // Personal Contact Information Card (Firestore Data-Driven)
                item {
                    Text(
                        text = "PERSONAL & CONTACT DETAILS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ContactDetailRow(
                                icon = Icons.Default.Person,
                                label = "Full Name",
                                value = userProfile.fullName.ifBlank { userName }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            ContactDetailRow(
                                icon = Icons.Default.Phone,
                                label = "Primary Mobile",
                                value = userProfile.phoneNumber.ifBlank { userPhone }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            ContactDetailRow(
                                icon = Icons.Default.Call,
                                label = "Alternate / WhatsApp",
                                value = userProfile.alternatePhone.ifBlank { "+91 91234 56789" }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            ContactDetailRow(
                                icon = Icons.Default.Email,
                                label = "Email Address",
                                value = userProfile.email.ifBlank { userEmail }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            ContactDetailRow(
                                icon = Icons.Default.School,
                                label = "College / Workplace",
                                value = userProfile.collegeOrWorkplace.ifBlank { "Galgotias University & Tech Park" }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            ContactDetailRow(
                                icon = Icons.Default.ContactEmergency,
                                label = "Emergency Contact (Parent/Guardian)",
                                value = "${userProfile.emergencyContactName} • ${userProfile.emergencyContactPhone}"
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            ContactDetailRow(
                                icon = Icons.Default.Home,
                                label = "Permanent Address",
                                value = userProfile.permanentAddress.ifBlank { "Sector 62, Noida, Delhi NCR, 201309" }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            ContactDetailRow(
                                icon = Icons.Default.Bloodtype,
                                label = "Blood Group (Hostel Medical Record)",
                                value = userProfile.bloodGroup.ifBlank { "O+" }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            ContactDetailRow(
                                icon = Icons.Default.Info,
                                label = "Resident Bio / Preferences",
                                value = userProfile.bio.ifBlank { "Student seeking quiet study atmosphere with Wi-Fi and home-style meals." }
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_edit_contact_details"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Edit Personal & Contact Information")
                            }
                        }
                    }
                }

                // Government KYC & Document Storage Card (Firestore Direct Sync)
                item {
                    Text(
                        text = "GOVERNMENT KYC & DOCUMENTS (FIRESTORE)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(StynoEmerald.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = StynoEmerald,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${userProfile.kycDocType.ifEmpty { kycDocType.ifEmpty { "Aadhaar Card" } }} Verified",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "ID: ${userProfile.kycMaskedId.ifEmpty { kycMaskedId.ifEmpty { "XXXX-XXXX-4812" } }}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StynoEmerald.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = StynoEmerald,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Verified ✓",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StynoEmerald
                                        )
                                    }
                                }
                            }

                            val docBitmap = remember(userProfile.kycDocImageBase64) {
                                ImageStorageHelper.base64ToBitmap(userProfile.kycDocImageBase64.orEmpty())
                            }

                            if (docBitmap != null) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, StynoEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            bitmap = docBitmap.asImageBitmap(),
                                            contentDescription = "Document Scan",
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Scanned Document Attached",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (!userProfile.kycDocUploadedAt.isNullOrBlank()) "Uploaded ${userProfile.kycDocUploadedAt}" else "Cloud Firestore Protected",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showDocCameraSheet = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_scan_kyc_camera"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Scan with Camera", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { viewModel.setShowKycDialog(true) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_manage_kyc_dialog"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Update Details", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Application Role Switcher
                item {
                    Text(
                        text = "ACCOUNT TYPE & ACCESS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                UserRole.entries.forEach { role ->
                                    val isSelected = currentRole == role
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { viewModel.setUserRole(role) }
                                            .testTag("role_${role.name.lowercase()}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = when (role) {
                                                    UserRole.GUEST -> Icons.Default.Person
                                                    UserRole.OWNER -> Icons.Default.HomeWork
                                                    UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                                },
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = when (role) {
                                                    UserRole.GUEST -> "Guest"
                                                    UserRole.OWNER -> "Host / Owner"
                                                    UserRole.ADMIN -> "Admin"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Navigation Shortcuts
                item {
                    Text(
                        text = "ACCOMMODATION & SERVICES",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            ProfileMenuRow(
                                icon = Icons.Default.CalendarMonth,
                                title = "View All Bookings & Passcodes",
                                subtitle = "Active, upcoming and past stay history"
                            ) {
                                selectedTopTab = 1
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp))
                            ProfileMenuRow(
                                icon = Icons.Default.Bookmark,
                                title = "Saved & Wishlist Stays",
                                subtitle = "Compare prices, room sharing and food menus"
                            ) {
                                viewModel.navigateTo(Screen.SAVED)
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp))
                            ProfileMenuRow(
                                icon = Icons.Default.VerifiedUser,
                                title = if (isKycVerified) "Government ID Verified ($kycDocType)" else "Verify ID for Fast Check-In",
                                subtitle = if (isKycVerified) "ID: $kycMaskedId (Masked & Encrypted)" else "Aadhaar, Passport, Driving License"
                            ) {
                                viewModel.setShowKycDialog(true)
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp))
                            ProfileMenuRow(
                                icon = Icons.Default.Shield,
                                title = "Safety & Emergency SOS Center",
                                subtitle = "24/7 Helplines & Rapid SOS Dispatch"
                            ) {
                                viewModel.navigateTo(Screen.SAFETY_CENTER)
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp))
                            ProfileMenuRow(
                                icon = Icons.Default.AutoAwesome,
                                title = "STYNO AI Concierge",
                                subtitle = "Instant answers on security deposits, curfew, meals"
                            ) {
                                viewModel.setShowAiSupportSheet(true)
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp))
                            ProfileMenuRow(
                                icon = Icons.Default.Tune,
                                title = "Stay & Personalization Preferences",
                                subtitle = "Update your location, purposes, stay types & room sharing"
                            ) {
                                viewModel.reopenOnboardingPreferences()
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp))
                            ProfileMenuRow(
                                icon = Icons.Default.FlashOn,
                                title = "Instant Action Complaints (${complaints.size})",
                                subtitle = "Report maintenance, Wi-Fi or canteen issues"
                            ) {
                                viewModel.openComplaintDialog(null)
                            }
                        }
                    }
                }

                // Room Database & Offline Cache Center Card
                item {
                    Text(
                        text = "ROOM DATABASE & OFFLINE CACHE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0284C7).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Storage,
                                            contentDescription = "Room DB",
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Room SQLite Cache",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Offline property storage & search index",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSimulatingOffline) StynoAccent.copy(alpha = 0.2f) else StynoEmerald.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isSimulatingOffline) Icons.Default.WifiOff else Icons.Default.Wifi,
                                            contentDescription = null,
                                            tint = if (isSimulatingOffline) StynoAccent else StynoEmerald,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isSimulatingOffline) "Offline Mode" else "Online Active",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSimulatingOffline) StynoAccent else StynoEmerald
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Cache Metrics Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "$cachedPropertyCount",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Cached Stays",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${frequentlyAccessed.size}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = StynoAccent
                                        )
                                        Text(
                                            text = "Frequently Viewed",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${recentCachedSearches.size}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = StynoEmerald
                                        )
                                        Text(
                                            text = "Offline Searches",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            if (!cacheActionMessage.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = cacheActionMessage.orEmpty(),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.preCacheAllStaysForOffline { count ->
                                            Toast.makeText(context, "Saved $count stays into Room database!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_precache_all_stays"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.OfflinePin, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pre-Cache All", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.toggleOfflineModeSimulation()
                                        val nowOffline = !isSimulatingOffline
                                        Toast.makeText(
                                            context,
                                            if (nowOffline) "Switched to Simulated Offline Mode" else "Switched to Live Online Mode",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_test_offline_mode"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSimulatingOffline) Icons.Default.Wifi else Icons.Default.WifiOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isSimulatingOffline) "Go Online" else "Test Offline", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    viewModel.clearOfflineCache {
                                        Toast.makeText(context, "Room cache cleared successfully", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_clear_room_cache"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Clear Local Room Cache", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Account Sign Out / Switch Session
                item {
                    OutlinedButton(
                        onClick = { showLogoutConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_logout_profile"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out / Switch Account", fontWeight = FontWeight.SemiBold)
                    }
                }

                // Cloud Status & Version & Official Branding
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        StynoAppLogo(
                            size = 48.dp,
                            cornerRadius = 12.dp,
                            elevation = 2.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cloud Sync: $profileSyncMessage",
                            style = MaterialTheme.typography.labelSmall,
                            color = StynoEmerald
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "STYNO Real-Estate & Accommodation Platform v1.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else if (selectedTopTab == 1) {
            // TAB 1: BOOKING HISTORY & STAYS WITH CALENDAR VIEW & LIST VIEW
            val filteredBookings = when (bookingStatusFilter) {
                "UPCOMING" -> bookings.filter { it.status == BookingStatus.UPCOMING || it.status == BookingStatus.ACTIVE }
                "COMPLETED" -> bookings.filter { it.status == BookingStatus.COMPLETED }
                "CANCELLED" -> bookings.filter { it.status == BookingStatus.CANCELLED }
                else -> bookings
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // View Mode Switcher: Calendar Schedule vs List View
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { bookingsTabSubMode = "CALENDAR" }
                                    .testTag("tab_bookings_calendar_mode"),
                                shape = RoundedCornerShape(10.dp),
                                color = if (bookingsTabSubMode == "CALENDAR") MaterialTheme.colorScheme.primary else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Calendar View",
                                        tint = if (bookingsTabSubMode == "CALENDAR") Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Calendar Schedule",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (bookingsTabSubMode == "CALENDAR") Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { bookingsTabSubMode = "LIST" }
                                    .testTag("tab_bookings_list_mode"),
                                shape = RoundedCornerShape(10.dp),
                                color = if (bookingsTabSubMode == "LIST") MaterialTheme.colorScheme.primary else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Receipt,
                                        contentDescription = "List View",
                                        tint = if (bookingsTabSubMode == "LIST") Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "List View (${bookings.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (bookingsTabSubMode == "LIST") Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                if (bookingsTabSubMode == "CALENDAR") {
                    item {
                        PersistentStaysCalendar(
                            bookings = bookings,
                            onViewBookingReceipt = { selectedBookingForDetail = it },
                            onCancelBooking = { bookingToCancel = it },
                            onExploreStaysClick = { viewModel.navigateTo(Screen.HOME) }
                        )
                    }
                } else {
                    // Filter Chips Row for List View
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = bookingStatusFilter == "ALL",
                                onClick = { bookingStatusFilter = "ALL" },
                                label = { Text("All (${bookings.size})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = bookingStatusFilter == "UPCOMING",
                                onClick = { bookingStatusFilter = "UPCOMING" },
                                label = { Text("Upcoming (${bookings.count { it.status == BookingStatus.UPCOMING || it.status == BookingStatus.ACTIVE }})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = bookingStatusFilter == "COMPLETED",
                                onClick = { bookingStatusFilter = "COMPLETED" },
                                label = { Text("Completed (${bookings.count { it.status == BookingStatus.COMPLETED }})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = bookingStatusFilter == "CANCELLED",
                                onClick = { bookingStatusFilter = "CANCELLED" },
                                label = { Text("Cancelled (${bookings.count { it.status == BookingStatus.CANCELLED }})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    if (filteredBookings.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No Bookings Found in this Category",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Explore verified Hostels, PGs, Hotels and Quick Stays on the Home catalog.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { viewModel.navigateTo(Screen.HOME) },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Browse Stays")
                                    }
                                }
                            }
                        }
                    } else {
                        items(filteredBookings, key = { it.id }) { booking ->
                            BookingHistoryCard(
                                booking = booking,
                                onViewReceipt = { selectedBookingForDetail = booking },
                                onCancelBooking = { bookingToCancel = booking },
                                onContactHost = { phone ->
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Contact: $phone", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onChatWithHost = {
                                    viewModel.navigateTo(Screen.CHAT)
                                }
                            )
                        }
                    }
                }
            }
        } else if (selectedTopTab == 2) {
            // TAB 2: SAVED WISHLISTS & SHORTLISTED PROPERTIES (Firestore Synced)
            var selectedWishlistCategory by remember { mutableStateOf("All") }

            val filteredWishlist = when (selectedWishlistCategory) {
                "All" -> wishlistItems
                "Hostels" -> wishlistItems.filter { it.collectionName.contains("Hostel", ignoreCase = true) || it.property.propertyType.name.contains("HOSTEL") }
                "Work" -> wishlistItems.filter { it.collectionName.contains("Work", ignoreCase = true) }
                "Budget" -> wishlistItems.filter { it.collectionName.contains("Budget", ignoreCase = true) || it.property.startingPrice <= 7000 }
                else -> wishlistItems
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header card with Firestore Sync banner & shortcuts
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Saved Stay Wishlists",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${wishlistItems.size} properties synced across devices",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = StynoEmerald.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable {
                                            viewModel.syncWishlistWithFirestore()
                                            Toast.makeText(context, "Wishlists refreshed from Firestore", Toast.LENGTH_SHORT).show()
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = null,
                                            tint = StynoEmerald,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Cloud Synced",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StynoEmerald
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick Collection Categories
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("All" to "All (${wishlistItems.size})", "Hostels" to "🎓 Hostels", "Work" to "💼 Work", "Budget" to "💰 Budget").forEach { (key, label) ->
                                    FilterChip(
                                        selected = selectedWishlistCategory == key,
                                        onClick = { selectedWishlistCategory = key },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                if (filteredWishlist.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Wishlisted Stays in this Collection",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap the heart / bookmark icon on any property to save it to your Firestore cloud wishlist.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(Screen.HOME) },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Explore Accommodations")
                                }
                            }
                        }
                    }
                } else {
                    items(filteredWishlist, key = { it.property.id }) { wishlistItem ->
                        ProfileWishlistCard(
                            item = wishlistItem,
                            onOpenDetail = {
                                viewModel.openPropertyDetails(wishlistItem.property)
                            },
                            onRemove = {
                                viewModel.toggleSave(wishlistItem.property.id)
                                Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show()
                            },
                            onShare = {
                                val priceFormatted = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                                    .format(wishlistItem.property.startingPrice)
                                    .replace(".00", "")
                                val shareText = buildString {
                                    appendLine("🏡 ${wishlistItem.property.name}")
                                    appendLine("📍 ${wishlistItem.property.area}, ${wishlistItem.property.city}")
                                    appendLine("💰 Starting: $priceFormatted/month")
                                    appendLine("⭐ Rating: ${wishlistItem.property.rating}★")
                                    if (wishlistItem.userNotes.isNotBlank()) {
                                        appendLine("📝 My Notes: ${wishlistItem.userNotes}")
                                    }
                                    appendLine()
                                    appendLine("View on STYNO Accommodation:")
                                    append("https://styno.com/property/${wishlistItem.property.id}")
                                }

                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Check out ${wishlistItem.property.name} on STYNO")
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Wishlisted Stay via"))
                            }
                        )
                    }

                    item {
                        Button(
                            onClick = { viewModel.navigateTo(Screen.SAVED) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Full Wishlist & Comparison View")
                        }
                    }
                }
            }
        } else {
            // TAB 3: USER ACCOUNT SETTINGS (Firestore Persistent)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Identity & Government KYC Security
                item {
                    Text(
                        text = "IDENTITY & ACCOUNT SECURITY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = if (isKycVerified) StynoEmerald else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (isKycVerified) "Government KYC Verified" else "Complete KYC Verification",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isKycVerified) "$kycDocType ID • Masked ($kycMaskedId)" else "Required for instant passcode check-in",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = { viewModel.setShowKycDialog(true) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(if (isKycVerified) "View ID" else "Verify Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Biometric Check-In Setting
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Biometric / Fast Passcode Entry", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Text("Use fingerprint or PIN for instant check-in keys", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = biometricCheckIn,
                                    onCheckedChange = { biometricCheckIn = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }

                // Section 2: Notifications & Stay Alerts
                item {
                    Text(
                        text = "NOTIFICATIONS & RESIDENT ALERTS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Booking Notification
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Booking & Check-In Updates", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Text("Instant gate passcodes, check-in reminders, receipts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = notifyBookings,
                                    onCheckedChange = { notifyBookings = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Price Drop Alerts
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Price Drop & Flash Offers", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Text("Alerts when wishlisted hostel/PG rents drop", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = notifyPriceDrops,
                                    onCheckedChange = { notifyPriceDrops = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Curfew & Safety Warnings
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Curfew & Warden Gate Alerts", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Text("Night gate entry cutoff warnings & safety notices", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Switch(
                                    checked = notifyCurfew,
                                    onCheckedChange = { notifyCurfew = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }

                // Section 3: App & Regional Preferences
                item {
                    Text(
                        text = "APP & REGIONAL PREFERENCES",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Display Currency", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Text("Indian Rupee (₹ INR) • All Inclusive Pricing", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                ) {
                                    Text("₹ INR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Language", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Text("English (Indian Regional Dialects)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text("EN-IN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                // Section 4: Role Switcher
                item {
                    Text(
                        text = "ACCOUNT ACCESS TIER & ROLE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                UserRole.entries.forEach { role ->
                                    val isSelected = currentRole == role
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                viewModel.setUserRole(role)
                                                Toast.makeText(context, "Switched role to ${role.name}", Toast.LENGTH_SHORT).show()
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = when (role) {
                                                    UserRole.GUEST -> Icons.Default.Person
                                                    UserRole.OWNER -> Icons.Default.HomeWork
                                                    UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                                },
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = when (role) {
                                                    UserRole.GUEST -> "Resident"
                                                    UserRole.OWNER -> "Host / Owner"
                                                    UserRole.ADMIN -> "Admin"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 5: Firestore Cloud Data Management
                item {
                    Text(
                        text = "CLOUD FIRESTORE & DATA SYNC",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Sync All Button
                            Button(
                                onClick = {
                                    viewModel.refreshUserProfileFromFirestore()
                                    viewModel.syncBookingsWithFirestore()
                                    viewModel.syncWishlistWithFirestore()
                                    viewModel.refreshPropertiesFromFirestore()
                                    Toast.makeText(context, "Full Firestore Synchronization Complete!", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sync All Cloud Data (Firestore)")
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = { showResetConfirmDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reset / Clear Local Cache")
                            }
                        }
                    }
                }

                // Section 6: Account Sign Out & Session Management
                item {
                    Text(
                        text = "ACCOUNT SESSION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Currently signed in as ${userProfile.fullName.ifBlank { userName }} (${userProfile.email.ifBlank { userEmail }})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showLogoutConfirmDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_settings_logout"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Log Out of STYNO", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Footer version info
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Cloud Sync: $profileSyncMessage",
                            style = MaterialTheme.typography.labelSmall,
                            color = StynoEmerald
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "STYNO Real-Estate & Accommodation Platform v1.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // LOGOUT CONFIRMATION DIALOG
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            },
            title = { Text("Log Out of STYNO?", fontWeight = FontWeight.Bold) },
            text = {
                Text("You will be returned to the Welcome screen. Your profile and bookings will remain securely synced in Cloud Firestore.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        viewModel.logout()
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_logout_button")
                ) {
                    Text("Confirm Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Stay Signed In")
                }
            }
        )
    }

    // RESET CONFIRMATION DIALOG
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Local Cache?", fontWeight = FontWeight.Bold) },
            text = {
                Text("This will refresh local properties, wishlists, and bookings with cloud data from Firestore.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.refreshUserProfileFromFirestore()
                        viewModel.syncBookingsWithFirestore()
                        viewModel.syncWishlistWithFirestore()
                        showResetConfirmDialog = false
                        Toast.makeText(context, "Local cache reset & synced with Firestore", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirm Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // EDIT CONTACT PROFILE DIALOG (Firestore Sync)
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(userProfile.fullName.ifBlank { userName }) }
        var tempPhone by remember { mutableStateOf(userProfile.phoneNumber.ifBlank { userPhone }) }
        var tempAltPhone by remember { mutableStateOf(userProfile.alternatePhone) }
        var tempEmail by remember { mutableStateOf(userProfile.email.ifBlank { userEmail }) }
        var tempCollege by remember { mutableStateOf(userProfile.collegeOrWorkplace) }
        var tempEmergName by remember { mutableStateOf(userProfile.emergencyContactName) }
        var tempEmergPhone by remember { mutableStateOf(userProfile.emergencyContactPhone) }
        var tempAddress by remember { mutableStateOf(userProfile.permanentAddress) }
        var tempBloodGroup by remember { mutableStateOf(userProfile.bloodGroup) }
        var tempBio by remember { mutableStateOf(userProfile.bio) }

        var validationError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Contact & Personal Info", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Changes are saved to your account and synced with Cloud Firestore.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = { showProfilePhotoCameraSheet = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (!userProfile.profilePhotoBase64.isNullOrBlank()) "Change Profile Picture (Camera / Gallery)" else "Take / Upload Profile Picture")
                    }

                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Full Legal Name *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_name"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("Primary Mobile Number *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_phone"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = tempAltPhone,
                        onValueChange = { tempAltPhone = it },
                        label = { Text("Alternate / WhatsApp Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = tempEmail,
                        onValueChange = { tempEmail = it },
                        label = { Text("Email Address *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_email"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = tempCollege,
                        onValueChange = { tempCollege = it },
                        label = { Text("College / University / Workplace") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = tempEmergName,
                        onValueChange = { tempEmergName = it },
                        label = { Text("Emergency Contact (Parent/Guardian)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.ContactEmergency, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = tempEmergPhone,
                        onValueChange = { tempEmergPhone = it },
                        label = { Text("Emergency Contact Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = tempAddress,
                        onValueChange = { tempAddress = it },
                        label = { Text("Permanent Residential Address") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = tempBloodGroup,
                        onValueChange = { tempBloodGroup = it },
                        label = { Text("Blood Group") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Bloodtype, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = tempBio,
                        onValueChange = { tempBio = it },
                        label = { Text("Resident Bio & Preferences") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                    )

                    if (validationError != null) {
                        Text(
                            text = validationError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isBlank() || tempPhone.isBlank() || tempEmail.isBlank()) {
                            validationError = "Please enter Name, Mobile, and Email."
                            return@Button
                        }
                        viewModel.updateUserProfile(
                            name = tempName,
                            phone = tempPhone,
                            email = tempEmail,
                            alternatePhone = tempAltPhone,
                            college = tempCollege,
                            emergencyName = tempEmergName,
                            emergencyPhone = tempEmergPhone,
                            address = tempAddress,
                            bloodGroup = tempBloodGroup,
                            bio = tempBio
                        )
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profile updated & synced to Firestore!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("btn_save_profile")
                ) {
                    Text("Save & Sync")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // CANCEL BOOKING CONFIRMATION DIALOG
    bookingToCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            title = { Text("Cancel Stay Booking?", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Are you sure you want to cancel your booking for:")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(booking.propertyName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Booking ID: ${booking.id}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "As per STYNO Free Cancellation Guarantee, your ₹${NumberFormat.getNumberInstance(Locale.US).format(booking.finalAmount)} payment and ₹${NumberFormat.getNumberInstance(Locale.US).format(booking.securityDeposit)} deposit will be fully refunded to your original payment method within 24 hours.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelBooking(booking.id)
                        bookingToCancel = null
                        Toast.makeText(context, "Booking ${booking.id} cancelled. Refund initiated.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
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

    // VIEW BOOKING RECEIPT & PASSCODE DIALOG
    selectedBookingForDetail?.let { booking ->
        BookingReceiptDialog(
            booking = booking,
            onDismiss = { selectedBookingForDetail = null },
            onCopyPasscode = { code ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Passcode", code)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Passcode $code copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun StatBadgeItem(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = color)
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ContactDetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun BookingHistoryCard(
    booking: Booking,
    onViewReceipt: () -> Unit,
    onCancelBooking: () -> Unit,
    onContactHost: (String) -> Unit,
    onChatWithHost: () -> Unit
) {
    val isUpcoming = booking.status == BookingStatus.UPCOMING || booking.status == BookingStatus.ACTIVE
    val isCompleted = booking.status == BookingStatus.COMPLETED

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row with Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = booking.propertyType.name.replace("_", " "),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isUpcoming -> StynoEmerald.copy(alpha = 0.15f)
                        isCompleted -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    }
                ) {
                    Text(
                        text = when (booking.status) {
                            BookingStatus.UPCOMING -> "● UPCOMING STAY"
                            BookingStatus.ACTIVE -> "● ACTIVE STAY"
                            BookingStatus.COMPLETED -> "✓ COMPLETED"
                            BookingStatus.CANCELLED -> "✕ CANCELLED"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isUpcoming -> StynoEmerald
                            isCompleted -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Property Title & Room Type
            Text(
                text = booking.propertyName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${booking.roomTypeName} • ${booking.durationText}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = booking.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            // Dates & Passcode Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "CHECK-IN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${booking.checkInDate} (${booking.checkInTime})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }

                // Digital Passcode Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("PASSCODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(booking.digitalPasscode, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rent Paid & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "TOTAL PAID", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "₹${NumberFormat.getNumberInstance(Locale.US).format(booking.finalAmount)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onViewReceipt,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pass & Receipt", fontSize = 12.sp)
                    }

                    if (isUpcoming) {
                        OutlinedButton(
                            onClick = onCancelBooking,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancel", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingReceiptDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onCopyPasscode: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Digital Stay Pass & Receipt", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Passcode Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "DIGITAL ENTRY PASSCODE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = booking.digitalPasscode, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Display at biometric / warden gate for instant check-in", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onCopyPasscode(booking.digitalPasscode) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Passcode", fontSize = 12.sp)
                        }
                    }
                }

                Text(
                    text = "STAY DETAILS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ReceiptItemRow("Stay Property", booking.propertyName)
                ReceiptItemRow("Room Type", booking.roomTypeName)
                ReceiptItemRow("Check-In", "${booking.checkInDate} at ${booking.checkInTime}")
                ReceiptItemRow("Check-Out", booking.checkOutDate)
                ReceiptItemRow("Duration", booking.durationText)
                ReceiptItemRow("Guest Name", booking.guestName)
                ReceiptItemRow("Guest Mobile", booking.guestPhone)
                ReceiptItemRow("Guest Email", booking.guestEmail)
                ReceiptItemRow("ID Proof Verified", booking.guestIdProofType)

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    text = "PAYMENT SUMMARY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ReceiptItemRow("Base Rent", "₹${NumberFormat.getNumberInstance(Locale.US).format(booking.basePrice)}")
                ReceiptItemRow("Platform Convenience Fee", "₹${NumberFormat.getNumberInstance(Locale.US).format(booking.serviceFee)}")
                if (booking.discount > 0) {
                    ReceiptItemRow("Promo Discount", "-₹${NumberFormat.getNumberInstance(Locale.US).format(booking.discount)}")
                }
                if (booking.securityDeposit > 0) {
                    ReceiptItemRow("Security Deposit (Escrow Refundable)", "₹${NumberFormat.getNumberInstance(Locale.US).format(booking.securityDeposit)}")
                }
                ReceiptItemRow("Total Amount Paid", "₹${NumberFormat.getNumberInstance(Locale.US).format(booking.finalAmount)}", isBold = true)
                ReceiptItemRow("Payment Mode", booking.paymentMethod)
                ReceiptItemRow("Payment Status", booking.paymentStatus)
                ReceiptItemRow("Booking Reference ID", booking.id)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close Pass")
            }
        }
    )
}

@Composable
private fun ReceiptItemRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.2f)
        )
    }
}

@Composable
private fun ProfileWishlistCard(
    item: com.example.data.model.WishlistItem,
    onOpenDetail: () -> Unit,
    onRemove: () -> Unit,
    onShare: () -> Unit
) {
    val property = item.property
    val priceFormatted = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        .format(property.startingPrice)
        .replace(".00", "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Type Badge + Priority / Rating + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = property.propertyType.name.replace("_", " "),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (item.priorityTag.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StynoAmber.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = item.priorityTag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StynoAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Stay",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Remove from Wishlist",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Property Name & Location
            Text(
                text = property.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${property.area}, ${property.city} • ${property.nearbyLandmark}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (item.userNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "Note: ${item.userNotes}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            // Price, Rating and View Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STARTING FROM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "$priceFormatted/mo",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StynoEmerald.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${property.rating}★",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StynoEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onOpenDetail,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("View Stay", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import com.example.ui.components.PropertyPhotoItem
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Booking
import com.example.data.model.DailyFoodPhoto
import com.example.data.model.DurationType
import com.example.data.model.GenderSuitability
import com.example.data.model.OwnerPaymentDetails
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.QuickStayConfig
import com.example.data.model.Review
import com.example.data.model.RoomOption
import com.example.data.model.VerificationStatus
import com.example.ui.screens.owner.OwnerPaymentSetupSection
import com.example.ui.screens.owner.OwnerQuickStaySection
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun OwnerDashboardScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val propertyReviews by viewModel.propertyReviews.collectAsStateWithLifecycle()
    val propertyDrafts by viewModel.propertyDrafts.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "My Properties (${allProperties.size})",
        "Bookings (${bookings.size})",
        "Quick Stay ⚡",
        "Payment Setup 💳",
        "Canteen & Food Media",
        "Reviews & Host Responses",
        "Earnings & Stats",
        "Host Verification"
    )

    // Management Dialog States
    var editingPropertyInfo by remember { mutableStateOf<Property?>(null) }
    var editingPropertyPricing by remember { mutableStateOf<Property?>(null) }
    var managingPropertyRooms by remember { mutableStateOf<Property?>(null) }
    var uploadingFoodMediaProperty by remember { mutableStateOf<Property?>(null) }
    var managingPropertyQuickStay by remember { mutableStateOf<Property?>(null) }
    var managingPropertyPayout by remember { mutableStateOf<Property?>(null) }
    var respondingToReviewData by remember { mutableStateOf<Pair<Property, Review>?>(null) }

    if (!isLoggedIn) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(StynoEmerald.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Apartment,
                    contentDescription = null,
                    tint = StynoEmerald,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Host Account Required",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please sign in to manage your property listings, room allocations, food menus, and view earnings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.navigateTo(Screen.AUTH) },
                colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Sign In as Host",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    viewModel.setUserRole(com.example.ui.viewmodel.UserRole.GUEST)
                    viewModel.navigateTo(Screen.HOME)
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Return to Stays (Guest Mode)",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.HOME) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Host Management Studio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Production Control & Property Operations",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleUserRole() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch to Resident Mode",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.clearActiveWizardContext()
                                viewModel.navigateTo(Screen.ADD_PROPERTY)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("add_new_property_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("List Stay", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedTab) {
                // TAB 0: MY PROPERTIES & COMPREHENSIVE MANAGEMENT
                0 -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OwnerStatCard(
                                title = "Active Listings",
                                value = "${allProperties.size}",
                                icon = Icons.Default.Bed,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            OwnerStatCard(
                                title = "Total Bookings",
                                value = "${bookings.size}",
                                icon = Icons.Default.TrendingUp,
                                color = StynoEmerald,
                                modifier = Modifier.weight(1f)
                            )
                            OwnerStatCard(
                                title = "Revenue",
                                value = "₹${bookings.sumOf { it.finalAmount }.toInt()}",
                                icon = Icons.Default.Money,
                                color = StynoAccent,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (propertyDrafts.isNotEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                            Text(
                                                text = "Listing Drafts (${propertyDrafts.size})",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text("In-Progress Wizard", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                    propertyDrafts.forEach { draft ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = draft.propertyName.ifBlank { "Untitled ${draft.propertyType} Draft" },
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "${draft.propertyType} • Step ${draft.currentStep}/9 • ${draft.city}",
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Button(
                                                        onClick = {
                                                            viewModel.resumeEditingDraft(draft)
                                                        },
                                                        shape = RoundedCornerShape(8.dp),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                        modifier = Modifier.height(30.dp)
                                                    ) {
                                                        Text("Resume", fontSize = 11.sp)
                                                    }
                                                    IconButton(
                                                        onClick = { viewModel.deleteListingDraft(draft.id) },
                                                        modifier = Modifier.size(30.dp)
                                                    ) {
                                                        Icon(Icons.Default.Close, contentDescription = "Delete Draft", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "All Managed Properties (${allProperties.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    items(allProperties) { property ->
                        EnhancedOwnerPropertyCard(
                            property = property,
                            onManageDetails = { viewModel.openPropertyDetails(property) },
                            onEditInWizard = {
                                viewModel.startEditingProperty(property)
                            },
                            onEditInfo = { editingPropertyInfo = property },
                            onEditPricing = { editingPropertyPricing = property },
                            onManageRooms = { managingPropertyRooms = property },
                            onUploadFoodMedia = { uploadingFoodMediaProperty = property },
                            onManageQuickStay = { managingPropertyQuickStay = property },
                            onManagePayout = { managingPropertyPayout = property },
                            onToggleAvailability = { viewModel.togglePropertyAvailability(property.id) },
                            onDelete = { viewModel.deleteOwnerProperty(property.id) }
                        )
                    }
                }

                // TAB 1: BOOKINGS & GUESTS
                1 -> {
                    item {
                        Text(
                            text = "Live Guests & Bookings",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (bookings.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = "📭",
                                title = "No booking requests yet",
                                description = "When customers book your stay, their passes & payment details will appear here."
                            )
                        }
                    } else {
                        items(bookings) { booking ->
                            OwnerBookingItemCard(booking = booking)
                        }
                    }
                }

                // TAB 2: QUICK STAY MANAGEMENT
                2 -> {
                    item {
                        OwnerQuickStaySection(
                            properties = allProperties,
                            onSaveConfig = { propertyId, config ->
                                viewModel.updateOwnerQuickStayConfig(propertyId, config)
                            }
                        )
                    }
                }

                // TAB 3: OWNER PAYMENT SETUP
                3 -> {
                    item {
                        OwnerPaymentSetupSection(
                            properties = allProperties,
                            allBookings = bookings,
                            onSavePaymentDetails = { propertyId, details ->
                                viewModel.updateOwnerPaymentDetails(propertyId, details)
                            }
                        )
                    }
                }

                // TAB 4: CANTEEN & DAILY FOOD MEDIA
                4 -> {
                    item {
                        OwnerSectionHeader(
                            icon = Icons.Default.Restaurant,
                            title = "Canteen & Daily Food Hub",
                            subtitle = "Upload fresh 24h food photos, kitchen hygiene videos, and update meal timings"
                        )
                    }

                    items(allProperties) { property ->
                        FoodMediaPropertyCard(
                            property = property,
                            onUploadMedia = { uploadingFoodMediaProperty = property }
                        )
                    }
                }

                // TAB 5: RESIDENT REVIEWS & HOST REPLIES
                5 -> {
                    item {
                        OwnerSectionHeader(
                            icon = Icons.Default.RateReview,
                            title = "Resident Reviews & Host Replies",
                            subtitle = "Inspect 8-dimension rating breakdowns and write verified host responses"
                        )
                    }

                    val propertiesWithReviews = allProperties.filter { it.reviewCount > 0 || (propertyReviews[it.id]?.isNotEmpty() == true) }

                    if (propertiesWithReviews.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = "⭐",
                                title = "No reviews yet",
                                description = "As residents review your properties, their 8-dimension ratings and comments will appear here for you to respond."
                            )
                        }
                    } else {
                        items(propertiesWithReviews) { property ->
                            val reviews = viewModel.getReviews(property.id)
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
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(property.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("${property.propertyType.displayName} • ${property.city} • ${property.rating} ★ (${reviews.size} reviews)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(10.dp))

                                    reviews.forEach { rev ->
                                        OwnerReviewItem(
                                            review = rev,
                                            onReply = { respondingToReviewData = Pair(property, rev) }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 6: EARNINGS & STATS
                6 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Total Monthly Gross Earnings",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${bookings.sumOf { it.finalAmount }.toInt()}",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Direct bank settlement: Every Monday at 10:00 AM • Zero Commission on Styno Direct Host tier.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // TAB 7: HOST VERIFICATION
                7 -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = StynoEmerald,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Host Verification Desk",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Strict verification ensures trust & priority placement",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(14.dp))

                                VerificationCheckRow("Owner Aadhaar / Government ID", true)
                                VerificationCheckRow("Property Electricity / Water Bill", true)
                                VerificationCheckRow("Hostel Trade License / Commercial Clearance", false)
                                VerificationCheckRow("Fire & Safety NOC Certificate", false)
                            }
                        }
                    }
                }
            }
        }
    }

    // ========================================================
    // OWNER MANAGEMENT MODAL DIALOGS
    // ========================================================

    // 1. Edit Property Info Dialog
    editingPropertyInfo?.let { property ->
        EditPropertyInfoDialog(
            property = property,
            onDismiss = { editingPropertyInfo = null },
            onSave = { updated ->
                viewModel.updatePropertyInfo(updated)
                editingPropertyInfo = null
            }
        )
    }

    // 2. Edit Pricing & Availability Dialog
    editingPropertyPricing?.let { property ->
        EditPricingDialog(
            property = property,
            onDismiss = { editingPropertyPricing = null },
            onSave = { startingPrice, durationType, hourly, daily, weekly, monthly, yearly, deposit, depositOption, maintenance, electricity ->
                viewModel.updatePropertyPricing(
                    propertyId = property.id,
                    startingPrice = startingPrice,
                    durationType = durationType,
                    hourlyPrice = hourly,
                    dailyPrice = daily,
                    weeklyPrice = weekly,
                    monthlyPrice = monthly,
                    yearlyPrice = yearly,
                    securityDeposit = deposit,
                    securityDepositOption = depositOption,
                    maintenanceAmount = maintenance,
                    electricityInfo = electricity
                )
                editingPropertyPricing = null
            }
        )
    }

    // 3. Manage Rooms Dialog
    managingPropertyRooms?.let { property ->
        ManageRoomsDialog(
            property = property,
            onDismiss = { managingPropertyRooms = null },
            onAddRoom = { newRoom ->
                viewModel.addRoomOption(property.id, newRoom)
            },
            onDeleteRoom = { roomId ->
                viewModel.deleteRoomOption(property.id, roomId)
            }
        )
    }

    // 4. Upload Kitchen Media & Daily Food Photos Dialog
    uploadingFoodMediaProperty?.let { property ->
        UploadFoodMediaDialog(
            property = property,
            onDismiss = { uploadingFoodMediaProperty = null },
            onUploadDailyPhoto = { mealType, title ->
                val newDaily = DailyFoodPhoto(
                    id = "food-${UUID.randomUUID()}",
                    propertyId = property.id,
                    mealType = mealType,
                    title = title,
                    uploadDateFormatted = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                )
                viewModel.uploadDailyFoodPhoto(property.id, newDaily)
            },
            onUploadKitchenMedia = { photos, videoUrl ->
                viewModel.uploadKitchenMedia(property.id, photos, videoUrl)
            }
        )
    }

    // 5. Host Response Dialog
    respondingToReviewData?.let { (property, review) ->
        HostResponseDialog(
            property = property,
            review = review,
            onDismiss = { respondingToReviewData = null },
            onSubmitResponse = { replyText ->
                viewModel.respondToReview(property.id, review.id, replyText)
                respondingToReviewData = null
            }
        )
    }

    // 6. Quick Stay Hourly Configuration Dialog
    managingPropertyQuickStay?.let { property ->
        QuickStayConfigDialog(
            property = property,
            onDismiss = { managingPropertyQuickStay = null },
            onSave = { config ->
                viewModel.updateOwnerQuickStayConfig(property.id, config)
                managingPropertyQuickStay = null
            }
        )
    }

    // 7. Host Direct Bank & UPI Payout Setup Dialog
    managingPropertyPayout?.let { property ->
        OwnerPaymentDetailsDialog(
            property = property,
            onDismiss = { managingPropertyPayout = null },
            onSave = { details ->
                viewModel.updateOwnerPaymentDetails(property.id, details)
                managingPropertyPayout = null
            }
        )
    }
}

// ========================================================
// REUSABLE OWNER COMPONENTS & DIALOG IMPLEMENTATIONS
// ========================================================

@Composable
private fun EnhancedOwnerPropertyCard(
    property: Property,
    onManageDetails: () -> Unit,
    onEditInWizard: () -> Unit,
    onEditInfo: () -> Unit,
    onEditPricing: () -> Unit,
    onManageRooms: () -> Unit,
    onUploadFoodMedia: () -> Unit,
    onManageQuickStay: () -> Unit,
    onManagePayout: () -> Unit,
    onToggleAvailability: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = property.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${property.propertyType.displayName} • ${if (property.targetCustomerTypes.isNotEmpty()) property.targetCustomerTypes.joinToString(", ") else property.genderSuitability.displayName} • ${property.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (property.isAvailable) StynoEmerald.copy(alpha = 0.15f) else Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = if (property.isAvailable) "LIVE & ACCEPTING" else "PAUSED / FULL",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (property.isAvailable) StynoEmerald else Color(0xFFDC2626),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Property Thumbnail Strip / Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                PropertyPhotoItem(
                    imageName = property.imageDrawableNames.firstOrNull() ?: "img_hostel_modern",
                    propertyName = property.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Photo Count Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${property.imageDrawableNames.size} Photos",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & Deposit Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${property.startingPrice.toInt()} ${property.durationType.unitLabel.ifBlank { "/month" }}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "Deposit: ₹${property.securityDepositAmount.toInt()} (${property.securityDepositOption})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${property.roomOptions.size} Room Categories",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "GPS: ${"%.4f".format(property.latitude)}, ${"%.4f".format(property.longitude)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (property.quickStayConfig.isEnabled || property.ownerPaymentDetails.upiId.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (property.quickStayConfig.isEnabled) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StynoBluePrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "⚡ Quick Stay (₹${property.quickStayConfig.price3Hours.toInt()}/3h)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StynoBluePrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (property.ownerPaymentDetails.upiId.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StynoEmerald.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "💳 Payout: ${property.ownerPaymentDetails.upiId}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StynoEmerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Operational Action Buttons Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onEditInfo,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Info", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onEditPricing,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pricing", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onManageRooms,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Bed, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rooms", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onUploadFoodMedia,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Kitchen, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Food", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2 of Action Buttons: Quick Stay & Payout Accounts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onManageQuickStay,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (property.quickStayConfig.isEnabled) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (property.quickStayConfig.isEnabled) "Quick Stay ✓" else "Quick Stay", fontSize = 11.sp, color = if (property.quickStayConfig.isEnabled) StynoBluePrimary else MaterialTheme.colorScheme.onSurface)
                }

                OutlinedButton(
                    onClick = onManagePayout,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (property.ownerPaymentDetails.upiId.isNotBlank()) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (property.ownerPaymentDetails.upiId.isNotBlank()) "Payout UPI ✓" else "Payout / UPI", fontSize = 11.sp, color = if (property.ownerPaymentDetails.upiId.isNotBlank()) StynoEmerald else MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Accept Bookings:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = property.isAvailable,
                        onCheckedChange = { onToggleAvailability() }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }

                    OutlinedButton(
                        onClick = onEditInWizard,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Wizard", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onManageDetails,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Preview Page", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodMediaPropertyCard(
    property: Property,
    onUploadMedia: () -> Unit
) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(property.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("${property.propertyType.displayName} • ${property.city}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = onUploadMedia,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Food Photo", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Daily Food Photos List
            if (property.dailyFoodPhotos.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🕒", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "No daily meal photos uploaded yet today. Daily food photos stay live for 24h then auto-delete.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Text("Today's Live Meal Photos (${property.dailyFoodPhotos.size}) - Auto-deletes in 24h:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    property.dailyFoodPhotos.forEach { photo ->
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🍲", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("${photo.mealType}: ${photo.title}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF92400E))
                                }
                                Text("Expires in ${photo.hoursRemaining}h", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnerReviewItem(
    review: Review,
    onReply: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${review.userRole} • ${review.date}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text("${review.rating} ★", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(review.title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(review.comment, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            // 8-Dimension Breakdown Mini Grid
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Cleanliness: ${review.breakdown.cleanliness}★", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Food: ${review.breakdown.foodQuality}★", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Staff: ${review.breakdown.staffBehavior}★", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Safety: ${review.breakdown.safety}★", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!review.ownerResponse.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Your Host Reply (${review.ownerResponseDate ?: "Published"}):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                        }
                        Text(review.ownerResponse, fontSize = 10.sp, color = Color(0xFF1E3A8A))
                    }
                }
            } else {
                Button(
                    onClick = onReply,
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Reply to Resident", fontSize = 10.sp)
                }
            }
        }
    }
}

// 1. Edit Property Info Dialog
@Composable
private fun EditPropertyInfoDialog(
    property: Property,
    onDismiss: () -> Unit,
    onSave: (Property) -> Unit
) {
    var name by remember { mutableStateOf(property.name) }
    var address by remember { mutableStateOf(property.address) }
    var landmark by remember { mutableStateOf(property.nearbyLandmark) }
    var description by remember { mutableStateOf(property.description) }
    var targetCustomers by remember { mutableStateOf(if (property.targetCustomerTypes.isNotEmpty()) property.targetCustomerTypes.joinToString(", ") else property.genderSuitability.displayName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Property Information", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Property Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address & Area") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Prominent Landmark") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = targetCustomers,
                        onValueChange = { targetCustomers = it },
                        label = { Text("Target Customers (e.g. Boys, Girls, Families, Business)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val updated = property.copy(
                    name = name.trim(),
                    address = address.trim(),
                    nearbyLandmark = landmark.trim(),
                    description = description.trim(),
                    targetCustomerTypes = targetCustomers.split(",").map { it.trim() }.filter { it.isNotBlank() }
                )
                onSave(updated)
            }) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// 2. Edit Pricing Dialog
@Composable
private fun EditPricingDialog(
    property: Property,
    onDismiss: () -> Unit,
    onSave: (startingPrice: Double, durationType: DurationType, hourly: Double, daily: Double, weekly: Double, monthly: Double, yearly: Double, deposit: Double, depositOption: String, maintenance: Double, electricity: String) -> Unit
) {
    var startingPriceText by remember { mutableStateOf(property.startingPrice.toInt().toString()) }
    var selectedDuration by remember { mutableStateOf(property.durationType) }
    var depositText by remember { mutableStateOf(property.securityDepositAmount.toInt().toString()) }
    var depositOption by remember { mutableStateOf(property.securityDepositOption) }
    var maintenanceText by remember { mutableStateOf(property.maintenanceAmount.toInt().toString()) }
    var electricityInfo by remember { mutableStateOf(property.electricityChargeInfo) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pricing, Modes & Deposit", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = startingPriceText,
                        onValueChange = { startingPriceText = it },
                        label = { Text("Primary Starting Rent (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Select Primary Duration Mode:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(DurationType.HOURLY, DurationType.DAILY, DurationType.WEEKLY, DurationType.MONTHLY, DurationType.YEARLY).forEach { dur ->
                            FilterChip(
                                selected = selectedDuration == dur,
                                onClick = { selectedDuration = dur },
                                label = { Text(dur.name.take(3), fontSize = 10.sp) }
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = depositText,
                        onValueChange = { depositText = it },
                        label = { Text("Security Deposit Amount (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = depositOption,
                        onValueChange = { depositOption = it },
                        label = { Text("Deposit Refund Policy (e.g. 1 Month Refundable / Zero Deposit)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = maintenanceText,
                        onValueChange = { maintenanceText = it },
                        label = { Text("Monthly Maintenance Charges (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = electricityInfo,
                        onValueChange = { electricityInfo = it },
                        label = { Text("Electricity & Utility Charges") },
                        placeholder = { Text("e.g. ₹9/unit as per sub-meter or Included") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val startP = startingPriceText.toDoubleOrNull() ?: property.startingPrice
                val dep = depositText.toDoubleOrNull() ?: property.securityDepositAmount
                val maint = maintenanceText.toDoubleOrNull() ?: property.maintenanceAmount
                onSave(startP, selectedDuration, 0.0, 0.0, 0.0, startP, 0.0, dep, depositOption, maint, electricityInfo)
            }) {
                Text("Update Pricing")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// 3. Manage Rooms Dialog
@Composable
private fun ManageRoomsDialog(
    property: Property,
    onDismiss: () -> Unit,
    onAddRoom: (RoomOption) -> Unit,
    onDeleteRoom: (String) -> Unit
) {
    var showAddRoomForm by remember { mutableStateOf(false) }
    var newRoomName by remember { mutableStateOf("Double Sharing AC Room") }
    var newRoomSharing by remember { mutableStateOf("Double Sharing") }
    var newRoomPrice by remember { mutableStateOf("8500") }
    var isAc by remember { mutableStateOf(true) }
    var hasAttachedBath by remember { mutableStateOf(true) }
    var bedCapacity by remember { mutableStateOf("2") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Room Options & Inventory (${property.roomOptions.size})", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(property.roomOptions) { room ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(room.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("₹${room.price.toInt()} • ${if (room.isAc) "AC" else "Non-AC"} • ${if (room.hasAttachedBathroom) "Attached Bath" else "Common Bath"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { onDeleteRoom(room.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Room", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                if (!showAddRoomForm) {
                    item {
                        Button(
                            onClick = { showAddRoomForm = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add New Room Category")
                        }
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("New Room Configuration", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                OutlinedTextField(value = newRoomName, onValueChange = { newRoomName = it }, label = { Text("Room Display Name") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = newRoomSharing, onValueChange = { newRoomSharing = it }, label = { Text("Sharing Type (e.g. Single / Double / 3 BHK)") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = newRoomPrice, onValueChange = { newRoomPrice = it }, label = { Text("Monthly / Nightly Rent (₹)") }, modifier = Modifier.fillMaxWidth())
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("AC:", fontSize = 12.sp)
                                        Switch(checked = isAc, onCheckedChange = { isAc = it })
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Attached Bath:", fontSize = 12.sp)
                                        Switch(checked = hasAttachedBath, onCheckedChange = { hasAttachedBath = it })
                                    }
                                }
                                Button(
                                    onClick = {
                                        val newRoom = RoomOption(
                                            id = "room-${UUID.randomUUID()}",
                                            name = newRoomName.trim(),
                                            sharingType = newRoomSharing.trim(),
                                            price = newRoomPrice.toDoubleOrNull() ?: 8000.0,
                                            durationType = DurationType.MONTHLY,
                                            isAc = isAc,
                                            hasAttachedBathroom = hasAttachedBath,
                                            availableBeds = bedCapacity.toIntOrNull() ?: 2
                                        )
                                        onAddRoom(newRoom)
                                        showAddRoomForm = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Confirm & Save Room")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

// 4. Upload Food Media Dialog
@Composable
private fun UploadFoodMediaDialog(
    property: Property,
    onDismiss: () -> Unit,
    onUploadDailyPhoto: (mealType: String, title: String) -> Unit,
    onUploadKitchenMedia: (photos: List<String>, videoUrl: String?) -> Unit
) {
    var selectedMealType by remember { mutableStateOf("Breakfast") }
    var dishTitle by remember { mutableStateOf("Fresh Hot Parathas & Masala Omelette") }
    var kitchenVideoUrl by remember { mutableStateOf(property.kitchenVideoUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kitchen & Daily Food Photos", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text("Daily Meal Photo (Expires in 24 Hours):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Breakfast", "Lunch", "Dinner", "Hygiene").forEach { type ->
                            FilterChip(
                                selected = selectedMealType == type,
                                onClick = { selectedMealType = type },
                                label = { Text(type, fontSize = 10.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = dishTitle,
                        onValueChange = { dishTitle = it },
                        label = { Text("Meal Dish Title / Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Kitchen Inspection Video URL (Optional):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    OutlinedTextField(
                        value = kitchenVideoUrl,
                        onValueChange = { kitchenVideoUrl = it },
                        placeholder = { Text("https://youtube.com/... or cloud video link") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onUploadDailyPhoto(selectedMealType, dishTitle.trim().ifBlank { "Daily Fresh $selectedMealType" })
                if (kitchenVideoUrl.isNotBlank()) {
                    onUploadKitchenMedia(emptyList(), kitchenVideoUrl.trim())
                }
                onDismiss()
            }) {
                Text("Upload & Publish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// 5. Host Response Dialog
@Composable
private fun HostResponseDialog(
    property: Property,
    review: Review,
    onDismiss: () -> Unit,
    onSubmitResponse: (String) -> Unit
) {
    var responseText by remember { mutableStateOf("Thank you for your feedback! We are glad you enjoyed your stay at ${property.name}. Our housekeeping and warden teams strive to ensure maximum comfort and hygiene.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Respond to ${review.userName}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Review: \"${review.comment}\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = responseText,
                    onValueChange = { responseText = it },
                    label = { Text("Your Host Response (Public)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmitResponse(responseText) }) {
                Text("Publish Response")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun OwnerSectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OwnerBookingItemCard(booking: Booking) {
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
                        text = booking.guestName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Pass: ${booking.digitalPasscode} • ${booking.roomTypeName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    color = StynoEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "₹${booking.finalAmount.toInt()} PAID",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StynoEmerald,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Dates: ${booking.checkInDate} to ${booking.checkOutDate} (${booking.durationText})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Contact: ${booking.guestPhone} • ${booking.guestEmail}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun VerificationCheckRow(title: String, isDone: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        if (isDone) {
            Surface(
                color = StynoEmerald.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verified", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StynoEmerald)
                }
            }
        } else {
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Pending", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
    }
}

@Composable
private fun OwnerStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyStateCard(
    icon: String,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// 6. Quick Stay Hourly Configuration Dialog
@Composable
private fun QuickStayConfigDialog(
    property: Property,
    onDismiss: () -> Unit,
    onSave: (QuickStayConfig) -> Unit
) {
    var isEnabled by remember { mutableStateOf(property.quickStayConfig.isEnabled) }
    var price3h by remember { mutableStateOf(property.quickStayConfig.price3Hours.toInt().toString()) }
    var price6h by remember { mutableStateOf(property.quickStayConfig.price6Hours.toInt().toString()) }
    var price12h by remember { mutableStateOf(property.quickStayConfig.price12Hours.toInt().toString()) }
    var price24h by remember { mutableStateOf(property.quickStayConfig.price24Hours.toInt().toString()) }
    var luggageStorage by remember { mutableStateOf(property.quickStayConfig.luggageStorageAvailable) }
    var showerAccess by remember { mutableStateOf(property.quickStayConfig.instantShowerAccess) }
    var hourlyCheckIn by remember { mutableStateOf(property.quickStayConfig.hourlyCheckInTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = StynoBluePrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Quick Stay & Transit Setup", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Enable short hourly bookings (3h, 6h, 12h, 24h) for transit travelers, exam candidates, or day-use guests.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEnabled) StynoBluePrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Accept Quick Stay Bookings", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    if (isEnabled) "Guests can book hourly transit slots" else "Hourly booking is currently disabled",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
                        }
                    }
                }

                if (isEnabled) {
                    item {
                        Text("Hourly Slot Pricing (₹):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = price3h,
                                onValueChange = { price3h = it },
                                label = { Text("3 Hours (₹)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = price6h,
                                onValueChange = { price6h = it },
                                label = { Text("6 Hours (₹)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = price12h,
                                onValueChange = { price12h = it },
                                label = { Text("12 Hours (₹)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = price24h,
                                onValueChange = { price24h = it },
                                label = { Text("24 Hours (₹)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Text("Transit Amenities Provided:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Safe Luggage Cloakroom", fontSize = 13.sp)
                            Switch(checked = luggageStorage, onCheckedChange = { luggageStorage = it })
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Instant Shower & Towels Access", fontSize = 13.sp)
                            Switch(checked = showerAccess, onCheckedChange = { showerAccess = it })
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = hourlyCheckIn,
                            onValueChange = { hourlyCheckIn = it },
                            label = { Text("Check-in Window / Policy") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        QuickStayConfig(
                            isEnabled = isEnabled,
                            price3Hours = price3h.toDoubleOrNull() ?: property.quickStayConfig.price3Hours,
                            price6Hours = price6h.toDoubleOrNull() ?: property.quickStayConfig.price6Hours,
                            price12Hours = price12h.toDoubleOrNull() ?: property.quickStayConfig.price12Hours,
                            price24Hours = price24h.toDoubleOrNull() ?: property.quickStayConfig.price24Hours,
                            luggageStorageAvailable = luggageStorage,
                            instantShowerAccess = showerAccess,
                            hourlyCheckInTime = hourlyCheckIn.ifBlank { "24/7 Any Time" },
                            transitFacilities = property.quickStayConfig.transitFacilities
                        )
                    )
                }
            ) {
                Text("Save Quick Stay Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// 7. Host Direct Bank & UPI Payout Setup Dialog
@Composable
private fun OwnerPaymentDetailsDialog(
    property: Property,
    onDismiss: () -> Unit,
    onSave: (OwnerPaymentDetails) -> Unit
) {
    var upiId by remember { mutableStateOf(property.ownerPaymentDetails.upiId) }
    var accountHolder by remember { mutableStateOf(property.ownerPaymentDetails.accountHolderName) }
    var accountNumber by remember { mutableStateOf(property.ownerPaymentDetails.accountNumber) }
    var ifscCode by remember { mutableStateOf(property.ownerPaymentDetails.ifscCode) }
    var bankName by remember { mutableStateOf(property.ownerPaymentDetails.bankName) }
    var settlementMode by remember { mutableStateOf(property.ownerPaymentDetails.settlementMode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Payout & Bank Accounts", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Set your direct UPI ID or Bank Account for automatic booking payouts with 0% gateway deductions.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    OutlinedTextField(
                        value = accountHolder,
                        onValueChange = { accountHolder = it },
                        label = { Text("Account Holder / Business Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = { Text("Direct UPI ID (e.g. host@okaxis, 9876543210@paytm)") },
                        placeholder = { Text("name@bankupi") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Bank Name (e.g. HDFC Bank, SBI)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        label = { Text("Bank Account Number (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = ifscCode,
                        onValueChange = { ifscCode = it.uppercase() },
                        label = { Text("IFSC Code (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StynoEmerald.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StynoEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Settlements are processed directly to your account immediately after guest check-in verification.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        OwnerPaymentDetails(
                            upiId = upiId.trim(),
                            accountHolderName = accountHolder.trim(),
                            accountNumber = accountNumber.trim(),
                            ifscCode = ifscCode.trim(),
                            bankName = bankName.trim(),
                            isVerifiedForPayouts = true,
                            settlementMode = settlementMode
                        )
                    )
                }
            ) {
                Text("Save Payout Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

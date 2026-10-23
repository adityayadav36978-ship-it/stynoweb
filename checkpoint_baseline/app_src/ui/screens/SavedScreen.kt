package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Login
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Property
import com.example.data.model.WishlistCollection
import com.example.data.model.WishlistItem
import com.example.data.model.WishlistSyncState
import com.example.ui.components.WishlistShimmerList
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
    val filteredWishlistItems by viewModel.filteredWishlistItems.collectAsStateWithLifecycle()
    val selectedCollection by viewModel.selectedWishlistCollection.collectAsStateWithLifecycle()
    val collectionsWithCounts by viewModel.wishlistCollectionsWithCounts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.wishlistSearchQuery.collectAsStateWithLifecycle()
    val sortOption by viewModel.wishlistSortOption.collectAsStateWithLifecycle()
    val syncState by viewModel.wishlistSyncState.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastWishlistSyncTime.collectAsStateWithLifecycle()
    val comparisonList by viewModel.comparisonProperties.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    if (!isLoggedIn) {
        WishlistUnauthenticatedView(
            viewModel = viewModel,
            modifier = modifier
        )
        return
    }

    var showComparisonSheet by remember { mutableStateOf(false) }
    var editingWishlistItem by remember { mutableStateOf<WishlistItem?>(null) }
    var showCreateCollectionDialog by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    val sortOptions = listOf(
        "Recently Added",
        "Price: Low to High",
        "Price: High to Low",
        "Highest Rating",
        "Priority First"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "sync_spin"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar & Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = StynoAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "My Wishlist",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        val userLabel = userEmail.ifBlank { userName.ifBlank { "Member" } }
                        Text(
                            text = "${wishlistItems.size} accommodations saved • $userLabel",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Share Wishlist Button
                        if (wishlistItems.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    val shareText = viewModel.buildWishlistShareText(wishlistItems)
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "My STYNO Shortlisted Stays")
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Wishlist"))
                                },
                                modifier = Modifier.testTag("wishlist_share_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Wishlist",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Firestore Sync Button
                        IconButton(
                            onClick = { viewModel.syncWishlistWithFirestore() },
                            modifier = Modifier.testTag("wishlist_cloud_sync_button")
                        ) {
                            Icon(
                                imageVector = if (syncState == WishlistSyncState.SYNCING) Icons.Default.Refresh else Icons.Default.CloudSync,
                                contentDescription = "Sync Wishlist with Cloud Firestore",
                                tint = if (syncState == WishlistSyncState.SYNCING) StynoBluePrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .then(if (syncState == WishlistSyncState.SYNCING) Modifier.rotate(rotationAngle) else Modifier)
                            )
                        }

                        // Compare button if >= 2 items
                        if (wishlistItems.size >= 2) {
                            Button(
                                onClick = {
                                    if (comparisonList.size < 2 && wishlistItems.size >= 2) {
                                        viewModel.clearComparison()
                                        viewModel.toggleComparison(wishlistItems[0].property)
                                        viewModel.toggleComparison(wishlistItems[1].property)
                                    }
                                    showComparisonSheet = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("compare_stays_button")
                            ) {
                                Icon(imageVector = Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Compare", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Cloud Sync Status Pill
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (syncState) {
                        WishlistSyncState.SYNCING -> StynoBluePrimary.copy(alpha = 0.12f)
                        WishlistSyncState.SYNCED -> StynoEmerald.copy(alpha = 0.12f)
                        WishlistSyncState.OFFLINE -> MaterialTheme.colorScheme.surfaceVariant
                        WishlistSyncState.IDLE -> StynoEmerald.copy(alpha = 0.08f)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (syncState) {
                                    WishlistSyncState.SYNCING -> Icons.Default.Refresh
                                    WishlistSyncState.SYNCED -> Icons.Default.CloudDone
                                    WishlistSyncState.OFFLINE -> Icons.Default.CloudSync
                                    WishlistSyncState.IDLE -> Icons.Default.CloudDone
                                },
                                contentDescription = null,
                                tint = when (syncState) {
                                    WishlistSyncState.SYNCING -> StynoBluePrimary
                                    WishlistSyncState.SYNCED -> StynoEmerald
                                    WishlistSyncState.OFFLINE -> MaterialTheme.colorScheme.onSurfaceVariant
                                    WishlistSyncState.IDLE -> StynoEmerald
                                },
                                modifier = Modifier
                                    .size(14.dp)
                                    .then(if (syncState == WishlistSyncState.SYNCING) Modifier.rotate(rotationAngle) else Modifier)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (syncState) {
                                    WishlistSyncState.SYNCING -> "Syncing with Firestore Cloud Database..."
                                    WishlistSyncState.SYNCED -> lastSyncTime ?: "Synced with Firestore"
                                    WishlistSyncState.OFFLINE -> "Offline cache active"
                                    WishlistSyncState.IDLE -> lastSyncTime ?: "Cloud Synced with Firestore"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = when (syncState) {
                                    WishlistSyncState.SYNCING -> StynoBluePrimary
                                    WishlistSyncState.SYNCED -> StynoEmerald
                                    WishlistSyncState.OFFLINE -> MaterialTheme.colorScheme.onSurfaceVariant
                                    WishlistSyncState.IDLE -> StynoEmerald
                                }
                            )
                        }

                        Text(
                            text = "Cloud Backup",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Collections Horizontal Selector & Add Collection Chip
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                collectionsWithCounts.forEach { collection ->
                    val isSelected = selectedCollection.equals(collection.name, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectWishlistCollection(collection.name) },
                        label = {
                            Text(
                                text = "${collection.name} (${collection.count})",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                // Add Custom Collection Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showCreateCollectionDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Collection", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ New Folder", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Search Bar & Sort Dropdown Row
        if (wishlistItems.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setWishlistSearchQuery(it) },
                    placeholder = { Text("Search stays, areas, or notes...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.setWishlistSearchQuery("")
                                focusManager.clearFocus()
                            }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("wishlist_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                )

                // Sort Dropdown Button
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showSortMenu = true }
                            .padding(horizontal = 10.dp),
                        tonalElevation = 1.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Sort",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = sortOption.take(10),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        sortOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opt,
                                        fontWeight = if (sortOption == opt) FontWeight.Bold else FontWeight.Normal,
                                        color = if (sortOption == opt) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    viewModel.setWishlistSortOption(opt)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Saved List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (syncState == WishlistSyncState.SYNCING && wishlistItems.isEmpty()) {
                item {
                    WishlistShimmerList(count = 4)
                }
            } else if (filteredWishlistItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty() || selectedCollection != "All Favorites") "No matching stays found" else "No saved properties yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty() || selectedCollection != "All Favorites")
                                "Try switching your folder filter or clearing the search query."
                            else
                                "Bookmark verified Hostels, PGs, Hotels & Quick Stays to keep them organized across devices with Firestore Cloud sync.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (searchQuery.isNotEmpty() || selectedCollection != "All Favorites") {
                            OutlinedButton(
                                onClick = {
                                    viewModel.setWishlistSearchQuery("")
                                    viewModel.selectWishlistCollection("All Favorites")
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reset Filters")
                            }
                        } else {
                            Button(
                                onClick = { viewModel.navigateTo(Screen.HOME) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Explore Stays")
                            }
                        }
                    }
                }
            } else {
                items(
                    items = filteredWishlistItems,
                    key = { it.property.id }
                ) { wishlistItem ->
                    WishlistItemCard(
                        item = wishlistItem,
                        onCardClick = { viewModel.openPropertyDetails(wishlistItem.property) },
                        onEditClick = { editingWishlistItem = wishlistItem },
                        onBookClick = { viewModel.startBookingFlow(wishlistItem.property) },
                        onRemoveClick = { viewModel.toggleSave(wishlistItem.property.id) }
                    )
                }
            }
        }
    }

    // Edit Wishlist Item BottomSheet
    editingWishlistItem?.let { item ->
        EditWishlistBottomSheet(
            item = item,
            collections = collectionsWithCounts.map { it.name }.distinct(),
            onDismiss = { editingWishlistItem = null },
            onSave = { collection, notes, priority, priceAlert ->
                viewModel.updateWishlistDetails(
                    propertyId = item.property.id,
                    collectionName = collection,
                    notes = notes,
                    priorityTag = priority,
                    priceAlert = priceAlert
                )
                editingWishlistItem = null
            }
        )
    }

    // Create New Custom Collection Dialog
    if (showCreateCollectionDialog) {
        CreateCollectionDialog(
            onDismiss = { showCreateCollectionDialog = false },
            onConfirm = { name, emoji ->
                viewModel.addCustomWishlistCollection(name = name, iconEmoji = emoji)
                viewModel.selectWishlistCollection(name)
                showCreateCollectionDialog = false
            }
        )
    }

    // Compare Stays Bottom Sheet
    if (showComparisonSheet && comparisonList.size >= 2) {
        val prop1 = comparisonList[0]
        val prop2 = comparisonList[1]

        ModalBottomSheet(
            onDismissRequest = { showComparisonSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Side-by-Side Stay Comparison",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { showComparisonSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Table Header with Stay Names
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f).padding(4.dp)) {
                        Text(
                            text = prop1.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Box(modifier = Modifier.weight(1f).padding(4.dp)) {
                        Text(
                            text = prop2.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = StynoAccent
                        )
                    }
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                CompareRow("Starting Rent", "₹${prop1.startingPrice.toInt()} /${prop1.durationType.displayName}", "₹${prop2.startingPrice.toInt()} /${prop2.durationType.displayName}")
                CompareRow("Category", prop1.propertyType.displayName, prop2.propertyType.displayName)
                CompareRow("Suitability", prop1.genderSuitability.displayName, prop2.genderSuitability.displayName)
                CompareRow("Rating", "★ ${prop1.rating} (${prop1.reviewCount})", "★ ${prop2.rating} (${prop2.reviewCount})")
                CompareRow("Landmark", prop1.nearbyLandmark, prop2.nearbyLandmark)
                CompareRow("Gate Closing", prop1.gateClosingTime, prop2.gateClosingTime)
                CompareRow("Canteen Food", if (prop1.hasCanteenMenu) "3x Meals Included" else "Self/External", if (prop2.hasCanteenMenu) "3x Meals Included" else "Self/External")
                CompareRow("Deposit", "₹${prop1.roomOptions.firstOrNull()?.securityDeposit?.toInt() ?: 0}", "₹${prop2.roomOptions.firstOrNull()?.securityDeposit?.toInt() ?: 0}")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            showComparisonSheet = false
                            viewModel.openPropertyDetails(prop1)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("View ${prop1.propertyType.displayName}", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            showComparisonSheet = false
                            viewModel.openPropertyDetails(prop2)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StynoAccent)
                    ) {
                        Text("View ${prop2.propertyType.displayName}", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Rich Card displaying an individual Wishlist item with notes, priority pill, and cloud sync status.
 */
@Composable
fun WishlistItemCard(
    item: WishlistItem,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onBookClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val property = item.property

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Section: Thumbnail + Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    SavedPropertyThumbnail(
                        imageName = property.imageDrawableNames.firstOrNull() ?: "img_hostel_modern",
                        propertyType = property.propertyType.displayName,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Property Type badge
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = property.propertyType.displayName,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // Info Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(105.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = property.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            // Wishlist heart toggle
                            IconButton(
                                onClick = onRemoveClick,
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("wishlist_item_heart_${property.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Remove from Wishlist",
                                    tint = StynoAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Area & Rating
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${property.area}, ${property.city}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${property.rating}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Price & Collection Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "₹${NumberFormat.getNumberInstance(Locale.getDefault()).format(property.startingPrice.toInt())}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "/${property.durationType.displayName.lowercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Collection Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = item.collectionName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // User Notes & Priority Banner (if set)
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Priority Badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when {
                                item.priorityTag.contains("High") || item.priorityTag.contains("⭐") -> Color(0xFFFEF3C7)
                                item.priorityTag.contains("Considering") -> Color(0xFFE0E7FF)
                                else -> Color(0xFFDCFCE7)
                            }
                        ) {
                            Text(
                                text = item.priorityTag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    item.priorityTag.contains("High") || item.priorityTag.contains("⭐") -> Color(0xFF78350F)
                                    item.priorityTag.contains("Considering") -> Color(0xFF3730A3)
                                    else -> Color(0xFF166534)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Firestore Cloud Synced Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Cloud Synced",
                                tint = StynoEmerald,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Synced",
                                fontSize = 10.sp,
                                color = StynoEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Notes Text
                    if (item.userNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📝 \"${item.userNotes}\"",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit Notes & Folder Button
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Notes",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (item.userNotes.isBlank()) "+ Add Note" else "Edit Note",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Quick Book Button
                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Book Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Bottom Sheet to customize Wishlist Item: assign to collection, add personal notes, pick priority, toggle price alerts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWishlistBottomSheet(
    item: WishlistItem,
    collections: List<String>,
    onDismiss: () -> Unit,
    onSave: (collection: String, notes: String, priority: String, priceAlert: Boolean) -> Unit
) {
    var selectedCol by remember { mutableStateOf(item.collectionName) }
    var notesText by remember { mutableStateOf(item.userNotes) }
    var selectedPriority by remember { mutableStateOf(item.priorityTag) }
    var priceAlert by remember { mutableStateOf(item.priceAlertEnabled) }

    val priorityOptions = listOf(
        "High Priority ⭐",
        "Considering 📌",
        "Planning Visit 🔍",
        "Backup Option 💡"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Customize Wishlist Item",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = item.property.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Choose Collection
            Text(
                text = "📁 Save to Folder / Collection",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                collections.forEach { col ->
                    val isSelected = selectedCol.equals(col, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCol = col },
                        label = { Text(col, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Priority Selection
            Text(
                text = "🏷️ Priority Level",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                priorityOptions.forEach { opt ->
                    val isSelected = selectedPriority.equals(opt, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPriority = opt },
                        label = { Text(opt, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StynoAccent.copy(alpha = 0.2f),
                            selectedLabelColor = StynoAccent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Personal Notes
            Text(
                text = "📝 Personal Notes (Warden contact, questions, impressions)",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                placeholder = { Text("e.g. 3x meals included, 300m from metro, visited on Sunday with parents...", fontSize = 12.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price Alert Toggle
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (priceAlert) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = if (priceAlert) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Price & Vacancy Alerts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Notify when rent drops or a room becomes vacant",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = priceAlert,
                        onCheckedChange = { priceAlert = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = StynoBluePrimary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(
                onClick = {
                    onSave(selectedCol, notesText, selectedPriority, priceAlert)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save to Cloud & Device", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Dialog to create a custom wishlist folder / collection.
 */
@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, emoji: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🎓") }

    val emojis = listOf("🎓", "💼", "🏖️", "💰", "🏠", "❤️", "🌟", "🎒", "✈️", "🏢")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Wishlist Folder", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Organize your saved stays by category, trip, or campus.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Folder Name") },
                    placeholder = { Text("e.g. DU North Campus Hostels") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Select Icon:", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    emojis.forEach { emoji ->
                        Surface(
                            shape = CircleShape,
                            color = if (selectedEmoji == emoji) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { selectedEmoji = emoji }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm("${name.trim()} $selectedEmoji", selectedEmoji)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Create Folder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CompareRow(label: String, val1: String, val2: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = val1,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            Text(
                text = val2,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    }
}

@Composable
private fun SavedPropertyThumbnail(
    imageName: String,
    propertyType: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = remember(imageName) {
        context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }

    if (imageResId != 0) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(
                Brush.linearGradient(listOf(StynoBluePrimary, Color(0xFF1E293B)))
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = propertyType.take(2).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Dedicated unauthenticated view explaining Wishlist benefits and prompting sign-in.
 */
@Composable
fun WishlistUnauthenticatedView(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_heart")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = StynoAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Wishlist",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Guest Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Visual Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    StynoAccent.copy(alpha = 0.08f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Pulsing Heart in Soft Container
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(StynoAccent.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(StynoAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = StynoAccent,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .graphicsLayer(scaleX = heartScale, scaleY = heartScale)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Save Your Favorite Stays",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Sign in to save hostels, PGs, and apartments to your personal wishlist, organize them into folders, and track price changes across all your devices.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // CTA Buttons
                        Button(
                            onClick = { viewModel.navigateTo(Screen.AUTH) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("wishlist_sign_in_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign In / Create Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.HOME) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("wishlist_explore_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Explore Verified Stays",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // What You Can Do With Wishlist Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Why use Wishlist?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Feature Highlights
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WishlistFeatureCard(
                    icon = Icons.Filled.Favorite,
                    iconTint = StynoAccent,
                    title = "One-Tap Shortlisting",
                    description = "Heart any stay while exploring to instantly bookmark it to your favorites."
                )
                WishlistFeatureCard(
                    icon = Icons.Default.NotificationsActive,
                    iconTint = Color(0xFFD97706),
                    title = "Real-Time Price Alerts",
                    description = "Turn on price drop alerts to get notified whenever a room's rent is reduced."
                )
                WishlistFeatureCard(
                    icon = Icons.Default.Folder,
                    iconTint = StynoBluePrimary,
                    title = "Organize in Custom Folders",
                    description = "Sort stays into College Hostels, Work Commute, or Budget Stays folders."
                )
                WishlistFeatureCard(
                    icon = Icons.Default.CloudSync,
                    iconTint = StynoEmerald,
                    title = "Multi-Device Cloud Sync",
                    description = "Your saved stays automatically sync between your phone, tablet, and web."
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun WishlistFeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

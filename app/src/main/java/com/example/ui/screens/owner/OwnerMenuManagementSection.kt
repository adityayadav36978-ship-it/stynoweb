package com.example.ui.screens.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.data.util.ImageStorageHelper
import coil.compose.AsyncImage
import com.example.data.model.FoodCategories
import com.example.data.model.FoodMenu
import com.example.data.model.FoodMenuItem
import com.example.data.model.Property
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import java.util.UUID

// Curated high quality food image presets with names & categories for fast owner additions
data class FoodPreset(
    val name: String,
    val category: String,
    val isVeg: Boolean,
    val suggestedPrice: Double,
    val photoUrl: String,
    val timing: String,
    val description: String
)

private val FOOD_PRESETS = listOf(
    FoodPreset("Aloo Pyaaz Paratha", "Breakfast", true, 80.0, "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600&q=80", "7:30 AM - 10:30 AM", "2 stuffed whole wheat parathas served with creamy dahi & Amul butter"),
    FoodPreset("Masala Poha with Sev", "Breakfast", true, 60.0, "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&q=80", "7:30 AM - 10:30 AM", "Steamed flattened rice tempered with mustard, fresh curry leaves, peanuts & lemon"),
    FoodPreset("Chole Bhature Platter", "Breakfast", true, 110.0, "https://images.unsplash.com/photo-1626132647523-66f5bf380027?w=600&q=80", "8:00 AM - 11:00 AM", "2 fluffy bhaturas with authentic Delhi style spiced chickpeas & pickled onions"),
    FoodPreset("Paneer Butter Masala", "Lunch", true, 180.0, "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600&q=80", "12:30 PM - 3:00 PM", "Soft malai paneer cubes simmered in creamy makhani cashew gravy"),
    FoodPreset("Shahi Special Thali", "Lunch", true, 200.0, "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&q=80", "12:30 PM - 3:00 PM", "Paneer, Dal Tadka, 4 Butter Phulkas, Jeera Rice, Curd, Salad & Sweet"),
    FoodPreset("Crispy Samosa Chaat", "Snacks", true, 60.0, "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&q=80", "4:00 PM - 7:00 PM", "Crushed hot samosas topped with chole, chilled sweet curd, and mint tamarind chutney"),
    FoodPreset("Chicken Biryani with Raita", "Dinner", false, 220.0, "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600&q=80", "7:30 PM - 10:30 PM", "Dum-cooked fragrant basmati rice with tender spiced chicken pieces and cucumber raita"),
    FoodPreset("Egg Bhurji with 3 Rotis", "Dinner", false, 120.0, "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=600&q=80", "7:30 PM - 10:30 PM", "3 eggs scrambled with onions, tomatoes and green chillies served with hot phulkas"),
    FoodPreset("Kulhad Masala Chai", "Beverages", true, 30.0, "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=600&q=80", "All Day (7 AM - 10 PM)", "Brewed ginger-cardamom whole milk tea served hot in earthen clay cup"),
    FoodPreset("Fresh Sweet Lassi", "Beverages", true, 50.0, "https://images.unsplash.com/photo-1553787499-6f9133860278?w=600&q=80", "All Day", "Traditional thick churned yogurt drink garnished with cardamom and malai")
)

@Composable
fun OwnerMenuManagementSection(
    properties: List<Property>,
    propertyMenus: Map<String, FoodMenu>,
    onAddItem: (propertyId: String, item: FoodMenuItem) -> Unit,
    onUpdateItem: (propertyId: String, item: FoodMenuItem) -> Unit,
    onToggleItem: (propertyId: String, itemId: String, isAvailable: Boolean) -> Unit,
    onDeleteItem: (propertyId: String, itemId: String) -> Unit,
    onUploadMedia: (Property) -> Unit,
    initialSelectedPropertyId: String? = null,
    modifier: Modifier = Modifier,
    onNavigateToAddProperty: () -> Unit = {}
) {
    if (properties.isEmpty()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🍽️", fontSize = 42.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Managed Properties Listed",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "List a hostel, PG, or co-living property first to enable and manage its food menu for residents.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToAddProperty,
                    colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("menu_empty_add_property_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("List Stay to Manage Food Menu", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    var selectedPropertyId by remember(properties, initialSelectedPropertyId) {
        val defaultId = initialSelectedPropertyId ?: properties.firstOrNull()?.id ?: ""
        mutableStateOf(defaultId)
    }

    val selectedProperty = properties.find { it.id == selectedPropertyId } ?: properties.first()
    val currentMenu = propertyMenus[selectedProperty.id] ?: FoodMenu(propertyId = selectedProperty.id)

    var activeSubTab by remember { mutableStateOf("MENU") } // "MENU" or "PHOTOS"
    var selectedCategoryFilter by remember { mutableStateOf(FoodCategories.ALL) }
    var itemToEdit by remember { mutableStateOf<FoodMenuItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<FoodMenuItem?>(null) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Property Selector Chips
        if (properties.size > 1) {
            Text(
                text = "Select Managed Property:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
            ) {
                items(properties) { prop ->
                    val isSelected = prop.id == selectedProperty.id
                    val menuCount = propertyMenus[prop.id]?.aLaCarteItems?.size ?: 0
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPropertyId = prop.id },
                        label = {
                            Text(
                                text = "${prop.name.take(24)}${if (prop.name.length > 24) "..." else ""} ($menuCount)",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Apartment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StynoBluePrimary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }
        }

        // Selected Property Summary Card & Sub-tabs
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedProperty.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "📍 ${selectedProperty.area}, ${selectedProperty.city} • ${selectedProperty.propertyType.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDCFCE7),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Text(
                            text = "${currentMenu.aLaCarteItems.size} Menu Items",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sub-tabs: "Food Menu" vs "24h Live Food Photos"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp)
                ) {
                    val isMenu = activeSubTab == "MENU"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isMenu) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { activeSubTab = "MENU" }
                            .padding(vertical = 8.dp)
                            .testTag("owner_property_menu_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.RestaurantMenu,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isMenu) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Menu Management",
                                fontSize = 12.sp,
                                fontWeight = if (isMenu) FontWeight.Bold else FontWeight.Normal,
                                color = if (isMenu) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    val isPhotos = activeSubTab == "PHOTOS"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isPhotos) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { activeSubTab = "PHOTOS" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isPhotos) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "24h Food Photos (${selectedProperty.dailyFoodPhotos.size})",
                                fontSize = 12.sp,
                                fontWeight = if (isPhotos) FontWeight.Bold else FontWeight.Normal,
                                color = if (isPhotos) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeSubTab == "MENU") {
            // MENU MANAGEMENT SECTION
            // Actions Header: Category Filter & Add Item Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Organize Menu Items",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("owner_add_menu_item_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Food Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FoodCategories.DEFAULT_FILTER_LIST) { category ->
                    val isSelected = category == selectedCategoryFilter
                    val count = if (category == FoodCategories.ALL) {
                        currentMenu.aLaCarteItems.size
                    } else {
                        currentMenu.aLaCarteItems.count { it.category.equals(category, ignoreCase = true) }
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = category },
                        label = {
                            Text(
                                text = if (count > 0) "$category ($count)" else category,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("category_chip_$category"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filtered Items
            val displayedItems = remember(currentMenu.aLaCarteItems, selectedCategoryFilter) {
                if (selectedCategoryFilter == FoodCategories.ALL) {
                    currentMenu.aLaCarteItems
                } else {
                    currentMenu.aLaCarteItems.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
                }
            }

            // Overview stats banner
            val totalCount = currentMenu.aLaCarteItems.size
            val availableCount = currentMenu.aLaCarteItems.count { it.isAvailable }
            val disabledCount = totalCount - availableCount

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total $totalCount items • $availableCount active live • $disabledCount unavailable",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Auto-syncs to guests",
                        fontSize = 10.sp,
                        color = StynoEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (displayedItems.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🍲", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedCategoryFilter == FoodCategories.ALL)
                                "No food items on this property's menu yet"
                            else
                                "No items added under \"$selectedCategoryFilter\"",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add breakfast, lunch, dinner or snacks to publish your dining menu to guests.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Menu Item Now", fontSize = 11.sp)
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    displayedItems.forEach { item ->
                        OwnerMenuItemCard(
                            item = item,
                            onToggleAvailability = { isAvailable ->
                                onToggleItem(selectedProperty.id, item.id, isAvailable)
                            },
                            onEdit = { itemToEdit = item },
                            onDelete = { itemToDelete = item }
                        )
                    }
                }
            }
        } else {
            // DAILY FOOD PHOTOS & KITCHEN MEDIA (PRESERVED)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Daily 24-Hour Food Photos", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Upload fresh food photos that automatically expire in 24 hours.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { onUploadMedia(selectedProperty) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload Photo", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedProperty.dailyFoodPhotos.isEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("🕒", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "No live daily photos today. Uploading daily meal photos builds trust with prospective residents.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            selectedProperty.dailyFoodPhotos.forEach { photo ->
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🍲", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text("${photo.mealType}: ${photo.title}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                                Text("Uploaded ${photo.uploadDateFormatted}", fontSize = 10.sp, color = Color(0xFFB45309))
                                            }
                                        }
                                        Text("Expires in ${photo.hoursRemaining}h", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Item Dialog
    if (showAddDialog) {
        AddEditFoodMenuItemDialog(
            propertyId = selectedProperty.id,
            existingItem = null,
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                onAddItem(selectedProperty.id, newItem)
                showAddDialog = false
            }
        )
    }

    // Edit Item Dialog
    itemToEdit?.let { item ->
        AddEditFoodMenuItemDialog(
            propertyId = selectedProperty.id,
            existingItem = item,
            onDismiss = { itemToEdit = null },
            onSave = { updatedItem ->
                onUpdateItem(selectedProperty.id, updatedItem)
                itemToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Menu Item?", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to remove \"${item.name}\" (₹${item.price.toInt()}) from the menu? Guests viewing this property will immediately no longer see this item.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteItem(selectedProperty.id, item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Item")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun OwnerMenuItemCard(
    item: FoodMenuItem,
    onToggleAvailability: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isAvailable) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            1.dp,
            if (item.isAvailable) MaterialTheme.colorScheme.outline.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("menu_item_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Veg/Non-Veg Badge
                VegNonVegBadge(isVeg = item.isVeg)
                Spacer(modifier = Modifier.width(10.dp))

                // Item Details
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (!item.isAvailable) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = if (item.isAvailable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Text(
                            text = "₹${item.price.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = if (item.isAvailable) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Tags: Category and Timing
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StynoBluePrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = item.category,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StynoBluePrimary
                            )
                        }

                        if (item.availabilityTiming.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = item.availabilityTiming,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (item.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Photo Thumbnail if present
                if (item.photoUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.width(10.dp))
                    AsyncImage(
                        model = item.photoUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom control bar: Availability Switch + Edit / Delete Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Availability Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = item.isAvailable,
                        onCheckedChange = { onToggleAvailability(it) },
                        modifier = Modifier
                            .scale(0.8f)
                            .testTag("menu_switch_${item.id}"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = StynoEmerald
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (item.isAvailable) "Available" else "Disabled",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (item.isAvailable) StynoEmerald else MaterialTheme.colorScheme.error
                    )
                }

                // Action Buttons: Edit & Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onEdit,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("menu_edit_btn_${item.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Item", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("menu_delete_btn_${item.id}")
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete Item",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// Indian FSSAI compliant Veg/Non-Veg square with filled shape
@Composable
fun VegNonVegBadge(
    isVeg: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isVeg) Color(0xFF16A34A) else Color(0xFFDC2626)
    val dotColor = if (isVeg) Color(0xFF16A34A) else Color(0xFFDC2626)

    Box(
        modifier = modifier
            .size(16.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(3.dp))
            .background(Color.White, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}

@Composable
fun AddEditFoodMenuItemDialog(
    propertyId: String,
    existingItem: FoodMenuItem?,
    onDismiss: () -> Unit,
    onSave: (FoodMenuItem) -> Unit
) {
    val isEditing = existingItem != null

    var name by remember { mutableStateOf(existingItem?.name ?: "") }
    var description by remember { mutableStateOf(existingItem?.description ?: "") }
    var priceText by remember { mutableStateOf(existingItem?.price?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
    var category by remember { mutableStateOf(existingItem?.category ?: FoodCategories.BREAKFAST) }
    var isVeg by remember { mutableStateOf(existingItem?.isVeg ?: true) }
    var isAvailable by remember { mutableStateOf(existingItem?.isAvailable ?: true) }
    var timing by remember { mutableStateOf(existingItem?.availabilityTiming ?: "7:30 AM - 10:30 AM") }
    var photoUrl by remember { mutableStateOf(existingItem?.photoUrl ?: "") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Real Camera capture launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val savedFile = ImageStorageHelper.saveBitmapToPersistentStorage(
                context = context,
                bitmap = bitmap,
                prefix = "menu_photo"
            )
            if (savedFile != null) {
                photoUrl = savedFile.absolutePath
                Toast.makeText(context, "Photo captured & saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera permission request launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission needed to snap menu food item photo", Toast.LENGTH_LONG).show()
        }
    }

    // Gallery image picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val bitmap = ImageStorageHelper.uriToBitmap(context, uri)
            if (bitmap != null) {
                val savedFile = ImageStorageHelper.saveBitmapToPersistentStorage(
                    context = context,
                    bitmap = bitmap,
                    prefix = "menu_photo"
                )
                if (savedFile != null) {
                    photoUrl = savedFile.absolutePath
                    Toast.makeText(context, "Gallery photo selected!", Toast.LENGTH_SHORT).show()
                } else {
                    photoUrl = uri.toString()
                }
            } else {
                photoUrl = uri.toString()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.AddCircle,
                    contentDescription = null,
                    tint = StynoBluePrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Edit Menu Item" else "Add New Menu Item",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Preset Suggestions
                if (!isEditing) {
                    item {
                        Text(
                            text = "💡 Quick 1-Tap Presets (Includes Image & Details):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StynoBluePrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(FOOD_PRESETS) { preset ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                                    modifier = Modifier.clickable {
                                        name = preset.name
                                        category = preset.category
                                        isVeg = preset.isVeg
                                        priceText = preset.suggestedPrice.toInt().toString()
                                        photoUrl = preset.photoUrl
                                        timing = preset.timing
                                        description = preset.description
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        VegNonVegBadge(isVeg = preset.isVeg)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(preset.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Item Name
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            validationError = null
                        },
                        label = { Text("Item Name *") },
                        placeholder = { Text("e.g. Paneer Butter Masala, Masala Dosa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("menu_item_name_input")
                    )
                }

                // Price & Veg/Non-Veg Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() || char == '.' }) {
                                    priceText = it
                                    validationError = null
                                }
                            },
                            label = { Text("Price (₹) *") },
                            placeholder = { Text("e.g. 150") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("menu_item_price_input")
                        )

                        // Veg / Non-Veg Selector
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text("Dietary Type *", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = isVeg,
                                    onClick = { isVeg = true },
                                    label = { Text("Veg 🌱", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFDCFCE7),
                                        selectedLabelColor = Color(0xFF166534)
                                    )
                                )
                                FilterChip(
                                    selected = !isVeg,
                                    onClick = { isVeg = false },
                                    label = { Text("Non-Veg 🍗", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFFEE2E2),
                                        selectedLabelColor = Color(0xFF991B1B)
                                    )
                                )
                            }
                        }
                    }
                }

                // Category Selection
                item {
                    Text("Category / Meal Type *", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(FoodCategories.OWNER_CATEGORIES) { cat ->
                            val isSelected = category.equals(cat, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    category = cat
                                    if (timing.isBlank() || timing == "7:30 AM - 10:30 AM" || timing == "12:30 PM - 3:00 PM" || timing == "7:30 PM - 10:30 PM") {
                                        timing = when (cat) {
                                            "Breakfast" -> "7:30 AM - 10:30 AM"
                                            "Lunch" -> "12:30 PM - 3:00 PM"
                                            "Dinner" -> "7:30 PM - 10:30 PM"
                                            "Snacks" -> "4:00 PM - 7:00 PM"
                                            else -> "All Day"
                                        }
                                    }
                                },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StynoBluePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Timing
                item {
                    OutlinedTextField(
                        value = timing,
                        onValueChange = { timing = it },
                        label = { Text("Meal Timing / Availability") },
                        placeholder = { Text("e.g. 7:30 AM - 10:30 AM or All Day") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("menu_item_timing_input")
                    )
                }

                // Description
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("e.g. Served with fresh dahi, mint chutney & butter") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("menu_item_desc_input")
                    )
                }

                // Food Image URL & Real Camera/Gallery Upload
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Food Item Photo",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Preview if photo is present
                        if (photoUrl.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Food preview",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Photo Selected",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = StynoEmerald
                                    )
                                    Text(
                                        text = photoUrl.takeLast(32),
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(
                                    onClick = { photoUrl = "" },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove photo",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Camera and Gallery buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                        cameraLauncher.launch(null)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_menu_camera"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Camera", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_menu_gallery"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gallery", fontSize = 12.sp)
                            }
                        }

                        // Or manual URL
                        OutlinedTextField(
                            value = photoUrl,
                            onValueChange = { photoUrl = it },
                            label = { Text("Or Image URL / File Path", fontSize = 11.sp) },
                            placeholder = { Text("https://... or /data/...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("menu_item_photo_input")
                        )
                    }
                }

                // Availability Switch
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Initial Availability", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = if (isAvailable) "Available for resident orders" else "Disabled / Currently unavailable",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isAvailable,
                            onCheckedChange = { isAvailable = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = StynoEmerald
                            )
                        )
                    }
                }

                // Validation Error Banner
                validationError?.let { err ->
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEE2E2),
                            border = BorderStroke(1.dp, Color(0xFFF87171))
                        ) {
                            Text(
                                text = err,
                                color = Color(0xFFB91C1C),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = name.trim()
                    val priceVal = priceText.toDoubleOrNull()

                    if (trimmedName.isBlank()) {
                        validationError = "Please enter food item name."
                        return@Button
                    }
                    if (priceVal == null || priceVal <= 0.0) {
                        validationError = "Please enter a valid price in rupees (e.g. 120)."
                        return@Button
                    }

                    val finalItem = FoodMenuItem(
                        id = existingItem?.id ?: "item-${UUID.randomUUID()}",
                        name = trimmedName,
                        description = description.trim(),
                        price = priceVal,
                        isVeg = isVeg,
                        category = category.trim().ifBlank { "Main Course" },
                        availabilityTiming = timing.trim().ifBlank { "All Day" },
                        isAvailable = isAvailable,
                        photoUrl = photoUrl.trim()
                    )
                    onSave(finalItem)
                },
                colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                modifier = Modifier.testTag("save_menu_item_button")
            ) {
                Text(if (isEditing) "Save Changes" else "Add Item to Menu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Utility extension for scaling switch
private fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.size((52 * scale).dp, (32 * scale).dp)
)

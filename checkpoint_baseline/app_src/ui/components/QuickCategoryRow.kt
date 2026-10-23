package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Countertops
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GenderSuitability
import com.example.data.model.PropertyType
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald

data class Animated3DCategory(
    val type: PropertyType,
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val accentColor: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickCategorySection(
    selectedCategory: PropertyType?,
    onCategorySelected: (PropertyType?) -> Unit,
    selectedGender: GenderSuitability?,
    onGenderSelected: (GenderSuitability?) -> Unit,
    modifier: Modifier = Modifier,
    selectedRoomSharing: String? = null,
    onRoomSharingSelected: (String?) -> Unit = {},
    selectedRentalDuration: String? = null,
    onRentalDurationSelected: (String?) -> Unit = {},
    selectedFurnishing: String? = null,
    onFurnishingSelected: (String?) -> Unit = {},
    selectedOccupantType: String? = null,
    onOccupantTypeSelected: (String?) -> Unit = {},
    selectedAcFilter: Boolean? = null,
    onAcFilterSelected: (Boolean?) -> Unit = {},
    selectedFoodFilter: Boolean? = null,
    onFoodFilterSelected: (Boolean?) -> Unit = {},
    selectedAmenities: Set<String> = emptySet(),
    onToggleAmenity: (String) -> Unit = {},
    verifiedOnly: Boolean = false,
    onVerifiedToggle: (Boolean) -> Unit = {},
    onResetCategoryFilters: () -> Unit = {},
    onExploreMatchesClicked: () -> Unit = {},
    matchedCount: Int = 0
) {
    val categories = listOf(
        Animated3DCategory(
            type = PropertyType.HOSTEL,
            title = "Hostel",
            subtitle = "Boys, Girls & Co-Ed",
            badge = "From ₹5,499/mo",
            icon = Icons.Default.Diversity3,
            gradientColors = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6)),
            accentColor = Color(0xFF60A5FA)
        ),
        Animated3DCategory(
            type = PropertyType.HOTEL,
            title = "Hotel",
            subtitle = "Nightly & Luxury Stay",
            badge = "Instant 24/7",
            icon = Icons.Default.Hotel,
            gradientColors = listOf(Color(0xFF7C2D12), Color(0xFFEA580C)),
            accentColor = Color(0xFFFB923C)
        ),
        Animated3DCategory(
            type = PropertyType.PG,
            title = "PG",
            subtitle = "Homely Food & Wi-Fi",
            badge = "Zero Brokerage",
            icon = Icons.Default.Home,
            gradientColors = listOf(Color(0xFF065F46), Color(0xFF10B981)),
            accentColor = Color(0xFF34D399)
        ),
        Animated3DCategory(
            type = PropertyType.ROOM,
            title = "Room",
            subtitle = "Single & Shared Private",
            badge = "Deposit Free",
            icon = Icons.Default.MeetingRoom,
            gradientColors = listOf(Color(0xFF581C87), Color(0xFF8B5CF6)),
            accentColor = Color(0xFFA78BFA)
        ),
        Animated3DCategory(
            type = PropertyType.FLAT,
            title = "Flat",
            subtitle = "1, 2, 3, 4 BHK Gated",
            badge = "Furnished",
            icon = Icons.Default.Apartment,
            gradientColors = listOf(Color(0xFF0F172A), Color(0xFF334155)),
            accentColor = Color(0xFF38BDF8)
        ),
        Animated3DCategory(
            type = PropertyType.QUICK_STAY,
            title = "Quick Stay",
            subtitle = "3h, 6h, 12h Slots",
            badge = "From ₹299/slot",
            icon = Icons.Default.Bolt,
            gradientColors = listOf(Color(0xFF78350F), Color(0xFFF59E0B)),
            accentColor = Color(0xFFFBBF24)
        )
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = StynoBluePrimary.copy(alpha = 0.12f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = StynoBluePrimary,
                        modifier = Modifier
                            .padding(6.dp)
                            .size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Choose Property Category",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "6 specialized 3D stay categories with verified amenities",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (selectedCategory != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onCategorySelected(null)
                            onResetCategoryFilters()
                        }
                        .testTag("clear_category_selection_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reset All",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 6 MAIN ANIMATED 3D OPTION CARDS (Horizontal Scroll with 3D isometric tactile depth)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat.type
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.04f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "scale_${cat.type.name}"
                )
                val elevation by animateDpAsState(
                    targetValue = if (isSelected) 8.dp else 3.dp,
                    label = "elevation_${cat.type.name}"
                )

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface,
                    tonalElevation = elevation,
                    shadowElevation = elevation,
                    modifier = Modifier
                        .width(136.dp)
                        .scale(scale)
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            brush = if (isSelected) {
                                Brush.linearGradient(listOf(cat.accentColor, StynoBluePrimary))
                            } else {
                                Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
                            },
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            if (isSelected) {
                                onCategorySelected(null)
                                onResetCategoryFilters()
                            } else {
                                onCategorySelected(cat.type)
                            }
                        }
                        .testTag("category_3d_card_${cat.type.name}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) {
                                    Brush.verticalGradient(
                                        listOf(
                                            cat.accentColor.copy(alpha = 0.15f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                }
                            )
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Badge Tag
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) cat.accentColor else Color(0xFF0F172A).copy(alpha = 0.08f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = cat.badge,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // 3D Illuminated Icon Box with Shadow
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(
                                    elevation = if (isSelected) 6.dp else 2.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    spotColor = cat.accentColor
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.linearGradient(cat.gradientColors)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = cat.title,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Title
                        Text(
                            text = cat.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Subtitle
                        Text(
                            text = cat.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Active Indicator Pill
                        if (isSelected) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = StynoEmerald.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = StynoEmerald,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Active",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StynoEmerald
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // DYNAMIC WORKFLOW STUDIO FOR SELECTED CATEGORY
        AnimatedVisibility(
            visible = selectedCategory != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            if (selectedCategory != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("category_refinement_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, StynoBluePrimary.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header Bar of Refinement Studio
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = StynoBluePrimary,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${selectedCategory.displayName} Refinement Studio",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StynoEmerald.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$matchedCount Stays Found",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StynoEmerald,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // CATEGORY SPECIFIC WORKFLOWS
                        when (selectedCategory) {
                            // 1. HOSTEL WORKFLOW
                            PropertyType.HOSTEL -> {
                                HostelRefinementView(
                                    selectedGender = selectedGender,
                                    onGenderSelected = onGenderSelected,
                                    selectedRoomSharing = selectedRoomSharing,
                                    onRoomSharingSelected = onRoomSharingSelected,
                                    selectedRentalDuration = selectedRentalDuration,
                                    onRentalDurationSelected = onRentalDurationSelected,
                                    selectedAcFilter = selectedAcFilter,
                                    onAcFilterSelected = onAcFilterSelected,
                                    selectedFoodFilter = selectedFoodFilter,
                                    onFoodFilterSelected = onFoodFilterSelected,
                                    selectedAmenities = selectedAmenities,
                                    onToggleAmenity = onToggleAmenity,
                                    verifiedOnly = verifiedOnly,
                                    onVerifiedToggle = onVerifiedToggle
                                )
                            }

                            // 2. HOTEL WORKFLOW
                            PropertyType.HOTEL -> {
                                HotelRefinementView(
                                    selectedOccupantType = selectedOccupantType,
                                    onOccupantTypeSelected = onOccupantTypeSelected,
                                    selectedRoomSharing = selectedRoomSharing,
                                    onRoomSharingSelected = onRoomSharingSelected,
                                    selectedAcFilter = selectedAcFilter,
                                    onAcFilterSelected = onAcFilterSelected,
                                    selectedAmenities = selectedAmenities,
                                    onToggleAmenity = onToggleAmenity,
                                    verifiedOnly = verifiedOnly,
                                    onVerifiedToggle = onVerifiedToggle
                                )
                            }

                            // 3. PG WORKFLOW
                            PropertyType.PG -> {
                                PgRefinementView(
                                    selectedGender = selectedGender,
                                    onGenderSelected = onGenderSelected,
                                    selectedRoomSharing = selectedRoomSharing,
                                    onRoomSharingSelected = onRoomSharingSelected,
                                    selectedFoodFilter = selectedFoodFilter,
                                    onFoodFilterSelected = onFoodFilterSelected,
                                    selectedAmenities = selectedAmenities,
                                    onToggleAmenity = onToggleAmenity,
                                    verifiedOnly = verifiedOnly,
                                    onVerifiedToggle = onVerifiedToggle
                                )
                            }

                            // 4. ROOM & 5. FLAT WORKFLOW
                            PropertyType.ROOM, PropertyType.FLAT -> {
                                RoomAndFlatRefinementView(
                                    isFlat = selectedCategory == PropertyType.FLAT,
                                    selectedOccupantType = selectedOccupantType,
                                    onOccupantTypeSelected = onOccupantTypeSelected,
                                    selectedRentalDuration = selectedRentalDuration,
                                    onRentalDurationSelected = onRentalDurationSelected,
                                    selectedFurnishing = selectedFurnishing,
                                    onFurnishingSelected = onFurnishingSelected,
                                    selectedRoomSharing = selectedRoomSharing,
                                    onRoomSharingSelected = onRoomSharingSelected,
                                    selectedAcFilter = selectedAcFilter,
                                    onAcFilterSelected = onAcFilterSelected,
                                    selectedAmenities = selectedAmenities,
                                    onToggleAmenity = onToggleAmenity,
                                    verifiedOnly = verifiedOnly,
                                    onVerifiedToggle = onVerifiedToggle
                                )
                            }

                            // 6. QUICK STAY WORKFLOW
                            PropertyType.QUICK_STAY -> {
                                QuickStayRefinementView(
                                    selectedOccupantType = selectedOccupantType,
                                    onOccupantTypeSelected = onOccupantTypeSelected,
                                    selectedRentalDuration = selectedRentalDuration,
                                    onRentalDurationSelected = onRentalDurationSelected,
                                    selectedAcFilter = selectedAcFilter,
                                    onAcFilterSelected = onAcFilterSelected,
                                    selectedAmenities = selectedAmenities,
                                    onToggleAmenity = onToggleAmenity,
                                    verifiedOnly = verifiedOnly,
                                    onVerifiedToggle = onVerifiedToggle
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bottom Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Clear Customizations",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable { onResetCategoryFilters() }
                                    .padding(4.dp)
                                    .testTag("clear_customizations_btn")
                            )

                            Button(
                                onClick = onExploreMatchesClicked,
                                colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("view_matched_stays_btn")
                            ) {
                                Text("Explore $matchedCount Stays", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- HOSTEL WORKFLOW ----------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HostelRefinementView(
    selectedGender: GenderSuitability?,
    onGenderSelected: (GenderSuitability?) -> Unit,
    selectedRoomSharing: String?,
    onRoomSharingSelected: (String?) -> Unit,
    selectedRentalDuration: String?,
    onRentalDurationSelected: (String?) -> Unit,
    selectedAcFilter: Boolean?,
    onAcFilterSelected: (Boolean?) -> Unit,
    selectedFoodFilter: Boolean?,
    onFoodFilterSelected: (Boolean?) -> Unit,
    selectedAmenities: Set<String>,
    onToggleAmenity: (String) -> Unit,
    verifiedOnly: Boolean,
    onVerifiedToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 1. Choose Resident: Boys, Girls, or Both
        FilterSectionTitle("1. Resident Occupancy (Boys / Girls / Both)")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WorkflowChip(
                label = "Boys Only",
                icon = Icons.Default.Male,
                isSelected = selectedGender == GenderSuitability.BOYS_ONLY,
                onClick = { onGenderSelected(if (selectedGender == GenderSuitability.BOYS_ONLY) null else GenderSuitability.BOYS_ONLY) }
            )
            WorkflowChip(
                label = "Girls Only",
                icon = Icons.Default.Female,
                isSelected = selectedGender == GenderSuitability.GIRLS_ONLY,
                onClick = { onGenderSelected(if (selectedGender == GenderSuitability.GIRLS_ONLY) null else GenderSuitability.GIRLS_ONLY) }
            )
            WorkflowChip(
                label = "Both (Co-Ed)",
                icon = Icons.Default.Diversity3,
                isSelected = selectedGender == GenderSuitability.CO_ED || selectedGender == GenderSuitability.ALL,
                onClick = { onGenderSelected(if (selectedGender == GenderSuitability.CO_ED) null else GenderSuitability.CO_ED) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Room Sharing Type (Single, Double, Triple, Four-sharing, Dormitory)
        FilterSectionTitle("2. Room Sharing Type")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Single", "Double", "Triple", "Four-sharing", "Dormitory").forEach { sharing ->
                WorkflowChip(
                    label = sharing,
                    isSelected = selectedRoomSharing == sharing,
                    onClick = { onRoomSharingSelected(if (selectedRoomSharing == sharing) null else sharing) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Rental Duration (Monthly, 2 Months, 3 Months, 6 Months, 1 Year, Custom)
        FilterSectionTitle("3. Rental Duration Term")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Monthly", "2 Months", "3 Months", "6 Months", "1 Year", "Custom Duration").forEach { duration ->
                WorkflowChip(
                    label = duration,
                    isSelected = selectedRentalDuration == duration,
                    onClick = { onRentalDurationSelected(if (selectedRentalDuration == duration) null else duration) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Key Amenities & Facilities Toggle Grid
        FilterSectionTitle("4. Key Facilities & Verification")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AmenityToggleChip("📶 Free Wi-Fi", selectedAmenities.contains("Wi-Fi")) { onToggleAmenity("Wi-Fi") }
            AmenityToggleChip("❄️ AC", selectedAcFilter == true) { onAcFilterSelected(if (selectedAcFilter == true) null else true) }
            AmenityToggleChip("🚿 Attached Washroom", selectedAmenities.contains("Attached Washroom")) { onToggleAmenity("Attached Washroom") }
            AmenityToggleChip("🧺 Laundry", selectedAmenities.contains("Laundry")) { onToggleAmenity("Laundry") }
            AmenityToggleChip("📖 Study Table", selectedAmenities.contains("Study")) { onToggleAmenity("Study") }
            AmenityToggleChip("🚪 Wardrobe", selectedAmenities.contains("Wardrobe")) { onToggleAmenity("Wardrobe") }
            AmenityToggleChip("🍲 3-Time Food/Mess", selectedFoodFilter == true) { onFoodFilterSelected(if (selectedFoodFilter == true) null else true) }
            AmenityToggleChip("📹 24/7 CCTV / Security", selectedAmenities.contains("Security") || selectedAmenities.contains("CCTV")) { onToggleAmenity("Security") }
            AmenityToggleChip("⚡ Power Backup", selectedAmenities.contains("Power Backup")) { onToggleAmenity("Power Backup") }
            AmenityToggleChip("🅿️ Parking", selectedAmenities.contains("Parking")) { onToggleAmenity("Parking") }
            AmenityToggleChip("🎓 Near Colleges", selectedAmenities.contains("University") || selectedAmenities.contains("College")) { onToggleAmenity("University") }
            AmenityToggleChip("🛡️ Verified Owner", verifiedOnly) { onVerifiedToggle(!verifiedOnly) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Live Benchmark Summary Card
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Monthly Rent: ₹5,499 - ₹12,000/mo",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Deposit: 1 Month Refundable • Verified Wardens",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StynoEmerald.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = "● 14 Beds Available",
                        color = StynoEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

// ---------------- HOTEL WORKFLOW ----------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HotelRefinementView(
    selectedOccupantType: String?,
    onOccupantTypeSelected: (String?) -> Unit,
    selectedRoomSharing: String?,
    onRoomSharingSelected: (String?) -> Unit,
    selectedAcFilter: Boolean?,
    onAcFilterSelected: (Boolean?) -> Unit,
    selectedAmenities: Set<String>,
    onToggleAmenity: (String) -> Unit,
    verifiedOnly: Boolean,
    onVerifiedToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FilterSectionTitle("1. Guest Profile (Family / Business / Tourists / Everyone)")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Family", "Business Travelers", "Tourists", "Everyone").forEach { profile ->
                WorkflowChip(
                    label = profile,
                    isSelected = selectedOccupantType == profile,
                    onClick = { onOccupantTypeSelected(if (selectedOccupantType == profile) null else profile) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FilterSectionTitle("2. Room Type (Standard, Deluxe, Premium, Suites)")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Standard Room", "Deluxe AC", "Premium Suite", "Executive Suites").forEach { roomType ->
                WorkflowChip(
                    label = roomType,
                    isSelected = selectedRoomSharing == roomType,
                    onClick = { onRoomSharingSelected(if (selectedRoomSharing == roomType) null else roomType) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FilterSectionTitle("3. Hotel Amenities & Pricing per Night")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AmenityToggleChip("❄️ Central AC", selectedAcFilter == true) { onAcFilterSelected(if (selectedAcFilter == true) null else true) }
            AmenityToggleChip("🍳 Complimentary Breakfast", selectedAmenities.contains("Breakfast")) { onToggleAmenity("Breakfast") }
            AmenityToggleChip("🕒 24/7 Check-in", selectedAmenities.contains("24-Hour")) { onToggleAmenity("24-Hour") }
            AmenityToggleChip("📶 High-Speed Wi-Fi", selectedAmenities.contains("Wi-Fi")) { onToggleAmenity("Wi-Fi") }
            AmenityToggleChip("🚗 Valet Parking", selectedAmenities.contains("Parking")) { onToggleAmenity("Parking") }
            AmenityToggleChip("✈️ Airport / Station Shuttle", selectedAmenities.contains("Shuttle") || selectedAmenities.contains("Pickup")) { onToggleAmenity("Pickup") }
            AmenityToggleChip("🛡️ Verified Hotel", verifiedOnly) { onVerifiedToggle(!verifiedOnly) }
        }
    }
}

// ---------------- PG WORKFLOW ----------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PgRefinementView(
    selectedGender: GenderSuitability?,
    onGenderSelected: (GenderSuitability?) -> Unit,
    selectedRoomSharing: String?,
    onRoomSharingSelected: (String?) -> Unit,
    selectedFoodFilter: Boolean?,
    onFoodFilterSelected: (Boolean?) -> Unit,
    selectedAmenities: Set<String>,
    onToggleAmenity: (String) -> Unit,
    verifiedOnly: Boolean,
    onVerifiedToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FilterSectionTitle("1. PG Suitability (Boys / Girls / Both)")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WorkflowChip("Boys PG", Icons.Default.Male, selectedGender == GenderSuitability.BOYS_ONLY) {
                onGenderSelected(if (selectedGender == GenderSuitability.BOYS_ONLY) null else GenderSuitability.BOYS_ONLY)
            }
            WorkflowChip("Girls PG", Icons.Default.Female, selectedGender == GenderSuitability.GIRLS_ONLY) {
                onGenderSelected(if (selectedGender == GenderSuitability.GIRLS_ONLY) null else GenderSuitability.GIRLS_ONLY)
            }
            WorkflowChip("Co-Ed PG", Icons.Default.Diversity3, selectedGender == GenderSuitability.CO_ED || selectedGender == GenderSuitability.ALL) {
                onGenderSelected(if (selectedGender == GenderSuitability.CO_ED) null else GenderSuitability.CO_ED)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FilterSectionTitle("2. Room Choice (Private Single or Shared)")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Private Room", "2 Sharing", "3 Sharing").forEach { room ->
                WorkflowChip(
                    label = room,
                    isSelected = selectedRoomSharing == room,
                    onClick = { onRoomSharingSelected(if (selectedRoomSharing == room) null else room) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FilterSectionTitle("3. Homely Food, Wi-Fi & Security")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AmenityToggleChip("🍲 3 Homely Meals Included", selectedFoodFilter == true) { onFoodFilterSelected(if (selectedFoodFilter == true) null else true) }
            AmenityToggleChip("🧺 Free Laundry / Washing", selectedAmenities.contains("Laundry") || selectedAmenities.contains("Washing")) { onToggleAmenity("Laundry") }
            AmenityToggleChip("📶 Fiber Wi-Fi", selectedAmenities.contains("Wi-Fi")) { onToggleAmenity("Wi-Fi") }
            AmenityToggleChip("🔒 Biometric / CCTV Security", selectedAmenities.contains("Security") || selectedAmenities.contains("CCTV")) { onToggleAmenity("Security") }
            AmenityToggleChip("⚡ Sub-Meter Power Backup", selectedAmenities.contains("Power Backup")) { onToggleAmenity("Power Backup") }
            AmenityToggleChip("🛡️ Verified Host", verifiedOnly) { onVerifiedToggle(!verifiedOnly) }
        }
    }
}

// ---------------- ROOM & FLAT WORKFLOW ----------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoomAndFlatRefinementView(
    isFlat: Boolean,
    selectedOccupantType: String?,
    onOccupantTypeSelected: (String?) -> Unit,
    selectedRentalDuration: String?,
    onRentalDurationSelected: (String?) -> Unit,
    selectedFurnishing: String?,
    onFurnishingSelected: (String?) -> Unit,
    selectedRoomSharing: String?,
    onRoomSharingSelected: (String?) -> Unit,
    selectedAcFilter: Boolean?,
    onAcFilterSelected: (Boolean?) -> Unit,
    selectedAmenities: Set<String>,
    onToggleAmenity: (String) -> Unit,
    verifiedOnly: Boolean,
    onVerifiedToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FilterSectionTitle("1. Intended Occupants (Boys, Girls, Family, Anyone)")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Boys", "Girls", "Family", "Anyone").forEach { occ ->
                WorkflowChip(
                    label = occ,
                    isSelected = selectedOccupantType == occ,
                    onClick = { onOccupantTypeSelected(if (selectedOccupantType == occ) null else occ) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isFlat) {
            FilterSectionTitle("2. Flat BHK Configuration")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("1 BHK", "2 BHK", "3 BHK", "4 BHK", "Studio").forEach { bhk ->
                    WorkflowChip(
                        label = bhk,
                        isSelected = selectedRoomSharing == bhk,
                        onClick = { onRoomSharingSelected(if (selectedRoomSharing == bhk) null else bhk) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        FilterSectionTitle("3. Rental Duration (Weekly to Yearly)")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Weekly", "Monthly", "3 Months", "6 Months", "1 Year").forEach { term ->
                WorkflowChip(
                    label = term,
                    isSelected = selectedRentalDuration == term,
                    onClick = { onRentalDurationSelected(if (selectedRentalDuration == term) null else term) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FilterSectionTitle("4. Furnishing Status")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Fully Furnished", "Semi-Furnished", "Unfurnished").forEach { furn ->
                WorkflowChip(
                    label = furn,
                    isSelected = selectedFurnishing == furn,
                    onClick = { onFurnishingSelected(if (selectedFurnishing == furn) null else furn) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FilterSectionTitle("5. Key Amenities & Live Location")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AmenityToggleChip("🍳 Modular Kitchen Access", selectedAmenities.contains("Kitchen")) { onToggleAmenity("Kitchen") }
            AmenityToggleChip("❄️ Air Conditioner", selectedAcFilter == true) { onAcFilterSelected(if (selectedAcFilter == true) null else true) }
            AmenityToggleChip("📶 High-Speed Wi-Fi", selectedAmenities.contains("Wi-Fi")) { onToggleAmenity("Wi-Fi") }
            AmenityToggleChip("🅿️ Covered Car/Bike Parking", selectedAmenities.contains("Parking")) { onToggleAmenity("Parking") }
            AmenityToggleChip("⚡ 100% Power Backup", selectedAmenities.contains("Power Backup")) { onToggleAmenity("Power Backup") }
            AmenityToggleChip("📍 Live GPS Location", selectedAmenities.contains("Location")) { onToggleAmenity("Location") }
            AmenityToggleChip("🛡️ Verified Owner", verifiedOnly) { onVerifiedToggle(!verifiedOnly) }
        }
    }
}

// ---------------- QUICK STAY WORKFLOW ----------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickStayRefinementView(
    selectedOccupantType: String?,
    onOccupantTypeSelected: (String?) -> Unit,
    selectedRentalDuration: String?,
    onRentalDurationSelected: (String?) -> Unit,
    selectedAcFilter: Boolean?,
    onAcFilterSelected: (Boolean?) -> Unit,
    selectedAmenities: Set<String>,
    onToggleAmenity: (String) -> Unit,
    verifiedOnly: Boolean,
    onVerifiedToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FilterSectionTitle("1. Guest Type (Boys / Girls / Family / Anyone)")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Boys", "Girls", "Family", "Anyone").forEach { g ->
                WorkflowChip(
                    label = g,
                    isSelected = selectedOccupantType == g,
                    onClick = { onOccupantTypeSelected(if (selectedOccupantType == g) null else g) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FilterSectionTitle("2. Flexible Hourly / Daily Duration Slot")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("3 Hours (₹299+)", "6 Hours (₹499+)", "12 Hours (₹799+)", "Daily (24h)").forEach { slot ->
                WorkflowChip(
                    label = slot,
                    isSelected = selectedRentalDuration == slot,
                    onClick = { onRentalDurationSelected(if (selectedRentalDuration == slot) null else slot) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FilterSectionTitle("3. Transit Features & Instant Booking")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AmenityToggleChip("⚡ Instant Bed Confirmation", true) { }
            AmenityToggleChip("❄️ AC Sleeping Pod", selectedAcFilter == true) { onAcFilterSelected(if (selectedAcFilter == true) null else true) }
            AmenityToggleChip("🚿 Hot Power Shower", selectedAmenities.contains("Shower")) { onToggleAmenity("Shower") }
            AmenityToggleChip("🧳 Luggage Storage Locker", selectedAmenities.contains("Luggage") || selectedAmenities.contains("Locker")) { onToggleAmenity("Luggage") }
            AmenityToggleChip("📶 Fast Wi-Fi & Charging", selectedAmenities.contains("Wi-Fi")) { onToggleAmenity("Wi-Fi") }
            AmenityToggleChip("☕ Free Coffee Bar", selectedAmenities.contains("Coffee")) { onToggleAmenity("Coffee") }
            AmenityToggleChip("🛡️ Verified Transit Hub", verifiedOnly) { onVerifiedToggle(!verifiedOnly) }
        }
    }
}

// ---------------- HELPER REUSABLE COMPONENTS ----------------
@Composable
private fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun WorkflowChip(
    label: String,
    icon: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AmenityToggleChip(
    label: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) StynoEmerald.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) StynoEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StynoEmerald,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

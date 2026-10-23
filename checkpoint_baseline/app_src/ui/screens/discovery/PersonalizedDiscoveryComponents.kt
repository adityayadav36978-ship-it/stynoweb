package com.example.ui.screens.discovery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiscoveryStep
import com.example.data.model.FoodRequirementChoice
import com.example.data.model.GuestTarget
import com.example.data.model.PersonalizedDiscoveryState
import com.example.data.model.PropertyType
import com.example.data.model.SharingOptionType
import com.example.data.model.StayDurationChoice
import com.example.data.model.StayDurationUnit
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.StynoViewModel

/**
 * STEP 3: BASIC REQUIREMENTS VIEW (Primary Requirements + Progressive "More Preferences")
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step3BasicRequirementsView(
    viewModel: StynoViewModel,
    state: PersonalizedDiscoveryState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Select Stay Requirements",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Tell us what you need. Tap 'More Preferences' below for additional amenities.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Attached Bathroom
        RequirementHeader(title = "Attached Washroom / Bathroom", icon = Icons.Default.Bathtub)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SelectableRequirementCard(
                title = "Attached Bathroom",
                subtitle = "Private attached washroom",
                isSelected = state.attachedBathroomRequired,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setDiscoveryAttachedBathroom(true) }
            )
            SelectableRequirementCard(
                title = "Doesn't Matter",
                subtitle = "Attached or shared",
                isSelected = !state.attachedBathroomRequired,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setDiscoveryAttachedBathroom(false) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Air Conditioning (AC)
        RequirementHeader(title = "AC / Non-AC Preference", icon = Icons.Default.AcUnit)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectablePill(
                label = "AC Required",
                isSelected = state.acRequired == true,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setDiscoveryAc(true) }
            )
            SelectablePill(
                label = "Non-AC",
                isSelected = state.acRequired == false,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setDiscoveryAc(false) }
            )
            SelectablePill(
                label = "Any",
                isSelected = state.acRequired == null,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setDiscoveryAc(null) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Sharing Preference
        RequirementHeader(title = "Sharing / Room Type", icon = Icons.Default.Hotel)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SharingOptionType.values().forEach { option ->
                FilterChip(
                    selected = state.selectedSharingOption == option,
                    onClick = { viewModel.setDiscoverySharingPreference(option) },
                    label = { Text(option.displayName, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StynoBluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Guest Target / Category
        RequirementHeader(title = "Who is this stay for?", icon = Icons.Default.CheckCircle)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            GuestTarget.values().forEach { target ->
                FilterChip(
                    selected = state.selectedGuestTarget == target,
                    onClick = { viewModel.setDiscoveryGuestTarget(target) },
                    label = { Text(target.displayName, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StynoBluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 5. Stay Duration
        RequirementHeader(title = "Expected Stay Duration", icon = Icons.Default.Apartment)
        val defaultDurations = listOf(
            StayDurationChoice("1_month", "1 Month", 1, StayDurationUnit.MONTHS, isPopular = true),
            StayDurationChoice("3_months", "3 Months", 3, StayDurationUnit.MONTHS),
            StayDurationChoice("6_months", "6 Months", 6, StayDurationUnit.MONTHS),
            StayDurationChoice("1_year", "1 Year", 1, StayDurationUnit.YEARS),
            StayDurationChoice("daily", "Daily / Transient", 1, StayDurationUnit.DAYS),
            StayDurationChoice("hourly_3", "3 Hours (Quick Stay)", 3, StayDurationUnit.HOURS)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            defaultDurations.forEach { duration ->
                FilterChip(
                    selected = state.selectedDurationChoice.id == duration.id,
                    onClick = { viewModel.setDiscoveryDuration(duration) },
                    label = { Text(duration.label, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StynoBluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==================== MORE PREFERENCES (Progressive Disclosure) ====================
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { viewModel.toggleDiscoveryMorePreferences() }
                .testTag("toggle_more_preferences_button"),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = StynoBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "More Preferences",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (state.morePreferencesExpanded) "Tap to collapse" else "Kitchen, Wi-Fi, Laundry, Gym, Furnishing",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (state.morePreferencesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AnimatedVisibility(
            visible = state.morePreferencesExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Kitchen
                PreferenceToggleRow(
                    title = "Kitchen / Cooking Allowed",
                    subtitle = "Modular kitchen or self-cooking access",
                    icon = Icons.Default.Kitchen,
                    isChecked = state.kitchenRequired,
                    onCheckedChange = { viewModel.setDiscoveryKitchen(it) }
                )

                // Wi-Fi
                PreferenceToggleRow(
                    title = "High-Speed Wi-Fi Included",
                    subtitle = "Essential for study and work from home",
                    icon = Icons.Default.Wifi,
                    isChecked = state.wifiRequired,
                    onCheckedChange = { viewModel.setDiscoveryWifi(it) }
                )

                // Laundry
                PreferenceToggleRow(
                    title = "Washing Machine / Laundry",
                    subtitle = "Automated washing facility",
                    icon = Icons.Default.LocalLaundryService,
                    isChecked = state.laundryRequired,
                    onCheckedChange = { viewModel.setDiscoveryLaundry(it) }
                )

                // Gym
                PreferenceToggleRow(
                    title = "Gym / Fitness Facility",
                    subtitle = "On-site gym or yoga area",
                    icon = Icons.Default.FitnessCenter,
                    isChecked = state.gymRequired,
                    onCheckedChange = { viewModel.setDiscoveryGym(it) }
                )

                // Furnishing
                Column {
                    Text(
                        text = "Furnishing Type",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Any", "Furnished", "Semi-Furnished").forEach { furnishing ->
                            SelectablePill(
                                label = furnishing,
                                isSelected = state.furnishingPreference.equals(furnishing, ignoreCase = true),
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setDiscoveryFurnishing(furnishing) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * STEP 4: FOOD REQUIREMENT VIEW (Food Required vs Food Not Required)
 */
@Composable
fun Step4FoodRequirementView(
    viewModel: StynoViewModel,
    state: PersonalizedDiscoveryState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Food & Meal Preference",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Choose whether you want hygienic daily meals included with your stay.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Food Required Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.setDiscoveryFoodRequirement(FoodRequirementChoice.YES) }
                .testTag("food_required_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (state.selectedFoodRequirement == FoodRequirementChoice.YES)
                    StynoEmerald.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                width = if (state.selectedFoodRequirement == FoodRequirementChoice.YES) 2.dp else 1.dp,
                color = if (state.selectedFoodRequirement == FoodRequirementChoice.YES)
                    StynoEmerald
                else
                    MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(StynoEmerald.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = StynoEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Food Required",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Breakfast, Lunch & Dinner options",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (state.selectedFoodRequirement == FoodRequirementChoice.YES) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = StynoEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Includes hygienic, nutritious daily home-cooked meals prepared in verified canteens with FSSAI hygiene standards.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Food Not Required Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.setDiscoveryFoodRequirement(FoodRequirementChoice.NO) }
                .testTag("food_not_required_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (state.selectedFoodRequirement == FoodRequirementChoice.NO)
                    StynoBluePrimary.copy(alpha = 0.10f)
                else
                    MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                width = if (state.selectedFoodRequirement == FoodRequirementChoice.NO) 2.dp else 1.dp,
                color = if (state.selectedFoodRequirement == FoodRequirementChoice.NO)
                    StynoBluePrimary
                else
                    MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Food Not Required",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Room only / Self cooking / Tiffin",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (state.selectedFoodRequirement == FoodRequirementChoice.NO) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = StynoBluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pay only for accommodation and stay amenities. Ideal if you arrange your own meals, order online, or use shared kitchen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Flexible / Does Not Matter Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.setDiscoveryFoodRequirement(FoodRequirementChoice.DOES_NOT_MATTER) },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (state.selectedFoodRequirement == FoodRequirementChoice.DOES_NOT_MATTER)
                    StynoAccent.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                width = if (state.selectedFoodRequirement == FoodRequirementChoice.DOES_NOT_MATTER) 2.dp else 1.dp,
                color = if (state.selectedFoodRequirement == FoodRequirementChoice.DOES_NOT_MATTER)
                    StynoAccent
                else
                    MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Flexible / Either is Fine",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Show all stays with or without food",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (state.selectedFoodRequirement == FoodRequirementChoice.DOES_NOT_MATTER) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = StynoAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

/**
 * STEP 5: BUDGET / PRICE REQUIREMENT VIEW
 * Simple, natural input: "I need a stay within ₹____"
 * With duration unit badge (Per month, Per day, Quick stay) and quick preset chips.
 */
@Composable
fun Step5BudgetView(
    viewModel: StynoViewModel,
    state: PersonalizedDiscoveryState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val isHourly = state.selectedDurationChoice.unit == StayDurationUnit.HOURS
    val isDaily = state.selectedDurationChoice.unit == StayDurationUnit.DAYS

    val durationLabel = when {
        isHourly -> "Quick Stay (Hourly)"
        isDaily -> "Per Day"
        else -> "Per Month"
    }

    val quickBudgetPresets = when {
        isHourly -> listOf(499.0, 799.0, 1199.0, 1999.0)
        isDaily -> listOf(699.0, 999.0, 1499.0, 2499.0)
        else -> listOf(4000.0, 6000.0, 8000.0, 10000.0, 14000.0, 20000.0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Your Budget Requirement",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Enter your maximum target price limit for this stay.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.5.dp, StynoBluePrimary.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StynoBluePrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = durationLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = StynoBluePrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "I need a stay within",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.userBudgetInput,
                    onValueChange = { input ->
                        val sanitized = input.filter { it.isDigit() }
                        val num = sanitized.toDoubleOrNull() ?: 0.0
                        viewModel.setDiscoveryUserBudget(num, sanitized)
                    },
                    prefix = {
                        Text(
                            text = "₹ ",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = StynoBluePrimary
                        )
                    },
                    suffix = {
                        Text(
                            text = if (isHourly) "/stay" else if (isDaily) "/day" else "/mo",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StynoBluePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_budget_input_field")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickBudgetPresets.forEach { preset ->
                        val isSelected = state.userBudget == preset
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.clickable {
                                viewModel.setDiscoveryUserBudget(preset, preset.toInt().toString())
                            }
                        ) {
                            Text(
                                text = "₹${preset.toInt()}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 0% Brokerage Guarantee Banner
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = StynoEmerald.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = StynoEmerald,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "0% Brokerage Guarantee",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = StynoEmerald
                    )
                    Text(
                        text = "All prices are set directly by property owners. STYNO never charges broker fees.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * NO MATCHING RESULT VIEW
 * Section 11:
 * "If no suitable listing exists, DO NOT show confusing empty screens.
 * Clearly say: 'No matching stays found.'
 * Provide simple next options: Change location, Change stay type, Adjust budget, Open More Preferences, Try again.
 * Do NOT automatically push unrelated categories."
 */
@Composable
fun NoMatchingStaysView(
    viewModel: StynoViewModel,
    state: PersonalizedDiscoveryState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("no_matching_stays_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No matching stays found.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "We couldn't find any stays matching all your selected criteria in ${state.selectedLocality}. Try adjusting one or more options below:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // The 5 Requested Action Options
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.jumpToDiscoveryStep(DiscoveryStep.LOCATION) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change Location", fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { viewModel.jumpToDiscoveryStep(DiscoveryStep.PROPERTY_TYPE) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Hotel, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change Stay Type", fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { viewModel.jumpToDiscoveryStep(DiscoveryStep.BUDGET) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Adjust Budget Limit", fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { viewModel.jumpToDiscoveryStep(DiscoveryStep.BASIC_REQUIREMENTS) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open More Preferences", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { viewModel.resetDiscoveryCriteria() },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Try Again (Reset Filters)", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))

            // Optional Intentional toggle: "Explore other available stay types"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setDiscoveryAllowOtherStayTypes(!state.allowOtherStayTypes)
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Explore other stay types in this area",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Optional: Show Hostels, Hotels, Rooms, Flats, or PGs nearby",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.allowOtherStayTypes,
                    onCheckedChange = { viewModel.setDiscoveryAllowOtherStayTypes(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = StynoBluePrimary)
                )
            }
        }
    }
}

// ---------------- Helper Components ----------------

@Composable
private fun RequirementHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = StynoBluePrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SelectableRequirementCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) StynoBluePrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.5.dp,
            if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SelectablePill(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            1.dp,
            if (isSelected) StynoBluePrimary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PreferenceToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isChecked) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = StynoBluePrimary)
        )
    }
}

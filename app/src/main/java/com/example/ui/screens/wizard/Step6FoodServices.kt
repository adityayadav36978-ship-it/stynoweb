package com.example.ui.screens.wizard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodDietaryType
import com.example.data.model.FoodPricingModel
import com.example.data.model.FoodServiceConfig
import com.example.data.model.MealOption

@Composable
fun Step6FoodServices(
    state: PropertyWizardState,
    onUpdateState: (PropertyWizardState) -> Unit,
    modifier: Modifier = Modifier
) {
    val food = state.foodConfig

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step Banner
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 6 of 10: Food Services & Mess",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Specify dietary choices (Veg/Non-Veg/Jain), meals provided, mess subscription or per-meal pricing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Toggle: Food Service Available
        OutlinedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (food.hasFoodService) Color(0xFF2E7D32) else Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = "Food & Mess Service Provided",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (food.hasFoodService) "In-house kitchen / mess meals provided" else "Self-cooking or outside food delivery only",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = food.hasFoodService,
                    onCheckedChange = { onUpdateState(state.copy(foodConfig = food.copy(hasFoodService = it))) },
                    modifier = Modifier.testTag("switch_food_service")
                )
            }
        }

        AnimatedVisibility(visible = food.hasFoodService) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Dietary Classification
                OutlinedCard(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Dietary Classification",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        FoodDietaryType.values().forEach { diet ->
                            val isSelected = food.dietaryType == diet
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onUpdateState(state.copy(foodConfig = food.copy(dietaryType = diet))) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(diet.badgeColorHex).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) Color(diet.badgeColorHex) else MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onUpdateState(state.copy(foodConfig = food.copy(dietaryType = diet))) }
                                    )
                                    Text(
                                        text = diet.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(diet.badgeColorHex) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Meals Offered & Timings
                OutlinedCard(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Meals Offered & Serving Timings",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        food.meals.forEachIndexed { index, meal ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (meal.isEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                border = BorderStroke(1.dp, if (meal.isEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Checkbox(
                                                checked = meal.isEnabled,
                                                onCheckedChange = { checked ->
                                                    val updated = food.meals.toMutableList()
                                                    updated[index] = meal.copy(isEnabled = checked)
                                                    onUpdateState(state.copy(foodConfig = food.copy(meals = updated)))
                                                }
                                            )
                                            Text(
                                                text = meal.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        if (food.pricingModel == FoodPricingModel.PAY_PER_MEAL && meal.isEnabled) {
                                            Text(
                                                text = "₹${meal.perMealPrice.toInt()}/meal",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    if (meal.isEnabled) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = meal.timings,
                                                onValueChange = { newTiming ->
                                                    val updated = food.meals.toMutableList()
                                                    updated[index] = meal.copy(timings = newTiming)
                                                    onUpdateState(state.copy(foodConfig = food.copy(meals = updated)))
                                                },
                                                label = { Text("Serving Hours") },
                                                leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                                                modifier = Modifier.weight(1.2f)
                                            )

                                            if (food.pricingModel == FoodPricingModel.PAY_PER_MEAL) {
                                                OutlinedTextField(
                                                    value = meal.perMealPrice.toInt().toString(),
                                                    onValueChange = { p ->
                                                        val updated = food.meals.toMutableList()
                                                        updated[index] = meal.copy(perMealPrice = p.toDoubleOrNull() ?: 0.0)
                                                        onUpdateState(state.copy(foodConfig = food.copy(meals = updated)))
                                                    },
                                                    label = { Text("Price (₹)") },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    modifier = Modifier.weight(0.8f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Food Pricing Model
                OutlinedCard(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Food Pricing Model",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        FoodPricingModel.values().forEach { model ->
                            val isSelected = food.pricingModel == model
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onUpdateState(state.copy(foodConfig = food.copy(pricingModel = model))) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onUpdateState(state.copy(foodConfig = food.copy(pricingModel = model))) }
                                    )
                                    Text(
                                        text = model.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        // If Monthly Mess Charge
                        AnimatedVisibility(visible = food.pricingModel == FoodPricingModel.MONTHLY_MESS) {
                            OutlinedTextField(
                                value = if (food.monthlyCharge > 0) food.monthlyCharge.toInt().toString() else "",
                                onValueChange = { onUpdateState(state.copy(foodConfig = food.copy(monthlyCharge = it.toDoubleOrNull() ?: 0.0))) },
                                label = { Text("Monthly Mess Subscription Fee (₹ / month) *") },
                                placeholder = { Text("2500") },
                                leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("input_food_monthly_charge")
                            )
                        }

                        // Menu Description & Kitchen Hygiene Notes
                        OutlinedTextField(
                            value = food.description,
                            onValueChange = { onUpdateState(state.copy(foodConfig = food.copy(description = it))) },
                            label = { Text("Menu Highlights & Hygiene Certification") },
                            placeholder = { Text("e.g. FSSAI certified kitchen, North & South Indian meals, Sunday special chicken/paneer") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth().testTag("input_food_description")
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.viewmodel.StynoViewModel

@Composable
fun CustomerIntentScreen(
    viewModel: StynoViewModel,
    onBack: () -> Unit,
    onRequirementMatched: () -> Unit,
    modifier: Modifier = Modifier
) {
    PersonalizedDiscoveryScreen(
        viewModel = viewModel,
        onBack = onBack,
        onNavigateToSearch = onRequirementMatched,
        modifier = modifier
    )
}

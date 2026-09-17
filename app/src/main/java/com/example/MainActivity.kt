package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiSupportAssistantSheet
import com.example.ui.components.ComplaintDetailSheet
import com.example.ui.components.FileComplaintDialog
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.KycVerificationDialog
import com.example.ui.components.LocationSelectorDialog
import com.example.ui.components.NotificationSheet
import com.example.ui.components.PropertyComparisonSheet
import com.example.ui.components.SafetySosConfirmDialog
import com.example.ui.components.StynoBottomBar
import com.example.ui.components.StynoTopBar

import com.example.ui.screens.AddPropertyScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AiWorkspaceScreen
import com.example.ui.screens.AuthenticationScreen
import com.example.ui.screens.BookingConfirmationScreen
import com.example.ui.screens.BookingFlowScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CustomerIntentScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LocationOnboardingScreen
import com.example.ui.screens.LocationSelectionScreen
import com.example.ui.screens.MyBookingsScreen
import com.example.ui.screens.OnboardingFlowScreen
import com.example.ui.screens.OwnerDashboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PropertyDetailScreen
import com.example.ui.screens.SafetyCenterScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WelcomeEntryScreen
import com.example.ui.theme.StynoTheme
import com.example.ui.theme.StynoAccent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.LocationViewModel
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel

@androidx.compose.material3.ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val viewModel: StynoViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            com.example.ui.components.GoogleMapsManager.initialize(applicationContext)
        } catch (t: Throwable) {
            android.util.Log.w("MainActivity", "GoogleMapsManager startup init notice: ${t.message}")
        }
        enableEdgeToEdge()
        setContent {
            StynoTheme {
                StynoApp(
                    viewModel = viewModel,
                    authViewModel = authViewModel,
                    locationViewModel = locationViewModel
                )
            }
        }
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun StynoApp(
    viewModel: StynoViewModel,
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    locationViewModel: LocationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showSplash by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(onSplashFinished = { showSplash = false })
        return
    }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val selectedGlobalLocation by viewModel.selectedGlobalLocation.collectAsStateWithLifecycle()
    val savedPropertyIds by viewModel.savedPropertyIds.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    val showLocationDialog by viewModel.showLocationDialog.collectAsStateWithLifecycle()
    val showFilterSheet by viewModel.showFilterSheet.collectAsStateWithLifecycle()
    val showNotificationSheet by viewModel.showNotificationSheet.collectAsStateWithLifecycle()
    val showSosConfirmDialog by viewModel.showSosConfirmDialog.collectAsStateWithLifecycle()

    val showComplaintDialog by viewModel.showComplaintDialog.collectAsStateWithLifecycle()
    val complaintTargetProperty by viewModel.complaintTargetProperty.collectAsStateWithLifecycle()
    val selectedComplaint by viewModel.selectedComplaint.collectAsStateWithLifecycle()
    val showAiSupportSheet by viewModel.showAiSupportSheet.collectAsStateWithLifecycle()
    val showComparisonSheet by viewModel.showComparisonSheet.collectAsStateWithLifecycle()
    val comparisonProperties by viewModel.comparisonProperties.collectAsStateWithLifecycle()
    val showKycDialog by viewModel.showKycDialog.collectAsStateWithLifecycle()
    val kycDocType by viewModel.kycDocType.collectAsStateWithLifecycle()
    val kycMaskedId by viewModel.kycMaskedId.collectAsStateWithLifecycle()
    val showLoginPromptForWishlist by viewModel.showLoginPromptForWishlist.collectAsStateWithLifecycle()
    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()

    // Filter states for FilterBottomSheet

    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedGender by viewModel.selectedGenderSuitability.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val selectedAmenities by viewModel.selectedAmenities.collectAsStateWithLifecycle()
    val verifiedOnly by viewModel.verifiedOnly.collectAsStateWithLifecycle()
    val minPriceFilter by viewModel.minPriceFilter.collectAsStateWithLifecycle()
    val maxPriceFilter by viewModel.maxPriceFilter.collectAsStateWithLifecycle()
    val minRatingFilter by viewModel.minRatingFilter.collectAsStateWithLifecycle()
    val maxDistanceKm by viewModel.maxDistanceKm.collectAsStateWithLifecycle()
    val selectedAttachedBathroom by viewModel.selectedAttachedBathroomFilter.collectAsStateWithLifecycle()
    val selectedKitchen by viewModel.selectedKitchenFilter.collectAsStateWithLifecycle()
    val selectedAc by viewModel.selectedAcFilter.collectAsStateWithLifecycle()
    val selectedFood by viewModel.selectedFoodFilter.collectAsStateWithLifecycle()
    val selectedCleanliness by viewModel.selectedCleanlinessFilter.collectAsStateWithLifecycle()
    val minReviewCountFilter by viewModel.minReviewCountFilter.collectAsStateWithLifecycle()
    val userCoordinates by viewModel.userCoordinates.collectAsStateWithLifecycle()
    val isFetchingLocation by viewModel.isFetchingLocation.collectAsStateWithLifecycle()
    val availableAmenities by viewModel.availableAmenities.collectAsStateWithLifecycle()
    val propertyTypeCounts by viewModel.propertyTypeCounts.collectAsStateWithLifecycle()
    val filteredProperties by viewModel.filteredProperties.collectAsStateWithLifecycle()

    // Handle back button on Android
    BackHandler(enabled = currentScreen != Screen.WELCOME && currentScreen != Screen.ONBOARDING && currentScreen != Screen.AUTH) {
        viewModel.navigateBack()
    }

    val isMainTabScreen = currentScreen == Screen.HOME ||
        currentScreen == Screen.SEARCH ||
        currentScreen == Screen.BOOKINGS ||
        currentScreen == Screen.SAVED ||
        currentScreen == Screen.PROFILE

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen == Screen.HOME) {
                StynoTopBar(
                    selectedLocation = "${selectedGlobalLocation.countryFlag} ${selectedGlobalLocation.toDisplayText()}",
                    onLocationClick = { viewModel.navigateTo(Screen.LOCATION_PICKER) },
                    onNotificationClick = { viewModel.setShowNotificationSheet(true) },
                    onSafetyClick = { viewModel.navigateTo(Screen.SAFETY_CENTER) },
                    unreadNotificationCount = notifications.count { !it.isRead }
                )
            }
        },
        bottomBar = {
            if (isMainTabScreen) {
                StynoBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) },
                    savedCount = savedPropertyIds.size,
                    activeBookingsCount = bookings.count { it.status.name == "UPCOMING" || it.status.name == "ACTIVE" }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = if (currentScreen == Screen.HOME) innerPadding.calculateTopPadding() else 0.dp,
                    bottom = if (isMainTabScreen) innerPadding.calculateBottomPadding() else 0.dp
                )
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
                     slideInVertically(animationSpec = tween(320, easing = FastOutSlowInEasing)) { it / 24 }) togetherWith
                    (fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)) +
                     slideOutVertically(animationSpec = tween(240, easing = FastOutSlowInEasing)) { -it / 28 })
                },
                label = "screen_transition"
            ) { targetScreen ->
                when (targetScreen) {
                    Screen.WELCOME -> WelcomeEntryScreen(
                        viewModel = viewModel,
                        authViewModel = authViewModel,
                        onSelectOwner = {
                            viewModel.setUserRole(com.example.ui.viewmodel.UserRole.OWNER)
                            viewModel.navigateTo(Screen.OWNER_DASHBOARD)
                        },
                        onSelectCustomer = {
                            viewModel.setUserRole(com.example.ui.viewmodel.UserRole.GUEST)
                            viewModel.setGuestAuthentication()
                            viewModel.navigateTo(Screen.HOME)
                        },
                        onOpenCustomerWizard = {
                            viewModel.navigateTo(Screen.CUSTOMER_INTENT)
                        }
                    )
                    Screen.AI_WORKSPACE -> AiWorkspaceScreen(viewModel = viewModel)
                    Screen.ONBOARDING -> OnboardingFlowScreen(
                        viewModel = viewModel,
                        onFinished = { viewModel.navigateTo(Screen.HOME) }
                    )
                    Screen.AUTH -> AuthenticationScreen(
                        authViewModel = authViewModel,
                        stynoViewModel = viewModel,
                        onAuthSuccess = { user ->
                            viewModel.setAuthenticated(
                                identifier = user.email?.ifBlank { null }
                                    ?: user.phoneNumber
                                    ?: user.uid,
                                phone = user.phoneNumber ?: "",
                                name = user.displayName ?: ""
                            )
                            val hasPendingWishlist = viewModel.pendingWishlistPropertyId.value != null
                            viewModel.savePendingWishlistPropertyIfAny()
                            if (hasPendingWishlist) {
                                viewModel.navigateTo(Screen.SAVED)
                            } else if (viewModel.userRole.value == com.example.ui.viewmodel.UserRole.OWNER) {
                                viewModel.navigateTo(Screen.OWNER_DASHBOARD)
                            } else {
                                viewModel.navigateTo(Screen.HOME)
                            }
                        },
                        onNavigateBack = {
                            viewModel.navigateBack()
                        }
                    )
                    Screen.LOCATION_ONBOARDING -> LocationOnboardingScreen(
                        viewModel = viewModel,
                        onLocationConfirmed = { viewModel.navigateTo(Screen.HOME) }
                    )
                    Screen.LOCATION_PICKER -> LocationSelectionScreen(
                        viewModel = viewModel,
                        locationViewModel = locationViewModel,
                        onLocationConfirmed = { viewModel.navigateTo(Screen.HOME) },
                        canGoBack = true
                    )
                    Screen.CUSTOMER_INTENT -> CustomerIntentScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() },
                        onRequirementMatched = { viewModel.navigateTo(Screen.SEARCH) }
                    )
                    Screen.HOME -> HomeScreen(viewModel = viewModel)
                    Screen.SEARCH -> SearchScreen(viewModel = viewModel)
                    Screen.BOOKINGS -> MyBookingsScreen(viewModel = viewModel)
                    Screen.SAVED -> SavedScreen(viewModel = viewModel)
                    Screen.PROFILE -> ProfileScreen(viewModel = viewModel)
                    Screen.PROPERTY_DETAIL -> PropertyDetailScreen(viewModel = viewModel)
                    Screen.BOOKING_FLOW -> BookingFlowScreen(viewModel = viewModel)
                    Screen.BOOKING_CONFIRMATION -> BookingConfirmationScreen(viewModel = viewModel)
                    Screen.SAFETY_CENTER -> SafetyCenterScreen(viewModel = viewModel)
                    Screen.CHAT -> ChatScreen(viewModel = viewModel)
                    Screen.OWNER_DASHBOARD -> OwnerDashboardScreen(viewModel = viewModel)
                    Screen.ADD_PROPERTY -> AddPropertyScreen(viewModel = viewModel)
                    Screen.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Modal Dialogs & Sheets
    if (showLocationDialog) {
        LocationSelectorDialog(
            cities = viewModel.availableCities,
            currentCity = selectedCity,
            onCitySelected = { city -> viewModel.selectCity(city) },
            onGlobalLocationSelected = { loc -> viewModel.selectGlobalLocation(loc) },
            onUseCurrentLocation = { viewModel.useCurrentGpsLocation() },
            isFetchingLocation = isFetchingLocation,
            userCoordinates = userCoordinates,
            onDismiss = { viewModel.setShowLocationDialog(false) }
        )
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            selectedCategory = selectedCategory,
            selectedGender = selectedGender,
            selectedSort = selectedSort,
            selectedAmenities = selectedAmenities,
            verifiedOnly = verifiedOnly,
            minPrice = minPriceFilter,
            maxPrice = maxPriceFilter,
            minRating = minRatingFilter,
            maxDistance = maxDistanceKm,
            selectedAttachedBathroom = selectedAttachedBathroom,
            selectedKitchen = selectedKitchen,
            selectedAc = selectedAc,
            selectedFood = selectedFood,
            selectedCleanliness = selectedCleanliness,
            minReviews = minReviewCountFilter,
            availableAmenities = availableAmenities,
            propertyTypeCounts = propertyTypeCounts,
            matchingResultsCount = filteredProperties.size,
            onApply = { category, gender, sort, amenities, verified, minPrice, maxPrice, minRating, maxDistance ->
                viewModel.applyAllFilters(
                    category = category,
                    gender = gender,
                    sort = sort,
                    amenities = amenities,
                    verifiedOnly = verified,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    minRating = minRating,
                    maxDistance = maxDistance
                )
            },
            onApplyExtended = { category, gender, sort, amenities, verified, minPrice, maxPrice, minRating, maxDistance, attachedBathroom, kitchen, ac, food, cleanliness, minReviews ->
                viewModel.applyAllFilters(
                    category = category,
                    gender = gender,
                    sort = sort,
                    amenities = amenities,
                    verifiedOnly = verified,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    minRating = minRating,
                    maxDistance = maxDistance,
                    attachedBathroom = attachedBathroom,
                    kitchen = kitchen,
                    ac = ac,
                    food = food,
                    cleanliness = cleanliness,
                    minReviews = minReviews
                )
            },
            onReset = { viewModel.resetFilters() },
            onDismiss = { viewModel.setShowFilterSheet(false) }
        )
    }

    if (showNotificationSheet) {
        NotificationSheet(
            notifications = notifications,
            onDismiss = { viewModel.setShowNotificationSheet(false) }
        )
    }

    if (showSosConfirmDialog) {
        SafetySosConfirmDialog(
            onConfirm = { viewModel.confirmSosActivation() },
            onDismiss = { viewModel.dismissSosModal() }
        )
    }

    if (showComplaintDialog) {
        FileComplaintDialog(
            targetProperty = complaintTargetProperty,
            allProperties = allProperties,
            isOwner = userRole == com.example.ui.viewmodel.UserRole.OWNER,
            onDismiss = { viewModel.dismissComplaintDialog() },
            onSubmit = { title, desc, cat, priority, prop ->
                viewModel.submitComplaint(
                    title = title,
                    description = desc,
                    category = cat,
                    priority = priority,
                    property = prop,
                    isOwnerComplaint = userRole == com.example.ui.viewmodel.UserRole.OWNER
                )
            }
        )
    }

    selectedComplaint?.let { complaint ->
        ComplaintDetailSheet(
            complaint = complaint,
            canUpdateStatus = userRole == com.example.ui.viewmodel.UserRole.ADMIN || userRole == com.example.ui.viewmodel.UserRole.OWNER,
            onDismiss = { viewModel.selectComplaint(null) },
            onUpdateStatus = { newStatus, notes ->
                viewModel.updateComplaintResolution(complaint.id, newStatus, notes)
            }
        )
    }

    if (showAiSupportSheet) {
        AiSupportAssistantSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowAiSupportSheet(false) }
        )
    }

    if (showComparisonSheet) {
        PropertyComparisonSheet(
            viewModel = viewModel,
            properties = comparisonProperties,
            onDismiss = { viewModel.setShowComparisonSheet(false) }
        )
    }

    if (showKycDialog) {
        KycVerificationDialog(
            currentDocType = kycDocType,
            currentMaskedId = kycMaskedId,
            onDismiss = { viewModel.setShowKycDialog(false) },
            onSubmitVerification = { docType, docNumber, base64, localPath ->
                viewModel.submitKycVerification(docType, docNumber, base64, localPath)
            }
        )
    }

    if (showLoginPromptForWishlist) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowLoginPromptForWishlist(false) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = StynoAccent,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Sign in to save to Wishlist",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Create an account or sign in to save this property to your personal Wishlist, organize your favorite stays, and access them across all your devices.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setShowLoginPromptForWishlist(false)
                        viewModel.navigateTo(Screen.AUTH)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("wishlist_login_confirm_button")
                ) {
                    Text("Sign In / Register")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.setShowLoginPromptForWishlist(false) },
                    modifier = Modifier.testTag("wishlist_login_dismiss_button")
                ) {
                    Text("Not Now")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}


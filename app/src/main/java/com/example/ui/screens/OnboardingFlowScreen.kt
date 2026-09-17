package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GlobalLocationCatalog
import com.example.data.model.GlobalLocationItem
import com.example.data.model.LocationCategory
import com.example.data.model.PropertyType
import com.example.data.model.UserPreferences
import com.example.ui.components.StynoHeroLogo
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoAmber
import com.example.ui.theme.StynoBlueLight
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import com.example.ui.viewmodel.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class OnboardingStep(val stepNumber: Int, val title: String) {
    AUTH(1, "Sign In / Register"),
    ACCOUNT_TYPE(2, "Select Role"),
    LOCATION(3, "Select Location"),
    STAY_TYPE(4, "Stay Type"),
    SHARING_TYPE(5, "Room Preference"),
    USER_PURPOSE(6, "Your Purpose")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingFlowScreen(
    viewModel: StynoViewModel,
    initialStep: OnboardingStep = OnboardingStep.AUTH,
    isEditingPreferencesOnly: Boolean = false,
    onFinished: () -> Unit = { viewModel.navigateTo(Screen.HOME) },
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(initialStep) }
    val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()

    // Step 1: Auth State (Email + Password Login / Signup)
    var authMode by remember { mutableStateOf("SIGN_IN") } // "SIGN_IN" or "SIGN_UP"
    var authFullName by remember { mutableStateOf("") }
    var authEmail by remember { mutableStateOf("") }
    var authPassword by remember { mutableStateOf("") }
    var authConfirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    // Step 2: Account Type State
    var selectedRole by remember { mutableStateOf(userPrefs.userRole) }

    // Step 3: Global Location State
    var selectedCountry by remember { mutableStateOf(userPrefs.selectedCountry) }
    var selectedState by remember { mutableStateOf(userPrefs.selectedState) }
    var selectedCity by remember { mutableStateOf(userPrefs.selectedCity) }
    var selectedLocality by remember { mutableStateOf(userPrefs.selectedLocality) }
    var selectedLandmark by remember { mutableStateOf(userPrefs.selectedLandmark) }
    var locationSearchQuery by remember { mutableStateOf("") }
    var showCustomLocationDialog by remember { mutableStateOf(false) }
    val isGpsLocating by viewModel.isGpsLocating.collectAsStateWithLifecycle()
    val gpsStatus by viewModel.gpsDetectionStatus.collectAsStateWithLifecycle()
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }

    // Step 4: Stay Type State (Multi-select)
    var selectedStayTypes by remember { mutableStateOf(userPrefs.selectedStayTypes) }

    // Step 5: Sharing / Room Preferences (Multi-select)
    var selectedSharingPrefs by remember { mutableStateOf(userPrefs.selectedSharingPreferences) }

    // Step 6: User Purpose State (Multi-select)
    var selectedPurposes by remember { mutableStateOf(userPrefs.selectedUserPurposes) }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current



    // Permission Launcher for Location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            permissionDeniedMessage = null
            viewModel.detectAndSetGpsLocation { success, detectedItem ->
                if (success && detectedItem != null) {
                    selectedCountry = detectedItem.country
                    selectedState = detectedItem.state
                    selectedCity = detectedItem.city
                    selectedLocality = detectedItem.locality ?: detectedItem.name
                    selectedLandmark = detectedItem.popularLandmarks.firstOrNull() ?: "Near Metro / Landmark"
                }
            }
        } else {
            permissionDeniedMessage = "Location permission is required for automatic detection. You can select your location manually."
        }
    }

    // Back handling within onboarding
    BackHandler {
        when (currentStep) {
            OnboardingStep.AUTH -> {
                if (isEditingPreferencesOnly) {
                    onFinished()
                }
            }
            OnboardingStep.ACCOUNT_TYPE -> currentStep = OnboardingStep.AUTH
            OnboardingStep.LOCATION -> currentStep = OnboardingStep.ACCOUNT_TYPE
            OnboardingStep.STAY_TYPE -> currentStep = OnboardingStep.LOCATION
            OnboardingStep.SHARING_TYPE -> currentStep = OnboardingStep.STAY_TYPE
            OnboardingStep.USER_PURPOSE -> currentStep = OnboardingStep.SHARING_TYPE
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_flow_screen"),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStep != OnboardingStep.AUTH || isEditingPreferencesOnly) {
                            IconButton(
                                onClick = {
                                    when (currentStep) {
                                        OnboardingStep.AUTH -> onFinished()
                                        OnboardingStep.ACCOUNT_TYPE -> currentStep = OnboardingStep.AUTH
                                        OnboardingStep.LOCATION -> currentStep = OnboardingStep.ACCOUNT_TYPE
                                        OnboardingStep.STAY_TYPE -> currentStep = OnboardingStep.LOCATION
                                        OnboardingStep.SHARING_TYPE -> currentStep = OnboardingStep.STAY_TYPE
                                        OnboardingStep.USER_PURPOSE -> currentStep = OnboardingStep.SHARING_TYPE
                                    }
                                },
                                modifier = Modifier.testTag("onboarding_back_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(48.dp))
                        }

                        // Stepper Indicator Text
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "STYNO ONBOARDING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.5.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Step ${currentStep.stepNumber} of 6: ${currentStep.title}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Skip / Continue As Guest
                        if (currentStep == OnboardingStep.AUTH && !isEditingPreferencesOnly) {
                            TextButton(
                                onClick = {
                                    viewModel.setGuestAuthentication()
                                    currentStep = OnboardingStep.ACCOUNT_TYPE
                                },
                                modifier = Modifier.testTag("onboarding_guest_skip_btn")
                            ) {
                                Text(
                                    text = "Explore",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(48.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Linear Progress Bar across 6 Steps
                    LinearProgressIndicator(
                        progress = { currentStep.stepNumber.toFloat() / 6f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        },
        bottomBar = {
            // Persistent Next / Continue Action Bar (Steps 3-6)
            if (currentStep != OnboardingStep.AUTH && currentStep != OnboardingStep.ACCOUNT_TYPE) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = when (currentStep) {
                                    OnboardingStep.LOCATION -> "Target City"
                                    OnboardingStep.STAY_TYPE -> "${selectedStayTypes.size} Selected"
                                    OnboardingStep.SHARING_TYPE -> "${selectedSharingPrefs.size} Preferences"
                                    OnboardingStep.USER_PURPOSE -> "${selectedPurposes.size} Selected"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = when (currentStep) {
                                    OnboardingStep.LOCATION -> "$selectedLocality, $selectedCity"
                                    OnboardingStep.STAY_TYPE -> if (selectedStayTypes.isEmpty()) "Select at least 1" else selectedStayTypes.joinToString(", ") { it.displayName }
                                    OnboardingStep.SHARING_TYPE -> if (selectedSharingPrefs.isEmpty()) "Any Room" else selectedSharingPrefs.take(2).joinToString(", ")
                                    OnboardingStep.USER_PURPOSE -> if (selectedPurposes.isEmpty()) "Select your purpose" else selectedPurposes.take(2).joinToString(", ")
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 180.dp)
                            )
                        }

                        Button(
                            onClick = {
                                when (currentStep) {
                                    OnboardingStep.LOCATION -> currentStep = OnboardingStep.STAY_TYPE
                                    OnboardingStep.STAY_TYPE -> {
                                        if (selectedStayTypes.isEmpty()) {
                                            selectedStayTypes = setOf(PropertyType.HOSTEL, PropertyType.PG)
                                        }
                                        currentStep = OnboardingStep.SHARING_TYPE
                                    }
                                    OnboardingStep.SHARING_TYPE -> {
                                        if (selectedSharingPrefs.isEmpty()) {
                                            selectedSharingPrefs = setOf("Private Room", "Single Sharing", "Double Sharing")
                                        }
                                        currentStep = OnboardingStep.USER_PURPOSE
                                    }
                                    OnboardingStep.USER_PURPOSE -> {
                                        if (selectedPurposes.isEmpty()) {
                                            selectedPurposes = setOf("Student", "Working Professional")
                                        }
                                        // Finalize and save preferences
                                        viewModel.completeFullOnboarding(
                                            country = selectedCountry,
                                            state = selectedState,
                                            city = selectedCity,
                                            locality = selectedLocality,
                                            landmark = selectedLandmark,
                                            stayTypes = selectedStayTypes,
                                            sharingPreferences = selectedSharingPrefs,
                                            purposes = selectedPurposes
                                        )
                                        onFinished()
                                    }
                                    else -> {}
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("onboarding_continue_step_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (currentStep == OnboardingStep.USER_PURPOSE) "Personalize Home ✨" else "Continue",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Icon(
                                    imageVector = if (currentStep == OnboardingStep.USER_PURPOSE) Icons.Default.AutoAwesome else Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState.stepNumber > initialState.stepNumber) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            label = "onboarding_step_transition"
        ) { step ->
            when (step) {
                // ==========================================
                // STEP 1: AUTHENTICATION / LOGIN / SIGN UP
                // ==========================================
                OnboardingStep.AUTH -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // STYNO Hero Brand Icon & Badge
                        StynoHeroLogo(
                            size = 80.dp,
                            cornerRadius = 20.dp,
                            animated = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "STYNO",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Verified Student & Professional Accommodations",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = if (authMode == "SIGN_IN") "Welcome Back" else "Create Your Account",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (authMode == "SIGN_IN") "Sign in to sync your bookings & explore verified stays" else "Join Styno for zero-brokerage verified accommodation",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                // Google Sign-In Button
                                OutlinedButton(
                                    onClick = {
                                        val googleEmail = "google_user@styno.com"
                                        viewModel.setAuthenticated(identifier = googleEmail, phone = "")
                                        currentStep = OnboardingStep.ACCOUNT_TYPE
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("onboarding_google_signin_button"),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Text(text = "🌐", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Continue with Google",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "OR WITH EMAIL",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    HorizontalDivider(modifier = Modifier.weight(1f))
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Mode Switcher: Sign In vs Sign Up
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (authMode == "SIGN_IN") MaterialTheme.colorScheme.primary else Color.Transparent,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                authMode = "SIGN_IN"
                                                authError = null
                                            }
                                    ) {
                                        Text(
                                            text = "Sign In",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (authMode == "SIGN_IN") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (authMode == "SIGN_UP") MaterialTheme.colorScheme.primary else Color.Transparent,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                authMode = "SIGN_UP"
                                                authError = null
                                            }
                                    ) {
                                        Text(
                                            text = "Sign Up",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (authMode == "SIGN_UP") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // If SIGN_UP: Full Name
                                if (authMode == "SIGN_UP") {
                                    OutlinedTextField(
                                        value = authFullName,
                                        onValueChange = {
                                            authFullName = it
                                            authError = null
                                        },
                                        label = { Text("Full Name") },
                                        placeholder = { Text("Enter your full name") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("onboarding_fullname_input")
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Email input
                                OutlinedTextField(
                                    value = authEmail,
                                    onValueChange = {
                                        authEmail = it
                                        authError = null
                                    },
                                    label = { Text("Email Address") },
                                    placeholder = { Text("Enter your email address") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("onboarding_email_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Password input
                                OutlinedTextField(
                                    value = authPassword,
                                    onValueChange = {
                                        authPassword = it
                                        authError = null
                                    },
                                    label = { Text("Password") },
                                    placeholder = { Text(if (authMode == "SIGN_IN") "Enter your password" else "Create password (min 6 chars)") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle password visibility"
                                            )
                                        }
                                    },
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = if (authMode == "SIGN_UP") ImeAction.Next else ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("onboarding_password_input")
                                )

                                // If SIGN_UP: Confirm Password
                                if (authMode == "SIGN_UP") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = authConfirmPassword,
                                        onValueChange = {
                                            authConfirmPassword = it
                                            authError = null
                                        },
                                        label = { Text("Confirm Password") },
                                        placeholder = { Text("Re-enter your password") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = "Toggle password visibility"
                                                )
                                            }
                                        },
                                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("onboarding_confirm_password_input")
                                    )
                                }

                                if (authError != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = authError ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = {
                                        val trimmedEmail = authEmail.trim()
                                        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                                            authError = "Please enter a valid email address."
                                            return@Button
                                        }
                                        if (authPassword.length < 6) {
                                            authError = "Password must be at least 6 characters."
                                            return@Button
                                        }
                                        if (authMode == "SIGN_UP") {
                                            if (authFullName.trim().isBlank()) {
                                                authError = "Please enter your full name."
                                                return@Button
                                            }
                                            if (authPassword != authConfirmPassword) {
                                                authError = "Passwords do not match."
                                                return@Button
                                            }
                                        }

                                        isAuthenticating = true
                                        authError = null
                                        viewModel.setAuthenticated(
                                            identifier = trimmedEmail,
                                            phone = ""
                                        )
                                        isAuthenticating = false
                                        // Move to account type step
                                        currentStep = OnboardingStep.ACCOUNT_TYPE
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("onboarding_auth_submit_btn")
                                ) {
                                    if (isAuthenticating) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(22.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (authMode == "SIGN_IN") Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = if (authMode == "SIGN_IN") "Sign In & Continue" else "Create Account & Continue",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Forgot password link
                                if (authMode == "SIGN_IN") {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        TextButton(onClick = {
                                            viewModel.navigateTo(Screen.AUTH)
                                        }) {
                                            Text(
                                                text = "Forgot Password? Reset Here",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Terms & Privacy Links
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "By continuing, you agree to our ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Terms of Service",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { showTermsDialog = true }
                            )
                            Text(
                                text = " & ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Privacy Policy",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { showPrivacyDialog = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // ==========================================
                // STEP 2: ACCOUNT TYPE SELECTION
                // ==========================================
                OnboardingStep.ACCOUNT_TYPE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "What are you looking for?",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Choose your role to customize your STYNO experience",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // 1. User / Guest Card
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedRole == UserRole.GUEST) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (selectedRole == UserRole.GUEST) 2.dp else 1.dp,
                                if (selectedRole == UserRole.GUEST) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRole = UserRole.GUEST
                                    viewModel.setUserRole(UserRole.GUEST)
                                    currentStep = OnboardingStep.LOCATION
                                }
                                .testTag("select_role_guest_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Explore,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "User / Guest",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "Most Popular",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Find & book verified hostels, PGs, flats, private rooms, and quick hourly stays with zero brokerage.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 2. Property Owner Card
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedRole == UserRole.OWNER) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (selectedRole == UserRole.OWNER) 2.dp else 1.dp,
                                if (selectedRole == UserRole.OWNER) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRole = UserRole.OWNER
                                    viewModel.setUserRole(UserRole.OWNER)
                                    viewModel.navigateTo(Screen.OWNER_DASHBOARD)
                                }
                                .testTag("select_role_owner_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = StynoEmerald,
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CorporateFare,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Property Owner / Warden",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "List & manage student hostels, PGs, or flats. Manage daily food menus, rooms, tenant bookings & KYC.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = StynoEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // STEP 3: SMART GLOBAL LOCATION ONBOARDING
                // ==========================================
                OnboardingStep.LOCATION -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Where do you want to stay?",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select your global country, region, city, and neighborhood",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // GPS Use Current Location Button
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                                .testTag("onboarding_use_gps_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isGpsLocating) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.MyLocation,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Use Current Location",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = gpsStatus ?: "Auto-detect Country, State, City & Area coordinates",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (permissionDeniedMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = permissionDeniedMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Country Selector Chips
                        Text(
                            text = "SELECT COUNTRY",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val countries = listOf(
                            Pair("India", "🇮🇳"),
                            Pair("USA", "🇺🇸"),
                            Pair("United Kingdom", "🇬🇧"),
                            Pair("UAE", "🇦🇪"),
                            Pair("Singapore", "🇸🇬"),
                            Pair("Canada", "🇨🇦"),
                            Pair("Australia", "🇦🇺"),
                            Pair("Germany", "🇩🇪")
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            countries.forEach { (country, flag) ->
                                val isSelected = selectedCountry.equals(country, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            selectedCountry = country
                                            if (country == "India") {
                                                selectedState = "Delhi NCR"
                                                selectedCity = "Noida"
                                                selectedLocality = "Sector 62"
                                            } else if (country == "USA") {
                                                selectedState = "New York"
                                                selectedCity = "New York City"
                                                selectedLocality = "Manhattan"
                                            } else if (country == "United Kingdom") {
                                                selectedState = "Greater London"
                                                selectedCity = "London"
                                                selectedLocality = "Westminster"
                                            } else if (country == "UAE") {
                                                selectedState = "Dubai"
                                                selectedCity = "Dubai"
                                                selectedLocality = "Downtown Dubai"
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = flag, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = country,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Location Search Field
                        OutlinedTextField(
                            value = locationSearchQuery,
                            onValueChange = { locationSearchQuery = it },
                            placeholder = { Text("Search city, locality, university, landmark...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingIcon = {
                                if (locationSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { locationSearchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_location_search")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Popular City & Locality Presets
                        Text(
                            text = "POPULAR HUBS IN $selectedCountry",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val filteredLocations = remember(selectedCountry, locationSearchQuery) {
                            GlobalLocationCatalog.ALL_LOCATIONS.filter { loc ->
                                val matchesCountry = loc.country.equals(selectedCountry, ignoreCase = true) ||
                                        (selectedCountry == "USA" && loc.country.contains("USA", ignoreCase = true)) ||
                                        (selectedCountry == "UAE" && loc.country.contains("UAE", ignoreCase = true)) ||
                                        locationSearchQuery.isNotBlank()
                                val matchesQuery = locationSearchQuery.isBlank() ||
                                        loc.name.contains(locationSearchQuery, ignoreCase = true) ||
                                        loc.city.contains(locationSearchQuery, ignoreCase = true) ||
                                        (loc.locality?.contains(locationSearchQuery, ignoreCase = true) == true) ||
                                        loc.popularLandmarks.any { it.contains(locationSearchQuery, ignoreCase = true) }
                                matchesCountry && matchesQuery
                            }
                        }

                        if (filteredLocations.isEmpty()) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Setting custom location for \"$locationSearchQuery\"",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(
                                        onClick = {
                                            selectedLocality = locationSearchQuery.trim()
                                            selectedCity = "Metropolitan"
                                            locationSearchQuery = ""
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Confirm Custom Area")
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                filteredLocations.take(6).forEach { loc ->
                                    val isSelected = selectedLocality.equals(loc.locality ?: loc.name, ignoreCase = true) &&
                                            selectedCity.equals(loc.city, ignoreCase = true)
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            if (isSelected) 1.5.dp else 1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedCountry = loc.country
                                                selectedState = loc.state
                                                selectedCity = loc.city
                                                selectedLocality = loc.locality ?: loc.name
                                                selectedLandmark = loc.popularLandmarks.firstOrNull() ?: "Metro Station"
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = loc.toDisplayText(),
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = loc.toFullHierarchyText(),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                if (loc.popularLandmarks.isNotEmpty()) {
                                                    Text(
                                                        text = "⭐ ${loc.popularLandmarks.take(2).joinToString(" • ")}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Selected",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // STEP 4: STAY TYPE SELECTION (MULTI-SELECT)
                // ==========================================
                OnboardingStep.STAY_TYPE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "What type of stay do you need?",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select one or more categories that suit your accommodation needs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        val stayOptions = listOf(
                            Triple(PropertyType.HOSTEL, "Student & Youth Hostels", "Budget friendly, study halls, 3x meals & warden safety"),
                            Triple(PropertyType.PG, "PG (Paying Guest)", "AC rooms, attached washroom, daily food & housekeeping"),
                            Triple(PropertyType.FLAT, "Flats & Apartments", "1 BHK, 2 BHK, 3 BHK, 4+ BHK furnished independent stays"),
                            Triple(PropertyType.ROOM, "Independent Rooms", "Private single/double bedrooms with privacy and flexibility"),
                            Triple(PropertyType.HOTEL, "Hotels & Suites", "Daily & nightly stays with room service & premium amenities"),
                            Triple(PropertyType.QUICK_STAY, "Quick Stay (Hourly Slots)", "3h, 6h, 12h, 24h slots near stations, airports & transit hubs")
                        )

                        stayOptions.forEach { (type, title, desc) ->
                            val isSelected = selectedStayTypes.contains(type)
                            val icon: ImageVector = when (type) {
                                PropertyType.HOSTEL -> Icons.Default.CorporateFare
                                PropertyType.PG -> Icons.Default.Home
                                PropertyType.FLAT -> Icons.Default.Apartment
                                PropertyType.ROOM -> Icons.Default.MeetingRoom
                                PropertyType.HOTEL -> Icons.Default.Hotel
                                PropertyType.QUICK_STAY -> Icons.Default.Bolt
                            }

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clickable {
                                        selectedStayTypes = if (isSelected) {
                                            if (selectedStayTypes.size > 1) selectedStayTypes - type else selectedStayTypes
                                        } else {
                                            selectedStayTypes + type
                                        }
                                    }
                                    .testTag("stay_type_card_${type.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // STEP 5: SHARING / ROOM PREFERENCE (DYNAMIC)
                // ==========================================
                OnboardingStep.SHARING_TYPE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "How would you like to stay?",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select your preferred room configuration & sharing style",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Dynamic Room options based on selected stay types
                        val isFlatSelected = selectedStayTypes.contains(PropertyType.FLAT)
                        val isHostelOrPgSelected = selectedStayTypes.contains(PropertyType.HOSTEL) || selectedStayTypes.contains(PropertyType.PG)
                        val isHotelSelected = selectedStayTypes.contains(PropertyType.HOTEL)
                        val isQuickStaySelected = selectedStayTypes.contains(PropertyType.QUICK_STAY)

                        // 1. Flat Configuration Options
                        if (isFlatSelected) {
                            Text(
                                text = "FLAT CONFIGURATION (BHK)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val bhkOptions = listOf("1 BHK", "2 BHK", "3 BHK", "4+ BHK", "Studio Apartment")
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                bhkOptions.forEach { opt ->
                                    val isSelected = selectedSharingPrefs.contains(opt)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedSharingPrefs = if (isSelected) selectedSharingPrefs - opt else selectedSharingPrefs + opt
                                        },
                                        label = { Text(opt, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                        }

                        // 2. Hostel & PG Sharing Options
                        if (isHostelOrPgSelected || (!isFlatSelected && !isHotelSelected)) {
                            Text(
                                text = "HOSTEL & PG SHARING TYPE",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val sharingOptions = listOf(
                                "Private Room (Single Occupancy)",
                                "Single Sharing",
                                "Double Sharing (2 Beds)",
                                "Triple Sharing (3 Beds)",
                                "4 Sharing",
                                "5+ Sharing / Dormitory",
                                "Any Sharing"
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                sharingOptions.forEach { opt ->
                                    val isSelected = selectedSharingPrefs.contains(opt)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedSharingPrefs = if (isSelected) selectedSharingPrefs - opt else selectedSharingPrefs + opt
                                        },
                                        label = { Text(opt, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                        }

                        // 3. Hotel Room Types
                        if (isHotelSelected) {
                            Text(
                                text = "HOTEL ROOM CATEGORY",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val hotelOptions = listOf("Standard Room", "Deluxe Room", "Executive Suite", "Family Suite")
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                hotelOptions.forEach { opt ->
                                    val isSelected = selectedSharingPrefs.contains(opt)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedSharingPrefs = if (isSelected) selectedSharingPrefs - opt else selectedSharingPrefs + opt
                                        },
                                        label = { Text(opt, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                        }

                        // 4. Quick Stay Slot Preferences
                        if (isQuickStaySelected) {
                            Text(
                                text = "QUICK STAY DURATION PREFERENCE",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val quickOptions = listOf("3 Hours Slot", "6 Hours Slot", "12 Hours Slot", "24 Hours Day-Use", "Private Micro-Pod")
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                quickOptions.forEach { opt ->
                                    val isSelected = selectedSharingPrefs.contains(opt)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedSharingPrefs = if (isSelected) selectedSharingPrefs - opt else selectedSharingPrefs + opt
                                        },
                                        label = { Text(opt, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        shape = RoundedCornerShape(12.dp),
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

                // ==========================================
                // STEP 6: USER PURPOSE / PROFILE (MULTI-SELECT)
                // ==========================================
                OnboardingStep.USER_PURPOSE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "What best describes your stay?",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select all that apply so STYNO can tailor your home feed & recommendations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        val purposeOptions = listOf(
                            Pair("Student", "🎓 College, University & Coaching Institutes"),
                            Pair("Working Professional", "💼 Tech Parks, MNCs & Office Workspaces"),
                            Pair("Business Traveler", "🏢 Client Meetings, Conferences & Work Trips"),
                            Pair("Tourist / Traveler", "✈️ Sightseeing, Vacations & City Exploration"),
                            Pair("Family", "👨‍👩‍👧 Family Vacations & Long-term Home Living"),
                            Pair("Couple", "💑 Safe & Verified Accommodations (Valid ID)"),
                            Pair("Job Seeker", "🔍 Job Interviews & Transit Stays"),
                            Pair("Intern", "💡 Summer & Industrial Internships"),
                            Pair("Medical Visitor", "🏥 Hospital Visits & Medical Treatment Stays"),
                            Pair("Relocating / New to City", "🚚 New Move & Long-Term Relocation"),
                            Pair("Long-Term Resident", "🏡 6+ Months Sustainable Living"),
                            Pair("Short-Term Visitor", "📅 Daily & Weekly Micro-Stays"),
                            Pair("Other", "🌐 General Accommodation")
                        )

                        purposeOptions.forEach { (purpose, desc) ->
                            val isSelected = selectedPurposes.contains(purpose)
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        selectedPurposes = if (isSelected) {
                                            if (selectedPurposes.size > 1) selectedPurposes - purpose else selectedPurposes
                                        } else {
                                            selectedPurposes + purpose
                                        }
                                    }
                                    .testTag("purpose_chip_${purpose.lowercase().replace(" ", "_")}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Terms & Privacy Dialogs
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Service") },
            text = {
                Text(
                    "Welcome to STYNO. By using STYNO, you agree to verified bookings, zero-brokerage policies, digital guest safety compliance, and genuine property reviews. Security deposits are managed under transparent partner policies."
                )
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }) {
                    Text("I Understand")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy") },
            text = {
                Text(
                    "Your privacy is our priority. STYNO encrypts phone numbers, emails, and emergency contacts. Location data is strictly used for reverse geocoding nearby accommodations and is never shared with third parties without consent."
                )
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

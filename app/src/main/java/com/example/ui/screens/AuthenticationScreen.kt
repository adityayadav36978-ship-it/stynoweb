package com.example.ui.screens

import android.app.Activity
import android.content.Context
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.StynoHeroLogo
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthUser
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.StynoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Authentication screen modes for Email + Password auth flow.
 */
enum class AuthScreenMode {
    PHONE_OTP,
    SIGN_IN,
    SIGN_UP,
    FORGOT_PASSWORD,
    EMAIL_VERIFICATION
}

/**
 * Main Authentication Screen for STYNO.
 * Provides Email + Password login and signup, email verification, forgot password,
 * and optional Google Sign-In.
 */
@Composable
fun AuthenticationScreen(
    authViewModel: AuthViewModel = viewModel(),
    stynoViewModel: StynoViewModel? = null,
    onAuthSuccess: (AuthUser) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by authViewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    var currentMode by remember { mutableStateOf(AuthScreenMode.PHONE_OTP) }

    // Phone OTP form fields
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var otpCooldownSeconds by remember { mutableIntStateOf(0) }
    var otpExpireSeconds by remember { mutableIntStateOf(0) }

    // Decrement OTP timers
    LaunchedEffect(otpCooldownSeconds, otpExpireSeconds) {
        if (otpCooldownSeconds > 0 || otpExpireSeconds > 0) {
            delay(1000L)
            if (otpCooldownSeconds > 0) otpCooldownSeconds--
            if (otpExpireSeconds > 0) otpExpireSeconds--
        }
    }

    // Sign In form fields
    var signInEmail by remember { mutableStateOf("") }
    var signInPassword by remember { mutableStateOf("") }
    var signInPasswordVisible by remember { mutableStateOf(false) }

    // Sign Up form fields
    var signUpName by remember { mutableStateOf("") }
    var signUpEmail by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var signUpConfirmPassword by remember { mutableStateOf("") }
    var signUpPasswordVisible by remember { mutableStateOf(false) }
    var signUpConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Forgot Password form fields
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var resetEmailSent by remember { mutableStateOf(false) }

    // Email verification cooldown
    var resendCooldownSeconds by remember { mutableIntStateOf(0) }

    // Decrement resend cooldown timer
    LaunchedEffect(resendCooldownSeconds) {
        if (resendCooldownSeconds > 0) {
            delay(1000L)
            resendCooldownSeconds--
        }
    }

    // React to UI state error changes
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Error) {
            val message = (uiState as AuthUiState.Error).message
            snackbarHostState.showSnackbar(message)
            authViewModel.clearError()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("auth_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            // Ambient background gradient glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                StynoEmerald.copy(alpha = 0.10f),
                                StynoBluePrimary.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (currentMode == AuthScreenMode.FORGOT_PASSWORD ||
                                currentMode == AuthScreenMode.EMAIL_VERIFICATION
                            ) {
                                currentMode = AuthScreenMode.SIGN_IN
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.testTag("btn_auth_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = StynoEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "256-Bit SSL Secured",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Brand Header with StynoHeroLogo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StynoHeroLogo(
                        size = 64.dp,
                        cornerRadius = 16.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "STYNO",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StynoEmerald
                        ) {
                            Text(
                                text = "STAYS",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Find Your Stay • 0% Brokerage • Verified Hostels & Hotels",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                }

                // Active Session Card (if user is currently signed in)
                if (currentUser != null) {
                    val user = currentUser!!
                    ActiveSessionCard(
                        user = user,
                        onContinue = {
                            stynoViewModel?.setAuthenticated(
                                identifier = user.email ?: user.uid,
                                phone = user.phoneNumber ?: "",
                                name = user.displayName ?: ""
                            )
                            onAuthSuccess(user)
                        },
                        onVerifyEmailClick = {
                            currentMode = AuthScreenMode.EMAIL_VERIFICATION
                        },
                        onLogoutClick = {
                            authViewModel.signOut()
                            scope.launch {
                                snackbarHostState.showSnackbar("Logged out successfully")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Mode Selector Tabs (shown on PHONE_OTP, SIGN_IN and SIGN_UP modes)
                if (currentMode == AuthScreenMode.PHONE_OTP ||
                    currentMode == AuthScreenMode.SIGN_IN ||
                    currentMode == AuthScreenMode.SIGN_UP
                ) {
                    AuthTabSelector(
                        selectedMode = currentMode,
                        onModeSelected = { mode ->
                            currentMode = mode
                            focusManager.clearFocus()
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Main Animated Content Body
                AnimatedContent(
                    targetState = currentMode,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "auth_mode_transition"
                ) { mode ->
                    when (mode) {
                        AuthScreenMode.PHONE_OTP -> {
                            PhoneOtpContent(
                                phone = phoneInput,
                                otp = otpInput,
                                otpSent = otpSent,
                                cooldownSeconds = otpCooldownSeconds,
                                expireSeconds = otpExpireSeconds,
                                isLoading = uiState is AuthUiState.Loading,
                                onPhoneChanged = { phoneInput = it },
                                onOtpChanged = { otpInput = it },
                                onRequestOtp = {
                                    focusManager.clearFocus()
                                    authViewModel.requestPhoneOtp(
                                        phoneNumber = phoneInput,
                                        onSuccess = { msg, cooldownSec ->
                                            otpSent = true
                                            otpCooldownSeconds = cooldownSec
                                            otpExpireSeconds = 300
                                            scope.launch { snackbarHostState.showSnackbar(msg) }
                                        },
                                        onError = { err ->
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                },
                                onVerifyOtp = {
                                    focusManager.clearFocus()
                                    authViewModel.verifyPhoneOtp(
                                        phoneNumber = phoneInput,
                                        enteredOtp = otpInput,
                                        onSuccess = { user ->
                                            stynoViewModel?.setAuthenticated(
                                                identifier = user.phoneNumber ?: user.uid,
                                                phone = user.phoneNumber ?: "",
                                                name = user.displayName ?: ""
                                            )
                                            onAuthSuccess(user)
                                        },
                                        onError = { err ->
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                },
                                onSwitchToEmail = {
                                    currentMode = AuthScreenMode.SIGN_IN
                                },
                                onGoogleSignIn = {
                                    authViewModel.signInWithGoogle(
                                        context = context,
                                        onSuccess = { user ->
                                            stynoViewModel?.setAuthenticated(
                                                identifier = user.email ?: user.uid,
                                                phone = user.phoneNumber ?: "",
                                                name = user.displayName ?: ""
                                            )
                                            onAuthSuccess(user)
                                        },
                                        onError = { err ->
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                }
                            )
                        }
                        AuthScreenMode.SIGN_IN -> {
                            SignInContent(
                                email = signInEmail,
                                password = signInPassword,
                                isPasswordVisible = signInPasswordVisible,
                                isLoading = uiState is AuthUiState.Loading,
                                onEmailChanged = { signInEmail = it },
                                onPasswordChanged = { signInPassword = it },
                                onTogglePasswordVisibility = { signInPasswordVisible = !signInPasswordVisible },
                                onForgotPasswordClick = {
                                    forgotPasswordEmail = signInEmail
                                    currentMode = AuthScreenMode.FORGOT_PASSWORD
                                },
                                onSignIn = {
                                    focusManager.clearFocus()
                                    authViewModel.signInWithEmail(
                                        email = signInEmail,
                                        password = signInPassword,
                                        onSuccess = {
                                            val user = authViewModel.currentUser.value
                                            if (user != null) {
                                                stynoViewModel?.setAuthenticated(
                                                    identifier = user.email ?: user.uid,
                                                    phone = user.phoneNumber ?: "",
                                                    name = user.displayName ?: ""
                                                )
                                                onAuthSuccess(user)
                                            }
                                        },
                                        onError = { err ->
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                },
                                onGoogleSignIn = {
                                    authViewModel.signInWithGoogle(
                                        context = context,
                                        onSuccess = { user ->
                                            stynoViewModel?.setAuthenticated(
                                                identifier = user.email ?: user.uid,
                                                phone = user.phoneNumber ?: "",
                                                name = user.displayName ?: ""
                                            )
                                            onAuthSuccess(user)
                                        },
                                        onError = { err ->
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                },
                                onAppleSignIn = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        authViewModel.signInWithApple(
                                            activity = activity,
                                            onSuccess = { user ->
                                                stynoViewModel?.setAuthenticated(
                                                    identifier = user.email ?: user.uid,
                                                    phone = user.phoneNumber ?: "",
                                                    name = user.displayName ?: ""
                                                )
                                                onAuthSuccess(user)
                                            },
                                            onError = { err ->
                                                scope.launch { snackbarHostState.showSnackbar(err) }
                                            }
                                        )
                                    } else {
                                        scope.launch { snackbarHostState.showSnackbar("Apple Sign-In requires an active window context") }
                                    }
                                },
                                onGuestContinue = {
                                    onNavigateBack()
                                }
                            )
                        }

                        AuthScreenMode.SIGN_UP -> {
                            SignUpContent(
                                name = signUpName,
                                email = signUpEmail,
                                password = signUpPassword,
                                confirmPassword = signUpConfirmPassword,
                                isPasswordVisible = signUpPasswordVisible,
                                isConfirmPasswordVisible = signUpConfirmPasswordVisible,
                                isLoading = uiState is AuthUiState.Loading,
                                onNameChanged = { signUpName = it },
                                onEmailChanged = { signUpEmail = it },
                                onPasswordChanged = { signUpPassword = it },
                                onConfirmPasswordChanged = { signUpConfirmPassword = it },
                                onTogglePasswordVisibility = { signUpPasswordVisible = !signUpPasswordVisible },
                                onToggleConfirmPasswordVisibility = { signUpConfirmPasswordVisible = !signUpConfirmPasswordVisible },
                                onSignUp = {
                                    if (signUpPassword != signUpConfirmPassword) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Passwords do not match")
                                        }
                                        return@SignUpContent
                                    }
                                    focusManager.clearFocus()
                                    authViewModel.signUpWithEmail(
                                        email = signUpEmail,
                                        password = signUpPassword,
                                        displayName = signUpName.ifBlank { null },
                                        onSuccess = {
                                            val user = authViewModel.currentUser.value
                                            if (user != null) {
                                                stynoViewModel?.setAuthenticated(
                                                    identifier = user.email ?: user.uid,
                                                    phone = user.phoneNumber ?: "",
                                                    name = user.displayName ?: ""
                                                )
                                            }
                                            resendCooldownSeconds = 30
                                            currentMode = AuthScreenMode.EMAIL_VERIFICATION
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    "Account created! Verification link sent to $signUpEmail"
                                                )
                                            }
                                        },
                                        onError = { err ->
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                },
                                onGoogleSignIn = {
                                    authViewModel.signInWithGoogle(
                                        context = context,
                                        onSuccess = { user ->
                                            stynoViewModel?.setAuthenticated(
                                                identifier = user.email ?: user.uid,
                                                phone = user.phoneNumber ?: "",
                                                name = user.displayName ?: ""
                                            )
                                            onAuthSuccess(user)
                                        },
                                        onError = { err ->
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                },
                                onAppleSignIn = {
                                    val activity = context as? Activity
                                    if (activity != null) {
                                        authViewModel.signInWithApple(
                                            activity = activity,
                                            onSuccess = { user ->
                                                stynoViewModel?.setAuthenticated(
                                                    identifier = user.email ?: user.uid,
                                                    phone = user.phoneNumber ?: "",
                                                    name = user.displayName ?: ""
                                                )
                                                onAuthSuccess(user)
                                            },
                                            onError = { err ->
                                                scope.launch { snackbarHostState.showSnackbar(err) }
                                            }
                                        )
                                    } else {
                                        scope.launch { snackbarHostState.showSnackbar("Apple Sign-In requires an active window context") }
                                    }
                                }
                            )
                        }

                        AuthScreenMode.FORGOT_PASSWORD -> {
                            ForgotPasswordContent(
                                email = forgotPasswordEmail,
                                isSent = resetEmailSent,
                                isLoading = uiState is AuthUiState.Loading,
                                onEmailChanged = { forgotPasswordEmail = it },
                                onSendResetLink = {
                                    focusManager.clearFocus()
                                    authViewModel.sendPasswordResetEmail(
                                        email = forgotPasswordEmail,
                                        onSuccess = {
                                            resetEmailSent = true
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    "Reset link sent to $forgotPasswordEmail"
                                                )
                                            }
                                        },
                                        onError = { err ->
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                },
                                onBackToSignIn = {
                                    currentMode = AuthScreenMode.SIGN_IN
                                    resetEmailSent = false
                                }
                            )
                        }

                        AuthScreenMode.EMAIL_VERIFICATION -> {
                            val targetEmail = currentUser?.email ?: signUpEmail.ifBlank { signInEmail }
                            val isVerified = currentUser?.isEmailVerified == true

                            EmailVerificationContent(
                                email = targetEmail,
                                isVerified = isVerified,
                                isLoading = uiState is AuthUiState.Loading,
                                resendCooldownSeconds = resendCooldownSeconds,
                                onCheckVerification = {
                                    authViewModel.reloadUser(
                                        onSuccess = { updated ->
                                            if (updated.isEmailVerified) {
                                                stynoViewModel?.setAuthenticated(
                                                    identifier = updated.email ?: updated.uid,
                                                    phone = updated.phoneNumber ?: "",
                                                    name = updated.displayName ?: ""
                                                )
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Email verified! Welcome to STYNO.")
                                                }
                                                onAuthSuccess(updated)
                                            } else {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        "Verification pending. Please tap the confirmation link in your email."
                                                    )
                                                }
                                            }
                                        },
                                        onError = { err ->
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                },
                                onResendVerification = {
                                    authViewModel.sendEmailVerification(
                                        onSuccess = {
                                            resendCooldownSeconds = 30
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Verification email resent!")
                                            }
                                        },
                                        onError = { err ->
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                },
                                onContinueToApp = {
                                    val user = currentUser
                                    if (user != null) {
                                        stynoViewModel?.setAuthenticated(
                                            identifier = user.email ?: user.uid,
                                            phone = user.phoneNumber ?: "",
                                            name = user.displayName ?: ""
                                        )
                                        onAuthSuccess(user)
                                    } else {
                                        onNavigateBack()
                                    }
                                },
                                onBackToSignIn = {
                                    currentMode = AuthScreenMode.SIGN_IN
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modern Segmented Tab Bar for switching between Mobile OTP, Email Sign In, and Sign Up.
 */
@Composable
private fun AuthTabSelector(
    selectedMode: AuthScreenMode,
    onModeSelected: (AuthScreenMode) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Mobile OTP Tab
            val isOtp = selectedMode == AuthScreenMode.PHONE_OTP
            Surface(
                onClick = { onModeSelected(AuthScreenMode.PHONE_OTP) },
                shape = RoundedCornerShape(11.dp),
                color = if (isOtp) MaterialTheme.colorScheme.surface else Color.Transparent,
                shadowElevation = if (isOtp) 2.dp else 0.dp,
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_phone_otp")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Mobile OTP",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isOtp) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isOtp) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Sign In Tab
            val isSignIn = selectedMode == AuthScreenMode.SIGN_IN
            Surface(
                onClick = { onModeSelected(AuthScreenMode.SIGN_IN) },
                shape = RoundedCornerShape(11.dp),
                color = if (isSignIn) MaterialTheme.colorScheme.surface else Color.Transparent,
                shadowElevation = if (isSignIn) 2.dp else 0.dp,
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_sign_in")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isSignIn) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSignIn) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Sign Up Tab
            val isSignUp = selectedMode == AuthScreenMode.SIGN_UP
            Surface(
                onClick = { onModeSelected(AuthScreenMode.SIGN_UP) },
                shape = RoundedCornerShape(11.dp),
                color = if (isSignUp) MaterialTheme.colorScheme.surface else Color.Transparent,
                shadowElevation = if (isSignUp) 2.dp else 0.dp,
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_sign_up")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Register",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSignUp) StynoEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Mobile Phone Number + OTP Verification Content.
 */
@Composable
private fun PhoneOtpContent(
    phone: String,
    otp: String,
    otpSent: Boolean,
    cooldownSeconds: Int,
    expireSeconds: Int,
    isLoading: Boolean,
    onPhoneChanged: (String) -> Unit,
    onOtpChanged: (String) -> Unit,
    onRequestOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onSwitchToEmail: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (!otpSent) "Login with Mobile Number" else "Verify Security Code",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (!otpSent)
                        "Enter your 10-digit mobile number to receive a secure one-time verification code."
                    else
                        "Enter the 6-digit OTP sent to +91 $phone to verify and continue.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Phone Input Field
                OutlinedTextField(
                    value = phone,
                    onValueChange = { input ->
                        if (input.length <= 10 && input.all { it.isDigit() }) {
                            onPhoneChanged(input)
                        }
                    },
                    label = { Text("Mobile Number") },
                    placeholder = { Text("9876543210") },
                    leadingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = StynoEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+91",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = if (otpSent) ImeAction.Next else ImeAction.Done
                    ),
                    singleLine = true,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_phone_input")
                )

                if (otpSent) {
                    // OTP Input Field
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { input ->
                            if (input.length <= 6 && input.all { it.isDigit() }) {
                                onOtpChanged(input)
                            }
                        },
                        label = { Text("6-Digit OTP Code") },
                        placeholder = { Text("• • • • • •") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = StynoEmerald
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_otp_input")
                    )

                    // Timers & Resend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (expireSeconds > 0) {
                            val mins = expireSeconds / 60
                            val secs = expireSeconds % 60
                            Text(
                                text = "Code valid: %02d:%02d".format(mins, secs),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Code expired",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        if (cooldownSeconds > 0) {
                            Text(
                                text = "Resend in ${cooldownSeconds}s",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            TextButton(
                                onClick = onRequestOtp,
                                enabled = !isLoading && phone.length == 10
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Resend OTP",
                                    fontWeight = FontWeight.Bold,
                                    color = StynoEmerald
                                )
                            }
                        }
                    }

                    // Verify Button
                    Button(
                        onClick = onVerifyOtp,
                        enabled = !isLoading && otp.length == 6,
                        colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_btn_verify_otp")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "Verify & Continue",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // Send OTP Button
                    Button(
                        onClick = onRequestOtp,
                        enabled = !isLoading && phone.length == 10,
                        colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_btn_request_otp")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "Get OTP",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "OR",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In Option
        OutlinedButton(
            onClick = onGoogleSignIn,
            enabled = !isLoading,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("auth_btn_google")
        ) {
            Text(
                text = "G",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = Color(0xFF4285F4)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Continue with Google",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Switch to Email
        TextButton(onClick = onSwitchToEmail) {
            Text(
                text = "Sign In with Email & Password instead",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Sign In Form Content.
 */
@Composable
private fun SignInContent(
    email: String,
    password: String,
    isPasswordVisible: Boolean,
    isLoading: Boolean,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onAppleSignIn: () -> Unit,
    onGuestContinue: () -> Unit
) {
    val canSubmit = email.isNotBlank() && password.isNotBlank() && !isLoading

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Sign in to manage your bookings and saved properties",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            label = { Text("Email Address") },
            placeholder = { Text("you@example.com") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = StynoEmerald)
            },
            trailingIcon = {
                if (email.isNotEmpty()) {
                    IconButton(onClick = { onEmailChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear email", modifier = Modifier.size(18.dp))
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StynoEmerald,
                cursorColor = StynoEmerald
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_email")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = StynoEmerald)
            },
            trailingIcon = {
                IconButton(
                    onClick = onTogglePasswordVisibility,
                    modifier = Modifier.testTag("btn_toggle_password")
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (canSubmit) onSignIn()
                }
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StynoEmerald,
                cursorColor = StynoEmerald
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_password")
        )

        // Forgot password text button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier.testTag("btn_forgot_password_link")
            ) {
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = StynoBluePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Primary Sign In Button
        Button(
            onClick = onSignIn,
            enabled = canSubmit,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StynoEmerald,
                disabledContainerColor = StynoEmerald.copy(alpha = 0.4f),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(if (canSubmit) 4.dp else 0.dp, RoundedCornerShape(14.dp))
                .testTag("btn_sign_in")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Alternative divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "OR QUICK ACCESS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apple Sign-In Button
        AppleSignInButton(
            onClick = onAppleSignIn,
            enabled = !isLoading,
            text = "Continue with Apple"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Google Sign-In Button
        OutlinedButton(
            onClick = onGoogleSignIn,
            enabled = !isLoading,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_auth_google")
        ) {
            Text(text = "🌐", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Continue with Google",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Guest Continue
        TextButton(
            onClick = onGuestContinue,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_auth_guest")
        ) {
            Text(
                text = "Explore Stays as Guest",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Sign Up Form Content.
 */
@Composable
private fun SignUpContent(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    isPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    isLoading: Boolean,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onSignUp: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onAppleSignIn: () -> Unit
) {
    val passwordsMatch = password.isNotEmpty() && password == confirmPassword
    val isPasswordLengthValid = password.length >= 6
    val canSubmit = email.isNotBlank() && isPasswordLengthValid && passwordsMatch && !isLoading

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Join STYNO for 0% brokerage stays, verified hosts & instant booking",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Full Name
        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            label = { Text("Full Name (Optional)") },
            placeholder = { Text("e.g. Rahul Sharma") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = StynoEmerald)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StynoEmerald,
                cursorColor = StynoEmerald
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_display_name")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            label = { Text("Email Address") },
            placeholder = { Text("you@example.com") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = StynoEmerald)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StynoEmerald,
                cursorColor = StynoEmerald
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_email")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            label = { Text("Password (Min. 6 chars)") },
            placeholder = { Text("Create strong password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = StynoEmerald)
            },
            trailingIcon = {
                IconButton(
                    onClick = onTogglePasswordVisibility,
                    modifier = Modifier.testTag("btn_toggle_password")
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StynoEmerald,
                cursorColor = StynoEmerald
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_password")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChanged,
            label = { Text("Confirm Password") },
            placeholder = { Text("Repeat password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = StynoEmerald)
            },
            trailingIcon = {
                if (confirmPassword.isNotEmpty()) {
                    if (passwordsMatch) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Passwords match",
                            tint = StynoEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        IconButton(
                            onClick = onToggleConfirmPasswordVisibility,
                            modifier = Modifier.testTag("btn_toggle_confirm_password")
                        ) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle confirm password",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            },
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (canSubmit) onSignUp()
                }
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StynoEmerald,
                cursorColor = StynoEmerald
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_confirm_password")
        )

        // Real-time helper text
        if (confirmPassword.isNotEmpty() && !passwordsMatch) {
            Text(
                text = "Passwords do not match",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )
        } else if (password.isNotEmpty() && !isPasswordLengthValid) {
            Text(
                text = "Password must be at least 6 characters",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Create Account Button
        Button(
            onClick = onSignUp,
            enabled = canSubmit,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StynoEmerald,
                disabledContainerColor = StynoEmerald.copy(alpha = 0.4f),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(if (canSubmit) 4.dp else 0.dp, RoundedCornerShape(14.dp))
                .testTag("btn_create_account")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Apple Sign-In fallback
        AppleSignInButton(
            onClick = onAppleSignIn,
            enabled = !isLoading,
            text = "Sign Up with Apple"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Google Sign-In fallback
        OutlinedButton(
            onClick = onGoogleSignIn,
            enabled = !isLoading,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_auth_google")
        ) {
            Text(text = "🌐", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Sign Up with Google",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

/**
 * Forgot Password Content.
 */
@Composable
private fun ForgotPasswordContent(
    email: String,
    isSent: Boolean,
    isLoading: Boolean,
    onEmailChanged: (String) -> Unit,
    onSendResetLink: () -> Unit,
    onBackToSignIn: () -> Unit
) {
    val canSubmit = email.isNotBlank() && !isLoading

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = StynoBluePrimary.copy(alpha = 0.12f),
            modifier = Modifier.size(68.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = StynoBluePrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Reset Your Password",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Enter your registered email address and we'll send you instructions to reset your password.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isSent) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StynoEmerald.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StynoEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Password reset email sent! Check your inbox and spam folder to complete the reset.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            label = { Text("Email Address") },
            placeholder = { Text("you@example.com") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = StynoBluePrimary)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (canSubmit) onSendResetLink()
                }
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StynoBluePrimary,
                cursorColor = StynoBluePrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_forgot_email")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onSendResetLink,
            enabled = canSubmit,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StynoBluePrimary,
                disabledContainerColor = StynoBluePrimary.copy(alpha = 0.4f),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(if (canSubmit) 4.dp else 0.dp, RoundedCornerShape(14.dp))
                .testTag("btn_send_reset_password")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSent) "Resend Reset Link" else "Send Password Reset Link",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(
            onClick = onBackToSignIn,
            modifier = Modifier.testTag("btn_back_to_login")
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = StynoBluePrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Back to Sign In",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = StynoBluePrimary
            )
        }
    }
}

/**
 * Email Verification Content.
 */
@Composable
private fun EmailVerificationContent(
    email: String,
    isVerified: Boolean,
    isLoading: Boolean,
    resendCooldownSeconds: Int,
    onCheckVerification: () -> Unit,
    onResendVerification: () -> Unit,
    onContinueToApp: () -> Unit,
    onBackToSignIn: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mail Icon Graphic
        Surface(
            shape = CircleShape,
            color = if (isVerified) StynoEmerald.copy(alpha = 0.15f) else StynoBluePrimary.copy(alpha = 0.12f),
            modifier = Modifier.size(76.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isVerified) Icons.Default.VerifiedUser else Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = if (isVerified) StynoEmerald else StynoBluePrimary,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isVerified) "Email Verified!" else "Verify Your Email",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Target Email Pill
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = email.ifBlank { "your registered email" },
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isVerified) {
                "Your email has been verified. You now have full access to bookings and member benefits."
            } else {
                "We sent a verification link to your inbox. Open the link to verify your account, then return here."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Primary: "I've Verified My Email" / "Continue to App"
        Button(
            onClick = {
                if (isVerified) {
                    onContinueToApp()
                } else {
                    onCheckVerification()
                }
            },
            enabled = !isLoading,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StynoEmerald,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp))
                .testTag("btn_check_email_verified")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isVerified) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isVerified) "Continue to App" else "I've Verified My Email",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Secondary: Resend Verification Email
        if (!isVerified) {
            OutlinedButton(
                onClick = onResendVerification,
                enabled = !isLoading && resendCooldownSeconds == 0,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_resend_verification_email")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (resendCooldownSeconds == 0) StynoBluePrimary else MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (resendCooldownSeconds > 0) {
                        "Resend Link in ${resendCooldownSeconds}s"
                    } else {
                        "Resend Verification Link"
                    },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (resendCooldownSeconds == 0) StynoBluePrimary else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Continue anyway / Skip for now
            TextButton(
                onClick = onContinueToApp,
                modifier = Modifier.testTag("btn_skip_email_verification")
            ) {
                Text(
                    text = "Continue to App (Verify Later)",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onBackToSignIn,
            modifier = Modifier.testTag("btn_back_to_login")
        ) {
            Text(
                text = "Use a Different Account",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

/**
 * Active authenticated session card showing user details and options.
 */
@Composable
private fun ActiveSessionCard(
    user: AuthUser,
    onContinue: () -> Unit,
    onVerifyEmailClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar initial badge
                Surface(
                    shape = CircleShape,
                    color = StynoEmerald,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val initial = (user.displayName ?: user.email ?: user.phoneNumber ?: "U")
                            .firstOrNull()?.uppercase() ?: "U"
                        Text(
                            text = initial,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.displayName?.ifBlank { null } ?: "STYNO Member",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = user.email ?: user.phoneNumber ?: "Signed in",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Verification Status Chip
                    if (user.isEmailVerified) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StynoEmerald.copy(alpha = 0.15f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StynoEmerald,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Email Verified",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StynoEmerald
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                            modifier = Modifier.clickable(onClick = onVerifyEmailClick)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Email Unverified • Tap to verify",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_auth_continue_session"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald)
                ) {
                    Text("Continue to App", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_auth_logout"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

/**
 * Apple Human Interface Guidelines (HIG) Compliant Sign-in with Apple Button.
 */
@Composable
fun AppleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = "Sign in with Apple"
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color.Black,
            contentColor = androidx.compose.ui.graphics.Color.White,
            disabledContainerColor = androidx.compose.ui.graphics.Color(0xFF333333),
            disabledContentColor = androidx.compose.ui.graphics.Color(0xFF888888)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("btn_auth_apple")
    ) {
        Text(
            text = "",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.White
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = androidx.compose.ui.graphics.Color.White
        )
    }
}


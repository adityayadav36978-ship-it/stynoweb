package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.StynoEmerald
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.viewmodel.AuthUser
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import com.example.ui.viewmodel.UserRole
import kotlinx.coroutines.launch

@Composable
fun WelcomeEntryScreen(
    viewModel: StynoViewModel,
    authViewModel: AuthViewModel = viewModel(),
    onSelectOwner: () -> Unit = {},
    onSelectCustomer: () -> Unit = {},
    onOpenCustomerWizard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEmailAuth by remember { mutableStateOf(false) }
    var isSignUp by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    fun handleAuthSuccess(user: AuthUser) {
        viewModel.setAuthenticated(
            identifier = user.email?.ifBlank { null } ?: user.phoneNumber ?: user.uid,
            phone = user.phoneNumber ?: "",
            name = user.displayName ?: ""
        )
        if (viewModel.userRole.value == UserRole.OWNER) {
            viewModel.navigateTo(Screen.OWNER_DASHBOARD)
        } else {
            viewModel.navigateTo(Screen.HOME)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("welcome_entry_screen"),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 520.dp)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // STYNO Brand Emblem
                StynoHeroLogo(
                    size = 90.dp,
                    cornerRadius = 20.dp,
                    animated = true,
                    showGlow = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // STYNO Brand Title & Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "STYNO",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = StynoEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "STAYS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = StynoEmerald,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Find Your Stay • 0% Brokerage • Verified Hostels & PGs",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Role Selection Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Guest Option Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.setUserRole(UserRole.GUEST)
                                viewModel.setGuestAuthentication()
                                viewModel.navigateTo(Screen.HOME)
                            }
                            .testTag("role_guest_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, StynoEmerald.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = StynoEmerald,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Guest / Traveler",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Find & Book Stays",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Owner Option Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.setUserRole(UserRole.OWNER)
                                if (viewModel.isLoggedIn.value) {
                                    viewModel.navigateTo(Screen.OWNER_DASHBOARD)
                                } else {
                                    viewModel.navigateTo(Screen.AUTH)
                                }
                            }
                            .testTag("role_owner_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Host / Owner",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "List & Manage",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Primary Authentication Options
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Sign In with Mobile OTP
                    Button(
                        onClick = {
                            viewModel.navigateTo(Screen.AUTH)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("sign_in_with_phone_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sign In with Mobile OTP",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    // Continue with Google
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                            .clickable(enabled = !isLoading) {
                                isLoading = true
                                authViewModel.signInWithGoogle(
                                    context = context,
                                    onSuccess = { user ->
                                        isLoading = false
                                        handleAuthSuccess(user)
                                    },
                                    onError = { err ->
                                        isLoading = false
                                        scope.launch { snackbarHostState.showSnackbar(err) }
                                    }
                                )
                            }
                            .testTag("continue_with_google_button")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFF4285F4)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Continue with Apple
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                            .clickable(enabled = !isLoading) {
                                val activity = context as? Activity
                                if (activity != null) {
                                    isLoading = true
                                    authViewModel.signInWithApple(
                                        activity = activity,
                                        onSuccess = { user ->
                                            isLoading = false
                                            handleAuthSuccess(user)
                                        },
                                        onError = { err ->
                                            isLoading = false
                                            scope.launch { snackbarHostState.showSnackbar(err) }
                                        }
                                    )
                                } else {
                                    scope.launch { snackbarHostState.showSnackbar("Apple Sign-In requires active window context") }
                                }
                            }
                            .testTag("continue_with_apple_button")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Continue with Apple",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Continue with Email & Password toggle
                    OutlinedButton(
                        onClick = { showEmailAuth = !showEmailAuth },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("continue_with_email_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (showEmailAuth) "Hide Email Form" else "Continue with Email & Password",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    // Expandable Email / Password Form
                    AnimatedVisibility(
                        visible = showEmailAuth,
                        enter = fadeIn() + androidx.compose.animation.expandVertically(),
                        exit = fadeOut() + androidx.compose.animation.shrinkVertically()
                    ) {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (isSignUp) {
                                    OutlinedTextField(
                                        value = nameInput,
                                        onValueChange = { nameInput = it },
                                        label = { Text("Full Name") },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                OutlinedTextField(
                                    value = emailInput,
                                    onValueChange = { emailInput = it },
                                    label = { Text("Email Address") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { passwordInput = it },
                                    label = { Text("Password") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (emailInput.isBlank() || passwordInput.isBlank()) {
                                            scope.launch { snackbarHostState.showSnackbar("Please enter both email and password") }
                                            return@Button
                                        }
                                        isLoading = true
                                        if (isSignUp) {
                                            authViewModel.signUpWithEmail(
                                                email = emailInput,
                                                password = passwordInput,
                                                displayName = nameInput.ifBlank { null },
                                                onSuccess = {
                                                    isLoading = false
                                                    val u = authViewModel.currentUser.value
                                                    if (u != null) {
                                                        handleAuthSuccess(u)
                                                    } else {
                                                        viewModel.setAuthenticated(emailInput, "")
                                                        viewModel.navigateTo(Screen.HOME)
                                                    }
                                                },
                                                onError = { err ->
                                                    isLoading = false
                                                    scope.launch { snackbarHostState.showSnackbar(err) }
                                                }
                                            )
                                        } else {
                                            authViewModel.signInWithEmail(
                                                email = emailInput,
                                                password = passwordInput,
                                                onSuccess = {
                                                    isLoading = false
                                                    val u = authViewModel.currentUser.value
                                                    if (u != null) {
                                                        handleAuthSuccess(u)
                                                    } else {
                                                        viewModel.setAuthenticated(emailInput, "")
                                                        viewModel.navigateTo(Screen.HOME)
                                                    }
                                                },
                                                onError = { err ->
                                                    isLoading = false
                                                    scope.launch { snackbarHostState.showSnackbar(err) }
                                                }
                                            )
                                        }
                                    },
                                    enabled = !isLoading,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    } else {
                                        Text(if (isSignUp) "Create Account & Enter" else "Sign In & Enter")
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isSignUp) "Already have an account?" else "Need an account?",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    TextButton(onClick = { isSignUp = !isSignUp }) {
                                        Text(
                                            text = if (isSignUp) "Sign In" else "Sign Up",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Continue as Guest button
                    OutlinedButton(
                        onClick = {
                            viewModel.setUserRole(UserRole.GUEST)
                            viewModel.setGuestAuthentication()
                            viewModel.navigateTo(Screen.HOME)
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("continue_as_guest_button")
                    ) {
                        Text(
                            text = "Explore Stays as Guest (No Login Required)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Preserved Secondary Entry Points
            item {
                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            viewModel.setUserRole(UserRole.GUEST)
                            viewModel.setGuestAuthentication()
                            viewModel.navigateTo(Screen.HOME)
                        }
                    ) {
                        Text(
                            text = "Browse Stays",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    TextButton(
                        onClick = {
                            viewModel.setUserRole(UserRole.OWNER)
                            viewModel.navigateTo(Screen.OWNER_DASHBOARD)
                        }
                    ) {
                        Text(
                            text = "Host / Owner Portal",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

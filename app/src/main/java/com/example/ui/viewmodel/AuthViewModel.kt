package com.example.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.example.data.util.StynoAuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Representation of an authenticated user in Styno.
 */
data class AuthUser(
    val uid: String,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val isAnonymous: Boolean = false,
    val isEmailVerified: Boolean = false,
    val providerId: String? = null
) {
    val isAppleUser: Boolean get() = providerId == "apple.com"
    val isGoogleUser: Boolean get() = providerId == "google.com"
}

/**
 * Helper extension to convert a FirebaseUser to Styno's AuthUser.
 */
fun FirebaseUser.toAuthUser(): AuthUser {
    val primaryProvider = providerData.firstOrNull { it.providerId != "firebase" }?.providerId ?: providerId
    return AuthUser(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString(),
        phoneNumber = phoneNumber,
        isAnonymous = isAnonymous,
        isEmailVerified = isEmailVerified,
        providerId = primaryProvider
    )
}

/**
 * UI State for authentication operations.
 */
sealed interface AuthUiState {
    data object Idle : AuthUiState
    data class Loading(val message: String = "Signing in...") : AuthUiState
    data class Authenticated(val user: AuthUser, val isNewUser: Boolean = false) : AuthUiState
    data class Error(val message: String, val throwable: Throwable? = null) : AuthUiState
}

/**
 * Authentication ViewModel scaffolding Firebase Auth and AndroidX Credential Manager (Google Sign-In).
 */
class AuthViewModel(
    private val firebaseAuth: FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUser?>(firebaseAuth?.currentUser?.toAuthUser())
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    val isAuthenticated: StateFlow<Boolean> = _currentUser
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _currentUser.value != null
        )

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser?.toAuthUser()
        _currentUser.value = user
        if (user != null && _uiState.value !is AuthUiState.Loading) {
            _uiState.value = AuthUiState.Authenticated(user)
        } else if (user == null && _uiState.value !is AuthUiState.Loading && _uiState.value !is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }

    init {
        firebaseAuth?.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth?.removeAuthStateListener(authStateListener)
    }

    /**
     * Clear error state and return to Authenticated or Idle.
     */
    fun clearError() {
        val user = _currentUser.value
        _uiState.value = if (user != null) {
            AuthUiState.Authenticated(user)
        } else {
            AuthUiState.Idle
        }
    }

    /**
     * Sign in with Email and Password.
     */
    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (email.isBlank() || password.isBlank()) {
            val errorMsg = "Email and password cannot be empty"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        val auth = firebaseAuth ?: run {
            val errorMsg = "Firebase Auth is not initialized"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading("Signing in with email...")
            try {
                val result: AuthResult = auth.signInWithEmailAndPassword(email.trim(), password).awaitTask()
                val user = result.user?.toAuthUser()
                if (user != null) {
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Authenticated(user, isNewUser = false)
                    onSuccess?.invoke()
                } else {
                    val errorMsg = "Failed to retrieve user profile after sign-in"
                    _uiState.value = AuthUiState.Error(errorMsg)
                    onError?.invoke(errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sign in with email failed", e)
                val message = e.localizedMessage ?: "Sign in failed"
                _uiState.value = AuthUiState.Error(message, e)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Sign up with Email and Password.
     */
    fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String? = null,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            val errorMsg = "Please enter a valid email address"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        if (password.isBlank() || password.length < 6) {
            val errorMsg = "Password must be at least 6 characters"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        val auth = firebaseAuth ?: run {
            val errorMsg = "Firebase Auth is not initialized"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading("Creating your account...")
            try {
                val result: AuthResult = auth.createUserWithEmailAndPassword(email.trim(), password).awaitTask()
                val firebaseUser = result.user

                if (firebaseUser != null && !displayName.isNullOrBlank()) {
                    try {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName.trim())
                            .build()
                        firebaseUser.updateProfile(profileUpdates).awaitTask()
                    } catch (profileEx: Exception) {
                        Log.w(TAG, "Failed to update display name on user creation", profileEx)
                    }
                }

                // Automatically attempt to send verification email upon sign up
                try {
                    firebaseUser?.sendEmailVerification()?.awaitTask()
                } catch (verifyEx: Exception) {
                    Log.w(TAG, "Failed to dispatch automatic verification email", verifyEx)
                }

                val user = auth.currentUser?.toAuthUser() ?: firebaseUser?.toAuthUser()
                if (user != null) {
                    syncUserProfileToFirestore(user)
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Authenticated(user, isNewUser = true)
                    onSuccess?.invoke()
                } else {
                    val errorMsg = "Failed to retrieve user after sign-up"
                    _uiState.value = AuthUiState.Error(errorMsg)
                    onError?.invoke(errorMsg)
                }
            } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                Log.w(TAG, "Email already in use: ${e.message}")
                val message = "An account with this email address already exists. Please sign in instead."
                _uiState.value = AuthUiState.Error(message, e)
                onError?.invoke(message)
            } catch (e: com.google.firebase.auth.FirebaseAuthWeakPasswordException) {
                Log.w(TAG, "Weak password: ${e.message}")
                val message = e.reason ?: "Password is too weak. Please use at least 6 characters."
                _uiState.value = AuthUiState.Error(message, e)
                onError?.invoke(message)
            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                Log.w(TAG, "Invalid credentials during sign up: ${e.message}")
                val message = "The email address format is invalid. Please enter a valid email."
                _uiState.value = AuthUiState.Error(message, e)
                onError?.invoke(message)
            } catch (e: Exception) {
                Log.e(TAG, "Sign up failed", e)
                val raw = e.localizedMessage ?: "Sign up failed"
                val message = if (raw.contains("already in use", ignoreCase = true)) {
                    "An account with this email address already exists. Please sign in instead."
                } else raw
                _uiState.value = AuthUiState.Error(message, e)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Sign in with Google ID Token directly (production support for Web, Cross-platform, and custom OAuth).
     */
    fun signInWithGoogleIdToken(
        idToken: String,
        accessToken: String? = null,
        onSuccess: ((AuthUser) -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        val auth = firebaseAuth ?: run {
            val errorMsg = "Firebase Auth is not initialized"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        if (idToken.isBlank()) {
            val errorMsg = "Invalid Google ID token"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading("Authenticating with Google...")
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, accessToken)
                val authResult: AuthResult = auth.signInWithCredential(credential).awaitTask()
                val user = authResult.user?.toAuthUser()
                if (user != null) {
                    syncUserProfileToFirestore(user)
                    _currentUser.value = user
                    val isNew = authResult.additionalUserInfo?.isNewUser ?: false
                    _uiState.value = AuthUiState.Authenticated(user, isNewUser = isNew)
                    onSuccess?.invoke(user)
                } else {
                    val errorMsg = "Failed to obtain Firebase user after Google sign-in"
                    _uiState.value = AuthUiState.Error(errorMsg)
                    onError?.invoke(errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In with token failed", e)
                val errorMsg = e.localizedMessage ?: "Google Sign-In failed"
                _uiState.value = AuthUiState.Error(errorMsg, e)
                onError?.invoke(errorMsg)
            }
        }
    }

    /**
     * Sign in with Google using AndroidX Credential Manager and Google Identity.
     */
    fun signInWithGoogle(
        context: Context,
        webClientId: String = "",
        onSuccess: ((AuthUser) -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        val resolvedClientId = if (webClientId.isNotBlank()) {
            webClientId.trim()
        } else {
            val resId = runCatching {
                context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            }.getOrDefault(0)
            if (resId != 0) context.getString(resId) else "732313288789-web-client.apps.googleusercontent.com"
        }

        val auth = firebaseAuth ?: run {
            val errorMsg = "Firebase Auth is not initialized"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading("Opening Google Sign-In...")
            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(resolvedClientId)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        _uiState.value = AuthUiState.Loading("Authenticating with Google token...")

                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        val authResult: AuthResult = auth.signInWithCredential(firebaseCredential).awaitTask()

                        val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
                        val user = authResult.user?.toAuthUser()

                        if (user != null) {
                            syncUserProfileToFirestore(user)
                            _currentUser.value = user
                            _uiState.value = AuthUiState.Authenticated(user, isNewUser = isNewUser)
                            onSuccess?.invoke(user)
                        } else {
                            val errorMsg = "Failed to obtain Firebase user after Google sign-in"
                            _uiState.value = AuthUiState.Error(errorMsg)
                            onError?.invoke(errorMsg)
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Invalid Google ID token response", e)
                        val errorMsg = "Invalid Google ID token response: ${e.message}"
                        _uiState.value = AuthUiState.Error(errorMsg, e)
                        onError?.invoke(errorMsg)
                    }
                } else {
                    val errorMsg = "Unexpected credential type returned from Credential Manager: ${credential.javaClass.name}"
                    _uiState.value = AuthUiState.Error(errorMsg)
                    onError?.invoke(errorMsg)
                }
            } catch (e: GetCredentialCancellationException) {
                Log.d(TAG, "Google sign-in cancelled by user")
                _uiState.value = AuthUiState.Idle
            } catch (e: GetCredentialException) {
                val isCancelled = e.message?.contains("cancel", ignoreCase = true) == true
                if (isCancelled) {
                    Log.d(TAG, "Google sign-in cancelled by user (exception message)")
                    _uiState.value = AuthUiState.Idle
                } else {
                    Log.e(TAG, "Credential Manager error: ${e.message}", e)
                    val errorMsg = if (e.message?.contains("No credentials available", ignoreCase = true) == true) {
                        "No Google accounts found on device. Please add a Google account in Android Settings."
                    } else {
                        "Google Sign-In failed: ${e.message}"
                    }
                    _uiState.value = AuthUiState.Error(errorMsg, e)
                    onError?.invoke(errorMsg)
                }
            } catch (e: Exception) {
                val isCancelled = e.message?.contains("cancel", ignoreCase = true) == true
                if (isCancelled) {
                    Log.d(TAG, "Google sign-in cancelled by user")
                    _uiState.value = AuthUiState.Idle
                } else {
                    Log.e(TAG, "Google Sign-In authentication error", e)
                    val errorMsg = e.localizedMessage ?: "Google Sign-In failed"
                    _uiState.value = AuthUiState.Error(errorMsg, e)
                    onError?.invoke(errorMsg)
                }
            }
        }
    }

    /**
     * Sign in as an anonymous guest user.
     */
    fun signInAnonymously(
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        val auth = firebaseAuth ?: run {
            val errorMsg = "Firebase Auth is not initialized"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading("Continuing as guest...")
            try {
                val result: AuthResult = auth.signInAnonymously().awaitTask()
                val user = result.user?.toAuthUser()
                if (user != null) {
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Authenticated(user, isNewUser = true)
                    onSuccess?.invoke()
                } else {
                    val errorMsg = "Failed to create anonymous session"
                    _uiState.value = AuthUiState.Error(errorMsg)
                    onError?.invoke(errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Anonymous sign in failed", e)
                val message = e.localizedMessage ?: "Guest sign-in failed"
                _uiState.value = AuthUiState.Error(message, e)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Send password reset email.
     */
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (email.isBlank()) {
            val errorMsg = "Please enter your registered email address"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            val errorMsg = "Please enter a valid email address"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        val auth = firebaseAuth ?: run {
            val errorMsg = "Firebase Auth is not initialized"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading("Sending password reset email...")
            try {
                auth.sendPasswordResetEmail(email.trim()).awaitTask()
                _uiState.value = AuthUiState.Idle
                onSuccess?.invoke()
            } catch (e: Exception) {
                Log.e(TAG, "Password reset failed", e)
                val message = e.localizedMessage ?: "Password reset failed"
                _uiState.value = AuthUiState.Error(message, e)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Send email verification to current user.
     */
    fun sendEmailVerification(
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        val user = firebaseAuth?.currentUser ?: run {
            val errorMsg = "No signed-in user found"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading("Sending verification email...")
            try {
                user.sendEmailVerification().awaitTask()
                _uiState.value = AuthUiState.Authenticated(user.toAuthUser())
                onSuccess?.invoke()
            } catch (e: Exception) {
                Log.e(TAG, "Email verification failed", e)
                val message = e.localizedMessage ?: "Failed to send email verification"
                _uiState.value = AuthUiState.Error(message, e)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Reload the current user profile from Firebase to refresh isEmailVerified status.
     */
    fun reloadUser(
        onSuccess: ((AuthUser) -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        val user = firebaseAuth?.currentUser ?: run {
            val errorMsg = "No signed-in user found"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading("Checking verification status...")
            try {
                user.reload().awaitTask()
                val reloadedUser = firebaseAuth.currentUser?.toAuthUser() ?: user.toAuthUser()
                _currentUser.value = reloadedUser
                _uiState.value = AuthUiState.Authenticated(reloadedUser)
                onSuccess?.invoke(reloadedUser)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reload user", e)
                val message = e.localizedMessage ?: "Failed to check email verification status"
                _uiState.value = AuthUiState.Error(message, e)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Update user profile display name.
     */
    fun updateDisplayName(
        displayName: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        val user = firebaseAuth?.currentUser ?: run {
            val errorMsg = "No signed-in user found"
            _uiState.value = AuthUiState.Error(errorMsg)
            onError?.invoke(errorMsg)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading("Updating display name...")
            try {
                val updates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName.trim())
                    .build()
                user.updateProfile(updates).awaitTask()
                val updatedUser = firebaseAuth.currentUser?.toAuthUser() ?: user.toAuthUser()
                _currentUser.value = updatedUser
                _uiState.value = AuthUiState.Authenticated(updatedUser)
                onSuccess?.invoke()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update profile", e)
                val message = e.localizedMessage ?: "Failed to update display name"
                _uiState.value = AuthUiState.Error(message, e)
                onError?.invoke(message)
            }
        }
    }

    /**
     * Sign in with Apple using official Firebase OAuthProvider for Android.
     * Auto-creates accounts for first-time users and directly signs in existing users.
     */
    fun signInWithApple(
        activity: Activity,
        onSuccess: (AuthUser) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val auth = firebaseAuth ?: run {
            val err = "Firebase Authentication is not available"
            _uiState.value = AuthUiState.Error(err)
            onError(err)
            return
        }

        _uiState.value = AuthUiState.Loading("Connecting with Apple ID...")

        viewModelScope.launch {
            try {
                val provider = OAuthProvider.newBuilder("apple.com")
                provider.setScopes(listOf("email", "name"))

                val pendingResult = auth.pendingAuthResult
                val task = if (pendingResult != null) {
                    pendingResult
                } else {
                    auth.startActivityForSignInWithProvider(activity, provider.build())
                }

                val authResult = task.awaitTask()
                val user = authResult.user?.toAuthUser()
                if (user != null) {
                    syncUserProfileToFirestore(user)
                    _currentUser.value = user
                    val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
                    _uiState.value = AuthUiState.Authenticated(user, isNewUser = isNewUser)
                    onSuccess(user)
                } else {
                    val err = "Unable to retrieve Apple account details"
                    _uiState.value = AuthUiState.Error(err)
                    onError(err)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Apple Sign-In failed", e)
                val isCancelled = e.localizedMessage?.contains("cancel", ignoreCase = true) == true
                val err = if (isCancelled) {
                    "Apple Sign-In was cancelled."
                } else {
                    e.localizedMessage ?: "Apple authentication failed. Please check network connection."
                }
                _uiState.value = AuthUiState.Error(err, e)
                onError(err)
            }
        }
    }

    /**
     * Sync authenticated user profile data to Firestore user_profiles and users collections securely.
     */
    private suspend fun syncUserProfileToFirestore(authUser: AuthUser) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            val identifier = authUser.email?.ifBlank { null }
                ?: authUser.phoneNumber?.filter { it.isDigit() }
                ?: authUser.uid
            val sanitizedDocId = "usr_" + identifier.replace("@", "_").replace(".", "_")

            val profileData = hashMapOf(
                "id" to sanitizedDocId,
                "uid" to authUser.uid,
                "email" to (authUser.email ?: ""),
                "phoneNumber" to (authUser.phoneNumber ?: ""),
                "displayName" to (authUser.displayName ?: "Styno Traveler"),
                "fullName" to (authUser.displayName ?: (if (authUser.phoneNumber != null) "Styno Guest (${authUser.phoneNumber.takeLast(4)})" else "Styno Explorer")),
                "photoUrl" to (authUser.photoUrl ?: ""),
                "provider" to (authUser.providerId ?: "PASSWORD"),
                "providerId" to (authUser.providerId ?: "PASSWORD"),
                "platform" to "Android",
                "role" to "traveler",
                "isEmailVerified" to authUser.isEmailVerified,
                "lastLoginAt" to System.currentTimeMillis(),
                "isCloudSynced" to true
            )
            firestore.collection("user_profiles").document(sanitizedDocId)
                .set(profileData, SetOptions.merge())
                .awaitTask()
            firestore.collection("users").document(authUser.uid)
                .set(profileData, SetOptions.merge())
                .awaitTask()
            Log.i(TAG, "Synced profile to Firestore for user: $sanitizedDocId and uid: ${authUser.uid}")
        } catch (e: Exception) {
            Log.w(TAG, "Firestore user profile sync warning: ${e.localizedMessage}")
        }
    }

    /**
     * Sign out user from Firebase and clear Credential Manager state.
     */
    fun signOut(context: Context? = null) {
        try {
            firebaseAuth?.signOut()
            _currentUser.value = null
            _uiState.value = AuthUiState.Idle

            if (context != null) {
                viewModelScope.launch {
                    try {
                        val credentialManager = CredentialManager.create(context)
                        credentialManager.clearCredentialState(ClearCredentialStateRequest())
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to clear Credential Manager state during sign out", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign out error", e)
            _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Sign out failed", e)
        }
    }

    /**
     * Request a 6-digit OTP for phone authentication via StynoAuthService.
     */
    fun requestPhoneOtp(
        phoneNumber: String,
        onSuccess: (message: String, cooldownSec: Int) -> Unit = { _, _ -> },
        onError: (String) -> Unit = {}
    ) {
        val cleanPhone = phoneNumber.trim().replace("\\s+".toRegex(), "").replace("-", "")
        if (cleanPhone.length < 10) {
            val error = "Please enter a valid 10-digit mobile number"
            _uiState.value = AuthUiState.Error(error)
            onError(error)
            return
        }

        _uiState.value = AuthUiState.Loading("Sending OTP to $cleanPhone...")
        viewModelScope.launch {
            when (val result = StynoAuthService.requestOtp(cleanPhone, isPhone = true)) {
                is StynoAuthService.OtpSendResult.Success -> {
                    _uiState.value = AuthUiState.Idle
                    onSuccess(result.message, result.cooldownSeconds)
                }
                is StynoAuthService.OtpSendResult.RateLimited -> {
                    val msg = result.message
                    _uiState.value = AuthUiState.Error(msg)
                    onError(msg)
                }
                is StynoAuthService.OtpSendResult.Error -> {
                    _uiState.value = AuthUiState.Error(result.message)
                    onError(result.message)
                }
            }
        }
    }

    /**
     * Verify the 6-digit OTP code. If valid, authenticate and sync user profile.
     */
    fun verifyPhoneOtp(
        phoneNumber: String,
        enteredOtp: String,
        displayName: String = "",
        onSuccess: (AuthUser) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val cleanPhone = phoneNumber.trim().replace("\\s+".toRegex(), "").replace("-", "")
        val cleanOtp = enteredOtp.trim()

        if (cleanOtp.length != 6) {
            val error = "Please enter the complete 6-digit verification code"
            _uiState.value = AuthUiState.Error(error)
            onError(error)
            return
        }

        _uiState.value = AuthUiState.Loading("Verifying security code...")
        viewModelScope.launch {
            when (val result = StynoAuthService.verifyOtp(cleanPhone, isPhone = true, enteredOtp = cleanOtp)) {
                is StynoAuthService.OtpVerifyResult.Success -> {
                    val user = AuthUser(
                        uid = "styno_ph_${cleanPhone}",
                        displayName = displayName.ifBlank { "Styno Traveler" },
                        phoneNumber = if (cleanPhone.startsWith("+")) cleanPhone else "+91$cleanPhone",
                        email = null,
                        photoUrl = null
                    )
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Authenticated(user)
                    syncUserProfileToFirestore(user)
                    onSuccess(user)
                }
                is StynoAuthService.OtpVerifyResult.Expired -> {
                    val msg = result.message
                    _uiState.value = AuthUiState.Error(msg)
                    onError(msg)
                }
                is StynoAuthService.OtpVerifyResult.InvalidOtp -> {
                    val msg = result.message
                    _uiState.value = AuthUiState.Error(msg)
                    onError(msg)
                }
                is StynoAuthService.OtpVerifyResult.MaxAttemptsExceeded -> {
                    val msg = result.message
                    _uiState.value = AuthUiState.Error(msg)
                    onError(msg)
                }
                is StynoAuthService.OtpVerifyResult.Error -> {
                    val msg = result.message
                    _uiState.value = AuthUiState.Error(msg)
                    onError(msg)
                }
            }
        }
    }
}

/**
 * Extension function to await Android GMS Tasks with coroutines without requiring external libraries.
 */
private suspend fun <T> Task<T>.awaitTask(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            if (continuation.isActive) {
                continuation.resume(result)
            }
        }
        addOnFailureListener { exception ->
            if (continuation.isActive) {
                continuation.resumeWithException(exception)
            }
        }
        addOnCanceledListener {
            if (continuation.isActive) {
                continuation.cancel()
            }
        }
    }
}

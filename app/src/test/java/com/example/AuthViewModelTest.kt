package com.example

import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthUser
import com.example.ui.viewmodel.AuthViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthViewModelTest {

    @Test
    fun testInitialState() {
        val viewModel = AuthViewModel(firebaseAuth = null)

        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
        assertNull(viewModel.currentUser.value)
        assertFalse(viewModel.isAuthenticated.value)
    }

    @Test
    fun testInputValidationForEmailAndPassword() {
        val viewModel = AuthViewModel(firebaseAuth = null)

        var errorMessage: String? = null
        viewModel.signInWithEmail(
            email = "",
            password = "",
            onError = { errorMessage = it }
        )

        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals("Email and password cannot be empty", errorMessage)

        viewModel.clearError()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)

        viewModel.signUpWithEmail(
            email = "user@test.com",
            password = "123",
            onError = { errorMessage = it }
        )
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals("Password must be at least 6 characters", errorMessage)
    }

    @Test
    fun testPasswordResetValidation() {
        val viewModel = AuthViewModel(firebaseAuth = null)

        var errorMessage: String? = null
        viewModel.sendPasswordResetEmail(
            email = "",
            onError = { errorMessage = it }
        )

        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals("Please enter your registered email address", errorMessage)

        viewModel.clearError()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun testEmailVerificationWhenNotLoggedIn() {
        val viewModel = AuthViewModel(firebaseAuth = null)

        var sendVerifyError: String? = null
        viewModel.sendEmailVerification(
            onError = { sendVerifyError = it }
        )
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals("No signed-in user found", sendVerifyError)

        var reloadError: String? = null
        viewModel.reloadUser(
            onError = { reloadError = it }
        )
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals("No signed-in user found", reloadError)
    }

    @Test
    fun testSignOutResetsState() {
        val viewModel = AuthViewModel(firebaseAuth = null)
        viewModel.signOut()

        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
        assertNull(viewModel.currentUser.value)
        assertFalse(viewModel.isAuthenticated.value)
    }

    @Test
    fun testAuthUserDataClass() {
        val user = AuthUser(
            uid = "test-uid-123",
            email = "test@example.com",
            displayName = "Test User",
            photoUrl = "https://example.com/photo.jpg",
            phoneNumber = "+919876543210",
            isAnonymous = false,
            isEmailVerified = true
        )

        assertEquals("test-uid-123", user.uid)
        assertEquals("test@example.com", user.email)
        assertEquals("Test User", user.displayName)
        assertFalse(user.isAnonymous)
        assertTrue(user.isEmailVerified)
    }

    @Test
    fun testCompleteAuthCycleSimulation() {
        val viewModel = AuthViewModel(firebaseAuth = null)

        // 1. Initial State
        assertFalse(viewModel.isAuthenticated.value)
        assertNull(viewModel.currentUser.value)

        // 2. Email Sign Up Validation
        var signUpError: String? = null
        viewModel.signUpWithEmail(
            email = "newstudent@styno.com",
            password = "securePassword123",
            onError = { signUpError = it }
        )
        // With firebaseAuth = null in unit tests, error is reported gracefully
        assertNotNull(signUpError)
        assertTrue(viewModel.uiState.value is AuthUiState.Error)

        // 3. Clear error
        viewModel.clearError()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)

        // 4. Email Sign In Validation
        var signInError: String? = null
        viewModel.signInWithEmail(
            email = "student@styno.com",
            password = "securePassword123",
            onError = { signInError = it }
        )
        assertNotNull(signInError)
        assertTrue(viewModel.uiState.value is AuthUiState.Error)

        // 5. Forgot Password Validation
        var resetSent = false
        var resetError: String? = null
        viewModel.sendPasswordResetEmail(
            email = "student@styno.com",
            onSuccess = { resetSent = true },
            onError = { resetError = it }
        )
        // Handled cleanly with null firebaseAuth
        assertNotNull(resetError)

        // 6. Sign Out
        viewModel.signOut()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
        assertNull(viewModel.currentUser.value)
        assertFalse(viewModel.isAuthenticated.value)
    }
}

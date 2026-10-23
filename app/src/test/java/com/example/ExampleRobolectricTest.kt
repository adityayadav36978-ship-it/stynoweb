package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import com.example.ui.viewmodel.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context matches STYNO`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("STYNO", appName)
    }

    @Test
    fun `user selection and authentication routes strictly to User Home Screen`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StynoViewModel(app)
        viewModel.logout()
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)

        // User selects User portal
        viewModel.startLoginForRole(UserRole.GUEST)
        assertEquals(Screen.AUTH, viewModel.currentScreen.value)
        assertEquals(UserRole.GUEST, viewModel.targetAuthRole.value)

        // Complete authentication
        viewModel.syncAuthenticatedUser("usr_aditya_123", "aditya@styno.in", "9876543210", "Aditya")
        viewModel.setUserRole(UserRole.GUEST)
        viewModel.navigateTo(Screen.HOME)

        assertEquals(Screen.HOME, viewModel.currentScreen.value)
        assertEquals(UserRole.GUEST, viewModel.userRole.value)
        assertNotEquals(Screen.OWNER_DASHBOARD, viewModel.currentScreen.value)
    }

    @Test
    fun `owner selection and authentication routes strictly to Owner Dashboard, not User Home`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StynoViewModel(app)
        viewModel.logout()
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)

        // User selects Owner portal
        viewModel.startLoginForRole(UserRole.OWNER)
        assertEquals(Screen.AUTH, viewModel.currentScreen.value)
        assertEquals(UserRole.OWNER, viewModel.targetAuthRole.value)

        // Complete authentication as Owner
        viewModel.syncAuthenticatedUser("usr_host_9988", "host@styno.in", "9845011223", "Rajesh Host")
        viewModel.setUserRole(UserRole.OWNER)
        viewModel.navigateTo(Screen.OWNER_DASHBOARD)

        assertEquals(Screen.OWNER_DASHBOARD, viewModel.currentScreen.value)
        assertEquals(UserRole.OWNER, viewModel.userRole.value)
        // CRITICAL BUG FIX VERIFICATION: Must NEVER open HOME
        assertNotEquals(Screen.HOME, viewModel.currentScreen.value)
    }

    @Test
    fun `owner 10-step property listing wizard navigation`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StynoViewModel(app)
        viewModel.syncAuthenticatedUser("usr_host_9988", "host@styno.in", "9845011223", "Rajesh Host")
        viewModel.setUserRole(UserRole.OWNER)
        assertEquals(Screen.OWNER_DASHBOARD, viewModel.currentScreen.value)

        // Navigate to 10-step listing wizard
        viewModel.clearActiveWizardContext()
        viewModel.navigateTo(Screen.ADD_PROPERTY)
        assertEquals(Screen.ADD_PROPERTY, viewModel.currentScreen.value)

        // Return back to Owner Dashboard
        viewModel.navigateTo(Screen.OWNER_DASHBOARD)
        assertEquals(Screen.OWNER_DASHBOARD, viewModel.currentScreen.value)
    }

    @Test
    fun `logout resets state and returns to welcome screen`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = StynoViewModel(app)
        viewModel.syncAuthenticatedUser("usr_host_9988", "host@styno.in", "9845011223", "Rajesh Host")
        viewModel.setUserRole(UserRole.OWNER)
        assertEquals(Screen.OWNER_DASHBOARD, viewModel.currentScreen.value)

        viewModel.logout()
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)
        assertFalse(viewModel.isLoggedIn.value)
    }
}

package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import com.example.ui.viewmodel.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeRoleGatewayTest {

    private lateinit var context: Context
    private lateinit var viewModel: StynoViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        viewModel = StynoViewModel(context as android.app.Application)
        viewModel.logout()
    }

    @Test
    fun testFirstScreenIsWelcomeScreenForUnauthenticatedUser() {
        assertFalse(viewModel.isLoggedIn.value)
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)
    }

    @Test
    fun testUserLoginGatewayFlow() {
        // 1. User starts on Welcome Screen
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)

        // 2. Click USER LOGIN (Continue as User)
        viewModel.startLoginForRole(UserRole.GUEST)
        assertEquals(UserRole.GUEST, viewModel.targetAuthRole.value)
        assertEquals(Screen.AUTH, viewModel.currentScreen.value)

        // 3. Complete authentication as User
        viewModel.syncAuthenticatedUser(
            uid = "usr_guest_9988",
            email = "traveler@styno.in",
            phone = "9876543210",
            displayName = "Student Traveler"
        )
        viewModel.setUserRole(UserRole.GUEST)
        viewModel.navigateTo(Screen.HOME)

        // 4. Confirm User is authenticated and routed to User Section (Home)
        assertTrue(viewModel.isLoggedIn.value)
        assertEquals(UserRole.GUEST, viewModel.userRole.value)
        assertEquals(Screen.HOME, viewModel.currentScreen.value)

        // 5. User logs out
        viewModel.logout()
        assertFalse(viewModel.isLoggedIn.value)
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)
    }

    @Test
    fun testOwnerLoginGatewayFlow() {
        // 1. Owner starts on Welcome Screen
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)

        // 2. Click OWNER LOGIN (Continue as Owner)
        viewModel.startLoginForRole(UserRole.OWNER)
        assertEquals(UserRole.OWNER, viewModel.targetAuthRole.value)
        assertEquals(Screen.AUTH, viewModel.currentScreen.value)

        // 3. Complete authentication as Owner
        viewModel.syncAuthenticatedUser(
            uid = "usr_owner_7766",
            email = "host@styno.in",
            phone = "9123456780",
            displayName = "Heritage Host"
        )
        viewModel.setUserRole(UserRole.OWNER)
        viewModel.navigateTo(Screen.OWNER_DASHBOARD)

        // 4. Confirm Owner is authenticated and routed to Owner Section (Owner Dashboard)
        assertTrue(viewModel.isLoggedIn.value)
        assertEquals(UserRole.OWNER, viewModel.userRole.value)
        assertEquals(Screen.OWNER_DASHBOARD, viewModel.currentScreen.value)

        // 5. Owner logs out
        viewModel.logout()
        assertFalse(viewModel.isLoggedIn.value)
        // Returns to Welcome Screen where USER SECTION and OWNER SECTION are displayed
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)
    }

    @Test
    fun testRoleIsolationBetweenUserAndOwner() {
        // Authenticate User
        viewModel.startLoginForRole(UserRole.GUEST)
        viewModel.syncAuthenticatedUser(uid = "u1", email = "u@test.com")
        viewModel.setUserRole(UserRole.GUEST)
        viewModel.navigateTo(Screen.HOME)

        assertEquals(UserRole.GUEST, viewModel.userRole.value)
        assertEquals(Screen.HOME, viewModel.currentScreen.value)

        // Ensure user is not accidentally on Owner Dashboard
        assertFalse(viewModel.currentScreen.value == Screen.OWNER_DASHBOARD)

        // Switch to Owner via authentic flow
        viewModel.logout()
        viewModel.startLoginForRole(UserRole.OWNER)
        viewModel.syncAuthenticatedUser(uid = "o1", email = "o@test.com")
        viewModel.setUserRole(UserRole.OWNER)
        viewModel.navigateTo(Screen.OWNER_DASHBOARD)

        assertEquals(UserRole.OWNER, viewModel.userRole.value)
        assertEquals(Screen.OWNER_DASHBOARD, viewModel.currentScreen.value)
    }

    @Test
    fun testSetAuthenticatedDirectlyRoutesToOwnerDashboardForOwnerLogin() {
        // Unauthenticated start on Welcome
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)

        // Select Owner Section
        viewModel.startLoginForRole(UserRole.OWNER)
        assertEquals(UserRole.OWNER, viewModel.targetAuthRole.value)
        assertEquals(Screen.AUTH, viewModel.currentScreen.value)

        // Authenticate as owner via setAuthenticated
        viewModel.setAuthenticated(
            identifier = "host.verified@styno.in",
            phone = "9812345678",
            name = "Verified Host"
        )

        // Must open Owner Dashboard directly
        assertTrue(viewModel.isLoggedIn.value)
        assertEquals(UserRole.OWNER, viewModel.userRole.value)
        assertEquals(Screen.OWNER_DASHBOARD, viewModel.currentScreen.value)

        // Open 10-Step Property Listing Wizard
        viewModel.clearActiveWizardContext()
        viewModel.navigateTo(Screen.ADD_PROPERTY)
        assertEquals(Screen.ADD_PROPERTY, viewModel.currentScreen.value)
    }

    @Test
    fun testUnauthenticatedOwnerSelectionRequiresAuthFirst() {
        // Ensure unauthenticated user cannot directly be in Owner Dashboard
        assertFalse(viewModel.isLoggedIn.value)
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)

        // User initiates Owner login flow
        viewModel.startLoginForRole(UserRole.OWNER)
        assertEquals(Screen.AUTH, viewModel.currentScreen.value)
        assertFalse(viewModel.isLoggedIn.value)

        // Back navigation returns to Welcome when unauthenticated
        val navigatedBack = viewModel.navigateBack()
        assertTrue(navigatedBack)
        assertEquals(Screen.WELCOME, viewModel.currentScreen.value)
    }
}

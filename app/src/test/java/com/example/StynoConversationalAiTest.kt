package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.StynoConversationalEngine
import com.example.data.model.GenderSuitability
import com.example.data.model.Property
import com.example.data.model.PropertyType
import com.example.data.model.StynoAiActionType
import com.example.data.model.StynoAiConversationState
import com.example.data.model.StynoAiRole
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StynoConversationalAiTest {

    private lateinit var context: Context
    private lateinit var viewModel: StynoViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        viewModel = StynoViewModel(context as android.app.Application)
    }

    @Test
    fun testInitialGreetingMessage() {
        val initialResidentMsg = StynoConversationalEngine.createInitialMessage(StynoAiRole.RESIDENT)
        assertFalse(initialResidentMsg.isUser)
        assertTrue(initialResidentMsg.text.contains("Namaste", ignoreCase = true))
        assertTrue(initialResidentMsg.text.contains("location", ignoreCase = true))
        assertTrue(initialResidentMsg.quickReplies.contains("Bagaha"))

        val initialOwnerMsg = StynoConversationalEngine.createInitialMessage(StynoAiRole.OWNER)
        assertFalse(initialOwnerMsg.isUser)
        assertTrue(initialOwnerMsg.text.contains("Host", ignoreCase = true))
        assertTrue(initialOwnerMsg.quickReplies.contains("List New Property"))
    }

    @Test
    fun testResidentMultiTurnProgressiveFlow() {
        val allProps = viewModel.allProperties.value
        var state = StynoAiConversationState(role = StynoAiRole.RESIDENT, conversationStep = 1)

        // Step 1: Location input
        val (state1, reply1) = StynoConversationalEngine.processResidentInput(
            input = "Bagaha",
            currentState = state,
            allProperties = allProps
        )
        assertEquals("Bagaha", state1.selectedLocation)
        assertEquals(2, state1.conversationStep)
        assertTrue(reply1.text.contains("kiske liye chahiye", ignoreCase = true))
        assertTrue(reply1.quickReplies.any { it.contains("Boys", ignoreCase = true) })
        assertTrue(reply1.quickReplies.any { it.contains("Girls", ignoreCase = true) })

        // Step 2: Occupant input
        val (state2, reply2) = StynoConversationalEngine.processResidentInput(
            input = "Boys",
            currentState = state1,
            allProperties = allProps
        )
        assertEquals("Boys", state2.targetAudience)
        assertEquals(3, state2.conversationStep)
        assertTrue(reply2.text.contains("purpose", ignoreCase = true))

        // Step 3: Purpose input
        val (state3, reply3) = StynoConversationalEngine.processResidentInput(
            input = "College / Coaching",
            currentState = state2,
            allProperties = allProps
        )
        assertEquals(4, state3.conversationStep)
        assertTrue(reply3.text.contains("budget", ignoreCase = true))

        // Step 4: Budget input
        val (state4, reply4) = StynoConversationalEngine.processResidentInput(
            input = "Under ₹5,000/mo",
            currentState = state3,
            allProperties = allProps
        )
        assertEquals(5, state4.conversationStep)
        assertEquals(5000f, state4.budgetMax)
        assertTrue(reply4.text.contains("facilities", ignoreCase = true))

        // Step 5: Facilities / finish
        val (state5, reply5) = StynoConversationalEngine.processResidentInput(
            input = "Attached Washroom",
            currentState = state4,
            allProperties = allProps
        )
        assertEquals(6, state5.conversationStep)
        assertTrue(reply5.matchedProperties.isNotEmpty())
        // Verified: only genuine properties from the database are returned!
        reply5.matchedProperties.forEach { prop ->
            assertNotNull(allProps.find { it.id == prop.id })
        }
    }

    @Test
    fun testDirectCommandShortcutsInHindiAndEnglish() {
        val allProps = viewModel.allProperties.value
        val state = StynoAiConversationState(role = StynoAiRole.RESIDENT)

        // Bookings shortcut
        val (_, bookingsReply) = StynoConversationalEngine.processResidentInput(
            input = "Meri bookings dikhao",
            currentState = state,
            allProperties = allProps
        )
        assertNotNull(bookingsReply.action)
        assertEquals(StynoAiActionType.NAVIGATE_SCREEN, bookingsReply.action?.type)
        assertEquals("BOOKINGS", bookingsReply.action?.payload)

        // Add property shortcut
        val (_, listReply) = StynoConversationalEngine.processResidentInput(
            input = "Mujhe apni property list karni hai",
            currentState = state,
            allProperties = allProps
        )
        assertNotNull(listReply.action)
        assertEquals(StynoAiActionType.NAVIGATE_SCREEN, listReply.action?.type)
        assertEquals("ADD_PROPERTY", listReply.action?.payload)

        // Safety shortcut
        val (_, safetyReply) = StynoConversationalEngine.processResidentInput(
            input = "Emergency safety helpline",
            currentState = state,
            allProperties = allProps
        )
        assertNotNull(safetyReply.action)
        assertEquals(StynoAiActionType.NAVIGATE_SCREEN, safetyReply.action?.type)
        assertEquals("SAFETY_CENTER", safetyReply.action?.payload)
    }

    @Test
    fun testOwnerGuidanceNavigation() {
        val allProps = viewModel.allProperties.value
        val state = StynoAiConversationState(role = StynoAiRole.OWNER)

        // Photo upload query
        val (_, photoReply) = StynoConversationalEngine.processOwnerInput(
            input = "Help me upload my room photos",
            currentState = state,
            allProperties = allProps
        )
        assertNotNull(photoReply.action)
        assertEquals(2, photoReply.action?.wizardStep)

        // Pricing query
        val (_, priceReply) = StynoConversationalEngine.processOwnerInput(
            input = "Room rent aur security deposit set karna hai",
            currentState = state,
            allProperties = allProps
        )
        assertNotNull(priceReply.action)
        assertEquals(4, priceReply.action?.wizardStep)

        // Payout setup query
        val (_, payReply) = StynoConversationalEngine.processOwnerInput(
            input = "Host payment UPI setup",
            currentState = state,
            allProperties = allProps
        )
        assertNotNull(payReply.action)
        assertEquals(StynoAiActionType.OPEN_OWNER_TAB, payReply.action?.type)
        assertEquals(3, payReply.action?.ownerTabIndex)
    }

    @Test
    fun testViewModelAiWorkflowExecution() {
        // Open Resident AI
        viewModel.openConversationalAi(StynoAiRole.RESIDENT)
        assertTrue(viewModel.showConversationalAi.value)
        assertEquals(StynoAiRole.RESIDENT, viewModel.conversationalAiRole.value)

        // Send message
        viewModel.sendConversationalAiInput("Find a room in Bagaha")
        val messages = viewModel.conversationalAiMessages.value
        assertTrue(messages.size >= 2)
        assertTrue(messages.any { it.isUser && it.text == "Find a room in Bagaha" })
        assertTrue(messages.last().text.contains("Bagaha", ignoreCase = true) || messages.last().text.contains("room", ignoreCase = true))

        // Reset
        viewModel.resetConversationalAi(StynoAiRole.OWNER)
        assertEquals(StynoAiRole.OWNER, viewModel.conversationalAiRole.value)
        assertEquals(1, viewModel.conversationalAiMessages.value.size)

        // Dismiss
        viewModel.dismissConversationalAi()
        assertFalse(viewModel.showConversationalAi.value)
    }
}

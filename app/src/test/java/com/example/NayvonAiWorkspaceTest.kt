package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.NayvonAiEngine
import com.example.data.model.AiExecutionState
import com.example.data.model.NayvonAttachment
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel
import kotlinx.coroutines.runBlocking
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
class NayvonAiWorkspaceTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testNayvonBrandResource() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Styno", appName)
    }

    @Test
    fun testAiEngineAutonomousPlanAndExecution() = runBlocking {
        val engine = NayvonAiEngine()
        val statesObserved = mutableListOf<AiExecutionState>()

        val result = engine.processRequest(
            prompt = "Plan a comprehensive relocation strategy with zero-brokerage housing and checklist",
            attachments = emptyList(),
            onStateChanged = { statesObserved.add(it) },
            onPlanUpdated = { /* noop */ }
        )

        assertNotNull(result.plan)
        assertTrue(result.plan!!.steps.isNotEmpty())
        assertTrue(result.finalAnswer.isNotBlank())
        assertNotNull(result.verificationNotes)
        assertTrue(statesObserved.contains(AiExecutionState.THINKING))
        assertTrue(statesObserved.contains(AiExecutionState.PLANNING))
        assertTrue(statesObserved.contains(AiExecutionState.WORKING))
        assertTrue(statesObserved.contains(AiExecutionState.VERIFYING))
    }

    @Test
    fun testAiEngineClarificationGeneration() = runBlocking {
        val engine = NayvonAiEngine()
        val result = engine.processRequest(
            prompt = "find a place",
            attachments = emptyList()
        )

        // Vague query generates helpful clarification questions
        assertTrue(result.clarificationQuestions.isNotEmpty())
        assertTrue(result.finalAnswer.contains("clarify", ignoreCase = true) || result.finalAnswer.contains("specify", ignoreCase = true))
    }

    @Test
    fun testViewModelAiWorkspaceAndNavigation() {
        val viewModel = StynoViewModel(ApplicationProvider.getApplicationContext())

        // Test authentication leads to Home
        viewModel.setAuthenticated("user@nayvon.ai", "+1234567890", "Test User")
        assertEquals(Screen.HOME, viewModel.currentScreen.value)

        // Test guest authentication leads to Home
        viewModel.setGuestAuthentication()
        assertEquals(Screen.HOME, viewModel.currentScreen.value)

        // Test file attachment
        viewModel.attachFile(
            name = "Project_Scope.pdf",
            mimeType = "application/pdf",
            sizeString = "250 KB"
        )
        val attachments = viewModel.attachedFiles.value
        assertEquals(1, attachments.size)
        assertEquals("Project_Scope.pdf", attachments.first().name)

        // Test removing attachment
        viewModel.removeAttachment(attachments.first().id)
        assertTrue(viewModel.attachedFiles.value.isEmpty())

        // Test conversation management
        val initialConvCount = viewModel.conversations.value.size
        viewModel.startNewConversation(title = "Research Strategy")
        assertEquals(initialConvCount + 1, viewModel.conversations.value.size)
        assertEquals("Research Strategy", viewModel.conversations.value.first().title)
    }
}

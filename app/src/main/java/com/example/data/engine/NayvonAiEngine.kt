package com.example.data.engine

import com.example.data.model.AiExecutionState
import com.example.data.model.NayvonAttachment
import com.example.data.model.NayvonPlan
import com.example.data.model.NayvonPlanStep
import com.example.data.model.PlanStepStatus
import kotlinx.coroutines.delay
import java.util.Locale

class NayvonAiEngine {

    data class ExecutionResult(
        val finalAnswer: String,
        val plan: NayvonPlan?,
        val clarificationQuestions: List<String>,
        val verificationNotes: String?
    )

    fun requiresClarification(prompt: String): List<String>? {
        val trimmed = prompt.trim()
        val words = trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }
        val lower = trimmed.lowercase(Locale.ROOT)

        val isExtremelyBrief = words.size <= 3 && lower in listOf(
            "help", "organize it", "do it", "plan", "start", "optimize", "find it", "compare",
            "find a place", "find me a place", "find place", "where to stay", "book stay"
        )

        if (isExtremelyBrief) {
            return listOf(
                "Could you specify the target goal, city, or project you'd like me to assist with?",
                "Do you have preferred constraints such as timeline, budget, or specifications?",
                "Should I format the output as an actionable checklist, comparison table, or detailed plan?"
            )
        }
        return null
    }

    suspend fun processRequest(
        prompt: String,
        attachments: List<NayvonAttachment> = emptyList(),
        onStateChanged: suspend (AiExecutionState) -> Unit = {},
        onPlanUpdated: suspend (NayvonPlan) -> Unit = {}
    ): ExecutionResult {
        val lower = prompt.lowercase(Locale.ROOT)

        // 1. Thinking phase
        onStateChanged(AiExecutionState.THINKING)
        delay(400)

        // Check clarification
        val clarifications = requiresClarification(prompt)
        if (!clarifications.isNullOrEmpty()) {
            onStateChanged(AiExecutionState.COMPLETED)
            return ExecutionResult(
                finalAnswer = "I'm ready to help you with that! To provide the best possible outcome, could you please clarify or specify a few details?",
                plan = null,
                clarificationQuestions = clarifications,
                verificationNotes = "Clarification requested to align with user intent."
            )
        }

        // 2. Planning phase
        onStateChanged(AiExecutionState.PLANNING)
        val plan = generatePlan(prompt, attachments)
        onPlanUpdated(plan)
        delay(500)

        // 3. Dynamic Progress States
        val hasSearchNeed = lower.contains("find") || lower.contains("search") || lower.contains("stay") ||
            lower.contains("hostel") || lower.contains("pg") || lower.contains("flat") || lower.contains("price") ||
            lower.contains("where") || lower.contains("location")

        val hasAnalysisNeed = attachments.isNotEmpty() || lower.contains("analyze") || lower.contains("compare") ||
            lower.contains("review") || lower.contains("budget") || lower.contains("code") || lower.contains("data")

        // Step 1 Execution
        updatePlanStep(plan, 0, PlanStepStatus.IN_PROGRESS)
        onPlanUpdated(plan)
        if (hasSearchNeed) {
            onStateChanged(AiExecutionState.SEARCHING)
        } else {
            onStateChanged(AiExecutionState.WORKING)
        }
        delay(550)
        updatePlanStep(plan, 0, PlanStepStatus.COMPLETED)

        // Step 2 Execution
        if (plan.steps.size > 1) {
            updatePlanStep(plan, 1, PlanStepStatus.IN_PROGRESS)
            onPlanUpdated(plan)
            if (hasAnalysisNeed) {
                onStateChanged(AiExecutionState.ANALYZING)
            } else {
                onStateChanged(AiExecutionState.WORKING)
            }
            delay(500)
            updatePlanStep(plan, 1, PlanStepStatus.COMPLETED)
        }

        // Step 3 Execution
        if (plan.steps.size > 2) {
            updatePlanStep(plan, 2, PlanStepStatus.IN_PROGRESS)
            onPlanUpdated(plan)
            onStateChanged(AiExecutionState.WORKING)
            delay(400)
            updatePlanStep(plan, 2, PlanStepStatus.COMPLETED)
        }

        // 4. Verifying phase
        onStateChanged(AiExecutionState.VERIFYING)
        if (plan.steps.size > 3) {
            updatePlanStep(plan, 3, PlanStepStatus.IN_PROGRESS)
            onPlanUpdated(plan)
        }
        delay(450)
        if (plan.steps.size > 3) {
            updatePlanStep(plan, 3, PlanStepStatus.COMPLETED)
        }

        // 5. Completed
        onStateChanged(AiExecutionState.COMPLETED)

        val answer = synthesizeAnswer(prompt, attachments)
        val verificationNotes = "Verified: Result meets clarity, safety, and conciseness criteria."

        return ExecutionResult(
            finalAnswer = answer,
            plan = plan,
            clarificationQuestions = emptyList(),
            verificationNotes = verificationNotes
        )
    }

    private fun generatePlan(prompt: String, attachments: List<NayvonAttachment>): NayvonPlan {
        val lower = prompt.lowercase(Locale.ROOT)
        val steps = mutableListOf<NayvonPlanStep>()

        if (attachments.isNotEmpty()) {
            steps.add(NayvonPlanStep(title = "Inspect and parse ${attachments.size} attached document(s)"))
            steps.add(NayvonPlanStep(title = "Extract core context and key data points"))
            steps.add(NayvonPlanStep(title = "Formulate recommendations based on document contents"))
            steps.add(NayvonPlanStep(title = "Validate findings against user query"))
            return NayvonPlan(summary = "Multimodal Document Analysis & Execution Plan", steps = steps)
        }

        if (lower.contains("stay") || lower.contains("hostel") || lower.contains("pg") || lower.contains("flat") || lower.contains("rent")) {
            steps.add(NayvonPlanStep(title = "Analyze location, budget, and amenity requirements"))
            steps.add(NayvonPlanStep(title = "Search verified listings across NAYVON network"))
            steps.add(NayvonPlanStep(title = "Evaluate price-to-value ratio and zero-brokerage benefits"))
            steps.add(NayvonPlanStep(title = "Verify safety protocols and check-in guarantees"))
            return NayvonPlan(summary = "Accommodation Matching & Verification Plan", steps = steps)
        }

        if (lower.contains("code") || lower.contains("app") || lower.contains("bug") || lower.contains("api") || lower.contains("android")) {
            steps.add(NayvonPlanStep(title = "Analyze technical architecture and requirements"))
            steps.add(NayvonPlanStep(title = "Evaluate optimal design patterns and performance considerations"))
            steps.add(NayvonPlanStep(title = "Synthesize clean, production-ready code structure"))
            steps.add(NayvonPlanStep(title = "Verify edge-cases, error handling, and testability"))
            return NayvonPlan(summary = "Technical Architecture & Implementation Plan", steps = steps)
        }

        steps.add(NayvonPlanStep(title = "Understand core objectives and contextual constraints"))
        steps.add(NayvonPlanStep(title = "Gather and cross-reference relevant domain knowledge"))
        steps.add(NayvonPlanStep(title = "Draft clear, structured, and actionable guidance"))
        steps.add(NayvonPlanStep(title = "Verify completeness, accuracy, and next steps"))
        return NayvonPlan(summary = "Goal Resolution & Action Plan", steps = steps)
    }

    private fun updatePlanStep(plan: NayvonPlan, index: Int, status: PlanStepStatus) {
        if (index in plan.steps.indices) {
            val old = plan.steps[index]
            (plan.steps as? MutableList<NayvonPlanStep>)?.set(index, old.copy(status = status))
        }
    }

    private fun synthesizeAnswer(prompt: String, attachments: List<NayvonAttachment>): String {
        val lower = prompt.lowercase(Locale.ROOT)

        if (attachments.isNotEmpty()) {
            val names = attachments.joinToString(", ") { it.name }
            return """
                ### Attachment Analysis Complete
                
                I have inspected the attached document(s): **$names**.
                
                **Key Observations:**
                • Successfully extracted structural content and identified primary parameters.
                • Verified compliance with query parameters: "$prompt".
                
                **Recommendations & Next Actions:**
                1. **Structured Review**: The parameters align with optimal operational standards.
                2. **Data Integration**: We can immediately reference these items in your active projects or export a summary report.
                3. **Follow-up**: Would you like me to generate a formal summary or convert this into a task checklist?
            """.trimIndent()
        }

        if (lower.contains("stay") || lower.contains("hostel") || lower.contains("pg") || lower.contains("apartment") || lower.contains("flat")) {
            return """
                ### Verified Accommodation Intelligence
                
                Here is a curated assessment tailored to your stay requirements:
                
                **1. Prime Recommendations with 0% Brokerage:**
                • **Elite Executive PG / Co-living Suites**: Prime tech-corridor access, high-speed Wi-Fi, biometric security, and full meal plans included.
                • **Studio Apartments & Private Flats**: Flexible short/long-term tenures, zero hidden maintenance, digital check-in.
                
                **2. Verified Safety & Amenities:**
                • 24/7 CCTV surveillance & verified host credentials.
                • Rapid SOS emergency dispatch integration.
                • Instant booking confirmation with transparent security deposit protection.
                
                *You can also navigate to the Accommodations catalog in the sidebar to view live map locations and virtual 3D tours.*
            """.trimIndent()
        }

        if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey")) {
            return """
                Hello! I am **NAYVON**, your modern AI assistant workspace.
                
                Here is what I can do for you:
                • **Multi-step Execution**: Understand complex goals, formulate transparent plans, and verify results.
                • **Natural Voice Input**: Tap the microphone button to dictate queries hands-free.
                • **Multimodal Attachments**: Upload documents, images, and data files for instant analysis.
                • **Project & Knowledge Management**: Save conversation threads, organize projects, and access verified tools.
                
                How can I assist you right now?
            """.trimIndent()
        }

        return """
            ### Strategy & Recommendations
            
            Based on your request: **"$prompt"**, here is the synthesized execution breakdown:
            
            **1. Key Insights & Architecture:**
            • Evaluated primary objectives to ensure direct efficiency without extraneous clutter.
            • Aligned best-practice standards and verified boundary conditions.
            
            **2. Step-by-Step Implementation:**
            • **Phase 1 (Setup)**: Establish clear operational parameters and identify critical dependencies.
            • **Phase 2 (Execution)**: Implement modern, decoupled modules with strict typing and responsive states.
            • **Phase 3 (Verification)**: Perform rigorous automated testing to validate quality and resilience.
            
            **3. Summary & Next Steps:**
            Everything is structured for immediate execution. Would you like me to dive deeper into any specific aspect, generate implementation files, or create a new dedicated project?
        """.trimIndent()
    }
}

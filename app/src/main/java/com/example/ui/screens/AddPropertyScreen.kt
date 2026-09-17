package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PropertyType
import com.example.ui.viewmodel.Screen
import com.example.ui.screens.wizard.*
import com.example.ui.viewmodel.StynoViewModel
import kotlinx.coroutines.launch

val wizardStepTitles = listOf(
    "Type",
    "Basic Info",
    "Details",
    "Pricing",
    "Amenities",
    "Food",
    "Category",
    "Preview",
    "Publish",
    "Payment"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val activeEditingProperty by viewModel.activeEditingProperty.collectAsStateWithLifecycle()
    val activeEditingDraft by viewModel.activeEditingDraft.collectAsStateWithLifecycle()
    val savedDrafts by viewModel.propertyDrafts.collectAsStateWithLifecycle()
    val currentUserName by viewModel.userName.collectAsStateWithLifecycle()
    val currentUserPhone by viewModel.userPhone.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Initialize Wizard State from Draft or Property or New
    var wizardState by remember {
        mutableStateOf(
            when {
                activeEditingProperty != null -> PropertyWizardState.fromProperty(activeEditingProperty!!)
                activeEditingDraft != null -> PropertyWizardState.fromDraftEntity(activeEditingDraft!!)
                else -> PropertyWizardState(
                    ownerPhone = currentUserPhone,
                    ownerEmail = "host@stynostays.com"
                )
            }
        )
    }

    // Success Published Dialog State
    var showPublishSuccessDialog by remember { mutableStateOf(false) }
    var publishedPropertyName by remember { mutableStateOf("") }
    var showDraftSavedSnackbar by remember { mutableStateOf(false) }

    // Intercept back navigation
    BackHandler {
        if (wizardState.currentStep > 1) {
            wizardState = wizardState.copy(currentStep = wizardState.currentStep - 1)
        } else {
            viewModel.clearActiveWizardContext()
            viewModel.navigateTo(Screen.OWNER_DASHBOARD)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (wizardState.editingOriginalPropertyId != null) "Edit Property Listing" else "List Your Property",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Step ${wizardState.currentStep} of 10: ${wizardStepTitles[wizardState.currentStep - 1]}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (wizardState.currentStep > 1) {
                                wizardState = wizardState.copy(currentStep = wizardState.currentStep - 1)
                            } else {
                                viewModel.clearActiveWizardContext()
                                viewModel.navigateTo(Screen.OWNER_DASHBOARD)
                            }
                        },
                        modifier = Modifier.testTag("btn_wizard_back")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Quick Save Draft Button in TopBar
                    FilledTonalButton(
                        onClick = {
                            val draftEntity = wizardState.toDraftEntity()
                            viewModel.saveListingDraft(draftEntity) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Draft saved! You can resume editing anytime.")
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("topbar_btn_save_draft")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Draft", fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Previous Button
                    if (wizardState.currentStep > 1) {
                        OutlinedButton(
                            onClick = {
                                wizardState = wizardState.copy(currentStep = wizardState.currentStep - 1)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_step_prev")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Previous")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    // Primary Action: Next / Preview / Publish
                    Button(
                        onClick = {
                            when (wizardState.currentStep) {
                                1 -> {
                                    // Ensure default room templates for selected type
                                    val currentType = wizardState.propertyType
                                    val updatedRooms = PropertyWizardState.defaultRoomsFor(currentType)
                                    wizardState = wizardState.copy(
                                        currentStep = 2,
                                        rooms = if (wizardState.rooms.isEmpty()) updatedRooms else wizardState.rooms
                                    )
                                }
                                2 -> {
                                    if (wizardState.propertyName.isBlank()) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Please enter a property name before continuing.")
                                        }
                                    } else if (wizardState.address.isBlank()) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Please enter street address before continuing.")
                                        }
                                    } else {
                                        wizardState = wizardState.copy(currentStep = 3)
                                    }
                                }
                                3 -> wizardState = wizardState.copy(currentStep = 4)
                                4 -> {
                                    if (wizardState.rooms.isEmpty()) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Please configure at least one room type.")
                                        }
                                    } else {
                                        wizardState = wizardState.copy(currentStep = 5)
                                    }
                                }
                                5 -> wizardState = wizardState.copy(currentStep = 6)
                                6 -> wizardState = wizardState.copy(currentStep = 7)
                                7 -> wizardState = wizardState.copy(currentStep = 8)
                                8 -> wizardState = wizardState.copy(currentStep = 9)
                                9 -> wizardState = wizardState.copy(currentStep = 10)
                                10 -> {
                                    // Publish action
                                    val prop = wizardState.toProperty(currentUserName, currentUserPhone)
                                    publishedPropertyName = prop.name
                                    viewModel.publishOwnerProperty(
                                        property = prop,
                                        draftIdToDelete = wizardState.draftId
                                    ) {
                                        showPublishSuccessDialog = true
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (wizardState.currentStep == 10) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("btn_step_primary")
                    ) {
                        Text(
                            text = when (wizardState.currentStep) {
                                7 -> "View Preview (Step 8)"
                                8 -> "Proceed to Publish (Step 9)"
                                9 -> "Setup Payout (Step 10)"
                                10 -> if (wizardState.editingOriginalPropertyId != null) "Update Listing" else "Publish Property Live"
                                else -> "Next: ${wizardStepTitles[wizardState.currentStep]}"
                            },
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (wizardState.currentStep == 10) Icons.Default.RocketLaunch else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Stepper Header & Horizontal Progress
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp)
            ) {
                // Progress Bar
                LinearProgressIndicator(
                    progress = { wizardState.currentStep / 10f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                // Scrollable step indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    wizardStepTitles.forEachIndexed { index, title ->
                        val stepIndex = index + 1
                        val isCurrent = wizardState.currentStep == stepIndex
                        val isDone = wizardState.currentStep > stepIndex

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { wizardState = wizardState.copy(currentStep = stepIndex) }
                                .testTag("step_indicator_$stepIndex"),
                            shape = RoundedCornerShape(20.dp),
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isDone -> Color(0xFF2E7D32)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(Color.White.copy(alpha = 0.25f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    } else {
                                        Text(
                                            text = "$stepIndex",
                                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = title,
                                    color = if (isCurrent || isDone) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Step Content Scrollable Container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                when (wizardState.currentStep) {
                    1 -> Step1PropertyType(
                        selectedType = wizardState.propertyType,
                        onTypeSelected = { newType ->
                            wizardState = wizardState.copy(
                                propertyType = newType,
                                rooms = PropertyWizardState.defaultRoomsFor(newType)
                            )
                        }
                    )
                    2 -> Step2BasicInfo(
                        state = wizardState,
                        onUpdateState = { wizardState = it }
                    )
                    3 -> Step3PropertyDetails(
                        state = wizardState,
                        onUpdateState = { wizardState = it }
                    )
                    4 -> Step4RoomPricing(
                        state = wizardState,
                        onUpdateState = { wizardState = it }
                    )
                    5 -> Step5Amenities(
                        state = wizardState,
                        onUpdateState = { wizardState = it }
                    )
                    6 -> Step6FoodServices(
                        state = wizardState,
                        onUpdateState = { wizardState = it }
                    )
                    7 -> Step7CategoryFields(
                        state = wizardState,
                        onUpdateState = { wizardState = it }
                    )
                    8 -> Step8CustomerPreview(
                        state = wizardState,
                        ownerName = currentUserName,
                        ownerPhone = currentUserPhone,
                        onEditStep = { step -> wizardState = wizardState.copy(currentStep = step) }
                    )
                    9 -> Step9PublishAndDrafts(
                        state = wizardState,
                        savedDrafts = savedDrafts,
                        onSaveDraft = {
                            val draftEntity = wizardState.toDraftEntity()
                            viewModel.saveListingDraft(draftEntity) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Listing draft saved to database!")
                                }
                            }
                        },
                        onPublish = {
                            val prop = wizardState.toProperty(currentUserName, currentUserPhone)
                            publishedPropertyName = prop.name
                            viewModel.publishOwnerProperty(
                                property = prop,
                                draftIdToDelete = wizardState.draftId
                            ) {
                                showPublishSuccessDialog = true
                            }
                        },
                        onDelete = {
                            if (wizardState.editingOriginalPropertyId != null) {
                                viewModel.deleteOwnerProperty(wizardState.editingOriginalPropertyId!!)
                            } else {
                                viewModel.deleteListingDraft(wizardState.draftId)
                            }
                            viewModel.clearActiveWizardContext()
                            viewModel.navigateTo(Screen.OWNER_DASHBOARD)
                        },
                        onEditStep = { step -> wizardState = wizardState.copy(currentStep = step) },
                        onResumeDraft = { draft ->
                            wizardState = PropertyWizardState.fromDraftEntity(draft)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Resumed draft: ${draft.propertyName.ifBlank { "Untitled" }}")
                            }
                        },
                        onDeleteDraftById = { draftId ->
                            viewModel.deleteListingDraft(draftId) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Draft deleted successfully")
                                }
                            }
                        }
                    )
                    10 -> Step10PaymentSetup(
                        state = wizardState,
                        onUpdateState = { wizardState = it },
                        onSaveAndComplete = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Payment setup verified & saved!")
                            }
                        },
                        onPublishLive = {
                            val prop = wizardState.toProperty(currentUserName, currentUserPhone)
                            publishedPropertyName = prop.name
                            viewModel.publishOwnerProperty(
                                property = prop,
                                draftIdToDelete = wizardState.draftId
                            ) {
                                showPublishSuccessDialog = true
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Published Celebration Dialog
    if (showPublishSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showPublishSuccessDialog = false
                viewModel.clearActiveWizardContext()
                viewModel.navigateTo(Screen.OWNER_DASHBOARD)
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF2E7D32), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            },
            title = {
                Text("Listing Published Live!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Congratulations! '$publishedPropertyName' is now live and indexed on Styno.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Students, travelers, and executives in ${wizardState.city} can now discover, view photos, verify amenities, and reserve your property.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPublishSuccessDialog = false
                        viewModel.clearActiveWizardContext()
                        viewModel.navigateTo(Screen.OWNER_DASHBOARD)
                    },
                    modifier = Modifier.testTag("btn_go_to_dashboard")
                ) {
                    Text("Go to Owner Dashboard")
                }
            }
        )
    }
}

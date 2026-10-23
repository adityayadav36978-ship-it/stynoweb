package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GirlsSafetyVerificationStatus
import com.example.data.model.Property
import com.example.data.model.SafetyConcernReport
import com.example.data.model.SafetyReportStatus
import com.example.data.model.VerificationStatus
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel

import com.example.data.security.StynoSecurityEngine
import com.example.ui.viewmodel.UserRole

@Composable
fun AdminDashboardScreen(
    viewModel: StynoViewModel,
    modifier: Modifier = Modifier
) {
    val currentUserEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val currentUserRole by viewModel.userRole.collectAsStateWithLifecycle()

    val isAuthorizedAdmin = currentUserRole == UserRole.ADMIN &&
        StynoSecurityEngine.enforceAdminAccess(currentUserEmail)

    if (!isAuthorizedAdmin) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_access_denied_card")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Access Denied",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "Access Denied",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Administrator privileges are required to access this control center. Your attempt has been logged for security audit purposes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Button(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_return_home_button")
                    ) {
                        Text("Return to Safety")
                    }
                }
            }
        }
        return
    }

    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val complaints by viewModel.complaints.collectAsStateWithLifecycle()
    val safetyReports by viewModel.safetyReports.collectAsStateWithLifecycle()

    // Dialog state for Girls Safety Reject
    var rejectingProperty by remember { mutableStateOf<Property?>(null) }
    var rejectReason by remember { mutableStateOf("") }

    // Dialog state for Girls Safety Suspend
    var suspendingProperty by remember { mutableStateOf<Property?>(null) }
    var suspendReason by remember { mutableStateOf("") }

    // Dialog state for Safety Concern Report Action
    var activeReportToResolve by remember { mutableStateOf<SafetyConcernReport?>(null) }
    var reportActionNotes by remember { mutableStateOf("") }
    var reportActionTaken by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "STYNO Trust & Platform Admin",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Girls safety audit, verified badges, reports & compliance control",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminMetricCard("Total Stays", "${allProperties.size}", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                        AdminMetricCard("Bookings", "${bookings.size}", StynoEmerald, Modifier.weight(1f))
                        AdminMetricCard("Complaints", "${complaints.size}", StynoAccent, Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val pendingGirlsSafety = allProperties.count { it.girlsSafetyVerification.status == GirlsSafetyVerificationStatus.PENDING_REVIEW }
                        val verifiedGirlsSafety = allProperties.count { it.girlsSafetyVerification.status == GirlsSafetyVerificationStatus.VERIFIED }
                        val openReports = safetyReports.count { it.status == SafetyReportStatus.OPEN }

                        AdminMetricCard("Safety Audits Pending", "$pendingGirlsSafety", Color(0xFFD97706), Modifier.weight(1f))
                        AdminMetricCard("Girls Safety Verified", "$verifiedGirlsSafety", Color(0xFFBE123C), Modifier.weight(1f))
                        AdminMetricCard("Safety Reports", "$openReports Open", Color(0xFFDC2626), Modifier.weight(1f))
                    }
                }
            }

            // =========================================================================
            // 1. REPORTED SAFETY CONCERNS AUDIT DESK (HIGH PRIORITY)
            // =========================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.ReportProblem, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                        Text(
                            text = "Reported Safety Concerns (${safetyReports.count { it.status == SafetyReportStatus.OPEN }} Open)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (safetyReports.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No safety concerns reported. All verified stays complying with safety guidelines.",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(safetyReports) { report ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(
                            1.dp,
                            when (report.status) {
                                SafetyReportStatus.OPEN -> Color(0xFFEF4444).copy(alpha = 0.6f)
                                SafetyReportStatus.UNDER_INVESTIGATION -> Color(0xFFF59E0B).copy(alpha = 0.6f)
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${report.issueCategory} • ${report.propertyName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Reported: ${report.reportedDateFormatted} by ${report.reportedByUserName} (${report.reportedByUserPhone})",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (report.status) {
                                        SafetyReportStatus.OPEN -> Color(0xFFFEE2E2)
                                        SafetyReportStatus.UNDER_INVESTIGATION -> Color(0xFFFEF3C7)
                                        SafetyReportStatus.ACTION_TAKEN -> Color(0xFFE0E7FF)
                                        SafetyReportStatus.DISMISSED -> Color(0xFFF1F5F9)
                                    }
                                ) {
                                    Text(
                                        text = report.status.displayName,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (report.status) {
                                            SafetyReportStatus.OPEN -> Color(0xFFDC2626)
                                            SafetyReportStatus.UNDER_INVESTIGATION -> Color(0xFF92400E)
                                            SafetyReportStatus.ACTION_TAKEN -> Color(0xFF3730A3)
                                            SafetyReportStatus.DISMISSED -> Color(0xFF475569)
                                        }
                                    )
                                }
                            }

                            Text(
                                text = report.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (report.adminNotes.isNotBlank()) {
                                Text(
                                    text = "Audit Notes: ${report.adminNotes}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (report.actionTaken.isNotBlank()) {
                                Text(
                                    text = "Action: ${report.actionTaken}",
                                    fontSize = 11.sp,
                                    color = StynoEmerald,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Fast suspend button for safety report
                                val targetProp = allProperties.find { it.id == report.propertyId }
                                if (targetProp?.girlsSafetyVerification?.status == GirlsSafetyVerificationStatus.VERIFIED) {
                                    Button(
                                        onClick = {
                                            suspendingProperty = targetProp
                                            suspendReason = "Suspended due to safety report #${report.id.take(6)}: ${report.issueCategory}"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Suspend Safety Badge", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            activeReportToResolve = report
                                            reportActionNotes = report.adminNotes
                                            reportActionTaken = report.actionTaken
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Resolve / Update", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 2. GIRLS SAFETY VERIFICATION DESK (CORE FEATURE REQUIREMENT)
            // =========================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFFBE123C), modifier = Modifier.size(20.dp))
                        Text(
                            text = "Girls Safety Verification Desk",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Text(
                    text = "Strict STYNO rule: Only authorized admins can approve, reject, or suspend the Girls Safety badge.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val safetyAuditList = allProperties.filter {
                it.girlsSafetyVerification.status != GirlsSafetyVerificationStatus.NOT_SUBMITTED ||
                it.genderSuitability == com.example.data.model.GenderSuitability.GIRLS_ONLY
            }

            if (safetyAuditList.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No listings currently submitted for Girls Safety audit.",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(safetyAuditList) { property ->
                    val safety = property.girlsSafetyVerification
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (safety.status) {
                                GirlsSafetyVerificationStatus.VERIFIED -> Color(0xFFFDF2F8)
                                GirlsSafetyVerificationStatus.PENDING_REVIEW -> Color(0xFFFFFBEB)
                                GirlsSafetyVerificationStatus.SUSPENDED -> Color(0xFFFFF1F2)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = BorderStroke(
                            1.dp,
                            when (safety.status) {
                                GirlsSafetyVerificationStatus.VERIFIED -> Color(0xFFF472B6)
                                GirlsSafetyVerificationStatus.PENDING_REVIEW -> Color(0xFFFCD34D)
                                GirlsSafetyVerificationStatus.SUSPENDED -> Color(0xFFFDA4AF)
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Title & Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = property.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${property.area}, ${property.city} • Host: ${property.ownerInfo.name} (${property.ownerInfo.phone})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (safety.status) {
                                        GirlsSafetyVerificationStatus.VERIFIED -> Color(0xFFBE123C)
                                        GirlsSafetyVerificationStatus.PENDING_REVIEW -> Color(0xFFD97706)
                                        GirlsSafetyVerificationStatus.SUSPENDED -> Color(0xFFDC2626)
                                        GirlsSafetyVerificationStatus.REJECTED -> Color(0xFF64748B)
                                        GirlsSafetyVerificationStatus.NOT_SUBMITTED -> Color(0xFF94A3B8)
                                    }
                                ) {
                                    Text(
                                        text = safety.status.badgeLabel.uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Submitted Safety Protocols Summary
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Audited Safety Protocols:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "• Female Warden: ${if (safety.femaleWardenPresent) "Yes (${safety.femaleWardenName.ifBlank { "Assigned" }})" else "No"}",
                                            fontSize = 11.sp,
                                            color = if (safety.femaleWardenPresent) Color(0xFF047857) else Color(0xFFDC2626)
                                        )
                                        Text(
                                            text = "• CCTV: ${if (safety.cctvCoverageCommonAreas) "Active 24/7" else "No"}",
                                            fontSize = 11.sp,
                                            color = if (safety.cctvCoverageCommonAreas) Color(0xFF047857) else Color(0xFFDC2626)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "• Gate Entry: ${if (safety.biometricOrSmartLock) "Biometric / RFID" else "Standard Key"}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "• Curfew: ${safety.curfewOrGateLockTime}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "• Police Verification: ${if (safety.policeVerificationCompleted) "Verified ✓" else "Pending"}",
                                            fontSize = 11.sp,
                                            color = if (safety.policeVerificationCompleted) Color(0xFF047857) else Color(0xFFD97706)
                                        )
                                        Text(
                                            text = "• Visitor Register: ${if (safety.visitorLogMaintained) "Enforced ✓" else "Not logged"}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (safety.safetyDocumentsUploaded.isNotEmpty()) {
                                        Text(
                                            text = "• Documents Attached: ${safety.safetyDocumentsUploaded.joinToString(", ")}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (safety.auditCertificateNumber != null) {
                                        Text(
                                            text = "• Official Cert #: ${safety.auditCertificateNumber} (Audited: ${safety.verificationDateFormatted ?: "Recently"})",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFBE123C)
                                        )
                                    }
                                    if (safety.suspensionReason != null) {
                                        Text(
                                            text = "• Suspension Reason: ${safety.suspensionReason}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDC2626)
                                        )
                                    }
                                    if (safety.rejectionReason != null) {
                                        Text(
                                            text = "• Rejection Reason: ${safety.rejectionReason}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDC2626)
                                        )
                                    }
                                }
                            }

                            // Admin Actions Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (safety.status) {
                                    GirlsSafetyVerificationStatus.VERIFIED -> {
                                        Button(
                                            onClick = {
                                                suspendingProperty = property
                                                suspendReason = ""
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(imageVector = Icons.Default.Block, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Suspend Badge", fontSize = 11.sp)
                                        }
                                    }
                                    GirlsSafetyVerificationStatus.PENDING_REVIEW -> {
                                        Button(
                                            onClick = {
                                                viewModel.adminApproveGirlsSafety(
                                                    propertyId = property.id,
                                                    notes = "Physical on-site audit & document verification approved by STYNO Trust Team."
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Approve Girls Safety", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                rejectingProperty = property
                                                rejectReason = ""
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reject", fontSize = 11.sp)
                                        }
                                    }
                                    GirlsSafetyVerificationStatus.SUSPENDED, GirlsSafetyVerificationStatus.REJECTED -> {
                                        Button(
                                            onClick = {
                                                viewModel.adminApproveGirlsSafety(
                                                    propertyId = property.id,
                                                    notes = "Re-audit completed. Compliance verified and badge restored."
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Re-Instate Badge", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    else -> {
                                        Button(
                                            onClick = {
                                                viewModel.adminApproveGirlsSafety(
                                                    propertyId = property.id,
                                                    notes = "Direct admin verification grant after manual audit."
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Grant Badge", fontSize = 11.sp)
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = { viewModel.openPropertyDetails(property) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Audit Site", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 3. RESIDENT COMPLAINTS DESK
            // =========================================================================
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Instant Action Resident Complaints (${complaints.count { it.status.name != "RESOLVED" }} Active)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(complaints) { complaint ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        width = 1.dp,
                        color = when (complaint.priority) {
                            com.example.data.model.ComplaintPriority.INSTANT_ACTION -> Color(0xFFEF4444).copy(alpha = 0.5f)
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${complaint.category.displayName} • ${complaint.propertyName ?: "General"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (complaint.status) {
                                    com.example.data.model.ComplaintStatus.RESOLVED -> StynoEmerald.copy(alpha = 0.15f)
                                    com.example.data.model.ComplaintStatus.IN_PROGRESS -> StynoBluePrimary.copy(alpha = 0.15f)
                                    com.example.data.model.ComplaintStatus.ASSIGNED -> Color(0xFFFEF3C7)
                                    com.example.data.model.ComplaintStatus.SUBMITTED -> Color(0xFFFEE2E2)
                                    else -> Color(0xFFF1F5F9)
                                }
                            ) {
                                Text(
                                    text = complaint.status.displayName,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (complaint.status) {
                                        com.example.data.model.ComplaintStatus.RESOLVED -> StynoEmerald
                                        com.example.data.model.ComplaintStatus.IN_PROGRESS -> StynoBluePrimary
                                        com.example.data.model.ComplaintStatus.ASSIGNED -> Color(0xFF92400E)
                                        com.example.data.model.ComplaintStatus.SUBMITTED -> Color(0xFFDC2626)
                                        else -> Color(0xFF475569)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = complaint.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = complaint.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resident: ${complaint.userName} (${complaint.userPhone})",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = { viewModel.openComplaintDetail(complaint) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Update Status", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 4. GENERAL PROPERTY VERIFICATION AUDIT DESK
            // =========================================================================
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "General Property Verification & Audit Desk",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(allProperties) { property ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = property.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Host: ${property.ownerInfo.name} • ${property.city}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (property.verificationStatus) {
                                    VerificationStatus.VERIFIED -> StynoEmerald.copy(alpha = 0.15f)
                                    VerificationStatus.UNDER_REVIEW -> Color(0xFFFEF3C7)
                                    VerificationStatus.PENDING -> Color(0xFFE2E8F0)
                                    VerificationStatus.REJECTED -> Color(0xFFFEE2E2)
                                }
                            ) {
                                Text(
                                    text = property.verificationStatus.displayName,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (property.verificationStatus) {
                                        VerificationStatus.VERIFIED -> StynoEmerald
                                        VerificationStatus.UNDER_REVIEW -> Color(0xFF92400E)
                                        VerificationStatus.PENDING -> Color(0xFF475569)
                                        VerificationStatus.REJECTED -> Color(0xFFDC2626)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Amenities: ${property.shortFacilities.take(3).joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (property.verificationStatus != VerificationStatus.VERIFIED) {
                                    Button(
                                        onClick = { viewModel.updateVerificationStatus(property.id, VerificationStatus.VERIFIED) },
                                        colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Approve", fontSize = 11.sp)
                                    }
                                }

                                OutlinedButton(
                                    onClick = { viewModel.openPropertyDetails(property) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Audit Site", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Reject Girls Safety Verification
    rejectingProperty?.let { prop ->
        AlertDialog(
            onDismissRequest = { rejectingProperty = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reject Girls Safety Application", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Property: ${prop.name}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("Specify the exact audit requirement or safety violation causing this rejection:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("Rejection Reason *") },
                        placeholder = { Text("e.g. CCTV dead zones found in rear gate, warden contact invalid") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val reason = rejectReason.ifBlank { "Audit requirements not fulfilled according to STYNO Safety Guidelines." }
                        viewModel.adminRejectGirlsSafety(prop.id, reason)
                        rejectingProperty = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Confirm Rejection")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectingProperty = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Suspend Girls Safety Badge
    suspendingProperty?.let { prop ->
        AlertDialog(
            onDismissRequest = { suspendingProperty = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Suspend Girls Safety Badge", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Property: ${prop.name}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(
                        "Suspending will immediately remove the 'Girls Safety Verified' badge from all search results and user views.",
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626)
                    )
                    OutlinedTextField(
                        value = suspendReason,
                        onValueChange = { suspendReason = it },
                        label = { Text("Suspension Reason *") },
                        placeholder = { Text("e.g. Reported safety concern under investigation, warden absent without substitute") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val reason = suspendReason.ifBlank { "Safety badge suspended pending comprehensive compliance review." }
                        viewModel.adminSuspendGirlsSafety(prop.id, reason)
                        suspendingProperty = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Confirm Suspension")
                }
            },
            dismissButton = {
                TextButton(onClick = { suspendingProperty = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Update Safety Concern Report
    activeReportToResolve?.let { report ->
        AlertDialog(
            onDismissRequest = { activeReportToResolve = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Review Safety Concern #${report.id.take(6)}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Property: ${report.propertyName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Issue: ${report.issueCategory}", color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("Description: ${report.description}", fontSize = 12.sp)

                    OutlinedTextField(
                        value = reportActionNotes,
                        onValueChange = { reportActionNotes = it },
                        label = { Text("Auditor Investigation Notes") },
                        placeholder = { Text("e.g. Spoke with warden Sunita, gate register checked") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = reportActionTaken,
                        onValueChange = { reportActionTaken = it },
                        label = { Text("Action Taken / Resolution") },
                        placeholder = { Text("e.g. Biometric sensor repaired, extra night guard deployed") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            viewModel.adminUpdateSafetyReport(
                                reportId = report.id,
                                status = SafetyReportStatus.UNDER_INVESTIGATION,
                                adminNotes = reportActionNotes.ifBlank { "Under on-site safety audit" },
                                actionTaken = reportActionTaken
                            )
                            activeReportToResolve = null
                        }
                    ) {
                        Text("Investigating", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.adminUpdateSafetyReport(
                                reportId = report.id,
                                status = SafetyReportStatus.ACTION_TAKEN,
                                adminNotes = reportActionNotes.ifBlank { "Audit verified" },
                                actionTaken = reportActionTaken.ifBlank { "Corrective safety measures enforced" }
                            )
                            activeReportToResolve = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StynoEmerald)
                    ) {
                        Text("Action Taken ✓", fontSize = 11.sp)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { activeReportToResolve = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = color))
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GenderSuitability
import com.example.data.model.PropertyType
import com.example.ui.theme.StynoAmber
import com.example.ui.theme.StynoEmergencyRed
import com.example.ui.theme.StynoGreen
import com.example.ui.theme.StynoIndigo
import com.example.ui.theme.StynoNavyBlue
import com.example.ui.theme.StynoTeal
import com.example.ui.viewmodel.AiAssistantMessage
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StynoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSupportAssistantSheet(
    viewModel: StynoViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val messages by androidx.compose.runtime.rememberUpdatedState(viewModel.aiMessages)
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "🔑 How does Digital Passcode check-in work?",
        "🛡️ What safety features exist in Girls Hostels?",
        "⚡ Book a Quick Stay Transit Pod",
        "🚖 How does Station/Airport pickup work?",
        "💰 How are security deposits refunded?",
        "🚨 I have an urgent room maintenance issue"
    )

    LaunchedEffect(messages.value.size) {
        if (messages.value.isNotEmpty()) {
            listState.animateScrollToItem(messages.value.size - 1)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier
            .fillMaxHeight(0.85f)
            .testTag("ai_support_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Header
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(StynoIndigo, StynoTeal))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "STYNO AI Concierge",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StynoGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "24/7 Live",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StynoGreen
                                    )
                                }
                            }
                            Text(
                                text = "Instant answers on bookings, safety & resident requests",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            HorizontalDivider()

            // Quick suggestions chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
            ) {
                items(quickQuestions) { question ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.sendAiSupportQuery(question)
                            },
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = question,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Conversation history
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
            ) {
                items(messages.value) { msg ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                bottomEnd = if (msg.isUser) 4.dp else 16.dp
                            ),
                            color = if (msg.isUser) StynoIndigo else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth(if (msg.isUser) 0.85f else 0.92f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                if (!msg.isUser) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = StynoIndigo,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "STYNO Assistant",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StynoIndigo
                                        )
                                    }
                                }

                                Text(
                                    text = msg.text,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Action Buttons inside Bot reply
                                if (msg.actionType != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    when (msg.actionType) {
                                        "COMPLAINT" -> {
                                            Button(
                                                onClick = {
                                                    onDismiss()
                                                    viewModel.openComplaintDialog(null)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = StynoEmergencyRed),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Open Instant Action Complaint", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        "SEARCH_GIRLS_HOSTEL" -> {
                                            Button(
                                                onClick = {
                                                    onDismiss()
                                                    viewModel.selectCategory(PropertyType.HOSTEL)
                                                    viewModel.selectGenderSuitability(GenderSuitability.GIRLS_ONLY)
                                                    viewModel.navigateTo(Screen.SEARCH)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = StynoIndigo),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("View Verified Girls Hostels", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        "SEARCH_QUICK_STAY" -> {
                                            Button(
                                                onClick = {
                                                    onDismiss()
                                                    viewModel.selectCategory(PropertyType.QUICK_STAY)
                                                    viewModel.navigateTo(Screen.SEARCH)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = StynoTeal),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Explore Transit Pods & Hourly Stays", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.timeString,
                                    fontSize = 10.sp,
                                    color = if (msg.isUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask STYNO AI anything...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_support_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendAiSupportQuery(inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (inputText.isNotBlank()) StynoIndigo else Color.LightGray)
                            .testTag("ai_support_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.DurationType
import com.example.data.model.Property
import com.example.data.model.StynoAiAction
import com.example.data.model.StynoAiActionType
import com.example.data.model.StynoAiMessage
import com.example.data.model.StynoAiRole
import com.example.ui.theme.StynoAccent
import com.example.ui.theme.StynoBluePrimary
import com.example.ui.theme.StynoEmerald
import com.example.ui.viewmodel.StynoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StynoConversationalAiSheet(
    viewModel: StynoViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val messages by viewModel.conversationalAiMessages.collectAsStateWithLifecycle()
    val role by viewModel.conversationalAiRole.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }

    // Voice recognition via existing robust voice engine
    val voiceState = rememberNayvonVoiceInput { spokenText ->
        inputText = spokenText
        viewModel.sendConversationalAiInput(spokenText)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    var isTtsReady by remember { mutableStateOf(false) }
    var isAutoSpeechEnabled by remember { mutableStateOf(true) }
    var isCurrentlySpeaking by remember { mutableStateOf(false) }

    val tts = remember {
        val ttsHolder = arrayOfNulls<TextToSpeech>(1)
        val listener = TextToSpeech.OnInitListener { status ->
            val engine = ttsHolder[0]
            if (status == TextToSpeech.SUCCESS && engine != null) {
                try {
                    // Task 3: Cute, soft, natural vocal persona configuration
                    engine.setPitch(1.20f) // Slightly higher, cute, soft, warm pitch
                    engine.setSpeechRate(0.96f) // Gentle, calm, conversational pacing

                    // Select high quality natural/soft female voice if available on system
                    val voices = try { engine.voices } catch (_: Throwable) { null }
                    if (!voices.isNullOrEmpty()) {
                        val softCuteVoice = voices.firstOrNull { v ->
                            val vName = v.name.lowercase(Locale.ROOT)
                            !v.isNetworkConnectionRequired &&
                                (vName.contains("female") || vName.contains("soft") ||
                                 vName.contains("en-in-x-c") || vName.contains("en-in-x-d") ||
                                 vName.contains("hi-in-x-c") || vName.contains("hi-in-x-d") ||
                                 vName.contains("en-us-x-sfg") || vName.contains("natural"))
                        } ?: voices.firstOrNull { v -> !v.isNetworkConnectionRequired && v.locale.language in listOf("en", "hi") }
                        if (softCuteVoice != null) {
                            engine.voice = softCuteVoice
                        }
                    } else {
                        engine.language = Locale.ENGLISH
                    }
                } catch (_: Throwable) {
                    engine.language = Locale.ENGLISH
                }
                isTtsReady = true
            }
        }
        val created = TextToSpeech(context, listener)
        ttsHolder[0] = created
        created
    }

    LaunchedEffect(tts, isTtsReady) {
        if (isTtsReady && tts != null) {
            tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isCurrentlySpeaking = true
                }
                override fun onDone(utteranceId: String?) {
                    isCurrentlySpeaking = false
                }
                override fun onError(utteranceId: String?) {
                    isCurrentlySpeaking = false
                }
            })
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (_: Throwable) {}
        isCurrentlySpeaking = false
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            try {
                tts?.stop()
                tts?.shutdown()
            } catch (_: Throwable) {}
        }
    }

    fun speakText(text: String) {
        if (isTtsReady && text.isNotBlank() && isAutoSpeechEnabled) {
            try {
                tts?.stop()
                // Strip emoji glyphs and markdown formatting before feeding to TTS for crystal-clear natural speech
                val cleanForSpeech = text
                    .replace(Regex("[*#_~`\\[\\](){}]"), " ")
                    .replace(Regex("[\\p{So}\\p{Cn}]"), "")
                    .replace(Regex("\\s+"), " ")
                    .trim()
                if (cleanForSpeech.isNotBlank()) {
                    tts?.speak(cleanForSpeech, TextToSpeech.QUEUE_FLUSH, null, "styno_ai_tts_${System.currentTimeMillis()}")
                }
            } catch (_: Throwable) {}
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
            val lastMsg = messages.last()
            if (!lastMsg.isUser && isAutoSpeechEnabled) {
                speakText(lastMsg.text)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = modifier
            .fillMaxHeight(0.92f)
            .testTag("styno_conversational_ai_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Header Bar
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
                        Surface(
                            shape = CircleShape,
                            color = StynoBluePrimary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Assistant",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Styno AI Assistant",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (role == StynoAiRole.RESIDENT) StynoBluePrimary.copy(alpha = 0.15f) else StynoEmerald.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (role == StynoAiRole.RESIDENT) "Resident Guide" else "Host Partner",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (role == StynoAiRole.RESIDENT) StynoBluePrimary else StynoEmerald,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "English, Hindi & Hinglish • 0% Brokerage Stays",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                isAutoSpeechEnabled = !isAutoSpeechEnabled
                                if (!isAutoSpeechEnabled) {
                                    tts?.stop()
                                }
                            },
                            modifier = Modifier.testTag("btn_ai_voice_toggle")
                        ) {
                            Icon(
                                imageVector = if (isAutoSpeechEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = if (isAutoSpeechEnabled) "Voice Speech Enabled" else "Voice Speech Muted",
                                tint = if (isAutoSpeechEnabled) StynoBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { viewModel.resetConversationalAi() },
                            modifier = Modifier.testTag("btn_ai_reset")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Conversation",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("btn_ai_close")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Message Feed
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    if (message.isUser) {
                        UserMessageBubble(message)
                    } else {
                        AssistantMessageBubble(
                            message = message,
                            onQuickReplyClicked = { reply ->
                                viewModel.sendConversationalAiInput(reply)
                            },
                            onActionClicked = { action ->
                                viewModel.executeConversationalAiAction(action)
                            },
                            onPropertySelected = { prop ->
                                viewModel.executeConversationalAiAction(
                                    StynoAiAction(
                                        type = StynoAiActionType.SELECT_PROPERTY,
                                        title = "View Property",
                                        property = prop
                                    )
                                )
                            },
                            onSpeakMessage = { text ->
                                speakText(text)
                            }
                        )
                    }
                }
            }

            // Voice Listening Indicator Banner
            if (voiceState.isListening) {
                VoiceListeningBanner(
                    rmsLevel = voiceState.rmsLevel,
                    onStop = voiceState.stopListening
                )
            }

            // Voice Speaking Indicator with Stop Control
            if (isCurrentlySpeaking) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Speaking",
                                tint = StynoBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Speaking in natural soft voice...",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        TextButton(
                            onClick = { stopSpeaking() },
                            modifier = Modifier.testTag("btn_stop_speaking")
                        ) {
                            Text(
                                text = "Stop",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Input Control Footer
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice Mic Button
                    IconButton(
                        onClick = {
                            if (voiceState.isListening) {
                                voiceState.stopListening()
                            } else {
                                voiceState.startListening()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_ai_voice_input")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (voiceState.isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (voiceState.isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = if (voiceState.isListening) "Stop voice" else "Voice input",
                                    tint = if (voiceState.isListening) Color.White else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Text Input Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_ai_message"),
                        placeholder = {
                            Text(
                                text = if (role == StynoAiRole.RESIDENT) "Poochiye: 'Bagaha mein PG', 'Under ₹5000'..." else "Poochiye: 'Photo upload', 'Rent set karein'...",
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        maxLines = 3,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StynoBluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val textToSend = inputText
                                inputText = ""
                                viewModel.sendConversationalAiInput(textToSend)
                            }
                        },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_ai_send")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (inputText.isNotBlank()) StynoBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserMessageBubble(message: StynoAiMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp).copy(bottomEnd = androidx.compose.foundation.shape.CornerSize(2.dp)),
            color = StynoBluePrimary,
            modifier = Modifier.padding(start = 48.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
        Text(
            text = formatTime(message.timestamp),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, end = 4.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AssistantMessageBubble(
    message: StynoAiMessage,
    onQuickReplyClicked: (String) -> Unit,
    onActionClicked: (StynoAiAction) -> Unit,
    onPropertySelected: (Property) -> Unit,
    onSpeakMessage: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Surface(
                shape = CircleShape,
                color = StynoBluePrimary.copy(alpha = 0.12f),
                modifier = Modifier
                    .size(28.dp)
                    .padding(top = 2.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = StynoBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(16.dp).copy(bottomStart = androidx.compose.foundation.shape.CornerSize(2.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )

                        // Action button if present
                        message.action?.let { action ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onActionClicked(action) },
                                colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("ai_action_btn_${action.type.name}")
                            ) {
                                Text(text = action.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Matched genuine property cards
                if (message.matchedProperties.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        message.matchedProperties.forEach { property ->
                            GenuinePropertyAiCard(
                                property = property,
                                onCardClick = { onPropertySelected(property) }
                            )
                        }
                    }
                }

                // Quick reply pills
                if (message.quickReplies.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        message.quickReplies.forEach { reply ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StynoBluePrimary.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { onQuickReplyClicked(reply) }
                                    .testTag("quick_reply_${reply.take(10)}")
                            ) {
                                Text(
                                    text = reply,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StynoBluePrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(message.timestamp),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = { onSpeakMessage(message.text) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak response aloud",
                            tint = StynoBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenuinePropertyAiCard(
    property: Property,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCardClick() }
            .testTag("ai_property_card_${property.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo thumbnail
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                PropertyPhotoItem(
                    imageName = property.imageDrawableNames.firstOrNull() ?: "img_hostel_modern",
                    propertyName = property.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = property.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StynoAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", property.rating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "${property.area}, ${property.city}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val durationLabel = when (property.durationType) {
                            DurationType.DAILY -> "/day"
                            DurationType.HOURLY -> "/slot"
                            else -> "/month"
                        }
                        Text(
                            text = "₹${property.startingPrice.toInt()}$durationLabel",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StynoBluePrimary
                        )
                        if (property.isShared) {
                            Text(
                                text = "${property.sharingCapacity}-Sharing • ${property.availableSpaces} spaces left",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        } else {
                            Text(
                                text = "Private Unit",
                                fontSize = 10.sp,
                                color = StynoEmerald
                            )
                        }
                    }

                    Button(
                        onClick = onCardClick,
                        colors = ButtonDefaults.buttonColors(containerColor = StynoBluePrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(text = "View & Book", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceListeningBanner(
    rmsLevel: Float,
    onStop: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(14.dp)
                        .scale(pulseScale)
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Listening... Speak in Hindi or English",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            TextButton(onClick = onStop) {
                Text(text = "Done", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(millis))
}

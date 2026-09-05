package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.ModelConfigEntity
import com.example.ui.DubbingViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.AbyssDark
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeBorder
import com.example.ui.theme.BlazeOrangeDim
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanBright
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBright
import com.example.ui.theme.SurfaceContainerDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.TextOnGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: DubbingViewModel,
    modifier: Modifier = Modifier
) {
    val currentConfig by viewModel.modelConfig.collectAsState()
    val testStatus by viewModel.connectionStatus.collectAsState()

    var sttModel by remember(currentConfig) { mutableStateOf(currentConfig.sttModel) }
    var translationModel by remember(currentConfig) { mutableStateOf(currentConfig.translationModel) }
    var ttsModel by remember(currentConfig) { mutableStateOf(currentConfig.ttsModel) }
    var serverUrl by remember(currentConfig) { mutableStateOf(currentConfig.modelServerUrl) }
    var ffmpegPath by remember(currentConfig) { mutableStateOf(currentConfig.ffmpegPath) }
    var chunkDuration by remember(currentConfig) { mutableIntStateOf(currentConfig.maxChunkDurationSec) }
    var parallelJobs by remember(currentConfig) { mutableIntStateOf(currentConfig.parallelWorkers) }
    var isOpenSourceMode by remember(currentConfig) { mutableStateOf(currentConfig.isOpenSourceMode) }
    var autoDiarization by remember(currentConfig) { mutableStateOf(currentConfig.autoDiarization) }

    var saveConfirmation by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = BlazeOrange)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "AI Model & Engine Configuration",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Configure self-hosted open-source inference servers and FFmpeg parameters",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Open Source Mode Banner
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Local / Open Source AI Mode",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Zero paid API credits required. Runs directly on your hardware.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = isOpenSourceMode,
                            onCheckedChange = { isOpenSourceMode = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BlazeOrange,
                                checkedTrackColor = BlazeOrangeDim
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceSubtle)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "“AI models are open-source/free to use, but running long movies still requires computing power, storage and bandwidth. Designed for free/open-source AI processing. Actual limits depend on your hosting hardware.”",
                            fontSize = 11.sp,
                            color = BlazeOrange,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // AI Provider Abstraction Endpoints (Requirement 18 & 19)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dns, contentDescription = null, tint = BlazeOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Self-Hosted Model Server URL",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Connects to your local or private vLLM, faster-whisper, or IndicTTS server",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        label = { Text("Inference Endpoint URL") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("model_server_url_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerDark,
                            unfocusedContainerColor = SurfaceContainerDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedIndicatorColor = BlazeOrange
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.testServerConnection(serverUrl) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceElevatedDark,
                                contentColor = BlazeOrange
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BlazeOrangeBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("test_connection_button")
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = BlazeOrange)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Connection", color = BlazeOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        if (testStatus != null) {
                            Text(
                                text = testStatus!!,
                                fontSize = 11.sp,
                                color = if (testStatus!!.startsWith("SUCCESS")) StatusSuccessBright else BlazeOrange,
                                modifier = Modifier.weight(1f),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Models Selection
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Model Architecture Mapping", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    // STT Model
                    Text("Speech-To-Text (Whisper Engine)", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    var expStt by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expStt, onExpandedChange = { expStt = !expStt }) {
                        OutlinedTextField(
                            value = sttModel,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expStt) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerDark,
                                unfocusedContainerColor = SurfaceContainerDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedIndicatorColor = BlazeOrange
                            )
                        )
                        ExposedDropdownMenu(expanded = expStt, onDismissRequest = { expStt = false }) {
                            listOf(
                                "Whisper Large-v3 (faster-whisper)",
                                "Whisper Medium (Self-Hosted)",
                                "Whisper Small (Low Resource)",
                                "OpenAI Whisper Local"
                            ).forEach { opt ->
                                DropdownMenuItem(text = { Text(opt) }, onClick = { sttModel = opt; expStt = false })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Translation Model
                    Text("Translation Engine (Telugu)", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    var expTrans by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expTrans, onExpandedChange = { expTrans = !expTrans }) {
                        OutlinedTextField(
                            value = translationModel,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expTrans) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerDark,
                                unfocusedContainerColor = SurfaceContainerDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedIndicatorColor = BlazeOrange
                            )
                        )
                        ExposedDropdownMenu(expanded = expTrans, onDismissRequest = { expTrans = false }) {
                            listOf(
                                "IndicTrans2 Multilingual Telugu Engine",
                                "NLLB-200-3.3B (Natural Telugu)",
                                "MarianMT English-to-Telugu",
                                "Colloquial Telugu Contextual Engine"
                            ).forEach { opt ->
                                DropdownMenuItem(text = { Text(opt) }, onClick = { translationModel = opt; expTrans = false })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // TTS Model
                    Text("Telugu Text-To-Speech (TTS Engine)", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    var expTts by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expTts, onExpandedChange = { expTts = !expTts }) {
                        OutlinedTextField(
                            value = ttsModel,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expTts) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerDark,
                                unfocusedContainerColor = SurfaceContainerDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedIndicatorColor = BlazeOrange
                            )
                        )
                        ExposedDropdownMenu(expanded = expTts, onDismissRequest = { expTts = false }) {
                            listOf(
                                "IndicTTS / MMS-Telugu VITS",
                                "Piper-TTS Telugu High Quality",
                                "Coqui VITS-Telugu Female/Male",
                                "Android Native Telugu Speech Synthesizer"
                            ).forEach { opt ->
                                DropdownMenuItem(text = { Text(opt) }, onClick = { ttsModel = opt; expTts = false })
                            }
                        }
                    }
                }
            }
        }

        // Long Video & Chunk Settings (Requirement 3 & 19)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Long Video Chunking & Performance", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Max Chunk Duration: ${chunkDuration}s (Splits 1h-3h movies safely)", fontSize = 12.sp, color = TextSecondary)
                    Slider(
                        value = chunkDuration.toFloat(),
                        onValueChange = { chunkDuration = it.toInt() },
                        valueRange = 15f..90f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = BlazeOrange,
                            activeTrackColor = BlazeOrange,
                            inactiveTrackColor = SurfaceElevatedDark
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Parallel Processing Jobs: $parallelJobs workers", fontSize = 12.sp, color = TextSecondary)
                    Slider(
                        value = parallelJobs.toFloat(),
                        onValueChange = { parallelJobs = it.toInt() },
                        valueRange = 1f..8f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = BlazeOrange,
                            activeTrackColor = BlazeOrange,
                            inactiveTrackColor = SurfaceElevatedDark
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.saveModelConfiguration(
                                currentConfig.copy(
                                    sttModel = sttModel,
                                    translationModel = translationModel,
                                    ttsModel = ttsModel,
                                    modelServerUrl = serverUrl,
                                    ffmpegPath = ffmpegPath,
                                    maxChunkDurationSec = chunkDuration,
                                    parallelWorkers = parallelJobs,
                                    isOpenSourceMode = isOpenSourceMode,
                                    autoDiarization = autoDiarization
                                )
                            )
                            saveConfirmation = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_model_settings_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlazeOrange,
                            contentColor = AbyssDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = AbyssDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (saveConfirmation) "Settings Saved! ✓" else "Save Engine Configuration", color = AbyssDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

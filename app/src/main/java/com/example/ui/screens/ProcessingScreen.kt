package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioChunkEntity
import com.example.data.model.ChunkStatus
import com.example.data.model.DubbingStatus
import com.example.ui.DubbingViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.PipelineStepItem
import com.example.ui.components.StatusBadge
import com.example.ui.components.StepState
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.AbyssDark
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeBorder
import com.example.ui.theme.BlazeOrangeDim
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardBorderHighlight
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaGoldDark
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanBright
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.RubyCrimson
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBright
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.SurfaceContainerDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.TextOnGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProcessingScreen(
    viewModel: DubbingViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.activeProject.collectAsState()
    val chunks by viewModel.activeChunks.collectAsState()
    val logs by viewModel.activeLogs.collectAsState()
    val isRunning by viewModel.isProcessing.collectAsState()

    var selectedFailedChunk by remember { mutableStateOf<AudioChunkEntity?>(null) }

    if (project == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AbyssDark)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No active dubbing project.\nStart dubbing from the 'Dub Movie' tab or select a sample.",
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        return
    }

    val currentProj = project!!
    val failedChunks = chunks.filter { it.status == ChunkStatus.FAILED.name }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Movie Video Canvas (matching Design HTML relative aspect-video)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
                    .border(1.dp, CardBorderHighlight, RoundedCornerShape(20.dp))
            ) {
                // Subtle waveform background / preview canvas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WaveformVisualizer(
                        isActive = isRunning,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(55.dp),
                        barColor = BlazeOrange
                    )
                }

                // Centered play/pulse action button (matching Design HTML w-14 h-14 rounded-full bg-[#FF5C00]/20)
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(BlazeOrangeDim)
                        .border(1.dp, BlazeOrangeBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isRunning) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = BlazeOrange,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Top Badge: Status / Live
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x99000000))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "NOW PROCESSING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlazeOrange,
                            letterSpacing = 1.sp
                        )
                    }

                    StatusBadge(status = currentProj.status)
                }

                // Bottom Gradient Overlay with movie title and timecode pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xDD000000), Color.Black)
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentProj.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = "${currentProj.sourceLanguage} → Telugu Dub",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }

                        // Timecode pill matching Design HTML
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x33FFFFFF))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = currentProj.durationFormatted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 2. Telugu Dubbing Dashboard Card (matching Design HTML bg-[#121214] rounded-[28px])
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header with % matching Design HTML
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Telugu Dubbing",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Text(
                                text = "Long movie pipeline active",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Text(
                            text = "${currentProj.overallProgress}%",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light,
                            color = BlazeOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { currentProj.overallProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = BlazeOrange,
                        trackColor = Slate800
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2 Grid Stat Cards (matching Design HTML CURRENT CHUNK & TIME LEFT)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val completedCount = chunks.count { it.status == ChunkStatus.COMPLETED.name }
                        val remainingChunks = (currentProj.totalChunks - completedCount).coerceAtLeast(0)
                        val estMinutesLeft = (remainingChunks * 2).coerceAtLeast(1)

                        // Card 1: CURRENT CHUNK
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceSubtle)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "CURRENT CHUNK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Chunk $completedCount / ${currentProj.totalChunks}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }

                        // Card 2: TIME LEFT
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceSubtle)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "TIME LEFT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextTertiary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (currentProj.overallProgress >= 100) "Completed" else "~ $estMinutesLeft mins",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = StatusSuccessBright
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Controls: Pause / Resume / Retry
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (isRunning) {
                            Button(
                                onClick = { viewModel.pauseDubbing() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pause_dubbing_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SurfaceElevatedDark,
                                    contentColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Pause, contentDescription = null, tint = BlazeOrange)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pause Job")
                            }
                        } else {
                            Button(
                                onClick = { viewModel.startDubbing() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("resume_dubbing_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BlazeOrange,
                                    contentColor = AbyssDark
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AbyssDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Resume Dubbing", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (failedChunks.isNotEmpty()) {
                            Button(
                                onClick = { viewModel.retryAllFailed() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StatusError.copy(alpha = 0.85f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry Failed (${failedChunks.size})")
                            }
                        }
                    }
                }
            }
        }

        // 3. Pipeline Stepper (matching Design HTML)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Processing Pipeline",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        Text(
                            text = "8 STAGES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlazeOrange,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val curStatus = currentProj.status
                    val progress = currentProj.overallProgress

                    PipelineStepItem(
                        stepNumber = 1,
                        title = "Upload & Source Verification",
                        subtitle = "Raw movie stream validated",
                        status = StepState.COMPLETED
                    )
                    PipelineStepItem(
                        stepNumber = 2,
                        title = "Audio Extraction (FFmpeg 48kHz)",
                        subtitle = "Multi-channel stereo source split",
                        status = if (progress >= 18) StepState.COMPLETED else if (curStatus == DubbingStatus.EXTRACTING_AUDIO.name) StepState.ACTIVE else StepState.PENDING
                    )
                    PipelineStepItem(
                        stepNumber = 3,
                        title = "Whisper Dialogue Transcription",
                        subtitle = "faster-whisper acoustic speech-to-text",
                        status = if (progress >= 45) StepState.COMPLETED else if (curStatus == DubbingStatus.TRANSCRIBING.name) StepState.ACTIVE else StepState.PENDING
                    )
                    PipelineStepItem(
                        stepNumber = 4,
                        title = "Speaker Detection & Diarization",
                        subtitle = "Character & vocal track separation",
                        status = if (progress >= 50) StepState.COMPLETED else if (curStatus == DubbingStatus.DETECTING_SPEAKERS.name || curStatus == DubbingStatus.TRANSCRIBING.name) StepState.ACTIVE else StepState.PENDING
                    )
                    PipelineStepItem(
                        stepNumber = 5,
                        title = "Natural Telugu Phrasing (IndicTrans2)",
                        subtitle = "Cinematic idiomatic translation",
                        status = if (progress >= 65) StepState.COMPLETED else if (curStatus == DubbingStatus.TRANSLATING.name) StepState.ACTIVE else StepState.PENDING
                    )
                    PipelineStepItem(
                        stepNumber = 6,
                        title = "Telugu Voice Generation (TTS)",
                        subtitle = "IndicTTS/MMS expressive synthesis",
                        status = if (progress >= 80) StepState.COMPLETED else if (curStatus == DubbingStatus.GENERATING_VOICE.name) StepState.ACTIVE else StepState.PENDING
                    )
                    PipelineStepItem(
                        stepNumber = 7,
                        title = "Dialogue Synchronization & Ducking",
                        subtitle = "Background music preserved with vocal ducking",
                        status = if (progress >= 90) StepState.COMPLETED else if (curStatus == DubbingStatus.SYNCING_AUDIO.name) StepState.ACTIVE else StepState.PENDING
                    )
                    PipelineStepItem(
                        stepNumber = 8,
                        title = "Final Video Muxing (FFmpeg)",
                        subtitle = "Subtitles muxed with synchronized Telugu track",
                        status = if (progress >= 100) StepState.COMPLETED else if (curStatus == DubbingStatus.RENDERING.name) StepState.ACTIVE else StepState.PENDING,
                        isLast = true
                    )
                }
            }
        }

        // Chunk Grid & Fault Tolerance (Requirement 3 & 22)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Chunk-Based Engine (${chunks.size} chunks)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "30s independent chunks with global timestamps",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        WaveformVisualizer(isPlaying = isRunning, barCount = 10)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (chunks.isEmpty()) {
                        Text("Extracting audio chunks...", fontSize = 12.sp, color = TextSecondary)
                    } else {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            chunks.forEach { chunk ->
                                val (color, label) = when (chunk.status) {
                                    ChunkStatus.COMPLETED.name -> Pair(StatusSuccessBright, "✓")
                                    ChunkStatus.PROCESSING.name -> Pair(BlazeOrange, "...")
                                    ChunkStatus.FAILED.name -> Pair(StatusError, "!")
                                    ChunkStatus.SKIPPED.name -> Pair(StatusWarning, "-")
                                    else -> Pair(TextSecondary.copy(alpha = 0.3f), "${chunk.chunkIndex + 1}")
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(color.copy(alpha = 0.15f))
                                        .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (chunk.status == ChunkStatus.FAILED.name) {
                                                selectedFailedChunk = chunk
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color
                                    )
                                }
                            }
                        }
                    }

                    // Failed Chunk Details Sheet / Banner
                    if (selectedFailedChunk != null) {
                        val fc = selectedFailedChunk!!
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(StatusError.copy(alpha = 0.15f))
                                .border(1.dp, StatusError, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Error, contentDescription = null, tint = StatusError)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Chunk #${fc.chunkIndex + 1} Failed", fontWeight = FontWeight.Bold, color = StatusError, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = fc.errorMessage ?: "Inference timeout or connection reset. Other chunks remain safe.",
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            viewModel.retryChunk(fc.id)
                                            selectedFailedChunk = null
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = BlazeOrange,
                                            contentColor = AbyssDark
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Retry Chunk", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.skipChunk(fc.id)
                                            selectedFailedChunk = null
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Skip Chunk", color = TextPrimary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Processing Logs Console (Requirement 8)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = ElectricCyanBright)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Pipeline Terminal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "${logs.size} log events",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF070B12))
                            .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        LazyColumn(
                            reverseLayout = true,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(logs) { log ->
                                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                                val logColor = when (log.level) {
                                    "ERROR" -> StatusError
                                    "WARN" -> StatusWarning
                                    "SUCCESS" -> StatusSuccess
                                    else -> ElectricCyanBright
                                }
                                Text(
                                    text = "[$timeStr] [${log.step}] ${log.message}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = logColor,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

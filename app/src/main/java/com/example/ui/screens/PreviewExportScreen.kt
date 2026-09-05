package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DubbingViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.WaveformVisualizer
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
import com.example.ui.theme.TextTertiary

@Composable
fun PreviewExportScreen(
    viewModel: DubbingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val project by viewModel.activeProject.collectAsState()
    val audioTrack by viewModel.playerAudioTrack.collectAsState()
    val subtitleTrack by viewModel.playerSubtitleTrack.collectAsState()

    var isVideoPlaying by remember { mutableStateOf(false) }
    var playbackPosition by remember { mutableFloatStateOf(0.15f) }

    if (project == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AbyssDark)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No project selected. Start dubbing or open a project.",
                color = TextSecondary,
                fontSize = 15.sp
            )
        }
        return
    }

    val currentProj = project!!

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Success Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF063520), Color(0xFF042416))
                        )
                    )
                    .border(1.dp, StatusSuccessBright.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusSuccessBright,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Your Telugu Dubbed Movie Is Ready",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Mixed with stereo dialogue ducking and background audio preservation.",
                            fontSize = 12.sp,
                            color = Color(0xFFA7F3D0)
                        )
                    }
                }
            }
        }

        // Cinematic Video Player Frame
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    // Video Viewport with Subtitles Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .background(Color.Black)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simulated movie background frame
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFF1C1917), Color(0xFF0C0A09))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = BlazeOrange.copy(alpha = 0.7f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentProj.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isVideoPlaying) "PLAYING [TELUGU DUB]" else "PAUSED",
                                    fontSize = 11.sp,
                                    color = BlazeOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Subtitle overlay
                        if (subtitleTrack != "NONE") {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.82f))
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = when (subtitleTrack) {
                                        "TELUGU" -> "నువ్వు ఆ మ్యాప్‌తో ఎక్కడికి వెళ్తున్నావు? మనకు అస్సలు సమయం లేదు!"
                                        "ENGLISH" -> "Where are you going with that map? We don't have enough time!"
                                        else -> "నువ్వు ఎక్కడికి వెళ్తున్నావు? [Where are you going?]"
                                    },
                                    color = Color(0xFFFED7AA),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Play/Pause overlay button
                        IconButton(
                            onClick = { isVideoPlaying = !isVideoPlaying },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .border(1.5.dp, BlazeOrange, CircleShape)
                        ) {
                            Icon(
                                if (isVideoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = BlazeOrange,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Player Controls Row
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("18:42", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Slider(
                                value = playbackPosition,
                                onValueChange = { playbackPosition = it },
                                colors = SliderDefaults.colors(
                                    thumbColor = BlazeOrange,
                                    activeTrackColor = BlazeOrange,
                                    inactiveTrackColor = SurfaceElevatedDark
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(currentProj.durationFormatted, fontSize = 11.sp, color = TextSecondary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Audio Track Selector (Telugu Dub vs Original)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = BlazeOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Audio Track:", fontSize = 12.sp, color = TextSecondary)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val isTelugu = audioTrack == "TELUGU_DUBBED"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isTelugu) BlazeOrange else SurfaceElevatedDark)
                                        .border(1.dp, if (isTelugu) BlazeOrangeBorder else BorderSubtle, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setPlayerAudioTrack("TELUGU_DUBBED") }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Telugu AI Dubbed",
                                        color = if (isTelugu) AbyssDark else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                val isOriginal = audioTrack == "ORIGINAL"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isOriginal) BlazeOrange else SurfaceElevatedDark)
                                        .border(1.dp, if (isOriginal) BlazeOrangeBorder else BorderSubtle, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setPlayerAudioTrack("ORIGINAL") }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Original Audio",
                                        color = if (isOriginal) AbyssDark else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Subtitles Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Subtitles, contentDescription = null, tint = BlazeOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Subtitles:", fontSize = 12.sp, color = TextSecondary)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(
                                    "TELUGU" to "Telugu",
                                    "ENGLISH" to "English",
                                    "BILINGUAL" to "Dual",
                                    "NONE" to "Off"
                                ).forEach { (code, lbl) ->
                                    val isSubSelected = subtitleTrack == code
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSubSelected) BlazeOrangeDim else SurfaceElevatedDark)
                                            .border(1.dp, if (isSubSelected) BlazeOrangeBorder else BorderSubtle, RoundedCornerShape(8.dp))
                                            .clickable { viewModel.setPlayerSubtitleTrack(code) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = lbl,
                                            color = if (isSubSelected) BlazeOrange else TextPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Export & Download Actions (Requirement 13)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Export & Download Dubbed Assets",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Download movie, master audio, and subtitle streams in standard formats",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Watch Movie
                        Button(
                            onClick = {
                                isVideoPlaying = true
                                Toast.makeText(context, "Playing Telugu dubbed movie stream", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("watch_movie_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BlazeOrange,
                                contentColor = AbyssDark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AbyssDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Watch Movie", color = AbyssDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        // Download Video
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Downloading final dubbed video (MP4)...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("download_video_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = SurfaceElevatedDark,
                                contentColor = TextPrimary
                            )
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = BlazeOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download Video (.mp4)")
                        }

                        // Download Telugu Audio
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Downloading Telugu dialogue & mixed master track (.wav)...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("download_audio_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = SurfaceElevatedDark,
                                contentColor = TextPrimary
                            )
                        ) {
                            Icon(Icons.Default.Audiotrack, contentDescription = null, tint = BlazeOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download Telugu Audio (.wav)")
                        }

                        // Download Subtitle
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Exporting Telugu SRT & VTT subtitles...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("download_subtitle_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = SurfaceElevatedDark,
                                contentColor = TextPrimary
                            )
                        ) {
                            Icon(Icons.Default.Subtitles, contentDescription = null, tint = BlazeOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download Subtitles (SRT / VTT)")
                        }

                        // Export Transcript
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Exporting bilingual transcript (.txt)...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("export_transcript_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = SurfaceElevatedDark,
                                contentColor = TextPrimary
                            )
                        ) {
                            Icon(Icons.Default.TextSnippet, contentDescription = null, tint = BlazeOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export Transcript (.txt / .json)")
                        }
                    }
                }
            }
        }

        // Technical Specs
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dubbing Technical Output Specifications", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BlazeOrange)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Video Stream: Direct stream copy (lossless, original resolution maintained)\n• Audio Stream: AAC 320kbps 48kHz Stereo with dynamic range compression\n• Dialogue Ducking: Original speech reduced by 95%, Telugu AI speech normalized at -14 LUFS\n• Subtitle Charset: UTF-8 with full Telugu script Unicode encoding", fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

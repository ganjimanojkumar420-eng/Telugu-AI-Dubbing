package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.ui.theme.RubyCrimson
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
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
fun TranslationEditorScreen(
    viewModel: DubbingViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.activeProject.collectAsState()
    val segments by viewModel.activeSegments.collectAsState()
    val activeIndex by viewModel.activeEditorIndex.collectAsState()
    val isPlaying by viewModel.isPlayingAudio.collectAsState()

    if (segments.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AbyssDark)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Dialogue segments are being transcribed & translated.\nThey will appear here for human review & correction.",
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        return
    }

    val currentSegment = segments.getOrNull(activeIndex) ?: segments.first()
    var editedTeluguText by remember(currentSegment.id) { mutableStateOf(currentSegment.teluguText) }
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
            // Header
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = BlazeOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Telugu Translation Editor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlazeOrange
                            )
                        }
                        Text(
                            text = "Segment ${activeIndex + 1} of ${segments.size}",
                            fontSize = 12.sp,
                            color = BlazeOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "Refine Telugu dialogue translations and audition voice previews before final rendering.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Horizontal Segment Selector Carousel
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(segments) { idx, seg ->
                    val isSelected = idx == activeIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BlazeOrange else SurfaceElevatedDark)
                            .border(
                                1.dp,
                                if (isSelected) BlazeOrangeBorder else BorderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                viewModel.setEditorIndex(idx)
                                saveConfirmation = false
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "#${idx + 1} (${(seg.startMs / 1000)}s)",
                            color = if (isSelected) AbyssDark else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Active Segment Editor Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Speaker badge & timing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(BlazeOrangeDim)
                                    .border(1.dp, BlazeOrangeBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentSegment.speakerId.takeLast(2),
                                    fontSize = 11.sp,
                                    color = BlazeOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = currentSegment.speakerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Emotion: ${currentSegment.emotion}  •  ${String.format("%.1f", currentSegment.startMs / 1000f)}s - ${String.format("%.1f", currentSegment.endMs / 1000f)}s",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        if (isPlaying) {
                            WaveformVisualizer(isActive = true, barCount = 6, barColor = BlazeOrange)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Original Dialogue Box
                    Text("Original Audio Transcript:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceSubtle)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "“${currentSegment.originalText}”",
                            fontSize = 14.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Editable Telugu Script Dialogue Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Telugu Dialogue (తెలుగు డైలాగ్):", fontSize = 12.sp, color = BlazeOrange, fontWeight = FontWeight.Bold)
                        if (currentSegment.isEdited) {
                            Text("Edited ✓", fontSize = 11.sp, color = StatusSuccessBright)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = editedTeluguText,
                        onValueChange = {
                            editedTeluguText = it
                            saveConfirmation = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("telugu_dialogue_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerDark,
                            unfocusedContainerColor = SurfaceContainerDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedIndicatorColor = BlazeOrange
                        ),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Audio and Voice Preview Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Play / Audition voice
                        Button(
                            onClick = {
                                if (isPlaying) {
                                    viewModel.stopAudio()
                                } else {
                                    viewModel.playSegmentAudio(currentSegment.copy(teluguText = editedTeluguText))
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("play_voice_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) RubyCrimson else SurfaceElevatedDark,
                                contentColor = if (isPlaying) Color.White else BlazeOrange
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isPlaying) "Stop Audio" else "Play Voice", fontWeight = FontWeight.Bold)
                        }

                        // Save changes button
                        Button(
                            onClick = {
                                viewModel.updateSegmentTranslation(currentSegment.id, editedTeluguText)
                                saveConfirmation = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_translation_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BlazeOrange,
                                contentColor = AbyssDark
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = AbyssDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (saveConfirmation) "Saved! ✓" else "Save Dialogue", color = AbyssDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Next / Prev Segment Navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (activeIndex > 0) {
                                    viewModel.setEditorIndex(activeIndex - 1)
                                    saveConfirmation = false
                                }
                            },
                            enabled = activeIndex > 0
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Previous Segment")
                        }

                        OutlinedButton(
                            onClick = {
                                if (activeIndex < segments.size - 1) {
                                    viewModel.setEditorIndex(activeIndex + 1)
                                    saveConfirmation = false
                                }
                            },
                            enabled = activeIndex < segments.size - 1
                        ) {
                            Text("Next Segment")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }

        // Quick Telugu Dialogue Suggestions / Idioms
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Cinematic Phrases (తెలుగు పంచ్ లైన్లు):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val quickPhrases = listOf(
                        "నువ్వు ఎక్కడికి వెళ్తున్నావు?",
                        "నా మాట నమ్ము, విజయం మనదే!",
                        "ఆజ్ఞ ఇచ్చే వరకు ఎవరూ కాల్పులు జరపవద్దు!",
                        "నా ప్రాణాలను అడ్డం వేసి ఈ నేలను కాపాడుకుంటాను.",
                        "నేను నీ కోసం మళ్ళీ వస్తాను.",
                        "జాగ్రత్త, శత్రువులు చుట్టుముట్టారు!"
                    )

                    quickPhrases.forEach { phrase ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceElevatedDark)
                                .clickable {
                                    editedTeluguText = phrase
                                    saveConfirmation = false
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = phrase, fontSize = 12.sp, color = ElectricCyanBright)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

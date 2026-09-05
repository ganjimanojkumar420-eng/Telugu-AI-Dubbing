package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioMode
import com.example.data.model.SupportedLanguage
import com.example.data.model.VoiceMode
import com.example.data.model.VoiceStyle
import com.example.ui.DubbingViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.AbyssDark
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeBorder
import com.example.ui.theme.BlazeOrangeDim
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardBorderHighlight
import com.example.ui.theme.RubyCrimson
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBright
import com.example.ui.theme.SurfaceContainerDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceGlassCard
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.TextOnGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: DubbingViewModel,
    modifier: Modifier = Modifier
) {
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("Bahubali_The_Beginning_Scene.mp4") }
    var selectedFileSize by remember { mutableStateOf("1.42 GB") }
    var selectedDuration by remember { mutableStateOf("01h 32m 40s") }
    var selectedDurationSec by remember { mutableStateOf(5560L) }

    var sourceLanguage by remember { mutableStateOf("English") }
    var targetLanguage by remember { mutableStateOf("Telugu") }
    var voiceMode by remember { mutableStateOf(VoiceMode.AUTO_CHARACTER) }
    var voiceStyle by remember { mutableStateOf(VoiceStyle.CINEMATIC) }
    var audioMode by remember { mutableStateOf(AudioMode.FULL_AI_MIX) }

    var showSampleDialog by remember { mutableStateOf(false) }

    // Character voice config sliders
    var heroPitch by remember { mutableFloatStateOf(1.0f) }
    var heroSpeed by remember { mutableFloatStateOf(1.0f) }
    var heroinePitch by remember { mutableFloatStateOf(1.08f) }
    var heroineSpeed by remember { mutableFloatStateOf(0.98f) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            selectedFileName = uri.lastPathSegment ?: "Uploaded_Movie.mp4"
            selectedFileSize = "2.35 GB"
            selectedDuration = "02h 10m 15s"
            selectedDurationSec = 7815L
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Section
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E1612),
                                Color(0xFF120E0C),
                                AbyssDark
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(BlazeOrangeBorder, BorderSubtle)
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(BlazeOrangeDim)
                            .border(1.dp, BlazeOrangeBorder, RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BlazeOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OPEN SOURCE AI DUBBING",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlazeOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "MANOJ AI MOVIE DUBBING",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = BlazeOrange
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Turn Any Movie Into Telugu",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "“Your Movie. Your Language. Your Voice.”\nCreate natural Telugu movie dubbing with open-source AI models.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { videoPickerLauncher.launch("video/*") },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("upload_movie_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BlazeOrange,
                                contentColor = AbyssDark
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AbyssDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Upload Movie",
                                color = AbyssDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showSampleDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("try_sample_button"),
                            border = BorderStroke(1.dp, BorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = SurfaceElevatedDark,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = BlazeOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Try Sample",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Uploaded Video Card & Specs
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BlazeOrangeDim),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.VideoFile,
                                contentDescription = null,
                                tint = BlazeOrange
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedFileName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Size: $selectedFileSize  •  Duration: $selectedDuration",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Formats supported badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Supported:",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        listOf("MP4", "MKV", "MOV", "AVI", "WEBM").forEach { fmt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceElevatedDark)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = fmt,
                                    fontSize = 10.sp,
                                    color = BlazeOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dubbing Settings Section
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Dubbing Settings",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlazeOrange
                    )
                    Text(
                        text = "Configure voice styles, audio separation, and AI character assignment",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Language Selection Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Source Language
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Source Language",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            var expandedSource by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expandedSource,
                                onExpandedChange = { expandedSource = !expandedSource }
                            ) {
                                OutlinedTextField(
                                    value = sourceLanguage,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSource) },
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
                                ExposedDropdownMenu(
                                    expanded = expandedSource,
                                    onDismissRequest = { expandedSource = false }
                                ) {
                                    listOf("Auto Detect", "English", "Hindi", "Tamil", "Kannada", "Malayalam", "Spanish", "French").forEach { lang ->
                                        DropdownMenuItem(
                                            text = { Text(lang) },
                                            onClick = {
                                                sourceLanguage = lang
                                                expandedSource = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Target Language
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Target Language",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = "Telugu (తెలుగు)",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceContainerDark,
                                    unfocusedContainerColor = SurfaceContainerDark,
                                    focusedTextColor = BlazeOrange,
                                    unfocusedTextColor = BlazeOrange,
                                    focusedIndicatorColor = BlazeOrange
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Voice Mode Selection
                    Text(
                        text = "Voice Mode",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        VoiceMode.values().forEach { mode ->
                            val isSelected = voiceMode == mode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BlazeOrangeDim else SurfaceElevatedDark)
                                    .border(
                                        1.dp,
                                        if (isSelected) BlazeOrangeBorder else BorderSubtle,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { voiceMode = mode }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, if (isSelected) BlazeOrange else TextSecondary, CircleShape)
                                        .background(if (isSelected) BlazeOrange else Color.Transparent)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = mode.label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) BlazeOrange else TextPrimary
                                    )
                                    Text(
                                        text = mode.description,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Voice Style & Audio Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Voice Style",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            var expandedStyle by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expandedStyle,
                                onExpandedChange = { expandedStyle = !expandedStyle }
                            ) {
                                OutlinedTextField(
                                    value = voiceStyle.label,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStyle) },
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
                                ExposedDropdownMenu(
                                    expanded = expandedStyle,
                                    onDismissRequest = { expandedStyle = false }
                                ) {
                                    VoiceStyle.values().forEach { st ->
                                        DropdownMenuItem(
                                            text = { Text(st.label) },
                                            onClick = {
                                                voiceStyle = st
                                                expandedStyle = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Audio Mixing Mode",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            var expandedAudio by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expandedAudio,
                                onExpandedChange = { expandedAudio = !expandedAudio }
                            ) {
                                OutlinedTextField(
                                    value = audioMode.label,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAudio) },
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
                                ExposedDropdownMenu(
                                    expanded = expandedAudio,
                                    onDismissRequest = { expandedAudio = false }
                                ) {
                                    AudioMode.values().forEach { am ->
                                        DropdownMenuItem(
                                            text = { Text(am.label) },
                                            onClick = {
                                                audioMode = am
                                                expandedAudio = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.createAndStartProject(
                                title = selectedFileName.removeSuffix(".mp4").replace("_", " "),
                                durationFormatted = selectedDuration,
                                durationSeconds = selectedDurationSec,
                                fileSizeFormatted = selectedFileSize,
                                videoUri = selectedVideoUri?.toString() ?: "file://$selectedFileName",
                                sourceLang = sourceLanguage,
                                targetLang = "Telugu",
                                voiceMode = voiceMode.name,
                                voiceStyle = voiceStyle.name,
                                audioMode = audioMode.name
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("start_dubbing_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlazeOrange,
                            contentColor = AbyssDark
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AbyssDark)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Start Telugu Dubbing",
                            color = AbyssDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Character Voice Management Panel (Requirement 7)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = BlazeOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Character Voice Management",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Customize character timbre, speed, and emotional tone for detected cast",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Character 1: Hero
                    CharacterVoiceCard(
                        charNumber = 1,
                        name = "Protagonist (Hero)",
                        voice = "Telugu Male 1 (Arjun - Heroic Baritone)",
                        emotion = "Cinematic & Punchy",
                        pitch = heroPitch,
                        speed = heroSpeed,
                        onPitchChange = { heroPitch = it },
                        onSpeedChange = { heroSpeed = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Character 2: Heroine
                    CharacterVoiceCard(
                        charNumber = 2,
                        name = "Leading Character (Heroine)",
                        voice = "Telugu Female 1 (Priya - Melodic Expressive)",
                        emotion = "Emotional & Natural",
                        pitch = heroinePitch,
                        speed = heroineSpeed,
                        onPitchChange = { heroinePitch = it },
                        onSpeedChange = { heroineSpeed = it }
                    )
                }
            }
        }

        // Feature Cards (Requirement 26)
        item {
            Text(
                text = "Core Dubbing Capabilities",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            val features = listOf(
                FeatureItem("AI Telugu Dubbing", "Convert movie dialogue into natural Telugu speech.", Icons.Default.RecordVoiceOver, BlazeOrange),
                FeatureItem("Long Movie Processing", "Designed for large videos (30m, 1h, 2h, 3h+) using chunk-based processing.", Icons.Default.Movie, BlazeOrange),
                FeatureItem("Character Voices", "Automatically assign different voices to characters with emotion preservation.", Icons.Default.GraphicEq, RubyCrimson),
                FeatureItem("Background Processing", "Continue processing safely in the background with full resume support.", Icons.Default.AutoAwesome, BlazeOrange),
                FeatureItem("Preserve Music & Effects", "Keep background score and sound effects intact with smart vocal ducking.", Icons.Default.MusicNote, StatusSuccessBright),
                FeatureItem("Subtitle Generation", "Generate Telugu SRT/VTT and bilingual subtitles automatically.", Icons.Default.Subtitles, StatusSuccessBright)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                features.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceElevatedDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(item.tint.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(item.icon, contentDescription = null, tint = item.tint, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(item.description, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        // Legal & Open Source Notice (Requirements 4 & 27)
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BorderSubtle,
                backgroundColor = SurfaceSubtle
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = BlazeOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Important Open Source & Legal Notice", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlazeOrange)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• “Designed for free/open-source AI processing. Actual limits depend on your hosting hardware.”\n• “Only upload movies and audio/video content that you own or have permission to process. Respect copyright and applicable laws.”",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Sample Movies Dialog
    if (showSampleDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showSampleDialog = false }) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Try Sample Long Movie Clips", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BlazeOrange)
                    Text("Select a scene to immediately experience Telugu AI dubbing:", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    val samples = listOf(
                        Triple("rrr", "RRR Climax Battlefield (2h 15m format)", "Hindi -> Telugu Dub • Action/Cinematic"),
                        Triple("baahubali", "Baahubali Coronation Scene (1h 45m format)", "Tamil -> Telugu Dub • Royal Monologue"),
                        Triple("interstellar", "Interstellar Wormhole (2h 49m format)", "English -> Telugu Dub • Sci-Fi"),
                        Triple("salaar", "Salaar Coal Mines Block (42m format)", "Hindi -> Telugu Dub • High Octane")
                    )

                    samples.forEach { (type, name, meta) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                .clickable {
                                    showSampleDialog = false
                                    viewModel.loadSampleMovie(type)
                                },
                            colors = CardDefaults.cardColors(containerColor = SurfaceElevatedDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text(meta, fontSize = 11.sp, color = BlazeOrange)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showSampleDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceContainerDark,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close", color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterVoiceCard(
    charNumber: Int,
    name: String,
    voice: String,
    emotion: String,
    pitch: Float,
    speed: Float,
    onPitchChange: (Float) -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevatedDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Character $charNumber: $name",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BlazeOrangeDim)
                        .border(1.dp, BlazeOrangeBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(emotion, fontSize = 10.sp, color = BlazeOrange, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Assigned Voice: $voice", fontSize = 11.sp, color = BlazeOrange)

            Spacer(modifier = Modifier.height(10.dp))

            // Sliders
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pitch: ${String.format("%.2f", pitch)}x", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(80.dp))
                Slider(
                    value = pitch,
                    onValueChange = onPitchChange,
                    valueRange = 0.7f..1.3f,
                    colors = SliderDefaults.colors(
                        thumbColor = BlazeOrange,
                        activeTrackColor = BlazeOrange,
                        inactiveTrackColor = SurfaceContainerDark
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Speed: ${String.format("%.2f", speed)}x", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(80.dp))
                Slider(
                    value = speed,
                    onValueChange = onSpeedChange,
                    valueRange = 0.8f..1.3f,
                    colors = SliderDefaults.colors(
                        thumbColor = BlazeOrange,
                        activeTrackColor = BlazeOrange,
                        inactiveTrackColor = SurfaceContainerDark
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private data class FeatureItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val tint: Color
)

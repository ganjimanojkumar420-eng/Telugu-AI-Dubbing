package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DubbingViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PreviewExportScreen
import com.example.ui.screens.ProcessingScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TranslationEditorScreen
import com.example.ui.theme.AbyssDark
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeBorder
import com.example.ui.theme.BlazeOrangeDim
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaGoldBright
import com.example.ui.theme.CinemaGoldDark
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanBright
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.SurfaceContainerDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TextOnGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: DubbingViewModel = viewModel()
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: DubbingViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isRunning by viewModel.isProcessing.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AbyssDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PRODUCTION CONSOLE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = BlazeOrange
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Manoj AI ",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.5).sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Dubbing",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = (-0.5).sp,
                                color = TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    if (isRunning) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(BlazeOrangeDim)
                                .border(1.dp, BlazeOrangeBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(BlazeOrange)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PROCESSING",
                                    fontSize = 9.sp,
                                    color = BlazeOrange,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(
                        onClick = { viewModel.selectTab(5) },
                        modifier = Modifier.testTag("settings_top_button")
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "AI Model Engine Settings",
                            tint = if (selectedTab == 5) BlazeOrange else TextSecondary
                        )
                    }

                    // Avatar circle badge matching Design HTML (h-10 w-10 rounded-full bg-slate-800 border-slate-700 MP)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Slate800)
                            .border(1.dp, Slate700, CircleShape)
                            .clickable { viewModel.selectTab(4) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AbyssDark,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerDark)
            ) {
                // Subtle top border matching design border-t border-white/5
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BorderSubtle)
                )

                NavigationBar(
                    containerColor = SurfaceContainerDark,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(72.dp)
                ) {
                    // Tab 0: Dub Movie (Home)
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = { Icon(Icons.Default.Movie, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlazeOrange,
                            selectedTextColor = BlazeOrange,
                            indicatorColor = BlazeOrangeDim,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary
                        ),
                        modifier = Modifier.testTag("tab_dub_movie")
                    )

                    // Tab 1: Processing (Pipeline)
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = {
                            if (isRunning) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = BlazeOrange,
                                            contentColor = AbyssDark
                                        ) { Text("●") }
                                    }
                                ) {
                                    Icon(Icons.Default.GraphicEq, contentDescription = "Pipeline")
                                }
                            } else {
                                Icon(Icons.Default.HourglassBottom, contentDescription = "Pipeline")
                            }
                        },
                        label = { Text("Pipeline", fontSize = 10.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlazeOrange,
                            selectedTextColor = BlazeOrange,
                            indicatorColor = BlazeOrangeDim,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary
                        ),
                        modifier = Modifier.testTag("tab_processing")
                    )

                    // Tab 2: Translation Editor
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        icon = { Icon(Icons.Default.Translate, contentDescription = "Editor") },
                        label = { Text("Editor", fontSize = 10.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlazeOrange,
                            selectedTextColor = BlazeOrange,
                            indicatorColor = BlazeOrangeDim,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary
                        ),
                        modifier = Modifier.testTag("tab_editor")
                    )

                    // Tab 3: Preview & Export
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.selectTab(3) },
                        icon = { Icon(Icons.Default.PlayCircle, contentDescription = "Preview") },
                        label = { Text("Preview", fontSize = 10.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlazeOrange,
                            selectedTextColor = BlazeOrange,
                            indicatorColor = BlazeOrangeDim,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary
                        ),
                        modifier = Modifier.testTag("tab_preview")
                    )

                    // Tab 4: Projects
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { viewModel.selectTab(4) },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Projects") },
                        label = { Text("Projects", fontSize = 10.sp, fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlazeOrange,
                            selectedTextColor = BlazeOrange,
                            indicatorColor = BlazeOrangeDim,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary
                        ),
                        modifier = Modifier.testTag("tab_projects")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(viewModel = viewModel)
                1 -> ProcessingScreen(viewModel = viewModel)
                2 -> TranslationEditorScreen(viewModel = viewModel)
                3 -> PreviewExportScreen(viewModel = viewModel)
                4 -> ProjectsScreen(viewModel = viewModel)
                5 -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

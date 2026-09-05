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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DubbingStatus
import com.example.data.model.ProjectEntity
import com.example.ui.DubbingViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AbyssDark
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeBorder
import com.example.ui.theme.BlazeOrangeDim
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.RubyCrimson
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectsScreen(
    viewModel: DubbingViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsState()
    val activeProjId by viewModel.activeProjectId.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Projects",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${projects.size} movie dubbing projects registered",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { viewModel.selectTab(0) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlazeOrange,
                        contentColor = AbyssDark
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = AbyssDark)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Dub", color = AbyssDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        if (projects.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No projects yet. Click 'New Dub' to start.", color = TextSecondary)
                }
            }
        } else {
            items(projects) { proj ->
                ProjectCard(
                    project = proj,
                    isActive = proj.id == activeProjId,
                    onSelect = {
                        viewModel.setActiveProject(proj.id)
                        if (proj.status == DubbingStatus.COMPLETED.name) {
                            viewModel.selectTab(3) // Preview
                        } else {
                            viewModel.selectTab(1) // Processing
                        }
                    },
                    onContinue = {
                        viewModel.setActiveProject(proj.id)
                        viewModel.selectTab(1) // Processing
                        if (proj.status == DubbingStatus.PAUSED.name || proj.status == DubbingStatus.UPLOADED.name) {
                            viewModel.startDubbing()
                        }
                    },
                    onPreview = {
                        viewModel.setActiveProject(proj.id)
                        viewModel.selectTab(3) // Preview/Export
                    },
                    onDelete = { viewModel.deleteProject(proj.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProjectCard(
    project: ProjectEntity,
    isActive: Boolean,
    onSelect: () -> Unit,
    onContinue: () -> Unit,
    onPreview: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(project.createdAt))

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        borderColor = if (isActive) BlazeOrange else BorderSubtle
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    // Movie Thumbnail Box
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceSubtle)
                            .border(1.dp, if (isActive) BlazeOrangeBorder else BorderSubtle, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = null, tint = BlazeOrange, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = project.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${project.durationFormatted}  •  ${project.sourceLanguage} → Telugu",
                            fontSize = 11.sp,
                            color = BlazeOrange
                        )
                        Text(
                            text = "Created: $dateStr  •  Size: ${project.fileSizeFormatted}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                StatusBadge(status = project.status)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dubbing Progress",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${project.overallProgress}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlazeOrange
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { project.overallProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = BlazeOrange,
                trackColor = SurfaceElevatedDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (project.status == DubbingStatus.COMPLETED.name) {
                    Button(
                        onClick = onPreview,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlazeOrange,
                            contentColor = AbyssDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = AbyssDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preview & Download", color = AbyssDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onContinue,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceElevatedDark,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = BlazeOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Continue Dubbing", color = TextPrimary, fontSize = 12.sp)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RubyCrimson)
                }
            }
        }
    }
}

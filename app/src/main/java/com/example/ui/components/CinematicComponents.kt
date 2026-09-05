package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DubbingStatus
import com.example.ui.theme.AbyssDark
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.BlazeOrangeBorder
import com.example.ui.theme.BlazeOrangeDim
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardBorderHighlight
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBorder
import com.example.ui.theme.StatusSuccessDim
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    borderColor: Color = BorderSubtle,
    backgroundColor: Color = ObsidianSurface,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .border(1.dp, borderColor, shape)
            .clip(shape),
        color = backgroundColor,
        shape = shape
    ) {
        content()
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, borderColor, textColor, label) = when (status) {
        DubbingStatus.COMPLETED.name -> Quad(StatusSuccessDim, StatusSuccessBorder, StatusSuccess, "Completed")
        DubbingStatus.FAILED.name -> Quad(StatusError.copy(alpha = 0.2f), StatusError.copy(alpha = 0.5f), StatusError, "Failed")
        DubbingStatus.PAUSED.name -> Quad(BlazeOrangeDim, BlazeOrangeBorder, BlazeOrange, "Paused")
        DubbingStatus.UPLOADED.name -> Quad(StatusInfo.copy(alpha = 0.2f), StatusInfo.copy(alpha = 0.5f), StatusInfo, "Uploaded")
        else -> Quad(BlazeOrangeDim, BlazeOrangeBorder, BlazeOrange, "Processing")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.2.sp
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun WaveformVisualizer(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    isActive: Boolean = isPlaying,
    barCount: Int = 18,
    color: Color = BlazeOrange,
    barColor: Color = color
) {
    val active = isPlaying && isActive
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val heights = (0 until barCount).map { i ->
        val duration = 400 + (i * 70) % 500
        val anim by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = if (active) 1.0f else 0.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$i"
        )
        anim
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEach { factor ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(28.dp * factor)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun PipelineStepItem(
    stepNumber: Int,
    title: String,
    status: StepState,
    subtitle: String? = null,
    isLast: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_step")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon Indicator Column with connecting line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(26.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        when (status) {
                            StepState.COMPLETED -> StatusSuccessDim
                            StepState.ACTIVE -> BlazeOrangeDim
                            StepState.FAILED -> StatusError.copy(alpha = 0.2f)
                            StepState.PENDING -> Slate800
                        }
                    )
                    .border(
                        1.dp,
                        when (status) {
                            StepState.COMPLETED -> StatusSuccessBorder
                            StepState.ACTIVE -> BlazeOrangeBorder.copy(alpha = pulseAlpha)
                            StepState.FAILED -> StatusError
                            StepState.PENDING -> Slate700
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (status) {
                    StepState.COMPLETED -> Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = StatusSuccess,
                        modifier = Modifier.size(13.dp)
                    )
                    StepState.ACTIVE -> Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(BlazeOrange)
                    )
                    StepState.FAILED -> Icon(
                        Icons.Default.Close,
                        contentDescription = "Failed",
                        tint = StatusError,
                        modifier = Modifier.size(13.dp)
                    )
                    StepState.PENDING -> Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Slate700)
                    )
                }
            }

            if (!isLast) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(24.dp)
                        .background(
                            if (status == StepState.COMPLETED) StatusSuccess.copy(alpha = 0.4f)
                            else Slate800
                        )
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (status == StepState.ACTIVE) FontWeight.Bold else FontWeight.Medium,
                color = when (status) {
                    StepState.COMPLETED -> TextSecondary
                    StepState.ACTIVE -> BlazeOrange
                    StepState.FAILED -> StatusError
                    StepState.PENDING -> TextTertiary
                }
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }
        }

        if (status == StepState.ACTIVE) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(BlazeOrangeDim)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "ACTIVE",
                    fontSize = 9.sp,
                    color = BlazeOrange,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

enum class StepState {
    PENDING,
    ACTIVE,
    COMPLETED,
    FAILED
}

package DI.Composables.SavingGoalUI.components

import DI.Composables.SavingGoalUI.theme.SavingGoalTheme
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymanagement_frontend.R

@Composable
fun SavingGoalProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    showPercentage: Boolean = true,
    animate: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (animate) progress else progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            // Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        color = SavingGoalTheme.ProgressBackground,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = size.height,
                        cap = StrokeCap.Round
                    )
                }
            }
            
            // Progress
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val progressColor = when {
                        progress >= 1.0f -> SavingGoalTheme.SuccessGreen
                        progress >= 0.7f -> SavingGoalTheme.PrimaryGreenLight
                        progress >= 0.5f -> SavingGoalTheme.AccentGreen
                        progress >= 0.3f -> SavingGoalTheme.WarningOrange
                        else -> SavingGoalTheme.DangerRed
                    }
                    
                    drawLine(
                        color = progressColor,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = size.height,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        
        // Percentage Text
        if (showPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SavingGoalTheme.TextPrimary,
                    fontSize = 14.sp
                )
                  val statusText = when {
                    progress >= 1.0f -> stringResource(R.string.progress_completed)
                    progress >= 0.8f -> stringResource(R.string.progress_almost_completed)
                    progress >= 0.5f -> stringResource(R.string.progress_in_progress)
                    progress >= 0.3f -> stringResource(R.string.progress_need_effort)
                    else -> stringResource(R.string.progress_start_saving)
                }
                
                val statusColor = when {
                    progress >= 1.0f -> SavingGoalTheme.SuccessGreen
                    progress >= 0.7f -> SavingGoalTheme.PrimaryGreenLight
                    progress >= 0.5f -> SavingGoalTheme.AccentGreen
                    progress >= 0.3f -> SavingGoalTheme.WarningOrange
                    else -> SavingGoalTheme.DangerRed
                }
                
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 80.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 8.dp,
    showPercentage: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "circular_progress"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = SavingGoalTheme.ProgressBackground,
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round
        )
        
        androidx.compose.material3.CircularProgressIndicator(
            progress = { animatedProgress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            color = when {
                progress >= 1.0f -> SavingGoalTheme.SuccessGreen
                progress >= 0.7f -> SavingGoalTheme.PrimaryGreenLight
                progress >= 0.5f -> SavingGoalTheme.AccentGreen
                progress >= 0.3f -> SavingGoalTheme.WarningOrange
                else -> SavingGoalTheme.DangerRed
            },
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round
        )
        
        if (showPercentage) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = SavingGoalTheme.TextPrimary,
                fontSize = 14.sp
            )
        }
    }
}

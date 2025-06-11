package DI.Composables.BudgetUI.components

import DI.Composables.BudgetUI.theme.BudgetTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BudgetProgressIndicator(
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
                        color = BudgetTheme.ProgressBackground, // Light gray background
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
            ) {                Canvas(modifier = Modifier.fillMaxSize()) {
                    val progressColor = when {
                        animatedProgress < 0.7f -> BudgetTheme.SuccessGreen
                        animatedProgress < 0.9f -> BudgetTheme.WarningOrange
                        else -> BudgetTheme.DangerRed
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
          // Percentage Text - moved to left like SavingGoal
        if (showPercentage) {              Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = BudgetTheme.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun CircularBudgetProgressIndicator(
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
    ) {        androidx.compose.material3.CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = BudgetTheme.ProgressBackground, // Light gray background
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round
        )
          androidx.compose.material3.CircularProgressIndicator(
            progress = { animatedProgress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            color = when {
                animatedProgress < 0.7f -> BudgetTheme.SuccessGreen
                animatedProgress < 0.9f -> BudgetTheme.WarningOrange
                else -> BudgetTheme.DangerRed
            },
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round
        )
        
        if (showPercentage) {            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = BudgetTheme.TextPrimary,
                fontSize = 14.sp
            )
        }
    }
}

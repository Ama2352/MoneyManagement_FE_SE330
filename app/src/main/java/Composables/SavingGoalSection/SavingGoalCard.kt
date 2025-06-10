package DI.Composables.SavingGoalSection

import DI.Composables.WalletSection.SavingGoalTheme
import DI.Models.SavingGoal.SavingGoal
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingGoalCard(
    savingGoal: SavingGoal,
    categoryName: String,
    walletName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val endDate = LocalDateTime.parse(savingGoal.endDate, dateFormatter)
    val progress = savingGoal.savedPercentage.divide(BigDecimal(100), 4, java.math.RoundingMode.HALF_UP) // Convert percentage to decimal
    val daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), endDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = SavingGoalTheme.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = savingGoal.description,
                        style = MaterialTheme.typography.headlineSmall,
                        color = SavingGoalTheme.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Target: $${String.format("%.2f", savingGoal.targetAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SavingGoalTheme.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SavingGoalTheme.SurfaceGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.bodySmall,
                                color = SavingGoalTheme.DarkGreen,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SavingGoalTheme.LightGreen.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = walletName,
                                style = MaterialTheme.typography.bodySmall,
                                color = SavingGoalTheme.DarkGreen,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = SavingGoalTheme.AccentGreen
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$${String.format("%.2f", savingGoal.savedAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = SavingGoalTheme.SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${savingGoal.savedPercentage.toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = SavingGoalTheme.AccentGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SavingGoalTheme.AccentGreen,
                    trackColor = SavingGoalTheme.SurfaceGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Days Left",
                        style = MaterialTheme.typography.bodySmall,
                        color = SavingGoalTheme.TextSecondary
                    )
                    Text(
                        text = if (daysLeft >= 0) "$daysLeft days" else "Expired",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (daysLeft >= 0) SavingGoalTheme.TextPrimary else Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "End Date",
                        style = MaterialTheme.typography.bodySmall,
                        color = SavingGoalTheme.TextSecondary
                    )
                    Text(
                        text = endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SavingGoalTheme.TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

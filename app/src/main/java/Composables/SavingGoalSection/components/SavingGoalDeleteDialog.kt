package DI.Composables.SavingGoalUI.components

import DI.Composables.SavingGoalUI.theme.SavingGoalTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun SavingGoalDeleteDialog(
    savingGoalDescription: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SavingGoalTheme.CardBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Warning Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = SavingGoalTheme.DangerRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = SavingGoalTheme.DangerRed,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Title
                Text(
                    text = "Xác nhận xóa",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = SavingGoalTheme.TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                // Description
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bạn có chắc chắn muốn xóa mục tiêu tiết kiệm này không?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SavingGoalTheme.TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    
                    Text(
                        text = "\"$savingGoalDescription\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SavingGoalTheme.TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(
                                color = SavingGoalTheme.BackgroundGreen,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                    
                    Text(
                        text = "Hành động này không thể hoàn tác.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SavingGoalTheme.DangerRed,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SavingGoalTheme.TextSecondary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(SavingGoalTheme.BorderColor)
                        )
                    ) {
                        Text(
                            text = "Hủy",
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    // Delete Button
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SavingGoalTheme.DangerRed,
                            contentColor = SavingGoalTheme.White
                        )
                    ) {
                        Text(
                            text = "Xóa",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

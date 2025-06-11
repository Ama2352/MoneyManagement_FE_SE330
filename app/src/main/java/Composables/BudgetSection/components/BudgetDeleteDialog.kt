package DI.Composables.BudgetUI.components

import DI.Composables.BudgetUI.theme.BudgetTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.moneymanagement_frontend.R

@Composable
fun BudgetDeleteDialog(
    budgetDescription: String,
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
                containerColor = BudgetTheme.CardBackground
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
                            color = BudgetTheme.DangerRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = BudgetTheme.DangerRed,
                        modifier = Modifier.size(32.dp)
                    )
                }
                  // Title
                Text(
                    text = stringResource(R.string.confirm_delete),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = BudgetTheme.TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                // Description
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.delete_budget_confirmation),
                        style = MaterialTheme.typography.bodyMedium,
                        color = BudgetTheme.TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    
                    Text(
                        text = "\"$budgetDescription\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BudgetTheme.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = stringResource(R.string.action_cannot_be_undone),
                        style = MaterialTheme.typography.bodySmall,
                        color = BudgetTheme.DangerRed,
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
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BudgetTheme.TextSecondary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.close),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Delete Button
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BudgetTheme.DangerRed,
                            contentColor = BudgetTheme.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.delete_budget),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

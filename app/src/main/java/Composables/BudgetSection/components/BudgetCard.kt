package DI.Composables.BudgetUI.components

import DI.ViewModels.CurrencyConverterViewModel
import DI.Composables.BudgetUI.theme.BudgetTheme
import DI.Utils.DateUtils
import DI.Models.Budget.Budget
import DI.Utils.CurrencyUtils
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import Utils.TranslationManager
import Utils.MessageTranslationUtils
import Utils.useTranslatedMessage
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.moneymanagement_frontend.R

@Composable
fun BudgetCard(
    budget: Budget,
    categoryName: String,
    walletName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    currencyConverterViewModel: CurrencyConverterViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Currency states
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsState()
    
    // Convert amounts from VND (database) to current currency preference
    val currentSpendingDisplay = remember(budget.currentSpending, isVND, exchangeRates) {
        if (isVND) {
            budget.currentSpending
        } else {
            val rate = exchangeRates?.usdToVnd ?: 24000.0
            CurrencyUtils.vndToUsd(budget.currentSpending, rate)
        }
    }
    
    val limitAmountDisplay = remember(budget.limitAmount, isVND, exchangeRates) {
        if (isVND) {
            budget.limitAmount
        } else {
            val rate = exchangeRates?.usdToVnd ?: 24000.0
            CurrencyUtils.vndToUsd(budget.limitAmount, rate)
        }
    }
    
    val progress = (budget.currentSpending / budget.limitAmount).toFloat().coerceIn(0f, 1f)
    val daysRemaining = DateUtils.getDaysRemaining(parseDateTime(budget.endDate))
    val isOverdue = DateUtils.isOverdue(parseDateTime(budget.endDate))
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = BudgetTheme.ShadowColor,
                spotColor = BudgetTheme.ShadowColor
            )
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BudgetTheme.CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Title and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {                        
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = BudgetTheme.PrimaryGreenLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = budget.description,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BudgetTheme.TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    // Status Badge - Using backend status
                    StatusBadge(
                        progressStatus = budget.progressStatus
                    )
                }
                
                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = BudgetTheme.PrimaryGreenLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_budget),
                            tint = BudgetTheme.DangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
              // Notification Alert
            budget.notification?.let { notification ->
                BudgetNotificationAlert(
                    message = notification,
                    progressStatus = budget.progressStatus,
                    currencyConverterViewModel = currencyConverterViewModel
                )
            }
            
            // Progress Section
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Amount Information
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.current_spending),
                            style = MaterialTheme.typography.bodySmall,
                            color = BudgetTheme.TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = CurrencyUtils.formatAmount(currentSpendingDisplay, isVND),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                progress < 0.7f -> BudgetTheme.SuccessGreen
                                progress < 0.9f -> BudgetTheme.WarningOrange
                                else -> BudgetTheme.DangerRed
                            },
                            fontSize = 16.sp
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.limit_label),
                            style = MaterialTheme.typography.bodySmall,
                            color = BudgetTheme.TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = CurrencyUtils.formatAmount(limitAmountDisplay, isVND),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BudgetTheme.TextPrimary,
                            fontSize = 16.sp
                        )
                    }
                }
                
                // Progress Bar
                BudgetProgressIndicator(
                    progress = progress,
                    showPercentage = true
                )
            }
            
            // Details Section
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoRow(
                    icon = Icons.Outlined.Category,
                    label = stringResource(R.string.category_label),
                    value = categoryName
                )
                
                InfoRow(
                    icon = Icons.Outlined.Wallet,
                    label = stringResource(R.string.wallet_label),
                    value = walletName
                )
                  InfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = stringResource(R.string.deadline_label),
                    value = DateUtils.getDaysRemainingText(context, parseDateTime(budget.endDate)),
                    valueColor = if (isOverdue) BudgetTheme.DangerRed 
                               else if (daysRemaining <= 7) BudgetTheme.WarningOrange
                               else BudgetTheme.TextSecondary
                )
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        BudgetDeleteDialog(
            budgetDescription = budget.description,
            onConfirm = {
                onDelete()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun StatusBadge(
    progressStatus: String
) {
    val context = LocalContext.current
    
    val (statusText, statusColor, backgroundColor) = when (progressStatus.lowercase()) {
        "not started" -> Triple(
            stringResource(R.string.budget_status_not_started), 
            BudgetTheme.White, 
            BudgetTheme.SecondaryGreen
        )
        "on track" -> Triple(
            stringResource(R.string.budget_status_on_track), 
            BudgetTheme.White, 
            BudgetTheme.SuccessGreen
        )
        "under budget" -> Triple(
            stringResource(R.string.budget_status_under_budget), 
            BudgetTheme.White, 
            BudgetTheme.PrimaryGreenLight
        )
        "minimal spending" -> Triple(
            stringResource(R.string.budget_status_minimal_spending), 
            BudgetTheme.White, 
            BudgetTheme.AccentGreen
        )
        "warning" -> Triple(
            stringResource(R.string.budget_status_warning), 
            BudgetTheme.White, 
            BudgetTheme.WarningOrange
        )
        "critical" -> Triple(
            stringResource(R.string.budget_status_critical), 
            BudgetTheme.White, 
            BudgetTheme.DangerRed
        )
        "over budget" -> Triple(
            stringResource(R.string.budget_status_over_budget), 
            BudgetTheme.White, 
            BudgetTheme.DangerRed
        )
        "nearly maxed" -> Triple(
            stringResource(R.string.budget_status_nearly_maxed), 
            BudgetTheme.White, 
            BudgetTheme.WarningOrange
        )
        else -> Triple(
            stringResource(R.string.budget_status_unknown), 
            BudgetTheme.White, 
            BudgetTheme.TextTertiary
        )
    }
    
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodySmall,
            color = statusColor,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = BudgetTheme.TextSecondary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = BudgetTheme.SecondaryGreen,
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = BudgetTheme.TextTertiary,
            modifier = Modifier.width(80.dp),
            fontSize = 12.sp
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = valueColor,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BudgetNotificationAlert(
    message: String,
    progressStatus: String,
    modifier: Modifier = Modifier,
    currencyConverterViewModel: CurrencyConverterViewModel
) {
    val context = LocalContext.current
    
    // Currency states
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsState()
    
    // Use translated message with currency conversion
    val translatedMessage = useTranslatedMessage(
        message = message,
        isVND = isVND,
        exchangeRates = exchangeRates
    )
      // Determine notification style based on backend status
    val (backgroundColor, iconColor, textColor, icon) = when (progressStatus.lowercase()) {
        "not started" -> Tuple4(
            BudgetTheme.SecondaryGreen.copy(alpha = 0.1f),
            BudgetTheme.SecondaryGreen,
            BudgetTheme.SecondaryGreen,
            Icons.Default.Info
        )
        "on track" -> Tuple4(
            BudgetTheme.PrimaryGreenLight.copy(alpha = 0.1f),
            BudgetTheme.PrimaryGreen,
            BudgetTheme.PrimaryGreen,
            Icons.Default.CheckCircle
        )
        "under budget" -> Tuple4(
            BudgetTheme.SuccessGreen.copy(alpha = 0.1f),
            BudgetTheme.SuccessGreen,
            BudgetTheme.SuccessGreen,
            Icons.Default.CheckCircle
        )
        "minimal spending" -> Tuple4(
            BudgetTheme.AccentGreen.copy(alpha = 0.1f),
            BudgetTheme.AccentGreen,
            BudgetTheme.AccentGreen,
            Icons.Default.CheckCircle
        )
        "warning" -> Tuple4(
            BudgetTheme.WarningOrange.copy(alpha = 0.1f),
            BudgetTheme.WarningOrange,
            BudgetTheme.WarningOrange,
            Icons.Default.Warning
        )
        "critical" -> Tuple4(
            BudgetTheme.DangerRed.copy(alpha = 0.1f),
            BudgetTheme.DangerRed,
            BudgetTheme.DangerRed,
            Icons.Default.Warning
        )
        "over budget" -> Tuple4(
            BudgetTheme.DangerRed.copy(alpha = 0.1f),
            BudgetTheme.DangerRed,
            BudgetTheme.DangerRed,
            Icons.Default.Warning
        )
        "nearly maxed" -> Tuple4(
            BudgetTheme.WarningOrange.copy(alpha = 0.1f),
            BudgetTheme.WarningOrange,
            BudgetTheme.WarningOrange,
            Icons.Default.Warning
        )
        else -> Tuple4(
            BudgetTheme.SecondaryGreen.copy(alpha = 0.1f),
            BudgetTheme.SecondaryGreen,
            BudgetTheme.SecondaryGreen,
            Icons.Default.Info
        )
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, iconColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
              Text(
                text = translatedMessage,
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                lineHeight = 16.sp,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Helper data class for multiple return values
private data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

// Helper function to parse date string to LocalDateTime
private fun parseDateTime(dateString: String): LocalDateTime {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        LocalDateTime.parse(dateString, formatter)
    } catch (e: Exception) {
        LocalDateTime.now()
    }
}

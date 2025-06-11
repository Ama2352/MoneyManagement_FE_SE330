package DI.Composables.SavingGoalUI.components

import DI.ViewModels.CurrencyConverterViewModel
import DI.Composables.SavingGoalUI.theme.SavingGoalTheme
import DI.Utils.DateUtils
import DI.Utils.rememberAppStrings
import DI.Models.SavingGoal.SavingGoal
import DI.Utils.CurrencyUtils
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingGoalCard(
    savingGoal: SavingGoal,
    categoryName: String,
    walletName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    currencyConverterViewModel: CurrencyConverterViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val strings = rememberAppStrings()
    
    // Currency states
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsState()
    
    // Convert amounts from VND (database) to current currency preference
    val savedAmountDisplay = remember(savingGoal.savedAmount, isVND, exchangeRates) {
        if (isVND) {
            savingGoal.savedAmount.toDouble()
        } else {
            val rate = exchangeRates?.usdToVnd ?: 24000.0
            CurrencyUtils.vndToUsd(savingGoal.savedAmount.toDouble(), rate)
        }
    }
    
    val targetAmountDisplay = remember(savingGoal.targetAmount, isVND, exchangeRates) {
        if (isVND) {
            savingGoal.targetAmount.toDouble()
        } else {
            val rate = exchangeRates?.usdToVnd ?: 24000.0
            CurrencyUtils.vndToUsd(savingGoal.targetAmount.toDouble(), rate)
        }
    }
    val progress = CurrencyUtils.calculatePercentage(savingGoal.savedAmount, savingGoal.targetAmount)
    // Keep using legacy function for internal logic (days remaining calculation for colors)
    val daysRemaining = DateUtils.getDaysRemaining(savingGoal.getEndDateAsLocalDateTime())
    val isOverdue = DateUtils.isOverdue(savingGoal.getEndDateAsLocalDateTime())
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = SavingGoalTheme.ShadowColor,
                spotColor = SavingGoalTheme.ShadowColor
            )
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SavingGoalTheme.CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {            // Header with Title and Actions
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
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = SavingGoalTheme.PrimaryGreenLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = savingGoal.description,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SavingGoalTheme.TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                      // Status Badge - Using backend status
                    StatusBadge(
                        progressStatus = savingGoal.progressStatus
                    )
                }
                
                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Chỉnh sửa",
                            tint = SavingGoalTheme.PrimaryGreenLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = SavingGoalTheme.DangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }            // Notification Alert (NEW FEATURE)
            savingGoal.notification?.let { notification ->
                NotificationAlert(
                    message = notification,
                    progressStatus = savingGoal.progressStatus,
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
                            text = "Đã tiết kiệm",
                            style = MaterialTheme.typography.bodySmall,
                            color = SavingGoalTheme.TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = CurrencyUtils.formatAmount(savedAmountDisplay, isVND),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SavingGoalTheme.PrimaryGreen,
                            fontSize = 16.sp
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Mục tiêu",
                            style = MaterialTheme.typography.bodySmall,
                            color = SavingGoalTheme.TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = CurrencyUtils.formatAmount(targetAmountDisplay, isVND),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SavingGoalTheme.TextPrimary,
                            fontSize = 16.sp
                        )
                    }
                }
                
                // Progress Bar
                SavingGoalProgressIndicator(
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
                    label = "Danh mục",
                    value = categoryName
                )
                
                InfoRow(
                    icon = Icons.Outlined.Wallet,
                    label = "Ví",
                    value = walletName
                )
                  InfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Thời hạn",
                    value = DateUtils.getDaysRemainingText(savingGoal.getEndDateAsLocalDateTime(), strings),
                    valueColor = if (isOverdue) SavingGoalTheme.DangerRed 
                               else if (daysRemaining <= 7) SavingGoalTheme.WarningOrange
                               else SavingGoalTheme.TextSecondary
                )
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        SavingGoalDeleteDialog(
            savingGoalDescription = savingGoal.description,
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
    val (statusText, statusColor, backgroundColor) = when (progressStatus.lowercase()) {
        "not started" -> Triple("Chưa bắt đầu", SavingGoalTheme.White, SavingGoalTheme.SecondaryGreen)
        "achieved" -> Triple("Đã đạt được", SavingGoalTheme.White, SavingGoalTheme.SuccessGreen)
        "achieved early" -> Triple("Đạt sớm", SavingGoalTheme.White, SavingGoalTheme.SuccessGreen)
        "partially achieved" -> Triple("Gần đạt", SavingGoalTheme.White, SavingGoalTheme.PrimaryGreenLight)
        "missed target" -> Triple("Lỡ mục tiêu", SavingGoalTheme.White, SavingGoalTheme.DangerRed)
        "ahead" -> Triple("Vượt tiến độ", SavingGoalTheme.White, SavingGoalTheme.SuccessGreen)
        "on track" -> Triple("Đúng tiến độ", SavingGoalTheme.White, SavingGoalTheme.PrimaryGreenLight)
        "slightly behind" -> Triple("Hơi chậm", SavingGoalTheme.White, SavingGoalTheme.WarningOrange)
        "at risk" -> Triple("Rủi ro cao", SavingGoalTheme.White, SavingGoalTheme.DangerRed)
        else -> Triple("Không xác định", SavingGoalTheme.PrimaryGreen, SavingGoalTheme.SecondaryGreenLight)
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
    valueColor: Color = SavingGoalTheme.TextSecondary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SavingGoalTheme.SecondaryGreen,
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SavingGoalTheme.TextTertiary,
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
private fun NotificationAlert(
    message: String,
    progressStatus: String,
    modifier: Modifier = Modifier,
    currencyConverterViewModel: CurrencyConverterViewModel
) {
    // Currency states
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsState()
    
    // Format message with correct currency
    val formattedMessage = remember(message, isVND, exchangeRates) {
        formatNotificationMessage(message, isVND, exchangeRates)
    }
    // Determine notification style based on backend status
    val (backgroundColor, iconColor, textColor, icon) = when (progressStatus.lowercase()) {
        "not started" -> Tuple4(
            SavingGoalTheme.SecondaryGreen.copy(alpha = 0.1f),
            SavingGoalTheme.SecondaryGreen,
            SavingGoalTheme.SecondaryGreen,
            Icons.Default.Info
        )
        "achieved", "achieved early" -> Tuple4(
            SavingGoalTheme.SuccessGreen.copy(alpha = 0.1f),
            SavingGoalTheme.SuccessGreen,
            SavingGoalTheme.SuccessGreen,
            Icons.Default.CheckCircle
        )
        "partially achieved" -> Tuple4(
            SavingGoalTheme.PrimaryGreenLight.copy(alpha = 0.1f),
            SavingGoalTheme.PrimaryGreen,
            SavingGoalTheme.PrimaryGreen,
            Icons.Default.CheckCircle
        )
        "missed target", "at risk" -> Tuple4(
            SavingGoalTheme.DangerRed.copy(alpha = 0.1f),
            SavingGoalTheme.DangerRed,
            SavingGoalTheme.DangerRed,
            Icons.Default.Warning
        )
        "ahead" -> Tuple4(
            SavingGoalTheme.SuccessGreen.copy(alpha = 0.1f),
            SavingGoalTheme.SuccessGreen,
            SavingGoalTheme.SuccessGreen,
            Icons.Default.CheckCircle
        )
        "on track" -> Tuple4(
            SavingGoalTheme.PrimaryGreenLight.copy(alpha = 0.1f),
            SavingGoalTheme.PrimaryGreen,
            SavingGoalTheme.PrimaryGreen,
            Icons.Default.Notifications
        )
        "slightly behind" -> Tuple4(
            SavingGoalTheme.WarningOrange.copy(alpha = 0.1f),
            SavingGoalTheme.WarningOrange,
            SavingGoalTheme.WarningOrange,
            Icons.Default.Warning
        )
        else -> Tuple4(
            SavingGoalTheme.PrimaryGreenLight.copy(alpha = 0.1f),
            SavingGoalTheme.PrimaryGreen,
            SavingGoalTheme.PrimaryGreen,
            Icons.Default.Notifications
        )
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(1.dp, iconColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
              Text(
                text = formattedMessage,
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp,
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

// Helper function to format currency amounts in notification messages
private fun formatNotificationMessage(
    message: String,
    isVND: Boolean,
    exchangeRates: DI.Models.Currency.CurrencyRates?
): String {
    // Regex to find currency amounts like $1000.00, $1,000.00, etc.
    val currencyRegex = """\$([0-9,]+\.?\d*)""".toRegex()
    
    return currencyRegex.replace(message) { matchResult ->
        val amountStr = matchResult.groupValues[1].replace(",", "")
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        
        // Convert and format the amount based on user preference
        val convertedAmount = if (isVND) {
            amount // Amount is already in VND from backend
        } else {
            // Convert VND to USD
            val rate = exchangeRates?.usdToVnd ?: 24000.0
            CurrencyUtils.vndToUsd(amount, rate)
        }
        
        CurrencyUtils.formatAmount(convertedAmount, isVND)
    }
}

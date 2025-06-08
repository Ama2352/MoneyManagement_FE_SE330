package DI.Composables.TransactionSection

import DI.Models.Transaction.TransactionDetail
import DI.Models.Transaction.Transaction
import DI.Models.Category.Category
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.CategoryViewModel
import DI.Utils.CurrencyUtils
import DI.Composables.CategorySection.getCategoryIcon
import DI.ViewModels.WalletViewModel
import DI.Utils.rememberAppStrings
import DI.Utils.TransactionUtils
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import com.example.moneymanagement_frontend.R

// Color scheme for transaction screen
object TransactionColors {
    val Primary = Color(0xFF10B981) // Emerald-500
    val PrimaryVariant = Color(0xFF059669) // Emerald-600
    val Secondary = Color(0xFF34D399) // Emerald-400
    val Background = Color(0xFFF0FDF4) // Green-50
    val Surface = Color(0xFFFFFFFF)
    val OnPrimary = Color.White
    val OnSurface = Color(0xFF1F2937) // Gray-800
    val OnSurfaceVariant = Color(0xFF6B7280) // Gray-500
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)
    val Income = Color(0xFF059669) // Green for income
    val Expense = Color(0xFFDC2626) // Red for expense
    val Transfer = Color(0xFF3B82F6) // Blue for transfer
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTransactionsScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    transactionViewModel: TransactionViewModel,
    currencyViewModel: CurrencyConverterViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel
) {
    val strings = rememberAppStrings()

    // Collect states from ViewModels
    val transactions by transactionViewModel.transactions
    val transactionDetails by transactionViewModel.transactionDetails
    val isLoading by transactionViewModel.isLoading
    val errorMessage by transactionViewModel.errorMessage
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()
    val wallets by walletViewModel.wallets.collectAsStateWithLifecycle()
    val isVND by currencyViewModel.isVND.collectAsStateWithLifecycle()

    // Create a map of categoryID to Category for quick lookup
    val categoryMap = remember(categories) {
        categories?.getOrNull()?.associateBy { it.categoryID } ?: emptyMap()
    }

    // Convert Transaction objects to TransactionDetail objects for display
    val displayTransactions = remember(transactions, categoryMap) {
        transactions.map { transaction ->
            val walletName = wallets?.getOrNull()?.find { it.walletID == transaction.walletID }?.walletName ?: strings.unknownWallet
            convertTransactionToDetail(transaction, categoryMap, walletName, strings.unknown)
        }
    }
    // Pagination state
    var displayedItemCount by remember { mutableStateOf(8) }
    val itemsPerPage = 8

    // Filter state
    var selectedFilter by remember { mutableStateOf(strings.filterAllValue) }

    // Use transactionDetails if available (from search/date range), otherwise use converted transactions
    val baseTransactions = if (transactionDetails.isNotEmpty()) {
        transactionDetails
    } else {
        displayTransactions
    }
    Log.d("TransactionScreen", "Base transactions: $baseTransactions")

    // Apply filter to transactions
    val finalTransactions = remember(baseTransactions, selectedFilter) {
        Log.d("TransactionScreen", "Applying filter: $selectedFilter, $baseTransactions")
        when (selectedFilter) {
            strings.filterAllValue -> baseTransactions
            strings.filterIncomeValue -> baseTransactions.filter { it.type == "Income" }
            strings.filterExpenseValue -> baseTransactions.filter { it.type == "Expense" }
            else -> baseTransactions
        }
    }
    Log.d("TransactionScreen", "Final transactions after filter: $finalTransactions")

    // Reset pagination when transactions change
    LaunchedEffect(finalTransactions.size) {
        displayedItemCount = minOf(itemsPerPage, finalTransactions.size)
    }

    // Refresh functionality
    val scope = rememberCoroutineScope()

    // Load data on first composition
    LaunchedEffect(Unit) {
        transactionViewModel.loadAllTransactions()
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TransactionColors.Background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header with gradient background
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                TransactionColors.Primary,
                                TransactionColors.PrimaryVariant
                            )
                        )
                    )
                    .padding(top = 24.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.transactions,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = strings.trackYourMoneyFlow,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Refresh button
                    IconButton(
                        onClick = {
                            scope.launch {
                                transactionViewModel.loadAllTransactions()
                                categoryViewModel.getCategories()
                                walletViewModel.getWallets()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = strings.refresh,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Error handling
        errorMessage?.let { error ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TransactionColors.Error.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = strings.error,
                            tint = TransactionColors.Error,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = error,
                            color = TransactionColors.Error,
                            modifier = Modifier.padding(start = 12.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        if (isLoading) {
            // Loading state
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = TransactionColors.Primary
                        )

                        Text(
                            text = strings.loadingTransactions,
                            modifier = Modifier.padding(top = 16.dp),
                            color = TransactionColors.OnSurfaceVariant
                        )
                    }
                }
            }
        } else if (finalTransactions.isEmpty() && errorMessage == null) {
            // Empty state
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = strings.noTransactionsFound,
                            modifier = Modifier.size(64.dp),
                            tint = TransactionColors.OnSurfaceVariant
                        )
                        Text(
                            text = strings.noTransactionsYet,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = TransactionColors.OnSurface,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = strings.startAddingTransactions,
                            fontSize = 14.sp,
                            color = TransactionColors.OnSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Button(
                            onClick = onNavigateToAdd,
                            modifier = Modifier.padding(top = 24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TransactionColors.Primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = strings.add,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.addFirstTransaction)
                        }
                    }
                }
            }
        } else {
            // Summary Cards
            item {
                TransactionSummarySection(displayTransactions, isVND)
            }

            // Filter Section
            item {
                FilterSection(
                    onNavigateToSearch = onNavigateToSearch,
                    onNavigateToAdd = onNavigateToAdd,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter ->
                        selectedFilter = filter
                        // Reset pagination when filter changes
                        displayedItemCount = minOf(itemsPerPage, finalTransactions.size)
                    }
                )
            }

            // Transaction List Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.recentTransactionsCount.format(minOf(displayedItemCount, finalTransactions.size), finalTransactions.size),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TransactionColors.OnSurface
                    )
                }
            }

            // Transaction Items
            items(finalTransactions.take(displayedItemCount)) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    isVND = isVND,
                    onTransactionClick = { onNavigateToDetail(transaction.transactionID) }
                )
            }

            // Show More button (only if there are more items to show)
            if (displayedItemCount < finalTransactions.size) {
                item {
                    Button(
                        onClick = {
                            displayedItemCount = minOf(
                                displayedItemCount + itemsPerPage,
                                finalTransactions.size
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TransactionColors.Primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = strings.more,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.showMore.format(minOf(itemsPerPage, finalTransactions.size - displayedItemCount)))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionSummarySection(transactions: List<TransactionDetail>, isVND: Boolean) {
    val strings = rememberAppStrings()
    val totalIncome = transactions.filter { it.type.equals("Income", ignoreCase = true) }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type.equals("Expense", ignoreCase = true) }.sumOf { it.amount }
    val balance = totalIncome - totalExpense
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = TransactionColors.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = strings.totalSummary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TransactionColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    title = strings.income,
                    amount = totalIncome,
                    color = TransactionColors.Income,
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    isVND = isVND
                )
                SummaryItem(
                    title = strings.expense,
                    amount = totalExpense,
                    color = TransactionColors.Expense,
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    isVND = isVND
                )
                SummaryItem(
                    title = strings.balance,
                    amount = balance,
                    color = if (balance >= 0) TransactionColors.Income else TransactionColors.Expense,
                    icon = Icons.Default.AccountBalance,
                    isVND = isVND
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    amount: Double,
    color: Color,
    icon: ImageVector,
    isVND: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Text(
            text = title,
            fontSize = 12.sp,
            color = TransactionColors.OnSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
          Text(
            text = if (isVND) CurrencyUtils.formatVND(amount) else CurrencyUtils.formatAmount(amount, isVND),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun FilterSection(
    onNavigateToSearch: () -> Unit,
    onNavigateToAdd: () -> Unit,
    selectedFilter: String = "",
    onFilterSelected: (String) -> Unit
) {
    val strings = rememberAppStrings()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Filter chips row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                text = strings.all,
                isSelected = selectedFilter == strings.filterAllValue,
                onClick = { onFilterSelected(strings.filterAllValue) }
            )
            FilterChip(
                text = strings.income,
                isSelected = selectedFilter == strings.filterIncomeValue,
                onClick = { onFilterSelected(strings.filterIncomeValue) }
            )
            FilterChip(
                text = strings.expense,
                isSelected = selectedFilter == strings.filterExpenseValue,
                onClick = { onFilterSelected(strings.filterExpenseValue) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search button
            OutlinedButton(
                onClick = onNavigateToSearch,
                modifier = Modifier
                    .padding(top = 12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TransactionColors.Primary
                ),
                border = BorderStroke(1.dp, TransactionColors.Primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = strings.searchTransactionsDesc,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.search)
            }

            // Add button
            OutlinedButton(
                onClick = onNavigateToAdd,
                modifier = Modifier
                    .padding(top = 12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TransactionColors.Primary
                ),
                border = BorderStroke(1.dp, TransactionColors.Primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = strings.addTransactionsDesc,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.add)
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(20.dp)),
        color = if (isSelected) TransactionColors.Primary else TransactionColors.Surface,
        contentColor = if (isSelected) Color.White else TransactionColors.OnSurface,
        tonalElevation = if (isSelected) 0.dp else 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
@Composable
fun TransactionCard(
    transaction: TransactionDetail,
    isVND: Boolean = false,
    onTransactionClick: () -> Unit = {}
) {
    val strings = rememberAppStrings()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTransactionClick() }
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = TransactionColors.Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top row with icon, description, and time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Transaction Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = TransactionUtils.getTransactionColor(transaction.type).copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(transaction.category),
                        contentDescription = transaction.category,
                        tint = TransactionUtils.getTransactionColor(transaction.type),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Transaction Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = transaction.description,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TransactionColors.OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = transaction.walletName,
                        fontSize = 12.sp,
                        color = TransactionColors.OnSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Time (compact)
                Text(
                    text = transaction.time,
                    fontSize = 12.sp,
                    color = TransactionColors.OnSurfaceVariant,
                    textAlign = TextAlign.End
                )
            }
            
            // Bottom row with date and amount (full width)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${transaction.dayOfWeek}, ${transaction.date}",
                    fontSize = 12.sp,
                )                
                  Text(
                    text = "${if (transaction.type.equals("Expense", ignoreCase = true)) "-" else "+"}${if (isVND) CurrencyUtils.formatVND(transaction.amount) else CurrencyUtils.formatAmount(transaction.amount, isVND)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TransactionUtils.getTransactionColor(transaction.type),
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

fun convertTransactionToDetail(transaction: Transaction, categoryMap: Map<String, Category>, walletName: String, unknownText: String): TransactionDetail {
    val category = categoryMap[transaction.categoryID]
    
    // Parse the transaction date
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val date = try {
        dateFormat.parse(transaction.transactionDate)
    } catch (e: Exception) {
        // Fallback to current date if parsing fails
        Date()
    }
    
    val dayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
    val monthFormatter = SimpleDateFormat("MMMM", Locale.getDefault())
    val dateDisplayFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    
    return TransactionDetail(
        transactionID = transaction.transactionID,
        transactionDate = transaction.transactionDate,
        date = dateDisplayFormatter.format(date),
        time = timeFormatter.format(date),
        dayOfWeek = dayFormatter.format(date),
        month = monthFormatter.format(date),
        amount = transaction.amount,
        type = transaction.type,
        category = category?.name ?: unknownText,
        categoryID = transaction.categoryID,
        description = transaction.description,
        walletID = transaction.walletID,
        walletName = walletName
    )
}
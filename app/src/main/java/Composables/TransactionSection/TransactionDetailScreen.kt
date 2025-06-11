package Composables.TransactionSection

import DI.Composables.CategorySection.getCategoryIcon
import DI.Models.Category.Category
import DI.Models.Currency.CurrencyRates
import DI.Models.Transaction.Transaction
import DI.Models.Wallet.Wallet
import DI.Utils.AppStrings
import DI.Utils.CurrencyUtils
import DI.Utils.TransactionType
import DI.Utils.TransactionUtils
import DI.Utils.rememberAppStrings
import DI.ViewModels.BudgetViewModel
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.CurrencyConverterViewModel
import DI.ViewModels.SavingGoalViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Transaction detail screen colors using the same scheme as TransactionScreen
object TransactionDetailColors {
    val Primary = Color(0xFF10B981) // Emerald-500
    val PrimaryVariant = Color(0xFF059669) // Emerald-600
    val Background = Color(0xFFF0FDF4) // Green-50
    val Surface = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF1F2937) // Gray-800
    val OnSurfaceVariant = Color(0xFF6B7280) // Gray-500
    val Success = Color(0xFF10B981)
    val Error = Color(0xFFEF4444)
    val Income = Color(0xFF059669) // Green for income
    val Expense = Color(0xFFDC2626) // Red for expense
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel,
    currencyConverterViewModel: CurrencyConverterViewModel,
    savingGoalViewModel: SavingGoalViewModel,
    budgetViewModel: BudgetViewModel
) {
    val strings = rememberAppStrings()
    val context = LocalContext.current
    val savingGoals by savingGoalViewModel.savingGoalProgress.collectAsState()
    val budgets by budgetViewModel.budgets.collectAsState()

    val selectedCategory by remember { mutableStateOf<Category?>(null) }
    val selectedWallet by remember { mutableStateOf<Wallet?>(null) }
    val transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }

    // Collect states
    val transaction by transactionViewModel.selectedTransaction.collectAsStateWithLifecycle()
    val isLoading by transactionViewModel.isLoading.collectAsStateWithLifecycle()
    val isDeleting by transactionViewModel.isDeleting.collectAsStateWithLifecycle()
    val errorMessage by transactionViewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by transactionViewModel.successMessage.collectAsStateWithLifecycle()
    val categories =
        categoryViewModel.categories.collectAsStateWithLifecycle().value?.getOrNull() ?: emptyList()
    val wallets =
        walletViewModel.wallets.collectAsStateWithLifecycle().value?.getOrNull() ?: emptyList()
    val isVND by currencyConverterViewModel.isVND.collectAsStateWithLifecycle()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsStateWithLifecycle()

    // Dialog states
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load transaction data when screen opens
    LaunchedEffect(transactionId) {
        transactionViewModel.loadTransactionById(transactionId)
    }    // Handle success/error messages
    LaunchedEffect(successMessage) {
        successMessage?.let {
            if (it.contains("deleted", ignoreCase = true)) {
                // Show success toast for transaction deletion using localized string
                Toast.makeText(context, strings.deleteTransactionSuccess, Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }
    }

    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            // Show error toast for transaction deletion using localized string
            Toast.makeText(context, strings.deleteTransactionError, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.transactionDetails,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back
                        )
                    }
                },
                actions = {
                    // Edit button
                    IconButton(
                        onClick = {
                            navController.navigate("transaction_edit/$transactionId")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = strings.edit,
                            tint = TransactionDetailColors.Primary
                        )
                    }
                    // Delete button
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = strings.deleteTransaction,
                            tint = TransactionDetailColors.Error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TransactionDetailColors.Background,
                    titleContentColor = TransactionDetailColors.OnSurface
                )
            )
        },
        containerColor = TransactionDetailColors.Background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = TransactionDetailColors.Primary
                            )
                            Text(
                                text = strings.loadingTransactions,
                                color = TransactionDetailColors.OnSurfaceVariant
                            )
                        }
                    }
                }

                errorMessage != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = TransactionDetailColors.Error.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = strings.error,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = TransactionDetailColors.Error
                            )
                            Text(
                                text = errorMessage ?: strings.unknown,
                                fontSize = 14.sp,
                                color = TransactionDetailColors.OnSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TransactionDetailColors.Primary
                                )
                            ) {
                                Text(strings.back)
                            }
                        }
                    }
                }

                transaction != null -> {
                    TransactionDetailContent(
                        transaction = transaction!!,
                        categories = categories,
                        wallets = wallets,
                        isVND = isVND,
                        exchangeRates = exchangeRates,
                        strings = strings
                    )
                }

                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = TransactionDetailColors.Surface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.transaction_not_found),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TransactionDetailColors.OnSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TransactionDetailColors.Primary
                                )
                            ) {
                                Text(strings.back)
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        TransactionDeleteDialog(
            onConfirm = {
                transactionViewModel.deleteTransaction(transactionId)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
            strings = strings,
            isDeleting = isDeleting
        )
    }
}

@Composable
private fun TransactionDetailContent(
    transaction: Transaction,
    categories: List<Category>,
    wallets: List<Wallet>,
    isVND: Boolean,
    exchangeRates: CurrencyRates?,
    strings: AppStrings
) {
    // Find category and wallet details
    val category = categories.find { it.categoryID == transaction.categoryID }
    val wallet = wallets.find { it.walletID == transaction.walletID }

    // Parse and format the transaction date
    val formattedDate = remember(transaction.transactionDate) {
        try {
            val possibleFormats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
            )

            var parsedDate: Date? = null
            for (formatPattern in possibleFormats) {
                try {
                    val formatter = SimpleDateFormat(formatPattern, Locale.US)
                    formatter.isLenient = false
                    parsedDate = formatter.parse(transaction.transactionDate)
                    break
                } catch (e: Exception) {
                    continue
                }
            }

            if (parsedDate != null) {
                val displayFormatter =
                    SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
                displayFormatter.format(parsedDate)
            } else {
                transaction.transactionDate
            }
        } catch (e: Exception) {
            transaction.transactionDate
        }
    }

    // Transaction header with icon and amount
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TransactionDetailColors.Surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Transaction icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = TransactionUtils.getTransactionColor(transaction.type)
                            .copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category?.name ?: "Other"),
                    contentDescription = category?.name,
                    tint = TransactionUtils.getTransactionColor(transaction.type),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount
            Text(
                text = "${if (transaction.type.equals("Expense", ignoreCase = true)) "-" else "+"}${
                    if (isVND) {
                        CurrencyUtils.formatVND(transaction.amount)
                    } else {
                        // Convert VND (stored in backend) to USD for display
                        val usdAmount = if (exchangeRates != null) {
                            CurrencyUtils.vndToUsd(transaction.amount, exchangeRates.usdToVnd)
                        } else {
                            transaction.amount // Fallback if no exchange rate
                        }
                        CurrencyUtils.formatUSD(usdAmount)
                    }
                }",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TransactionUtils.getTransactionColor(transaction.type)
            )

            // Transaction type
            Text(
                text = transaction.type,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TransactionDetailColors.OnSurfaceVariant
            )
        }
    }

    // Transaction information
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = TransactionDetailColors.Surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = strings.transactionInfo,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TransactionDetailColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Description
            DetailRow(
                label = strings.description,
                value = transaction.description.ifEmpty { strings.noDescriptionProvided },
            )

            // Category
            DetailRow(
                label = strings.category,
                value = category?.name ?: strings.unknown,
            )

            // Wallet
            DetailRow(
                label = strings.wallet,
                value = wallet?.walletName ?: strings.unknown,
            )

            // Date
            DetailRow(
                label = strings.createdOn,
                value = formattedDate,
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TransactionDetailColors.OnSurfaceVariant,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = TransactionDetailColors.OnSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TransactionDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    strings: AppStrings,
    isDeleting: Boolean
) {
    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = {
            Text(
                text = strings.deleteTransaction,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = strings.deleteTransactionConfirmation,
                color = TransactionDetailColors.OnSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TransactionDetailColors.Error
                )
            ) {
                if (isDeleting) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Text(stringResource(R.string.deleting))
                    }
                } else {
                    Text(strings.delete)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDeleting
            ) {
                Text(
                    text = strings.cancel,
                    color = TransactionDetailColors.OnSurfaceVariant
                )
            }
        },
        containerColor = TransactionDetailColors.Surface,
        titleContentColor = TransactionDetailColors.OnSurface,
        textContentColor = TransactionDetailColors.OnSurfaceVariant
    )
}

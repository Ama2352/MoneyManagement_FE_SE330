package DI.Composables.TransactionSection

import DI.Models.Category.Category
import DI.Models.Category.Transaction
import DI.Models.Wallet.Wallet
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R
import java.util.Calendar
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale


@Composable
fun TransactionEditScreen(
    navController: NavController,
    transactionId: String,
    viewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    walletViewModel: WalletViewModel
) {
    val selectedTransaction by viewModel.selectedTransaction
    val categories by categoryViewModel.categories.collectAsState()
    val wallets by walletViewModel.wallets.collectAsState()
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
        viewModel.loadTransactionById(transactionId) {
            isLoaded = it
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFA5DABC),
                        Color(0xFF87EAAF),
                        Color(0xFF00BE7C).copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        if (isLoaded && selectedTransaction != null && categories != null && wallets != null) {
            val categoryList = categories?.getOrNull() ?: emptyList()
            val walletList = wallets?.getOrNull() ?: emptyList()

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TransactionEditHeader(navController = navController)
                TransactionEditBody(
                    transaction = selectedTransaction!!,
                    categoryList = categoryList,
                    walletList = walletList,
                    viewModel = viewModel,
                    navController = navController
                )
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun TransactionEditHeader(
    navController: NavController,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = CircleShape
                    )
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Color(0xFF2D3748),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Edit Transaction",
                color = Color(0xFF2D3748),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            // Spacer to balance the layout
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
fun TransactionEditBody(
    transaction: Transaction,
    categoryList: List<Category>,
    walletList: List<Wallet>,
    viewModel: TransactionViewModel,
    navController: NavController
) {
    val dateFormatStorage = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }

    val dateFormatDisplay = remember {
        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    }

    val selectedDateTime = remember {
        mutableStateOf<Calendar?>(
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .parse(transaction.transactionDate)
                ?.let {
                    Calendar.getInstance().apply { time = it }
                }
        )
    }

    val displayDate by remember {
        derivedStateOf {
            selectedDateTime.value?.let { dateFormatDisplay.format(it.time) } ?: ""
        }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    var categoryId by remember { mutableStateOf(transaction.categoryID) }
    var categoryName by remember {
        mutableStateOf(categoryList.find { it.categoryID == transaction.categoryID }?.name ?: "")
    }

    var walletId by remember { mutableStateOf(transaction.walletID) }
    var walletName by remember {
        mutableStateOf(walletList.find { it.walletID == transaction.walletID }?.walletName ?: "")
    }

    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var title by remember { mutableStateOf(transaction.description) }
    val type by remember { mutableStateOf(transaction.type) }
    val isIncome = type.equals("income", ignoreCase = true)
    val isExpense = type.equals("expense", ignoreCase = true)
    val context = LocalContext.current

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            // Transaction Type Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Transaction Type (Unchangeable)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4A5568),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TransactionTypeButton(
                            text = "INCOME",
                            isSelected = isIncome,
                            selectedColor = Color(0xFF48BB78),
                            modifier = Modifier.weight(1f)
                        )

                        TransactionTypeButton(
                            text = "EXPENSE",
                            isSelected = isExpense,
                            selectedColor = Color(0xFFE53E3E),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            // Form Fields Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    TransactionTextField(
                        label = "Title",
                        value = title,
                        onValueChange = { title = it },
                        leadingIcon = Icons.Default.Edit,
                        placeholder = "Enter transaction title"
                    )

                    TransactionTextField(
                        label = "Amount",
                        value = amount,
                        onValueChange = { amount = it },
                        keyboardType = KeyboardType.Number,
                        leadingIcon = Icons.Default.AttachMoney,
                        placeholder = "0.00"
                    )

                    DropdownSelector(
                        label = "Category",
                        selectedName = categoryName,
                        options = categoryList.map { it.name to it.categoryID.toString() },
                        onSelect = { name, id ->
                            categoryName = name
                            categoryId = id
                        },
                        icon = Icons.Default.Category,
                        placeholder = "Select category"
                    )

                    DropdownSelector(
                        label = "Wallet",
                        selectedName = walletName,
                        options = walletList.map { it.walletName to it.walletID.toString() },
                        onSelect = { name, id ->
                            walletName = name
                            walletId = id
                        },
                        icon = Icons.Default.AccountBalanceWallet,
                        placeholder = "Select wallet"
                    )

                    TransactionTextField(
                        label = "Date & Time",
                        value = displayDate,
                        onValueChange = {},
                        isDropdown = true,
                        leadingIcon = Icons.Default.DateRange,
                        placeholder = "Select date and time",
                        trailingIcon = {
                            IconButton(onClick = { dateDialogState.show() }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Date",
                                    tint = Color(0xFF00D09E)
                                )
                            }
                        }
                    )
                }
            }
        }

        item {
            if (showError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFB71C1C),
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }

            // Save Button
            Button(
                onClick = {
                    // Validate all fields
                    when {
                        title.isBlank() -> {
                            showError = true
                            errorMessage = "Title cannot be empty"
                        }
                        amount.isBlank() || amount.toDoubleOrNull() == null -> {
                            showError = true
                            errorMessage = "Please enter a valid amount"
                        }
                        categoryId.isBlank() -> {
                            showError = true
                            errorMessage = "Please select a category"
                        }
                        walletId.isBlank() -> {
                            showError = true
                            errorMessage = "Please select a wallet"
                        }
                        selectedDateTime.value == null -> {
                            showError = true
                            errorMessage = "Please select a date and time"
                        }
                        else -> {
                            showError = false
                            val updatedTransaction = transaction.copy(
                                description = title,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                categoryID = categoryId,
                                walletID = walletId,
                                type = type,
                                transactionDate = selectedDateTime.value?.let {
                                    dateFormatStorage.format(it.time)
                                } ?: ""
                            )

                            viewModel.updateTransaction(
                                transactionID = updatedTransaction.transactionID,
                                amount = updatedTransaction.amount,
                                description = updatedTransaction.description,
                                categoryId = updatedTransaction.categoryID,
                                walletId = updatedTransaction.walletID,
                                type = updatedTransaction.type,
                                transactionDate = updatedTransaction.transactionDate
                            ) { success ->
                                if (success) {
                                    Toast.makeText(context, "Transaction updated successfully", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Failed to update transaction", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Date and Time Dialogs
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("Next") { timeDialogState.show() }
            negativeButton("Cancel")
        }
    ) {
        datepicker(
            initialDate = selectedDateTime.value?.let {
                LocalDate.of(it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1, it.get(Calendar.DAY_OF_MONTH))
            } ?: LocalDate.now(),
            title = "Select a date"
        ) { localDate ->
            val currentCal = selectedDateTime.value ?: Calendar.getInstance()
            val newCal = Calendar.getInstance().apply {
                timeInMillis = currentCal.timeInMillis
                set(Calendar.YEAR, localDate.year)
                set(Calendar.MONTH, localDate.monthValue - 1)
                set(Calendar.DAY_OF_MONTH, localDate.dayOfMonth)
            }
            selectedDateTime.value = newCal
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("OK")
            negativeButton("Cancel")
        }
    ) {
        timepicker(
            initialTime = selectedDateTime.value?.let {
                LocalTime.of(it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
            } ?: LocalTime.now(),
            title = "Select a time"
        ) { time ->
            val currentCal = selectedDateTime.value ?: Calendar.getInstance()
            val newCal = Calendar.getInstance().apply {
                timeInMillis = currentCal.timeInMillis
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
                set(Calendar.SECOND, 0)
            }
            selectedDateTime.value = newCal
        }
    }
}

@Composable
fun TransactionTypeButton(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = tween(300)
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(300)
    )

    Card(
        modifier = modifier.scale(animatedScale),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) selectedColor else Color(0xFFF7FAFC)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF718096)
            )
        }
    }
}




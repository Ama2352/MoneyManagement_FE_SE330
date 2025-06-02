package DI.Composables.TransactionSection

import DI.ViewModels.BudgetViewModel
import DI.ViewModels.CategoryViewModel
import DI.ViewModels.TransactionViewModel
import DI.ViewModels.WalletViewModel
import DI.ViewModels.OcrViewModel
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.Calendar
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import java.text.NumberFormat
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionForm(
    viewModel: TransactionViewModel,
    navController: NavController,
    type: String,
    categoryViewModel: CategoryViewModel,
    ocrViewModel: OcrViewModel,
    budgetViewModel: BudgetViewModel,
    walletViewModel: WalletViewModel
) {
    val context = LocalContext.current
    val categoriesResult by categoryViewModel.categories.collectAsState()
    val walletsResult by walletViewModel.wallets.collectAsState()

    // Validation states
    var walletError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        categoryViewModel.getCategories()
        walletViewModel.getWallets()
    }

    val selectedDateTime = remember { mutableStateOf(Calendar.getInstance()) }

    val storageFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }
    val displayFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
    }

    val displayDate by remember {
        derivedStateOf { displayFormatter.format(selectedDateTime.value.time) }
    }

    val storageDate by remember {
        derivedStateOf { storageFormatter.format(selectedDateTime.value.time) }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    var categoryId by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var walletId by remember { mutableStateOf("") }
    var walletName by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    fun formatAmount(input: String): String {
        return input.toLongOrNull()?.let {
            NumberFormat.getNumberInstance(Locale.US)
                .format(it)
                .replace(",", ".")
        } ?: input
    }

    fun unformatAmount(formatted: String): String {
        return formatted.replace(".", "").replace(",", "")
    }

    var rawAmount by remember { mutableStateOf("") }

    val formattedAmount by remember(rawAmount) {
        mutableStateOf(formatAmount(rawAmount))
    }

    val ocrResult by ocrViewModel.ocrResult.collectAsState()

    LaunchedEffect(ocrResult) {
        ocrResult?.let { result ->
            rawAmount = result.amount.toBigDecimal().toPlainString()

            walletsResult?.getOrNull()?.find { it.walletName.equals("Bank", ignoreCase = true) }?.let { wallet ->
                walletName = wallet.walletName
                walletId = wallet.walletID
            }

            // Parse the date from ocrResult and update selectedDateTime
            val ocrDate = result.date
            Log.d("TransactionForm", "OCR Date received: $ocrDate")
            try {
                // Parse ISO 8601 format
                val instant = java.time.Instant.parse(ocrDate)
                val localDateTime = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                
                // Create a new Calendar instance and set its time
                val newCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, localDateTime.year)
                    set(Calendar.MONTH, localDateTime.monthValue - 1) // Calendar months are 0-based
                    set(Calendar.DAY_OF_MONTH, localDateTime.dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, localDateTime.hour)
                    set(Calendar.MINUTE, localDateTime.minute)
                    set(Calendar.SECOND, localDateTime.second)
                }
                
                // Update the state with the new Calendar instance
                selectedDateTime.value = newCalendar
                Log.d("TransactionForm", "Updated selectedDateTime: ${selectedDateTime.value.time}")
            } catch (e: Exception) {
                Log.e("TransactionForm", "Error parsing date", e)
            }
        }
    }

    fun validateForm(): Boolean {
        var isValid = true

        if (walletId.isEmpty()) {
            walletError = "Please select a wallet"
            isValid = false
        } else {
            walletError = null
        }

        if (categoryId.isEmpty()) {
            categoryError = "Please select a category"
            isValid = false
        } else {
            categoryError = null
        }

        if (rawAmount.isEmpty()) {
            amountError = "Please enter an amount"
            isValid = false
        } else if (rawAmount.toLongOrNull() == null) {
            amountError = "Please enter a valid amount"
            isValid = false
        } else {
            amountError = null
        }

        if (title.trim().isEmpty()) {
            titleError = "Please enter a title"
            isValid = false
        } else {
            titleError = null
        }

        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wallet & Category Section
        FormSection(
            title = "Account Details",
            icon = Icons.Default.AccountBalanceWallet
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DropdownSelector(
                    label = "Wallet",
                    selectedName = walletName,
                    options = walletsResult?.getOrNull()?.map { it.walletName to it.walletID } ?: emptyList(),
                    onSelect = { name, id ->
                        walletName = name
                        walletId = id
                        walletError = null
                    },
                    icon = Icons.Default.AccountBalance,
                    error = walletError
                )

                DropdownSelector(
                    label = "Category",
                    selectedName = categoryName,
                    options = categoriesResult?.getOrNull()?.map { it.name to it.categoryID } ?: emptyList(),
                    onSelect = { name, id ->
                        categoryName = name
                        categoryId = id
                        categoryError = null
                    },
                    icon = Icons.Default.Category,
                    error = categoryError
                )
            }
        }

        // Amount Section
        FormSection(
            title = "Amount",
            icon = Icons.Default.Payments
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TransactionTextField(
                    label = "Amount (VND)",
                    value = formattedAmount,
                    onValueChange = {
                        rawAmount = unformatAmount(it.filter { char -> char.isDigit() })
                        amountError = null
                    },
                    leadingIcon = Icons.Default.AttachMoney,
                    keyboardType = KeyboardType.Number,
                    error = amountError
                )

                // Quick Amount Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("50K", "100K", "200K", "500K").forEach { amount ->
                        QuickAmountButton(
                            text = amount,
                            onClick = {
                                rawAmount = amount.replace("K", "000")
                                amountError = null
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Transaction Details Section
        FormSection(
            title = "Transaction Details",
            icon = Icons.Default.Receipt
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TransactionTextField(
                    label = "Title",
                    value = title,
                    onValueChange = { 
                        title = it
                        titleError = null
                    },
                    leadingIcon = Icons.Default.Title,
                    placeholder = "Enter transaction title",
                    error = titleError
                )

                TransactionTextField(
                    label = "Date & Time",
                    value = displayDate,
                    onValueChange = {},
                    isDropdown = true,
                    leadingIcon = Icons.Default.CalendarToday,
                    trailingIcon = {
                        IconButton(
                            onClick = { dateDialogState.show() },
                            modifier = Modifier
                                .background(
                                    Color(0xFF00D09E).copy(alpha = 0.1f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date",
                                tint = Color(0xFF00D09E)
                            )
                        }
                    }
                )
            }
        }

        // Save Button
        Button(
            onClick = {
                if (validateForm()) {
                    val parsedAmount = unformatAmount(formattedAmount).toDoubleOrNull()
                    if (parsedAmount != null) {
                        viewModel.createTransaction(
                            amount = parsedAmount,
                            description = title,
                            categoryId = categoryId,
                            walletId = walletId,
                            type = type,
                            transactionDate = storageDate
                        ) { success ->
                            if (success) {
                                Toast.makeText(context, "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                                budgetViewModel.getBudgetProgressAndAlerts()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Failed to save transaction", Toast.LENGTH_SHORT).show()
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
                containerColor = Color(0xFF00D09E)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                "Save Transaction",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }

    // Date & Time Dialogs
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = Color(0xFF00D09E),
                dateActiveBackgroundColor = Color(0xFF00D09E)
            )
        ) { date ->
            selectedDateTime.value.apply {
                set(Calendar.YEAR, date.year)
                set(Calendar.MONTH, date.monthValue - 1)
                set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
            }
            timeDialogState.show()
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        timepicker(
            initialTime = LocalTime.now(),
            colors = TimePickerDefaults.colors(
                activeBackgroundColor = Color(0xFF00D09E),
                selectorColor = Color(0xFF00D09E)
            )
        ) { time ->
            selectedDateTime.value.apply {
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
            }
        }
    }
}

@Composable
fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF00D09E),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A1A)
                )
            }
            content()
        }
    }
}

@Composable
fun QuickAmountButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFF00D09E)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF00D09E)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}

@Composable
fun TransactionTextField(
    label: String,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    isDropdown: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = isDropdown,
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder, color = Color(0xFFCCCCCC)) }
            } else null,
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (error != null) Color.Red else Color(0xFF00D09E),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) Color.Red else Color(0xFF00D09E),
                unfocusedBorderColor = if (error != null) Color.Red else Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = if (error != null) Color.Red else Color(0xFF00D09E),
                errorBorderColor = Color.Red,
                errorContainerColor = Color.White,
                errorCursorColor = Color.Red,
                errorLeadingIconColor = Color.Red,
                errorTrailingIconColor = Color.Red
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = error != null,
            supportingText = error?.let {
                { Text(error, color = Color.Red) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    selectedName: String,
    options: List<Pair<String, String>>,
    onSelect: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    placeholder: String = "Select an option",
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "dropdown_rotation"
    )

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedName.ifEmpty { "" },
                onValueChange = {},
                placeholder = {
                    Text(
                        text = placeholder,
                        color = Color(0xFFCCCCCC)
                    )
                },
                readOnly = true,
                leadingIcon = icon?.let {
                    {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = if (error != null) Color.Red else Color(0xFF00D09E),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = if (error != null) Color.Red else Color(0xFF00D09E),
                        modifier = Modifier
                            .rotate(animatedRotation)
                            .padding(8.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryEditable,
                        enabled = true
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (error != null) Color.Red else Color(0xFF00D09E),
                    unfocusedBorderColor = if (error != null) Color.Red else Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorBorderColor = Color.Red,
                    errorContainerColor = Color.White,
                    errorLeadingIconColor = Color.Red,
                    errorTrailingIconColor = Color.Red
                ),
                isError = error != null,
                supportingText = error?.let {
                    { Text(error, color = Color.Red) }
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
            ) {
                options.forEach { (name, id) ->
                    val isSelected = selectedName == name
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = name,
                                    color = if (isSelected) Color(0xFF00D09E) else Color(0xFF1A1A1A),
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color(0xFF00D09E),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSelect(name, id)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color(0xFFF0FDF4) else Color.Transparent
                            )
                    )
                }
            }
        }

    }
}


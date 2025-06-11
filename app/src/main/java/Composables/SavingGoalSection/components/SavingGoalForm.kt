package DI.Composables.SavingGoalUI.components

import DI.Composables.SavingGoalUI.theme.SavingGoalTheme
import DI.Models.Category.Category
import DI.Models.Wallet.Wallet
import DI.Utils.CurrencyInputTextField
import DI.Utils.CurrencyUtils
import DI.Utils.USDInputPreview
import DI.ViewModels.CurrencyConverterViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneymanagement_frontend.R
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingGoalForm(
    modifier: Modifier = Modifier,
    description: String,
    onDescriptionChange: (String) -> Unit,
    targetAmount: String,
    onTargetAmountChange: (String) -> Unit,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    categories: List<Category>,
    selectedWallet: Wallet?,
    onWalletSelected: (Wallet) -> Unit,
    wallets: List<Wallet>,
    startDate: LocalDate,
    onStartDateChange: (LocalDate) -> Unit,
    endDate: LocalDate,
    onEndDateChange: (LocalDate) -> Unit,
    isLoading: Boolean = false,
    currencyConverterViewModel: CurrencyConverterViewModel,
    // Add Save button parameters
    onSave: () -> Unit,
    isFormValid: Boolean,
    saveButtonText: String = "Lưu mục tiêu"
) {
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showWalletDropdown by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    // Currency state
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    var targetAmountTextFieldValue by remember { 
        mutableStateOf(TextFieldValue(targetAmount)) 
    }
    
    // Update TextFieldValue when targetAmount changes externally
    LaunchedEffect(targetAmount) {
        if (targetAmountTextFieldValue.text != targetAmount) {
            targetAmountTextFieldValue = TextFieldValue(targetAmount)
        }
    }
    
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
      Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Description Field
        CustomTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Mô tả mục tiêu",
            placeholder = "Ví dụ: Mua xe máy mới",
            icon = Icons.Outlined.Description,
            enabled = !isLoading
        )
          // Target Amount Field
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = SavingGoalTheme.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MonetizationOn,
                        contentDescription = null,
                        tint = SavingGoalTheme.PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Số tiền mục tiêu",
                        style = MaterialTheme.typography.labelMedium,
                        color = SavingGoalTheme.TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                var showUSDPreview by remember { mutableStateOf(false) }
                var isAmountFieldFocused by remember { mutableStateOf(false) }
                CurrencyInputTextField(
                    value = targetAmountTextFieldValue,
                    onValueChange = { newValue ->
                        targetAmountTextFieldValue = newValue
                        // Update the parent state with the raw text
                        onTargetAmountChange(newValue.text)
                    },
                    isVND = isVND,
                    placeholder = stringResource(R.string.enter_amount),
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            isAmountFieldFocused = focusState.isFocused
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SavingGoalTheme.PrimaryGreen,
                        unfocusedBorderColor = SavingGoalTheme.BorderColor,
                        focusedTextColor = SavingGoalTheme.TextPrimary,
                        unfocusedTextColor = SavingGoalTheme.TextPrimary,
                        cursorColor = SavingGoalTheme.PrimaryGreen,
                        focusedLabelColor = SavingGoalTheme.PrimaryGreen,
                        unfocusedLabelColor = SavingGoalTheme.TextSecondary,
                        focusedPlaceholderColor = SavingGoalTheme.TextTertiary,
                        unfocusedPlaceholderColor = SavingGoalTheme.TextTertiary
                    ),
                    onFormatted = { formattedText, parsedAmount ->
                        // Optionally handle formatted amount for validation
                        // You can use parsedAmount for validation logic
                    }
                )
                // Track focus state for USD preview
                LaunchedEffect(targetAmountTextFieldValue.text, isVND, isAmountFieldFocused) {
                    showUSDPreview = !isVND
                            && targetAmountTextFieldValue.text.isNotEmpty()
                            && isAmountFieldFocused
                }

                if (showUSDPreview) {
                    USDInputPreview(
                        inputText = targetAmountTextFieldValue.text,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        
        // Category Dropdown
        CustomDropdownField(
            value = selectedCategory?.name ?: "",
            onValueChange = { },
            label = "Danh mục",
            placeholder = "Chọn danh mục",
            icon = Icons.Outlined.Category,
            expanded = showCategoryDropdown,
            onExpandedChange = { showCategoryDropdown = it },
            enabled = !isLoading
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        showCategoryDropdown = false
                    }
                )
            }
        }
        
        // Wallet Dropdown
        CustomDropdownField(
            value = selectedWallet?.walletName ?: "",
            onValueChange = { },
            label = "Ví",
            placeholder = "Chọn ví",
            icon = Icons.Outlined.AccountBalanceWallet,
            expanded = showWalletDropdown,
            onExpandedChange = { showWalletDropdown = it },
            enabled = !isLoading
        ) {
            wallets.forEach { wallet ->
                DropdownMenuItem(
                    text = { Text(wallet.walletName) },
                    onClick = {
                        onWalletSelected(wallet)
                        showWalletDropdown = false
                    }
                )
            }
        }
        
        // Date Fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Start Date
            CustomDateField(
                value = startDate.format(dateFormatter),
                onValueChange = { },
                label = "Ngày bắt đầu",
                placeholder = "dd/mm/yyyy",
                modifier = Modifier.weight(1f),
                onClick = { showStartDatePicker = true },

            )
            
            // End Date
            CustomDateField(
                value = endDate.format(dateFormatter),
                onValueChange = { },
                label = "Ngày kết thúc",
                placeholder = "dd/mm/yyyy",
                modifier = Modifier.weight(1f),
                onClick = { showEndDatePicker = true },

            )
        }
          // Date Pickers
        if (showStartDatePicker) {
            CustomDatePickerDialog(
                onDateSelected = { selectedDate ->
                    onStartDateChange(selectedDate)
                    showStartDatePicker = false
                },
                onDismiss = { showStartDatePicker = false },
                initialDate = startDate
            )
        }
          if (showEndDatePicker) {
            CustomDatePickerDialog(
                onDateSelected = { selectedDate ->
                    onEndDateChange(selectedDate)
                    showEndDatePicker = false
                },
                onDismiss = { showEndDatePicker = false },
                initialDate = endDate
            )
        }
        
        // Save Button
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SavingGoalTheme.PrimaryGreen,
                contentColor = SavingGoalTheme.CardBackground,
                disabledContainerColor = SavingGoalTheme.BorderColor,
                disabledContentColor = SavingGoalTheme.TextTertiary
            ),
            enabled = isFormValid && !isLoading
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        color = SavingGoalTheme.CardBackground,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Đang xử lý...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = saveButtonText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    suffix: String? = null,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = SavingGoalTheme.TextPrimary,
            fontSize = 14.sp
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = SavingGoalTheme.TextTertiary,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SavingGoalTheme.SecondaryGreen,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = if (suffix != null) {
                {
                    Text(
                        text = suffix,
                        style = MaterialTheme.typography.bodySmall,
                        color = SavingGoalTheme.TextSecondary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            } else null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SavingGoalTheme.PrimaryGreenLight,
                unfocusedBorderColor = SavingGoalTheme.BorderColor,
                focusedTextColor = SavingGoalTheme.TextPrimary,
                unfocusedTextColor = SavingGoalTheme.TextPrimary,
                cursorColor = SavingGoalTheme.PrimaryGreenLight
            ),
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = SavingGoalTheme.TextPrimary,
            fontSize = 14.sp
        )
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            modifier = Modifier.fillMaxWidth()
        ) {            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                placeholder = {
                    Text(
                        text = placeholder,
                        color = SavingGoalTheme.TextTertiary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = SavingGoalTheme.SecondaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = SavingGoalTheme.TextSecondary
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SavingGoalTheme.PrimaryGreenLight,
                    unfocusedBorderColor = SavingGoalTheme.BorderColor,
                    focusedTextColor = SavingGoalTheme.TextPrimary,
                    unfocusedTextColor = SavingGoalTheme.TextPrimary
                ),
                readOnly = true,
                enabled = enabled,
                singleLine = true
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier
                    .background(SavingGoalTheme.CardBackground)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                content()
            }
        }
    }
}

@Composable
private fun CustomDateField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,

) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = SavingGoalTheme.TextPrimary,
            fontSize = 14.sp
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            placeholder = {
                Text(
                    text = placeholder,
                    color = SavingGoalTheme.TextTertiary,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = SavingGoalTheme.SecondaryGreen,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = SavingGoalTheme.TextPrimary,
                disabledTrailingIconColor = SavingGoalTheme.TextPrimary,
            ),
            readOnly = true,
            enabled = false,
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    initialDate: LocalDate
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toEpochDay() * 24 * 60 * 60 * 1000
    )
    
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        onDateSelected(selectedDate)
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SavingGoalTheme.PrimaryGreen
                )
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SavingGoalTheme.TextSecondary
                )
            ) {
                Text("Hủy")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = SavingGoalTheme.PrimaryGreenLight,
                todayContentColor = SavingGoalTheme.PrimaryGreen,
                todayDateBorderColor = SavingGoalTheme.PrimaryGreen
            )
        )
    }
}

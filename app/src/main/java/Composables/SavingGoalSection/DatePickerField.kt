package DI.Composables.SavingGoalSection

import DI.Composables.WalletSection.SavingGoalTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDateTime,
    onDateSelected: (LocalDateTime) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    OutlinedTextField(
        value = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
        onValueChange = { },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = SavingGoalTheme.AccentGreen,
            disabledLabelColor = SavingGoalTheme.AccentGreen,
            disabledTextColor = SavingGoalTheme.TextPrimary
        ),
        trailingIcon = {
            Icon(
                Icons.Default.DateRange,
                contentDescription = "Select Date",
                tint = SavingGoalTheme.AccentGreen
            )
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.ofEpochMilli(millis)
                            val newDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                            onDateSelected(newDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = SavingGoalTheme.AccentGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = SavingGoalTheme.TextSecondary)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = SavingGoalTheme.AccentGreen,
                    todayDateBorderColor = SavingGoalTheme.AccentGreen
                )
            )
        }
    }
}
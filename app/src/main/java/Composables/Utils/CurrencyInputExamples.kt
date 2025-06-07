package DI.Composables.Utils

import DI.Utils.CurrencyInputTextField
import DI.Utils.USDInputPreview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

/**
 * Example screen demonstrating the usage of CurrencyInputTextField utility component
 * This shows various ways to implement the currency input field in different scenarios.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyInputExamplesScreen() {
    var isVND by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CurrencyInputTextField Examples",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // Currency Toggle
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Currency Selection", fontWeight = FontWeight.Medium)
                Row {
                    Text("VND")
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = !isVND,
                        onCheckedChange = { isVND = !it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("USD")
                }
            }
        }
        
        // Example 1: Basic Usage
        ExampleSection(
            title = "1. Basic Usage",
            description = "Simple currency input with default settings"
        ) {
            var amount1 by remember { mutableStateOf(TextFieldValue("")) }
            
            CurrencyInputTextField(
                value = amount1,
                onValueChange = { amount1 = it },
                isVND = isVND,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Example 2: With Label and Custom Placeholder
        ExampleSection(
            title = "2. With Label and Custom Placeholder",
            description = "TextField with custom label and placeholder text"
        ) {
            var amount2 by remember { mutableStateOf(TextFieldValue("")) }
            
            CurrencyInputTextField(
                value = amount2,
                onValueChange = { amount2 = it },
                isVND = isVND,
                label = "Transaction Amount",
                placeholder = if (isVND) "Enter amount in VND" else "Enter amount in USD",
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Example 3: With Formatting Callback
        ExampleSection(
            title = "3. With Formatting Callback",
            description = "Monitor formatted text and parsed values in real-time"
        ) {
            var amount3 by remember { mutableStateOf(TextFieldValue("")) }
            var formattedText by remember { mutableStateOf("") }
            var parsedValue by remember { mutableStateOf<Double?>(null) }
            
            Column {
                CurrencyInputTextField(
                    value = amount3,
                    onValueChange = { amount3 = it },
                    isVND = isVND,
                    label = "Amount with Callback",
                    modifier = Modifier.fillMaxWidth(),
                    onFormatted = { formatted, parsed ->
                        formattedText = formatted
                        parsedValue = parsed
                    }
                )
                
                // Show callback results
                if (formattedText.isNotEmpty()) {
                    Text(
                        text = "Formatted: $formattedText",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                if (parsedValue != null) {
                    Text(
                        text = "Parsed Value: $parsedValue",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
        
        // Example 4: Error State
        ExampleSection(
            title = "4. Error State",
            description = "TextField in error state with custom supporting text"
        ) {
            var amount4 by remember { mutableStateOf(TextFieldValue("")) }
            var hasError by remember { mutableStateOf(false) }
            
            Column {
                CurrencyInputTextField(
                    value = amount4,
                    onValueChange = { 
                        amount4 = it
                        // Simulate error condition
                        hasError = it.text.contains("error")
                    },
                    isVND = isVND,
                    label = "Amount (type 'error' to see error state)",
                    supportingText = if (hasError) "Custom error message" else "Type 'error' to trigger error state",
                    isError = hasError,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Example 5: Disabled State
        ExampleSection(
            title = "5. Disabled State",
            description = "TextField in disabled state"
        ) {
            var amount5 by remember { mutableStateOf(TextFieldValue("1000")) }
            
            CurrencyInputTextField(
                value = amount5,
                onValueChange = { amount5 = it },
                isVND = isVND,
                label = "Disabled Amount",
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Example 6: USD Preview Demo
        ExampleSection(
            title = "6. USD Input Preview",
            description = "When currency is USD, this shows preview while typing"
        ) {
            var amount6 by remember { mutableStateOf(TextFieldValue("")) }
            
            Column {
                CurrencyInputTextField(
                    value = amount6,
                    onValueChange = { amount6 = it },
                    isVND = isVND,
                    label = "USD Amount (with preview)",
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Show USD preview separately for demonstration
                if (!isVND && amount6.text.isNotEmpty()) {
                    USDInputPreview(
                        inputText = amount6.text,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExampleSection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            content()
        }
    }
}

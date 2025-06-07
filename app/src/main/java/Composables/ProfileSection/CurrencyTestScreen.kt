package Composables.ProfileSection

import DI.Utils.CurrencyInputTextField
import DI.Utils.USDInputPreview
import DI.Utils.CurrencyUtils
import DI.ViewModels.CurrencyConverterViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CurrencyTestScreen(
    navController: NavController,
    currencyConverterViewModel: CurrencyConverterViewModel = hiltViewModel()
) {    
    val isVND by currencyConverterViewModel.isVND.collectAsState()
    val exchangeRates by currencyConverterViewModel.exchangeRates.collectAsState()
    val isLoading by currencyConverterViewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()
    
    // Extract the exchange rate value, with fallback
    val exchangeRate = exchangeRates?.usdToVnd ?: 24000.0    // Change your state from String to TextFieldValue
    var testAmount by remember { mutableStateOf(TextFieldValue("")) }
    var parsedAmount by remember { mutableStateOf<Double?>(null) }
    var formattedAmount by remember { mutableStateOf("") }
    var amountInVND by remember { mutableStateOf("") }
    var amountInUSD by remember { mutableStateOf("") }
    var testResult by remember { mutableStateOf("No test run yet") }

    val focusManager = LocalFocusManager.current

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .clickable { focusManager.clearFocus() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = "Currency Feature Test",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                // Currency Toggle
                SectionHeader("Current Currency")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {                        
                        Text(
                            text = "Current currency: ${if (isVND) "VND (₫)" else "USD ($)"}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Exchange rate: 1 USD = ${exchangeRate.toInt()} VND",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "VND",
                                fontSize = 14.sp,
                                color = if (isVND) MaterialTheme.colorScheme.primary else Color.Gray,
                                fontWeight = if (isVND) FontWeight.Bold else FontWeight.Normal
                            )
                            
                            Switch(
                                checked = !isVND,
                                onCheckedChange = {
                                    scope.launch {
                                        currencyConverterViewModel.toggleCurrency()
                                        currencyConverterViewModel.refreshExchangeRates()
                                    }
                                },
                                enabled = !isLoading
                            )
                            
                            Text(
                                text = "USD",
                                fontSize = 14.sp,
                                color = if (!isVND) MaterialTheme.colorScheme.primary else Color.Gray,
                                fontWeight = if (!isVND) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        
                        if (isLoading) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )
                        }
                    }
                }
                
                // Format Test
                SectionHeader("Format Test")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        CurrencyInputTextField(
                            value = testAmount,
                            onValueChange = { newValue ->
                                testAmount = newValue
                            },
                            isVND = isVND,
                            modifier = Modifier.fillMaxWidth(),
                            onFormatted = { _, parsed ->
                                parsedAmount = parsed
                                // Update other computed values when amount changes
                                scope.launch {
                                    if (parsed != null) {
                                        delay(100) // Small delay for smooth updates
                                        // Trigger recomposition for computed values
                                    }
                                }
                            }
                        )
                        // Show USD preview while typing (only if focused and USD)
                        var showUSDPreview by remember { mutableStateOf(false) }
                        
                        // Track focus state for USD preview
                        LaunchedEffect(testAmount.text, isVND) {
                            showUSDPreview = !isVND && testAmount.text.isNotEmpty()
                        }
                        
                        if (showUSDPreview) {
                            USDInputPreview(
                                inputText = testAmount.text,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                parsedAmount = CurrencyUtils.parseAmount(testAmount.text)
                                
                                if (parsedAmount != null) {
                                    formattedAmount = if (isVND) {
                                        CurrencyUtils.formatVND(parsedAmount!!)
                                    } else {
                                        CurrencyUtils.formatUSD(parsedAmount!!)
                                    }
                                    
                                    // Show in both currencies
                                    amountInVND = if (isVND) {
                                        CurrencyUtils.formatVND(parsedAmount!!)
                                    } else {
                                        CurrencyUtils.formatVND(CurrencyUtils.usdToVnd(parsedAmount!!, exchangeRate))
                                    }
                                    
                                    amountInUSD = if (!isVND) {
                                        CurrencyUtils.formatUSD(parsedAmount!!)
                                    } else {
                                        CurrencyUtils.formatUSD(CurrencyUtils.vndToUsd(parsedAmount!!, exchangeRate))
                                    }
                                    
                                    testResult = "Success"
                                } else {
                                    testResult = "Failed: Could not parse the amount"
                                    formattedAmount = "N/A"
                                    amountInVND = "N/A"
                                    amountInUSD = "N/A"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Format and Convert")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                          Text("Test Result: $testResult", fontWeight = FontWeight.Bold)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        ResultRow("Parsed Amount", parsedAmount?.toString() ?: "N/A")
                        ResultRow("Formatted (Current)", formattedAmount)
                        ResultRow("In VND", amountInVND)
                        ResultRow("In USD", amountInUSD)
                    }
                }
                
                // Example Format Patterns
                SectionHeader("Example Format Patterns")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        FormatExample("1000000", CurrencyUtils.formatVND(1000000.0), "VND format")
                        FormatExample("1000.50", CurrencyUtils.formatUSD(1000.50), "USD format")
                        FormatExample("1,000.50", CurrencyUtils.parseAmount("1,000.50")?.toString() ?: "Error", "Parse USD input")
                        FormatExample("1.000.000", CurrencyUtils.parseAmount("1.000.000")?.toString() ?: "Error", "Parse VND input")
                    }
                }
                
                // Format Input Test
                SectionHeader("Input Format Test")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        val testValues = listOf(
                            "1000",
                            "1,000",
                            "1.000",
                            "1,000.50",
                            "1.00050",
                            "1.000.000",
                            "1,000,000",
                            "$1,000.50",
                            "1.000.000 ₫"
                        )
                        
                        testValues.forEach { value ->
                            val parsed = CurrencyUtils.parseAmount(value)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(value, modifier = Modifier.weight(1f))
                                Text("→", modifier = Modifier.padding(horizontal = 8.dp))
                                Text(
                                    parsed?.toString() ?: "Error",
                                    modifier = Modifier.weight(1f),
                                    color = if (parsed != null) Color.Green else Color.Red
                                )                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun FormatExample(input: String, output: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = input,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(Color(0xFFEEEEEE))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = TextStyle(fontWeight = FontWeight.Medium)
            )
            Text("→")
            Text(
                text = output,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(Color(0xFFF0F8FF))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = TextStyle(fontWeight = FontWeight.Medium)
            )        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

package DI.Composables.ReportSection

import DI.ViewModels.ReportViewModel
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import android.os.Environment
import java.text.SimpleDateFormat
import java.util.*
import android.media.MediaScannerConnection
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.unit.sp

private val TextPrimary = Color(0xFF2E7D32)
private val TextSecondary = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    reportViewModel: ReportViewModel
) {
    val reportData by reportViewModel.reportData
    val isLoading by reportViewModel.isLoading
    val errorMessage by reportViewModel.errorMessage
    val context = LocalContext.current

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("cash-flow") }
    var currency by remember { mutableStateOf("VND") }

    // Dropdown states
    var typeExpanded by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }

    val reportTypes = listOf(
        "category-breakdown" to "Phân tích theo danh mục",
        "cash-flow" to "Dòng tiền",
        "daily-summary" to "Tóm tắt hàng ngày",
        "weekly-summary" to "Tóm tắt hàng tuần",
        "monthly-summary" to "Tóm tắt hàng tháng",
        "yearly-summary" to "Tóm tắt hàng năm"
    )

    val currencies = listOf("VND", "USD")

    // Check if current type needs end date
    val needsEndDate = type == "cash-flow" || type == "category-breakdown"

    // Launch for file share
    val shareLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("ReportScreen", "Share activity result: $result")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Assessment,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Tạo báo cáo",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tạo và chia sẻ báo cáo tài chính của bạn",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date Range Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Khoảng thời gian",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                if (needsEndDate) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(17.dp)
                    ) {
                        DatePickerField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = "Ngày bắt đầu",
                            modifier = Modifier.weight(1f),
                            context = context
                        )

                        DatePickerField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = "Ngày kết thúc",
                            modifier = Modifier.weight(1f),
                            context = context
                        )
                    }
                } else {
                    DatePickerField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = "Chọn ngày",
                        modifier = Modifier.fillMaxWidth(),
                        context = context
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Assessment,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Loại báo cáo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = reportTypes.find { it.first == type }?.second ?: type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chọn loại báo cáo") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextPrimary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextPrimary,
                            focusedLabelColor = TextPrimary
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        reportTypes.forEach { (key, displayName) ->
                            DropdownMenuItem(
                                text = { Text(displayName) },
                                onClick = {
                                    type = key
                                    typeExpanded = false
                                    if (key != "cash-flow" && key != "category-breakdown") {
                                        endDate = ""
                                    }
                                }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tiền tệ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = currencyExpanded,
                    onExpandedChange = { currencyExpanded = !currencyExpanded }
                ) {
                    OutlinedTextField(
                        value = currency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chọn tiền tệ") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextPrimary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextPrimary,
                            focusedLabelColor = TextPrimary
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = currencyExpanded,
                        onDismissRequest = { currencyExpanded = false }
                    ) {
                        currencies.forEach { curr ->
                            DropdownMenuItem(
                                text = { Text(curr) },
                                onClick = {
                                    currency = curr
                                    currencyExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                reportViewModel.generateReport(
                    startDate = startDate,
                    endDate = endDate,
                    type = type,
                    currency = currency
                ) { success ->
                    if (success && reportData != null) {
                        val (pdfBytes, fileName) = reportData!!
                        val defaultFileName = fileName ?: "report_${type}_${System.currentTimeMillis()}.pdf"

                        Log.d("ReportScreen", "Report generated: $defaultFileName, bytes size: ${pdfBytes.size}")
                        Toast.makeText(context, "Đã tạo báo cáo: $defaultFileName", Toast.LENGTH_LONG).show()
                    } else {
                        Log.e("ReportScreen", "Failed to generate report or reportData is null")
                        Toast.makeText(context, "Không thể tạo báo cáo", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = !isLoading && startDate.isNotEmpty() && (if (needsEndDate) endDate.isNotEmpty() else true),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(TextPrimary, TextSecondary)
                        ),
                        RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            "Đang tạo báo cáo...",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Tạo báo cáo",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        // Action Buttons (Show after report is generated)
        if (reportData != null && !isLoading) {
            val (pdfBytes, fileName) = reportData!!
            val defaultFileName = fileName ?: "report_${type}_${System.currentTimeMillis()}.pdf"

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    Text(
                        text = "Báo cáo đã sẵn sàng",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = TextPrimary
                    )

                    Text(
                        text = "Tên file: $defaultFileName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Save Button
                        Button(
                            onClick = {
                                saveToDownloads(context, pdfBytes, defaultFileName) { error ->
                                    if (error != null) {
                                        Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Đã lưu vào Downloads: $defaultFileName", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = TextPrimary
                            ),
                            border = BorderStroke(1.5.dp, TextPrimary),
                            shape = RoundedCornerShape(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Lưu",
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Share Button
                        Button(
                            onClick = {
                                sharePdf(context, pdfBytes, defaultFileName, shareLauncher) { error ->
                                    if (error != null) {
                                        Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    Brush.horizontalGradient(colors = listOf(TextPrimary, TextSecondary)),
                                    shape = RoundedCornerShape(40.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(40.dp),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        "Chia sẻ",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        errorMessage?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = it,
                        color = Color(0xFFD32F2F),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    context: Context
) {
    val calendar = Calendar.getInstance()
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    var showDatePicker by remember { mutableStateOf(false) }

    val initialDate = remember(value) {
        try {
            if (value.isNotEmpty()) {
                inputFormat.parse(value)?.let { date ->
                    calendar.time = date
                }
            }
        } catch (e: Exception) {
            // Keep current calendar instance
        }
        Triple(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            android.R.style.Theme_Material_Light_Dialog,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val selectedDate = inputFormat.format(calendar.time)
                onValueChange(selectedDate)
                showDatePicker = false
            },
            initialDate.first,
            initialDate.second,
            initialDate.third
        ).apply {
            setOnShowListener {
                val headerColor = android.graphics.Color.parseColor("#2A5630")
                try {
                    val datePicker = this@apply.datePicker
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        val headerId = context.resources.getIdentifier("date_picker_header", "id", "android")
                        val headerView = datePicker.findViewById<View>(headerId)
                        headerView?.setBackgroundColor(headerColor)
                    }
                    getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(headerColor)
                    getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(headerColor)
                } catch (e: Exception) {
                    Log.w("DatePickerField", "Header customization failed: ${e.message}")
                }
            }
            setOnDismissListener { showDatePicker = false }
            setOnCancelListener { showDatePicker = false }
        }
    }

    val displayText = remember(value) {
        try {
            if (value.isNotEmpty()) {
                outputFormat.format(inputFormat.parse(value)!!)
            } else {
                ""
            }
        } catch (e: Exception) {
            value
        }
    }

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        readOnly = true,
        label = {
            Text(
                text = label,
                maxLines = 1
            )
        },
        placeholder = {
            Text(
                text = "dd/mm/yyyy",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    showDatePicker = true
                    datePickerDialog.show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Chọn ngày",
                    tint = TextPrimary
                )
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TextPrimary,
            focusedLabelColor = TextPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier.clickable {
            showDatePicker = true
            datePickerDialog.show()
        }
    )

    DisposableEffect(datePickerDialog) {
        onDispose {
            if (datePickerDialog.isShowing) {
                datePickerDialog.dismiss()
            }
        }
    }
}

private fun saveToDownloads(context: Context, pdfBytes: ByteArray, fileName: String, onError: (String?) -> Unit) {
    try {
        // Save file to folder Downloads
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        FileOutputStream(file).use { it.write(pdfBytes) }
        Log.d("ReportScreen", "PDF saved to Downloads at ${file.absolutePath}")

        if (!file.exists()) {
            Log.e("ReportScreen", "PDF file does not exist after saving to Downloads: ${file.absolutePath}")
            onError("Không thể lưu file vào Downloads")
            return
        }

        // Notification to know success
        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            null
        ) { path, uri ->
            Log.d("ScanFile", "Scanned $path:")
            Log.d("ScanFile", "-> uri=$uri")
            Log.d("ReportScreen", "MediaScanner notified for file: $path")
            onError(null)
        }
    } catch (e: Exception) {
        Log.e("ReportScreen", "Error saving to Downloads: ${e.localizedMessage}", e)
        onError("Lỗi khi lưu file vào Downloads: ${e.localizedMessage}")
    }
}

private fun sharePdf(
    context: Context,
    pdfBytes: ByteArray,
    fileName: String,
    shareLauncher: androidx.activity.result.ActivityResultLauncher<Intent>,
    onError: (String?) -> Unit
) {
    try {
        // Save PDF as a temporary file in cache directory for sharing
        val cacheFile = File(context.cacheDir, fileName)
        FileOutputStream(cacheFile).use { it.write(pdfBytes) }
        Log.d("ReportScreen", "PDF saved to cache for sharing at ${cacheFile.absolutePath}")

        if (!cacheFile.exists()) {
            Log.e("ReportScreen", "PDF file does not exist after saving to cache: ${cacheFile.absolutePath}")
            onError("Không thể lưu file để chia sẻ")
            return
        }

        // Create a secure URI for the PDF file in cache, so other apps can access this file safely
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", cacheFile)
        Log.d("ReportScreen", "Share URI: $uri")

        // Create an Intent to send the PDF file to another app
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Check application can share file
        if (shareIntent.resolveActivity(context.packageManager) != null) {
            shareLauncher.launch(Intent.createChooser(shareIntent, "Chia sẻ báo cáo"))
            Log.d("ReportScreen", "Launched share Intent")
        } else {
            Log.e("ReportScreen", "No app found to share PDF")
            onError("Không tìm thấy ứng dụng để chia sẻ file PDF")
        }
    } catch (e: Exception) {
        Log.e("ReportScreen", "Error sharing PDF: ${e.localizedMessage}", e)
        onError("Lỗi khi chia sẻ file PDF: ${e.localizedMessage}")
    }
}
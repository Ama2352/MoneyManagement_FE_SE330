package DI.Composables.WalletSection

import DI.Composables.CategorySection.ModernColors
import DI.Models.UiEvent.UiEvent
import DI.Models.Wallet.AddWalletRequest
import DI.Models.Wallet.Wallet
import DI.ViewModels.WalletViewModel
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

// Color scheme
object WalletColors {
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
}

@Composable
fun WalletScreen(
    viewModel: WalletViewModel
) {
    val walletsState by viewModel.wallets.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingWallet by remember { mutableStateOf<Wallet?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        // Collect events for add, update, and delete actions
        launch {
            viewModel.addWalletEvent.collect { event ->
                when(event) {
                    is UiEvent.ShowMessage -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
        launch {
            viewModel.updateWalletEvent.collect { event ->
                when(event) {
                    is UiEvent.ShowMessage -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
        launch {
            viewModel.deleteWalletEvent.collect { event ->
                when(event) {
                    is UiEvent.ShowMessage -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WalletColors.Background,
                        Color(0xFFECFDF5) // Emerald-50
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            WalletHeader()

            Spacer(modifier = Modifier.height(24.dp))

            // Content based on state
            val walletsResult = walletsState
            when {
                walletsResult == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = WalletColors.Primary)
                    }
                }
                walletsResult.isSuccess -> {
                    val wallets = walletsResult.getOrNull() ?: emptyList()
                    WalletContent(
                        wallets = wallets,
                        onAddWallet = { showAddDialog = true },
                        onEditWallet = { wallet ->
                            editingWallet = wallet
                            showEditDialog = true
                        },
                        onDeleteWallet = { walletId ->
                            viewModel.deleteWallet(walletId)
                        }
                    )
                }
                walletsResult.isFailure -> {
                    ErrorMessage(
                        message = walletsResult.exceptionOrNull()?.message ?: "Unknown error",
                        onRetry = { viewModel.getWallets() }
                    )
                }
            }
        }

        // Dialogs
        if (showAddDialog) {
            AddWalletDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { request ->
                    viewModel.addWallet(request)
                    showAddDialog = false
                }
            )
        }

        if (showEditDialog && editingWallet != null) {
            EditWalletDialog(
                wallet = editingWallet!!,
                onDismiss = {
                    showEditDialog = false
                    editingWallet = null
                },
                onConfirm = { wallet ->
                    viewModel.updateWallet(wallet)
                    showEditDialog = false
                    editingWallet = null
                }
            )
        }


        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = WalletColors.Success,
                contentColor = WalletColors.OnPrimary
            )
        }
    }
}

@Composable
private fun WalletHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Wallet icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(WalletColors.Primary, WalletColors.PrimaryVariant)
                    ),
                    shape = CircleShape
                )
                .shadow(8.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = WalletColors.OnPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "My Wallets",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = WalletColors.OnSurface
        )

        Text(
            text = "Manage your digital wallets with ease",
            fontSize = 16.sp,
            color = WalletColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WalletContent(
    wallets: List<Wallet>,
    onAddWallet: () -> Unit,
    onEditWallet: (Wallet) -> Unit,
    onDeleteWallet: (String) -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Track which wallet is being deleted, not just if a dialog is shown
    var walletToDelete by remember { mutableStateOf<Wallet?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Total balance card
        TotalBalanceCard(totalBalance = wallets.sumOf { it.balance })

        // Add wallet button
        AddWalletButton(onClick = onAddWallet)

        // Wallet cards
        wallets.forEach { wallet ->
            WalletCard(
                wallet = wallet,
                onEdit = { onEditWallet(wallet) },
                onDelete = {
                    walletToDelete = wallet
                    showDeleteConfirmation = true
                }
            )
        }

        // Bottom spacing
        Spacer(modifier = Modifier.height(2.dp))
    }

    // Delete confirmation dialog - outside of LazyColumn
    walletToDelete?.let { wallet ->
        AlertDialog(
            onDismissRequest = { walletToDelete = null },
            title = {
                Text(
                    "Delete Wallet",
                    fontWeight = FontWeight.Bold,
                    color = ModernColors.OnSurface
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete \"${wallet.walletName}\"? This action cannot be undone.",
                    color = ModernColors.OnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteWallet(wallet.walletID)
                        walletToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ModernColors.Error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { walletToDelete = null },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Cancel",
                        color = ModernColors.OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun TotalBalanceCard(totalBalance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(WalletColors.Primary, WalletColors.PrimaryVariant)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Balance",
                        color = WalletColors.OnPrimary.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatCurrency(totalBalance),
                        color = WalletColors.OnPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = WalletColors.OnPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddWalletButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = WalletColors.Primary
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Add New Wallet",
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun WalletCard(
    wallet: Wallet,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = WalletColors.Surface
        ),
        border = BorderStroke(1.dp, WalletColors.Primary.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with icon and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            WalletColors.Primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = WalletColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = WalletColors.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = WalletColors.Error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wallet name
            Text(
                text = wallet.walletName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WalletColors.OnSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Balance
            Text(
                text = formatCurrency(wallet.balance),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = WalletColors.Primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Divider(color = WalletColors.Primary.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Balance",
                    fontSize = 12.sp,
                    color = WalletColors.OnSurfaceVariant
                )

                Text(
                    text = "Active",
                    fontSize = 12.sp,
                    color = WalletColors.Success,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AddWalletDialog(
    onDismiss: () -> Unit,
    onConfirm: (AddWalletRequest) -> Unit
) {
    var walletName by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WalletColors.Surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Add New Wallet",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WalletColors.OnSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = walletName,
                    onValueChange = { walletName = it },
                    label = { Text("Wallet Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WalletColors.Primary,
                        focusedLabelColor = WalletColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = balance,
                    onValueChange = { balance = it },
                    label = { Text("Initial Balance") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WalletColors.Primary,
                        focusedLabelColor = WalletColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val request = AddWalletRequest(
                                walletName = walletName.trim(),
                                balance = balance.toDoubleOrNull() ?: 0.0
                            )
                            onConfirm(request)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WalletColors.Primary
                        ),
                        enabled = walletName.isNotBlank()
                    ) {
                        Text("Add")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = WalletColors.OnSurfaceVariant
                        ),
                        border = BorderStroke(1.dp, WalletColors.OnSurfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
private fun EditWalletDialog(
    wallet: Wallet,
    onDismiss: () -> Unit,
    onConfirm: (Wallet) -> Unit
) {
    var walletName by remember { mutableStateOf(wallet.walletName) }
    var balance by remember { mutableStateOf(wallet.balance.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WalletColors.Surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Edit Wallet",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WalletColors.OnSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = walletName,
                    onValueChange = { walletName = it },
                    label = { Text("Wallet Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WalletColors.Primary,
                        focusedLabelColor = WalletColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = balance,
                    onValueChange = { balance = it },
                    label = { Text("Balance") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WalletColors.Primary,
                        focusedLabelColor = WalletColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val updatedWallet = wallet.copy(
                                walletName = walletName.trim(),
                                balance = balance.toDoubleOrNull() ?: wallet.balance
                            )
                            onConfirm(updatedWallet)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WalletColors.Primary
                        ),
                        enabled = walletName.isNotBlank()
                    ) {
                        Text("Update")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = WalletColors.OnSurfaceVariant
                        ),
                        border = BorderStroke(1.dp, WalletColors.OnSurfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = WalletColors.Error,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Something went wrong",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WalletColors.OnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            fontSize = 14.sp,
            color = WalletColors.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = WalletColors.Primary
            )
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}
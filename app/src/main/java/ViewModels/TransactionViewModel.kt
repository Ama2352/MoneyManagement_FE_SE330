package DI.ViewModels

import DI.Models.Category.Category
import DI.Models.Transaction.Transaction
import DI.Models.Transaction.TransactionDetail
import DI.Models.Transaction.CreateTransactionRequest
import DI.Models.Transaction.UpdateTransactionRequest
import DI.Models.Transaction.TransactionSearchRequest
import DI.Models.Transaction.GetTransactionsByDateRangeRequest
import DI.Repositories.CategoryRepository
import DI.Repositories.TransactionRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    // State for all transactions
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    // State for transaction details (used for search and date range filtering)
    private val _transactionDetails = MutableStateFlow<List<TransactionDetail>>(emptyList())
    val transactionDetails: StateFlow<List<TransactionDetail>> = _transactionDetails.asStateFlow()

    // State for selected transaction
    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    // Error states
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Success states
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadAllTransactions()
    }

    // Load all transactions
    fun loadAllTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = transactionRepository.getAllTransactions()
                if (response.isSuccessful) {
                    _transactions.value = response.body()?.reversed() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load transactions: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading transactions: ${e.message}"
                Log.e("TransactionViewModel", "Error loading transactions", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load transactions by wallet ID
    fun loadTransactionsByWalletId(walletId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = transactionRepository.getTransactionsByWalletId(walletId)
                if (response.isSuccessful) {
                    _transactions.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load wallet transactions: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading wallet transactions: ${e.message}"
                Log.e("TransactionViewModel", "Error loading wallet transactions", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Load transaction by ID
    fun loadTransactionById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = transactionRepository.getTransactionById(id)
                if (response.isSuccessful) {
                    _selectedTransaction.value = response.body()
                } else {
                    _errorMessage.value = "Failed to load transaction: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading transaction: ${e.message}"
                Log.e("TransactionViewModel", "Error loading transaction", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Create new transaction
    fun createTransaction(request: CreateTransactionRequest) {
        viewModelScope.launch {
            _isCreating.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val response = transactionRepository.createTransaction(request)
                if (response.isSuccessful) {
                    _successMessage.value = "Transaction created successfully"
                    loadAllTransactions() // Refresh the list
                } else {
                    _errorMessage.value = "Failed to create transaction: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error creating transaction: ${e.message}"
                Log.e("TransactionViewModel", "Error creating transaction", e)
            } finally {
                _isCreating.value = false
            }
        }
    }

    // Update transaction
    fun updateTransaction(request: UpdateTransactionRequest) {
        viewModelScope.launch {
            _isUpdating.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val response = transactionRepository.updateTransaction(request)
                if (response.isSuccessful) {
                    _successMessage.value = "Transaction updated successfully"
                    loadAllTransactions() // Refresh the list
                } else {
                    _errorMessage.value = "Failed to update transaction: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating transaction: ${e.message}"
                Log.e("TransactionViewModel", "Error updating transaction", e)
            } finally {
                _isUpdating.value = false
            }
        }
    }

    // Delete transaction
    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            _isDeleting.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val response = transactionRepository.deleteTransaction(id)
                if (response.isSuccessful) {
                    _successMessage.value = "Transaction deleted successfully"
                    loadAllTransactions() // Refresh the list
                } else {
                    _errorMessage.value = "Failed to delete transaction: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting transaction: ${e.message}"
                Log.e("TransactionViewModel", "Error deleting transaction", e)
            } finally {
                _isDeleting.value = false
            }
        }
    }

    // Get transactions by date range
    fun getTransactionsByDateRange(request: GetTransactionsByDateRangeRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = transactionRepository.getTransactionsByDateRange(request)
                if (response.isSuccessful) {
                    _transactionDetails.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load transactions by date range: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading transactions by date range: ${e.message}"
                Log.e("TransactionViewModel", "Error loading transactions by date range", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Search transactions
    fun searchTransactions(request: TransactionSearchRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = transactionRepository.searchTransactions(request)
                if (response.isSuccessful) {
                    _transactionDetails.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to search transactions: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error searching transactions: ${e.message}"
                Log.e("TransactionViewModel", "Error searching transactions", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear messages
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    // Clear transaction details (search/filter results)
    fun clearTransactionDetails() {
        _transactionDetails.value = emptyList()
    }

    // Clear selected transaction
    fun clearSelectedTransaction() {
        _selectedTransaction.value = null
    }

    // Set selected transaction
    fun setSelectedTransaction(transaction: Transaction) {
        _selectedTransaction.value = transaction
    }
}


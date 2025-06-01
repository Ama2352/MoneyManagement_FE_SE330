package DI.ViewModels

import DI.Composables.TransactionSection.GeneralTransactionItem
import DI.Composables.TransactionSection.toGeneralTransactionItem
import DI.Models.Category.Category
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import DI.Models.Category.Transaction
import DI.Models.Transaction.TransactionSearchRequest
import DI.Repositories.CategoryRepository
import DI.Repositories.TransactionRepository
import DI.Utils.EventBus
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val eventBus: EventBus
) : ViewModel() {

    private val _selectedType = mutableStateOf("All")
    val selectedType: State<String> = _selectedType

    private val _allTransactions = mutableStateOf<List<GeneralTransactionItem>>(emptyList())
    val allTransactions: List<GeneralTransactionItem> get() = _allTransactions.value

    // Original transactions before any filtering
    private val _originalTransactions = mutableStateOf<List<GeneralTransactionItem>>(emptyList())

    // Search parameters state
    private val _searchParams = mutableStateOf<TransactionSearchRequest?>(null)
    val searchParams: State<TransactionSearchRequest?> = _searchParams

    val filteredTransactions: State<List<GeneralTransactionItem>> = derivedStateOf {
        when (_selectedType.value) {
            "Income" -> allTransactions.filter { it.isIncome }
            "Expense" -> allTransactions.filter { !it.isIncome }
            else -> allTransactions
        }
    }
    private val _categories = mutableStateOf<List<Category>>(emptyList())
    val categories: List<Category> get() = _categories.value

    private val _selectedTransaction = mutableStateOf<Transaction?>(null)
    val selectedTransaction: State<Transaction?> = _selectedTransaction

    init {
        Log.d("TransactionScreenViewModel", "ViewModel initialized")
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = categoryRepository.getCategories()
            if (result.isSuccess) {
                _categories.value = result.getOrThrow()
                fetchTransactions()
            } else {
                Log.e("TransactionScreenVM", "Load categories failed: ${result.exceptionOrNull()?.message}")
            }
        }
    }


    internal fun fetchTransactions() {
        viewModelScope.launch {
            val result = transactionRepository.getTransactions()
            if (result.isSuccess) {
                val transactions = result.getOrThrow().map { it.toGeneralTransactionItem() }
                _allTransactions.value = transactions
                _originalTransactions.value = transactions
            }
        }
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = if (_selectedType.value == type) "All" else type
    }

    fun createTransaction(
        amount: Double,
        description: String,
        categoryId: String,
        walletId: String,
        type: String,
        transactionDate: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                transactionID = "",
                amount = amount,
                description = description,
                categoryID = categoryId,
                walletID = walletId,
                type = type,
                transactionDate = transactionDate
            )
            val result = transactionRepository.createTransaction(transaction)
            onResult(result.isSuccess)
            if (result.isSuccess) {
                fetchTransactions()
                eventBus.emitEvent("refresh_saving_goals")
            }
        }
    }

    fun loadTransactionById(id: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = transactionRepository.getTransactionById(id)
            if (result.isSuccess) {
                _selectedTransaction.value = result.getOrThrow()
                onResult(true)
            } else {
                Log.e("TransactionVM", "Failed to load transaction by ID: ${result.exceptionOrNull()?.message}")
                _selectedTransaction.value = null
                onResult(false)
            }
        }
    }


    fun updateTransaction(
        transactionID: String,
        amount: Double,
        description: String,
        categoryId: String,
        walletId: String,
        type: String,
        transactionDate: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                transactionID = transactionID,
                amount = amount,
                description = description,
                categoryID = categoryId,
                walletID = walletId,
                type = type,
                transactionDate = transactionDate
            )

            val result = transactionRepository.updateTransaction(transaction)
            onResult(result.isSuccess)

            if (result.isSuccess) {
                fetchTransactions()
                eventBus.emitEvent("refresh_saving_goals")
                onResult(true)
            } else {
                Log.e("TransactionVM", "Update failed: ${result.exceptionOrNull()?.message}")
                onResult(false)
            }
        }
    }

    fun deleteTransaction(id: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = transactionRepository.deleteTransaction(id)
            if (result.isSuccess) {
                fetchTransactions()
                eventBus.emitEvent("refresh_saving_goals")
                onResult(true)
            } else {
                Log.e("TransactionVM", "Delete failed: ${result.exceptionOrNull()?.message}")
                onResult(false)
            }
        }
    }

    fun fetchTransactionsByDateRange(from: String, to: String) {
        viewModelScope.launch {
            val result = transactionRepository.getTransactionsByDateRange(from, to)
            if (result.isSuccess) {
                Log.d("FetchSuccess", result.getOrThrow().toString())
                _allTransactions.value = result.getOrThrow().map { it.toGeneralTransactionItem() }
            } else {
                Log.e("FetchError", result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun searchTransactions(request: TransactionSearchRequest) {
        viewModelScope.launch {
            _searchParams.value = request
            val result = transactionRepository.searchTransactions(request)
            if (result.isSuccess) {
                _allTransactions.value = result.getOrThrow().map { it.toGeneralTransactionItem() }
            } else {
                Log.e("SearchTransactions", result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetSearch() {
        _searchParams.value = null
        _allTransactions.value = _originalTransactions.value
        _selectedType.value = "All"
    }

}


package DI.ViewModels

import DI.Models.Budget.Budget
import DI.Models.Budget.CreateBudgetRequest
import DI.Models.Budget.UpdateBudgetRequest
import DI.Models.UiEvent.UiEvent
import DI.Repositories.BudgetRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: BudgetRepository
) : ViewModel() {
    private val _budgets = MutableStateFlow<Result<List<Budget>>?>(null)
    val budgets: StateFlow<Result<List<Budget>>?> = _budgets.asStateFlow()

    private val _selectedBudget = MutableStateFlow<Result<Budget>?>(null)
    val selectedBudget: StateFlow<Result<Budget>?> = _selectedBudget.asStateFlow()

    private val _budgetEvent = MutableSharedFlow<UiEvent>()
    val budgetEvent = _budgetEvent.asSharedFlow()

    init {
        getBudgetProgressAndAlerts()
    }

    fun getBudgets() {
        viewModelScope.launch {
            val result = repository.getBudgets()
            _budgets.value = result
        }
    }

    fun getBudgetById(id: String) {
        viewModelScope.launch {
            val result = repository.getBudgetById(id)
            _selectedBudget.value = result
        }
    }

    fun createBudget(request: CreateBudgetRequest) {
        viewModelScope.launch {
            val result = repository.createBudget(request)
            if (result.isSuccess) {
                getBudgetProgressAndAlerts()
                _budgetEvent.emit(UiEvent.ShowMessage("Ngân sách được tạo thành công!"))
            } else {
                _budgetEvent.emit(UiEvent.ShowMessage("Lỗi: ${result.exceptionOrNull()?.message ?: "Không rõ lỗi"}"))
            }
        }
    }

    fun updateBudget(request: UpdateBudgetRequest) {
        viewModelScope.launch {
            val result = repository.updateBudget(request)
            if (result.isSuccess) {
                getBudgetProgressAndAlerts()
                _budgetEvent.emit(UiEvent.ShowMessage("Ngân sách được cập nhật thành công!"))
            } else {
                _budgetEvent.emit(UiEvent.ShowMessage("Lỗi: ${result.exceptionOrNull()?.message ?: "Không rõ lỗi"}"))
            }
        }
    }

    fun deleteBudget(id: String) {
        viewModelScope.launch {
            val result = repository.deleteBudget(id)
            if (result.isSuccess) {
                getBudgetProgressAndAlerts()
                _budgetEvent.emit(UiEvent.ShowMessage("Ngân sách đã được xóa!"))
            } else {
                _budgetEvent.emit(UiEvent.ShowMessage("Lỗi: ${result.exceptionOrNull()?.message ?: "Không rõ lỗi"}"))
            }
        }
    }

    fun getBudgetProgressAndAlerts() {
        viewModelScope.launch {
            val result = repository.getBudgetProgressAndAlerts()
            _budgets.value = result
            Log.d("BudgetViewModel", "Lỗi: ${result}")
        }
    }
}
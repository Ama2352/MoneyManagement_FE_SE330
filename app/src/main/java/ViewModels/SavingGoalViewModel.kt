package DI.ViewModels

import DI.Models.SavingGoal.CreateSavingGoal
import DI.Models.SavingGoal.SavingGoal
import DI.Models.SavingGoal.UpdateSavingGoal
import DI.Models.UiEvent.UiEvent
import DI.Repositories.SavingGoalRepository
import DI.Utils.EventBus
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavingGoalViewModel @Inject constructor(
    private val repository: SavingGoalRepository,
    private val eventBus: EventBus
) : ViewModel() {
    private val _savingGoals = MutableStateFlow<Result<List<SavingGoal>>?>(null)
    val savingGoals: StateFlow<Result<List<SavingGoal>>?> = _savingGoals.asStateFlow()

    private val _updateSavingGoalEvent = MutableSharedFlow<UiEvent>()
    val updateSavingGoalEvent = _updateSavingGoalEvent.asSharedFlow()

    private val _deleteSavingGoalEvent = MutableSharedFlow<UiEvent>()
    val deleteSavingGoalEvent = _deleteSavingGoalEvent.asSharedFlow()

    private val _selectedSavingGoal = MutableStateFlow<Result<SavingGoal>?>(null)
    val selectedSavingGoal: StateFlow<Result<SavingGoal>?> = _selectedSavingGoal.asStateFlow()

    private val _savingGoalProgress = MutableStateFlow<Result<List<SavingGoal>>?>(null)
    val savingGoalProgress: StateFlow<Result<List<SavingGoal>>?> = _savingGoalProgress.asStateFlow()

    init {
        getSavingGoalProgressAndAlerts()
        listenForEvents()
    }

    private fun listenForEvents() {
        viewModelScope.launch {
            eventBus.events.collectLatest { event ->
                if (event == "refresh_saving_goals") {
                    getSavingGoalProgressAndAlerts()
                }
            }
        }
    }

    fun getSavingGoals() {
        viewModelScope.launch {
            val result = repository.getAllSavingGoals()
            _savingGoals.value = result
        }
    }

    fun addSavingGoal(newSavingGoal: CreateSavingGoal, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            Log.d("SavingGoalDebug", "Sending CreateSavingGoal: $newSavingGoal")
            val result = repository.createSavingGoal(newSavingGoal)
            Log.d("SavingGoalDebug", "CreateSavingGoal response: $result")
            onResult(result.isSuccess)
            if (result.isSuccess) {
                getSavingGoalProgressAndAlerts() // Refresh progress list
                _updateSavingGoalEvent.emit(UiEvent.ShowMessage("Mục tiêu tiết kiệm đã được tạo thành công!"))
            } else {
                _updateSavingGoalEvent.emit(UiEvent.ShowMessage("Lỗi: ${result.exceptionOrNull()?.message ?: "Không thể tạo mục tiêu"}"))
            }
        }
    }

    fun getSavingGoalById(savingGoalId: String) {
        viewModelScope.launch {
            val result = repository.getSavingGoalById(savingGoalId)
            _selectedSavingGoal.value = result
        }
    }    fun updateSavingGoal(updatedSavingGoal: UpdateSavingGoal) {
        viewModelScope.launch {
            val result = repository.updateSavingGoal(updatedSavingGoal)
            if (result.isSuccess) {
                getSavingGoalProgressAndAlerts() // Refresh progress list
                _updateSavingGoalEvent.emit(UiEvent.ShowMessage("Mục tiêu tiết kiệm đã được cập nhật thành công!"))
            } else {
                _updateSavingGoalEvent.emit(UiEvent.ShowMessage("Lỗi: ${result.exceptionOrNull()?.message ?: "Không thể cập nhật mục tiêu"}"))
            }
        }
    }

    fun deleteSavingGoal(savingGoalId: String) {
        viewModelScope.launch {
            val result = repository.deleteSavingGoal(savingGoalId)
            if (result.isSuccess) {
                getSavingGoalProgressAndAlerts() // Refresh progress list
                _deleteSavingGoalEvent.emit(UiEvent.ShowMessage("Mục tiêu tiết kiệm đã được xóa thành công!"))
            } else {
                _deleteSavingGoalEvent.emit(UiEvent.ShowMessage("Lỗi: ${result.exceptionOrNull()?.message ?: "Không thể xóa mục tiêu"}"))
            }
        }
    }

    fun getSavingGoalProgressAndAlerts() {
        viewModelScope.launch {
            val result = repository.getSavingGoalProgressAndAlerts()
            _savingGoalProgress.value = result
        }
    }
}
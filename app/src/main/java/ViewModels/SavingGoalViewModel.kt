package DI.ViewModels

import DI.Models.SavingGoal.CreateSavingGoal
import DI.Models.SavingGoal.SavingGoal
import DI.Models.SavingGoal.UpdateSavingGoal
import DI.Models.UiEvent.UiEvent
import DI.Repositories.SavingGoalRepository
import DI.Utils.EventBus
import Utils.StringResourceProvider
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanagement_frontend.R
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
    private val eventBus: EventBus,
    private val stringResourceProvider: StringResourceProvider
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
    }    fun addSavingGoal(newSavingGoal: CreateSavingGoal, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            Log.d("SavingGoalDebug", "Sending CreateSavingGoal: $newSavingGoal")
            val result = repository.createSavingGoal(newSavingGoal)
            Log.d("SavingGoalDebug", "CreateSavingGoal response: $result")
            onResult(result.isSuccess)
            if (result.isSuccess) {
                getSavingGoalProgressAndAlerts() // Refresh progress list
                _updateSavingGoalEvent.emit(UiEvent.ShowMessage(
                    stringResourceProvider.getString(R.string.saving_goal_created_success)
                ))
            } else {
                val errorMessage = result.exceptionOrNull()?.message 
                    ?: stringResourceProvider.getString(R.string.saving_goal_unable_create)
                _updateSavingGoalEvent.emit(UiEvent.ShowMessage(
                    stringResourceProvider.getString(R.string.saving_goal_create_error, errorMessage)
                ))
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
                _updateSavingGoalEvent.emit(UiEvent.ShowMessage(
                    stringResourceProvider.getString(R.string.saving_goal_updated_success)
                ))
            } else {
                val errorMessage = result.exceptionOrNull()?.message 
                    ?: stringResourceProvider.getString(R.string.saving_goal_unable_update)
                _updateSavingGoalEvent.emit(UiEvent.ShowMessage(
                    stringResourceProvider.getString(R.string.saving_goal_update_error, errorMessage)
                ))
            }
        }
    }    fun deleteSavingGoal(savingGoalId: String) {
        viewModelScope.launch {
            val result = repository.deleteSavingGoal(savingGoalId)
            if (result.isSuccess) {
                getSavingGoalProgressAndAlerts() // Refresh progress list
                _deleteSavingGoalEvent.emit(UiEvent.ShowMessage(
                    stringResourceProvider.getString(R.string.saving_goal_deleted_success)
                ))
            } else {
                val errorMessage = result.exceptionOrNull()?.message 
                    ?: stringResourceProvider.getString(R.string.saving_goal_unable_delete)
                _deleteSavingGoalEvent.emit(UiEvent.ShowMessage(
                    stringResourceProvider.getString(R.string.saving_goal_delete_error, errorMessage)
                ))
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
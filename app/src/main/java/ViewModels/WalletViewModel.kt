package DI.ViewModels

import DI.Models.Wallet.AddWalletRequest
import DI.Models.Wallet.Wallet
import DI.Models.UiEvent.UiEvent
import DI.Repositories.WalletRepository
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
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {
    private val _wallets = MutableStateFlow<Result<List<Wallet>>?>(null)
    val wallets: StateFlow<Result<List<Wallet>>?> = _wallets.asStateFlow()

    private val _addWalletEvent = MutableSharedFlow<UiEvent>()
    val addWalletEvent = _addWalletEvent.asSharedFlow()

    private val _updateWalletEvent = MutableSharedFlow<UiEvent>()
    val updateWalletEvent = _updateWalletEvent.asSharedFlow()

    private val _deleteWalletEvent = MutableSharedFlow<UiEvent>()
    val deleteWalletEvent = _deleteWalletEvent.asSharedFlow()

    private val _selectedWallet = MutableStateFlow<Result<Wallet>?>(null)
    val selectedWallet: StateFlow<Result<Wallet>?> = _selectedWallet.asStateFlow()

    init {
        getWallets()
    }

    fun getWallets() {
        viewModelScope.launch {
            val result = repository.getWallets()
            _wallets.value = result
        }
    }

    fun addWallet(request: AddWalletRequest) {
        viewModelScope.launch {
            val result = repository.createWallet(request)
            if (result.isSuccess) {
                getWallets()
                _addWalletEvent.emit(UiEvent.ShowMessage("Wallet added!"))
            } else {
                _addWalletEvent.emit(UiEvent.ShowMessage("Error: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        }
    }

    fun getWalletById(id: String) {
        viewModelScope.launch {
            val result = repository.getWalletById(id)
            _selectedWallet.value = result
        }
    }

    fun updateWallet(wallet: Wallet) {
        viewModelScope.launch {
            val result = repository.updateWallet(wallet)
            if (result.isSuccess) {
                getWallets()
                _updateWalletEvent.emit(UiEvent.ShowMessage("Wallet updated!"))
            } else {
                _updateWalletEvent.emit(UiEvent.ShowMessage("Error: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        }
    }

    fun deleteWallet(id: String) {
        viewModelScope.launch {
            val result = repository.deleteWallet(id)
            if (result.isSuccess) {
                getWallets()
                _deleteWalletEvent.emit(UiEvent.ShowMessage("Wallet deleted!"))
            } else {
                _deleteWalletEvent.emit(UiEvent.ShowMessage("Error: ${result.exceptionOrNull()?.message ?: "Unknown error"}"))
            }
        }
    }
}
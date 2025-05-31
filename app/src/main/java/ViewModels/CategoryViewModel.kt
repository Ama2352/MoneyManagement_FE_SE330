package DI.ViewModels

import DI.Models.Category.AddCategoryRequest
import DI.Models.Category.Category
import DI.Models.Category.UpdateCategoryRequest
import DI.Models.UiEvent.UiEvent
import DI.Repositories.CategoryRepository
import Utils.StringResourceProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanagement_frontend.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository,
    private val stringProvider: StringResourceProvider
) : ViewModel() {
    private val _categories = MutableStateFlow<Result<List<Category>>?>(null)
    val categories: StateFlow<Result<List<Category>>?> = _categories.asStateFlow()

    private val _addCategoryEvent = MutableSharedFlow<UiEvent>()
    val addCategoryEvent = _addCategoryEvent.asSharedFlow()

    private val _updateCategoryEvent = MutableSharedFlow<UiEvent>()
    val updateCategoryEvent = _updateCategoryEvent.asSharedFlow()

    private val _deleteCategoryEvent = MutableSharedFlow<UiEvent>()
    val deleteCategoryEvent = _deleteCategoryEvent.asSharedFlow()

    private val _selectedCategory = MutableStateFlow<Result<Category>?>(null)
    val selectedCategory: StateFlow<Result<Category>?> = _selectedCategory.asStateFlow()

    init {
        getCategories()
    }

    fun getCategories() {
        viewModelScope.launch {
            val result = repository.getCategories()
            _categories.value = result
        }
    }

    fun addCategory(newCategoryName: AddCategoryRequest) {
        viewModelScope.launch {
            val result = repository.addCategory(newCategoryName)
            if (result.isSuccess) {
                getCategories()
                _addCategoryEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.category_added_success)))
            } else {
                _addCategoryEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(
                            R.string.error_message,
                            result.exceptionOrNull()?.message ?: stringProvider.getString(R.string.unknown_error)
                        )
                    )
                )
            }
        }
    }

    fun getCategoryById(id: String) {
        viewModelScope.launch {
            val result = repository.getCategoryById(id)
            _selectedCategory.value = result
        }
    }

    fun updateCategory(updatedCategory: UpdateCategoryRequest) {
        viewModelScope.launch {
            val result = repository.updateCategory(updatedCategory)
            if (result.isSuccess) {
                getCategories()
                _updateCategoryEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.category_updated_success)))
            } else {
                _updateCategoryEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(
                            R.string.error_message,
                            result.exceptionOrNull()?.message ?: stringProvider.getString(R.string.unknown_error)
                        )
                    )
                )
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            val result = repository.deleteCategory(id)
            if (result.isSuccess) {
                getCategories()
                _deleteCategoryEvent.emit(UiEvent.ShowMessage(stringProvider.getString(R.string.category_deleted_success)))
            } else {
                _deleteCategoryEvent.emit(
                    UiEvent.ShowMessage(
                        stringProvider.getString(
                            R.string.error_message,
                            result.exceptionOrNull()?.message ?: stringProvider.getString(R.string.unknown_error)
                        )
                    )
                )
            }
        }
    }
}

package DI.Models.UiEvent

sealed class UiEvent {
    data class ShowMessage(val message: String) : UiEvent()
}
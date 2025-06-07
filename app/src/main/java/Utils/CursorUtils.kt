package DI.Utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

// Helper function to set cursor position
fun setCursorPosition(text: String, isVND: Boolean, onUpdate: (TextFieldValue) -> Unit) {
    val cursorPosition = if (isVND) {
        // For VND, place cursor at the end
        text.length
    } else {
        // For USD, place cursor before the decimal point
        val decimalIndex = text.lastIndexOf('.')
        if (decimalIndex != -1) {
            decimalIndex // Position right before the decimal point
        } else {
            text.length // If no decimal point, place at end
        }
    }

    onUpdate(
        TextFieldValue(
            text = text,
            selection = TextRange(cursorPosition)
        )
    )
}
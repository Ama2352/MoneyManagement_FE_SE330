package DI.Composables.TransactionSection

import DI.Composables.CategorySection.getCategoryIcon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp

@Composable
fun TransactionIconButton(
    categoryName: String
) {
    val imageVector = getCategoryIcon(categoryName)

    Button(
        onClick = { },
        modifier = Modifier.size(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0068FF)),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp)
    ) {
        Image(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}



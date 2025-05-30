package DI.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun GeneralTemplate(
    contentHeader: @Composable () -> Unit,
    contentBody: @Composable () -> Unit,
    fraction: Float = 0.32f,
) {
    Box(
        modifier = Modifier.fillMaxSize().background(color = Color(0xFF53dba9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF53dba9)),
                shape = RectangleShape
            ) {
                contentHeader()
            }

            Card(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1FFF3))
            ) {
                contentBody()
            }
        }
    }
}
import DI.Composables.GeneralTemplate
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymanagement_frontend.R

@Composable
fun AuthScreen(title: String, fraction: Float, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF53dba9))
    ) {
        GeneralTemplate(
            contentHeader = { AuthHeader(title) },
            contentBody = content,
            fraction = fraction
        )
    }
}

@Composable
fun AuthHeader(title: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 35.sp,
            color = colorResource(R.color.textColor),
            fontWeight = FontWeight.W500
        )
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    isError: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = if(isError) Color.Red else Color.Transparent,
            focusedBorderColor = if(isError) Color.Red else Color.Transparent,
            unfocusedContainerColor = Color(0xFFDFF7E2),
            focusedContainerColor = Color(0xFFDFF7E2)
        ),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CustomButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(200.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D09E)),
        modifier = Modifier.fillMaxWidth(0.5f)
    ) {
        Text(text = label, fontSize = 20.sp, color = colorResource(R.color.textColor), fontWeight = FontWeight.W600)
    }
}

@Composable
fun CustomRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun SocialLoginSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        listOf(R.drawable.ic_facebook, R.drawable.ic_google).forEach {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "Social Login",
                    tint = Color(0xFF0E3E3E),
                    modifier = Modifier.size(37.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}


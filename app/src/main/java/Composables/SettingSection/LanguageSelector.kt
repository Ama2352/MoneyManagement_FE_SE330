package DI.Composables.ProfileSection

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import Utils.LanguageManager
import kotlinx.coroutines.launch
import com.example.moneymanagement_frontend.R
import java.util.*

// Import the color scheme from ProfileScreen

private const val TAG = "LanguageSelector"

@Composable
fun LanguageSelector() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentLanguageCode by LanguageManager.getLanguagePreference(context)
        .collectAsState(initial = LanguageManager.DEFAULT_LANGUAGE)
      // Get current language display name
    val currentLanguage = LanguageManager.getLanguages().find { it.code == currentLanguageCode }
    val currentLanguageName = when (currentLanguageCode) {
        "vi" -> stringResource(R.string.vietnamese)
        "en" -> stringResource(R.string.english)
        else -> currentLanguage?.name ?: "English"
    }
    val isVietnamese = currentLanguageCode == "vi"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF53dba9).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = Color(0xFF53dba9)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.language),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = stringResource(R.string.current_language, currentLanguageName),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            
            // Language Toggle Switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "VI",
                    fontSize = 12.sp,
                    color = if (isVietnamese) Color(0xFF53dba9) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isVietnamese) FontWeight.Bold else FontWeight.Normal
                )
                Switch(
                    checked = !isVietnamese, // Switch is checked when English is selected
                    onCheckedChange = { checked ->
                        scope.launch {
                            try {
                                val newLanguageCode = if (checked) "en" else "vi"

                                // Save preference and update locale
                                LanguageManager.saveLanguagePreference(context, newLanguageCode)

                                // Force app restart to apply language changes
                                if (context is Activity) {
                                    // Clear all activities and restart the app
                                    val packageManager = context.packageManager
                                    val intent =
                                        packageManager.getLaunchIntentForPackage(context.packageName)
                                    intent?.let {
                                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        context.startActivity(it)
                                        context.finish()
                                        // Kill the current process to ensure a complete restart
                                        android.os.Process.killProcess(android.os.Process.myPid())
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error changing language", e)
                            }
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF53dba9),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray
                    )
                )
                Text(
                    text = "EN",
                    fontSize = 12.sp,
                    color = if (!isVietnamese) Color(0xFF53dba9) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (!isVietnamese) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
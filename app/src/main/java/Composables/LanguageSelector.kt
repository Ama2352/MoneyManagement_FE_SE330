package Composables

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import Utils.Language
import Utils.LanguageManager
import kotlinx.coroutines.launch
import com.example.moneymanagement_frontend.R
import java.util.*

private const val TAG = "LanguageSelector"

@Composable
fun LanguageSelector() {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentLanguageCode by LanguageManager.getLanguagePreference(context)
        .collectAsState(initial = LanguageManager.DEFAULT_LANGUAGE)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LanguageManager.getLanguages().forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.name) },
                    onClick = {
                        scope.launch {
                            try {
                                // Save preference and update locale
                                LanguageManager.saveLanguagePreference(context, language.code)
                                
                                // Close dropdown
                                expanded = false
                                
                                // Force configuration update and recreate activity
                                if (context is Activity) {
                                    val config = context.resources.configuration
                                    val locale = Locale(language.code)
                                    Locale.setDefault(locale)
                                    config.setLocale(locale)
                                    context.resources.updateConfiguration(config, context.resources.displayMetrics)
                                    
                                    val intent = Intent(context, context.javaClass)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    context.startActivity(intent)
                                    context.finish()
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error changing language", e)
                            }
                        }
                    }
                )
            }
        }
    }
} 
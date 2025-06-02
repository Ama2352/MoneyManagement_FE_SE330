import Composables.LanguageSelector
import DI.Composables.ProfileSection.AvatarImage
import DI.Composables.ProfileSection.BackgroundColor
import DI.Composables.ProfileSection.CardColor
import DI.Composables.ProfileSection.DividerColor
import DI.Composables.ProfileSection.MainColor
import DI.Composables.ProfileSection.TextPrimaryColor
import DI.Composables.ProfileSection.TextSecondaryColor
import DI.Models.UserInfo.Profile
import DI.Navigation.Routes
import DI.ViewModels.ProfileViewModel
import ViewModels.AuthViewModel
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R

@Composable
fun ProfileScreen(
    appNavController: NavController,
    navController: NavController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel
) {
    // Reload init data when token is refreshed
    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()
    LaunchedEffect(refreshTokenState) {
        if (refreshTokenState?.isSuccess == true) {
            profileViewModel.getProfile()
        }
    }

    val profileResult = profileViewModel.profile.collectAsState()
    val profile = profileResult.value?.getOrNull()

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top App Bar
                TopAppBar()

                Spacer(modifier = Modifier.height(8.dp))

                // Profile Card
                ProfileHeaderCard(profile, navController, profileViewModel)

                Spacer(modifier = Modifier.height(24.dp))                // Account Settings
                SectionTitle(stringResource(R.string.account_settings))
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = stringResource(R.string.personal_information),
                    subtitle = stringResource(R.string.update_personal_details)
                )
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.security),
                    subtitle = stringResource(R.string.password_and_authentication)
                )
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.notifications_settings),
                    subtitle = stringResource(R.string.manage_alerts_notifications)
                )

                Spacer(modifier = Modifier.height(24.dp))                // Financial Settings
                SectionTitle(stringResource(R.string.financial_settings))
                SettingsItem(
                    icon = Icons.Default.CreditCard,
                    title = stringResource(R.string.payment_methods),
                    subtitle = stringResource(R.string.manage_cards_bank_accounts)
                )
                SettingsItem(
                    icon = Icons.Default.Savings,
                    title = stringResource(R.string.savings_goals),
                    subtitle = stringResource(R.string.set_track_financial_goals)
                )
                SettingsItem(
                    icon = Icons.Default.BarChart,
                    title = stringResource(R.string.budget_categories),
                    subtitle = stringResource(R.string.customize_spending_categories)
                )

                Spacer(modifier = Modifier.height(24.dp))                // Support and Info
                SectionTitle(stringResource(R.string.support_info))
                LanguageSelector()
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = stringResource(R.string.help_support),
                    subtitle = stringResource(R.string.faqs_contact_info)
                )
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.about),
                    subtitle = stringResource(R.string.app_version_legal_info)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        authViewModel.logout()
                        appNavController.navigate(Routes.Auth) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)                ) {
                    Text(
                        text = stringResource(R.string.log_out),
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween    ) {
        Text(
            text = stringResource(R.string.profile_screen_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryColor
            )
        )

        IconButton(onClick = { /* Handle settings */ }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = TextPrimaryColor
            )
        }
    }
}

@Composable
fun ProfileHeaderCard(
    profile: Profile?,
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val avatarVersion = profileViewModel.avatarVersion.collectAsState().value

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {            if(profile == null) {
                Text(stringResource(R.string.no_profile_data), color = TextPrimaryColor)
                return@Column
            }

            // Profile Image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MainColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Log.d("AvatarVersionProfile", avatarVersion)
                AvatarImage(profile.avatarUrl ?: "", avatarVersion)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Name
            Text(
                text = profile.displayName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryColor
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondaryColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stylish User ID with Copy Button
            val clipboardManager = LocalClipboardManager.current
            val context = LocalContext.current
            val idText = profile.id
            val copiedMessage = stringResource(R.string.id_copied)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MainColor.copy(alpha = 0.1f))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Icon(
                            imageVector = Icons.Default.PermIdentity,
                            contentDescription = null,
                            tint = MainColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.user_id),
                            fontSize = 15.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MainColor
                            )

                        )
                    }
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(idText))
                            Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.copy_id),
                            tint = MainColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MainColor.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = idText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimaryColor
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))            // Profile Stats / Finance Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(stringResource(R.string.budget), "$3,450")
                VerticalDivider()
                StatItem(stringResource(R.string.savings), "$12,580")
                VerticalDivider()
                StatItem(stringResource(R.string.spent), "$1,245")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit Profile Button
            OutlinedButton(
                onClick = { navController.navigate(Routes.EditProfile) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MainColor
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(MainColor)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.edit_profile))
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryColor
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondaryColor
            )
        )
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(DividerColor)
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = TextPrimaryColor
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor)
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
                    .background(MainColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MainColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimaryColor
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondaryColor
                    )
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.navigate),
                tint = TextSecondaryColor
            )
        }
    }
}
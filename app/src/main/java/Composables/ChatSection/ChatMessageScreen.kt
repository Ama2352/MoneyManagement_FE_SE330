package DI.Composables.ChatSection

import DI.API.TokenHandler.AuthStorage
import DI.Composables.ProfileSection.FriendAvatar
import DI.Models.Chat.ChatMessage
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanagement_frontend.R

// Define modern color scheme
private val PrimaryColor = Color(0xFF00C853)  // Bright green
private val SecondaryColor = Color(0xFF69F0AE)  // Light green
private val BackgroundColor = Color(0xFFF5F9F6)  // Very light mint
private val SentMessageColor = Color(0xFF00E676)  // Vibrant green
private val ReceivedMessageColor = Color.White
private val OnlineColor = Color(0xFF00E676)  // Matching sent message color
private val OfflineColor = Color(0xFF9E9E9E)
private val SurfaceColor = Color(0xFFE8F5E9)  // Light mint surface

@Composable
fun ChatMessageScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    friendId: String,
    profileViewModel: ProfileViewModel,
    friendViewModel: FriendViewModel
) {
    val currentUserId = AuthStorage.getUserIdFromToken(LocalContext.current)
    var messageContent by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        chatViewModel.getChatWithOtherUser(friendId)
        profileViewModel.getFriendAvatar(friendId)
        chatViewModel.markAllMessagesAsReadFromSingleChat(friendId)
    }

    val chatMessagesResult = chatViewModel.chatMessages.collectAsState()
    val chatMessages = chatMessagesResult.value?.getOrNull() ?: emptyList()
    val friendAvatar = profileViewModel.friendAvatar.collectAsState().value
    val isLoadingAvatar = profileViewModel.isLoadingAvatar.collectAsState()
    val friendsResult = friendViewModel.friends.collectAsState()
    val friends = friendsResult.value?.getOrNull() ?: emptyList()
    val friendName = chatMessages.firstOrNull { it.senderId == friendId }?.senderName ?: ""

    Scaffold(
        topBar = {
            ChatTopBar(
                userName = friendName,
                isOnline = friends.firstOrNull { it.userId == friendId}?.isOnline ?: false,
                friendAvatarUrl = friendAvatar.avatarUrl,
                isLoadingAvatar = isLoadingAvatar.value,
                onBackClick = { navController.popBackStack() },
                onInfoClick = { navController.navigate("friend_profile/$friendId") }
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageContent,
                onMessageChange = { messageContent = it },
                onSendClick = {
                    if (messageContent.isNotBlank()) {
                        chatViewModel.sendMessage(friendId, messageContent)
                        messageContent = ""
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatMessages.reversed()) { message ->
                MessageBubble(
                    message = message,
                    isSentByCurrentUser = message.senderId == currentUserId,
                    friendAvatarUrl = friendAvatar.avatarUrl,
                    isLoadingAvatar = isLoadingAvatar.value
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    isSentByCurrentUser: Boolean,
    friendAvatarUrl: String,
    isLoadingAvatar: Boolean,
) {
    val bubbleShape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = if (isSentByCurrentUser) 20.dp else 4.dp,
        bottomEnd = if (isSentByCurrentUser) 4.dp else 20.dp
    )

    val backgroundColor = if (isSentByCurrentUser) {
        SentMessageColor
    } else {
        ReceivedMessageColor
    }

    val textColor = if (isSentByCurrentUser) Color.White else Color.Black
    val alignment = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = alignment,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isSentByCurrentUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .shadow(4.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if(isLoadingAvatar) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = PrimaryColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    FriendAvatar(friendAvatarUrl)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.7f)
        ) {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(backgroundColor)
                    .shadow(2.dp, bubbleShape)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = message.content,
                        color = textColor,
                        fontSize = 16.sp
                    )
                    Text(
                        text = ChatTimeFormatter.formatTimestamp(message.sentAt),
                        fontSize = 11.sp,
                        color = if (isSentByCurrentUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    userName: String,
    isOnline: Boolean,
    friendAvatarUrl: String,
    isLoadingAvatar: Boolean,
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shadowElevation = 4.dp,
        color = SurfaceColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = PrimaryColor
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if(isLoadingAvatar) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PrimaryColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .shadow(4.dp, CircleShape)
                    ) {
                        FriendAvatar(friendAvatarUrl)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) OnlineColor else OfflineColor)
                        .border(2.dp, Color.White, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = if (isOnline) stringResource(R.string.online) else stringResource(R.string.offline),
                    fontSize = 13.sp,
                    color = if (isOnline) OnlineColor else OfflineColor
                )
            }

            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.info),
                    tint = PrimaryColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = SurfaceColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                value = messageText,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp, max = 100.dp)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text(stringResource(R.string.type_message)) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = BackgroundColor,
                    focusedContainerColor = BackgroundColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = onSendClick,
                modifier = Modifier.size(46.dp),
                containerColor = PrimaryColor,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.send),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

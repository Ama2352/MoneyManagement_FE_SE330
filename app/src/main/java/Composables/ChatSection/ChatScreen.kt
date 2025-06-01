package DI.Composables.ChatSection

import DI.Composables.ProfileSection.FriendAvatar
import DI.Composables.ProfileSection.MainColor
import DI.ViewModels.ChatViewModel
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import ViewModels.AuthViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.SolidColor
import androidx.navigation.NavController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.stringResource
import com.example.moneymanagement_frontend.R

@Composable
fun ChatScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel,
    profileViewModel: ProfileViewModel,
    friendViewModel: FriendViewModel
) {
    // Reload init data when token is refreshed
    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()
    LaunchedEffect(refreshTokenState) {
        if (refreshTokenState?.isSuccess == true) {
            chatViewModel.connectToSignalR()
            chatViewModel.getLatestChats()
        }
    }

    // State for search query
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        chatViewModel.getLatestChats()
    }

    val latestChatsResult = chatViewModel.latestChats.collectAsState()
    val latestChats = latestChatsResult.value?.getOrNull() ?: emptyList()
    val friendAvatars = profileViewModel.friendAvatars.collectAsState().value
    val isLoadingAvatar = profileViewModel.isLoadingAvatar.collectAsState()
    val profile = profileViewModel.profile.collectAsState().value?.getOrNull()

    LaunchedEffect(latestChats.toList()) { // Convert to list to trigger on changes
        if(latestChats.isNotEmpty()) {
            val friendIds = latestChats.map { chat ->
                if(chat.latestMessage.senderName == profile?.displayName)
                    chat.latestMessage.receiverId
                else
                    chat.latestMessage.senderId
            }
            profileViewModel.getFriendAvatars(friendIds)
        }
    }

    // When both chats and avatars are ready, update chats with avatarUrls
    val chatsWithAvatars = remember(latestChats, friendAvatars) {
        latestChats.map { chat ->
            val friendId =
                if(chat.latestMessage.senderName == profile?.displayName)
                    chat.latestMessage.receiverId
                else
                    chat.latestMessage.senderId
            val avatarUrl = friendAvatars.find { it.userId == friendId }?.avatarUrl ?: ""
            chat.copy(avatarUrl = avatarUrl)
        }
    }

    // Filter chats based on search query
    val filteredChats = remember(chatsWithAvatars, searchQuery) {
        if(searchQuery.isEmpty()) {
            chatsWithAvatars
        } else {
            chatsWithAvatars.filter { chat ->
                val friendName =
                    if(chat.latestMessage.senderName == profile?.displayName)
                        chat.latestMessage.receiverName
                    else
                        chat.latestMessage.senderName
                friendName.contains(searchQuery, ignoreCase = true) ||
                chat.latestMessage.content.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val friendsResult = friendViewModel.friends.collectAsState()
    val friends = friendsResult.value?.getOrNull() ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF53dba9))
            .padding(16.dp),
    ) {
        Text(stringResource(R.string.messages), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onClearQuery = { searchQuery = "" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if(chatsWithAvatars.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_conversations), fontSize = 16.sp)
            }
        } else if(filteredChats.isEmpty() && searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.no_conversations_search),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.try_different_search),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(filteredChats.size) { index ->
                    MessageItem(
                        navController = navController,
                        title =
                            if(filteredChats[index].latestMessage.senderName == profile?.displayName)
                                filteredChats[index].latestMessage.receiverName
                            else filteredChats[index].latestMessage.senderName,
                        message =
                            if(filteredChats[index].latestMessage.senderName == profile?.displayName)
                                stringResource(R.string.you_prefix, filteredChats[index].latestMessage.content)
                            else filteredChats[index].latestMessage.content,
                        time = filteredChats[index].latestMessage.sentAt,
                        count = filteredChats[index].unreadCount,
                        friendId =
                            if(filteredChats[index].latestMessage.senderName == profile?.displayName)
                                filteredChats[index].latestMessage.receiverId
                            else filteredChats[index].latestMessage.senderId,
                        friendAvatarUrl = filteredChats[index].avatarUrl ?: "",
                        isLoadingAvatar = isLoadingAvatar.value,
                        isOnline = friends.firstOrNull {
                            it.userId == filteredChats[index].latestMessage.receiverId
                        }?.isOnline ?: false,
                        color = Color(0xFF5C6BC0)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x809AE7C5)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) { innerTextField ->  
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_conversations),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }

            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = onClearQuery,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    navController: NavController,
    title: String,
    message: String,
    time: String,
    count: Int?,
    color: Color,
    friendId: String,
    friendAvatarUrl: String,
    isLoadingAvatar: Boolean,
    isOnline: Boolean,
    isAlert: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp)
            .clickable {
                navController.navigate("chat_message/$friendId")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with online dot
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            if(isLoadingAvatar) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MainColor,
                    strokeWidth = 2.dp
                )
            } else {
                FriendAvatar(friendAvatarUrl)
            }

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (isOnline) Color(0xFF4CAF50) else Color.Gray)
                    .border(1.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(message, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(ChatTimeFormatter.formatTimestamp(time), fontSize = 12.sp)
            if (count != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isAlert) Color(0xFFD32F2F) else Color(0xFFB0BEC5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(count.toString(), color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}


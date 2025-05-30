package com.example.friendsapp

import DI.Composables.ProfileSection.FriendAvatar
import DI.Models.Friend.AddFriendRequest
import DI.Models.Friend.Friend
import DI.Models.Friend.FriendRequest
import DI.Models.UiEvent.UiEvent
import DI.ViewModels.FriendViewModel
import DI.ViewModels.ProfileViewModel
import ViewModels.AuthViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// Define main color as provided
val MainColor = Color(0xFF53dba9)

@Composable
fun FriendsScreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = MainColor,
            secondary = MainColor.copy(alpha = 0.7f),
            tertiary = MainColor.copy(alpha = 0.5f),
            surface = Color.White,
            background = Color(0xFFF5F5F5)
        ),
        content = content
    )
}

@Composable
fun FriendsScreen(
    authViewModel: AuthViewModel,
    friendViewModel: FriendViewModel,
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    // Reload init data when token is refreshed
    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()
    LaunchedEffect(refreshTokenState) {
        if (refreshTokenState?.isSuccess == true) {
            friendViewModel.getAllFriends()
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var friendIdInput by remember { mutableStateOf("") }

    val context = LocalContext.current
    // Collect UI events once
    LaunchedEffect(Unit) {
        launch {
            friendViewModel.addFriendEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    } else -> {}
                }
            }
        }

        launch {
            friendViewModel.acceptFriendRequestEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    } else -> {}
                }
            }
        }
    }

    var showPendingRequestsDialog by remember { mutableStateOf(false) }
    val friendRequestsResult = friendViewModel.friendRequests.collectAsState()
    val friendRequests = friendRequestsResult.value?.getOrNull() ?: emptyList()

    LaunchedEffect(showPendingRequestsDialog) {
        launch {
            friendViewModel.getFriendRequests()
        }
    }

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = { CustomTopBar(
            title = "My Friends",
            onFriendRequestsClick = { showPendingRequestsDialog = true },
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onClearQuery = { searchQuery = "" }
        ) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MainColor
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Friend",
                    tint = Color.White
                )
            }
        }
    ) { innerPadding ->
        FriendsList(
            modifier = Modifier.padding(innerPadding),
            friendViewModel = friendViewModel,
            pendingRequests = friendRequests.size,
            profileViewModel = profileViewModel,
            navController = navController,
            searchQuery = searchQuery
        )

        // Add Friend Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    friendIdInput = ""
                },
                title = { Text("Add Friend") },
                text = {
                    Column {
                        Text(
                            "Enter Friend ID to send request:",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = friendIdInput,
                            onValueChange = { friendIdInput = it },
                            label = { Text("Friend ID") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MainColor,
                                focusedLabelColor = MainColor,
                                cursorColor = MainColor
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Handle adding friend with ID: friendIdInput
                            val addFriendRequest = AddFriendRequest(friendIdInput)
                            friendViewModel.addFriend(addFriendRequest)
                            showAddDialog = false
                            friendIdInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor
                        )
                    ) {
                        Text("Send Request")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAddDialog = false
                            friendIdInput = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Pending Requests Dialog
        if(showPendingRequestsDialog) {
            AlertDialog(
                onDismissRequest = { showPendingRequestsDialog = false },
                title = {
                    Text("Pending Requests") },
                text = {
                    if(friendRequests.isEmpty()) {
                        Text("No pending requests.")
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "You have ${friendRequests.size} pending requests.",
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            LazyColumn {
                                items(friendRequests) { request ->
                                    PendingRequestItem(
                                        request = request,
                                        onAccept = {
                                            friendViewModel.acceptFriendRequest(request.userId)
                                            showPendingRequestsDialog = false
                                        },
                                        onReject = {
                                            friendViewModel.rejectFriendRequest(request.userId)
                                            showPendingRequestsDialog = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showPendingRequestsDialog = false
                        }
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun PendingRequestItem(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 18.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User Info
            Text(
                text = request.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "@${request.username}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Reject")
                    Spacer(Modifier.width(4.dp))
                    Text("Reject")
                }

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Accept")
                    Spacer(Modifier.width(4.dp))
                    Text("Accept")
                }
            }
        }
    }
}

@Composable
fun FriendsList(
    modifier: Modifier = Modifier,
    friendViewModel: FriendViewModel,
    pendingRequests: Int,
    profileViewModel: ProfileViewModel,
    navController: NavController,
    searchQuery: String
) {
    val friendsResult = friendViewModel.friends.collectAsState()
    val friends = friendsResult.value?.getOrNull() ?: emptyList()

    val filteredFriends = remember(friends, searchQuery) {
        if(searchQuery.isEmpty()) {
            friends
        } else {
            friends.filter { friend ->
                friend.displayName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(filteredFriends) {
        val friendIds = filteredFriends.map { it.userId }
        profileViewModel.getFriendAvatars(friendIds)
    }

    val friendAvatars = profileViewModel.friendAvatars.collectAsState().value
    val isLoadingAvatar = profileViewModel.isLoadingAvatar.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (friends.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No friends found.", style = MaterialTheme.typography.bodyLarge)
            }
        } else if(filteredFriends.isEmpty() && searchQuery.isNotEmpty()) {
            // Show "No results found" when search has no matches
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No friends found",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Try a different search term",
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
            }
        } else {

            val onlineFriendCount = friends.count { it.isOnline }
            FriendsStatsSummary(friends.size, onlineFriendCount, pendingRequests)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(filteredFriends) { friend ->
                    FriendCard(
                        friend = friend,
                        friendViewModel = friendViewModel,
                        friendAvatarUrl = friendAvatars.find { it.userId == friend.userId }?.avatarUrl ?: "",
                        isLoadingAvatar = isLoadingAvatar.value,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun FriendsStatsSummary(friendCount: Int, onlineFriendCount: Int, pendingRequest: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MainColor.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(count = friendCount, label = "Friends")
            StatItem(count = onlineFriendCount, label = "Online")
            StatItem(count = pendingRequest, label = "Pending")
        }
    }
}

@Composable
fun StatItem(count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MainColor
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun FriendCard(
    friend: Friend,
    friendViewModel: FriendViewModel,
    friendAvatarUrl: String,
    isLoadingAvatar: Boolean,
    navController: NavController
) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        launch {
            friendViewModel.deleteFriendEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                        color = DI.Composables.ProfileSection.MainColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    FriendAvatar(friendAvatarUrl)
                }

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (friend.isOnline) Color(0xFF4CAF50) else Color.Gray)
                        .border(1.dp, Color.White, CircleShape)
                )
            }

            // Friend info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = friend.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = friend.lastActive ?: "Last active unknown",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = { navController.navigate("chat_message/${friend.userId}") }) {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = "Message",
                    tint = MainColor,
                )
            }
            IconButton(onClick = { friendViewModel.deleteFriend(friend.userId) }) {
                Icon(
                    imageVector = Icons.Default.PersonRemove,
                    contentDescription = "Delete",
                    tint = Color.Red,
                )
            }

        }
    }
}

@Composable
fun CustomTopBar(
    title: String,
    onFriendRequestsClick: () -> Unit = {},
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit
) {

    var onSearchMode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MainColor)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if(onSearchMode) {
                CustomSearchBar(
                    query = query,
                    onQueryChange = onQueryChange,
                    onClearQuery = {
                        onClearQuery()
                        onSearchMode = false
                    }
                )
            } else {
                // Title
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )

                // Friend Requests Icon
                IconButton(onClick = onFriendRequestsClick) {
                    Icon(
                        imageVector = Icons.Filled.Contacts,
                        contentDescription = "Friends",
                        tint = Color.White
                    )
                }

                // Search Icon
                IconButton(onClick = {
                    onSearchMode = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
            cursorBrush = SolidColor(Color.White),
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) { innerTextField ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search conversations...",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        }

        IconButton(
            onClick = { onClearQuery() },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                tint = Color.White
            )
        }
    }
}
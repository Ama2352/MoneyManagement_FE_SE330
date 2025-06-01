import DI.Composables.CategorySection.AddNewCategoryDialog
import DI.Composables.CategorySection.ModernColors
import DI.Composables.CategorySection.getCategoryIcon
import DI.Models.Category.AddCategoryRequest
import DI.Models.Category.Category
import DI.Models.Category.UpdateCategoryRequest
import DI.Models.UiEvent.UiEvent
import DI.ViewModels.CategoryViewModel
import Utils.LanguageManager
import ViewModels.AuthViewModel
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneymanagement_frontend.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

@Composable
fun ModernCategoryCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 2.dp else 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = ModernColors.Primary.copy(alpha = 0.1f),
                spotColor = ModernColors.Primary.copy(alpha = 0.25f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    isPressed = true
                    onClick()
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = ModernColors.cardGradient)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    ModernColors.Secondary,
                                    ModernColors.SecondaryLight,
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category.name),
                        contentDescription = "${category.name} icon",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Category name with better typography
                Text(
                    text = category.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ModernColors.OnSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ModernCategoryDetailDialog(
    category: Category,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onDelete: () -> Unit
) {
    var categoryName by remember { mutableStateOf(category.name) }
    var isNameValid by remember { mutableStateOf(true) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val density = LocalDensity.current
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { with(density) { 50.dp.roundToPx() } },
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { with(density) { 50.dp.roundToPx() } }
            ) + fadeOut(animationSpec = tween(200))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFFAFBFC)
                                )
                            )
                        )
                ) {
                    // Background decoration
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        ModernColors.Primary.copy(alpha = 0.1f),
                                        ModernColors.Secondary.copy(alpha = 0.1f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Large animated icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            ModernColors.SecondaryLight,
                                            ModernColors.Secondary
                                        )
                                    )
                                )
                                .shadow(
                                    elevation = 12.dp,
                                    shape = CircleShape,
                                    ambientColor = ModernColors.Primary.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(categoryName),
                                contentDescription = "$categoryName icon",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Modern text field
                        OutlinedTextField(
                            value = categoryName,
                            onValueChange = { newName ->
                                categoryName = newName
                                isNameValid = newName.trim().isNotEmpty()
                            },
                            label = {
                                Text(
                                    stringResource(R.string.category_name),
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ModernColors.Primary,
                                focusedLabelColor = ModernColors.Primary,
                                cursorColor = ModernColors.Primary,
                                unfocusedBorderColor = ModernColors.OnSurfaceVariant.copy(alpha = 0.3f)
                            ),
                            isError = !isNameValid,
                            supportingText = if (!isNameValid) {
                                {
                                    Text(
                                        stringResource(R.string.category_name_empty_error),
                                        color = ModernColors.Error,
                                        fontSize = 12.sp
                                    )
                                }
                            } else null
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Created date with icon
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = ModernColors.SurfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = stringResource(R.string.created),
                                    tint = ModernColors.OnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.created_at, category.createdAt),
                                    fontSize = 14.sp,
                                    color = ModernColors.OnSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Modern action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Save button
                            Button(
                                onClick = {
                                    if (categoryName.trim().isNotEmpty()) {
                                        onSave(categoryName.trim())
                                    }
                                },
                                modifier = Modifier.weight(1f).height(52.dp),
                                enabled = isNameValid && categoryName.trim() != category.name,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ModernColors.Primary,
                                    disabledContainerColor = ModernColors.OnSurfaceVariant.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 2.dp
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(R.string.save),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    stringResource(R.string.save),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Delete button
                            Button(
                                onClick = { showDeleteConfirmation = true },
                                modifier = Modifier.weight(1f).height(52.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ModernColors.Error
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 2.dp
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    stringResource(R.string.delete),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Close button
                        TextButton(
                            onClick = {
                                isVisible = false
                                kotlinx.coroutines.GlobalScope.launch {
                                    kotlinx.coroutines.delay(200)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.cancel),
                                color = ModernColors.OnSurfaceVariant,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = {
                    Text(
                        stringResource(R.string.delete_category),
                        fontWeight = FontWeight.Bold,
                        color = ModernColors.OnSurface
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.delete_category_confirm, category.name),
                        color = ModernColors.OnSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDelete()
                            showDeleteConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ModernColors.Error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.delete), fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirmation = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                            color = ModernColors.OnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun ModernCategoriesScreen(
    categoryViewModel: CategoryViewModel,
    authViewModel: AuthViewModel,
) {
    val context = LocalContext.current
    
    // Reload init data when token is refreshed
    val refreshTokenState by authViewModel.refreshTokenState.collectAsState()
    LaunchedEffect(refreshTokenState) {
        if (refreshTokenState?.isSuccess == true) {
            categoryViewModel.getCategories();
        }
    }

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Collect events for add, update, and delete actions
        launch {
            categoryViewModel.addCategoryEvent.collect { event ->
                when(event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        launch {
            categoryViewModel.updateCategoryEvent.collect { event ->
                when(event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        launch {
            categoryViewModel.deleteCategoryEvent.collect { event ->
                when(event) {
                    is UiEvent.ShowMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val categoriesResult = categoryViewModel.categories.collectAsState()
    val categories = categoriesResult.value?.getOrNull() ?: emptyList()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ModernColors.SecondaryLight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = stringResource(R.string.category),
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = stringResource(R.string.my_categories),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = stringResource(
                                        R.string.category_count,
                                        categories.size,
                                        stringResource(
                                            if (categories.size == 1)
                                                R.string.category_single
                                            else
                                                R.string.category_plural
                                        )
                                    ),
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(58.dp),
                containerColor = ModernColors.SecondaryLight,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_category),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = ModernColors.backgroundGradient)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    ModernCategoryCard(
                        category = category,
                        onClick = { selectedCategory = category }
                    )
                }
            }
        }

        // Add Category Dialog
        AddNewCategoryDialog(
            showDialog = showAddDialog,
            onDismiss = { showAddDialog = false },
            onSave = { newCategoryName ->
                categoryViewModel.addCategory(AddCategoryRequest(newCategoryName))
                showAddDialog = false
            }
        )

        // Edit Category Dialog
        selectedCategory?.let { category ->
            ModernCategoryDetailDialog(
                category = category,
                onDismiss = { selectedCategory = null },
                onSave = { newName ->
                    categoryViewModel.updateCategory(
                        UpdateCategoryRequest(
                            categoryID = category.categoryID,
                            name = newName
                        )
                    )
                    selectedCategory = null
                },
                onDelete = {
                    categoryViewModel.deleteCategory(category.categoryID);
                    selectedCategory = null
                }
            )
        }
    }
}

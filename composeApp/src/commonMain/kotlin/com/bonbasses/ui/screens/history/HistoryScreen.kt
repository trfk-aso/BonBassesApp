package com.bonbasses.ui.screens.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import bonbassesapp.composeapp.generated.resources.Res
import bonbassesapp.composeapp.generated.resources.btn_favorite_selected
import bonbassesapp.composeapp.generated.resources.btn_favorite_unselected
import bonbassesapp.composeapp.generated.resources.ic_filters
import bonbassesapp.composeapp.generated.resources.img_history_empty
import com.bonbasses.data.models.WritingHistoryItem
import com.bonbasses.platform.utils.TimeUtils
import com.bonbasses.ui.components.AppBottomNavigation
import com.bonbasses.ui.screens.home.getBackIcon
import com.bonbasses.ui.screens.home.getSettingsIcon
import com.bonbasses.ui.screens.result.getStarIcon
import com.bonbasses.ui.theme.AppBorders
import com.bonbasses.ui.theme.AppColors
import com.bonbasses.ui.theme.AppRadius
import com.bonbasses.ui.theme.AppSizes
import com.bonbasses.ui.theme.AppSpacing
import com.bonbasses.ui.theme.AppTypography
import com.bonbasses.ui.theme.LocalThemeColors
import com.bonbasses.ui.theme.RobotoSlabFontFamily
import com.bonbasses.ui.theme.getBackgroundBrush
import com.bonbasses.ui.theme.ResponsiveAppTypography
import com.bonbasses.ui.theme.ResponsiveAppSpacing
import com.bonbasses.ui.theme.ResponsiveAppSizes
import com.bonbasses.ui.theme.ResponsiveAppRadius
import com.bonbasses.ui.theme.ResponsiveAppBorders
import com.bonbasses.ui.theme.LocalScreenSizeConfig
import org.jetbrains.compose.resources.painterResource

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel { error("HistoryViewModel must be provided") },
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onNavigateToTab: (String) -> Unit = {},
    onNavigateToDetail: (WritingHistoryItem) -> Unit = {},
    onExportTxt: () -> Unit = {},
    onExportCsv: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val editedTitles = remember { mutableStateMapOf<Long, String>() }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundBrush())
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (isEditMode) {
                    focusManager.clearFocus()
                    editedTitles.forEach { (id, newTitle) ->
                        val item = uiState.filteredItems.find { it.id == id }
                        if (item != null && newTitle.isNotBlank() && newTitle != item.title) {
                            viewModel.updateTitle(id, newTitle.trim())
                        }
                    }
                    editedTitles.clear()
                    isEditMode = false
                }
            }
    ) {
        HistoryTopBar(
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onFilterClick = { showFilterDialog = true }
        )
        
        Box(modifier = Modifier.weight(1f)) {
            if (uiState.filteredItems.isEmpty()) {
                HistoryEmptyState()
            } else {
                HistoryListContent(
                    items = uiState.filteredItems,
                    selectedItems = uiState.selectedItems,
                    isEditMode = isEditMode,
                    editedTitles = editedTitles,
                    onItemClick = { item ->
                        if (!isEditMode) {
                            if (uiState.selectedItems.isEmpty()) {
                                onNavigateToDetail(item)
                            } else {
                                viewModel.toggleItemSelection(item.id)
                            }
                        }
                    },
                    onToggleSelection = { viewModel.toggleItemSelection(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                )
            }
        }
        
        if (uiState.filteredItems.isNotEmpty()) {
            HistoryBottomActions(
                hasSelection = uiState.selectedItems.isNotEmpty(),
                onExportTxt = onExportTxt,
                onExportCsv = onExportCsv,
                onEdit = {
                    isEditMode = !isEditMode
                    if (!isEditMode) {
                        editedTitles.forEach { (id, newTitle) ->
                            val item = uiState.filteredItems.find { it.id == id }
                            if (item != null && newTitle.isNotBlank() && newTitle != item.title) {
                                viewModel.updateTitle(id, newTitle.trim())
                            }
                        }
                        editedTitles.clear()
                    }
                },
                onDelete = { 
                    if (uiState.selectedItems.isNotEmpty()) {
                        showDeleteDialog = true
                    }
                }
            )
        }
        
        AppBottomNavigation(
            selectedTab = "history",
            onTabSelected = onNavigateToTab
        )
    }
    
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deleteSelectedItems()
                showDeleteDialog = false
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
    
    if (showFilterDialog) {
        FilterDialog(
            filterState = uiState.currentFilter,
            onDismiss = { showFilterDialog = false },
            onApply = { filterState ->
                viewModel.applyFilter(filterState)
                showFilterDialog = false
            },
            onReset = {
                viewModel.resetFilter()
                showFilterDialog = false
            }
        )
    }
}

@Composable
fun HistoryTopBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)
            .padding(top = ResponsiveAppSpacing.ScreenTop),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ResponsiveAppSizes.TouchTarget)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = getBackIcon(),
                contentDescription = "Back",
                modifier = Modifier.size(ResponsiveAppSizes.IconMedium),
                colorFilter = ColorFilter.tint(AppColors.TitleText)
            )
        }
        
        Text(
            text = "History",
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.TitleMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.SemiBold
        )
        
        Box(
            modifier = Modifier
                .size(ResponsiveAppSizes.TouchTarget)
                .clickable(onClick = onFilterClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_filters),
                contentDescription = "Filter",
                modifier = Modifier.size(ResponsiveAppSizes.IconMedium),
                colorFilter = ColorFilter.tint(AppColors.TitleText)
            )
        }
    }
}

@Composable
fun HistoryEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No entries yet",
            color = AppColors.TitleText,
            fontSize = ResponsiveAppTypography.BodyMedium,
            fontFamily = RobotoSlabFontFamily(),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(ResponsiveAppSpacing.ExtraLarge))
        
        Image(
            painter = painterResource(Res.drawable.img_history_empty),
            contentDescription = "No entries",
            modifier = Modifier.size(200.dp)
        )
    }
}

@Composable
fun HistoryListContent(
    items: List<WritingHistoryItem>,
    selectedItems: Set<Long>,
    isEditMode: Boolean,
    editedTitles: SnapshotStateMap<Long, String>,
    onItemClick: (WritingHistoryItem) -> Unit,
    onToggleSelection: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        items(items, key = { it.id }) { item ->
            HistoryItemCard(
                item = item,
                isSelected = item.id in selectedItems,
                isEditMode = isEditMode,
                editedTitle = editedTitles[item.id] ?: item.title,
                onEditedTitleChange = { newTitle ->
                    editedTitles[item.id] = newTitle
                },
                onItemClick = { onItemClick(item) },
                onToggleSelection = { onToggleSelection(item.id) },
                onToggleFavorite = { onToggleFavorite(item.id) }
            )
        }
    }
}

@Composable
fun HistoryItemCard(
    item: WritingHistoryItem,
    isSelected: Boolean,
    isEditMode: Boolean,
    editedTitle: String,
    onEditedTitleChange: (String) -> Unit,
    onItemClick: () -> Unit,
    onToggleSelection: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val dateText = remember(item.createdAt) {
        TimeUtils.formatDateDDMMYY(item.createdAt)
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .border(
                width = 1.5.dp,
                color = Color(0xFFAD8E7D),
                shape = RoundedCornerShape(15.dp)
            )
            .background(
                color = if (isSelected) Color(0xFF4A3228) else Color(0xFF312117),
                shape = RoundedCornerShape(15.dp)
            )
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onToggleSelection
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (isEditMode) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = editedTitle,
                        onValueChange = onEditedTitleChange,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFFD8BCAC),
                            fontSize = 16.sp,
                            fontFamily = RobotoSlabFontFamily(),
                            fontWeight = FontWeight.Medium
                        ),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF4A3228),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                } else {
                    Text(
                        text = item.title,
                        color = Color(0xFFD8BCAC),
                        fontSize = 16.sp,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Text(
                    text = dateText,
                    color = Color(0xFFAD8E7D),
                    fontSize = 12.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Normal
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF8E7A6D),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.genre,
                    color = Color(0xFF1F140F),
                    fontSize = 11.sp,
                    fontFamily = RobotoSlabFontFamily(),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            if (item.score != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.score.toString(),
                        color = Color(0xFFD8BCAC),
                        fontSize = 14.sp,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Medium
                    )
                    
                    Image(
                        painter = getStarIcon(),
                        contentDescription = "Star",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable(onClick = onToggleFavorite),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        if (item.isFavorite) Res.drawable.btn_favorite_selected
                        else Res.drawable.btn_favorite_unselected
                    ),
                    contentDescription = "Favorite",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun HistoryBottomActions(
    hasSelection: Boolean,
    onExportTxt: () -> Unit,
    onExportCsv: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ResponsiveAppSpacing.ScreenHorizontal)
            .padding(bottom = ResponsiveAppSpacing.Small),
        horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.ExtraSmall)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(ResponsiveAppSizes.ButtonHeight)
                .border(
                    width = ResponsiveAppBorders.Medium,
                    color = Color(0xFFAD8E7D),
                    shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                )
                .background(
                    color = Color(0xFF613923),
                    shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                )
                .clickable(onClick = onExportTxt),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Export TXT/CSV",
                color = Color(0xFFAD8E7D),
                fontSize = ResponsiveAppTypography.BodyMedium,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(ResponsiveAppSizes.ButtonHeight)
                .border(
                    width = ResponsiveAppBorders.Medium,
                    color = Color(0xFFAD8E7D),
                    shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                )
                .background(
                    color = Color(0xFF613923),
                    shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                )
                .clickable(onClick = onEdit),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Edit",
                color = Color(0xFFAD8E7D),
                fontSize = ResponsiveAppTypography.BodyMedium,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(ResponsiveAppSizes.ButtonHeight)
                .border(
                    width = ResponsiveAppBorders.Medium,
                    color = Color(0xFFAD8E7D),
                    shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                )
                .background(
                    color = Color(0xFF360F01),
                    shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                )
                .clickable(enabled = hasSelection, onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Delete",
                color = Color(0xFFAD8E7D),
                fontSize = ResponsiveAppTypography.BodyMedium,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(
                    color = themeColors.dialogBackground,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(enabled = false) { }
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Are you sure you want to\ndelete this data?",
                color = themeColors.dialogText,
                fontSize = 20.sp,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "This action cannot be undone.",
                color = themeColors.dialogText,
                fontSize = 14.sp,
                fontFamily = RobotoSlabFontFamily(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ResponsiveAppSpacing.Small)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ResponsiveAppSizes.ButtonHeight)
                        .border(
                            width = ResponsiveAppBorders.Medium,
                            color = Color(0xFFAD8E7D),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                        )
                        .background(
                            color = Color(0xFF613923),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                        )
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFFAD8E7D),
                        fontSize = ResponsiveAppTypography.TitleSmall,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Normal
                    )
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ResponsiveAppSizes.ButtonHeight)
                        .border(
                            width = ResponsiveAppBorders.Medium,
                            color = Color(0xFFAD8E7D),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                        )
                        .background(
                            color = Color(0xFF360F01),
                            shape = RoundedCornerShape(ResponsiveAppRadius.Medium)
                        )
                        .clickable(onClick = onConfirm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Delete",
                        color = Color(0xFFAD8E7D),
                        fontSize = ResponsiveAppTypography.TitleSmall,
                        fontFamily = RobotoSlabFontFamily(),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

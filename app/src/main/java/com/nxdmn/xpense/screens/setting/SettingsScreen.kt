package com.nxdmn.xpense.screens.setting

import android.icu.util.Currency
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxdmn.xpense.R
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.ui.components.DeleteConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
    onNavigateToCategoryDetail: (Long?) -> Unit
) {
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            settingsViewModel.refreshCategoryList()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = {
                    Text("Settings")
                },
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            CategoryList(
                categoryList = settingsUiState.categoryList,
                onNavigateToCategoryDetail = onNavigateToCategoryDetail,
                deleteCategory = { settingsViewModel.deleteCategory(it) }
            )

            HorizontalDivider(color = Color.LightGray)

            var openDialog by remember { mutableStateOf(false) }

            SettingsListItem(
                title = "Currency",
                leading = {
                    Icon(
                        painterResource(R.drawable.baseline_currency_exchange_24),
                        contentDescription = "Currency",
                        modifier = Modifier.size(36.dp)
                    )
                },
                trailing = {
                    Text(
                        settingsUiState.currencySymbol ?: "",
                        modifier = Modifier.height(24.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                },
                onClick = { openDialog = true },
                isBottom = true,
            )

            CurrencySelectionDialog(
                openDialog = openDialog,
                onDismiss = { openDialog = false },
                currencySymbolMap = settingsUiState.currencySymbolMap,
                onCurrencySelected = {
                    settingsViewModel.updateCurrency(it)
                    openDialog = false
                }
            )
        }
    }
}

@Composable
fun SettingsListItem(
    title: String,
    leading: @Composable () -> Unit = {},
    trailing: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
    isTop: Boolean = false,
    isBottom: Boolean = false,
) {
    Surface(
        shape = if (isTop) RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
        else if (isBottom) RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
        else RectangleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = onClick,
    ) {
        ListItem(
            headlineContent = {
                Text(title, fontSize = 20.sp)
            },
            modifier = Modifier.padding(10.dp),
            leadingContent = leading,
            trailingContent = trailing,
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        )
    }
}

@Composable
fun CategoryList(
    categoryList: List<CategoryModel>,
    onNavigateToCategoryDetail: (Long?) -> Unit,
    deleteCategory: (CategoryModel) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    SettingsListItem(
        title = "Category",
        leading = {
            Icon(
                painterResource(R.drawable.round_category_24),
                contentDescription = "Category",
                modifier = Modifier.size(36.dp)
            )
        },
        trailing = {
            Row(
                modifier = Modifier.height(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onNavigateToCategoryDetail(null)
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Category")
                }
                Icon(
                    Icons.AutoMirrored.Sharp.KeyboardArrowRight,
                    contentDescription = "Right Arrow",
                    modifier = Modifier.rotate(if (isExpanded) 90f else 0f)
                )
            }
        },
        isTop = true,
        onClick = {
            isExpanded = !isExpanded
        }
    )
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column {
            categoryList.forEach {
                var openDeleteDialog by remember { mutableStateOf(false) }
                DeleteConfirmationDialog(
                    description = "Are you sure you want to delete this category?",
                    openDialog = openDeleteDialog,
                    onDismiss = { openDeleteDialog = false },
                    onConfirmClicked = {
                        deleteCategory(it)
                        openDeleteDialog = false
                    },
                )
                SettingsListItem(
                    title = it.name,
                    leading = {
                        Icon(
                            painterResource(id = it.icon.resId),
                            contentDescription = it.name,
                            modifier = Modifier
                                .size(36.dp)
                                .border(
                                    1.dp, Color(it.color), CircleShape
                                )
                                .clip(CircleShape)
                                .background(Color(it.color))
                                .padding(5.dp)
                        )
                    },
                    trailing = {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            IconButton(onClick = {
                                openDeleteDialog = true
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Category")
                            }
                            IconButton(onClick = {
                                onNavigateToCategoryDetail(it.id)
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Category")
                            }
                        }
                    },
                    onClick = {
                        onNavigateToCategoryDetail(it.id)
                    }
                )
            }
        }
    }
}

@Composable
fun CurrencySelectionDialog(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    currencySymbolMap: Map<Currency, String>,
    onCurrencySelected: (Currency) -> Unit
) {
    if (openDialog)
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier.heightIn(max = 600.dp),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    items(currencySymbolMap.keys.toList(), key = { c -> c.hashCode() }) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    remember { MutableInteractionSource() },
                                    indication = ripple(),
                                    onClick = {
                                        onCurrencySelected(it)
                                    }
                                )
                                .padding(vertical = 20.dp),
                        ) {
                            Text("${it.displayName} - ${it.currencyCode} (${currencySymbolMap[it]})")
                        }
                    }
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = Devices.PIXEL_7_PRO)
@Composable
fun TestPreview() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = {
                    Text("Settings")
                },
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            SettingsListItem(
                title = "Category",
                leading = {
                    Icon(
                        painterResource(R.drawable.round_category_24),
                        contentDescription = "Category",
                        modifier = Modifier.size(36.dp)
                    )
                },
                trailing = {
                    Row(
                        modifier = Modifier.height(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Category")
                        }
                        Icon(
                            Icons.AutoMirrored.Sharp.KeyboardArrowRight,
                            contentDescription = "Right Arrow",
                        )
                    }
                },
                isTop = true,
                onClick = { }
            )

            HorizontalDivider(color = Color.LightGray)

            var openDialog by remember { mutableStateOf(false) }

            SettingsListItem(
                title = "Currency",
                leading = {
                    Icon(
                        painterResource(R.drawable.baseline_currency_exchange_24),
                        contentDescription = "Currency",
                        modifier = Modifier.size(36.dp)
                    )
                },
                trailing = {
                    Text(
                        "SGD",
                        modifier = Modifier.height(24.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                },
                onClick = { openDialog = true },
                isBottom = true,
            )
        }
    }
}
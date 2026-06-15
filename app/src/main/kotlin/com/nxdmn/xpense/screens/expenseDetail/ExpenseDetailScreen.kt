package com.nxdmn.xpense.screens.expenseDetail

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxdmn.xpense.AppBarState
import com.nxdmn.xpense.R
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.helpers.toEpochMilli
import com.nxdmn.xpense.ui.CategoryIcon
import com.nxdmn.xpense.ui.DisplayState
import com.nxdmn.xpense.ui.components.CategoryLabel
import com.nxdmn.xpense.ui.components.CurrencyTextField
import com.nxdmn.xpense.ui.components.DeleteConfirmationDialog
import com.nxdmn.xpense.ui.components.ErrorDialog
import com.nxdmn.xpense.ui.components.PhotoGrid
import com.nxdmn.xpense.ui.components.SelectionListDialog
import com.nxdmn.xpense.ui.states.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    appBarState: AppBarState,
    expenseDetailViewModel: ExpenseDetailViewModel = viewModel(factory = ExpenseDetailViewModel.Factory()),
    onNavigateToCamera: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val expenseDetailUiState by expenseDetailViewModel.uiState.collectAsState()

    var openDeleteDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Scaffold(
        Modifier.semantics { contentDescription = "ExpenseDetail" },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = {
                    Text("Expense")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onNavigateBack()
                        },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Icon(
                            painterResource(R.drawable.outline_arrow_back_24),
                            contentDescription = "Back Button"
                        )
                    }
                },
                actions = {
                    if (expenseDetailUiState.isEdit)
                        IconButton(
                            onClick = {
                                openDeleteDialog = true
                            },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                        ) {
                            Icon(
                                painterResource(R.drawable.baseline_delete_24),
                                contentDescription = "Delete"
                            )
                        }
                }
            )
        },
    ) { innerPadding ->
        when (expenseDetailUiState.displayState) {
            is DisplayState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DisplayState.Error -> {
                var openDialog by remember { mutableStateOf(true) }
                if (openDialog)
                    ErrorDialog(onDismiss = {
                        openDialog = false
                        onNavigateBack()
                    })
            }

            is DisplayState.Content -> {
                Column(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        }
                        .padding(top = innerPadding.calculateTopPadding())
                        .verticalScroll(rememberScrollState())
                        .let {
                            val configuration = LocalConfiguration.current
                            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                it.windowInsetsPadding(
                                    WindowInsets.safeContent.only(
                                        WindowInsetsSides.Horizontal
                                    )
                                )
                            } else it
                        }
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (openDeleteDialog)
                        DeleteConfirmationDialog(
                            description = "Are you sure you want to delete this expense?",
                            onDismiss = { openDeleteDialog = false },
                            onConfirmClicked = {
                                expenseDetailViewModel.deleteExpense()
                                openDeleteDialog = false
                                onNavigateBack()
                            },
                        )

                    CurrencyTextField(
                        currencyCode = expenseDetailUiState.currencyCode,
                        amount = if (expenseDetailUiState.expense.amount == 0.0) "" else "%.2f".format(
                            expenseDetailUiState.expense.amount
                        ),
                        onValueChanged = {
                            expenseDetailViewModel.updateAmount(it)
                        },
                        errorText = expenseDetailUiState.amountErrorText
                    )

                    val datePickerState =
                        rememberDatePickerState(initialSelectedDateMillis = expenseDetailUiState.expense.date.toEpochMilli())
                    datePickerState.selectedDateMillis?.let {
                        expenseDetailViewModel.updateDate(it)
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(bottom = 20.dp),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        DatePicker(
                            state = datePickerState,
                            title = null,
                            headline = null,
                            showModeToggle = false
                        )
                    }

                    CategorySection(
                        expenseDetailUiState.categoryList,
                        expenseDetailUiState.expense.category!!
                    ) {
                        expenseDetailViewModel.updateCategory(it)
                    }

                    TextField(
                        value = expenseDetailUiState.expense.remarks,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = {
                            expenseDetailViewModel.updateRemarks(it)
                        },
                        label = { Text("Remarks") }
                    )

                    var selectedImages by remember { mutableStateOf<List<String>>(emptyList()) }

                    PhotoGrid(
                        imagePaths = expenseDetailUiState.expense.images,
                        onSelectionChanged = {
                            selectedImages = it
                        })

                    val context = LocalContext.current
                    val imagePicker = if (expenseDetailUiState.allowImages > 1) {
                        rememberLauncherForActivityResult(
                            ActivityResultContracts.PickMultipleVisualMedia(
                                expenseDetailUiState.allowImages
                            )
                        ) { uris ->
                            if (uris.isNotEmpty()) {
                                uris.forEach {
                                    context.contentResolver.takePersistableUriPermission(
                                        it,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    )
                                    expenseDetailViewModel.addImage(it.toString())
                                }
                            }
                        }
                    } else {
                        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                            if (uri != null) {
                                context.contentResolver.takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                )
                                expenseDetailViewModel.addImage(uri.toString())
                            }
                        }
                    }

                    var openAddImageDialog by remember { mutableStateOf(false) }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = {
                            openAddImageDialog = true
                        }) {
                            Text("Add Image")
                        }
                        Button(onClick = {
                            expenseDetailViewModel.removeImages(selectedImages)
                        }) {
                            Text("Delete Image")
                        }
                    }

                    var openPermissionDialog by remember { mutableStateOf(false) }
                    var openPermissionDeniedDialog by remember { mutableStateOf(false) }

                    val cameraPermissionState =
                        rememberPermissionState(
                            Manifest.permission.CAMERA,
                            onGranted = {
                                onNavigateToCamera()
                            },
                            showPermissionDialog = { openPermissionDialog = true },
                            showPermissionDeniedDialog = { openPermissionDeniedDialog = true }
                        )

                    if (openPermissionDeniedDialog)
                        PermissionDialog(
                            title = "Permission Denied",
                            description = "Camera access has been disabled. To use this feature, please enable the camera permission in your device settings.",
                            dismissText = "Okay",
                            confirmText = null,
                            onDismiss = {
                                openPermissionDeniedDialog = false
                            }
                        )

                    val addImageOptions = listOf("Pick Image", "Take Photo")
                    if (openAddImageDialog)
                        SelectionListDialog(
                            addImageOptions,
                            onClicked = {
                                if (it == addImageOptions[0]) imagePicker.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                                else if (it == addImageOptions[1]) {
                                    cameraPermissionState.requestPermission()
                                }
                            },
                            onDismiss = { openAddImageDialog = false }
                        ) {
                            Text(it)
                        }

                    if (openPermissionDialog)
                        PermissionDialog(
                            title = "Camera Permission",
                            description = "We need access to your camera so you can take photos directly in the app. We’ll only use it for this purpose.",
                            onDismiss = { openPermissionDialog = false },
                            onConfirm = {
                                cameraPermissionState.requestPermission(showPermissionRationale = false)
                            }
                        )


                    appBarState.saveExpenseDetail = {
                        focusManager.clearFocus()
                        if (expenseDetailViewModel.saveExpense()) {
                            onNavigateBack()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySection(
    categoryList: List<CategoryModel>,
    selectedCategory: CategoryModel,
    onCategorySelected: (CategoryModel) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categoryList.forEach {
            CategoryLabel(
                icon = it.icon,
                text = it.name,
                selected = selectedCategory.id == it.id,
                onClicked = {
                    onCategorySelected(it)
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    title: String,
    description: String,
    dismissText: String = "Cancel",
    confirmText: String? = "Okay",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = {},
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    title,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = description)
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(dismissText)
                    }
                    if (confirmText != null)
                        TextButton(
                            onClick = {
                                onConfirm()
                                onDismiss()
                            },
                        ) {
                            Text(confirmText)
                        }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = Devices.PIXEL_7_PRO, heightDp = 2000)
@Composable
fun TestPreview() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Expense")
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painterResource(R.drawable.outline_arrow_back_24),
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CurrencyTextField(
                currencyCode = "SGD",
                amount = "",
                onValueChanged = {},
            )

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(20.dp)

            ) {
                DatePicker(
                    state = rememberDatePickerState(),
                    title = null,
                    headline = null,
                    showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        containerColor = Color.Blue
                    )
                )
            }

            var selected by remember {
                mutableStateOf(CategoryModel(name = "Food", icon = CategoryIcon.EATING))
            }
            CategorySection(
                listOf(
                    CategoryModel(name = "Food", icon = CategoryIcon.EATING),
                    CategoryModel(name = "Clothes", icon = CategoryIcon.CLOTHING),
                    CategoryModel(name = "Others", icon = CategoryIcon.OTHERS),
                    CategoryModel(name = "Entertainment", icon = CategoryIcon.ENTERTAINMENT),
                    CategoryModel(name = "Family", icon = CategoryIcon.FAMILY),
                    CategoryModel(name = "Fuel", icon = CategoryIcon.FUEL),
                    CategoryModel(name = "Gift", icon = CategoryIcon.GIFT),
                    CategoryModel(name = "Groceries", icon = CategoryIcon.GROCERIES),
                    CategoryModel(name = "Rental", icon = CategoryIcon.HOME),
                    CategoryModel(name = "Medical", icon = CategoryIcon.MEDICAL),
                    CategoryModel(name = "Phone bill", icon = CategoryIcon.PHONE_BILL),
                    CategoryModel(name = "Shopping", icon = CategoryIcon.SHOPPING),
                    CategoryModel(name = "Sports", icon = CategoryIcon.SPORTS),
                    CategoryModel(name = "Trip", icon = CategoryIcon.TRAVEL),
                    CategoryModel(name = "Utilities", icon = CategoryIcon.UTILITIES),
                    CategoryModel(name = "Insurance", icon = CategoryIcon.LIFE),
                ),
                selected
            ) { selected = it }


            TextField(
                value = "",
                onValueChange = { },
                label = { Text("Remarks") }
            )


            Button(onClick = {}) {
                Text("Add Image")
            }

            Button(onClick = {}) {
                Text("Add")
            }

            PhotoGrid(imagePaths = listOf())
        }
    }
}
package com.nxdmn.xpense.screens.expenseDetail

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxdmn.xpense.AppBarState
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.helpers.readImage
import com.nxdmn.xpense.helpers.readImageFromPath
import com.nxdmn.xpense.helpers.toEpochMilli
import com.nxdmn.xpense.helpers.toLocalDate
import com.nxdmn.xpense.screens.expenseList.ExpenseCard
import com.nxdmn.xpense.ui.CategoryIcon
import com.nxdmn.xpense.ui.components.CategoryLabel
import com.nxdmn.xpense.ui.components.CurrencyTextField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseDetailScreen(
    appBarState: AppBarState,
    expenseDetailViewModel: ExpenseDetailViewModel = viewModel(),
    onNavigateToList: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val expenseDetailUiState by expenseDetailViewModel.uiState.collectAsState()
    val expense: ExpenseModel = expenseDetailUiState.expense

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
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var amount by remember { mutableStateOf(if (expense.amount == 0.0) "" else expense.amount.toString()) }
            CurrencyTextField(
                amount = amount,
                onValueChanged = {
                    amount = if (it.isEmpty()) {
                        it.trim()
                    } else {
                        when (it.toDoubleOrNull()) {
                            null -> amount
                            else -> it.trim()
                        }
                    }
                    expenseDetailViewModel.updateAmount(amount)
                },
            )

            val datePickerState =
                rememberDatePickerState(initialSelectedDateMillis = expense.date.toEpochMilli())
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

            var category by remember { mutableStateOf(expense.category) }
            CategorySection(expenseDetailUiState.categoryList, category) {
                category = it
                expenseDetailViewModel.updateCategory(category)
            }

            var remarks by remember { mutableStateOf(expense.remarks) }
            TextField(
                value = remarks,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {
                    remarks = it
                    expenseDetailViewModel.updateRemarks(remarks)
                },
                label = { Text("Remarks") }
            )

            val context = LocalContext.current
            var imageBitmap by remember {
                mutableStateOf(
                    readImageFromPath(
                        context,
                        expense.image
                    )
                )
            }
            imageBitmap?.let {
                it.prepareToDraw()
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(300.dp)
                )
            }

            val imagePicker =
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    if (uri != null) {
                        expenseDetailViewModel.updateImage(uri.toString())
                        imageBitmap =
                            readImage(context.contentResolver, uri)
                    }
                }
            Button(onClick = {
                imagePicker.launch("image/*")
            }) {
                Text("Add Image")
            }

            appBarState.saveExpenseDetail = {
                if (expense.amount > 0.0) {
                    expenseDetailViewModel.saveExpense()
                    onNavigateToList()
                } else
                    Toast.makeText(context, "Please Enter amount", Toast.LENGTH_SHORT).show()
            }

            Spacer(modifier = Modifier.padding(bottom = 30.dp))
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CurrencyTextField(
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
                selected,
                { selected = it }
            )


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
        }
    }
}
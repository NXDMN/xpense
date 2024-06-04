package com.nxdmn.xpense.screens.expenseDetail

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.helpers.readImage
import com.nxdmn.xpense.helpers.readImageFromPath
import com.nxdmn.xpense.helpers.toEpochMilli

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
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
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            var amount by remember { mutableStateOf(expense.amount.toString()) }
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") }
            )

            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = expense.date.toEpochMilli())
            DatePicker(
                state = datePickerState,
                modifier = Modifier.padding(16.dp),
                title = null,
                headline = null,
                showModeToggle = false,
            )

            var category by remember { mutableStateOf(expense.category) }
            TextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") }
            )

            var remarks by remember { mutableStateOf(expense.remarks) }
            TextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Remarks") }
            )

            val context = LocalContext.current
            var imageBitmap by remember { mutableStateOf(readImageFromPath(context, expense.image)) }
            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null
                )
            }

            val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    expense.image = uri.toString()
                    imageBitmap =
                        readImage(context.contentResolver, uri)
                }
            }
            Button(onClick = {
                imagePicker.launch("image/*")
            }) {
                Text("Add Image")
            }

            Button(onClick = {
                expense.amount = amount.toIntOrNull() ?: 0
                expense.remarks = remarks

                if (expense.amount >= 0) {
                    expenseDetailViewModel.saveExpense(expense)
                    onNavigateToList()
                } else
                    Toast.makeText(context, "Please Enter amount", Toast.LENGTH_SHORT).show()

            }) {
                Text("Add")
            }
        }
    }
}

@Preview
@Composable
fun TestPreview() {

}
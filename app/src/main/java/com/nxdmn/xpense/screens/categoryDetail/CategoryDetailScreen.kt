package com.nxdmn.xpense.screens.categoryDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxdmn.xpense.ui.CategoryIcon

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryDetailScreen(
    categoryDetailViewModel: CategoryDetailViewModel = viewModel(factory = CategoryDetailViewModel.Factory),
    onNavigateBack: () -> Unit = {}
) {
    val categoryDetailUiState = categoryDetailViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Category")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            val focusManager = LocalFocusManager.current

            TextField(
                value = categoryDetailUiState.value.name ?: "",
                onValueChange = {
                    categoryDetailViewModel.updateName(it)
                },
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleMedium,
                label = {
                    Text(text = "Name")
                },
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CategoryIcon.entries.forEach { icon ->
                    Icon(
                        painterResource(icon.resId),
                        contentDescription = icon.name,
                        modifier = Modifier
                            .let {
                                if (categoryDetailUiState.value.icon?.ordinal == icon.ordinal) it.border(
                                    1.dp,
                                    Color.Black
                                )
                                else it
                            }
                            .clickable(
                                remember { MutableInteractionSource() },
                                indication = ripple(),
                                onClick = {
                                    categoryDetailViewModel.updateIcon(icon)
                                }
                            )
                            .background(
                                if (categoryDetailUiState.value.icon?.ordinal == icon.ordinal && categoryDetailUiState.value.color != null)
                                    Color(categoryDetailUiState.value.color!!)
                                else Color.Transparent
                            )
                            .padding(5.dp)
                    )
                }
            }

            categoryDetailUiState.value.color?.let {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(it))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Preview(device = Devices.PIXEL_7_PRO)
@Composable
fun TestPreview() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Category")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {},
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var name by remember { mutableStateOf("") }
            TextField(
                value = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleMedium,
                label = {
                    Text(text = "Name")
                },
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
            var selectedIcon by remember { mutableStateOf(CategoryIcon.LUNCH) }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CategoryIcon.entries.forEach { icon ->
                    Icon(
                        painterResource(icon.resId),
                        contentDescription = icon.name,
                        modifier = Modifier
                            .let {
                                if (selectedIcon.ordinal == icon.ordinal) it.border(
                                    1.dp,
                                    Color.Black
                                )
                                else it
                            }
                            .clickable(
                                remember { MutableInteractionSource() },
                                indication = ripple(),
                                onClick = {
                                    selectedIcon = icon
                                }
                            )
                            .padding(5.dp)
                    )
                }
            }
        }
    }
}
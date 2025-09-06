package com.nxdmn.xpense.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun <T> SelectionListDialog(
    items: List<T>,
    onClicked: ((T) -> Unit)? = null,
    onDismiss: () -> Unit,
    content: @Composable (T) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.heightIn(max = 600.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                itemsIndexed(items, key = { index, _ -> index }) { _, item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                remember { MutableInteractionSource() },
                                indication = ripple(),
                                enabled = onClicked != null,
                                onClick = {
                                    onClicked?.invoke(item)
                                    onDismiss()
                                }
                            )
                            .padding(16.dp)
                    ) {
                        content(item)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 500)
@Composable
fun SelectionListDialogPreview() {
    SelectionListDialog(
        items = listOf(
            "Apple",
            "Banana",
            "Apple",
            "Banana",
            "Apple",
            "Banana",
            "Apple",
            "Banana",
            "Apple",
            "Banana",
            "Apple",
            "Banana",
            "Apple",
            "Banana",
            "Apple",
            "Banana"
        ),
        onClicked = {},
        onDismiss = {},
    ) {
        Text(it)
    }
}
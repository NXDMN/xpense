package com.nxdmn.xpense.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlertDialogBase(
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    buttons: @Composable () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(text = description, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    buttons()
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    description: String,
    onDismiss: () -> Unit,
    onConfirmClicked: () -> Unit,
) {
    AlertDialogBase(
        title = "Delete Confirmation",
        description = description,
        onDismissRequest = onDismiss
    ) {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
        TextButton(onClick = onConfirmClicked) {
            Text("Confirm")
        }
    }
}

@Composable
fun ErrorDialog(message: String? = null, onDismiss: () -> Unit) {
    AlertDialogBase(
        title = "Error",
        description = message ?: "An error has occurred",
        onDismissRequest = onDismiss
    ) {
        TextButton(onClick = onDismiss) {
            Text("Okay")
        }
    }
}
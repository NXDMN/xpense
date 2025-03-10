package com.nxdmn.xpense.ui.components

import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.math.RoundingMode

@Composable
fun CurrencyTextField(
    currencyCode: String,
    amount: String,
    onValueChanged: (String) -> Unit,
    isError: Boolean = false,
    errorText: String = ""
) {
    val df = remember { DecimalFormat("0.00").also { it.roundingMode = RoundingMode.DOWN.ordinal } }
    TextField(
        value = amount,
        onValueChange = {
            // Prevent the case when the cursor after '.' and backspace to delete '.',
            // like 98.76 will become 9876.00
            if (amount.contains('.') && !it.contains('.')) {
                onValueChanged(amount)
            } else {
                val newValue = when (it.toDoubleOrNull()) {
                    is Double -> df.format(it.toDoubleOrNull())
                    null -> amount
                    else -> ""
                }
                onValueChanged(newValue)
            }
        },
        modifier = Modifier
            .padding(40.dp)
            .width(IntrinsicSize.Min),
        textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
        placeholder = {
            Text(
                text = "Amount",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
            )
        },
        prefix = {
            Text(
                text = currencyCode,
                modifier = Modifier.padding(end = 16.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        supportingText = {
            if (isError) Text(errorText)
        },
        isError = isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(30.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun CurrencyTextFieldPreview() {
    CurrencyTextField(
        currencyCode = "$",
        amount = "10",
        onValueChanged = {},
        isError = true
    )
}
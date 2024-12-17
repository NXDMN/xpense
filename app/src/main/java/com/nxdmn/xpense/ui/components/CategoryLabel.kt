package com.nxdmn.xpense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nxdmn.xpense.ui.CategoryIcon

@Composable
fun CategoryLabel(
    icon: CategoryIcon,
    text: String,
    selected: Boolean = false,
    onClicked: () -> Unit = {},
    allowToggle: Boolean = false,
) {

    var isSelected by remember(key1 = selected) { mutableStateOf(selected) }

    val shape = RoundedCornerShape(6.dp)

    Row(
        modifier = Modifier
            .border(1.dp, Color.Black, shape)
            .clip(shape)
            .clickable(
                remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = {
                    if (allowToggle) isSelected = !isSelected
                    onClicked()
                }
            )
            .let {
                if (isSelected) it.background(MaterialTheme.colorScheme.primaryContainer)
                else it
            }
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(icon.resId), contentDescription = text)
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryLabelPreview() {
    CategoryLabel(CategoryIcon.LUNCH, "Food")
}
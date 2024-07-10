package com.nxdmn.xpense.ui.components

import androidx.annotation.DrawableRes
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
import com.nxdmn.xpense.ui.MoneytreeIcon

@Composable
fun CategoryLabel(@DrawableRes iconId: Int, text: String, onClicked: () -> Unit = {}){

    var selected by remember{ mutableStateOf(false) }

    val shape = RoundedCornerShape(6.dp)

    Row(
        modifier = Modifier
            .border(1.dp, Color.Black, shape)
            .clip(shape)
            .clickable(
                remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = {
                    selected = !selected
                    onClicked()
                }
            )
            .let {
                if (selected) it.background(MaterialTheme.colorScheme.primaryContainer)
                else it
            }
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(iconId), contentDescription = text)
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryLabelPreview(){
    CategoryLabel(MoneytreeIcon.LUNCH,"Food")
}
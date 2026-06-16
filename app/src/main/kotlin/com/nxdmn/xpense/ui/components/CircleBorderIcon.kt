package com.nxdmn.xpense.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nxdmn.xpense.helpers.isLight

@Composable
fun CircleBorderIcon(@DrawableRes resId: Int, name: String = "", color: Long) {
    val backgroundColor = Color(color)
    val contentColor = if (backgroundColor.isLight()) Color.Black else Color.White

    Icon(
        painterResource(id = resId),
        contentDescription = name,
        modifier = Modifier
            .size(36.dp)
            .border(
                1.dp, backgroundColor, CircleShape
            )
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(5.dp),
        tint = contentColor
    )
}
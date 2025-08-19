package com.nxdmn.xpense.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nxdmn.xpense.R

@Composable
fun PhotoGrid(imagePath: List<String>) {
    var inSelectionMode by remember { mutableStateOf(false) }
    val selectedList = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        for (i in 0 until 4 step 2) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
            {
                for (j in 0..1) {
                    val hasImage = i + j < imagePath.size
                    val imagePath = if (hasImage) imagePath[i + j] else null
                    val isSelected = selectedList.contains(imagePath)
                    ImageItem(
                        hasImage = hasImage,
                        imagePath = imagePath,
                        inSelectionMode = inSelectionMode,
                        isSelected = isSelected,
                        onLongPress = {
                            inSelectionMode = true
                            if (!isSelected)
                                selectedList.add(imagePath!!)
                        },
                        onTap = {
                            if (!isSelected)
                                selectedList.add(imagePath!!)
                            else {
                                selectedList.remove(imagePath)
                                inSelectionMode = selectedList.isNotEmpty()
                            }
                        })
                }
            }
        }
    }
}

@Composable
private fun RowScope.ImageItem(
    hasImage: Boolean,
    imagePath: String?,
    inSelectionMode: Boolean,
    isSelected: Boolean,
    onLongPress: () -> Unit,
    onTap: () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .let {
                if (hasImage) {
                    it.pointerInput(inSelectionMode) {
                            detectTapGestures(
                                onLongPress = {
                                    onLongPress()
                                },
                            )
                        }
                        .pointerInput(inSelectionMode, isSelected) {
                            if (inSelectionMode) {
                                detectTapGestures(
                                    onTap = {
                                        onTap()
                                    }
                                )
                            }
                        }
                }
                else it
            }
            .background(Color.LightGray)
            .let {
                if (inSelectionMode && isSelected) it.padding(20.dp) else it
            },
        contentAlignment = Alignment.Center
    )
    {
        if (inSelectionMode && isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "",
                modifier = Modifier
                    .zIndex(1f)
                    .offset((-40).dp, (-40).dp)
                    .size(18.dp)
                    .border(
                        1.dp, Color.Transparent, CircleShape
                    )
                    .clip(CircleShape)
                    .background(Color.Green),
                tint = Color.White
            )
        }

        if (hasImage) {
            Image(
                painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.dp))
            )
        } else {
            Icon(
                painterResource(R.drawable.outline_image_24),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(0.5f),
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
fun PhotoGridPreview() {
    PhotoGrid(imagePath = listOf("a", "b", "c"))
}
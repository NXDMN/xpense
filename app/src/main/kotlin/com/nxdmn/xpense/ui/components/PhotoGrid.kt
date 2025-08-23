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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nxdmn.xpense.R

@Composable
fun PhotoGrid(imageBitmaps: List<ImageBitmap>) {
    var inSelectionMode by remember { mutableStateOf(false) }
    val selectedList = remember { mutableStateListOf<ImageBitmap>() }

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        for (i in 0 until 4 step 2) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
            {
                for (j in 0..1) {
                    val imageBitmap = if (i + j < imageBitmaps.size) imageBitmaps[i + j] else null
                    val isSelected = selectedList.contains(imageBitmap)
                    ImageItem(
                        imageBitmap = imageBitmap,
                        inSelectionMode = inSelectionMode,
                        isSelected = isSelected,
                        onLongPress = {
                            inSelectionMode = true
                            if (!isSelected)
                                selectedList.add(imageBitmap!!)
                        },
                        onTap = {
                            if (!isSelected)
                                selectedList.add(imageBitmap!!)
                            else {
                                selectedList.remove(imageBitmap)
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
    imageBitmap: ImageBitmap?,
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
                if (imageBitmap != null) {
                    it
                        .pointerInput(inSelectionMode) {
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
                } else it
            }
            .background(if (imageBitmap != null) MaterialTheme.colorScheme.surfaceContainerHighest else Color.LightGray)
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

        if (imageBitmap != null) {
            Image(
                imageBitmap,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
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
    PhotoGrid(imageBitmaps = listOf())
}
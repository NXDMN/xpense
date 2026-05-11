package com.nxdmn.xpense.ui.components

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.nxdmn.xpense.R
import com.nxdmn.xpense.helpers.readImageFromPath

@Composable
fun PhotoGrid(
    imagePaths: List<String>,
    onSelectionChanged: (List<String>) -> Unit = { }
) {
    var inSelectionMode by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateSetOf<String>() }

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        for (i in 0 until 4 step 2) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
            {
                for (j in 0..1) {
                    val imagePath = if (i + j < imagePaths.size) imagePaths[i + j] else null
                    val isSelected = selectedItems.contains(imagePath)
                    ImageItem(
                        imagePath = imagePath,
                        inSelectionMode = inSelectionMode,
                        isSelected = isSelected,
                        onLongPress = {
                            inSelectionMode = true
                            if (!isSelected) {
                                selectedItems.add(imagePath!!)
                                onSelectionChanged(selectedItems.toList())
                            }
                        },
                        onTap = {
                            if (!isSelected)
                                selectedItems.add(imagePath!!)
                            else {
                                selectedItems.remove(imagePath)
                                inSelectionMode = selectedItems.isNotEmpty()
                            }
                            onSelectionChanged(selectedItems.toList())
                        })
                }
            }
        }
    }
}

@Composable
private fun RowScope.ImageItem(
    imagePath: String?,
    inSelectionMode: Boolean,
    isSelected: Boolean,
    onLongPress: () -> Unit,
    onTap: () -> Unit,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    var openViewImageDialog by remember { mutableStateOf(false) }
    if (openViewImageDialog)
        Dialog(
            onDismissRequest = { openViewImageDialog = false },
        ) {
            val imageBitmap = remember(imagePath) { readImageFromPath(context, imagePath!!) }
            val imageRatio = imageBitmap!!.width.toFloat() / imageBitmap.height.toFloat()
            Image(
                imageBitmap,
                contentDescription = "",
                modifier = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(imageRatio)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(imageRatio)
                }
            )
        }

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .let {
                if (imagePath != null) {
                    it
                        .pointerInput(inSelectionMode) {
                            detectTapGestures(
                                onTap = {
                                    openViewImageDialog = true
                                },
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
            .background(if (imagePath != null) MaterialTheme.colorScheme.surfaceContainerHighest else Color.LightGray)
            .let {
                if (inSelectionMode && isSelected) it.padding(20.dp) else it
            },
        contentAlignment = Alignment.Center
    )
    {
        if (inSelectionMode && isSelected) {
            Icon(
                painterResource(R.drawable.outline_check_24),
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

        if (imagePath != null) {
            val imageBitmap = remember(imagePath) { readImageFromPath(context, imagePath) }
            Image(
                imageBitmap!!,
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
    PhotoGrid(imagePaths = listOf())
}
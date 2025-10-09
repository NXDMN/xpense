package com.nxdmn.xpense.screens.camera

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun CameraScreen(cameraViewModel: CameraViewModel = viewModel()) {
    val currentSurfaceRequest by cameraViewModel.surfaceRequests.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        cameraViewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    var showAutoFocusIndicator by remember { mutableStateOf(false) }
    var autofocusCoords by remember { mutableStateOf(Offset.Zero) }

    if (showAutoFocusIndicator) {
        LaunchedEffect(autofocusCoords) {
            delay(500)
            // Clear the offset to finish the request and hide the indicator
            showAutoFocusIndicator = false
        }
    }
    Box {
        currentSurfaceRequest?.let { surfaceRequest ->
            // CoordinateTransformer for transforming from Offsets to Surface coordinates
            val coordinateTransformer = remember { MutableCoordinateTransformer() }

            CameraXViewfinder(
                surfaceRequest = surfaceRequest,
                modifier =
                    Modifier.pointerInput(Unit) {
                        detectTapGestures {
                            with(coordinateTransformer) {
                                cameraViewModel.focusOnPoint(
                                    it.transform()
                                )
                                autofocusCoords = it
                                showAutoFocusIndicator = true
                            }
                        }
                    },
                coordinateTransformer = coordinateTransformer,
            )

            val indicatorSize = 48.dp
            AnimatedVisibility(
                visible = showAutoFocusIndicator,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .offset { autofocusCoords.round() }
                    .offset(
                        -indicatorSize / 2,
                        -indicatorSize / 2
                    ) // center point
            ) {
                Spacer(
                    Modifier
                        .border(2.dp, Color.White, CircleShape)
                        .size(indicatorSize)
                )
            }
        }

        Spacer(
            Modifier
                .windowInsetsPadding(WindowInsets.safeContent)
                .align(Alignment.BottomCenter)
                .size(50.dp)
                .background(Color.White, CircleShape)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = {
                        showAutoFocusIndicator = false
                        cameraViewModel.capturePhoto()
                    },
                )
        )
    }
}
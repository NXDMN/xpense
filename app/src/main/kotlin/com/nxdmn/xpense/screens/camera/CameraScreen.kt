package com.nxdmn.xpense.screens.camera

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxdmn.xpense.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel = viewModel(),
    onNavigateBackWithResult: (Uri?) -> Unit
) {
    val activity = LocalActivity.current as ComponentActivity
    DisposableEffect(activity) {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.Black.toArgb())
        )

        onDispose {
            activity.enableEdgeToEdge()
        }
    }

    val currentSurfaceRequest by cameraViewModel.surfaceRequests.collectAsState()
    val cameraUiState by cameraViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (cameraUiState.isCapturing) {
            currentSurfaceRequest?.let { surfaceRequest ->
                // CoordinateTransformer for transforming from Offsets to Surface coordinates
                val coordinateTransformer = remember { MutableCoordinateTransformer() }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .let {
                            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                it.fillMaxHeight()
                                it.aspectRatio(4f / 3f)
                            } else {
                                it.fillMaxWidth()
                                it.aspectRatio(3f / 4f)
                            }
                        }
                ) {
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
                        contentScale = ContentScale.None
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
            }
        } else {
            Image(
                cameraUiState.capturedBitmap!!,
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )
        }

        val scope = rememberCoroutineScope()
        Box(
            Modifier
                .windowInsetsPadding(WindowInsets.safeContent)
                .let {
                    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        it
                            .align(Alignment.CenterEnd)
                            .offset((-40).dp, 0.dp)
                    } else {
                        it
                            .align(Alignment.BottomCenter)
                            .offset(0.dp, (-40).dp)
                    }
                }
                .size(60.dp)
                .background(Color.White, CircleShape)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = {
                        showAutoFocusIndicator = false
                        if (cameraUiState.isCapturing) {
                            cameraViewModel.capturePhoto(context)
                        } else {
                            scope.launch {
                                val uri = cameraViewModel.savePhoto(context)
                                onNavigateBackWithResult(uri)
                            }
                        }
                    },
                )
        ) {
            if (!cameraUiState.isCapturing)
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Confirm photo",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp),
                    tint = Color.Black
                )
        }

        if (!cameraUiState.isCapturing)
            IconButton(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.safeContent)
                    .align(Alignment.BottomEnd)
                    .let {
                        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            it.offset((-40).dp, (-40).dp)
                        } else {
                            it.offset((-40).dp, (-40).dp)
                        }
                    }
                    .background(Color.White, CircleShape),
                onClick = { cameraViewModel.retakePhoto() }
            ) {
                Icon(
                    painterResource(R.drawable.baseline_replay_24),
                    contentDescription = "Retake photo",
                    tint = Color.Black
                )
            }

        if (cameraUiState.isBusy) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
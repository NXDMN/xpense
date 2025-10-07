package com.nxdmn.xpense.screens.camera

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CameraScreen(cameraViewModel: CameraViewModel = viewModel()) {
    val currentSurfaceRequest by cameraViewModel.surfaceRequests.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    LaunchedEffect(lifecycleOwner) {
        cameraViewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    currentSurfaceRequest?.let { surfaceRequest ->
        // CoordinateTransformer for transforming from Offsets to Surface coordinates
        val coordinateTransformer = remember { MutableCoordinateTransformer() }

        CameraXViewfinder(
            surfaceRequest = surfaceRequest,
            implementationMode = ImplementationMode.EXTERNAL, // Can also use EMBEDDED
            modifier =
                Modifier.pointerInput(Unit) {
                    detectTapGestures {
                        with(coordinateTransformer) {
                            val surfaceCoords = it.transform()
                            cameraViewModel.focusOnPoint(
                                surfaceRequest.resolution,
                                surfaceCoords.x,
                                surfaceCoords.y,
                            )
                        }
                    }
                },
            coordinateTransformer = coordinateTransformer,
        )
    }

}
package com.nxdmn.xpense.screens.camera

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.nxdmn.xpense.helpers.toLocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.format.DateTimeFormatter

data class CameraUiState(
    val isBusy: Boolean = false,
    val isCapturing: Boolean = true,
    val capturedBitmap: ImageBitmap? = null,
)

class CameraViewModel() : ViewModel() {
    private val _surfaceRequests = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequests: StateFlow<SurfaceRequest?> = _surfaceRequests.asStateFlow()

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var timeStamp: Long? = null

    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
    private var cameraControl: CameraControl? = null

    // default is 4:3
    private val resolution = ResolutionSelector.Builder().setAspectRatioStrategy(
        AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
    ).build()

    private val previewUseCase = Preview.Builder().setResolutionSelector(resolution).build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequests.update { newSurfaceRequest }
            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                newSurfaceRequest.resolution.width.toFloat(),
                newSurfaceRequest.resolution.height.toFloat()
            )
        }
    }

    private val imageCaptureUseCase = ImageCapture.Builder().setResolutionSelector(resolution)
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    private val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()


    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        val camera = processCameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
        )
        cameraControl = camera.cameraControl

        // Cancellation signals we're done with the camera
        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
            cameraControl = null
        }
    }

    fun focusOnPoint(tapCoords: Offset) {
        val point = surfaceMeteringPointFactory?.createPoint(tapCoords.x, tapCoords.y)
        if (point != null) {
            val meteringAction = FocusMeteringAction.Builder(point).build()
            cameraControl?.startFocusAndMetering(meteringAction)
        }
    }

    fun capturePhoto(context: Context) {
        imageCaptureUseCase.takePicture(
            context.mainExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    timeStamp = System.currentTimeMillis()
                    val bitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) },
                        true
                    )
                    _uiState.update {
                        it.copy(isCapturing = false, capturedBitmap = bitmap.asImageBitmap())
                    }
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    exception.printStackTrace()
                }
            })
    }

    fun retakePhoto() {
        _uiState.update { it.copy(isCapturing = true, capturedBitmap = null) }
        timeStamp = null
    }

    // make this suspend so this will be called with coroutinescope, which will suspend
    // so the onNavigateBack only called when savePhoto done
    suspend fun savePhoto(context: Context): Uri? {
        _uiState.update { it.copy(isBusy = true) }

        val resolver = context.contentResolver
        var uri: Uri? = null
        withContext(Dispatchers.IO) {
            try {
                val appName = try {
                    val packageManager = context.packageManager
                    val applicationInfo =
                        packageManager.getApplicationInfo(context.packageName, 0)
                    packageManager.getApplicationLabel(applicationInfo).toString()
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                    "Xpense"
                }

                val time =
                    timeStamp!!.toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$time")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/$appName")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                uri = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                uri?.let { it ->
                    val bitmap = _uiState.value.capturedBitmap!!.asAndroidBitmap()
                    resolver.openOutputStream(it)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }

                    // mark as not pending so it becomes visible in gallery
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)
                }
            } catch (e: IOException) {
                uri?.let {
                    resolver.delete(it, null, null)
                }
                e.printStackTrace()
            }
        }

        _uiState.update { it.copy(isBusy = false) }
        return uri
    }

    override fun onCleared() {
        super.onCleared()
        // clear bitmap
        _uiState.update { it.copy(capturedBitmap = null) }
    }
}
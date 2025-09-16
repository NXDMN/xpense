package com.nxdmn.xpense.ui.states

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Stable
class PermissionState(
    val permission: String,
    val launcher: ActivityResultLauncher<String>
) {
    var status by mutableIntStateOf(0)
        private set

    var shouldShowRationale by mutableStateOf(false)
        private set

    fun refreshStatus(context: Context) {
        status = ContextCompat.checkSelfPermission(
            context,
            permission
        )
        shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            context.findActivity()!!,
            permission
        )
    }

    fun requestPermission() = launcher.launch(permission)
}

@Composable
fun rememberPermissionState(
    permission: String,
    showPermissionDialog: (Boolean) -> Unit = {}
): PermissionState {
    val context = LocalContext.current
    lateinit var state: PermissionState

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            state.refreshStatus(context)
            if (state.status != PackageManager.PERMISSION_GRANTED)
                showPermissionDialog(state.shouldShowRationale)
        }

    state = remember { PermissionState(permission, launcher) }

    // Initialize the status, shouldShowRationale and
    // keep status fresh if context changes (new Activity after config change)
    LaunchedEffect(context) {
        state.refreshStatus(context)
    }

    return state
}


fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}
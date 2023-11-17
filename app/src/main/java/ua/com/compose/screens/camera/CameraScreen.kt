package ua.com.compose.screens.camera

import android.Manifest
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.composable.ColorPickerInfo
import ua.com.compose.composable.ColorPickerRing
import ua.com.compose.composable.IconButton
import ua.com.compose.composable.IconItem
import ua.com.compose.composable.Menu
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.extension.createVideoCaptureUseCase
import ua.com.compose.extension.showToast
import ua.com.compose.extension.throttleLatest
import ua.com.compose.extension.vibrate
import ua.com.compose.screens.info.InfoScreen
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun CameraScreen(viewModule: CameraViewModule, lifecycleOwner: LifecycleOwner) {
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA
        )
    )

    val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }
    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(previewView) {
        context.createVideoCaptureUseCase(
            executors = cameraExecutor,
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector.value,
            previewView = previewView,
            analyzed = ColorAnalyzer(scope = scope) {
                viewModule.changeColor(it)
            }
        )
    }

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    var stateInfoColor: Int? by remember { mutableStateOf(null) }
    stateInfoColor?.let {
        InfoScreen(color = it) {
            stateInfoColor = null
        }
    }

    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val view = LocalView.current
                IconButton(painter = painterResource(R.drawable.ic_camera), shape = CircleShape, modifier = Modifier.size(100.dp)) {
                    view.vibrate(EVibrate.BUTTON)
                    permissionState.launchMultiplePermissionRequest()
                }
            }
        },
        permissionsNotAvailableContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val view = LocalView.current
                IconButton(painter = painterResource(R.drawable.ic_camera), shape = CircleShape, modifier = Modifier.size(100.dp)) {
                    view.vibrate(EVibrate.BUTTON)
                    permissionState.launchMultiplePermissionRequest()
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
        }

        val state by viewModule.colorState.observeAsState()

        AnimatedVisibility(
            visible = state != null,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            state?.let { state ->
                ColorPickerRing(color = state.color)
                Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 90.dp)) {
                    val view = LocalView.current
                    ColorPickerInfo(state = state) {
                        view.vibrate(EVibrate.BUTTON)
                        stateInfoColor = state.color
                    }
                }

                Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 26.dp)) {
                    Menu {
                        val view = LocalView.current
                        IconItem(painter = painterResource(id = R.drawable.ic_copy)) {
                            view.vibrate(EVibrate.BUTTON)
                            analytics.send(SimpleEvent(key = Analytics.Event.COLOR_COPY_CAMERA))
                            context.clipboardCopy(state.typeValue)
                            context.showToast(R.string.color_pick_color_copy)
                        }
                        IconItem(painter = painterResource(id = R.drawable.ic_add_circle)) {
                            view.vibrate(EVibrate.BUTTON)
                            viewModule.pressPaletteAdd(state.color)
                            context.showToast(R.string.color_pick_color_add_to_pallete)
                        }
                    }
                }
            }
        }
    }
}
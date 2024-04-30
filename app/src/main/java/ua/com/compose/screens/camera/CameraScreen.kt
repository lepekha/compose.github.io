package ua.com.compose.screens.camera

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ua.com.compose.R
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
import ua.com.compose.extension.vibrate
import ua.com.compose.screens.dominantColors.DomainColors
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

    var icon by remember { mutableIntStateOf(R.drawable.ic_filter) }
    viewModule.stateDomainColors.value.takeIf { it }?.let {
        DomainColors(viewModule.domainColors) {
            viewModule.stateDomainColors.value = false
        }
        icon = R.drawable.ic_filter
    }

    LaunchedEffect(previewView) {
        context.createVideoCaptureUseCase(
            executors = cameraExecutor,
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector.value,
            previewView = previewView,
            analyzed = ColorAnalyzer(
                scope = scope,
                listenerColor =  {
                    viewModule.changeColor(it)
                                 }
            )
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

                Column(verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .navigationBarsPadding()) {
                    val view = LocalView.current
                    ColorPickerInfo(state = state) {
                        view.vibrate(EVibrate.BUTTON)
                        stateInfoColor = state.color
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val infiniteTransition = rememberInfiniteTransition(label = "")
                    val angle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = 1500
                            }
                        ), label = ""
                    )

                        Menu {
                            val modifier = if(icon == R.drawable.ic_progress_bar) {
                                Modifier.rotate(angle)
                            } else {
                                Modifier
                            }

                            IconItem(modifier = modifier, painter = painterResource(id = icon)) {
                                previewView.bitmap?.let {
                                    view.vibrate(EVibrate.BUTTON)
                                    icon = R.drawable.ic_progress_bar
                                    viewModule.generateDomainColors(it)
                                }
                            }
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
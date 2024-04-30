package ua.com.compose.screens.image

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.core.view.drawToBitmap
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
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
import ua.com.compose.extension.findActivity
import ua.com.compose.extension.showToast
import ua.com.compose.extension.throttleLatest
import ua.com.compose.extension.vibrate
import ua.com.compose.screens.dominantColors.DomainColors
import ua.com.compose.screens.info.InfoScreen
import java.lang.Float
import kotlin.math.ceil
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.O)
fun View.bitmapFormView(activity: Activity, result: (bmp: Bitmap) -> Unit) {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val locations = IntArray(2)
    this.getLocationInWindow(locations)
    val rect =
        Rect(locations[0], locations[1], locations[0] + this.width, locations[1] + this.height)
    PixelCopy.request(activity.getWindow(), rect, bitmap, { copyResult ->
        if (copyResult == PixelCopy.SUCCESS) {
            result(bitmap)
        }
    }, Handler(Looper.getMainLooper()))
}

@Composable
fun ImageScreen(viewModule: ImageViewModule, uri: String? = null) {
    val activity = LocalContext.current.findActivity()
    val view = LocalView.current
    uri?.let { Settings.lastUri = Uri.decode(it).toUri() }
    var photoUri: Uri? by remember { mutableStateOf(Settings.lastUri) }
    var image: Bitmap? by remember { mutableStateOf(null) }
    var imageLoadState: AsyncImagePainter.State by remember { mutableStateOf(AsyncImagePainter.State.Empty) }
    var positionInRoot by remember { mutableStateOf(Offset.Zero) }

    val scope = rememberCoroutineScope()

    var size by remember { mutableStateOf(Size.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    fun generatePixel(view: View) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                view.bitmapFormView(activity) {
                    viewModule.changeColor(it.getPixel((positionInRoot.x).roundToInt(), (positionInRoot.y).roundToInt()))
                }
            } else {
                view.drawToBitmap().getPixel((positionInRoot.x).roundToInt(), (positionInRoot.y).roundToInt()).let {
                    viewModule.changeColor(it)
                }
            }
        } catch (_: Exception) { }
    }

    val throttleLatest: ((View) -> Unit) = remember {
        throttleLatest(
            withFirst = true,
            intervalMs = 100,
            coroutineScope = scope
        ) {
            generatePixel(it)
        }
    }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = Float.max(1f, scale * zoomChange)
        offset += offsetChange

        val centerX = (size.width * scale / 2f).roundToInt() - 1
        val centerY = (size.height * scale / 2f).roundToInt() - 1

        if (offset.x.roundToInt() >= centerX) {
            offset = Offset(centerX.toFloat(), offset.y)
        }
        if (offset.x.roundToInt() <= -centerX) {
            offset = Offset(-centerX.toFloat(), offset.y)
        }

        if (offset.y.roundToInt() >= centerY) {
            offset = Offset(offset.x, centerY.toFloat())
        }
        if (offset.y.roundToInt() <= -centerY) {
            offset = Offset(offset.x, -centerY.toFloat())
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if(uri != null) {
            photoUri = uri
            Settings.lastUri = uri
            size = Size.Zero
            scale = 1f
            offset = Offset.Zero
        }
    }

    var stateInfoColor: Int? by remember { mutableStateOf(null) }
    stateInfoColor?.let {
        InfoScreen(color = it) {
            stateInfoColor = null
        }
    }

    var icon by remember { mutableIntStateOf(R.drawable.ic_filter) }
    viewModule.stateDomainColors.value .takeIf { it }?.let {
        DomainColors(colors = viewModule.domainColors) {
            viewModule.stateDomainColors.value = false
        }
        icon = R.drawable.ic_filter
    }

    if (photoUri == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            IconButton(
                shape = RoundedCornerShape(25.dp),
                painter = painterResource(R.drawable.ic_gallery),
                modifier = Modifier.size(100.dp)
            ) {
                view.vibrate(EVibrate.BUTTON)
                launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            val color1 = MaterialTheme.colorScheme.surfaceContainer
            val color2 = MaterialTheme.colorScheme.surfaceContainerHigh
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val _size = Size(20.dp.toPx(), 20.dp.toPx())
                        val countH = ceil(this.size.height / _size.height).toInt()
                        val countW = ceil(this.size.width / _size.width).toInt()
                        var _offset = Offset(0f, 0f)
                        repeat(countH) { h ->
                            repeat(countW) { w ->
                                val _color = if ((h + w) % 2 == 0) color1 else color2
                                drawRect(color = _color, topLeft = _offset, size = _size)
                                _offset = _offset.copy(x = _offset.x + _size.width, y = _offset.y)
                            }
                            _offset = _offset.copy(x = 0f, y = _offset.y + _size.height)
                        }
                    }
            ) {
                if(photoUri != null) {

                    if(imageLoadState is AsyncImagePainter.State.Success) {
                        throttleLatest.invoke(view)
                    }
                    val request = ImageRequest.Builder(LocalContext.current).data(photoUri).allowHardware(false).build()
                    AsyncImage(
                        model = request,
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(0f)
                            .transformable(state = state)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            ),
                        contentDescription = null,
                        onState = {
                            imageLoadState = it

                            if(it is AsyncImagePainter.State.Success) {
                                size = it.painter.intrinsicSize
                                image = it.result.drawable.toBitmap()
                            }
                        }
                    )
                }
                val colorState by viewModule.colorState.observeAsState()

                AnimatedVisibility(
                    visible = colorState != null,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    colorState?.let { colorState ->
                        ColorPickerRing(modifier = Modifier.onGloballyPositioned { coordinates ->
                            positionInRoot = coordinates.boundsInRoot().center
                        }, color = colorState.color)
                        Column(verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .fillMaxSize()
                            .navigationBarsPadding()) {
                            ColorPickerInfo(state = colorState) {
                                view.vibrate(EVibrate.BUTTON)
                                stateInfoColor = colorState.color
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val context = LocalContext.current
                            Menu {

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

                                val modifier = if(icon == R.drawable.ic_progress_bar) {
                                    Modifier.rotate(angle)
                                } else {
                                    Modifier
                                }

                                IconItem(painter = painterResource(id = R.drawable.ic_gallery)) {
                                    view.vibrate(EVibrate.BUTTON)
                                    launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                                IconItem(modifier = modifier, painter = painterResource(id = icon)) {
                                    image?.let {
                                        view.vibrate(EVibrate.BUTTON)
                                        icon = R.drawable.ic_progress_bar
                                        viewModule.generateDomainColors(it)
                                    }
                                }
                                IconItem(painter = painterResource(id = R.drawable.ic_copy)) {
                                    view.vibrate(EVibrate.BUTTON)
                                    analytics.send(SimpleEvent(key = Analytics.Event.COLOR_COPY_IMAGE))
                                    context.clipboardCopy(colorState.typeValue)
                                    context.showToast(R.string.color_pick_color_copy)
                                }
                                IconItem(painter = painterResource(id = R.drawable.ic_add_circle)) {
                                    view.vibrate(EVibrate.BUTTON)
                                    viewModule.pressPaletteAdd(colorState.color)
                                    context.showToast(R.string.color_pick_color_add_to_pallete)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package ua.com.compose.screens.image

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.drawToBitmap
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
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
import ua.com.compose.extension.findActivity
import ua.com.compose.extension.imageBitmap
import ua.com.compose.extension.vibrate
import ua.com.compose.screens.info.InfoScreen
import java.lang.Float
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
fun ImageScreen(viewModule: ImageViewModule) {

    val activity = LocalContext.current.findActivity()
    val view = LocalView.current

    var photoUri: Uri? by remember { mutableStateOf(null) }
    var imageLoadState: AsyncImagePainter.State by remember { mutableStateOf(AsyncImagePainter.State.Empty) }
    var positionInRoot by remember { mutableStateOf(Offset.Zero) }

    var size by remember { mutableStateOf(Size.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

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

    if (photoUri == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            IconButton(
                painter = painterResource(R.drawable.ic_gallery),
                modifier = Modifier.size(100.dp)
            ) {
                view.vibrate(EVibrate.BUTTON)
                launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            val color = viewModule.changeColor.observeAsState()

            val background = imageBitmap(id = R.drawable.ic_background_pattern, option = BitmapFactory.Options().apply {
                this.inPreferredConfig = Bitmap.Config.ARGB_8888
                this.outHeight = 15.dp.value.toInt()
                this.outWidth = 15.dp.value.toInt()
            }).asImageBitmap()

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        ShaderBrush(
                            ImageShader(
                                background,
                                TileMode.Repeated,
                                TileMode.Repeated
                            )
                        )
                    )
            ) {
                if(photoUri != null) {

                    if(imageLoadState is AsyncImagePainter.State.Success) {

                        val view = LocalView.current
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


                    AsyncImage(
                        model = photoUri,
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
                            }
                        }
                    )
                }

                val currentColor = color.value
                AnimatedVisibility(
                    visible = currentColor != null,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    currentColor?.let {
                        ColorPickerRing(modifier = Modifier.onGloballyPositioned { coordinates ->
                            positionInRoot = coordinates.boundsInRoot().center
                        }, color = it)
                        Box(
                            contentAlignment = Alignment.BottomCenter,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 90.dp
                                )
                        ) {
                            ColorPickerInfo(color = it) {
                            view.vibrate(EVibrate.BUTTON)
                                stateInfoColor = it
                            }
                        }

                        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp, end = 16.dp, bottom = 26.dp)) {
                            Menu {
                                IconItem(painter = painterResource(id = R.drawable.ic_gallery)) {
//                                this@apply.vibrate(EVibrate.BUTTON)
                                    launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                                IconItem(painter = painterResource(id = R.drawable.ic_copy)) {
//                                this@apply.vibrate(EVibrate.BUTTON)
                                    analytics.send(SimpleEvent(key = Analytics.Event.COLOR_COPY_CAMERA))
//                                requireContext().clipboardCopy(color.toString())
//                                requireContext().showToast(R.string.module_other_color_pick_color_copy)
                                }
                                IconItem(painter = painterResource(id = R.drawable.ic_add_circle)) {
//                                this@apply.vibrate(EVibrate.BUTTON)
                                    viewModule.pressPaletteAdd()
//                                requireActivity().createReview()
//                                requireContext().showToast(R.string.module_other_color_pick_color_add_to_pallete)
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

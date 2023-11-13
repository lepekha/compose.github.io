package ua.com.compose.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.launch
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.composable.BottomSheet
import ua.com.compose.composable.ColorPickerInfo
import ua.com.compose.composable.DialogAccentButton
import ua.com.compose.composable.DialogBottomSheet
import ua.com.compose.composable.DialogButton
import ua.com.compose.composable.HueBar
import ua.com.compose.composable.SatValPanel
import ua.com.compose.data.ColorNames
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate
import ua.com.compose.screens.info.InfoScreen
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogColorPick(color: Color = Color.Gray, onDone: (color: Color) -> Unit, onDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var stateInfoColor: Int? by remember { mutableStateOf(null) }
    stateInfoColor?.let {
        InfoScreen(color = it) {
            stateInfoColor = null
        }
    }

    BottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                    val startColor = color.toArgb()
                    val hsv = remember {
                        val hsv = floatArrayOf(0f, 0f, 0f)
                        AndroidColor.colorToHSV(startColor, hsv)
                        mutableStateOf(
                            Triple(hsv[0], hsv[1], hsv[2])
                        )
                    }
                    val backgroundColor = remember(hsv.value) {
                        mutableStateOf(Color.hsv(hsv.value.first, hsv.value.second, hsv.value.third))
                    }
                    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                        val view = LocalView.current
                        val color = backgroundColor.value.toArgb()
                        val borderColor = if (ColorUtils.calculateLuminance(color) < 0.5) Color.White else Color.Black

                        val colorFilled = IconButtonColors(
                            containerColor = Color(color),
                            contentColor = Color(color),
                            disabledContainerColor = Color(color),
                            disabledContentColor = Color(color)
                        )

                        FilledTonalIconButton(colors = colorFilled, shape = RoundedCornerShape(16.dp), modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(), onClick = {
                            view.vibrate(EVibrate.BUTTON)
                            stateInfoColor = color
                        }) {
                            Box {
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp), contentAlignment = Alignment.TopEnd) {
                                    Image(
                                        alignment = Alignment.Center,
                                        imageVector = Icons.Rounded.Info,
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(borderColor),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .alpha(0.5f)
                                    )
                                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                        val name = "≈${ColorNames.getColorName("#"+Integer.toHexString(color).substring(2).toLowerCase())}"
                                        Text(text = Settings.colorType.colorToString(color = color), color = borderColor, fontSize = 18.sp, lineHeight = 19.sp, fontWeight = FontWeight(700))
                                        Text(text = name, color = borderColor, fontSize = 14.sp, lineHeight = 15.sp, fontWeight = FontWeight(600))
                                    }
                                }
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SatValPanel(modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(start = 16.dp, end = 16.dp), hsv = hsv.value) { sat, value ->
                        hsv.value = Triple(hsv.value.first, sat, value)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    HueBar(modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .padding(start = 28.dp, end = 28.dp),
                        color = startColor) { hue -> hsv.value = Triple(hue, hsv.value.second, hsv.value.third) }
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 20.dp, top = 40.dp)
                    ) {
                        DialogButton(icon = Icons.Rounded.Close, modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp, end = 8.dp)
                            .height(50.dp)) {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onDismissRequest.invoke()
                                }
                            }
                        }

                        DialogAccentButton(icon = Icons.Rounded.Done, modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, end = 16.dp)
                            .height(50.dp)) {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onDone.invoke(backgroundColor.value)
                                    onDismissRequest.invoke()
                                }
                            }
                        }
                    }
                }
    }
}
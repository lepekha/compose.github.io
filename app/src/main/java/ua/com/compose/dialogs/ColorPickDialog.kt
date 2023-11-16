package ua.com.compose.dialogs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.composable.DialogBottomSheet
import ua.com.compose.composable.DialogConfirmButton
import ua.com.compose.composable.HueBar
import ua.com.compose.composable.SatValPanel
import ua.com.compose.data.ColorNames
import ua.com.compose.data.EColorType
import ua.com.compose.data.ECreateColorType
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.nonScaledSp
import ua.com.compose.extension.vibrate
import android.graphics.Color as AndroidColor

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DialogColorPick(
    color: Color = Color.Gray,
    onDone: (color: Color) -> Unit,
    onInfo: (color: Color) -> Unit,
    onDismissRequest: () -> Unit
) {

    var colorType by remember { mutableStateOf(Settings.dialogColorInputType) }
    var createColorType by remember { mutableStateOf(Settings.createColorType) }

    var startColor by remember {
        mutableIntStateOf(color.toArgb())
    }

    var hsv by remember {
        val hsv = floatArrayOf(0f, 0f, 0f)
        AndroidColor.colorToHSV(startColor, hsv)
        mutableStateOf(
            Triple(hsv[0], hsv[1], hsv[2])
        )
    }

    var errorInput by remember { mutableStateOf(false) }
    var stateField by remember {
        mutableStateOf(
            TextFieldValue(
                text = colorType.colorToString(
                    AndroidColor.HSVToColor(floatArrayOf(hsv.first, hsv.second, hsv.third))
                )
            )
        )
    }

    DialogBottomSheet(onDismissRequest = onDismissRequest) {
        val localFocusManager = LocalFocusManager.current

        val view = LocalView.current
        val color = AndroidColor.HSVToColor(floatArrayOf(hsv.first, hsv.second, hsv.third))
        val borderColor =
            if (ColorUtils.calculateLuminance(color) < 0.5) Color.White else Color.Black

        val colorFilled = IconButtonColors(
            containerColor = Color(color),
            contentColor = Color(color),
            disabledContainerColor = Color(color),
            disabledContentColor = Color(color)
        )

        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp,
                top = 16.dp
            )
        ) {
            FilledTonalIconButton(colors = colorFilled,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth(),
                onClick = {
                    view.vibrate(EVibrate.BUTTON)
                    onDismissRequest.invoke()
                    onInfo.invoke(Color(color))
                }) {
                Box {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp), contentAlignment = Alignment.TopEnd
                    ) {
                        Image(
                            alignment = Alignment.Center,
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(borderColor),
                            modifier = Modifier
                                .size(24.dp)
                                .alpha(0.5f)
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val name = "≈${
                                ColorNames.getColorName(
                                    "#" + Integer.toHexString(color).substring(2).toLowerCase()
                                )
                            }"
                            Text(
                                text = name,
                                color = borderColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight(600)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)) {
                ECreateColorType.values().forEach {
                    FilterChip(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp, end = 16.dp),
                        selected = createColorType == it,
                        border = null,
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.Transparent,
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                        onClick = {
                            Settings.createColorType = it
                            createColorType = it
                        },
                        label = {
                            Text(modifier = Modifier.fillMaxWidth(), text = stringResource(id = it.titleResId()).uppercase(), fontSize = 14.sp, fontWeight = FontWeight(600), textAlign = TextAlign.Center)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if(createColorType == ECreateColorType.BOX) {
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = MaterialTheme.shapes.medium
                        )
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    SatValPanel(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(), hsv = hsv
                    ) { sat, value ->
                        hsv = Triple(hsv.first, sat, value)
                        errorInput = false
                        localFocusManager.clearFocus()
                        stateField = TextFieldValue(
                            colorType.colorToString(
                                AndroidColor.HSVToColor(
                                    floatArrayOf(
                                        hsv.first,
                                        hsv.second,
                                        hsv.third
                                    )
                                )
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    HueBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .padding(start = 12.dp, end = 12.dp),
                        hue = hsv.first
                    ) { hue ->
                        hsv = Triple(hue, hsv.second, hsv.third)
                        errorInput = false
                        localFocusManager.clearFocus()
                        stateField = TextFieldValue(
                            colorType.colorToString(
                                AndroidColor.HSVToColor(
                                    floatArrayOf(
                                        hsv.first,
                                        hsv.second,
                                        hsv.third
                                    )
                                )
                            )
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = MaterialTheme.shapes.medium
                        )
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 0.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier,
                        value = stateField,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { localFocusManager.clearFocus() }),
                        isError = errorInput,
                        shape = MaterialTheme.shapes.small,
//                    label = { Text(text = colorType.title(), fontSize = 14.sp) },
                        trailingIcon = {
                            if(errorInput) {
                                Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null)
                            }
                        },
                        textStyle = TextStyle.Default.copy(fontSize = 20.sp.nonScaledSp),
                        colors = OutlinedTextFieldDefaults.colors(
                            selectionColors = TextSelectionColors(
                                handleColor = MaterialTheme.colorScheme.primary,
                                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            ),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        onValueChange = {
                            val _color = colorType.stringToColor(it.text)?.apply {
                                startColor = this
                                val _hsv = floatArrayOf(0f, 0f, 0f)
                                AndroidColor.colorToHSV(startColor, _hsv)
                                hsv = Triple(_hsv[0], _hsv[1], _hsv[2])
                            }
                            stateField = it
                            errorInput = _color == null
                        })
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(maxItemsInEachRow = 4, verticalArrangement = Arrangement.spacedBy((-12).dp, Alignment.Top)) {
                        EColorType.valuesForInputText().forEach {
                            FilterChip(
                                selected = it == colorType,
                                border = null,
                                shape = MaterialTheme.shapes.extraSmall,
                                modifier = Modifier
                                    .padding(start = 2.dp, end = 2.dp)
                                    .weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                onClick = {
                                    view.vibrate(EVibrate.BUTTON)
                                    colorType = it
                                    Settings.dialogColorInputType = it
                                    stateField = TextFieldValue(
                                        colorType.colorToString(
                                            AndroidColor.HSVToColor(
                                                floatArrayOf(
                                                    hsv.first,
                                                    hsv.second,
                                                    hsv.third
                                                )
                                            )
                                        )
                                    )
                                    errorInput = false
                                },
                                label = {
                                    Text(modifier = Modifier.fillMaxWidth(), text = it.shortTitle(), fontSize = 14.sp.nonScaledSp, textAlign = TextAlign.Center)
                                }
                            )
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(end = 0.dp, top = 0.dp)
                    .fillMaxWidth()
            ) {
                DialogConfirmButton(text = stringResource(id = R.string.module_other_color_pick_cancel)) {
                    onDismissRequest.invoke()
                }
                DialogConfirmButton(text = stringResource(id = R.string.module_other_color_pick_add)) {
                    onDone.invoke(
                        Color(
                            AndroidColor.HSVToColor(
                                floatArrayOf(
                                    hsv.first,
                                    hsv.second,
                                    hsv.third
                                )
                            )
                        )
                    )
                    onDismissRequest.invoke()
                }
            }
        }
    }
}
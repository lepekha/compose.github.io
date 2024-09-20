package ua.com.compose.dialogs.colorPickDialog

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.colors.asRGB
import ua.com.compose.colors.colorINTOf
import ua.com.compose.composable.DialogBottomSheet
import ua.com.compose.composable.DialogConfirmButton
import ua.com.compose.data.enums.EColorType
import ua.com.compose.data.enums.ECreateColorType
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.asComposeColor
import ua.com.compose.extension.nearestColorName
import ua.com.compose.extension.vibrate

import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.textColor
import ua.com.compose.dialogs.colorPickDialog.views.CreateHSLColor
import ua.com.compose.dialogs.colorPickDialog.views.CreateHSVColor
import ua.com.compose.dialogs.colorPickDialog.views.CreateRGBColor
import ua.com.compose.dialogs.colorPickDialog.views.CreateTXTColor

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DialogColorPick(
    name: String?,
    color: IColor,
    cancelString: String,
    doneString: String,
    onDone: (name: String?, color: IColor) -> Unit,
    onDismissRequest: () -> Unit
) {

    val createColorType by Settings.dialogColorPick.createType.flow.collectAsState(initial = Settings.dialogColorPick.createType.value)

    var iCol: MutableState<IColor> = remember {
        mutableStateOf(color.asRGB())
    }

    DialogBottomSheet(onDismissRequest = onDismissRequest) {
        val localFocusManager = LocalFocusManager.current

        val view = LocalView.current

        val nameFieldFocus = remember { FocusRequester() }
        var allowChangeColorName by remember { mutableStateOf(name == null) }

        val containerColor by remember {
            derivedStateOf {
                iCol.value.asComposeColor()
            }
        }

        val borderColor by remember {
            derivedStateOf {
                iCol.value.textColor().asComposeColor()
            }
        }

        val placeholder by remember {
            derivedStateOf {
                iCol.value.nearestColorName()
            }
        }
        var nameFieldText by remember {
            mutableStateOf(
                TextFieldValue(
                    text = name ?: color.nearestColorName()
                )
            )
        }

        LaunchedEffect(key1 = iCol.value) {
            if(allowChangeColorName) {
                nameFieldText = nameFieldText.copy(text = iCol.value.nearestColorName()).takeIf { nameFieldText.text.isNotEmpty() } ?: nameFieldText
            }
        }

        Column(
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = MaterialTheme.shapes.medium,
                        clip = false
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp,
                        top = 16.dp
                    )

            ) {
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxSize()
                                .focusRequester(nameFieldFocus),
                            value = nameFieldText,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { localFocusManager.clearFocus() }),
                            placeholder = {
                                Text(
                                    placeholder,
                                    fontSize = 18.sp
                                )
                            },
                            shape = MaterialTheme.shapes.medium,
                            textStyle = TextStyle.Default.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight(600),
                                textAlign = TextAlign.Start
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                selectionColors = TextSelectionColors(
                                    handleColor = MaterialTheme.colorScheme.primary,
                                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                ),
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                focusedContainerColor = containerColor,
                                unfocusedContainerColor = containerColor,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                focusedPlaceholderColor = borderColor.copy(alpha = 0.7f),
                                unfocusedPlaceholderColor = borderColor.copy(alpha = 0.7f),
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedTextColor = borderColor,
                                unfocusedTextColor = borderColor,
                                focusedLeadingIconColor = borderColor,
                                unfocusedLeadingIconColor = borderColor
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = null,
                                )
                            },
                            onValueChange = {
                                when {
                                    it.text.isEmpty() or it.text.isBlank() -> {
                                        allowChangeColorName = true
                                    }
                                    (color.nearestColorName() != it.text) -> {
                                        allowChangeColorName = false
                                    }
                                }

                                nameFieldText = it
                            })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()) {
                    ECreateColorType.entries.forEach {
                        FilterChip(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp, end = 4.dp),
                            selected = createColorType == it,
                            border = null,
                            shape = MaterialTheme.shapes.large,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            onClick = {
                                view.vibrate(EVibrate.BUTTON)
                                Settings.dialogColorPick.createType.update(it)
                            },
                            label = {
                                Text(modifier = Modifier.fillMaxWidth(), text = it.titleResId(), fontSize = 12.sp, fontWeight = FontWeight(600), textAlign = TextAlign.Center)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {

                when(createColorType) {
                    ECreateColorType.HSV -> {
                        CreateHSVColor(modifier = Modifier.padding(16.dp), color = iCol)
                    }
                    ECreateColorType.RGB -> {
                        CreateRGBColor(modifier = Modifier.padding(16.dp), color = iCol)
                    }
                    ECreateColorType.HSL -> {
                        CreateHSLColor(modifier = Modifier.padding(16.dp), color = iCol)
                    }
                    ECreateColorType.TEXT -> {
                        CreateTXTColor(modifier = Modifier.padding(16.dp), color = iCol)
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(top = 0.dp, bottom = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {
                DialogConfirmButton(text = cancelString) {
                    onDismissRequest.invoke()
                }
                DialogConfirmButton(text = doneString) {
                    onDone.invoke(
                        nameFieldText.text.takeIf { it.isNotEmpty() or it.isNotBlank() } ?: placeholder,
                        iCol.value
                    )
                    onDismissRequest.invoke()
                }
            }
        }
    }
}
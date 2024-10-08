package ua.com.compose.dialogs.colorPickDialog.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.colors.asHSL
import ua.com.compose.colors.asRGB
import ua.com.compose.colors.colorINTOf
import ua.com.compose.colors.data.IColor
import ua.com.compose.data.enums.EColorType
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateTXTColor(
    modifier: Modifier = Modifier,
    color: MutableState<IColor>
) {

    val view = LocalView.current
    val localFocusManager = LocalFocusManager.current
    val colorType by Settings.dialogColorPick.inputColorType.flow.collectAsState(initial = Settings.dialogColorPick.inputColorType.value)
    var errorInput by remember { mutableStateOf(false) }
    var stateField by remember {
        mutableStateOf(
            TextFieldValue(
                text = colorType.colorToString(colorINTOf(color.value.intColor))
            )
        )
    }

    Column(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = stateField,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { localFocusManager.clearFocus() }),
            isError = errorInput,
            shape = MaterialTheme.shapes.extraSmall,
            trailingIcon = if(errorInput) {
                { Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) }
            } else {
                null
            },
            textStyle = TextStyle.Default.copy(fontSize = 20.sp),
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
                   color.value = this
                }
                stateField = it
                errorInput = _color == null
            })
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(modifier = Modifier.padding(start = 4.dp, end = 4.dp), maxItemsInEachRow = 3, verticalArrangement = Arrangement.spacedBy((-12).dp, Alignment.Top)) {
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
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    onClick = {
                        view.vibrate(EVibrate.BUTTON)
                        localFocusManager.clearFocus()
                        Settings.dialogColorPick.inputColorType.update(it)
                        stateField = TextFieldValue(
                            it.colorToString(color.value)
                        )
                        errorInput = false
                    },
                    label = {
                        Text(modifier = Modifier.fillMaxWidth(), text = it.shortTitle(), fontSize = 14.sp, textAlign = TextAlign.Center)
                    }
                )
            }
        }
    }
}
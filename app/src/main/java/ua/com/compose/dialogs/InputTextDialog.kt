package ua.com.compose.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.launch
import ua.com.compose.R
import ua.com.compose.composable.DialogAccentButton
import ua.com.compose.composable.DialogBottomSheet
import ua.com.compose.composable.DialogButton
import ua.com.compose.composable.DialogConfirmButton

@Composable
fun DialogInputText(text: String, hint: String, onDone: (text: String) -> Unit, onDismissRequest: () -> Unit) {
    val state = remember { mutableStateOf(TextFieldValue(text = hint)) }

    DialogBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()

            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp),
                    value = state.value,
                    shape = RoundedCornerShape(12.dp),
                    label = { Text(text = text, fontSize = 14.sp) },
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
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    onValueChange = {
                        state.value = it
                    },
                    leadingIcon = {
                        Icon(modifier = Modifier.size(36.dp), painter = painterResource(id = R.drawable.ic_edit_note), tint = MaterialTheme.colorScheme.onSurfaceVariant, contentDescription = null)
                    })
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(end = 8.dp, top = 30.dp)
                        .fillMaxWidth()
                ) {
                    DialogConfirmButton(text = stringResource(id = R.string.module_other_color_pick_cancel)) {
                        onDismissRequest.invoke()
                    }
                    DialogConfirmButton(text = stringResource(id = R.string.module_other_color_pick_ok)) {
                        onDone.invoke(state.value.text)
                        onDismissRequest.invoke()
                    }
                }
            }
    }
}
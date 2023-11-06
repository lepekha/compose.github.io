package ua.com.compose.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ua.com.compose.R
import ua.com.compose.composable.DialogAccentButton
import ua.com.compose.composable.DialogBottomSheet
import ua.com.compose.composable.DialogButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogConfirmation(text: String, onDone: () -> Unit, onDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    DialogBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.color_night_5),
                    fontSize = 22.sp,
                    fontWeight = FontWeight(600),
                    modifier = Modifier.padding(start = 36.dp, end = 36.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp, top = 40.dp)
                ) {

                    DialogButton(painter = painterResource(id = R.drawable.ic_close), modifier = Modifier.weight(1f).padding(start = 16.dp, end = 8.dp).height(50.dp)) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismissRequest.invoke()
                            }
                        }
                    }

                    DialogAccentButton(painter = painterResource(id = R.drawable.ic_done), modifier = Modifier.weight(1f).padding(start = 8.dp, end = 16.dp).height(50.dp)) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDone.invoke()
                                onDismissRequest.invoke()
                            }
                        }
                    }
                }
        }
    }
}
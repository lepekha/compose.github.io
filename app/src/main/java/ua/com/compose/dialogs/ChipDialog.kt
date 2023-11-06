package ua.com.compose.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ua.com.compose.R
import ua.com.compose.composable.DialogBottomSheet

data class ChipItem<T>(val title: String, val icon: Painter? = null, val isSelect: Boolean = false, val obj: T)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun <T> DialogChoise(items: List<ChipItem<T>>, onDone: (value: T) -> Unit, onDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    DialogBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                items(items.count()) {
                    val item = items[it]
                    val color = if(item.isSelect) colorResource(id = R.color.color_night_6) else colorResource(id = R.color.color_night_5)

                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        onDone.invoke(item.obj)
                                        onDismissRequest.invoke()
                                    }
                                }
                            }
                            .wrapContentHeight(), verticalAlignment = Alignment.CenterVertically) {
                            item.icon?.let {
                                Image(painter = it, modifier = Modifier.size(25.dp), contentDescription = null)
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            Text(text = item.title, color = color, fontSize = 18.sp, fontWeight = FontWeight(500), modifier = Modifier.weight(1f))
                            if(item.isSelect) {
                                Icon(painter = painterResource(id = R.drawable.ic_done), tint = color, modifier = Modifier.size(30.dp), contentDescription = null)
                            }
                        }
                    if(items.lastIndex != it) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
        }
    }
}
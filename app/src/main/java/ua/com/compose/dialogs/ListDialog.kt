package ua.com.compose.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.com.compose.composable.DialogBottomSheet

data class ChipItem<T>(val title: String, val icon: Painter? = null, val iconTint: Color? = null, val isSelect: Boolean = false, val obj: T)

@Composable
fun <T> DialogChoise(items: List<ChipItem<T>>, onDone: (value: T) -> Unit, onDismissRequest: () -> Unit) {
    DialogBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
            LazyColumn(
                contentPadding = PaddingValues(start = 0.dp, end = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = 530.dp)
            ) {
                items(items.count()) {
                    val item = items[it]
                    val color = if(item.isSelect) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                    val background = if(item.isSelect) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .background(background)
                            .clickable {
                                onDone.invoke(item.obj)
                                onDismissRequest.invoke()
                            }
                            .wrapContentHeight(), verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(16.dp))
                            item.icon?.let {
                                val colorFilter = item.iconTint?.let { ColorFilter.tint(color = it) }
                                Image(painter = it, modifier = Modifier.size(23.dp), colorFilter = colorFilter, contentDescription = null)
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            Text(text = item.title, color = color, fontSize = 16.sp, fontWeight = FontWeight(500), modifier = Modifier
                                .weight(1f)
                                .padding(top = 16.dp, bottom = 16.dp))
                        }
                }
        }
    }
}
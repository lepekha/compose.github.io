package ua.com.compose.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate


data class ActionIconButton(val icon: Painter, val action: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNotification(
    text: String,
    imageModel: Any? = null,
    bottomInset: WindowInsets = WindowInsets.navigationBars,
    actions: List<ActionIconButton>,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        windowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        dragHandle = null,
    ) {
        val context = LocalContext.current
        val view = LocalView.current

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                .windowInsetsPadding(bottomInset)
        ) {

            Column(modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageModel != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Card(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .wrapContentWidth()) {
                        AsyncImage(
                            modifier = Modifier.fillMaxHeight().wrapContentWidth(),
                            model = imageModel,
                            imageLoader = ImageLoader.Builder(context)
                                .components {
                                    add(SvgDecoder.Factory())
                                }
                                .build(),
                            contentDescription = null)
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 4.dp)
                ) {

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = text,
                        modifier = Modifier.weight(1f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight(550),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    actions.forEach {
                        Spacer(modifier = Modifier.width(6.dp))
                        FilledTonalIconButton(
                            onClick = {
                                it.action.invoke()
                                onDismissRequest.invoke()
                                view.vibrate(EVibrate.BUTTON)
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                            ),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.width(60.dp)
                        ) {
                            Icon(painter = it.icon, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
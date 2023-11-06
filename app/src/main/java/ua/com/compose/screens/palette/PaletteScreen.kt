package ua.com.compose.screens.palette

import android.content.ClipData
import android.view.View
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransfer
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.launch
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.composable.IconButton
import ua.com.compose.composable.IconItem
import ua.com.compose.composable.Menu
import ua.com.compose.data.ColorNames
import ua.com.compose.dialogs.DialogColorPick
import ua.com.compose.dialogs.DialogConfirmation
import ua.com.compose.dialogs.DialogInputText
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate
import ua.com.compose.screens.info.InfoScreen
import kotlin.math.ceil
import kotlin.math.floor

data class TuneColorState(val id: Long, val color: Color)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaletteScreen(viewModule: PaletteViewModule) {
    val context = LocalContext.current
    val palettes by viewModule.palettes.observeAsState(listOf())
    val scope = rememberCoroutineScope()
    var stateRemovePalette: Card.CardPallet? by remember { mutableStateOf(null) }
    var stateCreatePalette by remember { mutableStateOf(false) }
    var stateTuneColor: TuneColorState? by remember { mutableStateOf(null) }
    var stateCreateColor: Boolean by remember { mutableStateOf(false) }
    var stateInfoColor: Int? by remember { mutableStateOf(null) }

    if(stateCreatePalette) {
        val defaultName = Settings.defaultPaletteName(context, withIncrement = false)
        DialogInputText(
            text = stringResource(id = R.string.module_other_color_pick_pallet_name),
            hint = defaultName,
            onDone = {
                if(defaultName == it) {
                    Settings.defaultPaletteName(context, withIncrement = true)
                }
                viewModule.pressCreatePallet(name = it)
            }) {
            stateCreatePalette = false
        }
    }

    if(stateRemovePalette != null) {
        stateRemovePalette?.item?.id?.let { paletteId ->
            DialogConfirmation(
                text = stringResource(id = R.string.module_other_color_pick_remove_pallet),
                onDone = {
                    viewModule.pressRemovePallet(id = paletteId)
                }) {
                stateRemovePalette = null
            }
        }
    }

    stateTuneColor?.let { state ->
        DialogColorPick(color = state.color, onDone = {
            Settings.lastColor = it.toArgb()
            viewModule.pressChangeColor(state.id, it.toArgb())
        }) {
            stateTuneColor = null
        }
    }

    stateCreateColor.takeIf { it }?.let {
        DialogColorPick(color = Color(Settings.lastColor), onDone = {
            Settings.lastColor = it.toArgb()
            viewModule.pressAddColor(it.toArgb())
        }) {
            stateCreateColor = false
        }
    }

    stateInfoColor?.let {
        InfoScreen(color = it) {
            stateInfoColor = null
        }
    }

    if(palettes.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            IconButton(
                painter = painterResource(R.drawable.ic_palette_add),
                modifier = Modifier.size(100.dp)) {
                    stateCreatePalette = true
            }
        }
    } else {
        val pagerState = rememberPagerState(initialPage = palettes.indexOfFirst { it.isCurrent }) {
            palettes.count()
        }

//        val lazyRowState = rememberLazyListState(palettes.indexOfFirst { it.isCurrent })
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = colorResource(id = R.color.color_main_background))
                    .padding(top = 15.dp, bottom = 10.dp)) {

                LazyRow(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
//                    state = lazyRowState,
                    contentPadding = PaddingValues(start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    item {
                        Box(
                            Modifier.width(100.dp), contentAlignment = Alignment.Center) {
                            val view = LocalView.current
                            IconButton(
                                painter = painterResource(R.drawable.ic_palette_add),
                                modifier = Modifier.size(48.dp)) {
                                view.vibrate(EVibrate.BUTTON)
                                stateCreatePalette = true
                            }
                        }
                    }
                    items(items = palettes, key = { it.item.id }) { pallet ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .animateItemPlacement()
                                .height(105.dp)
                                .padding(top = 0.dp, bottom = 0.dp)
                                .width(150.dp)
                        ) {

                            var draged by remember { mutableStateOf(false) }
                            var hovered by remember { mutableStateOf(false) }

                            val cardBorder = when {
                                hovered -> BorderStroke(1.dp, colorResource(id = R.color.color_night_6))
                                draged -> BorderStroke(1.dp, colorResource(id = R.color.color_night_5))
                                pallet.isCurrent -> BorderStroke(1.dp, colorResource(id = R.color.color_night_6))
                                else -> null
                            }

                            val view = LocalView.current
                            FilledTonalIconButton(modifier = Modifier
                                .width(140.dp)
                                .weight(1f), shape = RoundedCornerShape(10.dp), onClick = {
                                view.vibrate(EVibrate.BUTTON)
                                scope.launch {
                                    pagerState.animateScrollToPage(palettes.indexOfFirst { it.item.id == pallet.item.id })
                                }
                                viewModule.pressPallet(id = pallet.item.id)
                            }) {
                                Card(elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp),
                                    border = cardBorder,
                                    colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.color_night_10)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .dragAndDropTarget(
                                            onStarted = {
                                                draged = true
                                                true
                                            },
                                            onDropped = { event ->
                                                draged = false
                                                hovered = false

                                                true
                                            },
                                            onEntered = {
                                                hovered = true
                                            },
                                            onExited = {
                                                hovered = false
                                            },
                                            onEnded = {
                                                draged = false
                                            }
                                        )
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

                                        val (icon, tint) = if(!draged) {
                                            painterResource(id = R.drawable.ic_palette) to ColorFilter.tint(
                                                colorResource(id = R.color.color_night_8)
                                            )
                                        } else {
                                            painterResource(id = R.drawable.ic_add_fill) to ColorFilter.tint(
                                                colorResource(id = R.color.color_night_5)
                                            )
                                        }

                                        Image(
                                            painter = icon,
                                            contentDescription = pallet.item.name,
                                            modifier = Modifier.fillMaxSize(0.60f),
                                            colorFilter = tint
                                        )
                                        if(!draged) {
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val numColors = pallet.colors.size
                                                val squareSize = this.size.width / numColors // Розмір кожного квадратика
                                                for (i in 0 until numColors) {
                                                    val color = Color(pallet.colors[i].color)
                                                    val left = floor(i * squareSize)
                                                    val top = 0f
                                                    val right = ceil(left + squareSize)
                                                    val bottom = this.size.height
                                                    drawRect(color, Offset(left, top), size = Size(width = right - left, height = bottom))
                                                }
                                            }
                                        }

                                        if(pallet.isCurrent && !draged) {
                                            Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.Bottom) {
                                                if(pallet.colors.isNotEmpty()) {
                                                    IconButton(
                                                        painter = painterResource(R.drawable.ic_share),
                                                        modifier = Modifier
                                                            .padding(4.dp)
                                                            .height(30.dp)
                                                            .weight(1.0f)) {
                                                        view.vibrate(EVibrate.BUTTON)
                                                    }
                                                }

                                                IconButton(
                                                    painter = painterResource(R.drawable.ic_delete),
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .height(30.dp)
                                                        .weight(1.0f)) {
                                                    view.vibrate(EVibrate.BUTTON)
                                                    stateRemovePalette = pallet
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                                Text(text = pallet.item.name,
                                    color = colorResource(id = R.color.color_night_9),
                                    fontWeight = FontWeight(500),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    textAlign = TextAlign.Center)
                        }

                    }

//                    if(lazyRowState.firstVisibleItemIndex == 0 && lazyRowState.firstVisibleItemScrollOffset == 0) {
//                        scope.launch { lazyRowState.animateScrollToItem(0) }
//                    }
                }
            }

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = colorResource(id = R.color.color_night_3)
                ))

            LaunchedEffect(pagerState) {
//                pagerState.scrollToPage(page = palettes.indexOfFirst { it.isCurrent } ?: 0)
                snapshotFlow { pagerState.currentPage }.collect { page ->
                    palettes.getOrNull(page)?.let {
                        viewModule.pressPallet(id = it.item.id)
                    }
//                    lazyRowState.scrollToItem(page)
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(state = pagerState, modifier = Modifier.matchParentSize()) {
                    val align = Alignment.Center.takeIf { _ -> palettes.getOrNull(it)?.colors?.isEmpty() == true } ?: Alignment.TopCenter
                    Box(
                        contentAlignment = align,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Black)
                    ) {
                        if (palettes.getOrNull(it)?.colors?.isEmpty() != false) {
                            val view = LocalView.current
                            IconButton(
                                painter = painterResource(R.drawable.ic_add_circle),
                                modifier = Modifier.size(100.dp)
                            ) {
                                view.vibrate(EVibrate.BUTTON)
                                stateCreateColor = true
                            }
                        } else {
                            val view = LocalView.current
                            LazyColumn(
                                modifier = Modifier.matchParentSize(),
                                contentPadding = PaddingValues(bottom = 98.dp)
                            ) {
                                items(
                                    items = palettes.getOrNull(it)?.colors ?: listOf()
                                ) { colorItem ->
                                    val cardColor = colorResource(id = R.color.color_main_background)
                                    Card(elevation = CardDefaults.cardElevation(
                                        defaultElevation = 3.dp
                                    ),
                                        colors = CardDefaults.cardColors(containerColor = cardColor),
                                        modifier = Modifier
                                            .height(60.dp)
                                            .padding(start = 4.dp, top = 6.dp, end = 4.dp)
                                            .fillMaxWidth()
                                            .dragAndDropSource {
                                                detectTapGestures(
                                                    onTap = {
                                                        view.vibrate(EVibrate.BUTTON)
                                                        stateInfoColor = colorItem.color
                                                    },
                                                    onLongPress = {
                                                        startTransfer(
                                                            DragAndDropTransfer(
                                                                clipData = ClipData.newPlainText(
                                                                    "",
                                                                    ""
                                                                ),
                                                                flags = View.DRAG_FLAG_GLOBAL,
                                                            )
                                                        )
                                                    }
                                                )
                                            }
                                            .animateItemPlacement(),
                                        shape = RoundedCornerShape(10.dp)) {
                                        Row {
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(colorItem.color)
                                                ),
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .aspectRatio(1f)
                                                    .padding(4.dp)
                                                    .fillMaxSize(),
                                                shape = RoundedCornerShape(8.dp),
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(2.dp),
                                                    contentAlignment = Alignment.TopEnd
                                                ) {
                                                    Image(
                                                        alignment = Alignment.Center,
                                                        painter = painterResource(id = R.drawable.ic_info),
                                                        contentDescription = null,
                                                        colorFilter = ColorFilter.tint(
                                                            color = if (ColorUtils.calculateLuminance(colorItem.color) < 0.5) Color.White else Color.Black
                                                        ),
                                                        modifier = Modifier
                                                            .size(18.dp)
                                                            .alpha(0.6f)
                                                    )
                                                }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .padding(start = 4.dp)
                                                    .weight(1f)
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    val hex = "#${
                                                        Integer.toHexString(colorItem.color)
                                                            .substring(2).toLowerCase()
                                                    }"
                                                    Text(
                                                        text = ColorNames.getColorName(hex),
                                                        color = Color.White
                                                    )
                                                    Text(
                                                        text = Settings.colorType.colorToString(
                                                            colorItem.color,
                                                            withSeparator = ","
                                                        ), color = Color.White
                                                    )
                                                }
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .wrapContentWidth()
                                                    .padding(end = 4.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val view = LocalView.current

                                                IconButton(
                                                    painter = painterResource(R.drawable.ic_delete),
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .padding(2.dp)
                                                ) {
//                                                    this@apply.vibrate(EVibrate.BUTTON)
                                                    viewModule.pressRemoveColor(id = colorItem.id)
                                                }
                                                IconButton(
                                                    painter = painterResource(R.drawable.ic_color_tune),
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .padding(2.dp)
                                                ) {
                                                    view.vibrate(EVibrate.BUTTON)
                                                    stateTuneColor = TuneColorState(id = colorItem.id, Color(colorItem.color))
                                                }
                                                IconButton(
                                                    painter = painterResource(R.drawable.module_other_text_style_fragment_text_style_ic_copy),
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .padding(2.dp)
                                                ) {
//                                                    this@apply.vibrate(EVibrate.BUTTON)
                                                    analytics.send(SimpleEvent(key = Analytics.Event.COLOR_COPY_PALETTE))
//                                                    requireContext().clipboardCopy(
//                                                        Settings.colorType.colorToString(
//                                                            colorItem.color,
//                                                            withSeparator = ","
//                                                        )
//                                                    )
//                                                    requireContext().showToast(R.string.module_other_color_pick_color_copy)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = palettes.firstOrNull { it.isCurrent }?.colors?.isNotEmpty() == true,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 16.dp, bottom = 26.dp)) {
                        Menu {
                            val view = LocalView.current
                            IconItem(painter = painterResource(id = R.drawable.ic_add_circle)) {
                                view.vibrate(EVibrate.BUTTON)
                                stateCreateColor = true
                            }
                        }
                    }
                }
            }
        }
    }
}
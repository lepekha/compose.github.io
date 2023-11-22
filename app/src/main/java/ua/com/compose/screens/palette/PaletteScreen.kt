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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
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
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.extension.showToast
import ua.com.compose.extension.vibrate
import ua.com.compose.screens.info.InfoScreen
import ua.com.compose.screens.share.ShareScreen
import kotlin.math.ceil
import kotlin.math.floor

data class TuneColorState(val id: Long, val color: Color)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaletteScreen(viewModule: PaletteViewModule) {
    val context = LocalContext.current
    val palettes by viewModule.palettes.observeAsState(listOf())
    val scope = rememberCoroutineScope()
    var stateRemovePalette: Item? by remember { mutableStateOf(null) }
    var stateCreatePalette by remember { mutableStateOf(false) }
    var stateTuneColor: TuneColorState? by remember { mutableStateOf(null) }
    var stateCreateColor: Boolean by remember { mutableStateOf(false) }
    var stateSharePalette: Long? by remember { mutableStateOf(null) }
    var stateInfoColor: Int? by remember { mutableStateOf(null) }

    val view = LocalView.current

    fun touchedColor(value: Int) {
        view.vibrate(EVibrate.BUTTON)
        stateInfoColor = palettes.firstOrNull { it.isCurrent }?.colors?.getOrNull(value)?.color ?: -1
    }

    fun touchedID(value: Int): String {
        return palettes.firstOrNull { it.isCurrent }?.colors?.getOrNull(value)?.id?.toString() ?: ""
    }

    if(stateCreatePalette) {
        val defaultName = Settings.defaultPaletteName(context, withIncrement = false)
        DialogInputText(
            text = stringResource(id = R.string.color_pick_pallet_name),
            hint = defaultName,
            onDone = {
                if(defaultName == it) {
                    Settings.defaultPaletteName(context, withIncrement = true)
                }
                viewModule.createPallet(name = it)
            }) {
            stateCreatePalette = false
        }
    }

    stateSharePalette?.let {
        ShareScreen(paletteId = it, viewModel = koinViewModel()) {
            stateSharePalette = null
        }
    }

    if(stateRemovePalette != null) {
        stateRemovePalette?.let { palette ->
            DialogConfirmation(
                title = palette.name,
                text = stringResource(id = R.string.color_pick_remove_pallet),
                onDone = {
                    viewModule.pressRemovePallet(id = palette.id)
                }) {
                stateRemovePalette = null
            }
        }
    }

    stateTuneColor?.let { state ->
        DialogColorPick(
            color = state.color,
            onDone = {
                Settings.lastColor = it.toArgb()
                viewModule.changeColor(state.id, it.toArgb())
            },
            onInfo = {
                stateInfoColor = it.toArgb()
            }) {
            stateTuneColor = null
        }
    }

    stateCreateColor.takeIf { it }?.let {
        DialogColorPick(
            color = Color(Settings.lastColor),
            onDone = {
                Settings.lastColor = it.toArgb()
                viewModule.addColor(it.toArgb())
            },
            onInfo = {
                stateInfoColor = it.toArgb()
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
            val view = LocalView.current
            IconButton(
                shape = RoundedCornerShape(25.dp),
                painter = painterResource(R.drawable.ic_palette_add),
                modifier = Modifier.size(100.dp)) {
                    view.vibrate(EVibrate.BUTTON)
                    stateCreatePalette = true
            }
        }
    } else {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(4.dp)
                    .zIndex(1f)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(top = 15.dp, bottom = 10.dp)) {

                var draged by remember { mutableStateOf(false) }
                var hoveredItem by remember { mutableStateOf(false) }

                LazyRow(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    item {
                        Box(
                            Modifier.width(100.dp), contentAlignment = Alignment.Center) {
                            val view = LocalView.current

                            val buttonContainer = when {
                                hoveredItem -> MaterialTheme.colorScheme.primary
                                draged -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.secondary
                            }

                            val buttonContent = when {
                                hoveredItem -> MaterialTheme.colorScheme.onPrimaryContainer
                                draged -> MaterialTheme.colorScheme.onTertiaryContainer
                                else -> MaterialTheme.colorScheme.onSecondary
                            }

                            val buttonIcon = when {
                                draged -> painterResource(R.drawable.ic_add_fill)
                                else -> painterResource(R.drawable.ic_palette_add)
                            }

                            FilledTonalIconButton(
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = buttonContainer,
                                    contentColor = buttonContent
                                ),
                                modifier = Modifier
                                    .size(48.dp)
                                    .dragAndDropTarget(
                                        onStarted = {
                                            draged = true
                                            true
                                        },
                                        onDropped = {
                                            draged = false
                                            hoveredItem = false
                                            val colorId =
                                                it.toAndroidDragEvent().clipData.getItemAt(0).text
                                                    .toString()
                                                    .toLong()
                                            viewModule.dropColorToPallet(
                                                colorId = colorId,
                                                palletId = null
                                            )
                                            stateCreatePalette = true
                                            true
                                        },
                                        onEntered = {
                                            view.vibrate(EVibrate.BUTTON)
                                            hoveredItem = true
                                        },
                                        onExited = {
                                            hoveredItem = false
                                        },
                                        onEnded = {
                                            draged = false
                                        }
                                    ),
                                onClick = {
                                    view.vibrate(EVibrate.BUTTON)
                                    stateCreatePalette = true
                                },
                                shape = MaterialTheme.shapes.small) {
                                Icon(painter = buttonIcon, modifier = Modifier.fillMaxSize(0.60f), contentDescription = null)
                            }
                        }
                    }
                    items(items = palettes, key = { it.id }) { pallet ->

                        var hovered by remember { mutableStateOf(false) }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .animateItemPlacement()
                                .height(105.dp)
                                .padding(top = 0.dp, bottom = 0.dp)
                                .width(150.dp)
                        ) {

                            val cardBorder = when {
                                hovered -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                draged -> BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiaryContainer)
                                pallet.isCurrent -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                else -> BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary)
                            }

                            val view = LocalView.current
                            FilledTonalIconButton(
                                modifier = Modifier
                                    .width(140.dp)
                                    .weight(1f),
                                shape = MaterialTheme.shapes.extraSmall,
                                onClick = {
                                    view.vibrate(EVibrate.BUTTON)
                                    viewModule.selectPallet(id = pallet.id)
                                }
                            ) {

                                val containerBackground = if(draged) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer

                                Card(elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                    border = cardBorder,
                                    colors = CardDefaults.cardColors(containerColor = containerBackground),
                                    shape = MaterialTheme.shapes.extraSmall,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .dragAndDropTarget(
                                            onStarted = {
                                                view.vibrate(EVibrate.BUTTON)
                                                draged = true
                                                true
                                            },
                                            onDropped = { event ->
                                                draged = false
                                                hovered = false
                                                val colorId =
                                                    event.toAndroidDragEvent().clipData.getItemAt(0).text
                                                        .toString()
                                                        .toLong()
                                                viewModule.dropColorToPallet(
                                                    colorId = colorId,
                                                    palletId = pallet.id
                                                )
                                                true
                                            },
                                            onEntered = {
                                                view.vibrate(EVibrate.BUTTON)
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

                                        val (icon, tint) = when {
                                            hovered -> {
                                                painterResource(id = R.drawable.ic_add_fill) to ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                            }
                                            draged -> {
                                                painterResource(id = R.drawable.ic_add_fill) to ColorFilter.tint(MaterialTheme.colorScheme.onTertiaryContainer)
                                            }
                                            else -> {
                                                painterResource(id = R.drawable.ic_palette) to ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer)
                                            }
                                        }

                                        Image(
                                            painter = icon,
                                            contentDescription = pallet.name,
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
                                            Row(modifier = Modifier
                                                .fillMaxHeight()
                                                .padding(4.dp), verticalAlignment = Alignment.Bottom) {
                                                if(pallet.colors.isNotEmpty()) {
                                                    FilledTonalIconButton(
                                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                            containerColor = MaterialTheme.colorScheme.secondary,
                                                            contentColor = MaterialTheme.colorScheme.onSecondary
                                                        ),
                                                        modifier = Modifier
                                                            .height(30.dp)
                                                            .weight(1.0f),
                                                        onClick = {
                                                            view.vibrate(EVibrate.BUTTON)
                                                            stateSharePalette = pallet.id
                                                        },
                                                        shape = MaterialTheme.shapes.extraSmall.copy(CornerSize(5.dp))) {

                                                        Icon(painter = painterResource(R.drawable.ic_share), modifier = Modifier.fillMaxSize(0.60f), contentDescription = null)
                                                    }
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                }

                                                FilledTonalIconButton(
                                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.secondary,
                                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                                    ),
                                                    modifier = Modifier
                                                        .height(30.dp)
                                                        .weight(1.0f),
                                                    onClick = {
                                                        view.vibrate(EVibrate.BUTTON)
                                                        stateRemovePalette = pallet
                                                    },
                                                    shape = MaterialTheme.shapes.extraSmall.copy(CornerSize(5.dp))) {

                                                    Icon(painter = painterResource(R.drawable.ic_delete), modifier = Modifier.fillMaxSize(0.60f), contentDescription = null)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                                Text(text = pallet.name,
                                    color = if(pallet.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight(500),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    textAlign = TextAlign.Center)
                        }

                    }
                }
            }

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = MaterialTheme.colorScheme.outlineVariant
                ))

            Box(modifier = Modifier.fillMaxSize()) {
                palettes.firstOrNull { it.isCurrent }?.let { item ->
                    val align = Alignment.Center.takeIf { _ -> item.colors.isEmpty() == true } ?: Alignment.TopCenter
                    Box(
                        contentAlignment = align,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        if (item.colors.isEmpty()) {
                            val view = LocalView.current
                            IconButton(
                                painter = painterResource(R.drawable.ic_add_circle),
                                shape = RoundedCornerShape(25.dp),
                                modifier = Modifier.size(100.dp)
                            ) {
                                view.vibrate(EVibrate.BUTTON)
                                stateCreateColor = true
                            }
                        } else {
                            val view = LocalView.current
                            LazyColumn(
                                modifier = Modifier.matchParentSize(),
                                contentPadding = PaddingValues(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 60.dp)
                            ) {
                                items(
                                    count = item.colors.size
                                ) {
                                    val colorItem  = item.colors[it]
                                    val cardColor = MaterialTheme.colorScheme.surfaceContainer
                                    Card(elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                        colors = CardDefaults.cardColors(containerColor = cardColor),
                                        modifier = Modifier
                                            .height(68.dp)
                                            .padding(start = 4.dp, top = 6.dp, end = 4.dp)
                                            .fillMaxWidth()
                                            .dragAndDropSource ({
                                                drawRoundRect(color = Color(colorItem.color), topLeft = Offset.Zero.copy(x = this.size.width / 4), size = this.size.copy(width = this.size.width / 2), cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()))
                                            }){
                                                detectTapGestures(
                                                    onTap = { _ ->
                                                        touchedColor(it)
                                                    },
                                                    onLongPress = { _ ->
                                                        startTransfer(
                                                            DragAndDropTransfer(
                                                                clipData = ClipData.newPlainText(
                                                                    "COLOR_ID",
                                                                    touchedID(it)
                                                                ),
                                                                flags = View.DRAG_FLAG_GLOBAL,
                                                            )
                                                        )
                                                    }
                                                )
                                            }
                                            .animateItemPlacement(),
                                        shape = RoundedCornerShape(10.dp)) {
                                        Row(modifier = Modifier.wrapContentHeight(), verticalAlignment = Alignment.CenterVertically) {

                                            Box(
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .fillMaxHeight()
                                                    .aspectRatio(1f, true)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(colorItem.color)),
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

                                            Column(
                                                    modifier = Modifier
                                                        .wrapContentHeight()
                                                        .padding(start = 4.dp)
                                                        .weight(1f),
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    val hex = "#${Integer.toHexString(colorItem.color).substring(2).lowercase()}"
                                                    Text(
                                                        text = Settings.colorType.colorToString(colorItem.color, withSeparator = ","),
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        fontSize = 16.sp,
                                                        lineHeight = 17.sp,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis,
                                                        fontWeight = FontWeight(700)
                                                    )
                                                    Text(
                                                        text = ColorNames.getColorName(hex),
                                                        fontSize = 14.sp,
                                                        lineHeight = 15.sp,
                                                        maxLines = 1,
                                                        modifier = Modifier.padding(top = 4.dp),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .wrapContentWidth()
                                                    .padding(end = 4.dp, top = 8.dp, bottom = 8.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                androidx.compose.material3.IconButton(
                                                    colors = IconButtonDefaults.iconButtonColors(
                                                        contentColor = MaterialTheme.colorScheme.onSurface
                                                    ),
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .padding(2.dp),
                                                    onClick = {
                                                        view.vibrate(EVibrate.BUTTON)
                                                        viewModule.removeColor(id = colorItem.id)
                                                    }) {
                                                    Icon(painter = painterResource(R.drawable.ic_delete), modifier = Modifier.fillMaxSize(0.60f), contentDescription = null)
                                                }

                                                androidx.compose.material3.IconButton(
                                                    colors = IconButtonDefaults.iconButtonColors(
                                                        contentColor = MaterialTheme.colorScheme.onSurface
                                                    ),
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .padding(2.dp),
                                                    onClick = {
                                                        view.vibrate(EVibrate.BUTTON)
                                                        stateTuneColor = TuneColorState(id = colorItem.id, Color(colorItem.color))
                                                    }) {
                                                    Icon(painter = painterResource(R.drawable.ic_color_tune), modifier = Modifier.fillMaxSize(0.60f), contentDescription = null)
                                                }

                                                androidx.compose.material3.IconButton(
                                                    colors = IconButtonDefaults.iconButtonColors(
                                                      contentColor = MaterialTheme.colorScheme.onSurface
                                                    ),
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .padding(2.dp),
                                                    onClick = {
                                                        view.vibrate(EVibrate.BUTTON)
                                                        analytics.send(SimpleEvent(key = Analytics.Event.COLOR_COPY_PALETTE))
                                                        context.clipboardCopy(Settings.colorType.colorToString(color = colorItem.color))
                                                        context.showToast(R.string.color_pick_color_copy)
                                                    }) {
                                                    Icon(painter = painterResource(R.drawable.ic_copy), modifier = Modifier.fillMaxSize(0.60f), contentDescription = null)
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
                        .navigationBarsPadding()
                        .padding(start = 16.dp, end = 16.dp)
                    ) {
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
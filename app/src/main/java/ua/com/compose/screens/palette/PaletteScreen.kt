package ua.com.compose.screens.palette

import android.content.ClipData
import android.content.ClipDescription
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
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
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
import org.koin.androidx.compose.koinViewModel
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.composable.IconButton
import ua.com.compose.composable.IconItem
import ua.com.compose.composable.LocalToastState
import ua.com.compose.composable.Menu
import ua.com.compose.data.enums.EColorType
import ua.com.compose.data.InfoColor
import ua.com.compose.dialogs.DialogColorPick
import ua.com.compose.dialogs.DialogConfirmation
import ua.com.compose.dialogs.DialogInputText
import ua.com.compose.extension.color
import ua.com.compose.extension.userColorName
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.asComposeColor
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.extension.vibrate
import ua.com.compose.colors.data.Color
import ua.com.compose.colors.textColor
import ua.com.compose.screens.info.InfoScreen
import ua.com.compose.screens.genPalette.PalettesScreen
import ua.com.compose.screens.share.ShareScreen
import kotlin.math.ceil
import kotlin.math.floor

data class TuneColorState(val id: Long, val name: String?, val color: Color)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaletteScreen(viewModule: PaletteViewModule) {
    val context = LocalContext.current
    val palettes by viewModule.palettes.observeAsState(listOf())
    val colorType by Settings.colorType.flow.collectAsState(initial = Settings.colorType.value)
    val lastColor by Settings.lastColor.flow.collectAsState(initial = Settings.lastColor.value)
    var stateCreatePalette by remember { mutableStateOf(false) }
    var stateTuneColor: TuneColorState? by remember { mutableStateOf(null) }
    var stateCreateColor: Boolean by remember { mutableStateOf(false) }
    var statePalettes: Boolean by remember { mutableStateOf(false) }
    var stateSharePalette: Pair<String, Long>? by remember { mutableStateOf(null) }
    var stateInfoColor: InfoColor? by remember { mutableStateOf(null) }

    val toastState = LocalToastState.current
    val view = LocalView.current

    val string_copy = stringResource(id = R.string.color_pick_color_copy)

    fun touchedColor(value: Int) {
        view.vibrate(EVibrate.BUTTON)
        val color = palettes.firstOrNull { it.isCurrent }?.colors?.getOrNull(value)?.color() ?: return
        val name = palettes.firstOrNull { it.isCurrent }?.colors?.getOrNull(value)?.userColorName() ?: return
        stateInfoColor = InfoColor(name = name, color = color)
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

    if(statePalettes) {
        PalettesScreen {
            statePalettes = false
        }
    }

    stateSharePalette?.let {
        ShareScreen(paletteId = it.second, viewModel = koinViewModel()) {
            stateSharePalette = null
        }
    }

    stateTuneColor?.let { state ->
        DialogColorPick(
            name = state.name,
            color = state.color,
            cancelString = stringResource(id = R.string.color_pick_cancel),
            doneString = stringResource(id = R.string.color_pick_change),
            onDone = { name, color ->
                Settings.lastColor.update(color)
                viewModule.changeColor(state.id, name, color)
            }) {
            stateTuneColor = null
        }
    }

    stateCreateColor.takeIf { it }?.let {
        DialogColorPick(
            name = null,
            color = lastColor,
            cancelString = stringResource(id = R.string.color_pick_cancel),
            doneString = stringResource(id = R.string.color_pick_add),
            onDone = { name, color ->
                Settings.lastColor.update(color)
                viewModule.addColor(name, color)
            }) {
                stateCreateColor = false
            }
    }

    stateInfoColor?.let {
        InfoScreen(name = it.name, color = it.color) {
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
                                hoveredItem -> MaterialTheme.colorScheme.onTertiaryContainer
                                draged -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.secondary
                            }

                            val buttonContent = when {
                                hoveredItem -> MaterialTheme.colorScheme.tertiaryContainer
                                draged -> MaterialTheme.colorScheme.onTertiaryContainer
                                else -> MaterialTheme.colorScheme.onSecondary
                            }

                            val buttonIcon = when {
                                draged -> painterResource(R.drawable.ic_add_fill)
                                else -> painterResource(R.drawable.ic_palette_add)
                            }

                            val callback = remember {
                                object : DragAndDropTarget {
                                    override fun onEnded(event: DragAndDropEvent) {
                                        draged = false
                                    }

                                    override fun onEntered(event: DragAndDropEvent) {
                                        view.vibrate(EVibrate.BUTTON)
                                        hoveredItem = true
                                    }

                                    override fun onExited(event: DragAndDropEvent) {
                                        hoveredItem = false
                                    }

                                    override fun onStarted(event: DragAndDropEvent) {
                                        draged = true
                                    }

                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        draged = false
                                        hoveredItem = false
                                        val colorId = event.toAndroidDragEvent().clipData.getItemAt(0).text
                                                .toString()
                                                .toLong()
                                        viewModule.dropColorToPallet(
                                            colorId = colorId,
                                            palletId = null
                                        )
                                        stateCreatePalette = true
                                        return true
                                    }
                                }
                            }

                            FilledTonalIconButton(
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = buttonContainer,
                                    contentColor = buttonContent
                                ),
                                modifier = Modifier
                                    .size(48.dp)
                                    .dragAndDropTarget(
                                        shouldStartDragAndDrop = {
                                            it
                                                .mimeTypes()
                                                .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                                        },
                                        target = callback
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
                                hovered -> BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiaryContainer)
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
                                    if(pallet.isCurrent) {
                                        stateSharePalette = pallet.name to pallet.id
                                    } else {
                                        viewModule.selectPallet(id = pallet.id)
                                    }
                                }
                            ) {

                                val containerBackground = when {
                                    hovered -> MaterialTheme.colorScheme.onTertiaryContainer
                                    draged -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                }

                                val callback = remember {
                                    object : DragAndDropTarget {
                                        override fun onEnded(event: DragAndDropEvent) {
                                            draged = false
                                        }

                                        override fun onEntered(event: DragAndDropEvent) {
                                            view.vibrate(EVibrate.BUTTON)
                                            hovered = true
                                        }

                                        override fun onExited(event: DragAndDropEvent) {
                                            hovered = false
                                        }

                                        override fun onStarted(event: DragAndDropEvent) {
                                            view.vibrate(EVibrate.BUTTON)
                                            draged = true
                                        }

                                        override fun onDrop(event: DragAndDropEvent): Boolean {
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
                                            return true
                                        }
                                    }
                                }

                                Card(elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                    border = cardBorder,
                                    colors = CardDefaults.cardColors(containerColor = containerBackground),
                                    shape = MaterialTheme.shapes.extraSmall,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .dragAndDropTarget(
                                            shouldStartDragAndDrop = {
                                                it
                                                    .mimeTypes()
                                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                                            },
                                            target = callback
                                        )
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {

                                        val (icon, tint) = when {
                                            hovered -> {
                                                painterResource(id = R.drawable.ic_add_fill) to ColorFilter.tint(MaterialTheme.colorScheme.tertiaryContainer)
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
                                                    val color = pallet.colors[i].color().asComposeColor()
                                                    val left = floor(i * squareSize)
                                                    val top = 0f
                                                    val right = ceil(left + squareSize)
                                                    val bottom = this.size.height
                                                    drawRect(color, Offset(left, top), size = Size(width = right - left, height = bottom))
                                                }
                                            }
                                        }

                                        if(pallet.isCurrent && !draged) {
                                            Box(modifier = Modifier.fillMaxSize().padding(7.dp),
                                                contentAlignment = Alignment.TopEnd) {
                                                FilledTonalIconButton(
                                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                                        contentColor = MaterialTheme.colorScheme.onTertiary
                                                    ),
                                                    modifier = Modifier
                                                        .height(27.dp).width(27.dp),
                                                    onClick = {
                                                        view.vibrate(EVibrate.BUTTON)
                                                        stateSharePalette = pallet.name to pallet.id
                                                    },
                                                    shape = CircleShape) {

                                                    Icon(painter = painterResource(R.drawable.ic_more_horz), modifier = Modifier.fillMaxSize(0.68f), contentDescription = null)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                                Text(text = pallet.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
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
                val currentPallet = palettes.firstOrNull { it.isCurrent }
                currentPallet?.let { item ->
                    val align = Alignment.Center.takeIf { _ -> item.colors.isEmpty() == true } ?: Alignment.TopCenter
                    Box(
                        contentAlignment = align,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                bottom = (55.dp + WindowInsets.navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding()).takeIf { item.colors.isEmpty() }
                                    ?: 0.dp)
                    ) {
                        if (item.colors.isEmpty()) {
                            IconButton(
                                painter = painterResource(R.drawable.ic_add_circle),
                                shape = RoundedCornerShape(25.dp),
                                modifier = Modifier.size(100.dp)
                            ) {
                                view.vibrate(EVibrate.BUTTON)
                                stateCreateColor = true
                            }
                        } else {
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
                                            .dragAndDropSource({
                                                drawRoundRect(
                                                    color = colorItem
                                                        .color()
                                                        .asComposeColor(),
                                                    topLeft = Offset.Zero.copy(x = this.size.width / 4),
                                                    size = this.size.copy(width = this.size.width / 2),
                                                    cornerRadius = CornerRadius(
                                                        10.dp.toPx(),
                                                        10.dp.toPx()
                                                    )
                                                )
                                            }) {
                                                detectTapGestures(
                                                    onTap = { _ ->
                                                        touchedColor(it)
                                                    },
                                                    onLongPress = { of ->
                                                        startTransfer(
                                                            DragAndDropTransferData(
                                                                ClipData.newPlainText(
                                                                    "COLOR_ID",
                                                                    touchedID(it)
                                                                ),
                                                                flags = View.DRAG_FLAG_GLOBAL
                                                            )
                                                        )
                                                    })
                                            }
                                            .animateItemPlacement(),
                                        shape = RoundedCornerShape(10.dp)) {
                                        Row(modifier = Modifier.wrapContentHeight(), verticalAlignment = Alignment.CenterVertically) {

                                            Box(
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .fillMaxHeight()
                                                    .aspectRatio(1f, true)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        colorItem
                                                            .color()
                                                            .asComposeColor()
                                                    ),
                                                contentAlignment = Alignment.TopEnd
                                            ) {
                                                Image(
                                                    alignment = Alignment.Center,
                                                    painter = painterResource(id = R.drawable.ic_info),
                                                    contentDescription = null,
                                                    colorFilter = ColorFilter.tint(
                                                        color = colorItem.color().textColor().asComposeColor()
                                                    ),
                                                    modifier = Modifier
                                                        .padding(2.dp)
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
                                                    Text(
                                                        text = colorType.colorToString(colorItem.color()),
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        fontSize = 16.sp,
                                                        lineHeight = 17.sp,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis,
                                                        fontWeight = FontWeight(600)
                                                    )
                                                    Text(
                                                        text = colorItem.userColorName(),
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
                                                        stateTuneColor = TuneColorState(id = colorItem.id, name = colorItem.name, color = colorItem.color())
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
                                                        context.clipboardCopy(colorType.colorToString(color = colorItem.color()))
                                                        toastState.showMessage(string_copy)
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
                    visible = palettes.isNotEmpty(),
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(start = 16.dp, end = 16.dp)
                    ) {
                        Menu {
                            IconItem(painter = painterResource(id = R.drawable.ic_stars)) {
                                view.vibrate(EVibrate.BUTTON)
                                statePalettes = true
                            }

                            if(palettes.firstOrNull { it.isCurrent }?.colors?.isNotEmpty() == true) {
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
}
package ua.com.compose.screens.genPalette

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import ua.com.compose.Palettes
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.composable.BottomSheet
import ua.com.compose.dialogs.DialogBilling
import ua.com.compose.dialogs.DialogConfirmation
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.asComposeColor
import ua.com.compose.extension.throttleLatest
import ua.com.compose.extension.vibrate
import ua.com.compose.colors.asHSV
import ua.com.compose.colors.data.HSVColor
import ua.com.compose.colors.data.IColor
import ua.com.compose.composable.colorBars.HueVBar
import ua.com.compose.composable.colorBars.SatValBar
import kotlin.math.ceil
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalettesScreen(onDismissRequest: () -> Unit) {
    val viewModel: PalettesViewModel = koinViewModel()
    val context = LocalContext.current
    val isPremium by viewModel.isPremium.observeAsState(false)
    val lastColor = Settings.lastColor.value
    val palettes by viewModel.palettes.observeAsState(initial = emptyList())
    val view = LocalView.current
    var stateCreatePalette by remember { mutableStateOf<Palettes.Item?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bottomInset = WindowInsets.navigationBars

    var hsv: MutableState<IColor> = remember {
        mutableStateOf(lastColor.asHSV())
    }

    var stateShowBilling by remember { mutableStateOf(false) }
    if(stateShowBilling) {
        DialogBilling(text = stringResource(id = R.string.color_pick_half_access)) {
            stateShowBilling = false
        }
    }

    val scope = rememberCoroutineScope()

    val throttleLatest: ((IColor) -> Unit) = remember {
        throttleLatest(
            withFirst = true,
            intervalMs = 200,
            coroutineScope = scope
        ) {
            viewModel.generatePalettesForColor(color = it)
        }
    }

    LaunchedEffect(key1 = hsv.value) {
        throttleLatest.invoke(hsv.value)
    }

    stateCreatePalette?.let { item ->
        DialogConfirmation(
            title = viewModel.currentPalette?.name ?: "",
            text = stringResource(id = R.string.color_pick_add_colors),
            onDone = {
                viewModel.addColorsToCurrentPalette(item.colors)
                onDismissRequest.invoke()
            }) {
            stateCreatePalette = null
        }
    }

    BottomSheet(withSpacer = false, sheetState = sheetState, onDismissRequest = onDismissRequest) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
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
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = stringResource(id = R.string.color_pick_generate_palettes),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 20.dp, bottom = 15.dp),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 32.sp,
                    fontWeight = FontWeight(700)
                )

                SatValBar(
                    modifier = Modifier
                        .height(130.dp)
                        .padding(start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(16.dp))),
                    color = hsv
                )

                Spacer(
                    modifier = Modifier.height(
                        8.dp
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 17.dp),
                            text = stringResource(id = R.string.color_bar_hue),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                        Text(
                            modifier = Modifier.padding(end = 17.dp),
                            text = "${(hsv.value as HSVColor).hue.roundToInt()}º",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp,
                            fontWeight = FontWeight(600)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    HueVBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp),
                        color = hsv
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(
                start = 10.dp,
                end = 10.dp,
                top = 10.dp,
                bottom = bottomInset.asPaddingValues().calculateBottomPadding()
            ),
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {

            itemsIndexed(palettes) { index, item ->

                var alpha = 1f
                var icon: Int? = null
                val showFull = isPremium || index < 3
                if(!showFull) {
                    alpha = 0.2f
                    icon = R.drawable.ic_lock
                }

                FilledTonalIconButton(
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                        .height(100.dp)
                        .border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.5f), MaterialTheme.shapes.extraSmall),
                    onClick = {
                        view.vibrate(type = EVibrate.BUTTON)
                        if(showFull) {
                            stateCreatePalette = item
                        } else {
                            stateShowBilling = true
                        }
                    }
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val numColors = item.colors.size
                            val squareSize = this.size.height / numColors // Розмір кожного квадратика
                            for (i in 0 until numColors) {
                                val color = item.colors[i].asComposeColor()
                                val left = 0f
                                val top = ceil(i * squareSize)
                                val right = this.size.width
                                val bottom = ceil(top + squareSize)
                                drawRect(
                                    color.copy(alpha = alpha),
                                    Offset(left, top),
                                    size = Size(width = right, height = bottom - top)
                                )
                            }
                        }
                        icon?.let {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize(0.30f)
                                    .aspectRatio(1f, true),
                                painter = painterResource(id = icon),
                                tint = MaterialTheme.colorScheme.inverseSurface,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}
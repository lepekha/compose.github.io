package ua.com.compose.screens.colorWheel

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.protobuf.Value
import org.koin.androidx.compose.koinViewModel
import ua.com.compose.Palettes
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.colors.asHSL
import ua.com.compose.composable.BottomSheet
import ua.com.compose.dialogs.DialogBilling
import ua.com.compose.dialogs.DialogConfirmation
import ua.com.compose.extension.asComposeColor
import ua.com.compose.extension.throttleLatest

import ua.com.compose.colors.asHSV
import ua.com.compose.colors.data.HSLColor
import ua.com.compose.colors.data.HSVColor
import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.textColor
import ua.com.compose.composable.LocalToastState
import ua.com.compose.composable.colorBars.HueVBar
import ua.com.compose.composable.colorBars.LightnessBar
import ua.com.compose.composable.colorBars.ValueBar
import ua.com.compose.data.enums.EColorSchemeType
import ua.com.compose.dialogs.ColorWheel
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.asIColor
import ua.com.compose.extension.vibrate
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ColorWheelScreen(onDismissRequest: () -> Unit) {
    val viewModel: ColorWheelViewModel = koinViewModel()
    val colorSchemeType by Settings.colorWheelScheme.flow.collectAsState(initial = Settings.colorWheelScheme.value)
    val isPremium by viewModel.isPremium.observeAsState(false)
    val lastColor = Settings.lastColor.value
    val view = LocalView.current
    val toastState = LocalToastState.current
    val string_add_to_pallete = stringResource(id = R.string.color_pick_color_add_to_pallete)
    val bottomInset = WindowInsets.navigationBars
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var stateCreatePalette by remember { mutableStateOf(false) }

    var color: MutableState<IColor> = remember {
        mutableStateOf(lastColor.asHSL())
    }

    var colorCount by remember {
        mutableIntStateOf(5)
    }

    var stateShowBilling by remember { mutableStateOf(false) }
    if (stateShowBilling) {
        DialogBilling(text = stringResource(id = R.string.color_pick_half_access)) {
            stateShowBilling = false
        }
    }

    if(stateCreatePalette) {
        DialogConfirmation(
            title = viewModel.currentPalette?.name ?: "",
            text = stringResource(id = R.string.color_pick_add_colors),
            onDone = {
                viewModel.addColorsToCurrentPalette()
                onDismissRequest.invoke()
            }) {
            stateCreatePalette = false
        }
    }

    BottomSheet(withSpacer = true, sheetState = sheetState, onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 0.dp, start = 0.dp, end = 0.dp)
        ) {
            Text(
                text = stringResource(id = R.string.color_color_wheel),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 32.sp,
                fontWeight = FontWeight(700)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ColorWheel(
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp)
                    .weight(1f)
                    .aspectRatio(1f, false),
                color = color,
                count = colorCount,
                scheme = colorSchemeType
            )
            {
                color.value = it.first()

                viewModel.colors.clear()
                viewModel.colors.addAll(it)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.color_bar_lightness),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight(500)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically // Додаємо вирівнювання по вертикалі
                ) {
                    LightnessBar(
                        modifier = Modifier
                            .weight(1f)
                            .height(26.dp),
                        color = color
                    )

                    Text(
                        modifier = Modifier
                            .defaultMinSize(48.dp)
                            .wrapContentWidth(),
                        textAlign = TextAlign.End,
                        text = "${((color.value as HSLColor).lightness * 100).roundToInt()}%",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.color_color_scheme),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight(500)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(start = 24.dp, end = 16.dp), modifier = Modifier.fillMaxWidth()) {
                items(EColorSchemeType.entries) {
                    val onlyForPremium = !(isPremium || it.allowForAll)
                    val bColor = if(it == colorSchemeType) MaterialTheme.colorScheme.secondary else Color.Transparent
                    val cColor = if(it == colorSchemeType) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    val alpha = if(onlyForPremium) 0.40f else 1f

                    Box(contentAlignment = Alignment.Center) {

                        if(onlyForPremium) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(id = R.drawable.ic_lock),
                                tint = cColor,
                                contentDescription = null
                            )
                        }

                        Icon(
                            painter = painterResource(id = it.drawableResID),
                            modifier = Modifier
                                .size(50.dp)
                                .background(bColor, CircleShape)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .clickable {
                                    view.vibrate(EVibrate.BUTTON)
                                    if (onlyForPremium) {
                                        stateShowBilling = true
                                    } else {
                                        Settings.colorWheelScheme.update(it)
                                    }
                                },
                            tint = cColor.copy(alpha = alpha),
                            contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(start = 20.dp, end = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.color_result),
                    modifier = Modifier
                        .weight(1f),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight(600)
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = colorSchemeType.allowChangeColor(),
                    enter = fadeIn(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    Icon(
                        modifier = Modifier
                            .width(40.dp)
                            .fillMaxHeight()
                            .padding(6.dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable(enabled = colorSchemeType.checkRemoveColor(viewModel.colors.count())) {
                                view.vibrate(EVibrate.BUTTON)
                                colorCount--
                            },
                        painter = painterResource(id = R.drawable.ic_remove),
                        tint = MaterialTheme.colorScheme.onSurface.takeIf {
                            colorSchemeType.checkRemoveColor(
                                viewModel.colors.count()
                            )
                        } ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        contentDescription = null,
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = colorSchemeType.allowChangeColor(),
                    enter = fadeIn(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    Icon(
                        modifier = Modifier
                            .width(40.dp)
                            .fillMaxHeight()
                            .padding(6.dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable(enabled = colorSchemeType.checkAddColor(viewModel.colors.count())) {
                                view.vibrate(EVibrate.BUTTON)
                                colorCount++
                            },
                        painter = painterResource(id = R.drawable.ic_add),
                        tint = MaterialTheme.colorScheme.onSurface.takeIf { colorSchemeType.checkAddColor(viewModel.colors.count()) } ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        contentDescription = null,
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 24.dp, end = 24.dp)
                    .clickable {
                        view.vibrate(type = EVibrate.BUTTON)
                        stateCreatePalette = true
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.colors.forEach {
                    FilledTonalIconButton(
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = it.asComposeColor()),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = {
                            view.vibrate(EVibrate.BUTTON)
                            viewModel.pressPaletteAdd(it)
                            toastState.showColor(string_add_to_pallete)
                        }
                    ) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = viewModel.addedColors.contains(it),
                            enter = fadeIn(animationSpec = tween(500)),
                            exit = fadeOut(animationSpec = tween(500))
                        ) {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize(0.50f)
                                    .aspectRatio(1f, true),
                                painter = painterResource(id = R.drawable.ic_done),
                                tint = it.textColor().asComposeColor(),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier.height(
                    bottomInset.asPaddingValues().calculateBottomPadding()
                )
            )
        }
    }
}
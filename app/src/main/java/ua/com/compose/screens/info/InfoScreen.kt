package ua.com.compose.screens.info

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import ua.com.compose.R
import ua.com.compose.data.ColorNames
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.clipboardCopy
import ua.com.compose.extension.showToast
import ua.com.compose.extension.vibrate
import ua.com.compose.extension.visibleColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(color: Int, onDismissRequest: () -> Unit) {
    val viewModule: ColorInfoViewModel = koinViewModel()
    val context = LocalContext.current
    val items by viewModule.items.observeAsState(listOf())
    val view = LocalView.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    LaunchedEffect(key1 = viewModule) {
        viewModule.create(color)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        windowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        dragHandle = null,
    ) {

        Box(modifier = Modifier
            .fillMaxHeight()
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxWidth()
            .background(
                colorResource(id = R.color.color_main_header),
                shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)
            )) {
            val colorCopyText = stringResource(id = R.string.module_other_color_pick_color_add_to_pallete)

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp),
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 16.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .fillMaxWidth()
            ) {
                items(
                    items = items
                ) {
                    when(it){
                        is ColorInfoItem.Color -> {

                            Card(elevation =  CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(it.color)), modifier = Modifier
                                .height(70.dp)
                                .fillMaxWidth()) {
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp), contentAlignment = Alignment.TopEnd) {
                                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                        val name = "≈${ColorNames.getColorName("#"+Integer.toHexString(it.color).substring(2).toLowerCase())}"
                                        Text(text = name, color = it.color.visibleColor(), fontSize = 16.sp, fontWeight = FontWeight(700))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp))
                        }

                        is ColorInfoItem.Text -> {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 3.dp, bottom = 3.dp)
                                .clickable {
                                    view.vibrate(type = EVibrate.BUTTON)
                                    context.clipboardCopy(it.value)
                                    context.showToast(R.string.module_other_color_pick_color_copy)
                                },
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(text = it.title, textAlign = TextAlign.Start, color = colorResource(id = R.color.color_night_5), modifier = Modifier.wrapContentWidth(), fontSize = 14.sp, fontWeight = FontWeight(500))
                                Text(text = it.value, textAlign = TextAlign.End, color = colorResource(id = R.color.color_night_5), modifier = Modifier.fillMaxWidth(), fontSize = 16.sp, fontWeight = FontWeight(600))
                            }
                        }

                        is ColorInfoItem.Colors -> {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)) {
                                Text(text = it.title, textAlign = TextAlign.Start, color = colorResource(id = R.color.color_night_5), modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(bottom = 6.dp), fontSize = 14.sp, fontWeight = FontWeight(500))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    it.colors.forEach { color ->
                                        var visibleIcon by remember { mutableStateOf(false) }
                                        FilledTonalIconButton(shape = RoundedCornerShape(2.dp), modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 2.dp, end = 2.dp)
                                            .height(50.dp), onClick = {
                                                view.vibrate(type = EVibrate.BUTTON)
                                                viewModule.pressPaletteAdd(color)
                                                Toast.makeText(context, colorCopyText, Toast.LENGTH_SHORT).show()
                                                visibleIcon = true
                                        },
                                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color(color))) {
                                            Box(modifier = Modifier
                                                .fillMaxSize(),
                                                contentAlignment = Alignment.Center) {

                                                androidx.compose.animation.AnimatedVisibility(
                                                    visible = visibleIcon,
                                                    enter = fadeIn(animationSpec = tween(500)),
                                                    exit = fadeOut(animationSpec = tween(500))
                                                ) {
                                                    Icon(
                                                        modifier = Modifier.fillMaxSize(0.50f).aspectRatio(1f, true),
                                                        painter = painterResource(id = R.drawable.ic_done),
                                                        tint = color.visibleColor(),
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
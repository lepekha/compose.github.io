package ua.com.compose.screens.info

import android.app.Activity
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
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
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
import com.google.android.play.core.review.ReviewManagerFactory
import org.koin.androidx.compose.koinViewModel
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.composable.BottomSheet
import ua.com.compose.composable.rememberReviewTask
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var openInfoCount = Settings.openInfoCount
    if((openInfoCount % 25) == 0) {
        val reviewManager = remember { ReviewManagerFactory.create(context) }
        val reviewInfo = rememberReviewTask(reviewManager)
        LaunchedEffect(key1 = reviewInfo) {
            reviewInfo?.let {
                analytics.send(SimpleEvent(key = Analytics.Event.OPEN_IN_APP_REVIEW))
                reviewManager.launchReviewFlow(context as Activity, reviewInfo)
            }
        }
    }
    Settings.openInfoCount = ++openInfoCount

    LaunchedEffect(key1 = viewModule) {
        analytics.send(SimpleEvent(key = Analytics.Event.OPEN_INFO))
        viewModule.create(color)
    }

    val bottomInset = WindowInsets.navigationBars

    BottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {

        val colorCopyText = stringResource(id = R.string.color_pick_color_add_to_pallete)

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = bottomInset.asPaddingValues().calculateBottomPadding()),
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            items(
                items = items
            ) {
                when (it) {
                    is ColorInfoItem.Color -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(60.dp)
                                .background(Color(it.color), MaterialTheme.shapes.medium)
                                .fillMaxWidth()
                                .padding(top = 5.dp, bottom = 5.dp, start = 5.dp, end = 5.dp)
                        ) {
                            val name = "≈${
                                ColorNames.getColorName(
                                    "#" + Integer.toHexString(it.color).substring(2).toLowerCase()
                                )
                            }"
                            Text(
                                text = name,
                                color = it.color.visibleColor(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight(700)
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                        )
                    }

                    is ColorInfoItem.Text -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 2.dp, bottom = 2.dp)
                                .clickable {
                                    view.vibrate(type = EVibrate.BUTTON)
                                    context.clipboardCopy(it.value)
                                    analytics.send(SimpleEvent(key = Analytics.Event.COLOR_COPY_INFO))
                                    context.showToast(R.string.color_pick_color_copy)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.title,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.wrapContentWidth(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight(500)
                            )
                            Text(
                                text = it.value,
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(600)
                            )
                        }
                    }

                    is ColorInfoItem.Colors -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = it.titleResId),
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(bottom = 4.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(500)
                            )

                            Row(modifier = Modifier.fillMaxWidth()) {
                                it.colors.forEach { color ->
                                    var visibleIcon by remember { mutableStateOf(false) }
                                    FilledTonalIconButton(
                                        shape = RoundedCornerShape(2.dp), modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 2.dp, end = 2.dp)
                                            .height(50.dp), onClick = {
                                            view.vibrate(type = EVibrate.BUTTON)
                                            viewModule.pressPaletteAdd(color)
                                            Toast.makeText(
                                                context,
                                                colorCopyText,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            visibleIcon = true
                                        },
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = Color(color)
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {

                                            androidx.compose.animation.AnimatedVisibility(
                                                visible = visibleIcon,
                                                enter = fadeIn(animationSpec = tween(500)),
                                                exit = fadeOut(animationSpec = tween(500))
                                            ) {
                                                Icon(
                                                    modifier = Modifier
                                                        .fillMaxSize(0.50f)
                                                        .aspectRatio(1f, true),
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
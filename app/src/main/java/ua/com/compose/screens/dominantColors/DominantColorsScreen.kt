package ua.com.compose.screens.dominantColors

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import ua.com.compose.R
import ua.com.compose.composable.BottomSheet
import ua.com.compose.dialogs.DialogBilling
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.showToast
import ua.com.compose.extension.vibrate
import ua.com.compose.extension.visibleColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DomainColors(colors: List<Color>, onDismissRequest: () -> Unit) {
    val viewModule: DominantColorsViewModule = koinViewModel()
    val context = LocalContext.current
    val view = LocalView.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isPremium by viewModule.isPremium.observeAsState(false)

    var stateShowBilling by remember { mutableStateOf(false) }
    if(stateShowBilling) {
        DialogBilling(text = stringResource(id = R.string.color_pick_half_access)) {
            stateShowBilling = false
        }
    }

    LaunchedEffect(key1 = viewModule) {
        if(colors.isEmpty()) {
            context.showToast(R.string.color_pick_color_missing)
            onDismissRequest.invoke()
        } else {
            viewModule.init(colors)
        }
    }

    val bottomInset = WindowInsets.navigationBars

    BottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.color_pick_color_overview),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 4.dp),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 30.sp,
                fontWeight = FontWeight(500)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = bottomInset.asPaddingValues().calculateBottomPadding()
                ),
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {

                val size = viewModule.domainColors.size
                itemsIndexed(viewModule.domainColors) { index, item ->
                    var visibleIcon by remember { mutableStateOf(false) }

                    var alpha = 1f
                    if(!isPremium && index > size / 2) {
                        alpha = 0.2f
                    }

                    FilledTonalIconButton(
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = item.copy(alpha = alpha)),
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp)
                            .aspectRatio(1f, true),
                        onClick = {
                            view.vibrate(type = EVibrate.BUTTON)
                            if(isPremium or (index <= size / 2)) {
                                viewModule.pressPaletteAdd(item.toArgb())
                                visibleIcon = true
                                context.showToast(R.string.color_pick_color_add_to_pallete)
                            } else {
                                stateShowBilling = true
                            }
                        }
                    ) {

                        if((!isPremium && index > size / 2)) {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize(0.50f)
                                    .aspectRatio(1f, true),
                                painter = painterResource(id = R.drawable.ic_lock),
                                tint = MaterialTheme.colorScheme.inverseSurface,
                                contentDescription = null
                            )
                        }

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
                                tint = item.toArgb().visibleColor(),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}
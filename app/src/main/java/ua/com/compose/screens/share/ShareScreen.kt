package ua.com.compose.screens.share

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.com.compose.R
import ua.com.compose.composable.ActionIconButton
import ua.com.compose.composable.BottomNotification
import ua.com.compose.composable.BottomSheet
import ua.com.compose.composable.LocalToastState
import ua.com.compose.data.enums.EExportType
import ua.com.compose.data.enums.EFileExportScheme
import ua.com.compose.data.enums.EImageExportScheme
import ua.com.compose.dialogs.DialogBilling
import ua.com.compose.dialogs.DialogConfirmation
import ua.com.compose.dialogs.DialogInputText
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShareScreen(paletteId: Long, viewModel: ShareViewModel, onDismissRequest: () -> Unit) {
    val view = LocalView.current
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isPremium by viewModel.isPremium.observeAsState(false)
    val palette by viewModel.palette.observeAsState()

    val scope = rememberCoroutineScope()
    val toastState = LocalToastState.current
    val string_palette_saved = stringResource(id = R.string.color_pick_palette_saved)
    val containerBackground = MaterialTheme.colorScheme.surfaceContainerLow

    DisposableEffect(key1 = Unit) {
        onDispose { viewModel.resetSnackbarState() }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.create(paletteId)
    }

    val state = viewModel.snackbarUIState.value.state
    LaunchedEffect(key1 = state) {
        when(state) {
            is ShareViewModel.SnackbarState.PALETTE_SAVED -> {
                toastState.showMessage(string_palette_saved)
            }
            else -> {

            }
        }
    }

    var stateShowBilling by remember { mutableStateOf(false) }
    if(stateShowBilling) {
        DialogBilling(text = stringResource(id = R.string.color_pick_half_access)) {
            stateShowBilling = false
        }
    }

    var stateRemovePalette: Boolean by remember { mutableStateOf(false) }
    if(stateRemovePalette) {
        DialogConfirmation(
            title = palette?.name ?: "",
            text = stringResource(id = R.string.color_pick_remove_pallet),
            onDone = {
                viewModel.pressRemovePallet()
                onDismissRequest.invoke()
            }) {
            stateRemovePalette = false
        }
    }

    var stateRenamePalette: Boolean by remember { mutableStateOf(false) }
    if(stateRenamePalette) {
        val oldName = palette?.name ?: return run { stateRenamePalette = false }
        DialogInputText(
            text = stringResource(id = R.string.color_pick_pallet_name),
            hint = oldName,
            onDone = {
                if(oldName != it) {
                    viewModel.renamePalette(name = it)
                }
            }) {
            stateRenamePalette = false
        }
    }

    var stateShowFileShare: Pair<String, EFileExportScheme>? by remember { mutableStateOf(null) }
    stateShowFileShare?.let {
        BottomNotification(
            text = it.first.plus(it.second.fileFormat()),
            actions = listOf(
                ActionIconButton(
                    icon = painterResource(id = R.drawable.ic_download),
                    action = {
                        viewModel.createFile(context = context, paletteID = paletteId, exportType = EExportType.SAVE, scheme = it.second)
                    }
                ),
                ActionIconButton(
                    icon = painterResource(id = R.drawable.ic_share),
                    action = {
                        viewModel.createFile(context = context, paletteID = paletteId, exportType = EExportType.SHARE, scheme = it.second)
                    }
                )
            )
            ) {
            stateShowFileShare = null
        }
    }


    var stateShowImageShare: Triple<String, ByteArray, EImageExportScheme>? by remember { mutableStateOf(null) }
    stateShowImageShare?.let {
        BottomNotification(
            text = it.first.plus(it.third.fileFormat()),
            imageModel = it.second,
            actions = listOf(
                ActionIconButton(
                    icon = painterResource(id = R.drawable.ic_download),
                    action = {
                        viewModel.startLoadImage(it.third)
                        scope.launch(Dispatchers.IO) {
                            val request = ImageRequest.Builder(context)
                                .data(it.second)
                                .size(Size.ORIGINAL)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build()

                            val drawable = ImageLoader.Builder(context)
                                .components {
                                    add(SvgDecoder.Factory())
                                }
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build().execute(request).drawable ?: return@launch

                            viewModel.createImage(context = context, paletteID = paletteId, exportType = EExportType.SAVE, imageType = it.third, image = drawable.toBitmap())
                        }
                    }
                ),
                ActionIconButton(
                    icon = painterResource(id = R.drawable.ic_share),
                    action = {
                        viewModel.startLoadImage(it.third)
                        scope.launch(Dispatchers.IO) {
                            val request = ImageRequest.Builder(context)
                                .data(it.second)
                                .size(Size.ORIGINAL)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build()

                            val drawable = ImageLoader.Builder(context)
                                .components {
                                    add(SvgDecoder.Factory())
                                }
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build().execute(request).drawable ?: return@launch

                            viewModel.createImage(context = context, paletteID = paletteId, exportType = EExportType.SHARE, imageType = it.third, image = drawable.toBitmap())
                        }
                    }
                )
            )
            ) {
            stateShowImageShare = null
        }
    }

    BottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {

        LazyColumn(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            item {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = containerBackground,
                                shape = MaterialTheme.shapes.medium
                            ),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                            text = palette?.name ?: "",
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight(500)
                        )


                        androidx.compose.material3.IconButton(
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .size(40.dp)
                                .padding(2.dp),
                            onClick = {
                                view.vibrate(EVibrate.BUTTON)
                                stateRenamePalette = true
                            }) {
                            Icon(imageVector = Icons.Rounded.Edit, modifier = Modifier.fillMaxSize(0.60f), contentDescription = null)
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
                                stateRemovePalette = true
                            }) {
                            Icon(painter = painterResource(R.drawable.ic_delete), modifier = Modifier.fillMaxSize(0.60f), contentDescription = null)
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }
            }




                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Column(
                        modifier = Modifier
                            .background(
                                color = containerBackground,
                                shape = MaterialTheme.shapes.medium
                            )
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Text(
                            text = stringResource(id = R.string.color_pick_file),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight(500)
                        )

                        if(viewModel.images.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))

                            FlowRow(maxItemsInEachRow = 3, verticalArrangement = Arrangement.spacedBy((-8).dp, Alignment.Top)) {
                                EFileExportScheme.entries.forEach { scheme ->
                                    val onlyForPremium = !(isPremium || scheme.allowForAll)
                                    val alpha = if(onlyForPremium) 0.5f else 1f

                                    FilledTonalButton(
                                        contentPadding = PaddingValues(0.dp),
                                        onClick = {
                                            view.vibrate(EVibrate.BUTTON)

                                            if(onlyForPremium) {
                                                stateShowBilling = true
                                            } else {
                                                stateShowFileShare = (palette?.name ?: "new") to scheme
                                            }
                                        },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = alpha),
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        shape = MaterialTheme.shapes.small,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp)
                                    ) {
                                        var painter = painterResource(id = R.drawable.ic_file)
                                        if(onlyForPremium) {
                                            painter = painterResource(id = R.drawable.ic_lock)
                                            Spacer(modifier = Modifier.width(3.dp))
                                        }
                                        Icon(
                                            modifier = Modifier.size(16.dp),
                                            painter = painter,
                                            contentDescription = null
                                        )
                                        Text(text = scheme.title, fontSize = 16.sp)
                                    }
                                }
                            }
                        } else {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                                contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No colors",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight(450)
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Column(
                        modifier = Modifier
                            .background(
                                color = containerBackground,
                                shape = MaterialTheme.shapes.medium
                            )
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 16.dp, bottom = 16.dp)
                    ) {

                        Text(
                            text = stringResource(id = R.string.color_pick_images),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight(500)
                        )

                        if(viewModel.images.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyRow(
                                modifier = Modifier.wrapContentHeight(),
                                contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(viewModel.images) {
                                    val onlyForPremium = !(isPremium || it.type.allowForAll)
                                    val alpha = if(onlyForPremium) 0.45f else 1f

                                    Card(
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                        shape = MaterialTheme.shapes.extraSmall,
                                        modifier = Modifier
                                            .width(90.dp)
                                            .height(157.dp)
                                            .clickable {
                                                view.vibrate(EVibrate.BUTTON)
                                                when {
                                                    it.type in viewModel.stateLoadItems -> return@clickable
                                                    onlyForPremium -> stateShowBilling = true
                                                    else -> stateShowImageShare =
                                                        Triple(
                                                            (palette?.name ?: "new"),
                                                            it.image,
                                                            it.type
                                                        )
                                                }
                                            }) {

                                        val infiniteTransition = rememberInfiniteTransition(label = "")
                                        val angle by infiniteTransition.animateFloat(
                                            initialValue = 0f,
                                            targetValue = 360f,
                                            animationSpec = infiniteRepeatable(
                                                animation = keyframes {
                                                    durationMillis = 1500
                                                }
                                            ), label = ""
                                        )
                                        if(it.type in viewModel.stateLoadItems) {
                                            Box(contentAlignment = Alignment.Center, modifier = Modifier
                                                .fillMaxSize()
                                                .background(color = MaterialTheme.colorScheme.tertiaryContainer)) {

                                                Icon(tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                                    painter = painterResource(id = R.drawable.ic_progress_bar),
                                                    modifier = Modifier
                                                        .rotate(angle)
                                                        .size(30.dp), contentDescription = null)
                                            }
                                        } else {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                AsyncImage(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .alpha(alpha),
                                                    model = it.image,
                                                    imageLoader = ImageLoader.Builder(context)
                                                        .diskCachePolicy(CachePolicy.DISABLED)
                                                        .components {
                                                            add(SvgDecoder.Factory())
                                                        }
                                                        .build(),
                                                    contentDescription = null)

                                                if(onlyForPremium) {
                                                    Icon(tint = MaterialTheme.colorScheme.inverseSurface,
                                                        painter = painterResource(id = R.drawable.ic_lock),
                                                        modifier = Modifier.size(30.dp), contentDescription = null)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                                contentAlignment = Alignment.Center) {
                                Text(
                                    text = "No colors",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight(450)
                                )
                            }
                        }

                    }
                }

        }
    }
}
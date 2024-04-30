package ua.com.compose.screens.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.com.compose.R
import ua.com.compose.composable.BottomSheet
import ua.com.compose.data.EFileExportScheme
import ua.com.compose.dialogs.DialogBilling
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShareScreen(paletteId: Long, viewModel: ShareViewModel, onDismissRequest: () -> Unit) {
    val view = LocalView.current
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isPremium by viewModel.isPremium.observeAsState(false)

    val containerBackground = MaterialTheme.colorScheme.surfaceContainerLow

    var stateShowBilling by remember { mutableStateOf(false) }
    if(stateShowBilling) {
        DialogBilling(text = stringResource(id = R.string.color_pick_half_access)) {
            stateShowBilling = false
        }
    }

    LaunchedEffect(key1 = Any()) {
        viewModel.create(paletteId)
    }

    BottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {

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

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(maxItemsInEachRow = 3, verticalArrangement = Arrangement.spacedBy((-8).dp, Alignment.Top)) {
                    EFileExportScheme.entries.forEach { scheme ->
                        val onlyForPremium = !(isPremium || scheme.allowForAll)
                        val alpha = if(onlyForPremium) 0.5f else 1f

                        FilledTonalButton(
                            onClick = {
                                view.vibrate(EVibrate.BUTTON)

                                if(onlyForPremium) {
                                    stateShowBilling = true
                                } else {
                                    viewModel.createFile(context = context, paletteID = paletteId, scheme = scheme)
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = alpha),
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            if(onlyForPremium) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    painter = painterResource(id = R.drawable.ic_lock),
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(text = scheme.title, fontSize = 16.sp)
                        }
                    }
                }

            }

//            Spacer(modifier = Modifier.height(8.dp))
//
//            Column(
//                modifier = Modifier
//                    .background(
//                        color = containerBackground,
//                        shape = MaterialTheme.shapes.medium
//                    )
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//
//                Text(
//                    text = stringResource(id = R.string.color_pick_image),
//                    textAlign = TextAlign.Start,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight(500)
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                LazyVerticalGrid(GridCells.Fixed(3)) {
//                    item {
//                        val textMeasurer = rememberTextMeasurer()
//                        Canvas(modifier = Modifier
//                            .fillMaxWidth()
//                            .height(160.dp)
//                            .padding(4.dp)) {
//
//                            this.drawRect(color = Color.White)
//
//                            val count = viewModel.colors.count()
//                            val itemH = this.size.height / count
//                            var topLeft = Offset.Zero
//                            viewModel.colors.forEach { color ->
//                                this.drawRect(color = Color(color), topLeft = topLeft, size = Size(this.size.width, itemH))
//
////                                val paint = Paint().asFrameworkPaint()
////                                val text = Settings.colorType.colorToString(color = color)
////                                val textSize = 7.sp
////                                paint.textSize = textSize.value
////
////                                val textLayoutResult = textMeasurer.measure(
////                                    AnnotatedString(text),
////                                    style = TextStyle(fontSize = textSize)
////                                )
////
////                                this.drawText(
////                                    textMeasurer = textMeasurer,
////                                    text = Settings.colorType.colorToString(color = color),
////                                    style = TextStyle(color = color.visibleColor(), fontSize = TextUnit(textSize.value, TextUnitType.Sp)),
////                                    size = Size(textLayoutResult.size.width.toFloat(), itemH),
////                                    topLeft = Offset(
////                                        x = (topLeft.x + (this.size.width - textLayoutResult.size.width) / 2),
////                                        y = (topLeft.y + itemH / 6)
////                                    )
////                                )
//                                topLeft = topLeft.copy(y = topLeft.y + itemH)
//                            }
//
//                        }
//                    }
//
//                    item {
//                        Canvas(modifier = Modifier
//                            .fillMaxWidth()
//                            .height(160.dp)
//                            .padding(4.dp)) {
//                            this.drawRect(color = Color.White)
//
//                            val count = viewModel.colors.count()
//                            val itemW = this.size.width / count
//                            var topLeft = Offset.Zero
//                            viewModel.colors.forEach { color ->
//                                this.drawRect(color = Color(color), topLeft = topLeft, size = Size(itemW, this.size.height))
//                                topLeft = topLeft.copy(x = topLeft.x + itemW)
//                            }
//                        }
//                    }
//
//                    item {
//                        Canvas(modifier = Modifier
//                            .fillMaxWidth()
//                            .height(160.dp)
//                            .padding(4.dp)) {
//                            this.drawRect(color = Color.White)
//                            val numberOfSquares = viewModel.colors.count()
//                            val squaresInRow = Math.ceil(Math.sqrt(numberOfSquares.toDouble())).toInt()
//
//                            val squareSizeX = this.size.width / squaresInRow
//                            val squareSizeY = this.size.height / squaresInRow
//
//                            var topLeft = Offset.Zero
//                            viewModel.colors.forEachIndexed { index, color ->
//                                this.drawRect(color = Color(color), topLeft = topLeft, size = Size(squareSizeX, squareSizeY))
//                                topLeft = topLeft.copy(x = topLeft.x + squareSizeX)
//
//                                if((index + 1) % squaresInRow == 0) {
//                                    topLeft = topLeft.copy(x = 0f, y = topLeft.y + squareSizeY)
//                                }
//                            }
//                        }
//                    }
//
//                    item {
//                        Canvas(modifier = Modifier
//                            .fillMaxWidth()
//                            .height(160.dp)
//                            .padding(4.dp)) {
//                            this.drawRect(color = Color.White)
//                            val numberOfSquares = viewModel.colors.count()
//                            val squaresInRow = Math.ceil(Math.sqrt(numberOfSquares.toDouble())).toInt()
//
//                            val squareSize = min(this.size.width / squaresInRow, this.size.height / squaresInRow)
//
//                            var topLeft = Offset.Zero
//                            viewModel.colors.forEachIndexed { index, color ->
//                                this.drawCircle(color = Color(color), radius = squareSize / 3, center = topLeft.plus(Offset(squareSize / 2f, squareSize / 2f)))
//                                topLeft = topLeft.copy(x = topLeft.x + squareSize)
//
//                                if((index + 1) % squaresInRow == 0) {
//                                    topLeft = topLeft.copy(x = 0f, y = topLeft.y + squareSize)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}
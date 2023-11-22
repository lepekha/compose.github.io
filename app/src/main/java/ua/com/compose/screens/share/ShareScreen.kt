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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.com.compose.R
import ua.com.compose.composable.BottomSheet
import ua.com.compose.data.EFileExportScheme
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShareScreen(paletteId: Long, viewModel: ShareViewModel, onDismissRequest: () -> Unit) {
    val view = LocalView.current
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val containerBackground = MaterialTheme.colorScheme.surfaceContainerLow

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
                        FilledTonalButton(
                            onClick = {
                                view.vibrate(EVibrate.BUTTON)
                                viewModel.createFile(context = context, paletteID = paletteId, scheme = scheme)
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(text = scheme.title, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
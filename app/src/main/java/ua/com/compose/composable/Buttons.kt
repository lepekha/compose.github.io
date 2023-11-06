package ua.com.compose.composable

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ua.com.compose.R
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = modalBottomSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
                .background(Color.White)
                .height(400.dp))
        }
}

@Composable
fun IconButton(painter: Painter, modifier: Modifier, click: () -> Unit) {
    FilledTonalIconButton(
        colors = IconButtonColors(
            containerColor = colorResource(id = R.color.color_button_background),
            contentColor = colorResource(id = R.color.color_night_9),
            disabledContainerColor = colorResource(id = R.color.color_button_background),
            disabledContentColor = colorResource(id = R.color.color_night_9)
        ),
        modifier = modifier,
        onClick = click,
        shape = RoundedCornerShape(corner = CornerSize(6.dp))) {

        Icon(painter = painter, modifier = Modifier.fillMaxSize(0.60f), contentDescription = null)
    }
}
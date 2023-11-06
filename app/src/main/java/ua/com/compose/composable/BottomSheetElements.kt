package ua.com.compose.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import ua.com.compose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogBottomSheet(onDismissRequest: () -> Unit,
                      sheetState: SheetState = rememberModalBottomSheetState(),
                      content: @Composable BoxScope.() -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        windowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        dragHandle = null,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 20.dp, end = 20.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        colorResource(id = R.color.color_main_header),
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                content = content
            )
        }
    }
}
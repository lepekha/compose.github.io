package ua.com.compose.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogBottomSheet(onDismissRequest: () -> Unit,
                      content: @Composable BoxScope.() -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .fillMaxWidth()
                    .wrapContentHeight(),
                content = {
                    Box(content = content, modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight())
                }
            )
        }
    }
}

@Composable
fun DialogConfirmButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val view = LocalView.current
    Button(colors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary
    ),
        modifier = modifier,
        onClick = {
            view.vibrate(EVibrate.BUTTON)
            onClick.invoke()
        }) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight(600))
    }
}
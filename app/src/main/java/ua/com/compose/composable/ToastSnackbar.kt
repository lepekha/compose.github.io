package ua.com.compose.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay
import ua.com.compose.R

object GlobalToastID {
    private var value = 0;

    fun generateID(): Int = ++value
}
data class ToastMessage(val id: Int = GlobalToastID.generateID(), val text: String)

class ToastState {
    private val _messages = mutableStateListOf<ToastMessage>()
    val messages: List<ToastMessage> get() = _messages

    fun showMessage(newMessage: String) {
        _messages.add(ToastMessage(text = newMessage))
    }

    fun showColor(newMessage: String) {
        _messages.add(ToastMessage(text = newMessage))
    }

    fun removeMessage(message: ToastMessage) {
        _messages.remove(message)
    }
}

val LocalToastState = compositionLocalOf<ToastState> { error("No ToastState provided") }

@Composable
fun rememberToastState() = remember { ToastState() }

@Composable
fun ToastSnackbar(toastState: ToastState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding() + 40.dp
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            toastState.messages.forEach { message ->
                key(message.id) {
                    ToastSnackbarMessage(message = message, onDismiss = { toastState.removeMessage(it) })
                }
            }
        }
    }
}

@Composable
fun ToastSnackbarMessage(message: ToastMessage, onDismiss: (ToastMessage) -> Unit) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(message.id) {
        delay(3000)  // Час показу повідомлення
        isVisible = false
        delay(300)  // Додатковий час для завершення анімації зникнення
        onDismiss(message)
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Popup(
            onDismissRequest = {},
            alignment = Alignment.BottomCenter,
            properties = PopupProperties(dismissOnClickOutside = false)
        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .background(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        shape = CircleShape
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(26.dp)
                        .clip(CircleShape),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    text = message.text
                )
            }
        }
    }
}
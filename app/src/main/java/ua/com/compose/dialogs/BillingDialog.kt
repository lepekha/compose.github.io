package ua.com.compose.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ua.com.compose.AppBilling
import ua.com.compose.R
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.composable.DialogBottomSheet
import ua.com.compose.composable.DialogConfirmButton
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.appBilling
import ua.com.compose.extension.findActivity
import ua.com.compose.extension.vibrate

@Composable
fun DialogBilling(text: String, onDismissRequest: () -> Unit) {
    DialogBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        val context = LocalContext.current
        val view = LocalView.current

        LaunchedEffect(key1 = Unit) {
            analytics.send(SimpleEvent(key = Analytics.Event.OPEN_PREMIUM_DIALOG))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.color_pick_premium_version),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight(400),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp)
            )

            Text(
                text = text,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight(400),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp)
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(end = 8.dp, top = 30.dp)
                    .fillMaxWidth()
            ) {
                DialogConfirmButton(text = stringResource(id = R.string.color_pick_cancel)) {
                    view.vibrate(EVibrate.BUTTON)
                    onDismissRequest.invoke()
                }
                DialogConfirmButton(text = stringResource(id = R.string.color_pick_buy)) {
                    view.vibrate(EVibrate.BUTTON)
                    analytics.send(SimpleEvent(key = Analytics.Event.CLICK_BUY_PREMIUM))
                    appBilling.buyPremium(activity = context.findActivity())
                    onDismissRequest.invoke()
                }
            }
        }
    }
}
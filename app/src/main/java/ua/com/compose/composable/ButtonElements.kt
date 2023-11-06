package ua.com.compose.composable

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ua.com.compose.R

@Composable
fun DialogAccentButton(painter: Painter, modifier: Modifier, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        colors = IconButtonColors(
            containerColor = colorResource(id = R.color.color_night_6),
            contentColor = colorResource(id = R.color.color_night_10),
            disabledContainerColor = colorResource(id = R.color.color_button_background),
            disabledContentColor = colorResource(id = R.color.color_night_9)
        )
    ) {
        Icon(
            painter = painter,
            modifier = Modifier.fillMaxHeight(0.6f).aspectRatio(1f, true),
            contentDescription = null
        )
    }
}

@Composable
fun DialogButton(painter: Painter, modifier: Modifier, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        colors = IconButtonColors(
            containerColor = colorResource(id = R.color.color_button_background),
            contentColor = colorResource(id = R.color.color_night_9),
            disabledContainerColor = colorResource(id = R.color.color_button_background),
            disabledContentColor = colorResource(id = R.color.color_night_9)
        )
    ) {
        Icon(
            painter = painter,
            modifier = Modifier.fillMaxHeight(0.6f).aspectRatio(1f, true),
            contentDescription = null
        )
    }
}
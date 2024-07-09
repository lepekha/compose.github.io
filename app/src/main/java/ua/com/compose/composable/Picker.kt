package ua.com.compose.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.com.compose.R
import ua.com.compose.extension.asComposeColor
import ua.com.compose.colors.data.Color
import ua.com.compose.colors.textColor

@Composable
fun ColorPickerRing(modifier: Modifier = Modifier, color: Color) {
    val borderColor = color.textColor()
    Box(contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .alpha(0.5f)
                .border(3.dp, borderColor.asComposeColor(), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(44.dp)
                .border(7.dp, color.asComposeColor(), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(15.dp)
                .alpha(0.5f)
                .border(3.dp, borderColor.asComposeColor(), CircleShape)
        )
    }
}


data class ColorState(val color: Color, val name: String, val typeValue: String)

@Composable
fun ColorPickerInfo(state: ColorState, click: () -> Unit) {
    val borderColor = state.color.textColor()


    FilledTonalIconButton(
        onClick = click,
        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = state.color.asComposeColor()),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.height(60.dp).fillMaxWidth().border(BorderStroke(width = 2.dp, MaterialTheme.colorScheme.secondaryContainer), MaterialTheme.shapes.medium)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(5.dp), contentAlignment = Alignment.TopEnd) {
            Image(
                alignment = Alignment.Center,
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = null,
                colorFilter = ColorFilter.tint(borderColor.asComposeColor()),
                modifier = Modifier
                    .size(24.dp)
                    .alpha(0.5f)
            )
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = state.typeValue, color = borderColor.asComposeColor(), fontSize = 18.sp, lineHeight = 19.sp, fontWeight = FontWeight(700))
                Text(text = state.name, color = borderColor.asComposeColor(), fontSize = 14.sp, lineHeight = 15.sp, fontWeight = FontWeight(600))
            }
        }
    }
}
package ua.com.compose.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.data.ColorNames
import ua.com.compose.extension.visibleColor

@Composable
fun ColorPickerRing(modifier: Modifier = Modifier, color: Int) {
    val borderColor = color.visibleColor()
    Box(contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .alpha(0.5f)
                .border(3.dp, borderColor, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(44.dp)
                .border(7.dp, Color(color), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(15.dp)
                .alpha(0.5f)
                .border(3.dp, borderColor, CircleShape)
        )
    }
}

@Composable
fun ColorPickerInfo(color: Int, click: () -> Unit) {
    val borderColor = color.visibleColor()


    FilledTonalIconButton(onClick = click, shape = RoundedCornerShape(16.dp), modifier = Modifier.height(60.dp).fillMaxWidth().border(BorderStroke(width = 2.dp, colorResource(id = R.color.color_main_menu_background)), RoundedCornerShape(16.dp))) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(color)),
            border = BorderStroke(width = 2.dp, colorResource(id = R.color.color_main_menu_background))
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(5.dp), contentAlignment = Alignment.TopEnd) {
                Image(
                    alignment = Alignment.Center,
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(borderColor),
                    modifier = Modifier
                        .size(24.dp)
                        .alpha(0.5f)
                )
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    val name = "≈${ColorNames.getColorName("#"+Integer.toHexString(color).substring(2).toLowerCase())}"
                    Text(text = Settings.colorType.colorToString(color = color), color = borderColor, fontSize = 18.sp, fontWeight = FontWeight(700))
                    Text(text = name, color = borderColor, fontSize = 14.sp, fontWeight = FontWeight(600))
                }
            }
        }
    }
}
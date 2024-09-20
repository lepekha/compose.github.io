package ua.com.compose.dialogs.colorPickDialog.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.com.compose.R
import ua.com.compose.colors.asHSV
import ua.com.compose.colors.asRGB
import ua.com.compose.colors.data.IColor
import ua.com.compose.colors.data.RGBColor

@Composable
fun CreateRGBColor(
    modifier: Modifier = Modifier,
    color: MutableState<IColor>
) {

    val rgbColor = remember {
        color.value = color.value.asRGB()
        color.value
    }

    Column(modifier = modifier
        .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(start = 17.dp),
                    text = stringResource(id = R.string.color_bar_red),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
                Text(
                    modifier = Modifier.padding(end = 17.dp),
                    text = (color.value as RGBColor).red.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            ua.com.compose.composable.colorBars.RedBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                color = color
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(start = 17.dp),
                    text = stringResource(id = R.string.color_bar_green),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
                Text(
                    modifier = Modifier.padding(end = 17.dp),
                    text = (color.value as RGBColor).green.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            ua.com.compose.composable.colorBars.GreenBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                color = color
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(start = 17.dp),
                    text = stringResource(id = R.string.color_bar_blue),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
                Text(
                    modifier = Modifier.padding(end = 17.dp),
                    text = (color.value as RGBColor).blue.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            ua.com.compose.composable.colorBars.BlueBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                color = color
            )
        }
    }
}
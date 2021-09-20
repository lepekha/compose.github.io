package ua.com.compose

import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

@SuppressLint("ConflictingOnColor")
private val DarkColors = darkColors(
    primary = colorNight1,
    primaryVariant = colorNight2,
    secondary = colorNight3,
    background = colorNight1,
    onPrimary = colorNight3
)

@Composable
fun ThemeCompose(
    darkTheme: Boolean,
    content: @Composable () -> Unit
){
    MaterialTheme(
        colors = if (darkTheme) DarkColors else DarkColors
    ) {
        content()
    }

}
package ua.com.compose.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import kotlin.math.roundToInt

fun Bitmap.resizeImage(
    maxImageSize: Float,
    filter: Boolean
): Bitmap {
    val ratio = Math.min(
        maxImageSize / this.width,
        maxImageSize / this.height
    )
    val width = Math.round(ratio * this.width)
    val height = Math.round(ratio * this.height)
    return Bitmap.createScaledBitmap(
        this, width,
        height, filter
    )
}


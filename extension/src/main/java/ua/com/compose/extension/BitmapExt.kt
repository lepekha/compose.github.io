package ua.com.compose.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import java.io.File
import java.io.FileOutputStream
import java.util.*
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

fun Bitmap.mergeWith(bmp: Bitmap): Bitmap {
    val bmOverlay = Bitmap.createBitmap(this.width, this.height, this.config)
    val canvas = Canvas(bmOverlay)
    canvas.drawBitmap(this, Matrix(), null)
    canvas.drawBitmap(bmp,  (this.width / 2f) - (bmp.width / 2f), (this.height / 2f) - (bmp.height / 2f), null)
    this.recycle()
    bmp.recycle()
    return bmOverlay
}

fun Bitmap.blur(context: Context): Bitmap {
    val width = (this.width * 0.4f).roundToInt()
    val height = (this.height * 0.4f).roundToInt()

    val inputBitmap = Bitmap.createScaledBitmap(this, width, height, false)
    val outputBitmap = Bitmap.createBitmap(inputBitmap)

    val rs = RenderScript.create(context)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(12f)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)

    return outputBitmap
}


package com.inhelp.view.customView.reply

import android.graphics.Bitmap

interface ReplyStyle {
    fun generateImage(image: Bitmap): Bitmap
}
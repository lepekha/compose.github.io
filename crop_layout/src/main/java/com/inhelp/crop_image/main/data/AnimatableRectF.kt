package com.inhelp.crop_image.main.data

import android.graphics.Rect
import android.graphics.RectF

class AnimatableRectF(val cropRect: Rect) {

    fun setTop(top: Int) {
        cropRect.top = top
    }

    fun setBottom(bottom: Int) {
        cropRect.bottom = bottom
    }

    fun setRight(right: Int) {
        cropRect.right = right
    }

    fun setLeft(left: Int) {
        cropRect.left = left
    }
}
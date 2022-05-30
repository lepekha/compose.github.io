package ua.com.compose.image_maker.data

import android.graphics.Rect
import android.graphics.RectF
import androidx.annotation.Keep

@Keep
class AnimatableRectF(val cropRect: RectF) {

    fun setTop(top: Float) {
        cropRect.top = top
    }

    fun setBottom(bottom: Float) {
        cropRect.bottom = bottom
    }

    fun setRight(right: Float) {
        cropRect.right = right
    }

    fun setLeft(left: Float) {
        cropRect.left = left
    }
}
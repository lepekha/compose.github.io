package ua.com.compose.image_maker.data

import android.graphics.Rect
import android.graphics.RectF
import androidx.annotation.Keep

@Keep
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
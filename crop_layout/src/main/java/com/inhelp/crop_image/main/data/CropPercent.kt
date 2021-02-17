package com.inhelp.crop_image.main.data

import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF

data class CropPercent(val left: Float, val top: Float, val right: Float, val bottom: Float){

    companion object {
        fun create(originRect: RectF, targetRect: RectF, left: Float, top: Float, right: Float, bottom: Float): CropPercent {
            return CropPercent(
                    left = left * originRect.width() / targetRect.width(),
                    top = top * originRect.height() / targetRect.height(),
                    right = right * originRect.width() / targetRect.width(),
                    bottom = bottom * originRect.height() / targetRect.height()
            )
        }
    }

}
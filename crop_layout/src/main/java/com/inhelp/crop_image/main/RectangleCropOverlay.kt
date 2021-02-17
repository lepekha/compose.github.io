package com.inhelp.crop_image.main

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRectF
import com.inhelp.crop_image.R
import com.inhelp.crop_image.main.data.AspectRatio
import com.inhelp.crop_image.main.data.CropPercent
import com.inhelp.extension.dp
import com.inhelp.extension.vibrate
import kotlin.math.max
import kotlin.math.min

class RectangleCropOverlay(context: Context, val aspectRatio: AspectRatio? = null, val isShowGrid: Boolean = false) : Overlay(context) {

    companion object {
        private val MIN_CROP_SIZE = 50.dp
    }

    private val cropRect = Rect()

    private val borderColor by lazy {
        ContextCompat.getColor(context, R.color.crop_border)
    }

    private val pointInnerColor by lazy {
        ContextCompat.getColor(context, R.color.crop_touch_point)
    }

    private val colorActive by lazy {
        ContextCompat.getColor(context, R.color.crop_touch_active)
    }

    private val backgroundColor by lazy {
        ContextCompat.getColor(context, R.color.crop_background)
    }

    private val backgroundPaint = Paint().apply {
        this.color = backgroundColor
        this.style = Paint.Style.FILL
    }

    private val borderPaint = Paint().apply {
        this.color = borderColor
        this.strokeWidth = 1.dp
        this.isAntiAlias = true
        this.style = Paint.Style.STROKE
    }

    private val pointInnerPaint = Paint().apply {
        this.color = pointInnerColor
        this.isAntiAlias = true
        this.strokeWidth = 1.dp
        this.style = Paint.Style.FILL
    }


    private val backgroundRect = Rect()

    override fun init(frame: View) {
        this.frame = frame
        backgroundRect.set(frame.left, frame.top, frame.right, frame.bottom)
        cropRect.setEmpty()

        val aspectRatio = aspectRatio
        if (aspectRatio != null) {
            val centerX = frame.left + (frame.width / 2)
            val centerY = frame.top + (frame.height / 2)

            var onePart = frame.width / aspectRatio.first
            if((onePart * aspectRatio.second) > frame.height){
                onePart = frame.height / aspectRatio.second
            }

            cropRect.set(
                    centerX - (onePart * aspectRatio.first / 2),
                    centerY - (onePart * aspectRatio.second / 2),
                    centerX + (onePart * aspectRatio.first / 2),
                    centerY + (onePart * aspectRatio.second / 2)
            )
        } else {
            cropRect.set(backgroundRect)
        }
    }

    private val region = Region()
    private val op = Region.Op.XOR

    override fun onDraw(canvas: Canvas) {
        region.set(backgroundRect)
        region.op(cropRect, op)

        borderPaint.color = if(!moveRect.isEmpty && pointNumber == 100){
            colorActive
        }else{
            borderColor
        }

        canvas.drawPath(region.boundaryPath, backgroundPaint)
        canvas.drawRect(cropRect, borderPaint)

        aspectRatio?.takeIf { isShowGrid }?.let { param ->
            (1 until param.first).forEach {
                canvas.drawLine(
                        cropRect.left + (cropRect.width() / param.first) * it.toFloat(),
                        cropRect.top.toFloat(),
                        cropRect.left + (cropRect.width() / param.first) * it.toFloat(),
                        cropRect.bottom.toFloat(),
                        borderPaint)
            }

            (1 until param.second).forEach {
                canvas.drawLine(
                        cropRect.left.toFloat(),
                        cropRect.top + (cropRect.height() / param.second) * it.toFloat(),
                        cropRect.right.toFloat(),
                        cropRect.top + (cropRect.height() / param.second) * it.toFloat(),
                        borderPaint)
            }
        }

        mutableListOf(
                Point(cropRect.left, cropRect.top),
                Point(cropRect.right, cropRect.top),
                Point(cropRect.right, cropRect.bottom),
                Point(cropRect.left, cropRect.bottom),
        ).forEachIndexed { index, point ->
            pointInnerPaint.color = if (pointNumber == index) {
                colorActive
            } else {
                pointInnerColor
            }
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 12.dp, pointInnerPaint)
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 12.dp, borderPaint)
        }
    }

    private var pointNumber: Int = -1
    private val touchPoint = Point()
    private var moveRect = Rect()

    override fun onTouchDown(x: Float, y: Float) {
        val xOffset = (x - 20.dp)..(x + 20.dp)
        val yOffset = (y - 20.dp)..(y + 20.dp)
        touchPoint.x = x.toInt()
        touchPoint.y = y.toInt()
        moveRect.set(cropRect)

        mutableListOf(
                Point(cropRect.left, cropRect.top),
                Point(cropRect.right, cropRect.top),
                Point(cropRect.right, cropRect.bottom),
                Point(cropRect.left, cropRect.bottom),
        ).forEachIndexed { index, point ->
            if (xOffset.contains(point.x.toFloat()) and yOffset.contains(point.y.toFloat())) {
                pointNumber = index
                moveRect.set(cropRect)
                context.vibrate(10L)
                return
            }
        }

        if (cropRect.toRectF().contains(x, y)) {
            pointNumber = 100
            moveRect.set(cropRect)
            context.vibrate(50L)
        }
    }

    override fun onTouchUp(x: Float, y: Float) {
        pointNumber = -1
        moveRect.setEmpty()
    }

    override fun onTouchMove(x: Float, y: Float) {
        val frame = frame ?: return
        val diffX = touchPoint.x - x
        val diffY = touchPoint.y - y
        val diffYRatio = diffX * ((aspectRatio?.second ?: 1).toFloat() / (aspectRatio?.first
                ?: 1).toFloat())

        if ((0..3).contains(pointNumber)) {
            val rect = Rect(moveRect)

            if (pointNumber == 0) {
                rect.set(moveRect.left - diffX.toInt(), moveRect.top - diffYRatio.toInt(), moveRect.right, moveRect.bottom)
            }

            if (pointNumber == 1) {
                rect.set(moveRect.left, moveRect.top + diffYRatio.toInt(), moveRect.right - diffX.toInt(), moveRect.bottom)
            }

            if (pointNumber == 2) {
                rect.set(moveRect.left, moveRect.top, moveRect.right - diffX.toInt(), moveRect.bottom - diffYRatio.toInt())
            }

            if (pointNumber == 3) {
                rect.set(moveRect.left - diffX.toInt(), moveRect.top, moveRect.right, moveRect.bottom + diffYRatio.toInt())
            }

            if ((rect.left < frame.left) or (rect.right > frame.right) or (rect.top < frame.top) or (rect.bottom > frame.bottom)) {
                return
            }

            if(rect.height() < MIN_CROP_SIZE || rect.width() < MIN_CROP_SIZE){
                return
            }

            cropRect.set(rect)
        } else if (!moveRect.isEmpty && pointNumber == 100) {
            val rect = Rect(moveRect.left - diffX.toInt(), moveRect.top - diffY.toInt(), moveRect.right - diffX.toInt(), moveRect.bottom - diffY.toInt())

            if (rect.left < frame.left) {
                rect.offsetTo(frame.left, rect.top)
            }

            if (rect.right > frame.right) {
                rect.offsetTo((frame.right) - rect.width(), rect.top)
            }

            if (rect.top < frame.top) {
                rect.offsetTo(rect.left, frame.top)
            }

            if (rect.bottom > frame.bottom) {
                rect.offsetTo(rect.left, (frame.bottom) - rect.height())
            }

            cropRect.set(rect)
        }
    }

    fun getCrops(originHeight: Float, originWidth: Float, sliceByAspectRatio: Boolean): MutableList<CropPercent> {
        val param = aspectRatio
        val rects = mutableListOf<CropPercent>()
        val originRect = RectF(0f, 0f, originWidth, originHeight)
        if (param != null && sliceByAspectRatio) {
            val oneSide = cropRect.width() / param.first
            (0 until param.first).forEach { first ->
                (0 until param.second).forEach { second ->
                    val cropPercent = CropPercent.create(
                            originRect = originRect,
                            targetRect = backgroundRect.toRectF(),
                            left = (cropRect.left - backgroundRect.left) + oneSide * first.toFloat(),
                            top = (cropRect.top - backgroundRect.top) + oneSide * second.toFloat(),
                            right = (cropRect.left - backgroundRect.left) + oneSide * (first + 1).toFloat(),
                            bottom = (cropRect.top - backgroundRect.top) + oneSide * (second + 1).toFloat()
                    )

                    rects.add(cropPercent)
                }
            }
        } else {
            val cropPercent = CropPercent.create(
                    originRect = originRect,
                    targetRect = backgroundRect.toRectF(),
                    left = (cropRect.left - backgroundRect.left).toFloat(),
                    top = (cropRect.top - backgroundRect.top).toFloat(),
                    right = (cropRect.left - backgroundRect.left) + cropRect.width().toFloat(),
                    bottom = (cropRect.top - backgroundRect.top) + cropRect.height().toFloat()
            )
            rects.add(cropPercent)
        }
        return rects
    }
}
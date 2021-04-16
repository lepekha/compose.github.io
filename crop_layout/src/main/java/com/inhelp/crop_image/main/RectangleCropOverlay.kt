package com.inhelp.crop_image.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import androidx.core.graphics.toRectF
import com.inhelp.crop_image.R
import com.inhelp.crop_image.main.data.*
import com.inhelp.extension.bitmapPosition
import com.inhelp.extension.dp
import com.inhelp.extension.vibrate

class RectangleCropOverlay(context: Context, val ratio: Ratio, val isSliceByGrid: Boolean = false, val oldRect: Rect? = null) : Overlay(context) {

    companion object {
        private val MIN_CROP_SIZE = 50.dp
    }

    private val cropRect = Rect()

    private var isAnimate = false

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

    override fun init(frame: Rect) {
        this.frame = frame
        oldRect?.takeIf { !isAnimate }?.let {
            startRectAnimation(oldRect = it)
            return
        }
        backgroundRect.set(frame.left, frame.top, frame.right, frame.bottom)
        cropRect.setEmpty()

        when(ratio){
            is Ratio.AspectRatio -> {
                val centerX = frame.left + (frame.width() / 2)
                val centerY = frame.top + (frame.height() / 2)

                var onePart = frame.width() / ratio.first
                if((onePart * ratio.second) > frame.height()){
                    onePart = frame.height() / ratio.second
                }

                cropRect.set(
                        centerX - (onePart * ratio.first / 2),
                        centerY - (onePart * ratio.second / 2),
                        centerX + (onePart * ratio.first / 2),
                        centerY + (onePart * ratio.second / 2)
                )
            }
            is Ratio.OriginRatio,
            is Ratio.Custom -> {
                cropRect.set(backgroundRect)
            }
        }
    }

    private val region = Region()
    private val op = Region.Op.XOR

    override fun onDraw(canvas: Canvas) {
        gestureExclusion.clear()
        region.set(backgroundRect)
        region.op(cropRect, op)

        borderPaint.color = if(!moveRect.isEmpty && (action == ECropAction.MOVE_RECT || action == ECropAction.SCALE_RECT)){
            colorActive
        }else{
            borderColor
        }

        canvas.drawPath(region.boundaryPath, backgroundPaint)
        canvas.drawRect(cropRect, borderPaint)

        (ratio as? Ratio.AspectRatio)?.takeIf { isSliceByGrid }?.let { param ->
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
            val radius = 50.dp
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 12.dp, pointInnerPaint)
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 12.dp, borderPaint)
            gestureExclusion.add(Rect(point.x - radius.toInt() / 2, point.y - radius.toInt() / 2, point.x + radius.toInt() / 2, point.y + radius.toInt() / 2))
        }
    }

    private var pointNumber: Int = -1
    private val touchPoint = PointF()
    private var action = ECropAction.NONE
    private var moveRect = Rect()

    override fun onTouch(e: MotionEvent) {
        when {
            (e.action == MotionEvent.ACTION_DOWN) -> {
                touchPoint.set(e.x, e.y)
                val xOffset = (touchPoint.x - 20.dp)..(touchPoint.x + 20.dp)
                val yOffset = (touchPoint.y - 20.dp)..(touchPoint.y + 20.dp)

                mutableListOf(
                        Point(cropRect.left, cropRect.top),
                        Point(cropRect.right, cropRect.top),
                        Point(cropRect.right, cropRect.bottom),
                        Point(cropRect.left, cropRect.bottom),
                ).forEachIndexed { index, point ->
                    if (xOffset.contains(point.x.toFloat()) and yOffset.contains(point.y.toFloat())) {
                        pointNumber = index
                        action = ECropAction.MOVE_POINT
                        moveRect.set(cropRect)
                        context.vibrate(10L)
                        return
                    }
                }

                if (cropRect.toRectF().contains(touchPoint.x, touchPoint.y)) {
                    action = ECropAction.MOVE_RECT
                    moveRect.set(cropRect)
                    context.vibrate(50L)
                }
            }
            (e.action == MotionEvent.ACTION_MOVE) -> {
                moveFullRect(e)
                when(ratio){
                    is Ratio.Custom -> {
                        movePointByCustom(e)
                    }
                    is Ratio.OriginRatio,
                    is Ratio.AspectRatio -> {
                        movePointByAspectRatio(e)
                    }
                }
            }

            (e.action == MotionEvent.ACTION_UP)-> {
                action = ECropAction.NONE
                pointNumber = -1
                moveRect.setEmpty()
            }

            (e.actionMasked == MotionEvent.ACTION_DOWN) || (e.actionMasked == MotionEvent.ACTION_POINTER_DOWN) -> {
                if(e.pointerCount == 2 && (0 until e.pointerCount).map { cropRect.contains(e.getX(it).toInt(), e.getY(it).toInt()) }.all { it }){
                    action = ECropAction.SCALE_RECT
                }
            }
        }
    }

    private fun moveFullRect(e: MotionEvent){
        val frame = frame ?: return
        val diffX = touchPoint.x - e.x
        val diffY = touchPoint.y - e.y
        if (!moveRect.isEmpty && action == ECropAction.MOVE_RECT) {
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

    private fun movePointByAspectRatio(e: MotionEvent) {
        val frame = frame ?: return
        val aspectRatio = (ratio as? Ratio.AspectRatio) ?: Ratio.AspectRatio(1,1)
        val diffX = touchPoint.x - e.x
        val diffYRatio = diffX * ((aspectRatio.second).toFloat() / (aspectRatio.first).toFloat())

        if ((0..3).contains(pointNumber) && action == ECropAction.MOVE_POINT) {
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

            if (rect.height() < RectangleCropOverlay.MIN_CROP_SIZE || rect.width() < RectangleCropOverlay.MIN_CROP_SIZE) {
                return
            }

            cropRect.set(rect)
        }
    }

    private fun movePointByCustom(e: MotionEvent) {
        val frame = frame ?: return
        val x = when{
            (e.x < frame.left) -> frame.left
            (e.x > frame.right) -> frame.right
            else -> e.x.toInt()
        }
        val y = when{
            (e.y < frame.top) -> frame.top
            (e.y > frame.bottom) -> frame.bottom
            else -> e.y.toInt()
        }

        if ((0..3).contains(pointNumber) && action == ECropAction.MOVE_POINT) {
            val rect = Rect(moveRect)

            if (pointNumber == 0) {
                rect.set(x, y, moveRect.right, moveRect.bottom)
            }

            if (pointNumber == 1) {
                rect.set(moveRect.left, y, x, moveRect.bottom)
            }

            if (pointNumber == 2) {
                rect.set(moveRect.left, moveRect.top, x, y)
            }

            if (pointNumber == 3) {
                rect.set(x, moveRect.top, moveRect.right, y)
            }

            if (rect.height() < RectangleCropOverlay.MIN_CROP_SIZE || rect.width() < RectangleCropOverlay.MIN_CROP_SIZE) {
                return
            }

            cropRect.set(rect)
        }
    }


    fun getCrops(originHeight: Float, originWidth: Float): MutableList<CropPercent> {
        val param = ratio as? Ratio.AspectRatio
        val rects = mutableListOf<CropPercent>()
        val originRect = RectF(0f, 0f, originWidth, originHeight)
        if (param != null && isSliceByGrid) {
            val oneSide = cropRect.width() / param.first
            (0 until param.second).forEach { second ->
                (0 until param.first).forEach { first ->
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

    override fun onDoubleTap(x: Float, y: Float): Boolean {
        if (cropRect.toRectF().contains(x, y)) {
            startRectAnimation(Rect(cropRect))
        }
        return true
    }

    private fun startRectAnimation(oldRect: Rect) {
        isAnimate = true
        this.frame?.let {
            init(frame = it)
        }
        val animatableRectNew = AnimatableRectF(cropRect)
        val animateLeft: ObjectAnimator = ObjectAnimator.ofInt(animatableRectNew, "left", oldRect.left, cropRect.left)
        val animateTop: ObjectAnimator = ObjectAnimator.ofInt(animatableRectNew, "top", oldRect.top, cropRect.top)
        val animateRight: ObjectAnimator = ObjectAnimator.ofInt(animatableRectNew, "right", oldRect.right, cropRect.right)
        val animateBottom: ObjectAnimator = ObjectAnimator.ofInt(animatableRectNew, "bottom", oldRect.bottom, cropRect.bottom)

        animateBottom.addUpdateListener {
            onAnimate()
        }

        AnimatorSet().apply {
            this.playTogether(animateLeft, animateRight, animateTop, animateBottom)
            this.duration = 200
        }.start()
    }

    private val scaleRect = Rect()

    override fun onScaleBegin(x: Float, y: Float): Boolean {
        if (cropRect.toRectF().contains(x, y)) {
            scaleRect.set(cropRect)
        }
        return true
    }

    override fun onScale(scaleFactor: Float) {
        val frame = this.frame ?: return
        val diffW = scaleRect.width() - (scaleRect.width() * scaleFactor).toInt()
        val diffH = scaleRect.height() - (scaleRect.height() * scaleFactor).toInt()

        val rect = Rect(cropRect.left + diffW / 2, cropRect.top + diffH / 2, cropRect.right - diffW / 2, cropRect.bottom - diffH / 2)

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

        if (rect.left <= frame.left && rect.right >= frame.right){
            return
        }

        if (rect.top <= frame.top && rect.bottom >= frame.bottom){
            return
        }

        if(rect.height() < MIN_CROP_SIZE || rect.width() < MIN_CROP_SIZE){
            return
        }

        cropRect.set(rect)
    }

    fun getCropRect() = Rect(cropRect)
}
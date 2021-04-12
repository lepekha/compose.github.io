package com.inhelp.extension

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("ClickableViewAccessibility")
fun View.setVibrate(type: EVibrate) {
    this.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            this.context.vibrate(type = type)
        }
        false
    }
}

fun View.setPaddingTop(value: Int) = setPadding(paddingStart, value, paddingEnd, paddingBottom)

fun View.setPaddingBottom(value: Int) = setPadding(paddingStart, paddingTop, paddingEnd, value)

fun View.setPaddingLeft(value: Int) = setPadding(value, paddingTop, paddingEnd, paddingBottom)

fun View.setPaddingRight(value: Int) = setPadding(paddingStart, paddingTop, value, paddingBottom)

fun View.setMarginTop(value: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(params.leftMargin, value, params.rightMargin, params.bottomMargin)
    layoutParams = params
}

fun View.setMarginBottom(value: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, value)
    layoutParams = params
}

fun View.setMarginLeft(value: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(value, params.topMargin, params.rightMargin, params.bottomMargin)
    layoutParams = params
}

fun View.setMarginRight(value: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(params.leftMargin, params.topMargin, value, params.bottomMargin)
    layoutParams = params
}

fun RecyclerView.updateVisibleItem(peyload: Any) {
    val layoutManager = this.layoutManager as? LinearLayoutManager ?: return
    val firstItem = layoutManager.findFirstVisibleItemPosition()
    val lastItem = layoutManager.findLastVisibleItemPosition()
    this.adapter?.notifyItemRangeChanged(firstItem, (lastItem - firstItem) + 1, peyload)
}

fun RecyclerView.scrollTo(position: Int) {
    val smoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return LinearSmoothScroller.SNAP_TO_ANY
        }
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}

fun View.animateScale(toScale: Float, onStart: (() -> Unit)? = null, onEnd: (() -> Unit)? = null) {
    val scaleDownX = ObjectAnimator.ofFloat(this, "scaleX", toScale)
    val scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", toScale)
    scaleDownX.duration = 200
    scaleDownY.duration = 200

    AnimatorSet().apply {
        this.play(scaleDownX).with(scaleDownY)
        this.doOnStart {
            onStart?.invoke()
        }
        this.doOnEnd {
            onEnd?.invoke()
        }
    }.start()
}

fun View.animateMargin(top: Float? = null, bottom: Float? = null, start: Float? = null, end: Float? = null, duration: Long = 300) {
    val params = layoutParams as ConstraintLayout.LayoutParams
    val marginTop = params.topMargin
    val marginBottom = params.bottomMargin
    val marginStart = params.marginStart
    val marginEnd = params.marginEnd

    val anim = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            start?.let { params.marginStart = marginStart + (it * interpolatedTime).toInt() }
            top?.let { params.topMargin = marginTop + (it * interpolatedTime).toInt() }
            end?.let { params.marginEnd = marginEnd + (it * interpolatedTime).toInt() }
            bottom?.let { params.bottomMargin = marginBottom + (it * interpolatedTime).toInt() }
            this@animateMargin.layoutParams = params
        }
    }.apply {
        this.duration = duration
    }
    this.startAnimation(anim)
}
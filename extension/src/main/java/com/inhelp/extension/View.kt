package com.inhelp.extension

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

@SuppressLint("ClickableViewAccessibility")
fun View.setVibrate(type: EVibrate) {
    this.setOnTouchListener { _, event ->
        if(event.action == MotionEvent.ACTION_DOWN){
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
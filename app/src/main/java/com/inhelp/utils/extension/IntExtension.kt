package com.inhelp.utils.extension

import android.content.res.Resources

fun Int.percent(value: Int): Int {
    return (this * value) / 100
}

inline val Int.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)
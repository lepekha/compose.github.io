package ua.com.compose.extension

import android.content.Context
import android.graphics.Paint
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.doOnLayout
import androidx.core.widget.TextViewCompat
import java.lang.ref.WeakReference

fun TextView.changeTextAnimate(text: String){
    val anim = AlphaAnimation(1.0f, 0.0f)
    anim.duration = 200
    anim.repeatCount = 1
    anim.repeatMode = Animation.REVERSE

    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationEnd(animation: Animation?) { }
        override fun onAnimationStart(animation: Animation?) { }
        override fun onAnimationRepeat(animation: Animation?) {
                this@changeTextAnimate.text = text
        }
    })
    this.startAnimation(anim)
}

fun TextView.underLine() {
    paint.flags = paint.flags or Paint.UNDERLINE_TEXT_FLAG
    paint.isAntiAlias = true
}

fun TextView.underLineGone() {
    paint.flags = 0
    paint.isAntiAlias = true
}

fun TextView.deleteLine() {
    paint.flags = paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
    paint.isAntiAlias = true
}

fun TextView.bold() {
    paint.isFakeBoldText = true
    paint.isAntiAlias = true
}

fun TextView.setSizeOfSubstring(substring: String, sizeRes: Int) {
    val spannable = SpannableString(text)
    val start = text.indexOf(substring)
    spannable.setSpan(AbsoluteSizeSpan(context.resources.getDimensionPixelSize(sizeRes)), start, start + substring.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    text = spannable
}

fun TextView.setColorAndSizeOfSubstring(substring: String, color: Int, size: Int) {
    val spannable = SpannableString(text)
    val start = text.indexOf(substring)
    spannable.setSpan(ForegroundColorSpan(color), start, start + substring.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannable.setSpan(AbsoluteSizeSpan(size), start, start + substring.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    text = spannable
}

fun TextView.afterTextChangedListener(listener: (e: String) -> Unit) = this.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(p0: Editable) {
        listener(p0.toString())
    }

    override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        // not used in this extension
    }

    override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        // not used in this extension
    }
})

fun TextView.beforeTextChangedListener(listener: (e: String) -> Unit) = this.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(p0: Editable) {
        // not used in this extension
    }

    override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        listener(p0.toString())
    }

    override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        // not used in this extension
    }
})

fun TextView.onTextChangedListener(listener: (e: String) -> Unit) = this.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(p0: Editable) {
        // not used in this extension
    }

    override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        // not used in this extension
    }

    override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        if(this@onTextChangedListener.isFocused){
            listener(p0.toString())
        }
    }
})

fun EditText.onAction(actionID: Int, callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == actionID) {
            callback.invoke()
            return@setOnEditorActionListener true
        }
        false
    }
}

fun TextView.setDrawableRight(drawableResId: Int){
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableResId, 0)
}

fun TextView.setDrawableLeft(drawableResId: Int){
    this.setCompoundDrawablesWithIntrinsicBounds(drawableResId, 0, 0, 0)
}

fun TextView.clearDrawable(){
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
}


package ua.com.compose.extension

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import ua.com.compose.R

fun View.showTooltip(text: String) {
  Balloon.Builder(context)
    .setWidth(BalloonSizeSpec.WRAP)
    .setHeight(BalloonSizeSpec.WRAP)
    .setText(text)
    .setTextColorResource(R.color.color_night_5)
    .setTextSize(15f)
    .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
    .setArrowSize(10)
    .setArrowPosition(0.5f)
    .setPadding(12)
    .setCornerRadius(8f)
    .setAutoDismissDuration(5000)
    .setBackgroundColorResource(R.color.color_night_6)
    .setBalloonAnimation(BalloonAnimation.ELASTIC)
    .setLifecycleOwner(this.findViewTreeLifecycleOwner())
    .build()
    .showAlignTop(this)
}

fun View.hideKeyboard(): Boolean {
  try {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
  } catch (ignored: RuntimeException) {
  }
  return false
}

fun View.toggle(duration: Long = 300L) {
  Fade().apply {
    this.duration = duration
    this.addTarget(this@toggle.id)
    TransitionManager.beginDelayedTransition(this@toggle.parent as ViewGroup, this)
  }
  this.isVisible = !this.isVisible
}

fun View.animateHeightFromTo(initialHeight: Int, finalHeight: Int, duration: Long) {
  val animator = ValueAnimator.ofInt(initialHeight, finalHeight)
  animator.duration = duration
  animator.addUpdateListener {
    val value = it.animatedValue as Int
    val lp = this.layoutParams
    lp.height = value
    this.layoutParams = lp
    isVisible = value != 0
  }
  animator.start()
}

@SuppressLint("ClickableViewAccessibility")
fun View.setVibrate(type: EVibrate) {
  if(type == EVibrate.NONE){
    this.setOnTouchListener(null)
  }else{
    this.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_DOWN) {
        this.context.vibrate(type = type)
      }
      false
    }
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

  fun View.animateMargin(top: Float? = null, bottom: Float? = null, start: Float? = null, end: Float? = null, duration: Long = 300, onStart: (() -> Unit)? = null, onEnd: (() -> Unit)? = null) {
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
    anim.setAnimationListener(object : Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {}

      override fun onAnimationEnd(animation: Animation?) {
        onEnd?.invoke()
      }

      override fun onAnimationStart(animation: Animation?) {
        onStart?.invoke()
      }
    })

    this.startAnimation(anim)
  }

  fun ImageView.bitmapPosition(): FloatArray {
    val ret = FloatArray(4)
    if (this.drawable == null) return ret

    // Get image dimensions
    // Get image matrix values and place them in an array
    val f = FloatArray(9)
    this.imageMatrix.getValues(f)

    // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
    val scaleX = f[Matrix.MSCALE_X]
    val scaleY = f[Matrix.MSCALE_Y]

    // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
    val d = this.drawable
    val origW = d.intrinsicWidth
    val origH = d.intrinsicHeight

    // Calculate the actual dimensions
    val actW = origW * scaleX
    val actH = origH * scaleY
    ret[2] = actW
    ret[3] = actH

    // Get image position
    // We assume that the image is centered into ImageView
    val imgViewW = this.width
    val imgViewH = this.height
    val top = (imgViewH - actH) / 2
    val left = (imgViewW - actW) / 2
    ret[0] = left
    ret[1] = top
    return ret
  }

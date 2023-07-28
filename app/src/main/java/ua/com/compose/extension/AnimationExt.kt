package ua.com.compose.extension

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes

private class AnimationListener(
        private val onAnimationRepeat: () -> Unit,
        private val onAnimationStart: () -> Unit,
        private val onAnimationEnd: () -> Unit
) : Animation.AnimationListener {
    override fun onAnimationRepeat(p0: Animation?) = onAnimationRepeat()
    override fun onAnimationStart(p0: Animation?) = onAnimationStart()
    override fun onAnimationEnd(p0: Animation?) = onAnimationEnd()
}

fun View.playAnimation(
        @AnimRes animResId: Int,
        startOffset: Long? = null,
        duration: Long? = null,
        onAnimationRepeat: () -> Unit = {},
        onAnimationStart: () -> Unit = {},
        onAnimationEnd: () -> Unit = {}
) = with(AnimationUtils.loadAnimation(context, animResId)) {
    startOffset?.let { this.startOffset = startOffset }
    duration?.let { this.duration = duration }
    setAnimationListener(AnimationListener(onAnimationRepeat, onAnimationStart, onAnimationEnd))
    startAnimation(this)
}
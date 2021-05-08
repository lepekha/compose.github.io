package ua.com.compose.utils.extension

import androidx.fragment.app.Fragment
import androidx.transition.Transition
import androidx.transition.TransitionSet

fun Fragment.onTransitionEnd(listener: () -> Unit) {
    val transition: TransitionSet? = (this.sharedElementEnterTransition as? TransitionSet)
    if (transition == null) {
        listener()
    } else {
        transition.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                transition.removeListener(this)
                listener()
            }

            override fun onTransitionStart(transition: Transition) {}
            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionResume(transition: Transition) {}
        })
    }
}


package ua.com.compose.extension

import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.runLayoutAnimation(anim: Int) {
    AnimationUtils.loadLayoutAnimation(this.context, anim).apply {
        this@runLayoutAnimation.layoutAnimation = this
        this@runLayoutAnimation.adapter?.notifyDataSetChanged()
        this@runLayoutAnimation.scheduleLayoutAnimation()
    }
}
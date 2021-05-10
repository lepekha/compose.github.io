package ua.com.compose.gallery.main.customView

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


class CustomViewPager @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    var isPagingEnabled = true

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return isPagingEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return isPagingEnabled && super.onInterceptTouchEvent(event)
    }
}
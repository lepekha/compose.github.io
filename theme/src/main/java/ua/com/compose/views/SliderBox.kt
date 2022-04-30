/*
 * Copyright TraderEvolution Global LTD. В© 2017-2021. All rights reserved.
 */

package ua.com.compose.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.AttrRes
import ua.com.compose.R
import java.util.concurrent.TimeUnit

fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

val Int.dp: Float
    get() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)
    }

data class SliderBoxElement(val id: Int,
                            val drawable: Drawable)

@SuppressLint("UseCompatLoadingForDrawables")
class SliderBox @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    private var mItems: List<SliderBoxElement> = mutableListOf()
    private val mContainer: LinearLayout
    private var mSelected = -1
    private var mLastSelected = -1
    private var mFingerSlider: FingerSlider
    private var onSelectListener: OnSelectListener? = null

    private var mImageColor: Int = context.getColorFromAttr(R.attr.color_9)
    private var mActiveTabDrawable: Int = R.drawable.slider_image_active_background
    var isInTouch = true

    init {
        this.background = context.getDrawable(R.drawable.slider_image_background)
        setPadding(4.dp.toInt(), 4.dp.toInt(), 4.dp.toInt(), 4.dp.toInt())
        mContainer = LinearLayout(context)
        mContainer.gravity = Gravity.CENTER
        mContainer.orientation = LinearLayout.HORIZONTAL

        addView(mContainer, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        mFingerSlider = FingerSlider(context)

        addView(mFingerSlider, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    fun getItem(position: Int) = mItems[position]

    fun setSelected(selected: Int) {
        mItems.let { mItems ->
            if (mItems.size > selected) {
                this.mSelected = selected
                if (mLastSelected == -1)
                    this.mLastSelected = selected
                mFingerSlider.setImage(mItems[selected].drawable)
                mFingerSlider.id = mItems[selected].id
            }
        }
    }

    fun setActiveTabDrawableRes(activeTabDrawable: Int) {
        mFingerSlider.setBackground(activeTabDrawable)
        mActiveTabDrawable = activeTabDrawable

    }

    fun getSelected() = this.mSelected

    fun setOnSelectListener(onSelectListener: OnSelectListener) {
        this.onSelectListener = onSelectListener
    }

    fun setItems(items: List<SliderBoxElement>) {
        mContainer.removeAllViewsInLayout()
        this.mItems = items
        for ((id, drawable) in items) {
            val tv = ImageView(context)
            tv.id = id
            tv.isClickable = true
            tv.setImageDrawable(drawable)
            tv.imageTintList = ColorStateList.valueOf(mImageColor)
            val lp = LinearLayout.LayoutParams(25.dp.toInt(), 25.dp.toInt())
            lp.weight = 1f
            tv.layoutParams = lp
            mContainer.addView(tv)
        }
    }

    interface OnSelectListener {
        fun onSelect(position: Int)
    }

    private inner class FingerSlider @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {
        private val horizontalGestureDetector: GestureDetector
        private val scrollerHorizontal: Scroller
        private val tv: SliderText
        private var mWidth = 0
        private var elementSize = 0

        init {
            setPadding(0, 0, 0, 0)
            this.horizontalGestureDetector = GestureDetector(context, HorizontalGestureListener())
            scrollerHorizontal = Scroller(context)

            tv = SliderText(getContext())
            tv.setBackgroundResource(mActiveTabDrawable)
            addView(tv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        }

        fun setBackground(res: Int) {
            tv.setBackgroundResource(res)
        }

        fun setImage(image: Drawable) {
            tv.setImage(image)
        }

        fun setTint(color: Int) {
            tv.setTint(color)
        }

        override fun setId(id: Int) {
            tv.id = id
        }

        fun scrollToPosition() {
            mItems.let { mItems ->
                scrollTo(mWidth / mItems.size * mSelected * -1, scrollY)
                val task = EndOfScrollDetector()
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            if(!isInTouch) return true
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    val xCoord = event.x.toInt()
                    var selected = xCoord / elementSize
                    if (selected < 0)
                        selected = 0
                    else if (selected >= mItems.size)
                        selected = mItems.size - 1

                    this@SliderBox.setSelected(selected)
                    scrollToPosition()
                }
                MotionEvent.ACTION_DOWN ->{
                    requestDisallowInterceptTouchEvent(true)
                }
            }

            horizontalGestureDetector.onTouchEvent(event)
            return true
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)

            val width = MeasureSpec.getSize(widthMeasureSpec)
            if (mItems.isNotEmpty() && width != 0 && width != this.mWidth) {
                this.mWidth = width
                this.elementSize = width / mItems.size
                scrollToPosition()
            }
        }

        internal inner class EndOfScrollDetector : AsyncTask<Unit, Unit, Unit>() {

            override fun doInBackground(vararg params: Unit?) {
                while (scrollX * -1 != mSelected * elementSize && mSelected != mLastSelected) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(5)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onPostExecute(result: Unit) {
                super.onPostExecute(result)

                if (onSelectListener != null && mSelected != mLastSelected)
                    onSelectListener?.onSelect(mSelected)
                mLastSelected = mSelected
            }
        }

        private inner class HorizontalGestureListener : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                if ((this@FingerSlider.scrollX + distanceX) * -1 < 0 || (this@FingerSlider.scrollX + distanceX) * -1 > width - elementSize) {
                    return false
                } else {
                    this@FingerSlider.scrollBy(distanceX.toInt(), 0)
                }
                return true
            }
        }
    }

    private inner class SliderText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {
        private val imageView: ImageView = ImageView(context)

        init {
            val lp = LayoutParams(25.dp.toInt(), 25.dp.toInt())
            lp.gravity = Gravity.CENTER
            imageView.layoutParams = lp
            addView(imageView)
        }

        fun setTint(color: Int) {
            imageView.imageTintList = ColorStateList.valueOf(color)
        }

        fun setImage(image: Drawable) {
            imageView.setImageDrawable(image)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            val height = measuredHeight
            var width = MeasureSpec.getSize(widthMeasureSpec)
            mItems.let { mItems ->
                if (width != 0 && mItems.size > 0) {
                    width /= mItems.size
                    setMeasuredDimension(width, height)
                }
            }
        }
    }
}

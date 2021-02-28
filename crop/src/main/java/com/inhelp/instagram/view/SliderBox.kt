package com.inhelp.instagram.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.AsyncTask
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Scroller
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.inhelp.instagram.R
import com.inhelp.extension.dp
import com.inhelp.extension.getColorFromAttr
import com.inhelp.extension.setPaddingLeft
import com.inhelp.extension.setPaddingRight
import java.util.concurrent.TimeUnit

data class SliderBoxElement(val title: String,
                            val id: Int)

class SliderBox @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    private val TEXT_SIZE = 14f
    private val SELECTED_TAB = 1

    private lateinit var mItems: List<SliderBoxElement>
    private val mContainer: LinearLayout
    private var mSelected = -1
    private var mLastSelected = -1
    private var mFingerSlider: FingerSlider
    private var onSelectListener: OnSelectListener? = null

    private var mUnselectedTextColor: Int = 0
    private var mSelectedTextColor: Int = 0
    private var mTextSize: Float = 0f
    private var mActiveTabDrawable: Int = 0


    init {
        initAttrs(context, attrs)
        setPadding(0, 0, 0, 0)
        mContainer = LinearLayout(context)
        mContainer.orientation = LinearLayout.HORIZONTAL

        addView(mContainer, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        mFingerSlider = FingerSlider(context)

        addView(mFingerSlider, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliderBox)
        mTextSize = typedArray.getDimension(R.styleable.SliderBox_sbTextSize, TEXT_SIZE)
        mSelectedTextColor = typedArray.getResourceId(R.styleable.SliderBox_sbSelectedTextColor, context.getColorFromAttr(COLOR_FROM_ATTR))
        mUnselectedTextColor = typedArray.getResourceId(R.styleable.SliderBox_sbUnselectedTextColor, context.getColorFromAttr(COLOR_FROM_ATTR))
        mActiveTabDrawable = typedArray.getResourceId(R.styleable.SliderBox_sbActiveTabDrawable, SELECTED_TAB)
        typedArray.recycle()
    }

    fun setSelected(selected: Int) {
        mItems.let { mItems ->
            if (mItems.size > selected) {
                this.mSelected = selected
                if (mLastSelected == -1)
                    this.mLastSelected = selected
                mFingerSlider.setText(mItems[selected].title)
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
        for ((title, id) in items) {
            val tv = TextView(context)
            tv.text = title
            tv.id = id
            tv.isClickable = true
            tv.setTextColor(ContextCompat.getColor(context, mUnselectedTextColor))
            tv.gravity = Gravity.CENTER
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
            tv.ellipsize = TextUtils.TruncateAt.END
            tv.isSingleLine = true
            tv.setEms(3)
            tv.setTypeface(tv.typeface, Typeface.BOLD)
            tv.isAllCaps = true
            tv.marqueeRepeatLimit = -1
            val lp = LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT)
            lp.weight = 1f
            tv.layoutParams = lp
            tv.setPaddingLeft(2.dp.toInt())
            tv.setPaddingRight(2.dp.toInt())
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
        @Volatile
        private var wasDeatached = false
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

        fun setText(text: String) {
            tv.setText(text)
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

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            wasDeatached = true
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
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

        inner class EndOfScrollDetector : AsyncTask<Unit, Unit, Unit>() {

            override fun doInBackground(vararg params: Unit?) {
                while (scrollX * -1 != mSelected * elementSize && mSelected != mLastSelected && !wasDeatached) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(5)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onPostExecute(result: Unit) {
                super.onPostExecute(result)

                if (onSelectListener != null && mSelected != mLastSelected && !wasDeatached)
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
        private val textView: TextView = TextView(context)

        init {
            textView.gravity = Gravity.CENTER
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
            textView.setTextColor(ContextCompat.getColor(context, mSelectedTextColor))
            textView.ellipsize = TextUtils.TruncateAt.END
            textView.setEms(5)
            textView.setTypeface(textView.typeface, Typeface.BOLD)
            textView.isSingleLine = true
            textView.marqueeRepeatLimit = -1
            val lp = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.gravity = Gravity.CENTER
            textView.layoutParams = lp
            textView.isAllCaps = true
            addView(textView)
        }

        fun setText(text: String) {
            textView.text = text
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

    companion object {
        private const val COLOR_FROM_ATTR = Color.WHITE
        private const val TEXT_SIZE = 14f
        private const val SELECTED_TAB = 1
    }

}

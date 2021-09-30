package ua.com.compose.image_filter.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSeekBar

/**
 * Created by HeWhoWas on 5/10/13.
 */
class NegativeSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatSeekBar(context, attrs, defStyleAttr) {

    var minimumValue = 0
    var maximumValue = 0
    var listener: OnSeekBarChangeListener? = null

    init {
        setUpInternalListener()
    }

    override fun setMin(min: Int) {
        minimumValue = min
        super.setMax(maximumValue - minimumValue)
    }

    override fun setMax(max: Int) {
        maximumValue = max
        super.setMax(maximumValue - minimumValue)
    }

    override fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        this.listener = listener
    }

    private fun setUpInternalListener() {
        super.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (listener != null) {
                    listener!!.onProgressChanged(seekBar, minimumValue + i, b)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                if (listener != null) listener!!.onStartTrackingTouch(seekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (listener != null) listener!!.onStopTrackingTouch(seekBar)
            }
        })
    }
}
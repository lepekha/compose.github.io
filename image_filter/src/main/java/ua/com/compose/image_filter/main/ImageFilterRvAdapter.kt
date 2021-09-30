package ua.com.compose.image_filter.main

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import kotlinx.android.synthetic.main.module_image_filter_element_sneekbar.view.*
import ua.com.compose.extension.changeTextAnimate
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.extension.toPercentString
import ua.com.compose.image_filter.R
import ua.com.compose.image_filter.custom.NegativeSeekBar
import ua.com.compose.image_filter.data.FilterParam
import ua.com.compose.image_filter.data.ImageFilter
import androidx.annotation.NonNull





class ImageFilterRvAdapter(private val params: List<FilterParam>, private val onChange: () -> Unit) : RecyclerView.Adapter<ImageFilterRvAdapter.ViewHolder>() {

    override fun getItemCount() = params.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_image_filter_element_sneekbar, parent, false)).apply {

            progress.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
                params[adapterPosition].percent = value
                onChange.invoke()
                txtSize.text = value.toInt().toPercentString()
                txtSize.setTextColor(txtSize.context.getColorFromAttr(R.attr.color_6))
                txtSize.removeCallbacks(runnable)
                txtSize.postDelayed(runnable, 1000)
            })

        //            progress.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
//                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//                    params[adapterPosition].percent = progress
//                    onChange.invoke()
//                    txtSize.text = progress.toPercentString()
//                    txtSize.setTextColor(txtSize.context.getColorFromAttr(R.attr.color_6))
//                }
//
//                override fun onStartTrackingTouch(seekBar: SeekBar) {
//                    txtSize.removeCallbacks(null)
//                }
//
//                override fun onStopTrackingTouch(seekBar: SeekBar) {
//                    txtSize.postDelayed(runnable, 1000)
//                }
//            })
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtSize.text = holder.txtSize.context.getString(params[position].nameResId)
        holder.txtSize.contentDescription = holder.txtSize.context.getString(params[position].nameResId)
        holder.progress.valueFrom = params[position].minPercent()
        holder.progress.valueTo = params[position].maxPercent()
        holder.progress.value = params[position].defaultPercent()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val progress: Slider = view.sbSize
        val txtSize: TextView = view.txtSize

        val runnable = Runnable {
            txtSize?.changeTextAnimate(text = txtSize.contentDescription.toString())
            txtSize?.setTextColor(txtSize.context.getColorFromAttr(R.attr.color_5))
        }
    }
}
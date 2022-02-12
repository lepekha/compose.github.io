package ua.com.compose.image_filter.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import kotlinx.android.synthetic.main.module_image_filter_element_sneekbar.view.*
import ua.com.compose.image_filter.R
import ua.com.compose.image_filter.data.FilterParam
import ua.com.compose.extension.*
import ua.com.compose.image_filter.data.FilterValueParam


class ImageFilterRvAdapter(private val params: List<FilterParam>, private val onChange: () -> Unit) : RecyclerView.Adapter<ImageFilterRvAdapter.ViewHolder>() {

    override fun getItemCount() = params.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_image_filter_element_sneekbar, parent, false)).apply {

            progress.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
                (params[adapterPosition] as FilterValueParam).value = value
                onChange.invoke()
//                txtSize.context.vibrate(EVibrate.SLIDER)
                txtSize.text = if(value > 0f) { "+${value.toInt()}" } else { value.toInt().toString() }
                txtSize.setTextColor(txtSize.context.getColorFromAttr(R.attr.color_14))
                txtSize.removeCallbacks(runnable)
                txtSize.postDelayed(runnable, 700)
            })
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filter = params[position] as FilterValueParam
        holder.progress.valueFrom = filter.minPercent()
        holder.progress.valueTo = filter.maxPercent()
        holder.progress.stepSize = filter.step
        holder.txtSize.contentDescription = holder.txtSize.context.getString(filter.nameResId)
        holder.txtSize.text = holder.txtSize.contentDescription
        holder.progress.value = filter.defaultPercent()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val progress: Slider = view.sbSize
        val txtSize: TextView = view.txtSize

        val runnable = Runnable {
            txtSize.changeTextAnimate(text = txtSize.contentDescription.toString())
            txtSize.setTextColor(txtSize.context.getColorFromAttr(R.attr.color_9))
        }
    }
}
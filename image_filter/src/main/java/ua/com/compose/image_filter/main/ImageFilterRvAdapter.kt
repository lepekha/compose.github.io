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
                txtSize.context.vibrate(EVibrate.SLIDER)
                val formattedValue = if(value > 0f) { "+${value.toInt()}" } else { value.toInt().toString() }
                txtSize.text = buildString {
                    append(txtSize.contentDescription)
                    append(" ")
                    append(formattedValue)
                }

                txtSize.setColorOfSubstring(formattedValue, txtSize.context.getColorFromAttr(R.attr.color_14))
            })
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filter = params[position] as FilterValueParam
        holder.progress.valueFrom = filter.minPercent()
        holder.progress.valueTo = filter.maxPercent()
        holder.progress.value = filter.defaultPercent()
        holder.progress.stepSize = filter.step
        holder.txtSize.contentDescription = holder.txtSize.context.getString(filter.nameResId)
        holder.txtSize.text = buildString {
            append(holder.txtSize.contentDescription)
            append(" ")
            append(holder.progress.value.toInt())
        }
        holder.txtSize.setColorOfSubstring(holder.progress.value.toInt().toString(), holder.txtSize.context.getColorFromAttr(R.attr.color_14))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val progress: Slider = view.sbSize
        val txtSize: TextView = view.txtSize
    }
}
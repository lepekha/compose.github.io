package ua.com.compose.image_filter.main

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import kotlinx.android.synthetic.main.module_image_filter_element_history.view.*
import ua.com.compose.extension.*
import ua.com.compose.image_filter.R
import ua.com.compose.image_filter.data.ImageFilter


class ImageFilterHistoryRvAdapter(val context: Context, val image: Bitmap, val filters: List<ImageFilter>, val onImagePress: (position: Int) -> Unit) : RecyclerView.Adapter<ImageFilterHistoryRvAdapter.ViewHolder>() {

    private val gpuFilter = GPUImage(context).apply {
        this.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        this.setBackgroundColor(27 / 255f,27 / 255f,31 / 255f)
        this.setImage(image.resizeImage(100.dp, false))
    }

    private fun copyAllFilters(historyFilters: List<ImageFilter>): List<GPUImageFilter> {
        val filters = mutableListOf<GPUImageFilter>()
        historyFilters.map { it.copy() }.forEach {
            filters.add(it.filter)
        }
        return filters
    }

    override fun getItemCount(): Int = filters.size

    private var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_image_filter_element_history, parent, false)).apply {
            this.imgView.setVibrate(EVibrate.BUTTON)
            this.imgView.setOnClickListener {
                currentPosition = adapterPosition
                onImagePress(adapterPosition)
                notifyItemRangeChanged(0, itemCount, Any())
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        gpuFilter.setFilter(GPUImageFilterGroup(copyAllFilters(filters.take(filters.size - position))))
        Glide.with(context).load(gpuFilter.bitmapWithFilterApplied).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.imgView)
        holder.txtTitle.text = holder.txtTitle.context.getString(filters.reversed()[position].nameResId)
        changeTextColor(holder, position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if(payloads.isNotEmpty()) {
            changeTextColor(holder, position)
        }else {
            super.onBindViewHolder(holder,position, payloads);
        }
    }

    private fun changeTextColor(holder: ViewHolder, position: Int){
        if(position == currentPosition){
            holder.txtTitle.setTextColor(holder.txtTitle.context.getColorFromAttr(R.attr.color_14))
        }else{
            holder.txtTitle.setTextColor(holder.txtTitle.context.getColorFromAttr(R.attr.color_9))
        }

        if(position < currentPosition){
            holder.imgView.alpha = 0.4f
            holder.txtTitle.alpha = 0.4f
        }else{
            holder.imgView.alpha = 1f
            holder.txtTitle.alpha = 1f
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.imgIcon
        val txtTitle: TextView = view.txtTitle
    }
}

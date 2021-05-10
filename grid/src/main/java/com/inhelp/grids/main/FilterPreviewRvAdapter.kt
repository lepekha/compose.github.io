package com.inhelp.grids.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate
import com.inhelp.grids.R
import com.inhelp.grids.data.FilterType
import com.inhelp.grids.data.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.android.synthetic.main.element_filter_preview.view.*


class FilterPreviewRvAdapter(val context: Context, val filters: List<FilterType>, val image: Bitmap, val onImagePress: (value: FilterType) -> Unit) : RecyclerView.Adapter<FilterPreviewRvAdapter.ViewHolder>() {

    private val gpuImg by lazy {  GPUImage(context.applicationContext) }

    private fun getBitmapWithFilterApplied(filter: GPUImageFilter): Bitmap {
        gpuImg.setFilter(filter)
        return gpuImg.getBitmapWithFilterApplied(image)
    }

    override fun getItemCount(): Int {
        return filters.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_filter_preview, parent, false)).apply {
            this.imgView.setOnClickListener {
                this.imgView.context.vibrate(type = EVibrate.BUTTON)
                onImagePress(filters[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgView.setImageBitmap(getBitmapWithFilterApplied(GPUImageFilterTools.createFilterForType(context = holder.view.context, type = filters[position])))
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.imgView
    }
}

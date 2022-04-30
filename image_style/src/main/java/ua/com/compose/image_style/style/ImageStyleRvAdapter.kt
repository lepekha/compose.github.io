package ua.com.compose.image_style.style

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.card.MaterialCardView
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import kotlinx.android.synthetic.main.module_image_style_element_style.view.*
import ua.com.compose.extension.*
import ua.com.compose.image_style.R
import ua.com.compose.image_filter.data.Style


class ImageStyleRvAdapter(val context: Context,
                          val image: Bitmap?,
                          val styles: List<Style>,
                          val onImagePress: (position: Int) -> Unit,
                          val onRemovePress: (position: Int) -> Unit,
                          val onQRPress: (position: Int) -> Unit) : RecyclerView.Adapter<ImageStyleRvAdapter.ViewHolder>() {

    private var currentPosition = 0

    private val gpuFilter = GPUImage(context).apply {
        this.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        this.setBackgroundColor(27 / 255f,27 / 255f,31 / 255f)
        this.setImage(image)
    }

    override fun getItemCount(): Int = styles.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_image_style_element_style, parent, false)).apply {
            this.container.setOnClickListener {
                currentPosition = adapterPosition
                onImagePress(adapterPosition)
                notifyItemRangeChanged(0, itemCount, Any())
            }

            this.btnRemove.setVibrate(EVibrate.BUTTON)
            this.btnRemove.setOnClickListener {
                onRemovePress(adapterPosition)
            }

            this.btnQR.setVibrate(EVibrate.BUTTON)
            this.btnQR.setOnClickListener {
                onQRPress(adapterPosition)
            }
        }
    }

    fun remove(position: Int) {
        currentPosition = 0
        notifyItemRemoved(position)
        notifyItemChanged(0)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if(payloads.isNotEmpty()) {
            changeTextColor(holder, position)
        }else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    private fun changeTextColor(holder: ViewHolder, position: Int) {
        if(position == currentPosition){
            holder.btnRoot.isVisible = true
            holder.btnRemove.isVisible = !styles[position].app
            holder.txtTitle.setTextColor(holder.txtTitle.context.getColorFromAttr(R.attr.color_14))
        }else{
            holder.btnRoot.isVisible = false
            holder.txtTitle.setTextColor(holder.txtTitle.context.getColorFromAttr(R.attr.color_9))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        gpuFilter.setFilter(GPUImageFilterGroup(styles[position].getFilters().map { it.filter }))
        Glide.with(context).load(gpuFilter.bitmapWithFilterApplied).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.imgView)
        holder.txtTitle.text = styles[position].name
        changeTextColor(holder, position)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val container: ConstraintLayout = view.container
        val imgView: ImageView = view.imgImage
        val txtTitle: TextView = view.txtTitle
        val btnRoot: ConstraintLayout = view.btnRoot
        val btnRemove: MaterialCardView = view.btnRemove
        val btnQR: MaterialCardView = view.btnQR
    }
}

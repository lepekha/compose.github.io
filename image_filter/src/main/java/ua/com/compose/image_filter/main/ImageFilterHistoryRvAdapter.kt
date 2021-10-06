package ua.com.compose.image_filter.main

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.extension.EVibrate
import kotlinx.android.synthetic.main.module_image_filter_element_history.view.*
import ua.com.compose.extension.setVibrate
import ua.com.compose.image_filter.R
import ua.com.compose.image_filter.data.ImageFilter


class ImageFilterHistoryRvAdapter(val images: List<Bitmap>, val filters: List<ImageFilter>, val onImagePress: (value: ImageFilter) -> Unit) : RecyclerView.Adapter<ImageFilterHistoryRvAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return images.size
    }

    private var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_image_filter_element_history, parent, false)).apply {
            this.imgView.setVibrate(EVibrate.BUTTON)
            this.imgView.setOnClickListener {
                currentPosition = adapterPosition
                onImagePress(filters[adapterPosition])
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgView.setImageBitmap(images[position])
        holder.txtTitle.text = holder.txtTitle.context.getString(filters[position].nameResId)

        if(position < currentPosition){
            holder.imgView.alpha = 0.3f
            holder.txtTitle.alpha = 0.3f
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

package com.inhelp.instagram.grid.view.save

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inhelp.extension.getColorFromAttr
import com.inhelp.instagram.R
import kotlinx.android.synthetic.main.element_instagram_grid_image.view.*


class GridRvAdapter(val images: MutableList<Bitmap>, val onPress: (position: Int) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_instagram_grid_image, parent, false)).apply {
            this.btnShare.setOnClickListener {
                btnShare.backgroundTintList = ColorStateList.valueOf(parent.context.getColorFromAttr(R.attr.color_5))
                onPress(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.btnShare.text = (images.size - position).toString()
        Glide.with(holder.imgView.context).load(images[position]).centerInside().thumbnail(0.1f).into(holder.imgView)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imgView: ImageView = view.imgView
    val btnShare: Button = view.btnShare
}

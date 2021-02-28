package com.inhelp.instagram.view.grid

import android.graphics.Bitmap
import android.graphics.Point
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inhelp.extension.dp
import com.inhelp.instagram.R
import kotlinx.android.synthetic.main.element_grid_image.view.*


class GridRvAdapter(val images: MutableList<Bitmap>, val onImagePress: (value: Bitmap) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_grid_image, parent, false)).apply {
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imgView.context).load(images[position]).centerInside().thumbnail(0.1f).into(holder.imgView)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imgView: ImageView = view.imgView
}

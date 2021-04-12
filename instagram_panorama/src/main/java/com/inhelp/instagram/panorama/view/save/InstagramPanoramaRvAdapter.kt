package com.inhelp.instagram.panorama.view.save

import android.graphics.Bitmap
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inhelp.instagram.R
import kotlinx.android.synthetic.main.element_instagram_panorama_image.view.*


internal class PanoramaRvAdapter(private val images: MutableList<Bitmap>) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_instagram_panorama_image, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imgView.context).load(images[position]).centerInside().thumbnail(0.1f).into(holder.imgView)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imgView: ImageView = view.imgImage
}

package com.inhelp.gallery.main.content

import android.graphics.Point
import android.net.Uri
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.inhelp.extension.EVibrate
import com.inhelp.extension.dp
import com.inhelp.extension.vibrate
import com.inhelp.gallery.R
import kotlinx.android.synthetic.main.element_images.view.*


class GalleryRvAdapter(val images: MutableList<Uri>, val onImagePress: (value: Uri) -> Unit) : RecyclerView.Adapter<GalleryRvAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_images, parent, false)).apply {
            this.imgView.setOnClickListener {
                this.imgView.context.vibrate(type = EVibrate.BUTTON)
                onImagePress(images[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imgView.context).load(images[position]).centerInside().thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(holder.imgView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.imgView
    }
}

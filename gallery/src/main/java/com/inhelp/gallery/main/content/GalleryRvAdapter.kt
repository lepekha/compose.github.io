package com.inhelp.gallery.main.content

import android.graphics.Point
import android.net.Uri
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.inhelp.extension.dp
import com.inhelp.gallery.R
import kotlinx.android.synthetic.main.element_images.view.*


class GalleryRvAdapter(val images: MutableList<Uri>, windowManager: WindowManager, val onImagePress: (value: Uri) -> Unit) : RecyclerView.Adapter<ViewHolder>() {


    private val size by lazy {
        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        (size.x - 55.dp.toInt()) / 3
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_images, parent, false)).apply {
            val params = this.imgView.layoutParams
            params.height = size
            this.imgView.layoutParams = params

            this.imgView.setOnClickListener {
                onImagePress(images[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imgView.context).load(images[position]).centerInside().thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(holder.imgView)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imgView: ImageView = view.imgView
}

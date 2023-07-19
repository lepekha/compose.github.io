package ua.com.compose.gallery.main

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
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.setVibrate
import ua.com.compose.gallery.R


class GalleryFoldersRvAdapter(private val folders: List<ImageFolder>, val onPress: (value: ImageFolder) -> Unit) : RecyclerView.Adapter<GalleryFoldersRvAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return folders.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_gallery_element_gallery_folder, parent, false)).apply {
            this.container.setVibrate(EVibrate.BUTTON)
            this.container.setOnClickListener {
                onPress(folders[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]
        holder.txtName.text = folder.name
        holder.txtCount.text = folder.images.count().toString()
        holder.imgPhoto.isVisible = folder.images.isNotEmpty()
        folder.images.firstOrNull()?.let {
            Glide.with(holder.imgPhoto.context).load(it).centerInside().thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imgPhoto)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: ConstraintLayout = view.findViewById(R.id.container)
        val imgPhoto: ImageView = view.findViewById(R.id.imgPhoto)
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtCount: TextView = view.findViewById(R.id.txtCount)
    }
}

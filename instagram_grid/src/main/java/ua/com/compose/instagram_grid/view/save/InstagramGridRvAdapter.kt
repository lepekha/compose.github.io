package ua.com.compose.instagram_grid.view.save

import android.graphics.Bitmap
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.setVibrate
import kotlinx.android.synthetic.main.module_instagram_grid_element_instagram_grid_image.view.*
import ua.com.compose.instagram_grid.R


class GridRvAdapter(val images: MutableList<Bitmap>, val onPress: (position: Int) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_instagram_grid_element_instagram_grid_image, parent, false)).apply {
            this.imgView.setVibrate(EVibrate.BUTTON)
            this.imgView.setOnClickListener {
                btnShare.alpha = 0.3f
                this.imgView.setVibrate(EVibrate.NONE)
                this.imgView.isClickable = false
                onPress(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.btnShare.text = (images.size - position).toString()
        Glide.with(holder.imgView.context).load(images[position]).centerInside().diskCacheStrategy(DiskCacheStrategy.NONE).thumbnail(0.1f).into(holder.imgView)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imgView: ImageView = view.imgView
    val btnShare: TextView = view.btnShare
}

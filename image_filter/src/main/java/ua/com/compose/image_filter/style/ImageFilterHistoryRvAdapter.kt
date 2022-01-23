package ua.com.compose.image_filter.style

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ua.com.compose.extension.EVibrate
import kotlinx.android.synthetic.main.module_image_filter_element_history.view.*
import kotlinx.android.synthetic.main.module_image_filter_fragment_style.*
import ua.com.compose.extension.setVibrate
import ua.com.compose.image_filter.R
import ua.com.compose.image_filter.data.ImageFilter


class ImageFilterHistoryRvAdapter(val images: List<Bitmap>, val onImagePress: (value: ImageFilter) -> Unit) : RecyclerView.Adapter<ImageFilterHistoryRvAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_image_filter_element_history, parent, false)).apply {
            this.imgView.setVibrate(EVibrate.BUTTON)
            this.imgView.setOnClickListener {
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Glide.with(holder.imgView.context.applicationContext).load(images[position]).diskCacheStrategy(
//            DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.imgView)
//        holder.txtTitle.text = holder.txtTitle.context.getString(filters[position].nameResId)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imgView = view.imgIcon
        val txtTitle: TextView = view.txtTitle
    }
}

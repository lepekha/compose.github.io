package ua.com.compose.image_filter.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.module_image_filter_element_menu.view.*
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.setVibrate
import ua.com.compose.image_filter.R
import ua.com.compose.image_filter.data.ImageFilter


class ImageFilterMenuRvAdapter(private val filters: List<ImageFilter>, private val onPress: (filter: ImageFilter) -> Unit) : RecyclerView.Adapter<ImageFilterMenuRvAdapter.ViewHolder>() {

    override fun getItemCount() = filters.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.module_image_filter_element_menu, parent, false)).apply {
            this.container.setVibrate(EVibrate.BUTTON)
            this.container.setOnClickListener {
                onPress(filters[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        filters[position].let {
            holder.txtTitle.text = holder.txtTitle.context.getString(it.nameResId)
            holder.icon.setImageResource(it.iconResId)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.container
        val root: FrameLayout = view.root
        val txtTitle: TextView = view.txtTitle
        val icon: ImageView = view.icon
    }
}
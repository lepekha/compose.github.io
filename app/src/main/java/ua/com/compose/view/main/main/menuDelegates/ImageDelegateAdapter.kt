package ua.com.compose.view.main.main.menuDelegates

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.R
import ua.com.compose.mvp.adapters.ViewTypeDelegateAdapter
import ua.com.compose.core.models.data.DynamicMenu
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.setVibrate
import kotlinx.android.synthetic.main.element_menu_image.view.*

class ImageDelegateAdapter(val onPress: () -> Unit): ViewTypeDelegateAdapter {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent).apply {
            this.root.setOnClickListener {
                innerItem.onPress()
                onPress()
            }
            this.root.setVibrate(type = EVibrate.BUTTON)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.Image)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, payloads: MutableList<Any>) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.Image)
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.element_menu_image, parent, false)) {

        lateinit var innerItem: DynamicMenu.Image
        val root = itemView.root
        val container = itemView.container
        val txtTitle = itemView.txtTitle
        val imgIcon = itemView.imgIcon

        fun bind(item: DynamicMenu.Image) {
            innerItem = item
            txtTitle.setText(item.titleResId)
            imgIcon.setImageResource(item.backgroundImageId)
        }
    }
}
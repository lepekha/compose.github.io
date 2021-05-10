package ua.com.compose.view.main.main.menuDelegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ua.com.compose.R
import ua.com.compose.mvp.adapters.ViewTypeDelegateAdapter
import ua.com.compose.core.models.data.DynamicMenu
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.setVibrate
import kotlinx.android.synthetic.main.element_menu_long.view.*

class LongDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent).apply {
            this.itemView.layoutParams = (this.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).apply {
                this.isFullSpan = true
            }

            this.root.setOnClickListener {
                innerItem.onPress()
            }
            this.root.setVibrate(type = EVibrate.BUTTON)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.Long)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, payloads: MutableList<Any>) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.Long)
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.element_menu_long, parent, false)) {

        lateinit var innerItem: DynamicMenu.Long
        val root = itemView.root
        val container = itemView.container
        val txtTitle = itemView.txtTitle
        val imgIcon = itemView.imgIcon

        fun bind(item: DynamicMenu.Long) {
            innerItem = item

            txtTitle.setText(item.titleResId)
            imgIcon.setImageResource(item.backgroundImageId)
        }
    }
}
package ua.com.compose.view.main.main.menuDelegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.R
import ua.com.compose.mvp.adapters.ViewTypeDelegateAdapter
import ua.com.compose.core.models.data.DynamicMenu
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.setVibrate
import kotlinx.android.synthetic.main.element_menu_icon.view.*

class IconDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent).apply {
            this.root.setOnClickListener {
                innerItem.onPress()
            }
            this.root.setVibrate(type = EVibrate.BUTTON)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.Icon)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, payloads: MutableList<Any>) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.Icon)
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.element_menu_icon, parent, false)) {

        lateinit var innerItem: DynamicMenu.Icon
        val root = itemView.root
        val icon = itemView.imgIcon
        val txtTitle = itemView.txtTitle

        fun bind(item: DynamicMenu.Icon) {
            innerItem = item
            icon.setImageResource(item.iconResId)
            txtTitle.setText(item.titleResId)
//            root.setCardBackgroundColor(ColorUtils.setAlphaComponent(root.context.getColorFromAttr(R.attr.color_2), 125))
//            root.setCardForegroundColor(ColorStateList.valueOf(ColorUtils.setAlphaComponent(root.context.getColorFromAttr(R.attr.color_2), 125)))
        }
    }
}
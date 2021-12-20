package ua.com.compose.view.main

import android.content.res.ColorStateList
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ua.com.compose.R
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.extension.setVibrate
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import kotlinx.android.synthetic.main.element_bottom_menu_icon.view.*


class BottomMenuRvAdapter(var menu: MutableList<Menu>) : RecyclerView.Adapter<BottomMenuRvAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return menu.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_bottom_menu_icon, parent, false)).apply {
            this.btnMenu.setVibrate(type = EVibrate.BUTTON)
            this.btnMenu.setOnClickListener {
                (menu[adapterPosition] as? BottomMenu)?.onPress?.invoke()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = menu[position] as BottomMenu
        holder.btnMenu.isClickable = item.isEnabled
        holder.btnIcon.setImageResource(item.iconResId)
        val iconTintRes = item.iconTintRes ?: R.attr.color_9
        holder.btnIcon.setImageResource(item.iconResId)
        holder.btnIcon.setColorFilter(holder.btnMenu.context.getColorFromAttr(iconTintRes))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnMenu: FrameLayout = view.btnMenu
        val btnIcon: ImageView = view.btnIcon
    }
}

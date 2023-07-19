package ua.com.compose

import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.getColorFromAttr
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.extension.vibrate
import ua.com.compose.mvp.data.TextMenu


class MenuRvAdapter(var menu: MutableList<Menu>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ICON = 0
    private val VIEW_TYPE_TEXT = 1

    override fun getItemCount(): Int {
        return menu.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(menu[position] is BottomMenu) {
            VIEW_TYPE_ICON
        } else {
            VIEW_TYPE_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == VIEW_TYPE_ICON) {
            return ButtonViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_bottom_menu_icon, parent, false)).apply {
                this.btnMenu.setOnClickListener {
                    this.btnMenu.context.vibrate(EVibrate.BUTTON)
                    (menu[adapterPosition] as? BottomMenu)?.onPress?.invoke()
                }
            }
        } else {
            return TextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_bottom_menu_text, parent, false)).apply {
                this.btnMenu.setOnClickListener {
                    this.btnMenu.context.vibrate(EVibrate.BUTTON)
                    (menu[adapterPosition] as? TextMenu)?.onPress?.invoke()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = menu[position]
        when {
            item is BottomMenu -> {
                (holder as ButtonViewHolder).apply {
                    holder.btnMenu.isClickable = item.isEnabled
                    holder.btnIcon.setImageResource(item.iconResId)
                    val color = item.color ?: holder.btnMenu.context.getColor(R.color.color_main_menu_content)
                    holder.btnIcon.setImageResource(item.iconResId)
                    holder.btnIcon.setColorFilter(color)
                }
            }
            item is TextMenu -> {
                (holder as TextViewHolder).apply {
                    holder.btnMenu.isClickable = item.isEnabled
                    holder.title.text = item.text
                    val color = item.color ?: holder.btnMenu.context.getColorFromAttr(R.attr.color_9)
                    holder.title.setTextColor(color)
                }
            }
        }
    }

    class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnMenu: FrameLayout = view.findViewById(R.id.btnMenu)
        val btnIcon: ImageView = view.findViewById(R.id.btnIcon)
    }

    class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnMenu: FrameLayout = view.findViewById(R.id.btnMenu)
        val title: TextView = view.findViewById(R.id.txtTitle)
    }
}

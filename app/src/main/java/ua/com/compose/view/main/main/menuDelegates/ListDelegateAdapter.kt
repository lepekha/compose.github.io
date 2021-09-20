package ua.com.compose.view.main.main.menuDelegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ua.com.compose.R
import ua.com.compose.mvp.adapters.ViewTypeDelegateAdapter
import ua.com.compose.core.models.data.DynamicMenu
import ua.com.compose.view.main.main.MenuRvAdapter
import kotlinx.android.synthetic.main.element_menu_list.view.*

class ListDelegateAdapter(private val onPress: () -> Unit): ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent).apply {
            this.itemView.layoutParams = (this.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).apply {
                this.isFullSpan = true
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.List)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, payloads: MutableList<Any>) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.List)
    }

    inner class ViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.element_menu_list, parent, false)) {

        lateinit var innerItem: DynamicMenu.List
        val root = itemView.root
        val txtTitle = itemView.txtTitle
        val list = itemView.innerList

        fun bind(item: DynamicMenu.List) {
            innerItem = item
            txtTitle.setText(item.titleResId)
            list.layoutManager = LinearLayoutManager(parent.context, RecyclerView.HORIZONTAL, false)
            list.adapter = MenuRvAdapter(items = item.innerMenu.filter { it.isVisible.invoke() }, onPress = onPress)
        }
    }
}
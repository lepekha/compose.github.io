package com.inhelp.view.main.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inhelp.R
import com.inhelp.core.models.data.Menu
import kotlinx.android.synthetic.main.element_menu.view.*


class MenuRvAdapter(private val menuList: MutableList<Menu>, val clickListener: (menuItem: Menu) -> Unit = {},val clickView: (view: View) -> Unit = {} ) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.element_menu, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.setText(menuList[position].titleResId)
        holder.imgIcon.setImageResource(menuList[position].iconResId)
//        holder.container.setBackgroundResource(menuList[position].backgroundResId)

        holder.container.setOnClickListener {
            clickView(it)
            clickListener(menuList[position])
        }
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val container = view.container
    val txtTitle = view.txtTitle
    val imgIcon = view.imgIcon
}

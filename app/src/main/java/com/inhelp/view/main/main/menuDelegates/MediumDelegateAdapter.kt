package com.inhelp.view.main.main.menuDelegates

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inhelp.R
import com.inhelp.base.mvp.adapters.ViewTypeDelegateAdapter
import com.inhelp.core.models.data.DynamicMenu
import com.inhelp.extension.EVibrate
import com.inhelp.extension.setVibrate
import com.inhelp.extension.vibrate
import kotlinx.android.synthetic.main.element_menu_medium.view.*

class MediumDelegateAdapter : ViewTypeDelegateAdapter {

    @SuppressLint("ClickableViewAccessibility")
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
        holder.bind(item as DynamicMenu.Medium)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, payloads: MutableList<Any>) {
        holder as ViewHolder
        holder.bind(item as DynamicMenu.Medium)
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.element_menu_medium, parent, false)) {

        lateinit var innerItem: DynamicMenu.Medium
        val root = itemView.root
        val container = itemView.container
        val txtTitle = itemView.txtTitle
        val imgIcon = itemView.imgIcon

        fun bind(item: DynamicMenu.Medium) {
            innerItem = item
            txtTitle.setText(item.titleResId)
            imgIcon.setImageResource(item.backgroundImageId)
        }
    }
}
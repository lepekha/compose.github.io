/*
 * Copyright TraderEvolution Global LTD. В© 2017-2021. All rights reserved.
 */

package ua.com.compose.view.main.main

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import ua.com.compose.mvp.adapters.ViewTypeDelegateAdapter
import ua.com.compose.core.models.data.DynamicMenu
import ua.com.compose.view.main.main.menuDelegates.*

class MenuRvAdapter(private val items: List<DynamicMenu>, private val onPress: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_ICON = 5
        const val VIEW_TYPE_GRID = 6
    }

    private var mDelegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    init {
        mDelegateAdapters.put(VIEW_TYPE_IMAGE, ImageDelegateAdapter(onPress = onPress))
        mDelegateAdapters.put(VIEW_TYPE_ICON, IconDelegateAdapter())
        mDelegateAdapters.put(VIEW_TYPE_GRID, GridDelegateAdapter(onPress = onPress))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            mDelegateAdapters.get(viewType)!!.onCreateViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mDelegateAdapters.get(getItemViewType(position))?.onBindViewHolder(holder, items[position])
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            mDelegateAdapters.get(getItemViewType(position))?.onBindViewHolder(holder, items[position], payloads)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun getItemViewType(position: Int) = when (items[position]) {
        is DynamicMenu.Image -> VIEW_TYPE_IMAGE
        is DynamicMenu.Icon -> VIEW_TYPE_ICON
        is DynamicMenu.Grid -> VIEW_TYPE_GRID
        else -> -1
    }

    override fun getItemId(position: Int): Long {
        return items[position].hashCode().toLong()
    }
}
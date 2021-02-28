/*
 * Copyright TraderEvolution Global LTD. В© 2017-2021. All rights reserved.
 */

package com.inhelp.view.main.main

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.inhelp.R
import com.inhelp.base.mvp.adapters.ViewTypeDelegateAdapter
import com.inhelp.core.models.data.DynamicMenu
import com.inhelp.view.main.main.menuDelegates.LongDelegateAdapter
import com.inhelp.view.main.main.menuDelegates.MediumDelegateAdapter
import com.inhelp.view.main.main.menuDelegates.ShortDelegateAdapter
import com.inhelp.view.main.main.menuDelegates.TextDelegateAdapter

class MenuRvAdapter(private val items: List<DynamicMenu>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SHORT = 0
        const val VIEW_TYPE_MEDIUM = 1
        const val VIEW_TYPE_LONG = 2
        const val VIEW_TYPE_TEXT = 3
    }

    private var mDelegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    init {
        mDelegateAdapters.put(VIEW_TYPE_SHORT, ShortDelegateAdapter())
        mDelegateAdapters.put(VIEW_TYPE_MEDIUM, MediumDelegateAdapter())
        mDelegateAdapters.put(VIEW_TYPE_LONG, LongDelegateAdapter())
        mDelegateAdapters.put(VIEW_TYPE_TEXT, TextDelegateAdapter())
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
        is DynamicMenu.Short -> VIEW_TYPE_SHORT
        is DynamicMenu.Medium -> VIEW_TYPE_MEDIUM
        is DynamicMenu.Long -> VIEW_TYPE_LONG
        is DynamicMenu.Text -> VIEW_TYPE_TEXT
        else -> -1
    }

    override fun getItemId(position: Int): Long {
        return items[position].hashCode().toLong()
    }
}
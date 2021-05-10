/*
 * Copyright TraderEvolution Global LTD. В© 2017-2021. All rights reserved.
 */

package ua.com.compose.mvp.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface ViewTypeDelegateAdapter {
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any)

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, payloads: MutableList<Any>)

    fun onUnbindItemFromList(item: Any) {}
}
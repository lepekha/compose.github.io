package com.inhelp.dialogs.main

import android.content.Context
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.extension.count
import com.inhelp.dialogs.R


class PresenterTags(val context: Context) : BaseMvpPresenterImpl<ViewTags>() {

    val tags = mutableListOf<String>()
    val filterText = StringBuffer()
    private val originTags = context.resources.getStringArray(R.array.fragment_tags).toMutableList()

    override fun onBackPress() {}

    override fun attachView(view: ViewTags) {
        super.attachView(view)
        tags.clear()
        filterText.setLength(0)
        tags.addAll(originTags)
        view.setVisibleClearText(isVisible = false)
        view.setVisiblePlaceholder(isVisible = tags.isEmpty())
    }

    fun onEnterText(text: String){
        tags.clear()
        filterText.setLength(0)
        filterText.append(text)
        if(text.isNotEmpty() and text.isNotBlank()){
            tags.addAll(originTags.filter { it.contains(text, ignoreCase = true) }.sortedByDescending { it.count(text) })
        }else{
            tags.addAll(originTags)
        }
        view?.setVisibleClearText(isVisible = text.isNotEmpty())
        view?.setVisiblePlaceholder(isVisible = tags.isEmpty())
        view?.updateAllList()
    }
}
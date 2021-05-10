package ua.com.compose.other_tags.main

import android.content.Context
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.extension.count
import ua.com.compose.other_tags.R


class PresenterTags(val context: Context) : BaseMvpPresenterImpl<ViewTags>() {

    val tags = mutableListOf<String>()
    val filterText = StringBuffer()
    private val originTags = context.resources.getStringArray(R.array.module_other_tags_fragment_tags).toMutableList()

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
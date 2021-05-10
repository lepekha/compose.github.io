package ua.com.compose.other_tags.main

import ua.com.compose.mvp.BaseMvpView

interface ViewTags : BaseMvpView {
    fun updateAllList()
    fun setVisiblePlaceholder(isVisible: Boolean)
    fun setVisibleClearText(isVisible: Boolean)
}
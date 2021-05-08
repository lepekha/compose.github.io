package com.inhelp.dialogs.main

import com.inhelp.base.mvp.BaseMvpView

interface ViewTags : BaseMvpView {
    fun updateAllList()
    fun setVisiblePlaceholder(isVisible: Boolean)
    fun setVisibleClearText(isVisible: Boolean)
}
package com.inhelp.text_style.main

import com.inhelp.base.mvp.BaseMvpView


interface TextStyleView : BaseMvpView {
    fun setEnterText(value: String)
    fun updateListAll()
    fun setVisiblePlaceholder(isVisible: Boolean)
    fun setVisibleClearText(isVisible: Boolean)
}
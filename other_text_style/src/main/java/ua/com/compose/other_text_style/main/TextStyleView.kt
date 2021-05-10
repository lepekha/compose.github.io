package ua.com.compose.other_text_style.main

import ua.com.compose.mvp.BaseMvpView


interface TextStyleView : BaseMvpView {
    fun setEnterText(value: String)
    fun updateListAll()
    fun setVisiblePlaceholder(isVisible: Boolean)
    fun setVisibleClearText(isVisible: Boolean)
}
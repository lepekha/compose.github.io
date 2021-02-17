package com.inhelp.gallery.main

import com.inhelp.base.mvp.BaseMvpView

interface ViewGallery : BaseMvpView {
    fun updateAllList()
    fun setVisibleTabs(isVisible: Boolean)
    fun passData(value: String)
}
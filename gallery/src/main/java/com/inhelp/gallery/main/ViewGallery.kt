package com.inhelp.gallery.main

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpView
import com.inhelp.base.mvp.bottomSheetFragment.BaseMvpDialogView

interface ViewGallery : BaseMvpDialogView {
    fun updateAllList()
    fun setVisibleTabs(isVisible: Boolean)
    fun setVisibleButtons(isVisible: Boolean)
    fun setCount(value: String)
    fun clearSelect()
    fun backPress()
}
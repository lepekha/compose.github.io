package com.inhelp.base.mvp

import android.content.Context
import androidx.annotation.StringRes
import data.Menu

interface BaseMvpView {

    fun getCurrentContext(): Context

    fun getCurrentActivity(): androidx.fragment.app.FragmentActivity

    fun showAlert(@StringRes srtResId: Int)

    fun setTitle(title: String)

    fun setVisibleBack(isVisible: Boolean)

    fun backPress() : Boolean

    fun createBottomMenu(): MutableList<Menu> = mutableListOf()

    fun updateBottomMenu()

    fun bottomSheetCreate()

    fun bottomSheetDestroy()

    fun backToMain()
}
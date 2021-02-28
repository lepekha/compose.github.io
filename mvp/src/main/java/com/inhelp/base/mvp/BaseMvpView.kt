package com.inhelp.base.mvp

import android.content.Context
import androidx.annotation.StringRes

interface BaseMvpView {

    fun getCurrentContext(): Context

    fun getCurrentActivity(): androidx.fragment.app.FragmentActivity

    fun showError(error: String?)

    fun showError(@StringRes stringResId: Int)

    fun showAlert(@StringRes srtResId: Int)

    fun showAlert(message: String)

    fun setTitle(title: String)

    fun setVisibleBack(isVisible: Boolean)

    fun backPress() : Boolean

    fun backToMain()
}
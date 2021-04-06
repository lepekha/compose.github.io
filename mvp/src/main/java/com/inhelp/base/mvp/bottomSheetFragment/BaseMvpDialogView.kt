package com.inhelp.base.mvp.bottomSheetFragment

import android.content.Context

interface BaseMvpDialogView {
    fun getCurrentContext(): Context
    fun getCurrentActivity(): androidx.fragment.app.FragmentActivity
}
package com.inhelp.dialogs.main

import android.content.Context
import java.lang.ref.WeakReference

interface IDialog {
    fun closeDialog()
    fun showDialog(weakContext: WeakReference<Context?>)
}
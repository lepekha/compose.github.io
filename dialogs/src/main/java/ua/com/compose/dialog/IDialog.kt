package ua.com.compose.dialog

import android.content.Context
import java.lang.ref.WeakReference

interface IDialog {
    fun closeDialog()
    fun showDialog(weakContext: WeakReference<Context?>)
}
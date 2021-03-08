package com.inhelp.dialogs.main

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.inhelp.dialogs.R
import java.lang.ref.WeakReference

class DialogLoad private constructor() : IDialog {
    constructor(init: DialogLoad.() -> Unit) : this() {
        init()
    }

    private var dialog: Dialog? = null

    override fun showDialog(weakContext: WeakReference<Context?>) {
        val context = weakContext.get() ?: return
        dialog = Dialog(context, R.style.DialogStyle).apply {
            this.setCanceledOnTouchOutside(false)
            this.requestWindowFeature(Window.FEATURE_NO_TITLE)
            this.setCancelable(false)
            this.setContentView(R.layout.dialog_load)
        }

        dialog?.show()
    }

    override fun closeDialog() {
        dialog?.dismiss()
    }
}
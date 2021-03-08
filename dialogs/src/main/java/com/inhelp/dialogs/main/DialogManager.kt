package com.inhelp.dialogs.main

import android.content.Context
import java.lang.ref.WeakReference

object DialogManager {

    private var mContext: WeakReference<Context?> = WeakReference(null)

    fun init(context: WeakReference<Context?>){
        this.mContext = context
    }

    fun createLoad(init: DialogLoad.() -> Unit): IDialog {
        return DialogLoad(init).apply {
            this.showDialog(mContext)
        }
    }
}
package ua.com.compose.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.AnimationDrawable
import android.view.Window
import kotlinx.android.synthetic.main.dialog_load.*
import ua.com.compose.dialog.R
import java.lang.ref.WeakReference
import android.graphics.drawable.Drawable

import android.graphics.drawable.Animatable2
import android.os.Build
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat.registerAnimationCallback


class DialogLoad private constructor() : IDialog {
    constructor(init: DialogLoad.() -> Unit) : this() {
        init()
    }

    private var dialog: Dialog? = null
    private lateinit var rocketAnimation: AnimatedVectorDrawable
    override fun showDialog(weakContext: WeakReference<Context?>) {
        val context = weakContext.get() ?: return
        dialog = Dialog(context, R.style.DialogStyle).apply {
            this.setCanceledOnTouchOutside(false)
            this.requestWindowFeature(Window.FEATURE_NO_TITLE)
            this.setCancelable(false)
            this.setContentView(R.layout.dialog_load)
            rocketAnimation = this.imgSync.background as AnimatedVectorDrawable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rocketAnimation.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable) {
                        rocketAnimation.start()
                    }
                })
            }
            rocketAnimation.start()
        }

        dialog?.show()
    }

    override fun closeDialog() {
        dialog?.dismiss()
    }
}
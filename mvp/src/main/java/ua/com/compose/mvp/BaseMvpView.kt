package ua.com.compose.mvp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import ua.com.compose.mvp.data.Menu

interface BaseMvpView {

    fun getCurrentContext(): Context

    fun getCurrentActivity(): androidx.fragment.app.FragmentActivity

    fun showAlert(@StringRes srtResId: Int)

    fun setTitle(title: String, startDrawable: Drawable? = null)

    fun setVisibleBack(isVisible: Boolean)

    fun backPress() : Boolean

    fun createBottomMenu(): MutableList<Menu> = mutableListOf()

    fun setVisibleBottomMenu(isVisible: Boolean)

    fun updateBottomMenu()

    fun backToMain()
}
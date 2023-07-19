package ua.com.compose.mvp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import ua.com.compose.mvp.data.Menu

interface BaseMvpView {

    fun setupBottomMenu(menu: MutableList<Menu>) {

    }

    fun createBottomMenu(): MutableList<Menu> = mutableListOf()
}
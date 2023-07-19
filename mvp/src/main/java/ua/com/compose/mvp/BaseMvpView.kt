package ua.com.compose.mvp

import ua.com.compose.mvp.data.Menu

interface BaseMvpView {

    fun setupBottomMenu(menu: MutableList<Menu>) {

    }

    fun createBottomMenu(): MutableList<Menu> = mutableListOf()
}
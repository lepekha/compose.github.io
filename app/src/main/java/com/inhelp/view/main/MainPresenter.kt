package com.inhelp.view.main

import com.inhelp.base.mvp.BaseMvpPresenterImpl


class MainPresenter constructor() : BaseMvpPresenterImpl<MainView>() {

    private var mCurrentFragment: androidx.fragment.app.Fragment? = null

    override fun attachView(view: MainView) {
        super.attachView(view)
    }

    fun showStartScreen(){
    }

    fun pressWatchlist() {
    }

    override fun detachView() {
        super.detachView()
    }

    fun openFragment(intExtra: Int) {
    }
}
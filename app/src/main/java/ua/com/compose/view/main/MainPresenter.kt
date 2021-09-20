package ua.com.compose.view.main

import ua.com.compose.mvp.BaseMvpPresenterImpl


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
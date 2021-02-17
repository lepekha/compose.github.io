package com.inhelp.base.mvp


interface BaseMvpPresenter <in V : BaseMvpView> {

    fun onBackPress()

    fun attachView(view: V)

    fun detachView()

}
package com.inhelp.base.mvp.bottomSheetFragment

interface BaseMvpDialogPresenter <in V : BaseMvpDialogView> {
    fun attachView(view: V)
    fun detachView()
}
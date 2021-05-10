package ua.com.compose.mvp


interface BaseMvpPresenter <in V : BaseMvpView> {

    fun onBackPress()

    fun attachView(view: V)

    fun detachView()

}
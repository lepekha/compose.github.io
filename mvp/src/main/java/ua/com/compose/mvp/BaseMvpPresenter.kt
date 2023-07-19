package ua.com.compose.mvp


interface BaseMvpPresenter <in V : BaseMvpView> {
    fun attachView(view: V)

    fun detachView()

}
package ua.com.compose.mvp.bottomSheetFragment

open class BaseMvpDialogPresenterImpl<V : BaseMvpDialogView> : BaseMvpDialogPresenter<V> {
    protected var view: V? = null
    override fun attachView(view: V) {
        this.view = view
    }
    override fun detachView() {
        view = null
    }
}
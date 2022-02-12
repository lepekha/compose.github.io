package ua.com.compose.mvp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class BaseMvpPresenterImpl<V : BaseMvpView> : BaseMvpPresenter<V>, CoroutineScope {

    private var job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    override fun onBackPress() {
        this.view?.getCurrentActivity()?.onBackPressed()
    }

    protected var view: V? = null

    /**
     * Binds presenterMain with a view when resumed. The Presenter will perform initialization here.
     *
     * @param view the view associated with this presenterMain
     */
    override fun attachView(view: V) {
        this.view = view
        job = Job()
    }




    /**
     * Drops the reference to the view when destroyed
     */
    override fun detachView() {
        job.cancel()
        view = null
    }
}
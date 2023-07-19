package ua.com.compose.mvp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment

abstract class BaseMvpFragment<in V : BaseMvpView, out T : BaseMvpPresenter<V>> : AppCompatDialogFragment(), BaseMvpView {

    protected abstract val presenter: T
    private var view: BaseMvpActivity<BaseMvpView, BaseMvpPresenterImpl<BaseMvpView>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(view = this as V)
        (activity as BaseMvpView).setupBottomMenu(createBottomMenu())
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }
}
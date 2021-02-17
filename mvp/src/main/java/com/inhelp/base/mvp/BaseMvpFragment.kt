package com.inhelp.base.mvp

import android.content.Context
import android.os.Bundle
import android.view.View

abstract class BaseMvpFragment<in V : BaseMvpView, out T : BaseMvpPresenter<V>> : androidx.fragment.app.DialogFragment(), BaseMvpView {

    protected abstract val presenter: T
    private var view: BaseMvpActivity<BaseMvpView, BaseMvpPresenterImpl<BaseMvpView>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(view = this as V)
    }

    override fun showError(error: String?) {
        (activity as BaseMvpActivity<*, *>).showError(error)
    }

    override fun showError(stringResId: Int) {
        (activity as BaseMvpActivity<*, *>).showError(stringResId)
    }

    override fun showAlert(srtResId: Int) {
        (activity as BaseMvpActivity<*, *>).showAlert(srtResId)
    }

    override fun showAlert(message: String) {
        (activity as BaseMvpActivity<*, *>).showAlert(message)
    }

    override fun getCurrentContext(): Context {
        return (activity as BaseMvpActivity<*, *>).getCurrentContext()
    }


    override fun getCurrentActivity(): androidx.fragment.app.FragmentActivity {
        return (activity as BaseMvpActivity<*, *>).getCurrentActivity()
    }

    override fun setTitle(title: String) {
        (activity as BaseMvpActivity<*, *>).setTitle(title)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun backPress(): Boolean {
        return false
    }

    override fun backToMain() {
        (activity as BaseMvpActivity<*, *>).backToMain()
    }
}
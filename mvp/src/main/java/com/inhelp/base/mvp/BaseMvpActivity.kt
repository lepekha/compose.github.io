package com.inhelp.base.mvp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import data.Menu

abstract class BaseMvpActivity<in V : BaseMvpView, out T : BaseMvpPresenter<V>> : AppCompatActivity(), BaseMvpView {

    protected abstract val presenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(view = this as V)
    }

    override fun getCurrentContext(): Context = this

    override fun showAlert(srtResId: Int) {
        //TODO alert
    }

    override fun getCurrentActivity(): androidx.fragment.app.FragmentActivity {
        return this
    }

    abstract fun setupBottomMenu(menu: MutableList<Menu>)

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
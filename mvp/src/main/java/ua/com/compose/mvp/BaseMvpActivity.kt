package ua.com.compose.mvp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseMvpActivity<in V : BaseMvpView, out T : BaseMvpPresenter<V>> : AppCompatActivity(), BaseMvpView {

    protected abstract val presenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(view = this as V)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
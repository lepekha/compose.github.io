package ua.com.compose.mvp

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ua.com.compose.mvp.data.Menu

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

    abstract fun isVisibleBottomMenu(): Boolean

    abstract fun setupBottomMenu(menu: MutableList<Menu>)

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
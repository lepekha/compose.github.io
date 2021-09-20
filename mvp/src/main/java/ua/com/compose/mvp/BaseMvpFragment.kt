package ua.com.compose.mvp

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment

abstract class BaseMvpFragment<in V : BaseMvpView, out T : BaseMvpPresenter<V>> : AppCompatDialogFragment(), BaseMvpView {

    protected abstract val presenter: T
    private var view: BaseMvpActivity<BaseMvpView, BaseMvpPresenterImpl<BaseMvpView>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(view = this as V)
        (activity as BaseMvpActivity<*, *>).setupBottomMenu(createBottomMenu())
        setVisibleBack(true)
    }

    override fun showAlert(srtResId: Int) {
        (activity as BaseMvpActivity<*, *>).showAlert(srtResId)
    }

    override fun getCurrentContext(): Context {
        return (activity as BaseMvpActivity<*, *>).getCurrentContext()
    }


    override fun getCurrentActivity(): androidx.fragment.app.FragmentActivity {
        return (activity as BaseMvpActivity<*, *>).getCurrentActivity()
    }

    override fun setTitle(title: String, startDrawable: Drawable?) {
        (activity as BaseMvpActivity<*, *>).setTitle(title, startDrawable)
    }

    override fun setVisibleBack(isVisible: Boolean) {
        (activity as BaseMvpActivity<*, *>).setVisibleBack(isVisible)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun backPress(): Boolean {
        return false
    }

    override fun updateBottomMenu() {
        (activity as BaseMvpActivity<*, *>).updateBottomMenu()
    }

    override fun backToMain() {
        (activity as BaseMvpActivity<*, *>).backToMain()
    }
}
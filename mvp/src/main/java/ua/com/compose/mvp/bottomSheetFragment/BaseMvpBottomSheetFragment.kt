package ua.com.compose.mvp.bottomSheetFragment

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ua.com.compose.mvp.BaseMvpActivity
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.mvp.BaseMvpView

abstract class BaseMvpBottomSheetFragment<in V : BaseMvpDialogView, out T : BaseMvpDialogPresenter<V>> : BottomSheetDialogFragment(), BaseMvpDialogView {

    protected abstract val presenter: T
    private var view: BaseMvpActivity<BaseMvpView, BaseMvpPresenterImpl<BaseMvpView>>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(view = this as V)
    }

    override fun getCurrentContext(): Context {
        return (activity as BaseMvpActivity<*, *>).getCurrentContext()
    }

    override fun getCurrentActivity(): androidx.fragment.app.FragmentActivity {
        return (activity as BaseMvpActivity<*, *>).getCurrentActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
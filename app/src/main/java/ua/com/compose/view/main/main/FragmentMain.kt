package ua.com.compose.view.main.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import ua.com.compose.R
import kotlinx.android.synthetic.main.fragment_main.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ua.com.compose.mvp.BaseMvpFragment
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import ua.com.compose.view.main.MainActivity


class FragmentMain : BaseMvpFragment<ViewMain, PresenterMain>(), ViewMain {

    companion object {

        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): FragmentMain {
            return FragmentMain().apply {
                arguments = bundleOf(
                        BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    override val presenter: PresenterMain by lazy {
        val scope = getKoin().getOrCreateScope(
                "app", named("app"))
        scope.get()
    }

    private lateinit var mMenuRvAdapter: MenuRvAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_menu), requireContext().getDrawable(R.drawable.ic_logo_vector))
        setVisibleBack(false)
        (requireActivity() as MainActivity).setVisibleHeader(true)
        initList()

        presenter.onCreate()
        presenter.updateList()
    }

    override fun onDestroyView() {
        (requireActivity() as MainActivity).setVisibleHeader(false)
        super.onDestroyView()
    }

    private fun initList() {
        menuList.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        mMenuRvAdapter = MenuRvAdapter(presenter.getOrCreateMenu(fm = fragmentManager)){
            (getCurrentActivity() as MainActivity).setVisibleHeader(false)
        }
        menuList?.adapter = mMenuRvAdapter
    }

    override fun updatePhotoList(){
    }
}
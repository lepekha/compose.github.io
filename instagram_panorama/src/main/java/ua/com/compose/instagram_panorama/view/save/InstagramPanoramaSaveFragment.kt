package ua.com.compose.instagram_panorama.view.save

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.module_instagram_panorama_fragment_instagram_panorama_save.*
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.extension.dp
import ua.com.compose.instagram_panorama.di.Scope
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.instagram_panorama.R


class InstagramPanoramaSaveFragment : BaseMvpFragment<InstagramPanoramaSaveView, InstagramPanoramaSavePresenter>(), InstagramPanoramaSaveView {

    companion object {
        fun newInstance(): InstagramPanoramaSaveFragment {
            return InstagramPanoramaSaveFragment()
        }
    }

    override val presenter: InstagramPanoramaSavePresenter by lazy {
        Scope.INSTAGRAM.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_instagram_panorama_fragment_instagram_panorama_save, container, false)
    }

    private fun initPanoramaPreview() {
        viewPager.adapter = PanoramaRvAdapter(presenter.panoramaImages)
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3
        viewPager.setPageTransformer { page, position ->
            val offset = position * -(2 * 10.dp + 10.dp)
            if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            } else {
                page.translationY = offset
            }
        }
        TabLayoutMediator(tab_layout, viewPager) { tab, position ->
        }.attach()
    }

    private val btnSave by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_download) {
            presenter.pressSave()
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnSave)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_instagram_panorama_save_images))
        presenter.onCreate()
        initPanoramaPreview()
    }

    override fun backPress(): Boolean {
        backToMain()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }
}
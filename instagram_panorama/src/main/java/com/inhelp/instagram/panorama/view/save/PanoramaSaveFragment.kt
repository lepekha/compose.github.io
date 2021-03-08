package com.inhelp.instagram.panorama.view.save

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.extension.dp
import com.inhelp.gallery.main.FragmentGallery
import com.inhelp.instagram.R
import com.inhelp.instagram.panorama.di.Scope
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_instagram_panorama_save.*


class PanoramaSaveFragment : BaseMvpFragment<PanoramaSaveView, PanoramaSavePresenter>(), PanoramaSaveView {

    companion object {
        fun newInstance(): PanoramaSaveFragment {
            return PanoramaSaveFragment()
        }
    }

    override val presenter: PanoramaSavePresenter by lazy {
        Scope.INSTAGRAM.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instagram_panorama_save, container, false)
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
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_download) {
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
        setTitle(getCurrentContext().getString(R.string.fragment_title_panorama_save))

        initPanoramaPreview()

    }

    override fun backPress(): Boolean {
        backToMain()
        return true
    }

    override fun setDownloadDone() {
        btnSave.isEnabled = false
        btnSave.iconResId = R.drawable.ic_download_done
        updateBottomMenu()
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }
}
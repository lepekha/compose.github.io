package com.inhelp.panorama.view.save

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
import com.inhelp.panorama.R
import kotlinx.android.synthetic.main.fragment_panorama_save.*
import org.koin.android.ext.android.getKoin


class PanoramaSaveFragment : BaseMvpFragment<PanoramaSaveView, PanoramaSavePresenter>(), PanoramaSaveView {

    companion object {
        fun newInstance(): PanoramaSaveFragment {
            return PanoramaSaveFragment()
        }
    }

    override val presenter: PanoramaSavePresenter by lazy {
        val scope = getKoin().getScope("panorama")
        scope.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_panorama_save, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_panorama_save))

        btnSave.setOnClickListener {
            presenter.pressSave()
        }

        initPanoramaPreview()

    }

    override fun backPress(): Boolean {
        backToMain()
        return true
    }

    override fun setImage(uri: Uri) {
//        Glide
//                .with(imgView.context)
//                .asBitmap()
//                .centerInside()
//                .load(Uri.parse(arguments?.getString(FragmentGallery.ARGUMENT_ONE_URI)))
//                .into(object : CustomTarget<Bitmap>() {
//                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                        imgView.setBitmap(resource)
//                        imgView.post {
//                            tab_layout.getTabAt(2)?.select()
//                        }
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) {
//                    }
//                })
    }

    override fun setDownloadProgressVisible(isVisible: Boolean) {
        btnSave.isClickable = false
        if (isVisible) {
            btnSave.setImageResource(R.drawable.animate_progress_bar)
        } else {
            btnSave.setImageResource(R.drawable.ic_download_done)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getKoin().getScopeOrNull("panorama")?.close()
    }
}
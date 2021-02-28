package com.inhelp.instagram.view.grid

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.extension.dp
import com.inhelp.instagram.R
import com.inhelp.instagram.di.Scope
import kotlinx.android.synthetic.main.fragment_grid_save.*
import kotlinx.android.synthetic.main.fragment_panorama_save.*
import kotlinx.android.synthetic.main.fragment_panorama_save.btnSave


class GridSaveFragment : BaseMvpFragment<GridSaveView, GridSavePresenter>(), GridSaveView {

    companion object {
        fun newInstance(): GridSaveFragment {
            return GridSaveFragment()
        }
    }

    override val presenter: GridSavePresenter by lazy {
        Scope.INSTAGRAM.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grid_save, container, false)
    }

    private fun initGridPreview() {
        gridList.layoutManager = GridLayoutManager(getCurrentContext(), 3)
        gridList.adapter = GridRvAdapter(
                images = presenter.gridImages,
                onImagePress = {

                })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_panorama_save))

        btnSave.setOnClickListener {
            presenter.pressSave()
        }

        initGridPreview()

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
        Scope.INSTAGRAM.close()
    }
}
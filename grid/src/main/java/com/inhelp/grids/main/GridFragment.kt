package com.inhelp.grids.main

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.extension.dp
import com.inhelp.gallery.main.FragmentGallery
import com.inhelp.grids.R
import com.inhelp.grids.data.FilterType
import com.inhelp.grids.data.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter
import kotlinx.android.synthetic.main.fragment_grid.*
import org.koin.android.ext.android.inject
import replace


class GridFragment : BaseMvpFragment<GridView, GridPresenter>(), GridView {

    companion object {
        fun newInstance(): GridFragment {
            return GridFragment()
        }
    }

    private val gpuImage by lazy { GPUImage(this.getCurrentContext()).apply {
        this.setGLSurfaceView(imgView)
    } }

    override val presenter: GridPresenter by inject()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_grid))

        if (arguments == null) {
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        } else {
            presenter.onLoad(arguments?.getString(FragmentGallery.ARGUMENT_ONE_URI))
        }

        btnNext.setOnClickListener {
            presenter.pressSave(bitmap = gpuImage.bitmapWithFilterApplied)
        }



        btnGallery.setOnClickListener {
//            ColorFragment.newInstance(color = Color.WHITE, targetFragment = this).show(requireFragmentManager(), "ColorFragment")
        }


    }

    private fun initList(image: Bitmap){
        list.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.VERTICAL, false)
        list.adapter = FilterPreviewRvAdapter(context = getCurrentContext(), image = image, filters = FilterType.values().toMutableList()){
            gpuImage.setFilter(GPUImageFilterTools.createFilterForType(context = getCurrentContext(), it))
            gpuImage.requestRender()
        }.apply {
        }

        list.setHasFixedSize(true)
    }

    override fun setImage(uri: Uri){
        val imageUri = Uri.parse(arguments?.getString(FragmentGallery.ARGUMENT_ONE_URI))
        Glide
                .with(imgView.context)
                .asBitmap()
                .load(imageUri)
                .fitCenter()
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        Glide.with(getCurrentContext())
                                .asBitmap()
                                .load(resource)
                                .override(150.dp.toInt())
                                .centerInside()
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        initList(image = resource)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }
                                })





//                        gpuImage.setImage(resource) // this loads image on the current thread, should be run in a thread
//                        gpuImage.setFilter(GPUImageSepiaToneFilter())
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
    }
}
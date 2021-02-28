package com.inhelp.panorama.view.main

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.crop_image.main.SceneLayout
import com.inhelp.gallery.main.FragmentGallery
import com.inhelp.panorama.R
import com.inhelp.panorama.data.EPanorama
import com.inhelp.panorama.view.save.PanoramaSaveFragment
import kotlinx.android.synthetic.main.fragment_panorama.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import replace


class PanoramaFragment : BaseMvpFragment<PanoramaView, PanoramaPresenter>(), PanoramaView {

    companion object {
        fun newInstance(): PanoramaFragment {
            return PanoramaFragment()
        }
    }

    override val presenter: PanoramaPresenter by lazy {
        val scope = getKoin().getOrCreateScope(
                "panorama", named("panorama"))
        scope.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_panorama, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_panorama))

        if (arguments == null) {
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        } else {
            presenter.onLoad(arguments?.getString(FragmentGallery.ARGUMENT_ONE_URI))
        }

        btnNext.setOnClickListener {
            imgView.makeCrop()
        }

        btnGallery.setOnClickListener {
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        }

        imgView.addListener(object : SceneLayout.CropListener {
            override fun onCrop(bitmaps: List<Bitmap>) {
                presenter.onCrop(bitmaps = bitmaps)
            }
        })

        EPanorama.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.element_panorama_menu, null).apply {
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                }
            })
        }

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                imgView.createCropOverlay(ratio = EPanorama.values()[tab.position].ratio, isShowGrid = true)
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(ContextCompat.getColor(getCurrentContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(ContextCompat.getColor(getCurrentContext(), R.color.panorama_menu_unselect), PorterDuff.Mode.MULTIPLY)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    override fun setImage(uri: Uri){
        Glide
                .with(imgView.context)
                .asBitmap()
                .load(Uri.parse(arguments?.getString(FragmentGallery.ARGUMENT_ONE_URI)))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imgView.setBitmap(resource)
                        imgView.post {
                            tab_layout.getTabAt(2)?.select()
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
    }

    override fun navigateToPanoramaSave(){
        getCurrentActivity().supportFragmentManager.replace(fragment = PanoramaSaveFragment.newInstance(), addToBackStack = true)
    }

    override fun onDestroy() {
        super.onDestroy()
        getKoin().getScopeOrNull("panorama")?.close()
    }
}
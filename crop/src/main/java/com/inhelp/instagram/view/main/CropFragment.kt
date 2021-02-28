package com.inhelp.instagram.view.main

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.instagram.R
import com.inhelp.instagram.data.ECrop
import com.inhelp.instagram.view.edit.CropEditFragment
import com.inhelp.crop_image.main.SceneLayout
import com.inhelp.gallery.main.FragmentGallery
import com.inhelp.navigator.SharedElement
import kotlinx.android.synthetic.main.fragment_crop.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import replace


class CropFragment : BaseMvpFragment<CropView, CropPresenter>(), CropView {

    companion object {

        const val TRANSITION_IMAGE_NAME = "TRANSITION_IMAGE_NAME"

        fun newInstance(): CropFragment {
            return CropFragment()
        }
    }

    override val presenter: CropPresenter by lazy {
        val scope = getKoin().getOrCreateScope(
                "crop", named("crop"))
        scope.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_crop))

        ViewCompat.setTransitionName(imgView, TRANSITION_IMAGE_NAME)

        if (arguments == null) {
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        } else {
            presenter.onLoad(uriString = arguments?.getString(FragmentGallery.ARGUMENT_ONE_URI))
        }

        imgView.addListener(object : SceneLayout.CropListener {
            override fun onCrop(bitmaps: List<Bitmap>) {
                presenter.pressCrop(bitmap = bitmaps.first())
            }
        })

        btnNext.setOnClickListener {
            imgView.makeCrop()
        }

        btnGallery.setOnClickListener {
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        }

        ECrop.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.element_menu, null).apply {
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                    this.findViewById<TextView>(R.id.txtTitle).setText(it.titleResId)
                }
            })
        }

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                imgView.createCropOverlay(ratio = ECrop.values()[tab.position].ratio)
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(ContextCompat.getColor(getCurrentContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
                tab.customView?.findViewById<TextView>(R.id.txtTitle)?.setTextColor(ContextCompat.getColor(getCurrentContext(), R.color.colorAccent))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(ContextCompat.getColor(getCurrentContext(), R.color.crop_menu_unselect), PorterDuff.Mode.MULTIPLY)
                tab.customView?.findViewById<TextView>(R.id.txtTitle)?.setTextColor(ContextCompat.getColor(getCurrentContext(), R.color.crop_menu_unselect))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    override fun setImage(uri: Uri){
        Glide
                .with(imgView.context)
                .asBitmap()
                .format(DecodeFormat.PREFER_RGB_565)
                .load(uri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imgView.setBitmap(resource)
                        imgView.post {
                            tab_layout.getTabAt(1)?.select()
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
    }

    override fun navigateToCropEdit(){
        getCurrentActivity().supportFragmentManager.replace(fragment = CropEditFragment.newInstance(), addToBackStack = true, reordering = true, sharedElements = arrayOf(SharedElement(view = imgView, TRANSITION_IMAGE_NAME)))
    }

    override fun onDestroy() {
        super.onDestroy()
        getKoin().getScopeOrNull("crop")?.close()
    }

}
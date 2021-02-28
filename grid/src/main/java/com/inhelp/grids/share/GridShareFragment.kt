package com.inhelp.grids.share

import android.graphics.Bitmap
import android.graphics.Color
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
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.color.ColorFragment
import com.inhelp.gallery.main.FragmentGallery
import com.inhelp.grids.R
import com.inhelp.grids.data.EGrid
import kotlinx.android.synthetic.main.fragment_grid.*
import org.koin.android.ext.android.inject
import replace


class GridShareFragment : BaseMvpFragment<GridShareView, GridSharePresenter>(), GridShareView {

    companion object {
        fun newInstance(): GridShareFragment {
            return GridShareFragment()
        }
    }

    override val presenter: GridSharePresenter by inject()


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
            imgView.makeCrop()
        }

        ViewCompat.setOnApplyWindowInsetsListener(imgView) { v, insets ->
            insets.inset(0,0,0,0)
        }

        btnGallery.setOnClickListener {
            ColorFragment.newInstance(color = Color.WHITE, targetFragment = this).show(requireFragmentManager(), "ColorFragment")
        }

        EGrid.values().forEach {
            tab_layout.addTab(tab_layout.newTab().apply {
                this.customView = layoutInflater.inflate(R.layout.element_grid_menu, null).apply {
                    this.findViewById<ImageView>(R.id.icon).setImageResource(it.iconResId)
                    this.findViewById<TextView>(R.id.txtTitle).setText(it.title)
                }
            })
        }



        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                imgView.createCropOverlay(ratio = EGrid.values()[tab.position].ratio, isShowGrid = true)
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(ContextCompat.getColor(getCurrentContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
                tab.customView?.findViewById<TextView>(R.id.txtTitle)?.setTextColor(ContextCompat.getColor(getCurrentContext(), R.color.colorAccent))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<ImageView>(R.id.icon)?.setColorFilter(ContextCompat.getColor(getCurrentContext(), R.color.grid_menu_unselect), PorterDuff.Mode.MULTIPLY)
                tab.customView?.findViewById<TextView>(R.id.txtTitle)?.setTextColor(ContextCompat.getColor(getCurrentContext(), R.color.grid_menu_unselect))
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
}
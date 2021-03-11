package com.inhelp.core.models.data

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.inhelp.R
import com.inhelp.crop.view.main.CropFragment
import com.inhelp.instagram.grid.view.main.GridFragment
import com.inhelp.instagram.view.main.MainFragment
import com.inhelp.instagram.panorama.view.main.PanoramaFragment
import com.inhelp.dialogs.main.FragmentTags
import com.inhelp.text_style.main.TextStyleFragment
import replace
import java.lang.ref.WeakReference

class MenuObjects(context: Context) {

    private var fragmentManager: WeakReference<FragmentManager>? = null

    fun getOrCreateMenu(fm: FragmentManager?): List<DynamicMenu> {
        fragmentManager = fragmentManager ?: WeakReference(fm) ?: return listOf()
        return listOf(IMAGE, INSTAGRAM, OTHER)
    }

    private val NO_CROP by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_no_crop,
                titleResId = R.string.menu_no_crop,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = MainFragment.newInstance(), addToBackStack = true)
                },
                backgroundImageId = R.drawable.menu_12
        )
    }

    private val GRID by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_grid,
                titleResId = R.string.menu_grid,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = GridFragment.newInstance(), addToBackStack = true)
                },
                backgroundImageId = R.drawable.menu_5
        )
    }

    private val PANORAMA by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_panorama,
                titleResId = R.string.menu_panorama,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = PanoramaFragment.newInstance(), addToBackStack = true)
                },
                backgroundImageId = R.drawable.menu_22
        )
    }

    private val CROP by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_crop,
                titleResId = R.string.menu_crop,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = CropFragment.newInstance(), addToBackStack = true)
                },
                backgroundImageId = R.drawable.menu_4
        )
    }

    private val IMAGE by lazy {
        DynamicMenu.List(
                id = R.id.id_menu_image,
                titleResId = R.string.menu_image,
                innerMenu = mutableListOf(CROP)
        )
    }

    private val INSTAGRAM by lazy {
        DynamicMenu.List(
                id = R.id.id_menu_instagram,
                titleResId = R.string.menu_instagram,
                innerMenu = mutableListOf(NO_CROP, GRID, PANORAMA)
        )
    }

    private val OTHER by lazy {
        DynamicMenu.List(
                id = R.id.id_menu_other,
                titleResId = R.string.menu_other,
                innerMenu = mutableListOf(TEXT_STYLE, TAGS, TEXT_STYLE)
        )
    }


    private val TEXT_STYLE by lazy {
        DynamicMenu.Text(
                id = R.id.id_menu_text_style,
                titleResId = R.string.menu_text_style,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = TextStyleFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val TAGS by lazy {
        DynamicMenu.Text(
                id = R.id.id_menu_tags,
                titleResId = R.string.menu_tags,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = FragmentTags.newInstance(), addToBackStack = true)
                },
        )
    }
}


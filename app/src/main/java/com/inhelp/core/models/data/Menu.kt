package com.inhelp.core.models.data

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.inhelp.R
import com.inhelp.instagram.view.main.MainFragment
import com.inhelp.grids.main.GridFragment
import com.inhelp.panorama.view.main.PanoramaFragment
import com.inhelp.tags.main.FragmentTags
import com.inhelp.text_style.main.TextStyleFragment
import replace
import java.lang.ref.WeakReference

class MenuObjects(context: Context) {

    private var fragmentManager: WeakReference<FragmentManager>? = null

    fun getOrCreateMenu(fm: FragmentManager?): List<DynamicMenu> {
        fragmentManager = fragmentManager ?: WeakReference(fm) ?: return listOf()
        return listOf(TEXT_STYLE, GRID, NO_CROP, TAGS, PANORAMA)
    }

    private val NO_CROP by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_no_crop,
                titleResId = R.string.menu_crop,
                backgroundColorId = R.color.menu_background,
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
                backgroundColorId = R.color.menu_background,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = GridFragment.newInstance(), addToBackStack = true)
                },
                backgroundImageId = R.drawable.menu_5
        )
    }

    private val PANORAMA by lazy {
        DynamicMenu.Long(
                id = R.id.id_menu_panorama,
                titleResId = R.string.menu_panorama,
                backgroundColorId = R.color.menu_background,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = PanoramaFragment.newInstance(), addToBackStack = true)
                },
                backgroundImageId = R.drawable.menu_22
        )
    }

    private val TEXT_STYLE by lazy {
        DynamicMenu.Text(
                id = R.id.id_menu_text_style,
                titleResId = R.string.menu_text_style,
                backgroundColorId = R.color.menu_background,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = TextStyleFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val TAGS by lazy {
        DynamicMenu.Text(
                id = R.id.id_menu_tags,
                titleResId = R.string.menu_tags,
                backgroundColorId = R.color.menu_background,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = FragmentTags.newInstance(), addToBackStack = true)
                },
        )
    }
}


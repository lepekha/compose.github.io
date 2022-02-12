package ua.com.compose.core.models.data

import android.graphics.Color
import androidx.fragment.app.FragmentManager
import ua.com.compose.instagram_planer.view.main.InstagramPlanerFragment
import ua.com.compose.image_crop.main.ImageCropFragment
import ua.com.compose.instagram_grid.view.main.InstagramGridFragment
import ua.com.compose.instagram_panorama.view.main.InstagramPanoramaFragment
import ua.com.compose.instagram_no_crop.view.main.InstagramCropFragment
import ua.com.compose.other_text_style.main.TextStyleFragment
import ua.com.compose.navigator.replace
import ua.com.compose.R
import ua.com.compose.image_compress.main.ImageCompressFragment
import ua.com.compose.image_filter.main.ImageFilterFragment
import ua.com.compose.image_rotate.main.ImageRotateFragment
import ua.com.compose.view.main.main.ImageHolder
import java.lang.ref.WeakReference

class MenuObjects(private val imageHolder: ImageHolder) {

    private var fragmentManager: WeakReference<FragmentManager>? = null

    fun getOrCreateMenu(fm: FragmentManager?): List<DynamicMenu> {
        fragmentManager = fragmentManager ?: WeakReference(fm) ?: return listOf()
        return listOf(IMAGE, INSTAGRAM, OTHER)
    }

    private val INSTAGRAM_PLANER by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_planer,
            name = "INSTAGRAM_PLANER",
            titleResId = R.string.module_instagram_palaner_title,
            isVisible = { true },
            iconColor = Color.parseColor("#CCFFC400"),
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = InstagramPlanerFragment.newInstance(),
                    addToBackStack = true
                )
            },
            iconResId = R.drawable.ic_instagram_visual
        )
    }

    private val INSTAGRAM_NO_CROP by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_no_crop,
            name = "INSTAGRAM_NO_CROP",
            titleResId = R.string.module_instagram_no_crop_fragment_title_crop,
            isVisible = { true },
            iconColor = Color.parseColor("#FF5252"),
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = InstagramCropFragment.newInstance(uri = imageHolder.image),
                    addToBackStack = true
                )
            },
            iconResId = R.drawable.ic_instagram_square
        )
    }

    private val INSTAGRAM_GRID by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_grid,
            name = "INSTAGRAM_GRID",
            titleResId = R.string.module_instagram_grid_title,
            isVisible = { true },
            iconColor = Color.parseColor("#CC06B0FF"),
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = InstagramGridFragment.newInstance(uri = imageHolder.image),
                    addToBackStack = true
                )
            },
            iconResId = R.drawable.ic_instagram_grid
        )
    }

    private val INSTAGRAM_PANORAMA by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_panorama,
            name = "INSTAGRAM_PANORAMA",
            titleResId = R.string.module_instagram_panorama_title,
            isVisible = { true },
            iconColor = Color.parseColor("#CC06E576"),
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = InstagramPanoramaFragment.newInstance(uri = imageHolder.image),
                    addToBackStack = true
                )
            },
            iconResId = R.drawable.ic_instagram_panorama
        )
    }

    private val IMAGE_CROP by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_crop,
            name = "IMAGE_CROP",
            titleResId = R.string.module_image_crop_fragment_image_crop_title,
            iconResId = R.drawable.ic_crop,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = ImageCropFragment.newInstance(uri = imageHolder.image),
                    addToBackStack = true
                )
            },
        )
    }

    private val IMAGE_ROTATE by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_crop,
            name = "IMAGE_ROTATE",
            titleResId = R.string.module_image_rotate_fragment_image_rotate_title,
            iconResId = R.drawable.ic_rotate,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = ImageRotateFragment.newInstance(uri = imageHolder.image),
                    addToBackStack = true
                )
            },
        )
    }

    private val IMAGE_COMPRESS by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_compress,
            name = "IMAGE_COMPRESS",
            titleResId = R.string.module_image_compress_title,
            iconResId = R.drawable.ic_compress,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = ImageCompressFragment.newInstance(uri = imageHolder.image),
                    addToBackStack = true
                )
            },
        )
    }

    private val IMAGE_FILTER by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_filter,
            name = "IMAGE_FILTER",
            titleResId = R.string.module_image_filter_title,
            iconResId = R.drawable.ic_filter,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = ImageFilterFragment.newInstance(uri = imageHolder.image),
                    addToBackStack = true
                )
            },
        )
    }

    private val IMAGE_STYLE by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_style,
            name = "IMAGE_STYLE",
            titleResId = -1,
            iconResId = R.drawable.ic_style,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = ImageCompressFragment.newInstance(uri = imageHolder.image),
                    addToBackStack = true
                )
            },
        )
    }

    private val IMAGE by lazy {
        DynamicMenu.Grid(
            id = R.id.id_menu_image,
            name = "IMAGE",
            titleResId = R.string.menu_image,
            isVisible = { true },
            spanCount = 5,
            innerMenu = mutableListOf(
                IMAGE_FILTER,
                IMAGE_CROP,
                IMAGE_ROTATE,
                IMAGE_COMPRESS
            )
        )
    }

    private val INSTAGRAM by lazy {
        DynamicMenu.Grid(
            id = R.id.id_menu_instagram,
            name = "INSTAGRAM",
            titleResId = R.string.menu_instagram,
            isVisible = { true },
            spanCount = 4,
            innerMenu = mutableListOf(
                INSTAGRAM_PLANER,
                INSTAGRAM_NO_CROP,
                INSTAGRAM_GRID,
                INSTAGRAM_PANORAMA
            )
        )
    }

    private val OTHER by lazy {
        DynamicMenu.Grid(
            id = R.id.id_menu_other,
            name = "OTHER",
            titleResId = R.string.menu_other,
            isVisible = { true },
            spanCount = 5,
            innerMenu = mutableListOf(TEXT_STYLE)
        )
    }

    private val TEXT_STYLE by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_text_style,
            name = "TEXT_STYLE",
            titleResId = R.string.module_other_text_style_fragment_title_text_style,
            iconResId = R.drawable.module_other_text_style_fragment_text_style_ic_menu_icon,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()
                    ?.replace(fragment = TextStyleFragment.newInstance(), addToBackStack = true)
            },
        )
    }
}


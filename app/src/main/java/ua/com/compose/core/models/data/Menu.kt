package ua.com.compose.core.models.data

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
import ua.com.compose.view.main.history.ImageHistory
import ua.com.compose.image_rotate.main.ImageRotateFragment
import ua.com.compose.view.main.MainPresenter
import java.lang.ref.WeakReference

class MenuObjects(private val presenter: MainPresenter) {

    private var fragmentManager: WeakReference<FragmentManager>? = null

    fun getOrCreateMenu(fm: FragmentManager?): List<DynamicMenu> {
        fragmentManager = fragmentManager ?: WeakReference(fm) ?: return listOf()
        return listOf(IMAGE, INSTAGRAM, OTHER)
    }

    private val INSTAGRAM_PLANER by lazy {
        DynamicMenu.Image(
            id = R.id.id_menu_planer,
            titleResId = R.string.module_instagram_palaner_title,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = InstagramPlanerFragment.newInstance(),
                    addToBackStack = true
                )
            },
            backgroundImageId = R.drawable.ic_menu_planer
        )
    }

    private val INSTAGRAM_NO_CROP by lazy {
        DynamicMenu.Image(
            id = R.id.id_menu_no_crop,
            titleResId = R.string.module_instagram_no_crop_fragment_title_crop,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = InstagramCropFragment.newInstance(uri = ImageHistory.mainImage),
                    addToBackStack = true
                )
            },
            backgroundImageId = R.drawable.ic_menu_no_crop
        )
    }

    private val INSTAGRAM_GRID by lazy {
        DynamicMenu.Image(
            id = R.id.id_menu_grid,
            titleResId = R.string.module_instagram_grid_title,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = InstagramGridFragment.newInstance(uri = ImageHistory.mainImage),
                    addToBackStack = true
                )
            },
            backgroundImageId = R.drawable.ic_menu_grid
        )
    }

    private val INSTAGRAM_PANORAMA by lazy {
        DynamicMenu.Image(
            id = R.id.id_menu_panorama,
            titleResId = R.string.module_instagram_panorama_title,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = InstagramPanoramaFragment.newInstance(uri = ImageHistory.mainImage),
                    addToBackStack = true
                )
            },
            backgroundImageId = R.drawable.ic_menu_panorama
        )
    }

    private val IMAGE_CROP by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_crop,
            titleResId = R.string.module_image_crop_fragment_image_crop_title,
            iconResId = R.drawable.ic_crop,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = ImageCropFragment.newInstance(uri = ImageHistory.mainImage),
                    addToBackStack = true
                )
            },
        )
    }

    private val IMAGE_ROTATE by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_crop,
            titleResId = R.string.module_image_rotate_fragment_image_rotate_title,
            iconResId = R.drawable.ic_rotate,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = ImageRotateFragment.newInstance(uri = ImageHistory.mainImage),
                    addToBackStack = true
                )
            },
        )
    }

    private val IMAGE_COMPRESS by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_compress,
            titleResId = R.string.module_image_compress_title,
            iconResId = R.drawable.ic_compress,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = ImageCompressFragment.newInstance(uri = ImageHistory.mainImage),
                    addToBackStack = true
                )
            },
        )
    }

    private val IMAGE_FILTER by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_filter,
            titleResId = R.string.module_image_filter_title,
            iconResId = R.drawable.ic_filter,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = ImageFilterFragment.newInstance(uri = ImageHistory.mainImage),
                    addToBackStack = true
                )
            },
        )
    }

    private val IMAGE_STYLE by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_style,
            titleResId = -1,
            iconResId = R.drawable.ic_style,
            isVisible = { true },
            onPress = {
                fragmentManager?.get()?.replace(
                    fragment = ImageCompressFragment.newInstance(uri = ImageHistory.mainImage),
                    addToBackStack = true
                )
            },
        )
    }

    private val IMAGE by lazy {
        DynamicMenu.Grid(
            id = R.id.id_menu_image,
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
            titleResId = R.string.menu_other,
            isVisible = { true },
            spanCount = 5,
            innerMenu = mutableListOf(TEXT_STYLE)
        )
    }

    private val TEXT_STYLE by lazy {
        DynamicMenu.Icon(
            id = R.id.id_menu_text_style,
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


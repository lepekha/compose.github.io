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
import ua.com.compose.view.main.history.ImageHistory
import ua.com.compose.image_rotate.main.ImageRotateFragment
import ua.com.compose.view.main.MainPresenter
import java.lang.ref.WeakReference

class MenuObjects(private val presenter: MainPresenter) {

    private var fragmentManager: WeakReference<FragmentManager>? = null

    fun getOrCreateMenu(fm: FragmentManager?): List<DynamicMenu> {
        fragmentManager = fragmentManager ?: WeakReference(fm) ?: return listOf()
        return listOf(IMAGE, INSTAGRAM, OTHER).filter { it.innerMenu.any { _it -> _it.isVisible.invoke() } }
    }

    private val INSTAGRAM_PLANER by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_planer,
                titleResId = R.string.menu_planer,
                isVisible = { true },
                onPress = {
                    fragmentManager?.get()?.replace(fragment = InstagramPlanerFragment.newInstance(), addToBackStack = true)
                },
                backgroundImageId = R.drawable.ic_menu_planer
        )
    }

    private val INSTAGRAM_NO_CROP by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_no_crop,
                titleResId = R.string.menu_no_crop,
                isVisible = { true },
                onPress = {
                    fragmentManager?.get()?.replace(fragment = InstagramCropFragment.newInstance(uri = ImageHistory.mainImage), addToBackStack = true)
                },
                backgroundImageId = R.drawable.ic_menu_no_crop
        )
    }

    private val INSTAGRAM_GRID by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_grid,
                titleResId = R.string.menu_grid,
                isVisible = { true },
                onPress = {
                    fragmentManager?.get()?.replace(fragment = InstagramGridFragment.newInstance(uri = ImageHistory.mainImage), addToBackStack = true)
                },
                backgroundImageId = R.drawable.ic_menu_grid
        )
    }

    private val INSTAGRAM_PANORAMA by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_panorama,
                titleResId = R.string.menu_panorama,
                isVisible = { true },
                onPress = {
                    fragmentManager?.get()?.replace(fragment = InstagramPanoramaFragment.newInstance(uri = ImageHistory.mainImage), addToBackStack = true)
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
                    fragmentManager?.get()?.replace(fragment = ImageCropFragment.newInstance(uri = ImageHistory.mainImage), addToBackStack = true)
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
                    fragmentManager?.get()?.replace(fragment = ImageRotateFragment.newInstance(uri = ImageHistory.mainImage), addToBackStack = true)
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
                    fragmentManager?.get()?.replace(fragment = ImageCompressFragment.newInstance(uri = ImageHistory.mainImage), addToBackStack = true)
                },
        )
    }

    private val IMAGE by lazy {
        DynamicMenu.List(
                id = R.id.id_menu_image,
                titleResId = R.string.menu_image,
                isVisible = { true },
                innerMenu = mutableListOf(
                        IMAGE_CROP,
                        IMAGE_ROTATE,
                        IMAGE_COMPRESS
                )
        )
    }

    private val INSTAGRAM by lazy {
        DynamicMenu.List(
                id = R.id.id_menu_instagram,
                titleResId = R.string.menu_instagram,
                isVisible = { true },
                innerMenu = mutableListOf(INSTAGRAM_PLANER, INSTAGRAM_NO_CROP, INSTAGRAM_GRID, INSTAGRAM_PANORAMA)
        )
    }

    private val OTHER by lazy {
        DynamicMenu.List(
                id = R.id.id_menu_other,
                titleResId = R.string.menu_other,
                isVisible = { true },
                innerMenu = mutableListOf(TEXT_STYLE)
        )
    }


    private val TEXT_STYLE by lazy {
        DynamicMenu.Text(
                id = R.id.id_menu_text_style,
                titleResId = R.string.menu_text_style,
                isVisible = { true },
                onPress = {
                    fragmentManager?.get()?.replace(fragment = TextStyleFragment.newInstance(), addToBackStack = true)
                },
        )
    }
}


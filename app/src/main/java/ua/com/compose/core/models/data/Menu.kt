package ua.com.compose.core.models.data

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.dali.instagram.planer.view.main.InstagramPlanerFragment
import com.dali.rotate.view.main.ImageRotateFragment
import com.inhelp.crop.view.main.ImageCropFragment
import com.inhelp.dialogs.main.FragmentTags
import com.inhelp.instagram.grid.view.main.InstagramGridFragment
import com.inhelp.instagram.panorama.view.main.InstagramPanoramaFragment
import com.inhelp.instagram.view.main.InstagramCropFragment
import com.inhelp.text_style.main.TextStyleFragment
import replace
import ua.com.compose.R
import java.lang.ref.WeakReference

class MenuObjects(context: Context) {

    private var fragmentManager: WeakReference<FragmentManager>? = null

    fun getOrCreateMenu(fm: FragmentManager?): List<DynamicMenu> {
        fragmentManager = fragmentManager ?: WeakReference(fm) ?: return listOf()
        return listOf(IMAGE, INSTAGRAM, OTHER)
    }

    private val INSTAGRAM_PLANER by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_planer,
                titleResId = R.string.menu_planer,
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
                onPress = {
                    fragmentManager?.get()?.replace(fragment = InstagramCropFragment.newInstance(), addToBackStack = true)
                },
                backgroundImageId = R.drawable.ic_menu_no_crop
        )
    }

    private val INSTAGRAM_GRID by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_grid,
                titleResId = R.string.menu_grid,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = InstagramGridFragment.newInstance(), addToBackStack = true)
                },
                backgroundImageId = R.drawable.ic_menu_grid
        )
    }

    private val INSTAGRAM_PANORAMA by lazy {
        DynamicMenu.Medium(
                id = R.id.id_menu_panorama,
                titleResId = R.string.menu_panorama,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = InstagramPanoramaFragment.newInstance(), addToBackStack = true)
                },
                backgroundImageId = R.drawable.ic_menu_panorama
        )
    }

    private val IMAGE_CROP by lazy {
        DynamicMenu.Icon(
                id = R.id.id_menu_crop,
                titleResId = -1,
                iconResId = R.drawable.ic_crop,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = ImageCropFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val IMAGE_ROTATE by lazy {
        DynamicMenu.Icon(
                id = R.id.id_menu_crop,
                titleResId = -1,
                iconResId = R.drawable.ic_rotate,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = ImageRotateFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val IMAGE_COMPRESS by lazy {
        DynamicMenu.Icon(
                id = R.id.id_menu_compress,
                titleResId = -1,
                iconResId = R.drawable.ic_compress,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = ImageRotateFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val IMAGE_EXPOSURE by lazy {
        DynamicMenu.Icon(
                id = R.id.id_menu_crop,
                titleResId = -1,
                iconResId = R.drawable.ic_exposure,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = ImageRotateFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val IMAGE_HDR by lazy {
        DynamicMenu.Icon(
                id = R.id.id_menu_crop,
                titleResId = -1,
                iconResId = R.drawable.ic_filter_hdr,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = ImageRotateFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val IMAGE_TILT_SHIFT by lazy {
        DynamicMenu.Icon(
                id = R.id.id_menu_crop,
                titleResId = -1,
                iconResId = R.drawable.ic_filter_tilt_shift,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = ImageRotateFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val IMAGE_VINTAGE by lazy {
        DynamicMenu.Icon(
                id = R.id.id_menu_crop,
                titleResId = -1,
                iconResId = R.drawable.ic_filter_vintage,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = ImageRotateFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val IMAGE_FLARE by lazy {
        DynamicMenu.Icon(
                id = R.id.id_menu_crop,
                titleResId = -1,
                iconResId = R.drawable.ic_flare,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = ImageRotateFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val IMAGE_GRAIN by lazy {
        DynamicMenu.Icon(
                id = R.id.id_menu_crop,
                titleResId = -1,
                iconResId = R.drawable.ic_grain,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = ImageRotateFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val IMAGE_ISO by lazy {
        DynamicMenu.Icon(
                id = R.id.id_menu_crop,
                titleResId = -1,
                iconResId = R.drawable.ic_iso,
                onPress = {
                    fragmentManager?.get()?.replace(fragment = ImageRotateFragment.newInstance(), addToBackStack = true)
                },
        )
    }

    private val IMAGE by lazy {
        DynamicMenu.List(
                id = R.id.id_menu_image,
                titleResId = R.string.menu_image,
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
                innerMenu = mutableListOf(INSTAGRAM_PLANER, INSTAGRAM_NO_CROP, INSTAGRAM_GRID, INSTAGRAM_PANORAMA)
        )
    }

    private val OTHER by lazy {
        DynamicMenu.List(
                id = R.id.id_menu_other,
                titleResId = R.string.menu_other,
                innerMenu = mutableListOf(TEXT_STYLE)//, TAGS, TEXT_STYLE)
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

